package com.intuit.apl.engine;

import com.intuit.apl.AuthZDecision;
import com.intuit.apl.PolicyEngine;
import com.intuit.apl.PolicyEngineConfiguration;
import com.intuit.apl.PolicyEngineFactory;
import com.intuit.apl.engine.APLEngine;
import com.intuit.apl.model.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**c
 * These are the tests for the APL
 */
public class APLAITest
{
	private static Logger log = LoggerFactory.getLogger(APLAITest.class);

    @Test
	public void simplePerfTest()
	{
		String[] ruleFiles = {
				"com/intuit/authorization/ai.apl"
		};
		
		
        
        PolicyEngine<
        Map<String, String>, 
        Map<String, String>, 
        Map<String, String>, 
        Map<String, String>, 
        Map<String, String>> 
            policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
        
        long duration1 = runPerf(policyEngine);
        
     
        PolicyEngineConfiguration policyEngineConfiguration = new PolicyEngineConfiguration();
        policyEngineConfiguration.put(PolicyEngineConfiguration.ENABLE_AI, new Boolean(true));
        policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine(policyEngineConfiguration);
        
        long duration2 = runPerf(policyEngine);
        log.info("Times faster : "+1.0*duration1/duration2);
	}
        
      private long runPerf(PolicyEngine<
          Map<String, String>, 
          Map<String, String>, 
          Map<String, String>, 
          Map<String, String>, 
          Map<String, String>> 
              policyEngine) 
      {
        
        Map<String, String> environment = new HashMap<String, String>();
        Map<String, String> resource = new HashMap<String, String>();
        Map<String, String> subject = new HashMap<String, String>();
        Map<String, String> action = new HashMap<String, String>();
        List<Map<String, String>> obligationList = new ArrayList<>();
        
        int noOfConditionsEvaluated = -1;

        resource.put("r1", "r0");
        resource.put("r2", "r0");
        resource.put("r3", "r0");
        resource.put("r4", "r0");
        resource.put("r5", "r5");
        resource.put("r6", "r0");
        resource.put("r7", "r0");
        
        long startTime = System.nanoTime();
		for(int i=0; i< 10000; i++) {
			try {
				//AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,  obligationList, new ArrayList<Result>());
				Execution execution = ((APLEngine)policyEngine).executeAPLRulesInternal(subject, resource, action, environment, null, obligationList, new ArrayList<Result>());
				assertEquals(AuthZDecision.DENY, execution.decision);
				if(noOfConditionsEvaluated != execution.noOfConditionsEvaluated ) {
				  log.info("Change in number of conditions evaluated : "+execution.noOfConditionsEvaluated);
				  noOfConditionsEvaluated = execution.noOfConditionsEvaluated;
				}
			} catch (Exception e) {
				log.error("Error running engine", e);
			}
		}
		long duration = System.nanoTime() - startTime;
		log.info("Time for perf run (ns) : "+duration);
		return duration;
	}

}
