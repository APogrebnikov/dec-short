package org.edec.newMine.orders.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderActionsModel {
    private Long idOrdersAction;
    private String ordersAction;
    private List<OrderStudentsModel> listStudents = new ArrayList<>();
}
