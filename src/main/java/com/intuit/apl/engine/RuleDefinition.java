package com.intuit.apl.engine;


import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.ArrayList;
import java.util.List;


/**
 * This represents one rule in policy file.
 * 
 * @author bdutt
 *
 */
public class RuleDefinition {

  private String name = Constant.DEFAULT_NAME;
  private String description = Constant.DEFAULT_DESCRIPTION;
  private int salience = Constant.DEFAULT_SALIENCE;
  private boolean test = false;
  private List<String> conditions;
  private List<Expression> conditionExpressions;
  private List<String> actions = new ArrayList<>();
  private List<Expression> actionExpressions = new ArrayList<>();

  private boolean debug = true;

  private SpelExpressionParser parser = null;

  protected RuleDefinition(SpelExpressionParser parser, boolean debug) {
    this.parser = parser;
    this.debug = debug;
  }

  protected RuleDefinition() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    if (debug) {
      this.description = description;
    }
  }

  public List<String> getConditions() {
    return conditions;
  }

  void setConditions(List<String> conditions) {
    if (debug) {
      this.conditions = conditions;
    }
    if (null == conditionExpressions) {
      conditionExpressions = new ArrayList<>();
    }
    conditions.forEach(condition -> conditionExpressions.add(parser.parseExpression(condition)));
  }

  public int getSalience() {
    return salience;
  }

  public void setSalience(int salience) {
    this.salience = salience;
  }

  public List<String> getActions() {
    return actions;
  }

  void setActions(List<String> actions) {
    if (debug) {
      this.actions = actions;
    }
    this.actionExpressions = new ArrayList<>();
    for (String action : actions) {
      this.actionExpressions.add(parser.parseExpression(action));
    }
  }

  List<Expression> getConditionExpressions() {
    return this.conditionExpressions;
  }

  /**
   * @return the actionExpressions
   */
  List<Expression> getActionExpressions() {
    return actionExpressions;
  }

  public List<String> validate() {
    ArrayList<String> errors = new ArrayList<String>();

    if ((conditions == null || conditions.isEmpty())) {
      errors.add("The rule condition(s) must be specified");
    }

    if ((actions == null || actions.isEmpty())) {
      errors.add("The rule action(s) must be specified");
    }
    return errors;
  }

/**
 * A test rule is only for testing and not used in actual execution
 * 
 * @return the test
 */
public boolean isTest() {
	return test;
}

/**
 * @param test the test to set
 */
public void setTest(boolean test) {
	this.test = test;
}
}
