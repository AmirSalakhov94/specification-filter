package com.technokratos.parser.eq;

import com.technokratos.dto.enums.OperatorType;
import com.technokratos.dto.filter.EqualFilter;
import com.technokratos.dto.filter.Filter;
import com.technokratos.parser.OperatorParser;
import org.springframework.stereotype.Component;

@Component
public class EqualParser implements OperatorParser {

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.EQ;
    }

    @Override
    public Filter parse(final String field, final String value) {
        return EqualFilter.builder()
                .field(field)
                .operatorType(OperatorType.EQ)
                .value(value)
                .build();
    }
}
