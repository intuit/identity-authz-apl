package com.sample.apl;

import com.intuit.apl.AuthZDecision;
import com.intuit.apl.model.Result;
import com.intuit.apl.PolicyEngineFactory;
import com.intuit.apl.PolicyEngine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Hello World demonstrates a sample APL and call to APL Policy Engine to decide on access control.
 * This is setup as a simple java application to have minimum dependencies.
 * 
 * This sample has 5 parts,
 *     1. Setup the usecase
 *        Resource, Action, Subject and Environment are set for decision to be made in this context.
 *     2. Get a decision based on policy
 *        A Permit or Deny decision is made. Note: there could be decisions beyond Permit and Deny.
 *     3. Warm up the system
 *        For explain feature in next step to give realistic metrics we warm up the system.
 *     4. Explain the policy and execution to provide insights
 *        Provides information about policy and one execution of policy.
 *   
 *     For more details please refer to README.
 *     @see <a href="../../../../../../README.md">README</a>
 *     
 * @author bdutt
 *
 */
public class HelloWorld {
	static Logger logger = LoggerFactory.getLogger(HelloWorld.class);

	public static void main(String[] args) {
		
		//1. Setup the usecase
		// My product has a report called CustomDetailedReport that is 
		// accessible depending on flavour of the product and also role of the user
		String policies [] = {
				"com/intuit/authorization/my-product-rules.apl"
		};

		Map<String, String> environment = new HashMap<String, String>();
        Map<String, String> resource = new HashMap<String, String>();
        Map<String, String> subject = new HashMap<String, String>();
        Map<String, String> action = new HashMap<String, String>();
        List<Map<String, String>> obligationList = new ArrayList<>();
        
		environment.put("product", "my-product");
		environment.put("sku", "BASIC");

		resource.put("id", "CustomDetailedReport");

		subject.put("role", "admin");

		action.put("name", "execute");

		PolicyEngine policyEngine = null;

		//2. Get a decision based on policy
		try {
			policyEngine = (new PolicyEngineFactory(policies)).createNewEngine();
			AuthZDecision decision = policyEngine.decide( subject, resource, action, environment, obligationList, new ArrayList<Result>());
			System.out.println(decision);
			System.out.println(obligationList);
		} catch (Exception e) {
			logger.error("Error running hello world: "+e.getMessage(), e);
			return;
		}
		
		//3. Warm up the system : run it 1000 times
		try {
			for(int i=0; i<1000; i++) {
				policyEngine.decide( subject, resource, action,environment, obligationList, new ArrayList<Result>());
			}
		} catch (Exception e) {
			logger.error("Error warming up hello world: "+e.getMessage(), e);
			return;
		}
		
		//4. Explain the policy and execution to provide insights
		try {
			String explanation = policyEngine.explain( subject, resource, action,environment, obligationList, new ArrayList<Result>());
			logger.info(explanation);
		} catch (Exception e) {
			logger.error("Error explaining hello world: "+e.getMessage(), e);
		}
	}
}
