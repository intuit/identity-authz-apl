package com.intuit.apl.engine;


import com.intuit.apl.AuthZDecision;
import com.intuit.apl.PolicyRepository;
import com.intuit.apl.model.Result;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.Literal;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;
import org.springframework.expression.EvaluationException;

/**
 * ABAC based policy language interpreter. This interprets APL files and executes them when needed.
 * Main APL PolicyFile Engine Class. It does the following: 1. Parsing rule files 2. Checking for
 * variable assignments in a rule file 3. Checking for always false and always true condition rules
 * 4. Checking for inconsistencies in rule files 5. Checking for salience of deny is higher than
 * that of permit 6. Getting Variables used in apl files 7. Creating the alpha nodes and beta nodes
 * 8. Checking for default rules 9. Executing the rule files ( three modes: with explanation,
 * without explanation, without explanation and without any checks)
 * 
 * @author bdutt
 * 
 */

public class APLInterpreter <
				Subject extends Map<String, String>, 
				Resource extends Map<String, String>, 
				Action extends Map<String, String>, 
				Environment extends Map<String, String>, 
				Obligation extends Map<String, String>>
{

  /**
   * debug -> control dumping of Rete tree ruleDefinitions -> contain all the rules in the apl files
   * supplied expressionToAlphaNodesMap -> contain all the conditions except variable assignment
   * variableAssignmentExpressions -> Contains all the variable which we defined in apl files
   * example, a = 3 , a is a variable assignment
   * 
   * betaNodesToRuleDefinitionMap -> beta node mapping for the alpha node
   * rulesHavingAlwaysTrueConditions -> all the rules which have always true conditions
   * rulesHavingAlwaysFalseConditions -> all the rules which have always false conditions dumpString
   * -> Rete tree dump output noOfActions -> total number of action expression in a rule file
   * totalNoOfConditions -> total number of conditions apart from assignment operations in a rule
   * file salienceOfRules -> contains the salience of all the rules in a policy file
   * startTimeForRuleExecution -> start time for rule execution endTimeForRuleExecution -> end time
   * for rule execution
   * 
   */

  private static Logger logger = LoggerFactory.getLogger(APLInterpreter.class);
  
  static class AlphaNode implements Comparable{
	  private final Expression expression;
	  int trueInvocations = 0;
	  int order = 0;
	  boolean resetInvocations = false;
	  
	  public AlphaNode(Expression expression, int order) {
		  this.expression = expression;
		  this.order = order;
	  }
	  
	/**
	 * @return the expression
	 */
	public Expression getExpression() {
		return expression;
	}
	
	public void evaluatedTrue() {
	  if(resetInvocations) {
	    this.trueInvocations = 0;
	    this.resetInvocations = false;
	  }else {
		this.trueInvocations ++;
	  }
	}
	
	public void resetInvocations() {
      this.resetInvocations = true;;
  }

	@Override
	public int compareTo(Object o) {
		if(this.trueInvocations == ((AlphaNode)o).trueInvocations) {
			return Integer.compare(this.order, ((AlphaNode)o).order);
		}
		
		return -Integer.compare(this.trueInvocations, ((AlphaNode)o).trueInvocations);
	} 
  }


  private static final boolean useRete = true;
  private boolean debug = true;
  public static final String DEBUG_SYSTEM_PROPERTY = "APLEngine.debug";
  private PriorityQueue<RuleDefinition> ruleDefinitions = null;
  private Map<AlphaNode, List<BetaNode>> alphaToBetaNodesMap =
      new TreeMap<>();
  private ArrayList<Expression> variableAssignmentExpressions = new ArrayList<>();
  private LinkedHashMap<BetaNode, List<RuleDefinition>> betaNodesToRuleDefinitionMap =
      new LinkedHashMap<>();
  private List<Expression> DefaultActionExpressions = new ArrayList<>();
  private String dumpString;
  private List<String> rulesHavingAlwaysTrueConditions = new ArrayList<>();
  private List<String> rulesHavingAlwaysFalseConditions = new ArrayList<>();
  private PriorityQueue<Integer> salienceOfRules = new PriorityQueue<>(Collections.reverseOrder());
  private Integer noOfActions = 0;
  private Integer totalNoOfConditions = 0;
  private long startTimeForParse;
  private long endTimeForParse;
  
  private int executionsForStatsCollection = 100;
  private AtomicInteger currentExecutionsForStatsCollection = null;

  /**
   *
   * @param fileNames Files which will be parsed
   * @param inputStream inputstream of rule file
   * @param debug control dumping of Rete tree
   * @param justParsingAndExecutionOfEngine without any checks, just parse and execute rules
   * @param policyRepository a repository that can get the file if local filesystem does not contain policy file
   */
  public APLInterpreter(String[] fileNames, InputStream inputStream, boolean debug,
      boolean justParsingAndExecutionOfEngine, PolicyRepository policyRepository) {
    this.debug = debug;

    String debugStr = System.getProperty(DEBUG_SYSTEM_PROPERTY);
    if (!StringUtils.isEmpty(debugStr)) {
      if (Boolean.parseBoolean(debugStr)) {
        this.debug = true;
      }
    }
    startTimeForParse = System.nanoTime();
    QuotaConfigurationProperties quotaConfigurationProperties = new QuotaConfigurationProperties();

    ParsingRules ParseRuleFiles =
        new ParsingRules(quotaConfigurationProperties, this.debug, DefaultActionExpressions);
    ParseRuleFiles.parseRuleFiles(fileNames, inputStream, policyRepository);

    ruleDefinitions = ParseRuleFiles.getRuleDefinitions();
    DefaultActionExpressions = ParseRuleFiles.getDefaultActionExpressions();


    VariablesAssignments varAssignments =
        new VariablesAssignments(ruleDefinitions, variableAssignmentExpressions);
    varAssignments.obtainVariableAssignments();

    variableAssignmentExpressions = varAssignments.getVariableAssignments();


    if (!justParsingAndExecutionOfEngine) {
      ParsingChecks parsingChecks = new ParsingChecks(ruleDefinitions,
          variableAssignmentExpressions, rulesHavingAlwaysTrueConditions,
          rulesHavingAlwaysFalseConditions, DefaultActionExpressions);
      parsingChecks.getAlwaysTrueAndAlwaysFalseCndns();
      parsingChecks.checkInconsistencyInRules();
      parsingChecks.PermitDenySalienceCheck();
      parsingChecks.checkInconsistencyInDefaultRules();
      ruleDefinitions = parsingChecks.getRuleDefinitions();
      rulesHavingAlwaysTrueConditions = parsingChecks.getRulesHavingAlwaysTrueConditions();
      rulesHavingAlwaysFalseConditions = parsingChecks.getRulesHavingAlwaysFalseConditions();
    }
    if (useRete) {
      /* If rete algorithm flag is true make rete tree alpha and beta nodes */
      processForRete();
      if (this.debug) {
        dump(fileNames);
      }
    }
    endTimeForParse = System.nanoTime();
  }

  /**
   *
   * @param subject Subject/Actor
   * @param resource Resource
   * @param action Action being done on Resource
   * @param environment Environment containing additional information in its attributes
   * @param request request passed from caller to APL
   * @param obligationList If the action is executed the perform the required obligations
   * @param decision Deny/Permit decision
   */
  Execution execute(Subject subject, Resource resource, Action action, Environment environment, Map<String, Object> request,
      List<Obligation> obligationList, List<Result> results, final AuthZDecision decision) {
    return executeInternal(subject, resource, action, environment, request, obligationList, results, decision,
        false);
  }

  /**
   *
   * @param subject Subject/Actor
   * @param resource Resource
   * @param action Action being done on Resource
   * @param environment Environment containing additional information in its attributes
   * @param request request passed from caller to APL
   * @param obligationList If the action is executed the perform the required obligations
   * @param decision Deny/ Permit decision
   * @return ExplainOutput String This function prints the explanation of how the rules are fired
   */
  String executeWithExplanation(Subject subject, Resource resource, Action action,
      Environment environment, Map<String, Object> request, List<Obligation> obligationList, List<Result> results,
      AuthZDecision decision) {

    Execution execution = executeInternal(subject, resource, action, environment, request, obligationList,
        results, decision, true);
    return execution.expressionString;
  }

  /**
   *
   * @param AllVariables The list in which the variables will be added
   * @param t The spel expression node, if it is of instance literal then it will be added to
   *        AllVariables This function finds all literal variables in an APL file
   */
  private void getVariables(List<String> AllVariables, SpelNode t) {
    if (t != null) {

      /* If only literals are present then add to list of variables */
      if (t.getChildCount() == 0 && (t instanceof Literal)) {
        AllVariables.add(t.toStringAST());
      }
      for (int i = 0; i < t.getChildCount(); i++) {
        getVariables(AllVariables, t.getChild(i));
      }
    }
  }

  /**
   * This function creates alpha and beta nodes
   */
  private void processForRete() {

    /* noOfAssignments -> finding number of variable assignments in a rule file */
    /* Calculation of number of variable assignments */

    HashMap<String, AlphaNode> expressionStringToAlphaNodesMap = new HashMap<>();
    HashMap<Expression, Integer> variableAssignmentsHash = new HashMap<>();
    int alphaNodeOrder = 0;
    for (Expression expr : variableAssignmentExpressions)
      variableAssignmentsHash.put(expr, 1);
    for (RuleDefinition ruleDefinition : ruleDefinitions) {


      int noOfAssignments = 0;
      for (Expression conditionExpr : ruleDefinition.getConditionExpressions()) {

        /* Finding the number of variable assignments */
        if (variableAssignmentsHash.containsKey(conditionExpr))
          noOfAssignments++;
      }

      noOfActions = noOfActions + ruleDefinition.getActionExpressions().size();

      /* nConditions -> all the condition Expressions - number of variable assignment operations */
      int nConditions = ruleDefinition.getConditionExpressions().size() - noOfAssignments;
      totalNoOfConditions = totalNoOfConditions + nConditions;
      BetaNode newBetaNodeForThisRule = null;

      salienceOfRules.add(ruleDefinition.getSalience());
      for (Expression conditionExpr : ruleDefinition.getConditionExpressions()) {

        /* If the condition Expression is a variable assignment operation then continue */
        if (variableAssignmentsHash.containsKey(conditionExpr))
          continue;

        /* if we have not seen the expression create an alpha node */
        /* If we have not seen a beta node for the alpha node, create it */

        AlphaNode alphaNode = expressionStringToAlphaNodesMap.get(conditionExpr.getExpressionString());
        List<BetaNode> betaNodeList = null;

        if (alphaNode != null) {
          betaNodeList = alphaToBetaNodesMap.get(alphaNode);
        } else {
        	 alphaNode = new AlphaNode(conditionExpr, alphaNodeOrder++);
          expressionStringToAlphaNodesMap.put(conditionExpr.getExpressionString(), alphaNode);
         
        }
        if (betaNodeList == null || betaNodeList.size() == 0) {
          if (newBetaNodeForThisRule == null) {
            newBetaNodeForThisRule = new BetaNode();
          }
          if (betaNodeList == null) {
            betaNodeList = new ArrayList<>();
            alphaToBetaNodesMap.put(alphaNode, betaNodeList);
          }
        }
      }

      if (newBetaNodeForThisRule == null) {
        newBetaNodeForThisRule = new BetaNode();
      }


      newBetaNodeForThisRule.setSalience(ruleDefinition.getSalience());
      for (Expression conditionExpr : ruleDefinition.getConditionExpressions()) {

        /* If the condition expression is variable assignment then continue */
        if (variableAssignmentsHash.containsKey(conditionExpr))
          continue;

        AlphaNode alphaNode = expressionStringToAlphaNodesMap.get(conditionExpr.getExpressionString());
        List<BetaNode> betaNodes = alphaToBetaNodesMap.get(alphaNode);
        betaNodes.add(newBetaNodeForThisRule);
        newBetaNodeForThisRule.addCondition();
      }
      List<RuleDefinition> ruleDefinitionList =
          betaNodesToRuleDefinitionMap.get(newBetaNodeForThisRule);
      if (null == ruleDefinitionList) {
        ruleDefinitionList = new ArrayList<>();
      }
      /* Or else create a betaNode and add to all alphaNodes */
      ruleDefinitionList.add(ruleDefinition);
      betaNodesToRuleDefinitionMap.put(newBetaNodeForThisRule, ruleDefinitionList);
    }
  }

  /**
   * @param fileNames The apl file on which rete execute process needs to be shown This function
   *        shows the Rete Execute output, showing the process of creation of alpha and beta nodes
   */
  private void dump(String[] fileNames) {

    StringBuffer dumpStringBuffer = new StringBuffer();
    dumpStringBuffer.append("APL Dump: ").append(this);
    dumpStringBuffer.append("\n");
    for (String fileName : fileNames) {
      dumpStringBuffer.append("                                     FileName: ").append(fileName)
          .append("\n");
    }
    for (AlphaNode alphaNode : alphaToBetaNodesMap.keySet()) {
      List<BetaNode> betaNodes = alphaToBetaNodesMap.get(alphaNode);
      dumpStringBuffer.append("\n                                    AlphaNode: ")
          .append(alphaNode.getExpression()).append("\n");
      dumpStringBuffer.append("                                             : ")
          .append(alphaNode.getExpression().getExpressionString()).append("\n");
      for (BetaNode betaNode : betaNodes) {
        dumpStringBuffer.append("                                     BetaNode: ").append(betaNode)
            .append("\n");
        List<RuleDefinition> ruleDefinitions = betaNodesToRuleDefinitionMap.get(betaNode);
        for (RuleDefinition ruleDefinition : ruleDefinitions) {
          dumpStringBuffer.append("                                         Rule: ")
              .append(ruleDefinition).append("\n");
          dumpStringBuffer.append("                                             : ")
              .append(ruleDefinition.getName()).append("\n");
          for(String ruleActions: ruleDefinition.getActions()) {
            dumpStringBuffer.append("                                             : ")
            .append(ruleActions).append("\n");
          }
        }
      }
    }
    dumpString = dumpStringBuffer.toString();
    if (debug) {
      logger.info(dumpString);
    }
  }

  /**
   *
   * @param subject Subject/Actor
   * @param resource Resource
   * @param action Action being done on Resource
   * @param environment Environment containing additional information in its attributes
   * @param obligationList If the action is executed the perform the required obligations
   * @param decision Deny/Permit decision
   * @param explanationRequired A boolean telling if the explain output to be shown or not
   * @return Execution object
   */
  private Execution executeInternal(Subject subject, Resource resource, Action action,
      Environment environment,  Map<String, Object> request, List<Obligation> obligationList, List<Result> results,
      final AuthZDecision decision, boolean explanationRequired) {
	  resource = resource == null? (Resource) new HashMap<String, String>() : resource;
	  subject = subject == null? (Subject) new HashMap<String, String>() : subject;
	  final Action actionLocalFinalReference = action = action == null? (Action) new HashMap<String, String>() : action;
	  environment = environment == null? (Environment) new HashMap<String, String>() : environment;
	  request = request == null? new HashMap<String, Object>() : request;
	  obligationList = obligationList == null? new ArrayList<Obligation>() : obligationList;
	  results = results == null? new ArrayList<Result>() : results;
	  
    Context context =
        new Context(subject, resource, action, environment, request, obligationList, results, decision);
    final Execution execution = new Execution();

    if (useRete) {
      execution.startTimeForExecution = System.nanoTime();

      String[] actionIds = new String[0];
      if (action.get("id") != null) {
        actionIds = action.get("id").split(",");
      }
      if (isNotEmpty(actionIds)) {
        Arrays.stream(actionIds)
            .anyMatch(actionId -> checkIfNotAllow(actionLocalFinalReference, decision, context, execution, actionId));
      } else {
        reteExecute(execution, context, decision);
      }
      action.put("id", String.join(",", actionIds));
    } else {
      sequentialExecute(context, decision);
    }
    List<String> AllVariables = new ArrayList<>();

    /* If explanation is required then the variables are added to the list */
    if (explanationRequired
        || "true".equalsIgnoreCase(environment.get("explain_apl"))) {

      execution.endTimeForExecution = System.nanoTime();

      for (RuleDefinition ruleDefinition : ruleDefinitions) {
        for (Expression conditionExpr : ruleDefinition.getConditionExpressions()) {

          SpelExpression exprV = (SpelExpression) conditionExpr;
          SpelNode node = exprV.getAST();

          /* Adding variables in a rule file to the Explain output */
          getVariables(AllVariables, node);
        }
      }
      execution.expressionString = generateExplanation(context, decision, execution, AllVariables);
      logger.info(execution.expressionString);
    }
    return execution;
  }

  private boolean isNotEmpty(String[] actionIds) {
    if (actionIds == null) {
      return false;
    }
    return Array.getLength(actionIds) != 0;
  }

  private boolean checkIfNotAllow(Action action, AuthZDecision decision, Context context,
      Execution execution, String actionId) {
    action.put("id", actionId);
    context.setAction(action);
    reteExecute(execution, context, decision);
    if (decision == AuthZDecision.DENY
        || decision == AuthZDecision.INDETERMINATE) {
      return true;
    }
    return false;
  }

  /**
   *
   * @param context Context which we have set, on the basis of which conditions in a rule are
   *        evaluated
   * @param decision Deny/Permit Sequential Execution of all the rules, not using Rete Algorithm
   */
  private void sequentialExecute(Context context, AuthZDecision decision) {

    for (RuleDefinition ruleDefinition : ruleDefinitions) {
      boolean conditionMatched = true;
      for (Expression conditionExpr : ruleDefinition.getConditionExpressions()) {
        Object truthValue = conditionExpr.getValue(context);
        if (truthValue instanceof Boolean) {
          conditionMatched &= (Boolean) truthValue;
        }
      }
      if (conditionMatched) {
        /* PolicyFile fired */
        ruleDefinition.getActionExpressions().forEach(expr -> expr.getValue());

        /* As soon as we have decision we are done */
        /* Allow non-decision rules */
        if (!context.decision.equals(AuthZDecision.INDETERMINATE)) {
          decision = context.decision;
          //TODO: this will not work - BUG BUG 
          /* We have some decision we return */
          return;
        }
      }
    }
  }

  /**
   *
   * @param execution Execution object
   * @param context Context which we have set, on the basis of which conditions in a rule are
   *        evaluated
   * @param decision Deny/Permit
   */
  private void executeDefaultRules(Execution execution, Context context) {
    for (Expression expr : DefaultActionExpressions) {
      expr.getValue(context);
      if (!context.decision.equals(AuthZDecision.INDETERMINATE)) {
        execution.decision = context.decision;
        /* We have some decision we return */
        return;
      }
    }
  }

  /**
   *
   * @param context Context which we have set, on the basis of which conditions in a rule are
   *        evaluated
   * @param expr Expression to be validated
   */
  private void isValidExpression(StandardEvaluationContext context, Expression expr) {
    try {
      expr.getValue(context);
    } catch (EvaluationException evalEx) {
      throw new RuntimeException("Exception = " + evalEx.getMessage()
          + " occured while evaluation of the expression : " + expr.getExpressionString());
    } catch (Exception ex) {
      throw new RuntimeException("Invalid function name or incorrect number of arguments passed: "
          + expr.getExpressionString());
    }
  }

  /**
   *
   * @param context Context which we have set, on the basis of which conditions in a rule are
   *        evaluated
   */
  private void prepareVariableAssignmentMap(StandardEvaluationContext context) {
    for (Expression assignExp : variableAssignmentExpressions) {

      /* str -> RHS of the variable expression (variable value) */
      SpelExpression exprV = (SpelExpression) assignExp;
      String str = exprV.getAST().getChild(1).toStringAST();
      String variableName = exprV.getAST().getChild(0).toStringAST();

      /* Removing '.' which is sometimes created in str */
      str = str.replace(".", "");
      exprV = new SpelExpressionParser().parseRaw(str);

      /* check for invalid usage of Expression */
      isValidExpression(context, exprV);
      context.setVariable(variableName, exprV.getValue(context));
    }
  }

  /**
   *
   * @param betaNode BetaNode
   * @param decision Deny/Permit
   * @param context Context which we have set, on the basis of which conditions in a rule are
   *        evaluated
   * @param execution Execution object
   * @param APLContext APLContext
   */
  private Execution fireRule(BetaNode betaNode,
      StandardEvaluationContext context, Execution execution, Context APLContext) {
    /* PolicyFile is fired */

    /* Condition execution time is over at this point */
    execution.endTimeForConditionExecution = System.nanoTime();

    /* PolicyFile Execution time starts */
    execution.startTimeForRuleExecution = System.nanoTime();
    List<RuleDefinition> ruleDefinitions = execution.betaNodesList.get(betaNode);
    execution.noOfRulesFired = ruleDefinitions.size();

    for (RuleDefinition ruleDefinition : ruleDefinitions) {


      /* Putting all the rules fired in a map */
      execution.firedRulesDescription.put(ruleDefinition.getName(),
          ruleDefinition.getDescription());
      execution.noOfActionsFired =
          execution.noOfActionsFired + ruleDefinition.getActionExpressions().size();

      /* check for invalid function usage in action expressions */
      ruleDefinition.getActionExpressions().forEach(expr -> isValidExpression(context, expr));

      /* As soon as we have decision we are done */
      /* Allow non-decision rules */
      if (!APLContext.decision.equals(AuthZDecision.INDETERMINATE)) {
    	  
        execution.decision = APLContext.decision;

        /* PolicyFile Execution time ends at this point */
        execution.endTimeForRuleExecution = System.nanoTime();
        execution.executionStepNo = 0;

        /* We have some decision we return */
        return execution;
      }
    }
    return execution;
  }

  /**
   * The main Rete based algorithm that evaluates the rule files
   * 
   *
   * @param execution Execution object
   * @param APLContext APLContext
   * @param decision Deny/Permit
   * @return Execution Main algorithm of our rule engine
   */
  private Execution reteExecute(Execution execution, Context APLContext, final AuthZDecision decision) {

    // Should we update the stats based execution
    Map<AlphaNode, List<BetaNode>> newAlphaToBetaNodesMap = null;
    if (this.currentExecutionsForStatsCollection != null &&
          this.currentExecutionsForStatsCollection.incrementAndGet() > this.executionsForStatsCollection) {
      this.currentExecutionsForStatsCollection.set(0);
      newAlphaToBetaNodesMap = new TreeMap<>();
    }

    /* Every time we start rete Execute algorithm a new object execution is created */
    execution.startTimeForConditionExecution = System.nanoTime();
    execution.betaNodesList.clear();
    execution.betaNodesList =
        (LinkedHashMap<BetaNode, List<RuleDefinition>>) betaNodesToRuleDefinitionMap.clone();
    execution.prioritySalienceRules = new PriorityQueue<>(salienceOfRules);
    StandardEvaluationContext ctx = new StandardEvaluationContext(APLContext);
    prepareVariableAssignmentMap(ctx);


    /*
     * For all the variables in Variable Assignments put all the variable and their value in the
     * variable Mapping map
     */
    try {
      //logger.info("---------");
      for (Entry<AlphaNode, List<BetaNode>> entry : alphaToBetaNodesMap.entrySet()) {
        //logger.info(entry.getKey().getExpression().getExpressionString());
        if (newAlphaToBetaNodesMap != null) {
          newAlphaToBetaNodesMap.put(entry.getKey(), entry.getValue());
          entry.getKey().resetInvocations();
        }

        // 1. If no beta node at all, no reason going forward
        if (execution.betaNodesList.isEmpty()) {
          executeDefaultRules(execution, APLContext);
          /* PolicyFile Execution time ends at this point */
          execution.executionStepNo = 0;
          return execution;
        }


        // 2. We do not evaluate alpha nodes with no remaining beta nodes
        // We would move to next alpha node
        boolean hasBetaNode = false;
        for (BetaNode betaNode : entry.getValue()) {
          if (execution.betaNodesList.get(betaNode) != null) {
            hasBetaNode = true;
            break;
          }
        }
        if (!hasBetaNode)
          continue;

        Expression alphaNodeExpression = entry.getKey().getExpression();
        SpelExpression exprV = (SpelExpression) alphaNodeExpression;
        int childCount = exprV.getAST().getChildCount();
        Object truthValue;

        /* If only literals are present then evaluate the conditionExpression */
        if (childCount == 0) {
          truthValue = alphaNodeExpression.getValue();
        } else {
          /* check for invalid function usage */
          isValidExpression(ctx, alphaNodeExpression);
          truthValue = alphaNodeExpression.getValue(ctx);
        }
        execution.noOfConditionsEvaluated++;
        
        /*
         * If the condition expression evaluates to false then put the condition expression in the
         * falseEvaluatedExpressions map
         */
        if ((truthValue instanceof Boolean) && !(Boolean) truthValue) {

          /* Removing all the corresponding beta nodes for the alpha node */
          for (BetaNode betaNode : entry.getValue()) {

            if (null != execution.betaNodesList.get(betaNode)) {
              execution.prioritySalienceRules.remove(betaNode.getSalience());
              execution.betaNodesList.remove(betaNode);
            }
          }

          execution.falseEvaluatedExpressions.put(alphaNodeExpression.getExpressionString(),
              execution.executionStepNo++);
        } else if ((truthValue instanceof Boolean) && (Boolean) truthValue) {
          if(this.currentExecutionsForStatsCollection != null) {
            entry.getKey().evaluatedTrue();
          }
          execution.trueEvaluatedExpressions.put(alphaNodeExpression.getExpressionString(),
              execution.executionStepNo++);
          for (BetaNode betaNode : entry.getValue()) {
            betaNode.setTrue(execution);
            if (betaNode.isReady(execution)) {

              if (!execution.prioritySalienceRules.isEmpty()
                  && execution.prioritySalienceRules.peek() > betaNode.getSalience()) {
                execution.readyToFireRules.put(betaNode, 1);
                continue;
              }

              execution = fireRule(betaNode, ctx, execution, APLContext);
              execution.betaNodesList.remove(betaNode); // Remove the rule as this has already been
                                                        // fired
              if (!APLContext.decision.equals(AuthZDecision.INDETERMINATE)) {
                return execution;
              }
            }
          }
        }
      }


      /* Executing readyToFire Rules */
      for (BetaNode betaNode : execution.readyToFireRules.keySet()) {
        execution = fireRule(betaNode, ctx, execution, APLContext);
        execution.readyToFireRules.remove(betaNode);
        if (!APLContext.decision.equals(AuthZDecision.INDETERMINATE)) {
          return execution;
        }
      }

      /* At last executing default rules if there are any */
      executeDefaultRules(execution, APLContext);
      /* PolicyFile Execution time ends at this point */
      execution.executionStepNo = 0;
      return execution;

    } finally {
      if (newAlphaToBetaNodesMap != null) {
        this.alphaToBetaNodesMap = newAlphaToBetaNodesMap;
      }
      execution.endTimeForRuleExecution = System.nanoTime();
    }
  }

  /**
   *
   * @param context Context which we have set, on the basis of which conditions in a rule are
   *        evaluated
   * @param decision Deny/permit
   * @param execution Execution object
   * @param AllVariables The list in which the variables used in APL files are added
   * @return Explain Output String Generates Explanation object It contains the following decision
   *         explanations: 1. timeElapsedForParsing 2. timeElapsedForExecution 3.
   *         timeElapsedForConditionExecution 4. timeElapsedForRuleExecution 5. AuthZDecision value 6.
   *         True Evaluated Expressions 7. False Evaluated Expressions 8. Fired PolicyFile
   *         Description ( Name and description of a rule) 9. Total Conditions in a rule file 10.
   *         Unique number of conditions 11. Number of actions 12. Number of actions which are fired
   *         13. Number of rules 14. Number of rules which are fired 15. Variables list in a rule
   *         file 16. Rules having always False Conditions 17. Rules having always True Conditions
   *         18. RETE Execute process output
   */
  private String generateExplanation(Context context, AuthZDecision decision, Execution execution,
      List<String> AllVariables) {
    long timeElapsedForParsing = (endTimeForParse - startTimeForParse) / 1000;
    long timeElapsedForExecution =
        (execution.endTimeForExecution - execution.startTimeForExecution) / 1000;
    long timeElapsedForConditionExecution =
        (execution.endTimeForConditionExecution - execution.startTimeForConditionExecution) / 1000;
    long timeElapsedForRuleExecution =
        (execution.endTimeForRuleExecution - execution.startTimeForRuleExecution) / 1000;
    DecisionTrace decisionExplanationObject = new DecisionTrace();
    decisionExplanationObject.setContext(context);
    decisionExplanationObject.setDecision(execution.decision.name());
    decisionExplanationObject.setFalseEvaluatedExpressions(execution.falseEvaluatedExpressions);
    decisionExplanationObject.setTrueEvaluatedExpressions(execution.trueEvaluatedExpressions);
    decisionExplanationObject.setFiredRulesDescription(execution.firedRulesDescription);
    decisionExplanationObject.setNoOfConditions(totalNoOfConditions);
    decisionExplanationObject.setNoOfUniqueConditions(alphaToBetaNodesMap.size());
    decisionExplanationObject.setNoOfActions(noOfActions);
    decisionExplanationObject.setNoOfExecutedActions(execution.noOfActionsFired);
    decisionExplanationObject.setNoOfRules(ruleDefinitions.size());
    decisionExplanationObject.setNoOfRulesFired(execution.noOfRulesFired);
    decisionExplanationObject.setVariableList(AllVariables);
    decisionExplanationObject.setHalfBakedRulesList(rulesHavingAlwaysFalseConditions);
    decisionExplanationObject.setRulesHavingTrueConditions(rulesHavingAlwaysTrueConditions);
    decisionExplanationObject.setDumpString(dumpString);
    return decisionExplanationObject.toString(timeElapsedForParsing, timeElapsedForExecution,
        timeElapsedForConditionExecution, timeElapsedForRuleExecution);
  }

/**
 * @return the executionsForStatsCollection
 */
public int getExecutionsForStatsCollection() {
	return executionsForStatsCollection;
}

/**
 * @param executionsForStatsCollection the executionsForStatsCollection to set
 */
public void setExecutionsForStatsCollection(int executionsForStatsCollection) {
	this.executionsForStatsCollection = executionsForStatsCollection;
}

public void enableAI() {
  currentExecutionsForStatsCollection = new AtomicInteger(0);
}

}
