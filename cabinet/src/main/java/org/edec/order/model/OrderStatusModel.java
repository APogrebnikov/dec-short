package org.edec.order.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class OrderStatusModel {
    private Long idStatus;
    private String name;

    public OrderStatusModel (Long idStatus, String name) {
        this.idStatus = idStatus;
        this.name = name;
    }
}
