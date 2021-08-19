package com.technokratos.parser;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static com.technokratos.util.OperatorUtil.*;

@Component
public class ExpressionParserImpl implements ExpressionParser {

    @Override
    public String[] parseExpression(String[] inputTokens) {
        List<String> out = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();
        for (String token : inputTokens) {
            if (isOperator(token)) {
                boolean isBreak = false;
                while (!isBreak && !stack.isEmpty() && isOperator(stack.peek())) {
                    if ((isAssociative(token, LEFT_ASSOC) && cmpPrecedence(token, stack.peek()) <= 0)
                            || (isAssociative(token, RIGHT_ASSOC) && cmpPrecedence(token, stack.peek()) < 0)) {
                        out.add(stack.pop());
                        continue;
                    }
                    isBreak = true;
                }
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    out.add(stack.pop());
                }
                stack.pop();
            } else {
                out.add(token);
            }
        }

        out.addAll(stack.stream().toList());
        return out.toArray(new String[0]);
    }
}
