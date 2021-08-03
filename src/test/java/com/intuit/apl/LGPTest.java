package com.intuit.apl;

import com.intuit.apl.model.Result;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class LGPTest {
    private static Logger log = LoggerFactory.getLogger(LGPTest.class);

    @Test
    public void test_lgp_tkt_verification() {
        String[] ruleFiles = {
                "com/intuit/authorization/lgp.apl"
        };

        Map<String, String> environment = new HashMap<String, String>();
        Map<String, String> resource = new HashMap<String, String>();
        Map<String, String> subject = new HashMap<String, String>();
        Map<String, String> action = new HashMap<String, String>();
        List<Map<String, String>> obligationList = new ArrayList<>();

        resource.put("id", "Intuit.iam.identity.account");
        subject.put("authnLevel","25");
        action.put("id", "access");

        try {
            PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
            AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,  obligationList, new ArrayList<Result>());
            assertEquals(AuthZDecision.PERMIT, decision);
        } catch (Exception e) {
            log.error("Error running engine", e);
        }
    }

    @Test
    public void test_lgp_tkt_verification_min_aal() {
        String[] ruleFiles = {
                "com/intuit/authorization/lgp.apl"
        };

        Map<String, String> environment = new HashMap<String, String>();
        Map<String, String> resource = new HashMap<String, String>();
        Map<String, String> subject = new HashMap<String, String>();
        Map<String, String> action = new HashMap<String, String>();
        List<Map<String, String>> obligationList = new ArrayList<>();

        resource.put("id", "Intuit.iam.identity.account");
        subject.put("authnLevel","25");
        subject.put("min_aal","20");
        action.put("id", "access");

        try {
            PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
            AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,  obligationList, new ArrayList<Result>());
            assertEquals(AuthZDecision.PERMIT, decision);
        } catch (Exception e) {
            log.error("Error running engine", e);
        }
    }

    @Test
    public void test_lgp_tkt_verification_min_aal_not_met() {
        String[] ruleFiles = {
                "com/intuit/authorization/lgp.apl"
        };

        Map<String, String> environment = new HashMap<String, String>();
        Map<String, String> resource = new HashMap<String, String>();
        Map<String, String> subject = new HashMap<String, String>();
        Map<String, String> action = new HashMap<String, String>();
        List<Map<String, String>> obligationList = new ArrayList<>();

        resource.put("id", "Intuit.iam.identity.account");
        subject.put("authnLevel","15");
        subject.put("min_aal","20");
        action.put("id", "access");

        try {
            PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
            AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,  obligationList, new ArrayList<Result>());
            assertEquals(AuthZDecision.DENY, decision);
        } catch (Exception e) {
            log.error("Error running engine", e);
        }
    }

    @Test
    public void test_lgp_tkt_verification_offline_access() {
        String[] ruleFiles = {
                "com/intuit/authorization/lgp.apl"
        };

        Map<String, String> environment = new HashMap<String, String>();
        Map<String, String> resource = new HashMap<String, String>();
        Map<String, String> subject = new HashMap<String, String>();
        Map<String, String> action = new HashMap<String, String>();
        List<Map<String, String>> obligationList = new ArrayList<>();

        resource.put("id", "Intuit.iam.identity.account");
        subject.put("authnLevel","25");
        subject.put("min_aal","20");
        subject.put("namespaceId","50000001");
        action.put("id", "offline_access");

        try {
            PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
            AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,  obligationList, new ArrayList<Result>());
            assertEquals(AuthZDecision.PERMIT, decision);
        } catch (Exception e) {
            log.error("Error running engine", e);
        }
    }

    @Test
    public void test_lgp_tkt_verification_offline_access_corpuser() {
        String[] ruleFiles = {
                "com/intuit/authorization/lgp.apl"
        };

        Map<String, String> environment = new HashMap<String, String>();
        Map<String, String> resource = new HashMap<String, String>();
        Map<String, String> subject = new HashMap<String, String>();
        Map<String, String> action = new HashMap<String, String>();
        List<Map<String, String>> obligationList = new ArrayList<>();

        resource.put("id", "Intuit.iam.identity.account");
        subject.put("authnLevel","25");
        subject.put("min_aal","20");
        subject.put("namespaceId","50000000");
        action.put("id", "offline_access");

        try {
            PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
            AuthZDecision decision = policyEngine.decide(subject, resource, action, environment,  obligationList, new ArrayList<Result>());
            assertEquals(AuthZDecision.DENY, decision);
        } catch (Exception e) {
            log.error("Error running engine", e);
        }
    }
}
