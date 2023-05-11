package com.intuit.apl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Response {

    private AuthZDecision decision;
    private List<Map<String, String>> obligations;
    private List<Map<String, String>> causes;
    private List<Map<String, String>> advices;
    private List<Map<String, String>> remediations;

    public Response(AuthZDecision authZDecision){
        this();
        this.decision = authZDecision;
    }

    public Response(){
        this(null, null, null, null);
    }

    public Response(List<Map<String, String>> obligations){
        this(obligations, null, null, null);
    }

    public Response(List<Map<String, String>> obligations, List<Map<String, String>> causes, List<Map<String, String>> advices, List<Map<String, String>> remediations){
        this.obligations = obligations == null ? new LinkedList<>() : obligations;
        this.causes = causes == null ? new LinkedList<>() : causes;
        this.advices = advices == null ? new LinkedList<>() : advices;
        this.remediations = remediations == null ? new LinkedList<>() : remediations;
    }

    public List<Map<String, String>> getAdvices() {
        return advices;
    }
    public List<Map<String, String>> getCauses() {
        return causes;
    }
    public List<Map<String, String>> getObligations() {
        return obligations;
    }
    public List<Map<String, String>> getRemediations() {
        return remediations;
    }

    public AuthZDecision getDecision() {
        return decision;
    }

    public void setDecision(AuthZDecision decision) {
        this.decision = decision;
    }
}
