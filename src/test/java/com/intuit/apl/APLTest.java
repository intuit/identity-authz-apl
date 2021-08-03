package com.intuit.apl;

import com.intuit.apl.model.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**c
 * These are the tests for the APL
 */
public class APLTest
{
	private static Logger log = LoggerFactory.getLogger(APLTest.class);

    @Test
	public void simpleDeny()
	{
		String[] ruleFiles = {
				"com/intuit/authorization/simple_deny.apl"
		};
		
		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();

		resource.put("ownerId", "temp");

		subject.put("id", "temp");
		subject.put("role","admin");

		action.put("name", "execute");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,  obligationList, new ArrayList<Result>());
			assertEquals(AuthZDecision.DENY, decision);
		} catch (Exception e) {
			log.error("Error running engine", e);
		}
	}

	@Test
	public void simplePermit()
	{
		String[] ruleFiles = {
				"com/intuit/authorization/simple_permit.apl"
		};
		
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
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,  obligationList, new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}
	}

}
