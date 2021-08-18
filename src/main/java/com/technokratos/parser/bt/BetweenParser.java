package com.technokratos.parser.bt;

import com.technokratos.dto.enums.OperatorType;
import com.technokratos.dto.filter.BetweenFilter;
import com.technokratos.dto.filter.Filter;
import com.technokratos.parser.OperatorParser;

public class BetweenParser implements OperatorParser {

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.BT;
    }

    @Override
    public Filter parse(final String field, final String value) {
        String[] split = value.split(":");
        return BetweenFilter.builder()
                .field(field)
                .operatorType(OperatorType.BT)
                .firstValue(split[0])
                .secondaryValue(split[1])
                .build();
    }
}
