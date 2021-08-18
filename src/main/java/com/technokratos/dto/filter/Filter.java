package com.technokratos.dto.filter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.technokratos.dto.enums.OperatorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, property = "operationType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(
                value = BetweenFilter.class,
                name = "BT"),
        @JsonSubTypes.Type(
                value = EqualFilter.class,
                name = "EQ"),
        @JsonSubTypes.Type(
                value = LikeFilter.class,
                name = "LIKE")
})
public class Filter {

    private String field;
    private OperatorType operatorType;
}
