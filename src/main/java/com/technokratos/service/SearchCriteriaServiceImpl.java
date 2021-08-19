package com.technokratos.service;

import com.technokratos.dto.enums.JoinType;
import com.technokratos.dto.enums.OperatorType;
import com.technokratos.dto.filter.Filter;
import com.technokratos.dto.filter.SearchCriteria;
import com.technokratos.parser.ExpressionParser;
import com.technokratos.parser.OperatorParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static com.technokratos.util.OperatorUtil.isOperator;
import static com.technokratos.util.OperatorUtil.queryToTokens;

@RequiredArgsConstructor
@Service
public class SearchCriteriaServiceImpl implements SearchCriteriaService {

    private final List<OperatorParser> operatorParsers;
    private final ExpressionParser expressionParser;

    @Override
    public SearchCriteria collectSearchCriteria(final String query) {
        String[] tokens = queryToTokens(query);
        String[] rpn = expressionParser.parseExpression(tokens);
        return search(rpn);
    }

    private SearchCriteria search(final String[] tokens) {
        List<SearchCriteria> search = new ArrayList<>();
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
                                .searchCriteriaList(search)
                                .build();
                    }

                    search.add(SearchCriteria.builder()
                            .joinType(joinType)
                            .filters(new ArrayList<>(filters))
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
}
