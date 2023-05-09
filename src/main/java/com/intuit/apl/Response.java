package com.intuit.apl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Response {

    private AuthZDecision decision;
    private List<Map<String, String>> obligations;
    private List<Map<String, String>> causes;
    private List<Map<String, String>> advices;
    private List<Map<String, String>> remediations;


    public Response(){
        this(null, null, null, null);
    }

    public Response(List<Map<String, String>> obligations, List<Map<String, String>> causes, List<Map<String, String>> advices, List<Map<String, String>> remediations){
        this.obligations = obligations == null ? new ArrayList<>() : obligations;
        this.causes = causes == null ? new ArrayList<>() : causes;
        this.advices = advices == null ? new ArrayList<>() : advices;
        this.remediations = remediations == null ? new ArrayList<>() : remediations;
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
