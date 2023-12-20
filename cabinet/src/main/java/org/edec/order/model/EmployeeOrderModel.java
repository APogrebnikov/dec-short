package org.edec.order.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class EmployeeOrderModel {
    private Boolean sign;
    private Integer actionrule;
    private Long idHum;
    private String email;
    private String fio;
    /**
     * Доп запрос для динамических подписывающих лиц (иностранный отдел)
     **/
    private String subquery;
}
