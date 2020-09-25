package com.sogoodlabs.planner.util;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class StringUtils {

    public final static String DIVISOR = "/";


    public static String getFullName(Object object, String[] spelExpressions){
        ExpressionParser parser = new SpelExpressionParser();
        StringBuilder sb = new StringBuilder();
        String divisor = "";
        for(String expression : spelExpressions){
            String expResult = (String) parser.parseExpression(expression).getValue(object);
            if(expResult!=null){
                sb.append(divisor);
                divisor = DIVISOR;
                sb.append(expResult);
            }
        }
        return sb.toString();
    }

    public static Object getValue(Object obj, String expr){
        ExpressionParser parser = new SpelExpressionParser();
        return parser.parseExpression(expr).getValue(obj);
    }

}
