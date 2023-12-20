package org.edec.order.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class OrderTypeModel {
    private Long idType;

    private String name;

    public OrderTypeModel (Long idType, String name) {
        this.idType = idType;
        this.name = name;
    }
}
