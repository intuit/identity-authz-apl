package com.intuit.apl.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.BooleanLiteral;
import org.springframework.expression.spel.ast.FloatLiteral;
import org.springframework.expression.spel.ast.IntLiteral;
import org.springframework.expression.spel.ast.StringLiteral;
import org.springframework.expression.spel.standard.SpelExpression;

import java.util.*;

/**
 * This class checks the following: 1. Checks whether action is deny or permit 2. Check whether the
 * condition is always true or always false 3. Checking for inconsistencies in rules, conflicting
 * decision value in different rule files 4. Checking for conflicting default rules 5. Checking for
 * deny salience should be higher than permit salience
 */
class ParsingChecks {
  private static Logger log = LoggerFactory.getLogger(ParsingChecks.class);

  private PriorityQueue<RuleDefinition> ruleDefinitions;
  private List<String> rulesHavingAlwaysTrueConditions;
  private List<String> rulesHavingAlwaysFalseConditions;
  private List<Expression> defaultActionExpressions;
  private HashMap<Expression, Integer> variableAssignmentsHashMap;

  ParsingChecks(PriorityQueue<RuleDefinition> ruleDefinitions,
      ArrayList<Expression> VariableAssignments, List<String> RulesHavingAlwaysTrueConditions,
      List<String> RulesHavingAlwaysFalseConditions, List<Expression> DefaultActionExpressions) {
    this.ruleDefinitions = ruleDefinitions;
    this.rulesHavingAlwaysTrueConditions = RulesHavingAlwaysTrueConditions;
    this.rulesHavingAlwaysFalseConditions = RulesHavingAlwaysFalseConditions;
    this.defaultActionExpressions = DefaultActionExpressions;
    variableAssignmentsHashMap = new HashMap<>();
    for (Expression expr : VariableAssignments)
      variableAssignmentsHashMap.put(expr, 1);
  }

  PriorityQueue<RuleDefinition> getRuleDefinitions() {
    return ruleDefinitions;
  }

  List<String> getRulesHavingAlwaysTrueConditions() {
    return rulesHavingAlwaysTrueConditions;
  }

  List<String> getRulesHavingAlwaysFalseConditions() {
    return rulesHavingAlwaysFalseConditions;
  }

  /**
   *
   * @param t SpelNode
   * @return 1: If the action involves decision = permit 0: If the action involves decision = deny
   *         -1: if the action doesnt involve decision = deny or decision = permit Checks whether
   *         action is deny or permit
   *
   */
  private int checkDenyOrPermit(SpelNode t) {

    if (t != null) {
      String opeq = t.getClass().getSimpleName();
      if (opeq.equals(Constant.ASSIGN)) {
        if (t.getChild(0).toStringAST().toLowerCase().contains(Constant.DECISION)
            && t.getChild(1).toStringAST().toLowerCase().contains(Constant.PERMIT))
          return 1;

        if (t.getChild(0).toStringAST().toLowerCase().contains(Constant.DECISION)
            && t.getChild(1).toStringAST().toLowerCase().contains(Constant.DENY))
          return 0;
      }
    }
    return -1;

  }

  /**
   *
   * @param t SpelNode
   * @param conditionExpr Condition Expression
   * @return 0: If the condition is always true 1: If the condition is always false else returns -1
   *         Check whether the condition is always true or always false
   */
  private int checkAlwaysTrueAndAlwaysFalseConditions(SpelNode t, Expression conditionExpr) {
    if (t != null) {
      String opeq = t.getClass().getSimpleName();

      /* If the expression is just "true" or "false" return 1 and 0 respectively */
      if (opeq.equals(Constant.BOOLEANLITERAL)) {
        Object tv = conditionExpr.getValue();
        if (tv instanceof Boolean && (Boolean) tv)
          return 0;

        return 1;
      }
      /* All the condition operations (>, <, ==, =!, etc. ) start with 'Op' */
      if (opeq.equals("OpAnd") || opeq.equals("OpEQ") || opeq.equals("OperatorMatches")
          || opeq.equals("OpGE") || opeq.equals("OpGT") || opeq.equals("OpLE")
          || opeq.equals("OpLT") || opeq.equals("OpNE") || opeq.equals("OpOr")) {

        /* LHS and RHS both should have literals only */
        int countLiterals = 0;
        for (int i = 0; i < t.getChildCount(); i++) {
          if (t.getChild(i) instanceof IntLiteral || t.getChild(i) instanceof StringLiteral
              || t.getChild(i) instanceof FloatLiteral || t.getChild(i) instanceof BooleanLiteral) {
            countLiterals++;
          }
        }
        if (countLiterals == t.getChildCount()) {
          Object tv = conditionExpr.getValue();
          if (tv instanceof Boolean && (Boolean) tv)
            return 0;
          return 1;
        }
      }
    }

    return -1;
  }

