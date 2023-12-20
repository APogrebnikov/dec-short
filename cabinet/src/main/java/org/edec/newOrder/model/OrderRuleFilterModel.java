package org.edec.newOrder.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OrderRuleFilterModel {

    private Long idOrderRule;
    private String name;

    public OrderRuleFilterModel (Long idOdrerRule, String name) {
        this.idOrderRule = idOrderRule;
        this.name = name;
    }

}
