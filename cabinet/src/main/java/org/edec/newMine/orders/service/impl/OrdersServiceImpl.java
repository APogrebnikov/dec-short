package org.edec.newMine.orders.service.impl;

import org.edec.newMine.orders.manager.OrdersMineManager;
import org.edec.newMine.orders.model.OrderActionsModel;
import org.edec.newMine.orders.model.OrdersModel;
import org.edec.newMine.orders.service.OrderService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdersServiceImpl implements OrderService {
    OrdersMineManager mineManager  = new OrdersMineManager();
    @Override
    public List<OrdersModel> getMineOrder(Date dateCreateOrdersFrom) {
       return mineManager.getMineOrder(dateCreateOrdersFrom);
    }

    @Override
    public List<OrderActionsModel> getOrdersActions(Long idOrderMine) {
        List<OrderActionsModel> listOrdersAction = mineManager.getOrdersActions(idOrderMine);
        listOrdersAction.stream().forEach((model) -> model.setListStudents(mineManager.getStudents(idOrderMine, model.getIdOrdersAction())));
        return listOrdersAction;
    }
}
