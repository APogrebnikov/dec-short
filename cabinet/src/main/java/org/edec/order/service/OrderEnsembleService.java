package org.edec.order.service;

import org.edec.order.model.OrderModel;


public interface OrderEnsembleService {
    boolean sendOrderToEnsemble(OrderModel order);
}
