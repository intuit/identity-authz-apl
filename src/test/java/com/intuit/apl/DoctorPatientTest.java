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
import static org.testng.Assert.assertNotEquals;

/**c
 * These are the tests for the APL
 */
public class DoctorPatientTest
{
	private static Logger log = LoggerFactory.getLogger(DoctorPatientTest.class);

	@Test
	public void simplePermit()
	{
		String[] ruleFiles = {
				"com/intuit/authorization/doctor_patient.apl"
		};
		
		Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		

		resource.put("id", "PatientRecord");
		resource.put("doctorOfThePatient", "Dr A");

		subject.put("id", "Dr A");
		subject.put("role", "employee,doctor");

		action.put("id", "read");
		environment.put("skuOfTheProduct", "PLUS");

		try {
			PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
			AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,  obligationList, new ArrayList<Result>());
			assertEquals(AuthZDecision.PERMIT, decision);

		} catch (Exception e) {
			log.error("Error running engine", e);
		}
		
	}
	
	@Test
    public void simpleDeny()
    {
        String[] ruleFiles = {
                "com/intuit/authorization/doctor_patient.apl"
        };
        
        Map<String, String> environment = new HashMap<String, String>();
        Map<String, String> resource = new HashMap<String, String>();
        Map<String, String> subject = new HashMap<String, String>();
        Map<String, String> action = new HashMap<String, String>();
        List<Map<String, String>> obligationList = new ArrayList<>();
        

        resource.put("id", "PatientRecord");
        resource.put("doctorOfThePatient", "Dr B");

        subject.put("id", "Dr A");
        subject.put("role", "employee,doctor");

        action.put("id", "read");
        environment.put("skuOfTheProduct", "PLUS");

        try {
            PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
            AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,  obligationList, new ArrayList<Result>());
            assertNotEquals(AuthZDecision.PERMIT, decision);

        } catch (Exception e) {
            log.error("Error running engine", e);
        }
        
    }

}
