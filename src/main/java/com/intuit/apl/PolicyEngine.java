package com.intuit.apl;

import com.intuit.apl.model.Result;

import java.util.List;
import java.util.Map;


public abstract class PolicyEngine <
	Subject extends Map<String, String>, 
	Resource extends Map<String, String>, 
	Action extends Map<String, String>, 
	Environment extends Map<String, String>, 
	Obligation extends Map<String, String>>
{

  /**
   * Makes authorization decision based on policies and rules within them. One engine encompasses
   * one set of policy files. Typically one resource will have a mapping to one policy engine. There
   * can be many executions on one engine.
   * 
   * @param subject Subject/Actor
   * @param resource Resource
   * @param action Action being done on Resource
   * @param environment Environment containing additional information in its attributes
   * @param obligationList list of obligations returned by the policy
   * @param results any objects that policy would like to return to the caller
   * @return Decision decision made as per policies
   */
  public abstract AuthZDecision decide(Subject subject, Resource resource, Action action,
      Environment environment,  List<Obligation> obligationList, List<Result> results);

  /**
   * Makes authorization decision based on policies and rules within them. One engine encompasses
   * one set of policy files. Typically one resource will have a mapping to one policy engine. There
   * can be many executions on one engine.
   *
   *  Explain executes the policy and also records the steps to dump it for analysis and troubleshooting.
   *  This is like SQL explain for developers to tune their system.
   *  
   * @param subject Subject/Actor
   * @param resource Resource
   * @param action Action being done on Resource
   * @param environment Environment containing additional information in its attributes
   * @param obligationList list of obligations returned by the policy
   * @param results any objects that policy would like to return to the caller
   * @return Decision decision made as per policies
   */
  public String explain(Subject subject, Resource resource, Action action, Environment environment,
      List<Obligation> obligationList, List<Result> results) {
    return "";
  }

    public abstract AuthZDecision decide(Subject subject, Resource resource, Action action, Environment environment,
                                         Response response, List<Result> results);

    /**
   * Makes authorization decision based on policies and rules within them. One engine encompasses
   * one set of policy files. Typically one resource will have a mapping to one policy engine. There
   * can be many executions on one engine.
   * 
   * @param subject Subject/Actor
   * @param resource Resource
   * @param action Action being done on Resource
   * @param environment Environment containing additional information in its attributes
   * @param request more objects to be passed to the policy for execution
   * @param obligationList list of obligations returned by the policy
   * @param results any objects that policy would like to return to the caller
   * @return Decision decision made as per policies
   */
  public abstract AuthZDecision decide(Subject subject, Resource resource, Action action,
      Environment environment,  Map<String, Object> request, List<Obligation> obligationList, List<Result> results);

  public abstract AuthZDecision decide(Subject subject, Resource resource, Action action,
      Environment environment,  Map<String, Object> request, Response response, List<Result> results);

  /**
   * Makes authorization decision based on policies and rules within them. One engine encompasses
   * one set of policy files. Typically one resource will have a mapping to one policy engine. There
   * can be many executions on one engine.
   * 
   * Explain executes the policy and also records the steps to dump it for analysis and troubleshooting.
   * This is like SQL explain for developers to tune their system.
   * 
   * @param subject Subject/Actor
   * @param resource Resource
   * @param action Action being done on Resource
   * @param environment Environment containing additional information in its attributes
   *  @param request more objects to be passed to the policy for execution
   * @param obligationList list of obligations returned by the policy
   * @param results any objects that policy would like to return to the caller
   * @return Decision decision made as per policies
   */
  public String explain(Subject subject, Resource resource, Action action, Environment environment,  Map<String, Object> request,
      List<Obligation> obligationList, List<Result> results) {
    return "";
  }
}
