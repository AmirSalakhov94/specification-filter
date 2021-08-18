package com.technokratos.parser;

import com.technokratos.dto.enums.JoinType;
import com.technokratos.dto.enums.OperatorType;
import com.technokratos.dto.filter.Filter;
import com.technokratos.dto.filter.SearchCriteria;
import com.technokratos.parser.bt.BetweenParser;
import com.technokratos.parser.eq.EqualParser;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class ExpressionParserImpl implements ExpressionParser {

    private static final int LEFT_ASSOC = 0;
    private static final int RIGHT_ASSOC = 1;

    private static final Map<String, int[]> OPERATORS = new HashMap<>();

    private final List<OperatorParser> operatorParsers = Arrays.asList(new BetweenParser(), new EqualParser());

    static {
        OPERATORS.put("bt", new int[]{0, LEFT_ASSOC});
        OPERATORS.put("or", new int[]{0, LEFT_ASSOC});
        OPERATORS.put("eq", new int[]{0, LEFT_ASSOC});
        OPERATORS.put("and", new int[]{5, LEFT_ASSOC});
    }

    public static void main(String[] args) {
        String query = "((statementNumber eq 1241424) or (executiveName eq 1241424)) " +
                "and ((assignDate bt 10.10.2021:11.10.2021) " +
                "and (createDate eq 10.10.2021))";
        String changedQuery = StringUtils.normalizeSpace(StringUtils
                .replaceEach(query, new String[]{"(", ")"}, new String[]{" ( ", " ) "}));
        String[] tokens = changedQuery.split(" ");
        ExpressionParserImpl expressionParser = new ExpressionParserImpl();
        String[] strings = expressionParser.infixToRPN(tokens);
        SearchCriteria search = expressionParser.search(strings);
        System.out.println(strings);
    }

    private boolean isOperator(String token) {
        return OPERATORS.containsKey(token);
    }

    private boolean isAssociative(String token, int type) {
        if (!isOperator(token)) {
            throw new IllegalArgumentException("Invalid token: " + token);
        }

        if (OPERATORS.get(token)[1] == type) {
            return true;
        }
        return false;
    }

    // Compare precedence of operators.
    private int cmpPrecedence(String token1, String token2) {
        if (!isOperator(token1) || !isOperator(token2)) {
            throw new IllegalArgumentException("Invalid tokens: " + token1
                    + " " + token2);
        }
        return OPERATORS.get(token1)[0] - OPERATORS.get(token2)[0];
    }

    public SearchCriteria search(String[] tokens) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        List<Filter> filters = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();
        for (String token : tokens) {
            if (!isOperator(token)) {
                stack.push(token);
            } else {
                String operator = token.toUpperCase();
                if ("OR".equals(operator) || "AND".equals(operator)) {
                    JoinType joinType = JoinType.valueOf(operator);
                    if (filters.isEmpty() && stack.isEmpty()) {
                        return SearchCriteria.builder()
                                .joinType(joinType)
                                .searchCriteriaList(searchCriteriaList)
                                .build();
                    }

                    searchCriteriaList.add(SearchCriteria.builder()
                            .joinType(joinType)
                            .filters(filters)
                            .build());

                    filters.clear();
                } else {
                    final OperatorType operatorType = OperatorType.valueOf(operator);
                    final String value = stack.pop();
                    final String field = stack.pop();
                    operatorParsers.stream()
                            .filter(op -> operatorType == op.getOperatorType())
                            .findFirst()
                            .ifPresent(op -> filters.add(op.parse(field, value)));
                }
            }
        }

        return new SearchCriteria();
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
