package com.intuit.apl.engine;

import com.intuit.apl.PolicyRepository;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.Yaml;

/**
 * This class parses all the rule files
 */
class ParsingRules {

  private static final Logger logger = LoggerFactory.getLogger(ParsingRules.class);
  private PriorityQueue<RuleDefinition> ruleDefinitions;
  private PriorityQueue<RuleDefinition> testDefinitions;
  private QuotaConfigurationProperties quotaConfigurationProperties;
  private SpelExpressionParser parser;
  private boolean debug;
  private Integer actionsCheck;
  private Integer conditionsCheck;
  private Integer numberOfDefaultRules;
  private List<Expression> defaultActionExpressions;

  ParsingRules(QuotaConfigurationProperties quotaConfigurationProperties, boolean debug,
      List<Expression> defaultActionExpressions) {
    this.quotaConfigurationProperties = quotaConfigurationProperties;
    this.debug = debug;
    this.actionsCheck = 0;
    this.conditionsCheck = 0;
    this.numberOfDefaultRules = 0;
    this.defaultActionExpressions = defaultActionExpressions;
  }

  SpelExpressionParser getParser() {
    return parser;
  }

  PriorityQueue<RuleDefinition> getRuleDefinitions() {
    return ruleDefinitions;
  }
  
  PriorityQueue<RuleDefinition> getTestDefinitions() {
	    return testDefinitions;
	  }

  List<Expression> getDefaultActionExpressions() {
    return defaultActionExpressions;
  }

  /**
   *
   * @param map mapping of rules
   * @param rulePackage rulePackage
   * @return RuleDefinition Creating PolicyFile Definitions from the rule files parsed
   */
  private RuleDefinition createRuleDefinitionFrom(Map<String, Object> map,
      RulePackage rulePackage) {
    String packageName = (String) map.get(Constant.PACKAGE);
    if (packageName != null) {
      rulePackage.setPackageName(packageName);
    }
    List<String> imports = (List<String>) map.get(Constant.IMPORT);
    if (imports != null && !imports.isEmpty()) {
      rulePackage.setImports(imports.toArray(new String[0]));
    }
    RuleDefinition ruleDefinition = new RuleDefinition(parser, debug);
    String ruleName = (String) map.get(Constant.RULE);


    if (ruleName != null && ruleName.length() > quotaConfigurationProperties.getRuleSize())
      throw new IllegalArgumentException(
          "The rule name must be less than" + quotaConfigurationProperties.getRuleSize() + "bytes");
    ruleDefinition.setName(ruleName != null ? ruleName : Constant.DEFAULT_NAME);


    String description = (String) map.get(Constant.DESCRIPTION);

    if (description != null
        && description.length() > quotaConfigurationProperties.getDescriptionSize()) {
      throw new IllegalArgumentException("The rule description must be less than "
          + quotaConfigurationProperties.getDescriptionSize() + "bytes");
    }
    ruleDefinition.setDescription(description != null ? description : Constant.DEFAULT_DESCRIPTION);

    Integer salience = (Integer) map.get(Constant.SALIENCE);
    ruleDefinition.setSalience(salience != null ? salience : Constant.DEFAULT_SALIENCE);

    List<String> conditions = null;

    List<String> actions = (List<String>) map.get(Constant.THEN);
    if ((actions == null || actions.isEmpty())) {
      throw new IllegalArgumentException("The rule action(s) must be specified");
    }

    /*
     * If the number of actions in a rule are greater than the quota allowed number of actions per
     * rule then raise error
     */
    if (actions.size() < quotaConfigurationProperties.getActionPerRule()) {
      actionsCheck = actionsCheck + actions.size();
      ruleDefinition.setActions(actions);

      /*
       * If the total number of actions are greater than the total allowed actions then also raise
       * error
       */
      if (actionsCheck > quotaConfigurationProperties.getTotalActions()) {
        throw new IllegalArgumentException("Total number of actions have exceeded the limit of "
            + quotaConfigurationProperties.getTotalActions());
      }
    } else {
      throw new IllegalArgumentException("The rule action(s) must be less than "
          + quotaConfigurationProperties.getActionPerRule());
    }

    if (map.containsKey(Constant.WHEN)) {
      conditions = (List<String>) map.get(Constant.WHEN);
    }

    if (CollectionUtils.isEmpty(conditions)) {
      numberOfDefaultRules++;
      ruleDefinition.setSalience(Constant.DEFAULT_SALIENCE);
    } else {

      /*
       * If the number of conditions in a rule are greater than the quota allowed number of
       * conditions per rule then raise error
       */
      if (conditions.size() <= quotaConfigurationProperties.getConditionsPerRule()) {
        conditionsCheck = conditionsCheck + conditions.size();

        /*
         * If the total number of conditions are greater than the total allowed conditions then also
         * raise error
         */
        if (conditionsCheck > quotaConfigurationProperties.getTotalConditions())
          throw new IllegalArgumentException(
              "Total number of conditions have exceeded the limit of "
                  + quotaConfigurationProperties.getTotalConditions());

        /* Checking if expression is variable expression */
        for (int i = 0; i < conditions.size(); i++) {
          HashMap<Integer, Integer> checkIfOnlyDollar = new HashMap<>();
          String str = conditions.get(i);
          char[] ch = str.toCharArray();
          for (int j = 0; j < str.length(); j++) {
            if (str.charAt(j) == '$') {
              if ((j == 0) || (str.charAt(j - 1) != 92))
                ch[j] = '#';
              if ((j != 0) && (str.charAt(j - 1) == 92))
                checkIfOnlyDollar.put(j - 1, 1);
            }
          }
          StringBuilder sb = new StringBuilder();
          sb.append(ch);

          for (Integer i1 : checkIfOnlyDollar.keySet()) {
            sb.deleteCharAt(i1);
          }
          str = sb.toString();
          conditions.set(i, str);
        }
        ruleDefinition.setConditions(conditions);
      } else {
        throw new IllegalArgumentException("The rule condition(s) must be less than "
            + quotaConfigurationProperties.getConditionsPerRule());
      }
    }
    
    if (map.containsKey(Constant.TEST)) {
        ruleDefinition.setTest((Boolean)map.get(Constant.TEST));
      }

    return ruleDefinition;
  }

