package com.intuit.authorization.policy.engine.apl;
//

import com.intuit.authorization.policy.engine.apl.RuleDefinition;

//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//

/* Code for precompiled version of SimpleDeny. Currently in process */
public class SimpleDeny extends RuleDefinition {

    public int getCountOfConditions() {
        return 1;
    }

    public void test() {
        System.out.println("in test");
    }

    public boolean evaluateCondition(int index, Subject subject, Resource resource, Action action, Environment environment) {
        switch (index) {
            case 0:
                return resource.getvalue("ownerId") == subject.getvalue("id");
        }
        return false;
    }



    //
    public int getCountOfActions() {
        return 1;
    }

    //
    public void evaluateAction(int index, Decision decision) {
        switch (index) {
            case 0:
                decision.setValue(Decision.DecisionValue.DENY);
                break;
        }
    }

//    public void evaluateAllActions() {
//        decision = deny;
//
//    }
}