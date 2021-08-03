package com.intuit.apl.engine;

public class QuotaConfigurationProperties {

  Integer getRuleSize() {
    return Constant.DEFAULT_MAX_RULE_SIZE;
  }

  public Integer getConditionSize() {
    return Constant.DEFAULT_MAX_CONDITION_SIZE;
  }

  public Integer getActionSize() {
    return Constant.DEFAULT_MAX_ACTION_SIZE;
  }

  Integer getDescriptionSize() {
    return Constant.DEFAULT_MAX_DESCRIPTION_SIZE;
  }

  Integer getTotalRules() {
    return Constant.DEFAULT_TOTAL_RULES_SIZE;
  }

  Long getTotalConditions() {
    return Constant.DEFAULT_TOTAL_CONDITIONS_SIZE;
  }

  Long getTotalActions() {
    return Constant.DEFAULT_TOTAL_ACTIONS_SIZE;
  }

  Integer getConditionsPerRule() {
    return Constant.DEFAULT_CONDITIONS_PER_RULE;
  }

  Integer getActionPerRule() {
    return Constant.DEFAULT_ACTIONS_PER_RULE;
  }
}
