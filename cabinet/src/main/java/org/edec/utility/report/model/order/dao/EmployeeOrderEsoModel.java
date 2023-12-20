package org.edec.utility.report.model.order.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by dmmax
 */
@Getter
@Setter
@NoArgsConstructor
public class EmployeeOrderEsoModel {
    private String fio;
    private String post;
    private String subquery;

    private Integer actionrule;
    private Integer formofstudy;
}
