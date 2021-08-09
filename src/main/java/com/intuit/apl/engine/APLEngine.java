package com.intuit.apl.engine;

import com.intuit.apl.AuthZDecision;
import com.intuit.apl.PolicyEngine;
import com.intuit.apl.model.Result;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter of RuleEngine implementation to PolicyEngine. PolicyEngine is used inside ABAC and is the
 * contract with policy engine.
 * 
 * @author bdutt
 */
public class APLEngine <
				Subject extends Map<String, String>, 
				Resource extends Map<String, String>, 
				Action extends Map<String, String>, 
				Environment extends Map<String, String>, 
				Obligation extends Map<String, String>> extends PolicyEngine<Subject, Resource, Action, Environment, Obligation> {

  private static Logger log = LoggerFactory.getLogger(APLEngine.class);

  private APLInterpreter<Subject, Resource, Action, Environment, Obligation> ruleEngine = null;

  public APLEngine(APLInterpreter<Subject, Resource, Action, Environment, Obligation> ruleEngine) {
    this.ruleEngine = ruleEngine;
  }

  /**
   * Execute the policy file consisting of rules.
   *
   * @param subject Subject/Actor
   * @param resource Resource
   * @param action Action being done on Resource
   * @param environment Environment containing additional information in its attributes
   * @param obligationList If the action is executed the perform the required obligations
   * @return AuthZDecision
   */
  private AuthZDecision executeAPLRules(Subject subject, Resource resource, Action action,
      Environment environment,  Map<String, Object> request, List<Obligation> obligationList, List<Result> results)
      throws IOException {
    Execution execution = executeAPLRulesInternal(subject, resource, action, environment, request, obligationList, results);
    return execution.decision;
  }
  
  /**
   * This is exposed for testing purposes
   * 
   * @param subject Subject/Actor
   * @param resource Resource
   * @param action Action being done on Resource
   * @param environment Environment containing additional information in its attributes
   * @param request
   * @param obligationList If the action is executed the perform the required obligations
   * @param results
   * @return Execution object
   * @throws IOException
   */
  Execution executeAPLRulesInternal(Subject subject, Resource resource, Action action,
      Environment environment,  Map<String, Object> request, List<Obligation> obligationList, List<Result> results)
      throws IOException {
      AuthZDecision finaldecision = AuthZDecision.INDETERMINATE;
    Execution execution = ruleEngine.execute(subject, resource, action, environment, request, obligationList, results,
        finaldecision);
    return execution;
  }

  /**
   * Explain the policy execution.
   *
   * @param subject Subject/Actor
   * @param resource Resource
   * @param action Action being done on Resource
   * @param environment Environment containing additional information in its attributes
   * @param obligationList If the action is executed the perform the required obligations
   * @return ExplainOutput String This execute the rule engine with explaining how the rules are
   *         firing too
   */
  private String explainAPLRules(Subject subject, Resource resource, Action action,
      Environment environment,  Map<String, Object> request, List<Obligation> obligationList, List<Result> results) {
    AuthZDecision decision = AuthZDecision.INDETERMINATE;
    return ruleEngine.executeWithExplanation(subject, resource, action, environment, request, obligationList,
        results, decision);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.intuit.authorization.policy.PolicyEngine#decide(com.intuit.authorization.
   * policy.model.Subject, com.intuit.authorization.policy.model.Resource,
   * com.intuit.authorization.policy.model.Action,
   * com.intuit.authorization.policy.model.Environment)
   */

  /**
   * The main decide call to get decision.
   *
   * @param subject Subject/Actor
   * @param resource Resource
   * @param action Action being done on Resource
   * @param environment Environment containing additional information in its attributes
   * @param obligationList If the action is executed the perform the required obligations
   * @return AuthZDecision
   */
  @Override
  public AuthZDecision decide(Subject subject, Resource resource, Action action, Environment environment,
      List<Obligation> obligationList, List<Result> results) {
    try {
      return executeAPLRules(subject, resource, action, environment, null, obligationList, results);
    } catch (IOException e) {
      log.error("Exception running policy", e);
    }
    return AuthZDecision.INDETERMINATE;
  }
  
  /**
   * The main decide call to get decision.
   *
   * @param subject Subject/Actor
   * @param resource Resource
   * @param action Action being done on Resource
   * @param environment Environment containing additional information in its attributes
   * @param request request passed from caller to APL
   * @param obligationList If the action is executed the perform the required obligations
   * @return AuthZDecision
   */
  @Override
  public AuthZDecision decide(Subject subject, Resource resource, Action action, Environment environment,  Map<String, Object> request,
      List<Obligation> obligationList, List<Result> results) {
    try {
      return executeAPLRules(subject, resource, action, environment, request, obligationList, results);
    } catch (IOException e) {
      log.error("Exception running policy", e);
    }
    return AuthZDecision.INDETERMINATE;
  }

  /**
   * Explain the policy execution.
   *
   * @param subject Subject/Actor
   * @param resource Resource
   * @param action Action being done on Resource
   * @param environment Environment containing additional information in its attributes
   * @param obligationList If the action is executed the perform the required obligations
   * @return Explain String
   *
   */
  @Override
  public String explain(Subject subject, Resource resource, Action action, Environment environment,
      List<Obligation> obligationList, List<Result> results) {
    try {
      return explainAPLRules(subject, resource, action, environment, null, obligationList, results);
    } catch (Exception e) {
      log.error("Exception running policy", e);
      throw new IllegalArgumentException("Cant be parsed");
    }
  }
  
 

  /**
   * Explain the policy execution.
   *
   * @param subject Subject/Actor
   * @param resource Resource
   * @param action Action being done on Resource
   * @param environment Environment containing additional information in its attributes
   * @param request request passed from caller to APL
   * @param obligationList If the action is executed the perform the required obligations
   * @return Explain String
   *
   */
  @Override
  public String explain(Subject subject, Resource resource, Action action, Environment environment,  Map<String, Object> request,
      List<Obligation> obligationList, List<Result> results) {
    try {
      return explainAPLRules(subject, resource, action, environment, request, obligationList, results);
    } catch (Exception e) {
      log.error("Exception running policy", e);
      throw new IllegalArgumentException("Cant be parsed");
    }
  }
}
