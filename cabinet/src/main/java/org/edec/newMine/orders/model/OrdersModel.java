package org.edec.newMine.orders.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class OrdersModel {
    private Date dateSign, dateCreate;
    private String ordersNumber, ordersType, ordersDescription;
    private Long idOrerMine;
}
