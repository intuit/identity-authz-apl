package com.intuit.apl.engine;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.PriorityQueue;

import com.intuit.apl.AuthZDecision;



/**
 * For every execution a new object of this class is created. This let's execution specific data to
 * be stored.
 * 
 */
class Execution {

  long startTimeForExecution;
  long endTimeForExecution;
  long startTimeForConditionExecution;
  long endTimeForConditionExecution;
  long startTimeForRuleExecution;
  long endTimeForRuleExecution;
  Integer noOfRulesFired = 0;
  Integer noOfActionsFired = 0;
  Integer executionStepNo = 0;
  int noOfConditionsEvaluated = 0;
  String expressionString = "";
  HashMap<BetaNode, Integer> conditionsTrue = new HashMap<>();
  LinkedHashMap<BetaNode, List<RuleDefinition>> betaNodesList = new LinkedHashMap<>();
  LinkedHashMap<String, Integer> trueEvaluatedExpressions = new LinkedHashMap<>();
  LinkedHashMap<String, Integer> falseEvaluatedExpressions = new LinkedHashMap<>();
  LinkedHashMap<String, String> firedRulesDescription = new LinkedHashMap<>();
  LinkedHashMap<BetaNode, Integer> readyToFireRules = new LinkedHashMap<>();
  /* Rules ordered by salience */
  PriorityQueue<Integer> prioritySalienceRules = null;
  AuthZDecision decision = AuthZDecision.INDETERMINATE;
}
