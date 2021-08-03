package com.intuit.apl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.intuit.apl.model.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.AssertJUnit.assertEquals;


public class APLEngineQuotaTest {
    private static Logger log = LoggerFactory.getLogger(APLEngineQuotaTest.class);
    @Test
    public void testQuotaExplain()
    {
        String ruleFiles [] = {
            "com/intuit/authorization/internalPolicy.apl"
        };
        Map<String, String> environment = new HashMap<String, String>();
        environment.put("sku", "BASIC");
        executeQuotaTest(ruleFiles,environment);
    }
    @Test
    public void testQuota()
    {
        String ruleFiles [] = {
            "com/intuit/authorization/quotaTest.apl"
        };
        Map<String, String> environment = new HashMap<String, String>();
        environment.put("sku", "ADVANCED");
        executeQuotaTest(ruleFiles,environment);
    }

    private void executeQuotaTest(String[] ruleFiles, Map<String, String> environment) {
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
        environment.put("product", "QBO");
        resource.put("id", "CustomDetailedReport");
        action.put("name", "execute");
        PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
        String explanation = policyEngine.explain( subject, resource, action,environment, obligationList, new ArrayList<Result>());
        AuthZDecision decision = policyEngine.decide(subject, resource, action,environment, obligationList, new ArrayList<Result>());
        assertEquals(AuthZDecision.PERMIT, decision);
        log.info(explanation);
    }

    @Test
    public void testTTL()
    {
        String ruleFiles [] = {
            "com/intuit/authorization/ttLiveRuleFile.apl"
        };

        Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		

        resource.put("id", "Intuit.ttlive.test.app_id");
        resource.put("ownerId","13563577577860279");
        resource.put("onBehalfOf","1234,5678,9101112");
        resource.put("idp.metaTags","firm-docs-public,tax-docs");

        subject.put("nsId","50000003");
        subject.put("id","1234");
        //action.put("name", "execute");
        action.put("id","us.tax.adm,us.tax.exp");
        PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
        String explanation = policyEngine.explain( subject, resource, action,environment, obligationList, new ArrayList<Result>());
        log.info(explanation);
    }

    @Test(dataProvider = "IEPDataProvider")
    public void testIEPPolicy(String attributeName, String attributeValue,
        AuthZDecision expectedDecision)
    {
        String ruleFiles [] = {
            "com/intuit/authorization/IEPContext.apl"
        };
        
        Map<String, String> environment = new HashMap<String, String>();
		Map<String, String> resource = new HashMap<String, String>();
		Map<String, String> subject = new HashMap<String, String>();
		Map<String, String> action = new HashMap<String, String>();
		List<Map<String, String>> obligationList = new ArrayList<>();
		
        createIEPTestData(resource,subject,action);
        subject.put(attributeName, attributeValue);
        PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
        String explanation = policyEngine.explain( subject, resource, action,environment, obligationList, new ArrayList<Result>());
        log.info(explanation);
        AuthZDecision decision = policyEngine.decide(subject, resource, action,environment, obligationList, new ArrayList<Result>());
        assertEquals(expectedDecision, decision);
    }

    @DataProvider(name = "IEPDataProvider")
    public static Object[][] IEPDataProvider() {
        return new Object[][]{
            {"assetId","8359824400620181958", AuthZDecision.PERMIT},
            {"asse1tId","18359824400620181958", AuthZDecision.DENY}
        };
    }

    private void createIEPTestData ( Map<String, String> resource,  Map<String, String> subject, Map<String, String> action) {

        resource.put("id", "iep.v2");
        resource.put("ownerId","50000000");
        subject.put("ldapGrps","sfap[Tax Pro Expert][CG Tax Pro]");
        subject.put("nsId","50000000");
        action.put("id","us.tax.exp,us.tax.adm");
    }



}
