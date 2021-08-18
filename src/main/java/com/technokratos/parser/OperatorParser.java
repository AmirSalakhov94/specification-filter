package com.technokratos.parser;

import com.technokratos.dto.enums.OperatorType;
import com.technokratos.dto.filter.Filter;

public interface OperatorParser {

    OperatorType getOperatorType();

    Filter parse(final String field, final String value);
}
