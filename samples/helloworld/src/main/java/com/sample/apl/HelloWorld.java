package com.sample.apl;

import com.intuit.apl.model.Result;
import com.intuit.apl.PolicyEngineFactory;
import com.intuit.apl.PolicyEngine;
import com.intuit.identity.authz.sdk.model.Action;
import com.intuit.identity.authz.sdk.model.AuthZDecision;
import com.intuit.identity.authz.sdk.model.Environment;
import com.intuit.identity.authz.sdk.model.Obligation;
import com.intuit.identity.authz.sdk.model.Resource;
import com.intuit.identity.authz.sdk.model.Subject;
import java.util.ArrayList;
import java.util.List;

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

	public static void main(String[] args) {
		
		//1. Setup the usecase
		// My product has a report called CustomDetailedReport that is 
		// accessible depending on flavour of the product and also role of the user
		String policies [] = {
				"com/intuit/authorization/my-product-rules.apl"
		};

		Environment environment = new Environment();
		environment.setAttribute("product", "my-product");
		environment.setAttribute("sku", "BASIC");

		Resource resource = new Resource();
		resource.setAttribute("id", "CustomDetailedReport");

		Subject subject = new Subject();
		subject.setAttribute("role", "admin");

		Action action = new Action();
		action.setAttribute("name", "execute");

		PolicyEngine policyEngine = null;
		List<Obligation> obligationList = new ArrayList<Obligation>();

		//2. Get a decision based on policy
		try {
			policyEngine = (new PolicyEngineFactory(policies)).createNewEngine();
			AuthZDecision decision = policyEngine.decide( subject, resource, action, environment, obligationList, new ArrayList<Result>());
			System.out.println(decision);
			System.out.println(obligationList);
		} catch (Exception e) {
			System.out.println("Error running hello world: "+e.getMessage());
			e.printStackTrace();
			return;
		}
		
		//3. Warm up the system : run it 1000 times
		try {
			for(int i=0; i<1000; i++) {
				policyEngine.decide( subject, resource, action,environment, obligationList, new ArrayList<Result>());
			}
		} catch (Exception e) {
			System.out.println("Error warming up hello world: "+e.getMessage());
			e.printStackTrace();
			return;
		}
		
		//4. Explain the policy and execution to provide insights
		try {
			String explanation = policyEngine.explain( subject, resource, action,environment, obligationList, new ArrayList<Result>());
			System.out.println(explanation);
		} catch (Exception e) {
			System.out.println("Error explaining hello world: "+e.getMessage());
			e.printStackTrace();
		}
	}
}
