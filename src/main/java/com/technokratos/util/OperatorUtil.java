package com.technokratos.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class OperatorUtil {

    public static final int LEFT_ASSOC = 0;
    public static final int RIGHT_ASSOC = 1;
    private static final Map<String, int[]> OPERATORS = new HashMap<>();

    static {
        OPERATORS.put("bt", new int[]{0, LEFT_ASSOC});
        OPERATORS.put("or", new int[]{0, LEFT_ASSOC});
        OPERATORS.put("eq", new int[]{0, LEFT_ASSOC});
        OPERATORS.put("and", new int[]{1, LEFT_ASSOC});
    }

    public static boolean isOperator(String token) {
        return OPERATORS.containsKey(token);
    }

    public static boolean isAssociative(String token, int type) {
        if (!isOperator(token)) {
            throw new IllegalArgumentException("Invalid token: " + token);
        }

        return OPERATORS.get(token)[1] == type;
    }

    public static int cmpPrecedence(String token1, String token2) {
        if (!isOperator(token1) || !isOperator(token2)) {
            throw new IllegalArgumentException("Invalid tokens: " + token1 + " " + token2);
        }

        return OPERATORS.get(token1)[0] - OPERATORS.get(token2)[0];
    }

    public static String[] queryToTokens(String query) {
        String changedQuery = StringUtils.normalizeSpace(StringUtils
                .replaceEach(query, new String[]{"(", ")"}, new String[]{" ( ", " ) "}));
        return changedQuery.split(" ");
    }
}
