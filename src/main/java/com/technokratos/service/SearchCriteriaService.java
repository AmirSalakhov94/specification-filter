package com.technokratos.service;

import com.technokratos.dto.filter.SearchCriteria;

public interface SearchCriteriaService {

    SearchCriteria collectSearchCriteria(final String query);
}
