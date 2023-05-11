package com.intuit.apl;

import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ResponseTest {

    @Test
    public void testDefaultConstructor(){
        Response response = new Response();
        Assert.assertNull(response.getDecision());
        Assert.assertEquals(0, response.getAdvices().size());
        Assert.assertEquals(0, response.getCauses().size());
        Assert.assertEquals(0, response.getObligations().size());
        Assert.assertEquals(0, response.getRemediations().size());
    }

    @Test
    public void testConstructorWithDecision(){
        Response response = new Response(AuthZDecision.PERMIT);
        Assert.assertEquals(AuthZDecision.PERMIT, response.getDecision());
        Assert.assertEquals(0, response.getAdvices().size());
        Assert.assertEquals(0, response.getCauses().size());
        Assert.assertEquals(0, response.getObligations().size());
        Assert.assertEquals(0, response.getRemediations().size());
    }

    @Test
    public void testConstructorWithObligation(){
        List<Map<String, String>> obligations = new LinkedList<>();
        Response response = new Response(obligations);
        Assert.assertNull(response.getDecision());
        Assert.assertEquals(0, response.getAdvices().size());
        Assert.assertEquals(0, response.getCauses().size());
        Assert.assertEquals(obligations, response.getObligations());
        Assert.assertEquals(0, response.getRemediations().size());
    }

    @Test
    public void testConstructorWithObligationAndOtherV2Params(){
        List<Map<String, String>> obligations = new LinkedList<>();
        List<Map<String, String>> remediations = new LinkedList<>();
        List<Map<String, String>> causes = new LinkedList<>();
        List<Map<String, String>> advices = new LinkedList<>();
        Response response = new Response(obligations, remediations, causes, advices);
        Assert.assertNull(response.getDecision());
        Assert.assertEquals(advices, response.getAdvices());
        Assert.assertEquals(causes, response.getCauses());
        Assert.assertEquals(obligations, response.getObligations());
        Assert.assertEquals(remediations, response.getRemediations());
    }

    @Test
    public void testConstructorWithNullObligationAndOtherV2Params(){
        Response response = new Response(null, null, null, null);
        Assert.assertNull(response.getDecision());
        Assert.assertEquals(0, response.getAdvices().size());
        Assert.assertEquals(0, response.getCauses().size());
        Assert.assertEquals(0, response.getObligations().size());
        Assert.assertEquals(0, response.getRemediations().size());
    }
}
