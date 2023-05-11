package com.intuit.apl.engine;

import com.intuit.apl.Response;
import org.junit.Assert;
import org.junit.Test;

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
    public void testNewCause(){
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
}
