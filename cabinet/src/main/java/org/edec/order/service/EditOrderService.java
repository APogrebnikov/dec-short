package org.edec.order.service;

import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.order.model.OrderModel;
import org.edec.order.model.StudentModel;
import org.edec.utility.component.model.RatingModel;

import java.util.Date;
import java.util.List;


public interface EditOrderService {
    void fillOrderModel (OrderModel orderModel);
    List<RatingModel> getMarksStudent (Long idLoss, boolean isDebt);
    void updateOrderDesc (String desc, long idOrder);
    void updateDateForLoss (Long idLoss, Date date, int numDate) throws Exception;
    void saveFoundation (Long id, String foundation);
    void saveFoundationStudent (StudentModel student, String foundation);
    void deleteStudents (StudentModel student);
    void updateOrderInfo(OrderModel orderEditModel);
    void syncOrder(Long idOrder);
}
