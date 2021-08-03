package com.intuit.apl;

import com.intuit.apl.model.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class APLEngineIEPContextTest {
  private static Logger log = LoggerFactory.getLogger(APLEngineIEPContextTest.class);
  private PolicyEngine policyEngine;

  @BeforeClass
  public void setUp() throws Exception {
    String ruleFiles[] = {
        "com/intuit/authorization/IEPContext_v2.apl"
    };
    policyEngine = (new PolicyEngineFactory(ruleFiles)).createNewEngine();
  }

  @Test(dataProvider = "iepContextDataProvider")
  public void testDecide(String ownerId, String ldapGroup, String assetId, String nsId, String actionId, AuthZDecision expectedDecision){
    
	Map<String, String> environment = new HashMap<String, String>();
	Map<String, String> resource = new HashMap<String, String>();
	Map<String, String> subject = new HashMap<String, String>();
	Map<String, String> action = new HashMap<String, String>();
	List<Map<String, String>> obligationList = new ArrayList<>();	

    resource.put("ownerId", ownerId);

    subject.put("ldapGrps", ldapGroup);
    subject.put("assetId", assetId);
    subject.put("nsId", nsId);

    action.put("id", actionId);
    AuthZDecision decision = policyEngine.decide(subject, resource, action, environment, obligationList, new ArrayList<Result>());
    Assert.assertEquals(decision,expectedDecision);
  }

  @DataProvider(name = "iepContextDataProvider")
  private Object[][] iepContextDataProvider(){
    return new Object[][]{
        {
            "50000000", "SFAP[Tax Pro Expert][CG Tax Pro]", "8359824400620181958", "50000000", "us.tax.exp", AuthZDecision.PERMIT
        },
        {
            "50000000", "SFAP[Tax Pro Lead][CG Tax Pro]", "8359824400620181958", "50000000", "us.tax.adm", AuthZDecision.PERMIT
        },
        {
            "50000000", "SFAP[Tax Pro Admin][CG Tax Pro]", "8359824400620181958", "50000000", "us.tax.adm", AuthZDecision.PERMIT
        },
        {
            "50000000", "SFAP[Tax Pro Peer Review][CG Tax Pro]", "8359824400620181958", "50000000", "us.tax.rev", AuthZDecision.PERMIT
        },
        {
            "50000000", "IEP[ca.tax.exp]", "8359824400620181958", "50000000", "ca.tax.exp", AuthZDecision.PERMIT
        },
        {
            "50000000", "IEP[ca.tax.adm]", "8359824400620181958", "50000000", "ca.tax.adm", AuthZDecision.PERMIT
        },
        {
            "50000000", "SFAP[Bookkeeper Expert][US Bk Pro]", "8359824400620181958", "50000000", "us.bk.exp", AuthZDecision.PERMIT
        },
        {
            "50000000", "SFAP[Bookkeeper Admin][US Bk Pro]", "8359824400620181958", "50000000", "us.bk.adm", AuthZDecision.PERMIT
        },
        {
            "50000000", "SFAP[Bookkeeper Reviewer][US Bk Pro]", "8359824400620181958", "50000000", "us.bk.rev", AuthZDecision.PERMIT
        },
        {
            "50000000", "SFAP[Tax Pro Expert][CG Tax Pro]", null, "50000000", "us.tax.exp", AuthZDecision.DENY
        },
        {
            "50000000", "SFAP[Tax Pro Expert][CG Tax Pro]", "8359824400620181958", "50000001", "us.tax.exp", AuthZDecision.DENY
        },
        {
            "50000000", "SFAP[Tax Pro Expert][CG Tax Pro]", "8359824400620181959", "50000000", "us.tax.exp", AuthZDecision.DENY
        },
        {
            "50000000", "SFAP[Tax Pro Expert][CG Tax Pro]", "8359824400620181958", "50000000", "us.tax.adm", AuthZDecision.DENY
        },
        {
            "50000000", "SFAP[Tax Pro Expert][CG Tax Pro]", "8503939588691291606", "50000000", "us.tax.exp", AuthZDecision.PERMIT
        },
        {
            "50000000", "SFAP[Tax Pro Lead][CG Tax Pro]", "8503939588691291606", "50000000", "us.tax.adm", AuthZDecision.PERMIT
        },
        {
            "50000000", "SFAP[Tax Pro Peer Review][CG Tax Pro]", "8503939588691291606", "50000000", "us.tax.rev", AuthZDecision.PERMIT
        },
        {
            "50000000", "IEP[ca.tax.exp]", "8503939588691291606", "50000000", "ca.tax.exp", AuthZDecision.PERMIT
        },
        {
            "50000000", "IEP[ca.tax.adm]", "8503939588691291606", "50000000", "ca.tax.adm", AuthZDecision.PERMIT
        },
        {
            "50000000", "SFAP[Bookkeeper Expert][US Bk Pro]", "8503939588691291606", "50000000", "us.bk.exp", AuthZDecision.PERMIT
        },
        {
            "50000000", "SFAP[Bookkeeper Admin][US Bk Pro]", "8503939588691291606", "50000000", "us.bk.adm", AuthZDecision.PERMIT
        },
        {
            "50000000", "SFAP[Bookkeeper Reviewer][US Bk Pro]", "8503939588691291606", "50000000", "us.bk.rev", AuthZDecision.PERMIT
        },
        {
            "50000000", "SFAP[Tax Pro Expert][CG Tax Pro]", null, "50000000", "us.tax.exp", AuthZDecision.DENY
        },
        {
            "50000000", "SFAP[Tax Pro Expert][CG Tax Pro]", "8503939588691291606", null, "us.tax.exp", AuthZDecision.DENY
        },
        {
            "50000000", "SFAP[Tax Pro Expert][CG Tax Pro]", "8503939588691291606", "50000001", "us.tax.exp", AuthZDecision.DENY
        },
        {
            "50000000", "SFAP[Tax Pro Expert][CG Tax Pro]", "8359824400620181959", "50000000", "us.tax.exp", AuthZDecision.DENY
        },
        {
            "50000000", "SFAP[Tax Pro Expert][CG Tax Pro]", "8503939588691291606", "50000000", "us.tax.adm", AuthZDecision.DENY
        }
    };
  }
}
