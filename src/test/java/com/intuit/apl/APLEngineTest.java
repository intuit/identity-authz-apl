package com.intuit.apl;

import com.intuit.apl.engine.CustomFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.testng.annotations.Test;

import com.intuit.apl.model.Result;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * c These are the tests for the APL Engine
 */
public class APLEngineTest {
	private static Logger log = LoggerFactory.getLogger(APLEngineTest.class);

	/*
	 * Adjust number of threads, invocationCount represents number of times
	 * testPerformance would be calling
	 */
	/* Run with VisualVM to see the heap usage, cpu usage for the test */

	/*
	 * This test supports multithreading and calculates performance execution time
	 */
//	@Test
	public void testPerformance() {
		String[] ruleFiles = { "com/intuit/authorization/qbo-rules.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		environment.put("product", "QBO");
		environment.put("sku", "BASIC");

		Map<String, String> resource = new HashMap<String, String>();
		resource.put("id", "CustomDetailedReport");

		Map<String, String> subject = new HashMap<String, String>();
		subject.put("role", "admin");

		Map<String, String> action = new HashMap<String, String>();
		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();

			MultithreadedStressTester stressTester = new MultithreadedStressTester(1000000);

			// Warm up
			stressTester.stress(() -> {
				AuthZDecision decision = policyEngine.decide(subject, resource, action, environment, new ArrayList<>(),
						new ArrayList<Result>());
				assertEquals(AuthZDecision.DENY, decision);
			});

			// Now run perf test
			long startTime = System.nanoTime();
			stressTester.stress(() -> {
				AuthZDecision decision = policyEngine.decide(subject, resource, action, environment, new ArrayList<>(),
						new ArrayList<Result>());
				assertEquals(AuthZDecision.DENY, decision);
			});
			long duration = System.nanoTime() - startTime;
			log.info("Time taken (micro-secs) : " + (duration / (1000000 * 1000L)));
		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void testDeny() {
		String[] ruleFiles = { "com/intuit/authorization/qbo-rules.apl" };
		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		environment.put("product", "QBO");
		environment.put("sku", "BASIC");

		
		resource.put("id", "CustomDetailedReport");

		
		subject.put("role", "admin");

		
		action.put("name", "execute");

		PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
		AuthZDecision decision = policyEngine.decide(subject, resource, action, environment, obligationList,
				new ArrayList<Result>());
		assertEquals(AuthZDecision.DENY, decision);

	}

	@Test
	public void testDenyWithResponse() {
		String[] ruleFiles = { "com/intuit/authorization/qbo-rules-v2.apl" };
		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();

		environment.put("product", "QBO");
		environment.put("sku", "BASIC");

		resource.put("id", "CustomDetailedReport");

		subject.put("role", "admin");

		action.put("name", "execute");

		PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
		Response response = policyEngine.decide(subject, resource, action, environment,
				new ArrayList<Result>());
		assertEquals(AuthZDecision.DENY, response.getDecision());
		assertEquals(1, response.getObligations().size());
		assertEquals(3, response.getObligations().get(0).size());
		assertEquals(0, response.getAdvices().size());
		assertEquals(0, response.getCauses().size());
		assertEquals(0, response.getRemediations().size());
		assertEquals("deny", response.getObligations().get(0).get("decision"));
		assertEquals("Deny CustomDetailedReport for QBO Basic", response.getObligations().get(0).get("rule"));
		assertEquals("audit", response.getObligations().get(0).get("id"));
	}

	@Test
	public void testDenyWithResponseWithData() {
		String[] ruleFiles = { "com/intuit/authorization/qbo-rules-v2.apl" };
		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();

		environment.put("product", "QBO");
		environment.put("sku", "PLUS");

		resource.put("id", "CustomDetailedReportWithRemediation");

		subject.put("role", "admin");

		action.put("name", "execute");

		PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
		Response response = policyEngine.decide(subject, resource, action, environment,
				new ArrayList<Result>());
		assertEquals(AuthZDecision.DENY, response.getDecision());
		assertEquals(1, response.getObligations().size());
		assertEquals(3, response.getObligations().get(0).size());
		assertEquals(1, response.getAdvices().size());
		assertEquals(1, response.getAdvices().get(0).size());
		assertEquals(1, response.getCauses().size());
		assertEquals(1, response.getCauses().get(0).size());
		assertEquals(1, response.getRemediations().size());
		assertEquals(1, response.getRemediations().get(0).size());
		assertEquals("deny", response.getObligations().get(0).get("decision"));
		assertEquals("Deny CustomDetailedReport for QBO Basic", response.getObligations().get(0).get("rule"));
		assertEquals("audit", response.getObligations().get(0).get("id"));
		assertEquals("role=admin", response.getAdvices().get(0).get("advice"));
		assertEquals("role have no permission", response.getCauses().get(0).get("cause"));
		assertEquals("invalid role", response.getRemediations().get(0).get("remediation"));
	}

	@Test
	public void testDenyWithResponseWithMultipleData() {
		String[] ruleFiles = { "com/intuit/authorization/qbo-rules-v2.apl" };
		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();

		environment.put("product", "QBO");
		environment.put("sku", "PLUS");

		resource.put("id", "CustomDetailedReportWithMultipleRemediation");

		subject.put("role", "admin");

		action.put("name", "execute");

		PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
		Response response = policyEngine.decide(subject, resource, action, environment,
				new ArrayList<Result>());
		assertEquals(AuthZDecision.DENY, response.getDecision());
		assertEquals(2, response.getObligations().size());
		assertEquals(3, response.getObligations().get(0).size());
		assertEquals(2, response.getAdvices().size());
		assertEquals(1, response.getAdvices().get(0).size());
		assertEquals(2, response.getCauses().size());
		assertEquals(1, response.getCauses().get(0).size());
		assertEquals(2, response.getRemediations().size());
		assertEquals(1, response.getRemediations().get(0).size());
		assertEquals("deny", response.getObligations().get(0).get("decision"));
		assertEquals("Deny CustomDetailedReport for QBO Basic", response.getObligations().get(0).get("rule"));
		assertEquals("audit", response.getObligations().get(0).get("id"));
		assertEquals("role=admin", response.getAdvices().get(0).get("advice"));
		assertEquals("role have no permission", response.getCauses().get(0).get("cause"));
		assertEquals("invalid role", response.getRemediations().get(0).get("remediation"));

		assertEquals(3, response.getObligations().get(1).size());
		assertEquals(1, response.getAdvices().get(1).size());
		assertEquals(1, response.getCauses().get(1).size());
		assertEquals(1, response.getRemediations().get(1).size());
		assertEquals("deny", response.getObligations().get(1).get("decision"));
		assertEquals("Deny CustomDetailedReport for QBO Basic1", response.getObligations().get(1).get("rule"));
		assertEquals("audit1", response.getObligations().get(1).get("id"));
		assertEquals("role=admin1", response.getAdvices().get(1).get("advice1"));
		assertEquals("role have no permission1", response.getCauses().get(1).get("cause1"));
		assertEquals("invalid role1", response.getRemediations().get(1).get("remediation1"));
	}

	@Test
	public void testDenyWithResponseWithIndeterminate() {
		String[] ruleFiles = { "com/intuit/authorization/qbo-rules-v2.apl" };
		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();

		environment.put("product", "QBO");
		environment.put("sku", "PLUS");

		resource.put("id", "Non-matching");

		subject.put("role", "admin");

		action.put("name", "execute");

		PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
		Response response = policyEngine.decide(subject, resource, action, environment,
				new ArrayList<Result>());
		assertEquals(AuthZDecision.INDETERMINATE, response.getDecision());
		assertEquals(0, response.getObligations().size());
		assertEquals(0, response.getAdvices().size());
		assertEquals(0, response.getCauses().size());
		assertEquals(0, response.getRemediations().size());

	}

	@Test
	public void testReteImprove() {
		String[] ruleFiles = { "com/intuit/authorization/testReteImprove.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		

		resource.put("idp.metaTags", "admin");
		resource.put("ownerId", "admin");

		subject.put("id", "admin");
		subject.put("idp.fake", "admin");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment, obligationList,
					new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void testPermit() {
		String[] ruleFiles = { "com/intuit/authorization/qbo-rules.apl" };
		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		environment.put("product", "QBO");
		environment.put("sku", "PLUS");

		resource.put("id", "CustomDetailedReport");

		subject.put("role", "admin");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
					obligationList, new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);
		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void simpleDeny() {
		String[] ruleFiles = { "com/intuit/authorization/simple_deny.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		resource.put("ownerId", "temp");

		subject.put("id", "temp");
		subject.put("role", "admin");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment, obligationList,
					new ArrayList<Result>());
			assertEquals(AuthZDecision.DENY, decision);
		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void testMultipleFiles() {
		String[] ruleFiles = { "com/intuit/authorization/beanResolver_permit.apl",
				"com/intuit/authorization/qbo-rules.apl" };
		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		resource.put("ownerId", "temp");

		subject.put("id", "temp");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment, obligationList,
					new ArrayList<Result>());
			assertEquals(AuthZDecision.DENY, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void simplePermit() {
		String[] ruleFiles = { "com/intuit/authorization/simple_permit.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		resource.put("ownerId", "temp");

		subject.put("id", "temp");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment, obligationList,
					new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void policyAndRuleTargetUnresolved() {
		String[] ruleFiles = new String[] { "com/intuit/authorization/policyAndRuleTargetUnresolved.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		resource.put("idp.metaTags", "temp");

		subject.put("id", "temp");
		subject.put("idp.fake", "temp");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment, obligationList,
					new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void policyEvaluationException() {
		String[] ruleFiles = { "com/intuit/authorization/policyEvaluationException.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		resource.put("idp.metaTags", "temp");
		resource.put("ownerId", "temp");

		subject.put("id", "temp");
		subject.put("idp.fake", "temp");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment, obligationList,
					new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void policyRuleConditionUnresolved() {
		String[] ruleFiles = { "com/intuit/authorization/policyRuleConditionUnresolved.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		resource.put("idp.metaTags", "temp");
		resource.put("idp.bogus", "temp");

		subject.put("id", "temp");
		subject.put("idp.fake", "temp");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment, obligationList,
					new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void policyTargetAndConditionUnresolved() {
		String[] ruleFiles = { "com/intuit/authorization/policyTargetAndConditionUnresolved.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		resource.put("idp.metaTags", "temp");

		subject.put("id", "temp");
		subject.put("idp.fake", "temp");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment, obligationList,
					new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void policyTargetUnresolved() {
		String[] ruleFiles = { "com/intuit/authorization/policyTargetUnresolved.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		resource.put("idp.metaTags", "temp");

		subject.put("id", "temp");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment, obligationList,
					new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void indeterminate() {
		String[] ruleFiles = { "com/intuit/authorization/indeterminate.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		

		subject.put("nsid", "50000000");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
					obligationList, new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void ruleTargetAndConditionUnresolved() {
		String[] ruleFiles = { "com/intuit/authorization/ruleTargetAndConditionUnresolved.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		

		resource.put("idp.metaTags", "temp");

		subject.put("idp.fake", "temp");
		subject.put("id", "temp");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
					obligationList, new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void ruleTargetUnresolved() {
		String[] ruleFiles = { "com/intuit/authorization/ruleTargetUnresolved.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		

		resource.put("idp.metaTags", "temp");

		subject.put("id", "temp");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
					obligationList, new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void roleIdInAction_permit() {
		String[] ruleFiles = { "com/intuit/authorization/roleIdInAction_permit.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		

		resource.put("ownerId", "temp");

		subject.put("id", "temp");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
					obligationList, new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void beanResolver_permit() {
		String[] ruleFiles = { "com/intuit/authorization/beanResolver_permit.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();


		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
					obligationList, new ArrayList<Result>());
			assertEquals(AuthZDecision.DENY, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void filteredBy_ruleTarget() {
		String[] ruleFiles = { "com/intuit/authorization/filteredBy_ruleTarget.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		resource.put("ownerId", "temp");

		subject.put("id", "temp");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
					obligationList, new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void filteredBy_policyTarget() {
		String[] ruleFiles = { "com/intuit/authorization/filteredBy_policyTarget.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		resource.put("ownerId", "temp");

		subject.put("id", "temp");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
					obligationList, new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void internalPolicy() {
		String[] ruleFiles = { "com/intuit/authorization/internalPolicy.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		resource.put("ownerId", "temp");

		subject.put("id", "temp");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
					obligationList, new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void invalidPolicyStr() {
		String[] ruleFiles = { "com/intuit/authorization/invalidPolicyStr.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		resource.put("ownerId", "temp");

		subject.put("id", "temp");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
					obligationList, new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void proDelegation() {
		String[] ruleFiles = { "com/intuit/authorization/proDelegation.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		resource.put("ownerId", "50000000");

		subject.put("nsId", "50000000");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
					obligationList, new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void testExplain() {
		String[] ruleFiles = { "com/intuit/authorization/qbo-rules.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		environment.put("product", "QBO");
		environment.put("sku", "BASIC");

		resource.put("id", "CustomDetailedReport");

		subject.put("role", "admin");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			for (int i = 0; i < 1000; i++) {
				policyEngine.decide(subject, resource, action, environment, obligationList,
						new ArrayList<Result>());
			}
			long startTime = System.nanoTime();
			policyEngine.decide(subject, resource, action, environment, obligationList,
					new ArrayList<Result>());
			long duration = (System.nanoTime() - startTime) / 1000;
			String explanation = policyEngine.explain(subject, resource, action, environment,
					obligationList, new ArrayList<Result>());
			log.info(explanation);
		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void testExplainWithResponse() {
		String[] ruleFiles = { "com/intuit/authorization/qbo-rules.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();

		environment.put("product", "QBO");
		environment.put("sku", "BASIC");

		resource.put("id", "CustomDetailedReport");

		subject.put("role", "admin");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			for (int i = 0; i < 1000; i++) {
				policyEngine.decide(subject, resource, action, environment,
						new ArrayList<Result>());
			}
			long startTime = System.nanoTime();
			policyEngine.decide(subject, resource, action, environment,
					new ArrayList<Result>());
			long duration = (System.nanoTime() - startTime) / 1000;
			String explanation = policyEngine.explain(subject, resource, action, environment,
					new ArrayList<Result>());
			log.info(explanation);
		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void testLotOfQBORules() {
		String[] ruleFiles = { "com/intuit/authorization/lotof-qbo-rules.apl" };

		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		environment.put("product", "QBO");
		environment.put("sku", "PLUS");

		resource.put("id", "CustomDetailedReport");

		subject.put("role", "admin");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			String explanation = policyEngine.explain(subject, resource, action, environment, obligationList,
					new ArrayList<Result>());
			log.info(explanation);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void testPriority() {
		String[] ruleFiles = { "com/intuit/authorization/priority-test.apl"

		};
		
		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		environment.put("product", "QBO");
		environment.put("sku", "PLUS");

		resource.put("id", "CustomDetailedReport");
		action.put("name", "execute");
		try {

			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
					obligationList, new ArrayList<Result>());

			assertEquals(AuthZDecision.PERMIT, decision);
		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void testComplexRules() {
		String[] ruleFiles = { "com/intuit/authorization/complex-conditions.apl"

		};
		
		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		environment.put("product", "QBO");
		environment.put("sku", "PLUS");

		resource.put("id", "CustomDetailedReport");
		action.put("name", "execute");
		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
					obligationList, new ArrayList<Result>());

			assertEquals(AuthZDecision.DENY, decision);
		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void testViewDocument() {
		String[] ruleFiles = { "com/intuit/authorization/document.apl"

		};
		
		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();

		subject.put("userId", "123");
		subject.put("documentId", "0");

		action.put("name", "execute");
		action.put("view", "yes");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
					obligationList, new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);
		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void testNullArguments() {
		String[] ruleFiles = { "com/intuit/authorization/flag_permit.apl"

		};
		
		Map<String, String> environment = null;
		Map<String, String> resource = null;
		Map<String, String> subject = null;
		Map<String, String> action = null;
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		
		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,
					obligationList, new ArrayList<Result>());
			assertEquals(AuthZDecision.DENY, decision);
		} catch (Exception e) {
			log.error("Error running engine", e);
		}

	}

	@Test
	public void testAddAndArbitraryRequest() {
		String[] ruleFiles = { "com/intuit/authorization/add_arbitrary.apl" };
		
		Map<String, String> environment = null;
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = null;
		Map<String, String> action = null;
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		resource.put("b", "2");

		Map<String, Object> request = new HashMap<>();
		request.put("a", "5");

		PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
		AuthZDecision decision = policyEngine.decide(subject, resource, action, environment, request, obligationList,
				new ArrayList<Result>());
		assertEquals(AuthZDecision.DENY, decision);
		System.out.println(obligationList.toString());

	}

	@Test
	public void testSimpleRegex() {
		String[] ruleFiles = { "com/intuit/authorization/simple_regex_deny.apl" };
		
		Map<String, String> environment = null;
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = null;
		List<Map<String, String>> obligationList = new ArrayList<>();
		
		subject.put("role", "aaa,pqr");

		Map<String, Object> request = new HashMap<>();

		PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
		AuthZDecision decision = policyEngine.decide(subject, resource, action, environment, request, obligationList,
				new ArrayList<Result>());
		assertEquals(AuthZDecision.DENY, decision);
		System.out.println(obligationList.toString());

		subject.put("role", "bef,pqr");

		decision = policyEngine.decide(subject, resource, action, environment, request, obligationList,
				new ArrayList<Result>());
		assertEquals(AuthZDecision.PERMIT, decision);
		System.out.println(obligationList.toString());
	}

	@Test
	public void testPermitWithCustomFunction() {
		String[] ruleFiles = { "com/intuit/authorization/custom-function-rule.apl" };
		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();

		resource.put("id", "CustomDetailedReportWithRemediation");
		resource.put("isValid", "true");

		PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles, null, new CustomFunctionImpl())).createNewEngine();
		Response response = policyEngine.decide(subject, resource, action, environment, new ArrayList<Result>());
		assertEquals(AuthZDecision.PERMIT, response.getDecision());
	}

	@Test
	public void testDenyWithCustomFunction() {
		String[] ruleFiles = { "com/intuit/authorization/custom-function-rule.apl" };
		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();

		resource.put("id", "CustomDetailedReportWithRemediation");
		resource.put("isValid", "false");

		PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles, null, new CustomFunctionImpl())).createNewEngine();
		Response response = policyEngine.decide(subject, resource, action, environment, new ArrayList<Result>());
		assertEquals(AuthZDecision.DENY, response.getDecision());
	}

	@Test
	public void testIndeterminatedWithCustomFunction() {
		String[] ruleFiles = { "com/intuit/authorization/custom-function-rule.apl" };
		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();

		resource.put("id", "CustomDetailedReportWithRemediation");
		resource.put("isValid", "false");

		PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
		Response response = policyEngine.decide(subject, resource, action, environment, new ArrayList<Result>());
		assertEquals(AuthZDecision.INDETERMINATE, response.getDecision());
	}

	static class CustomFunctionImpl implements CustomFunctions {

		public boolean isValid(Map<String, String> resource){
			String isValid = resource.get("isValid");
			return "true".equals(isValid);
		}
	}

}
