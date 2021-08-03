package com.intuit.apl.engine;

/**
 *
 * Beta node class For every execution a new object of this class is created. This let's execution
 * specific data to be stored
 */
public class BetaNode {

  private int numberOfConditions = 0;
  private int salience = 0;

  public int getSalience() {
    return salience;
  }

  public void setSalience(int salience) {
    this.salience = salience;
  }

  void addCondition() {
    numberOfConditions++;
  }

  /**
   *
   * @param execution Execution object If the condition holds true, then the number of conditions
   *        pending to be held true get reduced by 1
   */
  public void setTrue(Execution execution) {
    Integer nConditionsTrue = execution.conditionsTrue.get(this);
    if (nConditionsTrue == null) {
      nConditionsTrue = numberOfConditions;
    }
    execution.conditionsTrue.put(this, --nConditionsTrue);
  }

  /**
   * +
   *
   * @param execution Execution Only when all the conditions are true for a beta node isReady
   *        function fires
   */

  boolean isReady(Execution execution) {
    Integer nConditionsTrue =
        execution.conditionsTrue.computeIfAbsent(this, k -> numberOfConditions);
    return (nConditionsTrue == 0);
  }

}
