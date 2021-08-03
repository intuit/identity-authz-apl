package com.intuit.apl.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DecisionTrace {

  private Integer noOfRules;
  private Integer noOfConditions;
  private Integer noOfUniqueConditions;
  private Integer noOfRulesFired;
  private Integer noOfActions;
  private Integer noOfExecutedActions;
  private String decision;
  private String dumpString;
  private Context context;
  private LinkedHashMap<String, Integer> trueEvaluatedExpressions = new LinkedHashMap<>();
  private LinkedHashMap<String, Integer> falseEvaluatedExpressions = new LinkedHashMap<>();
  private LinkedHashMap<String, String> firedRulesDescription = new LinkedHashMap<>();
  private List<String> variableList = new ArrayList<String>();
  private List<String> halfBakedRulesList = new ArrayList<String>();
  private List<String> rulesHavingTrueConditions = new ArrayList<String>();

  DecisionTrace() {
  }

  private Context getContext() {
    return context;
  }

  void setContext(Context context) {
    this.context = context;
  }

  private Integer getNoOfRules() {
    return noOfRules;
  }

  public void setNoOfRules(Integer noOfRules) {
    this.noOfRules = noOfRules;
  }

  public Integer getNoOfConditions() {
    return noOfConditions;
  }

  public void setNoOfConditions(Integer noOfConditions) {
    this.noOfConditions = noOfConditions;
  }

  public Integer getNoOfUniqueConditions() {
    return noOfUniqueConditions;
  }

  public void setNoOfUniqueConditions(Integer noOfUniqueConditions) {
    this.noOfUniqueConditions = noOfUniqueConditions;
  }

  public Integer getNoOfRulesFired() {
    return noOfRulesFired;
  }

  public void setNoOfRulesFired(Integer noOfRulesFired) {
    this.noOfRulesFired = noOfRulesFired;
  }

  public Integer getNoOfActions() {
    return noOfActions;
  }

  public void setNoOfActions(Integer noOfActions) {
    this.noOfActions = noOfActions;
  }

  public Integer getNoOfExecutedActions() {
    return noOfExecutedActions;
  }

  public void setNoOfExecutedActions(Integer noOfExecutedActions) {
    this.noOfExecutedActions = noOfExecutedActions;
  }

  public LinkedHashMap<String, Integer> getTrueEvaluatedExpressions() {
    return trueEvaluatedExpressions;
  }

  public void setTrueEvaluatedExpressions(LinkedHashMap<String, Integer> trueEvaluatedExpressions) {
    this.trueEvaluatedExpressions = trueEvaluatedExpressions;
  }

  public LinkedHashMap<String, Integer> getFalseEvaluatedExpressions() {
    return falseEvaluatedExpressions;
  }

  public void setFalseEvaluatedExpressions(
      LinkedHashMap<String, Integer> falseEvaluatedExpressions) {
    this.falseEvaluatedExpressions = falseEvaluatedExpressions;
  }

  public LinkedHashMap<String, String> getFiredRulesDescription() {
    return firedRulesDescription;
  }

  public void setFiredRulesDescription(LinkedHashMap<String, String> firedRulesDescription) {
    this.firedRulesDescription = firedRulesDescription;
  }

  public List<String> getVariableList() {
    return variableList;
  }

  public void setVariableList(List<String> variableList) {
    this.variableList = variableList;
  }

  public List<String> getHalfBakedRulesList() {
    return halfBakedRulesList;
  }

  public void setHalfBakedRulesList(List<String> halfBakedRulesList) {
    this.halfBakedRulesList = halfBakedRulesList;
  }

  public List<String> getRulesHavingTrueConditions() {
    return rulesHavingTrueConditions;
  }

  public void setRulesHavingTrueConditions(List<String> rulesHavingTrueConditions) {
    this.rulesHavingTrueConditions = rulesHavingTrueConditions;
  }

  public String getDecision() {
    return decision;
  }

  public void setDecision(String decision) {
    this.decision = decision;
  }

  public String getDumpString() {
    return dumpString;
  }

  public void setDumpString(String dumpString) {
    this.dumpString = dumpString;
  }

  /**
   * Compares two objects.
   * 
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DecisionTrace)) {
      return false;
    }
    DecisionTrace that = (DecisionTrace) o;
    return Objects.equals(getContext(), that.getContext())
        && Objects.equals(getNoOfRules(), that.getNoOfRules())
        && Objects.equals(getNoOfConditions(), that.getNoOfConditions())
        && Objects.equals(getNoOfUniqueConditions(), that.getNoOfUniqueConditions())
        && Objects.equals(getNoOfRulesFired(), that.getNoOfRulesFired())
        && Objects.equals(getNoOfActions(), that.getNoOfActions())
        && Objects.equals(getNoOfExecutedActions(), that.getNoOfExecutedActions())
        && Objects.equals(getTrueEvaluatedExpressions(), that.getTrueEvaluatedExpressions())
        && Objects.equals(getFalseEvaluatedExpressions(), that.getFalseEvaluatedExpressions())
        && Objects.equals(getFiredRulesDescription(), that.getFiredRulesDescription())
        && Objects.equals(getDecision(), that.getDecision());
  }

  /**
   * Return hashcode.
   * 
   */
  @Override
  public int hashCode() {
    return Objects.hash(getContext(), getNoOfRules(), getNoOfConditions(),
        getNoOfUniqueConditions(), getNoOfRulesFired(), getNoOfActions(), getNoOfExecutedActions(),
        getTrueEvaluatedExpressions(), getFalseEvaluatedExpressions(), getFiredRulesDescription(),
        getDecision());
  }

  /**
   * String representation of context, i.e. the request containing subject, resource, action etc.
   *
   * @param context Context on which conditions are evaluated
   * @return String Mapping the context to a string
   */
  private String toStringContext(Context<Map<String, String>, Map<String, String>, Map<String, String>, Map<String, String>, Map<String, String>> context) {
    StringBuffer contextString = new StringBuffer();

    contextString.append(" Subject:" + "                ");

    if (!context.getSubject().isEmpty()) {

      for (Map.Entry<String, String> entry : context.getSubject().entrySet()) {
        contextString.append(entry.getKey()).append(" -> ").append(entry.getValue());
        contextString.append(" ");
        variableList.add(entry.getKey());

      }
      // contextString.delete(contextString.length()-1);
      contextString.append("        ");
    }

    contextString.append("\n        " + " Resource:" + "               ");

    if (!context.getResource().isEmpty()) {
      for (Map.Entry<String, String> entry : context.getResource().entrySet()) {
        contextString.append(entry.getKey()).append(" -> ").append(entry.getValue());
        contextString.append(" ");
        variableList.add(entry.getKey());

      }
      contextString.append("        ");
    }

    contextString.append("\n        " + " Action:" + "                 ");

    if (!context.getAction().isEmpty()) {
      for (Map.Entry<String, String> entry : context.getAction().entrySet()) {
        contextString.append(entry.getKey()).append(" -> ").append(entry.getValue());
        contextString.append(" ");

      }
      contextString.append("       ");
    }

    contextString.append("\n        " + " Environment:" + "            ");

    if (!context.getEnvironment().isEmpty()) {
      for (Map.Entry<String, String> entry : context.getEnvironment().entrySet()) {
        contextString.append(entry.getKey()).append(" -> ").append(entry.getValue());
        contextString.append(" ");
        variableList.add(entry.getKey());

      }
      contextString.append("       ");
    }

    return contextString.toString();
  }

  /**
   * Print variables to string.
   *
   * @return String Printing variables
   */
  private String toStringVariables() {
    StringBuffer allV = new StringBuffer();
    List<String> variables = getVariableList();
    HashSet<String> set = new HashSet();
    for (String var : variables) {
      var = var.replaceAll("'", "");
      set.add(var);
    }

    Iterator<String> itr = set.iterator();
    allV.append("Number of variables:     ").append(set.size()).append("\n");
    while (itr.hasNext()) {
      allV.append("\n");
      allV.append("                                 ").append(itr.next());
    }
    return allV.toString();
  }

  /**
   * Format conditions to string.
   *
   * @return String Tabular format for condition printing
   */
  private String toStringMapForConditions() {
    StringBuffer stringMap = new StringBuffer();
    String format = "%3$-10s %2$-70s %1$-10s";
    stringMap.append("\n");
    stringMap.append(String.format(format, "OUTPUT", "CONDITION", "STEP"));
    stringMap.append(
        "\n-----------------------------------------------------------------------------------\n");
    Set<String> keys = trueEvaluatedExpressions.keySet();
    for (String k : keys) {
      stringMap.append(String.format(format, "T", k, trueEvaluatedExpressions.get(k)));
      stringMap.append("\n");
    }
    keys = falseEvaluatedExpressions.keySet();
    for (String k : keys) {
      stringMap.append(String.format(format, "F", k, falseEvaluatedExpressions.get(k)));
      stringMap.append("\n");
    }
    stringMap.append(
        "-------------------------------------------------------------------------------------\n");

    return stringMap.toString();
  }

  /**
   * String representation of fired rules.
   *
   * @param firedRulesDescription Fired Rules
   * @return String Returns the description of fired rules
   */
  private String toStringMapForRules(LinkedHashMap<String, String> firedRulesDescription) {
    StringBuffer stringMap = new StringBuffer();
    Set<String> keys = firedRulesDescription.keySet();
    for (String k : keys) {
      stringMap.append("\n                                 ");
      stringMap.append("Name : ").append(k)
          .append("\n                                 Description : ")
          .append(firedRulesDescription.get(k));
    }

    return stringMap.toString();
  }

  /**
   * String representation for false conditions.
   *
   * @return String Returns always false conditions
   */
  private String toStringFalseConditionLiterals() {
    StringBuffer allHalfBakedRules = new StringBuffer();
    List<String> hfBakedRls = getHalfBakedRulesList();
    for (String str : hfBakedRls) {
      allHalfBakedRules.append("\n                                 ").append(str);
    }
    return allHalfBakedRules.toString();
  }

  /**
   * String representation of true conditions.
   *
   * @return String Returns always true conditions
   */
  private String toStringtrueConiditonLiterals() {
    StringBuffer trueConditionsLiterals = new StringBuffer();
    List<String> truecndsLiterals = getRulesHavingTrueConditions();
    for (String str : truecndsLiterals) {
      trueConditionsLiterals.append("\n                                 ").append(str);

    }
    return trueConditionsLiterals.toString();
  }

  /**
   * String output for explain feature.
   *
   * @param timeElapsedForParsing timeElapsedForParsing
   * @param timeElapsedForExecution timeElapsedForExecution
   * @param timeElapsedForConditionExecution timeElapsedForConditionExecution
   * @param timeElapsedForRuleExecution timeElapsedForRuleExecution
   * @return String Returns the ExplainOutput string
   */
  String toString(long timeElapsedForParsing, long timeElapsedForExecution,
      long timeElapsedForConditionExecution, long timeElapsedForRuleExecution) {
    StringBuffer explainOutput = new StringBuffer();
    explainOutput.append("\n------------------\n" + "APL Explain :" + "\n------------------"
        + "\n\n    " + "Inputs:" + "\n\n        " + toStringContext(context) + "\n" + "\n\n    "
        + "Variables: " + "\n\n        " + toStringVariables() + "\n" + "\n\n    " + "Stats:"
        + "\n\n        " + " Rules:" + "                  " + noOfRules + "\n        "
        + " Rules Fired:" + "            " + noOfRulesFired + "\n        " + " Conditions:"
        + "             " + noOfConditions + "\n        " + " UniqueConditions:" + "       "
        + noOfUniqueConditions + "\n        " + " Actions:" + "                " + noOfActions
        + "\n        " + " ExecutedActions:" + "        " + noOfExecutedActions + "\n        "
        + " HalfBakedRules:" + "         " + getHalfBakedRulesList().size() + "\n        "
        + " Parsing(µs):" + "            " + timeElapsedForParsing + " \n" + "        "
        + " Execution(µs):" + "          " + timeElapsedForExecution + " \n" + "        "
        + " ConditionExecution(µs): " + timeElapsedForConditionExecution + " \n" + "        "
        + " RuleExecution(µs):    " + "  " + timeElapsedForRuleExecution + " \n" + "\n    "
        + "Execution:" + "\n\n        " + toStringMapForConditions() + "\n\n    " + "Rules fired:\n"
        + toStringMapForRules(firedRulesDescription) + "\n\n    " + "Decision:"
        + "                    " + decision + "\n\n");
    String falseConditions = toStringFalseConditionLiterals();
    if (isNotBlank(falseConditions)) {
      explainOutput
          .append("    Always false conditions:\n" + falseConditions + "\n\n        " + "\n\n");
    }
    String trueConditions = toStringtrueConiditonLiterals();
    if (isNotBlank(trueConditions)) {
      explainOutput
          .append("    Always true conditions:\n" + trueConditions + "\n\n        " + "\n");
    }
    String dumpString = getDumpString();
    if (isNotBlank(dumpString)) {
      explainOutput.append(
          "    Rete Execute Process:\n" + "                                 "
          /* True when debug variable is set to true */
          +  dumpString + "\n\n        " + "\n");
    }
    return explainOutput.toString();
  }

  /**
   * Is not blank.
   * 
   * @param falseConditions false conditions
   * @return true if not blank
   */
  private boolean isNotBlank(String falseConditions) {
    return !isBlank(falseConditions);
  }

  /**
   * Is blank.
   * 
   * @param cs string
   * @return true if blank
   */
  public static boolean isBlank(final CharSequence cs) {
    int strLen;
    if (cs == null || (strLen = cs.length()) == 0) {
      return true;
    }
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(cs.charAt(i))) {
        return false;
      }
    }
    return true;
  }
}
