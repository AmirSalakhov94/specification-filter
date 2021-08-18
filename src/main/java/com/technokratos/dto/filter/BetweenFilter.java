package com.technokratos.dto.filter;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class BetweenFilter extends Filter {

    private String firstValue;
    private String secondaryValue;
}
