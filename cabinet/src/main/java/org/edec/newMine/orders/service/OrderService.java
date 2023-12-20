package org.edec.newMine.orders.service;

import org.edec.newMine.orders.model.OrderActionsModel;
import org.edec.newMine.orders.model.OrdersModel;

import java.util.Date;
import java.util.List;

public interface OrderService {
    List<OrdersModel> getMineOrder(Date dateCreateOrdersFrom);
    List<OrderActionsModel> getOrdersActions (Long idOrderMine);
}
