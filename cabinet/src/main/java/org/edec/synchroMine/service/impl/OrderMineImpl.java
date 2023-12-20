package org.edec.synchroMine.service.impl;

import org.edec.synchroMine.manager.EntityManagerOrderDBO;
import org.edec.synchroMine.model.mine.Order;
import org.edec.synchroMine.model.mine.OrderAction;
import org.edec.synchroMine.model.mine.OrderActionStudent;
import org.edec.synchroMine.model.mine.StudentMove;
import org.edec.synchroMine.service.OrderService;

import java.util.Date;
import java.util.List;


public class OrderMineImpl implements OrderService {
    private EntityManagerOrderDBO emOrderDBO = new EntityManagerOrderDBO();

    @Override
    public List<Order> getOrderByInst (Long idInst, Date dateCreated) {
        return emOrderDBO.getOrderByInst(idInst, dateCreated);
    }

    @Override
    public List<OrderActionStudent> getOrderStudents (Long idOrder) {
        return emOrderDBO.getOasByOrder(idOrder);
    }

    @Override
    public List<OrderAction> getOrderAction (Long idOrder) {
        return emOrderDBO.getOrderAction(idOrder);
    }

    @Override
    public List<StudentMove> getStudentMove (String orderNumber, Long idInst) {
        return emOrderDBO.getStudentMoveByOrderAndInst(orderNumber, idInst);
    }

    @Override
    public boolean updateOrderActionStudent (OrderActionStudent oas) {
        return emOrderDBO.saveOrUpdate(oas);
    }

    @Override
    public boolean updateStudentMove (StudentMove studentMove) {
        return emOrderDBO.saveOrUpdate(studentMove);
    }
}
