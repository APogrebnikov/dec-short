package org.edec.newOrder.service;

import org.edec.newOrder.model.editOrder.OrderEditModel;


public interface OrderEnsembleService {
    boolean sendOrderToEnsemble (OrderEditModel order);
}