  /**
   *
   * @param fileNames FileNames of rule files loading up
   * @param rulePackage RulePackage
   * @param yaml loads all the yaml rule files Create PolicyFile Definitions from the rule files
   */
  private void loadRuleFiles(String[] fileNames, InputStream inputStream, RulePackage rulePackage,
      Yaml yaml, PolicyRepository policyRepository) {
    InputStream inStream = null;

    List<String> evaluatedFiles = new ArrayList<>();



    for (String fileName : fileNames) {
      numberOfDefaultRules = 0;
      try {

        if (inputStream == null) {
          inStream = policyRepository.getPolicyAsInputStream(fileName);
        } else {
          inStream = inputStream;
        }
        Iterable<Object> rules = yaml.loadAll(inStream);
        if (rules != null) {
          for (Object rule : rules) {
            Map<String, Object> map = (Map<String, Object>) rule;

            if (ruleDefinitions.size() + 1 < quotaConfigurationProperties.getTotalRules()) {
              RuleDefinition toAdd = createRuleDefinitionFrom(map, rulePackage);
              if (toAdd.getConditionExpressions() == null) {
                defaultActionExpressions.addAll(toAdd.getActionExpressions());
              } else {
            	  if(toAdd.isTest()) {
            		  testDefinitions.add(toAdd);
            	  }else {
            		  ruleDefinitions.add(toAdd);
            	  }
              }
            } else
              throw new IllegalArgumentException("no of rules are greater than the limit of "
                  + quotaConfigurationProperties.getTotalRules());
          }
        }

        if (numberOfDefaultRules == 0)
          logger.info("There are no default rules");
        if (numberOfDefaultRules > 1)
          logger.info("There has to be only 1 default rule");

        /* Processing imports */
        ArrayList<String> importedFileNames = new ArrayList<>();
        for (String importClassOrPackage : rulePackage.getImports()) {
          if (importClassOrPackage.endsWith(".*")) {
            /* Collect filenames */
            // TODO: how do we do this from classpath, unless we create a complete inventory
          } else {
            String importedFileName = importClassOrPackage.replace(".", File.separator);
            importedFileNames.add(importedFileName + ".apl");
            evaluatedFiles.addAll(importedFileNames);
          }
        }
        if (!importedFileNames.isEmpty()) {
          loadRuleFiles(importedFileNames.toArray(new String[0]), inputStream, new RulePackage(),
              yaml, policyRepository);
        }
      } catch (Exception e) {
        logger.error("Error reading rule file", e);
        try {
          throw e;
        } catch (Exception ex) {
          logger.error("Error reading rule file", e);
        }
      } finally {
        try {
          if (inStream != null) {
            inStream.close();
          }
        } catch (IOException e) {
          logger.error("Error closing file object", e);
        }

      }
    }
  }


  /**
   *
   * @param fileNames File names of the apl files Parsing PolicyFile files
   */
  void parseRuleFiles(String[] fileNames, InputStream inputStream,
      PolicyRepository policyRepository) {
    Yaml yaml = new Yaml();
    RulePackage rulePackage = new RulePackage();

    if (null == ruleDefinitions) {
      Comparator<RuleDefinition> priorityComparator = (rd1, rd2) -> rd2.getSalience() - rd1.getSalience();
      ruleDefinitions = new PriorityQueue<>(priorityComparator);
      SpelParserConfiguration config =
          new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, this.getClass().getClassLoader());
      parser = new SpelExpressionParser(config);
    }
    loadRuleFiles(fileNames, inputStream, rulePackage, yaml, policyRepository);
  }
}
