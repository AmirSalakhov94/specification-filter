package com.technokratos.parser;

public interface ExpressionParser {

    String[] infixToRPN(String[] inputTokens);
}