  /**
   * Getting list of always false and always true condition Rules
   */
  void getAlwaysTrueAndAlwaysFalseCndns() {
    List<RuleDefinition> ruleDefinitionCopy = new ArrayList<RuleDefinition>();

    for (RuleDefinition ruleDefinition : ruleDefinitions) {

      for (Expression conditionExpr : ruleDefinition.getConditionExpressions()) {

        /* If the Expression is variable assignment then continue */
        if (variableAssignmentsHashMap.containsKey(conditionExpr))
          continue;

        SpelExpression exprV = (SpelExpression) conditionExpr;
        SpelNode node = exprV.getAST();

        int isHalfBaked = checkAlwaysTrueAndAlwaysFalseConditions(node, conditionExpr);

        switch (isHalfBaked) {
          case 1:
            ruleDefinitionCopy.add(ruleDefinition);
            rulesHavingAlwaysFalseConditions.add(ruleDefinition.getName());
            break;
          case 0:
            rulesHavingAlwaysTrueConditions.add(ruleDefinition.getName());
            break;
        }
      }
    }
    /* Removing always false condition rules */
    ruleDefinitions.removeIf(ruleDefinitionCopy::contains);
  }

  /**
   * This function checks for inconsistencies in rules, conflicting decision value in different rule
   * files
   */
  void checkInconsistencyInRules() {
    HashMap<String, Integer> checkInconsistency = new HashMap<>();
    for (RuleDefinition ruleDefinition : ruleDefinitions) {

      TreeSet<String> setStr = new TreeSet<String>();
      if (ruleDefinition.getConditionExpressions() == null)
        continue;
      for (Expression conditionExpr : ruleDefinition.getConditionExpressions()) {

        SpelExpression exprV = (SpelExpression) conditionExpr;
        SpelNode node = exprV.getAST();

        /* If the condition is always true then continue */
        int isAlwaysTrueCondition = checkAlwaysTrueAndAlwaysFalseConditions(node, conditionExpr);
        if (isAlwaysTrueCondition == 0) {
          continue;
        }
        /* Remove all the white spaces in the rule file */
        String s1 = conditionExpr.getExpressionString();
        s1 = s1.replaceAll("\\s", "");

        /*
         * Using set to store conditions as there can be multiple same conditions, so avoiding them
         */
        setStr.add(s1);
      }
      StringBuilder concatStr = new StringBuilder();
      for (String str : setStr) {
        concatStr.append(str);
      }
      int decisionAction = -1;
      for (Expression expr : ruleDefinition.getActionExpressions()) {
        SpelExpression exprV = (SpelExpression) expr;
        SpelNode node = exprV.getAST();

        /* check the action for the condition expression */
        decisionAction = checkDenyOrPermit(node);
      }
      /*
       * If the checkInconsistency map already contains a key with same set of conditions but with
       * different action then throw error
       */
      if (checkInconsistency.containsKey(concatStr.toString())
          && checkInconsistency.get(concatStr.toString()) == 1 - decisionAction) {
        throw new java.lang.Error("The rule file has conflicting rules");
      }
      checkInconsistency.put(concatStr.toString(), decisionAction);
    }
  }

  /**
   * This function checks for conflicting default rules
   */
  void checkInconsistencyInDefaultRules() {
    int isDenyDefault = 0;
    int isPermitDefault = 0;
    for (Expression expr : defaultActionExpressions) {
      SpelExpression exprV = (SpelExpression) expr;
      SpelNode node = exprV.getAST();
      int decisionAction = checkDenyOrPermit(node);

      if (decisionAction == 1) {
        isPermitDefault = 1;
      } else if (decisionAction == 0)
        isDenyDefault = 1;
    }
    if (isPermitDefault == 1 && isDenyDefault == 1)
      throw new java.lang.Error("The rule file has conflicting default rules");
  }

  /**
   * Checking for deny salience should be higher than permit salience Variables Used: MaxSalience of
   * permit, MaxSalience of Deny, Is deny action present
   */
  void PermitDenySalienceCheck() {
    int maxSaliencePermit = 0;
    int maxSalienceDeny = 0;
    int isDenyPresent = -1;

    for (RuleDefinition ruleDefinition : ruleDefinitions) {
      int salienceRule = ruleDefinition.getSalience();

      for (Expression expr : ruleDefinition.getActionExpressions()) {
        SpelExpression exprV = (SpelExpression) expr;
        SpelNode node = exprV.getAST();
        int decisionAction = checkDenyOrPermit(node);

        /* Max salience of deny and permit rules in a rule file */
        if (decisionAction == 1)
          maxSaliencePermit = Math.max(salienceRule, maxSaliencePermit);
        else {
          maxSalienceDeny = Math.max(salienceRule, maxSalienceDeny);
          isDenyPresent = 1;
        }
      }
    }
    /*
     * If max salience of permit is greater than that of deny and If Deny action is present then
     * raise error
     */
    if (maxSaliencePermit > maxSalienceDeny && isDenyPresent == 1) {
      log.info("Salience of Deny must be higher then Permit");
    }

  }
}
