package com.technokratos;

import com.technokratos.dto.filter.SearchCriteria;
import com.technokratos.service.SearchCriteriaService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestMain {

    public static void main(String[] args) {
        String query = "((statementNumber eq 1241424) or (executiveName eq 1241424)) " +
                "and ((assignDate bt 10.10.2021:11.10.2021) " +
                "and (createDate eq 10.10.2021))";

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext("com.technokratos");
        SearchCriteriaService searchCriteriaService = ctx.getBean(SearchCriteriaService.class);
        SearchCriteria searchCriteria = searchCriteriaService.collectSearchCriteria(query);
        System.out.println(searchCriteria);
    }
}
