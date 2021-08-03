package com.intuit.apl;

import com.intuit.apl.model.Result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import com.intuit.apl.engine.APLInterpreter;

/**
 * c These are the tests for the APL
 */
public class IndeterminateTest {
  private static Logger log = LoggerFactory.getLogger(IndeterminateTest.class);

  @Test
  public void simpleIndeterminateWithNullResourceOwnerId() {
    String[] ruleFiles = {"com/intuit/authorization/simple_indeterminate.apl"};

    Map<String, String> environment = new HashMap<String, String>();
	Map<String, String> resource = new HashMap<String, String>();
	Map<String, String> subject = new HashMap<String, String>();
	Map<String, String> action = new HashMap<String, String>();
	List<Map<String, String>> obligationList = new ArrayList<>();
	
    resource.put("resourceOwnerId", null);

    subject.put("id", "temp");
    subject.put("role", "admin");

    action.put("name", "execute");

    try {
      PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
      AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
          obligationList, new ArrayList<Result>());
      assertEquals(AuthZDecision.INDETERMINATE, decision);
    } catch (Exception e) {
      log.error("Error running engine", e);
      assertTrue("error in execution: " + e.getMessage(), false);
    }
  }

  @Test
  public void simpleIndeterminateNoResourceOwnerId() {
    String[] ruleFiles = {"com/intuit/authorization/simple_indeterminate.apl"};

    Map<String, String> environment = new HashMap<String, String>();
	Map<String, String> resource = new HashMap<String, String>();
	Map<String, String> subject = new HashMap<String, String>();
	Map<String, String> action = new HashMap<String, String>();
	List<Map<String, String>> obligationList = new ArrayList<>();
	
    resource.put("id", "something");

    subject.put("id", "temp");
    subject.put("role", "admin");

    action.put("name", "execute");

    try {
      PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
      AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
          obligationList, new ArrayList<Result>());
      assertEquals(AuthZDecision.INDETERMINATE, decision);
    } catch (Exception e) {
      log.error("Error running engine", e);
      assertTrue("error in execution: " + e.getMessage(), false);
    }
  }

  @Test
  public void simpleDenyWithResourceOwnerId() {
    String[] ruleFiles = {"com/intuit/authorization/simple_indeterminate.apl"};

    Map<String, String> environment = new HashMap<String, String>();
	Map<String, String> resource = new HashMap<String, String>();
	Map<String, String> subject = new HashMap<String, String>();
	Map<String, String> action = new HashMap<String, String>();
	List<Map<String, String>> obligationList = new ArrayList<>();
	
    resource.put("resourceOwnerId", "something");

    subject.put("id", "temp");
    subject.put("role", "admin");

    action.put("name", "execute");

    try {
      PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
      AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
          obligationList, new ArrayList<Result>());
      assertEquals(AuthZDecision.DENY, decision);
    } catch (Exception e) {
      log.error("Error running engine", e);
      assertTrue("error in execution: " + e.getMessage(), false);
    }
  }

  @Test
  public void simpleDebuggedDenyWithResourceOwnerId() {
    String[] ruleFiles = {"com/intuit/authorization/simple_indeterminate.apl"};

    Map<String, String> environment = new HashMap<String, String>();
	Map<String, String> resource = new HashMap<String, String>();
	Map<String, String> subject = new HashMap<String, String>();
	Map<String, String> action = new HashMap<String, String>();
	List<Map<String, String>> obligationList = new ArrayList<>();
	
    resource.put("resourceOwnerId", "something");

    subject.put("id", "temp");
    subject.put("role", "admin");

    action.put("name", "execute");

    try {
      System.setProperty(APLInterpreter.DEBUG_SYSTEM_PROPERTY, "true");
      PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
      AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
          obligationList, new ArrayList<Result>());
      assertEquals(AuthZDecision.DENY, decision);
    } catch (Exception e) {
      log.error("Error running engine", e);
      assertTrue("error in execution: " + e.getMessage(), false);
    }
    System.setProperty(APLInterpreter.DEBUG_SYSTEM_PROPERTY, "false");
  }

}
