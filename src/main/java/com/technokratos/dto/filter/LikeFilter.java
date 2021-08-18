package com.technokratos.dto.filter;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LikeFilter extends Filter {

    private String value;
    private String prefix;
    private String postfix;
}
