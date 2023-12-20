package org.edec.synchroMine.service;

import org.edec.synchroMine.model.mine.Order;
import org.edec.synchroMine.model.mine.OrderAction;
import org.edec.synchroMine.model.mine.OrderActionStudent;
import org.edec.synchroMine.model.mine.StudentMove;

import java.util.Date;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public interface OrderService {
    List<Order> getOrderByInst (Long idInst, Date dateCreated);
    List<OrderActionStudent> getOrderStudents (Long idOrder);
    List<OrderAction> getOrderAction (Long idOrder);
    List<StudentMove> getStudentMove (String orderNumber, Long idInst);
    boolean updateOrderActionStudent (OrderActionStudent oas);
    boolean updateStudentMove (StudentMove studentMove);
}
