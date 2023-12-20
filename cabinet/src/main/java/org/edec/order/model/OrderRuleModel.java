package org.edec.order.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class OrderRuleModel {
    private Long id;
    private Long idOrderType;
    private String name;
}
