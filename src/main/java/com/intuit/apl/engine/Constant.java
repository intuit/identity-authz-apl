package com.intuit.apl.engine;


/**
 * This class contains all the constants defined in the code The vocabulary of APL is also defined
 * by the constants here.
 * 
 */
public class Constant {

  /* PolicyFile Definition */
  public static final String DEFAULT_NAME = "rule-name";
  public static final String DEFAULT_DESCRIPTION = "rule description";
  public static final int DEFAULT_SALIENCE = 0;

  /* Quota Configurations */
  public static final Integer DEFAULT_MAX_RULE_SIZE = 1000;
  public static final Integer DEFAULT_MAX_DESCRIPTION_SIZE = 1000;
  public static final Integer DEFAULT_MAX_CONDITION_SIZE = 1000;
  public static final Integer DEFAULT_MAX_ACTION_SIZE = 1000;
  public static final Integer DEFAULT_TOTAL_RULES_SIZE = 100;
  public static final Long DEFAULT_TOTAL_CONDITIONS_SIZE = 1000L;
  public static final Long DEFAULT_TOTAL_ACTIONS_SIZE = 1000L;
  public static final Integer DEFAULT_CONDITIONS_PER_RULE = 1000;
  public static final Integer DEFAULT_ACTIONS_PER_RULE = 1000;

  /* Rules parsing */
  public static final String PACKAGE = "package";
  public static final String IMPORT = "import";
  public static final String RULE = "rule";
  public static final String DESCRIPTION = "description";
  public static final String SALIENCE = "salience";
  public static final String THEN = "then";
  public static final String WHEN = "when";
  public static final String TEST = "test";


  /* Parsing Checks */
  public static final String DECISION = "decision";
  public static final String ASSIGN = "Assign";
  public static final String PERMIT = "permit";
  public static final String DENY = "deny";
  public static final String BOOLEANLITERAL = "BooleanLiteral";

  /* DECISION CLASS */
  public static final String PERMITDECISION = "PERMIT";
  public static final String DENYDECISION = "DENY";
  public static final String NOTAPPLICABLEDECISION = "NOT_APPLICABLE";
  public static final String INDETERMINATEEDECISION = "INDETERMINATE";

  public static final String VERIFY_PREFIX="verify_";

}
