package com.intuit.apl.engine;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

class TestMakingOwnFunctions {

    public static void  main(String[] args) throws NoSuchMethodException {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext seContext = new StandardEvaluationContext();

        seContext.registerFunction("getStringLen",Myclass.class.getDeclaredMethod("getmyLen",String.class));
        Integer len =  parser.parseExpression("#getStringLen('concretepage.com')").getValue(seContext, Integer.class);
        System.out.println(len);
    }
}

abstract class Myclass {
    public static int getmyLen(String str) {
        return str.length()+10;
    }
}
