package com.technokratos.service;

import com.technokratos.dto.enums.JoinType;
import com.technokratos.dto.enums.OperatorType;
import com.technokratos.dto.filter.*;

import java.util.*;

public class ExpressionParserImpl implements ExpressionParser {

    private static final int LEFT_ASSOC = 0;
    private static final int RIGHT_ASSOC = 1;

    private static final Map<String, int[]> OPERATORS = new HashMap<>();

    static {
        OPERATORS.put("bt", new int[]{0, LEFT_ASSOC});
        OPERATORS.put("or", new int[]{0, LEFT_ASSOC});
        OPERATORS.put("eq", new int[]{0, LEFT_ASSOC});
        OPERATORS.put("and", new int[]{5, LEFT_ASSOC});
        OPERATORS.put("/", new int[]{5, LEFT_ASSOC});
    }

    // Test if token is an operator
    private static boolean isOperator(String token) {
        return OPERATORS.containsKey(token);
    }

    // Test associativity of operator token
    private static boolean isAssociative(String token, int type) {
        if (!isOperator(token)) {
            throw new IllegalArgumentException("Invalid token: " + token);
        }

        if (OPERATORS.get(token)[1] == type) {
            return true;
        }
        return false;
    }

    // Compare precedence of operators.
    private static final int cmpPrecedence(String token1, String token2) {
        if (!isOperator(token1) || !isOperator(token2)) {
            throw new IllegalArgumentException("Invalid tokens: " + token1
                    + " " + token2);
        }
        return OPERATORS.get(token1)[0] - OPERATORS.get(token2)[0];
    }

    public static General search(String[] tokens) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        Stack<Filter> stackJoin = new Stack<>();
        Stack<String> stack = new Stack<>();
        for (String token : tokens) {
            if (!isOperator(token)) {
                stack.push(token);
            } else {
                if (stackJoin.empty() && stack.empty()) {
                    if ("or".equals(token)) {
                        return General.builder()
                                .joinType(JoinType.OR)
                                .searchCriteria(searchCriteriaList)
                                .build();
                    } else if ("and".equals(token)) {
                        return General.builder()
                                .joinType(JoinType.AND)
                                .searchCriteria(searchCriteriaList)
                                .build();
                    }
                }
                if ("or".equals(token)) {
                    searchCriteriaList.add(SearchCriteria.builder()
                            .joinType(JoinType.OR)
                            .filters(stackJoin.stream().toList())
                            .build());

                    stackJoin.clear();
                    continue;
                } else if ("and".equals(token)) {
                    searchCriteriaList.add(SearchCriteria.builder()
                            .joinType(JoinType.AND)
                            .filters(stackJoin.stream().toList())
                            .build());

                    stackJoin.clear();
                    continue;
                }

                String value = stack.pop();
                String field = stack.pop();
                if ("eq".equals(token)) {
                    stackJoin.push(EqualFilter.builder()
                            .field(field)
                            .operatorType(OperatorType.EQUAL)
                            .value(value)
                            .build());
                } else if ("bt".equals(token)) {
                    String[] split = value.split(":");
                    stackJoin.push(BetweenFilter.builder()
                            .field(field)
                            .operatorType(OperatorType.BETWEEN)
                            .firstValue(split[0])
                            .secondaryValue(split[1])
                            .build());
                }
            }
        }

        return new General();
    }

    public static void main(String[] args) {
        String[] tokens = ("( ( statementNumber eq 1241424 ) or ( executiveName eq 1241424 ) ) " +
                "and ( ( assignDate bt 10.10.2021:11.10.2021 ) " +
                "and ( createDate eq 10.10.2021 ) )").split(" ");
        ExpressionParser expressionParser = new ExpressionParserImpl();
        String[] strings = expressionParser.infixToRPN(tokens);
        General search = search(strings);
        System.out.println(strings);
    }

    @Override
    public String[] infixToRPN(String[] inputTokens) {
        ArrayList<String> out = new ArrayList<String>();
        Stack<String> stack = new Stack<String>();

        for (String token : inputTokens) {
            if (isOperator(token)) {
                while (!stack.empty() && isOperator(stack.peek())) {
                    if ((isAssociative(token, LEFT_ASSOC) &&
                            cmpPrecedence(token, stack.peek()) <= 0) ||
                            (isAssociative(token, RIGHT_ASSOC) &&
                                    cmpPrecedence(token, stack.peek()) < 0)) {
                        out.add(stack.pop());
                        continue;
                    }
                    break;
                }
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);  //
            } else if (token.equals(")")) {
                while (!stack.empty() && !stack.peek().equals("(")) {
                    out.add(stack.pop());
                }
                stack.pop();
            } else {
                out.add(token);
            }
        }
        while (!stack.empty()) {
            out.add(stack.pop());
        }
        String[] output = new String[out.size()];
        return out.toArray(output);
    }
}
