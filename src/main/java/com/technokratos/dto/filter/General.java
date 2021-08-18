package com.technokratos.dto.filter;

import com.technokratos.dto.enums.JoinType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class General {

    private JoinType joinType;
    private List<SearchCriteria> searchCriteria;
}
