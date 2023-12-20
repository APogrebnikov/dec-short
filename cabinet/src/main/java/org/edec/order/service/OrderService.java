package org.edec.order.service;

import org.edec.order.model.OrderModel;
import org.edec.order.model.OrderStatusModel;
import org.edec.order.model.OrderTypeModel;

import java.util.List;


public interface OrderService {
    List<OrderModel> getOrderArchiveByFilter (Long idInst, int formOfStudy, Long idStatus, Long idType, String fioStudent);
    List<OrderTypeModel> getDistinctType ();
    List<OrderStatusModel> getAllStatus ();
    void deleteOrder (OrderModel order) throws Exception;
    boolean updateMarks ();
}
