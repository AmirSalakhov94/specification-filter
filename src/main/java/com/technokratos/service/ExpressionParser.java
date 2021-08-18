package com.technokratos.service;

public interface ExpressionParser {

    String[] infixToRPN(String[] inputTokens);
}
