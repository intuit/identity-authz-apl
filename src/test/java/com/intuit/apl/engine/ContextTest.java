package com.intuit.apl.engine;

import com.intuit.apl.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ContextTest {

    @Test
    public void testNewAdvice(){
        Response response = new Response();
        Context context = new Context<>(null, null, null, null, null, response, null, null);
        Context.MyMap mymap = context.newAdvice();
        mymap.put("testKey", "testValue");

        Assert.assertEquals(1, response.getAdvices().size());
        Assert.assertEquals(1, response.getAdvices().get(0).size());
        Assert.assertEquals(0, response.getObligations().size());
        Assert.assertEquals(0, response.getRemediations().size());
        Assert.assertEquals(0, response.getCauses().size());

        Assert.assertEquals("testValue", response.getAdvices().get(0).get("testKey"));
    }

    @Test
    public void testNewObligation(){
        Response response = new Response();
        Context context = new Context<>(null, null, null, null, null, response, null, null);
        Context.MyMap mymap = context.newObligation();
        mymap.put("testKey", "testValue");

        Assert.assertEquals(1, response.getObligations().size());
        Assert.assertEquals(1, response.getObligations().get(0).size());
        Assert.assertEquals(0, response.getAdvices().size());
        Assert.assertEquals(0, response.getRemediations().size());
        Assert.assertEquals(0, response.getCauses().size());

        Assert.assertEquals("testValue", response.getObligations().get(0).get("testKey"));
    }

    @Test
    public void testNewRemediation(){
        Response response = new Response();
        Context context = new Context<>(null, null, null, null, null, response, null, null);
        Context.MyMap mymap = context.newRemediation();
        mymap.put("testKey", "testValue");

        Assert.assertEquals(1, response.getRemediations().size());
        Assert.assertEquals(1, response.getRemediations().get(0).size());
        Assert.assertEquals(0, response.getAdvices().size());
        Assert.assertEquals(0, response.getObligations().size());
        Assert.assertEquals(0, response.getCauses().size());

        Assert.assertEquals("testValue", response.getRemediations().get(0).get("testKey"));
    }

    @Test
    public void testNewCauseNullRequest(){
        Response response = new Response();
        Context context = new Context<>(null, null, null, null, null, response, null, null);
        Context.MyMap mymap = context.newCause();
        mymap.put("testKey", "testValue");

        Assert.assertEquals(1, response.getCauses().size());
        Assert.assertEquals(1, response.getCauses().get(0).size());
        Assert.assertEquals(0, response.getAdvices().size());
        Assert.assertEquals(0, response.getObligations().size());
        Assert.assertEquals(0, response.getRemediations().size());

        Assert.assertEquals("testValue", response.getCauses().get(0).get("testKey"));
    }

    @Test
    public void testVerifyRequestEmptyRequest(){
        Response response = new Response();
        Map<String, String> subject = new HashMap<>();
        Context context = new Context<>(subject, null, null, null, null, response, null, null);

        boolean flag = context.verifyRequest();

        Assert.assertTrue(flag);
    }

    @Test
    public void testVerifyRequestRequestWithSomeData(){
        Response response = new Response();
        Map<String, String> subject = new HashMap<>();
        subject.put("aal", "25");
        Context context = new Context<>(subject, null, null, null, null, response, null, null);

        boolean flag = context.verifyRequest();

        Assert.assertTrue(flag);
    }

    @Test
    public void testVerifyRequestRequestWithNoDataAndRuleMisMatch(){
        Response response = new Response();
        Map<String, String> subject = new HashMap<>();
        subject.put("verify_aal", "25");
        Context context = new Context<>(subject, null, null, null, null, response, null, null);

        boolean flag = context.verifyRequest();

        Assert.assertFalse(flag);
    }

    @Test
    public void testVerifyRequestRequestWithNullDataAndRuleMisMatch(){
        Response response = new Response();
        Map<String, String> subject = new HashMap<>();
        subject.put("aal", null);
        subject.put("verify_aal", "25");
        Context context = new Context<>(subject, null, null, null, null, response, null, null);

        boolean flag = context.verifyRequest();

        Assert.assertFalse(flag);
    }

    @Test
    public void testVerifyRequestRequestWithEmptyDataAndRuleMisMatch(){
        Response response = new Response();
        Map<String, String> subject = new HashMap<>();
        subject.put("aal", "");
        subject.put("verify_aal", "25");
        Context context = new Context<>(subject, null, null, null, null, response, null, null);

        boolean flag = context.verifyRequest();

        Assert.assertFalse(flag);
    }

    @Test
    public void testVerifyRequestRequestWithSomeDataAndNullRule(){
        Response response = new Response();
        Map<String, String> subject = new HashMap<>();
        subject.put("aal", "25");
        subject.put("verify_aal", null);
        Context context = new Context<>(subject, null, null, null, null, response, null, null);

        boolean flag = context.verifyRequest();

        Assert.assertTrue(flag);
    }

    @Test
    public void testVerifyRequestRequestWithSomeDataAndEmptyRule(){
        Response response = new Response();
        Map<String, String> subject = new HashMap<>();
        subject.put("aal", "25");
        subject.put("verify_aal", "");
        Context context = new Context<>(subject, null, null, null, null, response, null, null);

        boolean flag = context.verifyRequest();

        Assert.assertTrue(flag);
    }

    @Test
    public void testVerifyRequestRequestWithSomeDataAndRuleMatch(){
        Response response = new Response();
        Map<String, String> subject = new HashMap<>();
        subject.put("aal", "25");
        subject.put("verify_aal", "25");
        Context context = new Context<>(subject, null, null, null, null, response, null, null);

        boolean flag = context.verifyRequest();

        Assert.assertTrue(flag);
    }

    @Test
    public void testVerifyRequestRequestWithSomeDataAndRuleMismatch(){
        Response response = new Response();
        Map<String, String> subject = new HashMap<>();
        subject.put("aal", "25");
        subject.put("verify_aal", "26");
        Context context = new Context<>(subject, null, null, null, null, response, null, null);

        boolean flag = context.verifyRequest();

        Assert.assertFalse(flag);
    }

    @Test
    public void testVerifyRequestRequestWithSomeDataAndRuleRegexMatch(){
        Response response = new Response();
        Map<String, String> subject = new HashMap<>();
        subject.put("aal", "25");
        subject.put("verify_aal", "2.");
        Context context = new Context<>(subject, null, null, null, null, response, null, null);

        boolean flag = context.verifyRequest();

        Assert.assertTrue(flag);
    }

    @Test
    public void testVerifyRequestRequestWithSomeDataAndRuleRegexMisMatch(){
        Response response = new Response();
        Map<String, String> subject = new HashMap<>();
        subject.put("aal", "35");
        subject.put("verify_aal", "2?");
        Context context = new Context<>(subject, null, null, null, null, response, null, null);

        boolean flag = context.verifyRequest();

        Assert.assertFalse(flag);
    }

    @Test
    public void testVerifyRequestRequestWithSomeDataAndRuleRegexMatch2(){
        Response response = new Response();
        Map<String, String> subject = new HashMap<>();
        subject.put("aal", "25");
        subject.put("verify_aal", "2.");
        subject.put("userId", "123456");
        subject.put("verify_userId", "12345[6|7]");
        Context context = new Context<>(subject, null, null, null, null, response, null, null);

        boolean flag = context.verifyRequest();

        Assert.assertTrue(flag);
    }

    @Test
    public void testVerifyRequestRequestWithSomeDataAndRuleRegexMisMatch2(){
        Response response = new Response();
        Map<String, String> subject = new HashMap<>();
        subject.put("aal", "25");
        subject.put("verify_aal", "2.");
        subject.put("userId", "123458");
        subject.put("verify_userId", "12345[6|7]");
        Context context = new Context<>(subject, null, null, null, null, response, null, null);

        boolean flag = context.verifyRequest();

        Assert.assertFalse(flag);
    }

    @Test
    public void testVerifyRequestRequestWithSomeDataAndRuleRegexMatch2WithSubjectAndResource(){
        Response response = new Response();
        Map<String, String> subject = new HashMap<>();
        subject.put("aal", "25");
        subject.put("verify_aal", "2.");
        subject.put("userId", "123457");
        subject.put("verify_userId", "12345[6|7]");

        Map<String, String> resource = new HashMap<>();
        resource.put("aal", "10");
        resource.put("verify_aal", ".0");
        resource.put("userId", "1111");
        resource.put("verify_userId", "1111");
        Context context = new Context<>(subject, resource, null, null, null, response, null, null);

        boolean flag = context.verifyRequest();

        Assert.assertTrue(flag);
    }

    @Test
    public void testVerifyRequestRequestWithSomeDataAndRuleRegexMisMatch2WithSubjectAndResource(){
        Response response = new Response();
        Map<String, String> subject = new HashMap<>();
        subject.put("aal", "25");
        subject.put("verify_aal", "2.");
        subject.put("userId", "123457");
        subject.put("verify_userId", "12345[6|7]");

        Map<String, String> resource = new HashMap<>();
        resource.put("aal", "10");
        resource.put("verify_aal", ".0");
        resource.put("userId", "1111");
        resource.put("verify_userId", "12345[6|7]");
        Context context = new Context<>(subject, resource, null, null, null, response, null, null);

        boolean flag = context.verifyRequest();

        Assert.assertFalse(flag);
    }

    @Test
    public void testVerifyRequestRequestWithSomeDataAndRuleRegexMatch2WithSubjectAndResourceAndActionAndEnvironment(){
        Response response = new Response();
        Map<String, String> subject = new HashMap<>();
        subject.put("aal", "25");
        subject.put("verify_aal", "2.");
        subject.put("userId", "123457");
        subject.put("verify_userId", "12345[6|7]");

        Map<String, String> resource = new HashMap<>();
        resource.put("aal", "10");
        resource.put("verify_aal", ".0");
        resource.put("userId", "1111");
        resource.put("verify_userId", "1111");

        Map<String, String> action = new HashMap<>();
        resource.put("aal", "abcd");
        resource.put("verify_aal", "[a][b][c][d]");
        resource.put("userId", "1111");
        resource.put("verify_userId", "1111");

        Map<String, String> environment = new HashMap<>();
        resource.put("aal", "abcd");
        resource.put("verify_aal", "[a-z]*");
        resource.put("userId", "1111");
        resource.put("verify_userId", "1111");
        Context context = new Context<>(subject, resource, action, environment, null, response, null, null);

        boolean flag = context.verifyRequest();

        Assert.assertTrue(flag);
    }

    @Test
    public void testVerifyRequestRequestWithSomeDataAndRuleRegexMisMatch2WithSubjectAndResourceAndActionAndEnvironment(){
        Response response = new Response();
        Map<String, String> subject = new HashMap<>();
        subject.put("aal", "25");
        subject.put("verify_aal", "2.");
        subject.put("userId", "123457");
        subject.put("verify_userId", "12345[6|7]");

        Map<String, String> resource = new HashMap<>();
        resource.put("aal", "10");
        resource.put("verify_aal", ".0");
        resource.put("userId", "1111");
        resource.put("verify_userId", "1111");

        Map<String, String> action = new HashMap<>();
        resource.put("aal", "abcd");
        resource.put("verify_aal", "[a][b][c][d]");
        resource.put("userId", "1111");
        resource.put("verify_userId", "1111");

        Map<String, String> environment = new HashMap<>();
        resource.put("aal", "abcd");
        resource.put("verify_aal", "[a-z]*");
        resource.put("userId", "1111");
        resource.put("verify_userId", "111");
        Context context = new Context<>(subject, resource, action, environment, null, response, null, null);

        boolean flag = context.verifyRequest();

        Assert.assertFalse(flag);
    }
}
