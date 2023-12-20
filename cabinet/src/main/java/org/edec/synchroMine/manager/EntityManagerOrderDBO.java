package org.edec.synchroMine.manager;

import org.edec.dao.MineDAO;
import org.edec.synchroMine.model.mine.Order;
import org.edec.synchroMine.model.mine.OrderAction;
import org.edec.synchroMine.model.mine.OrderActionStudent;
import org.edec.synchroMine.model.mine.StudentMove;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;

import java.util.Date;
import java.util.List;


public class EntityManagerOrderDBO extends MineDAO {
    public List<Order> getOrderByInst (Long idInst, Date dateCreated) {
        Query q = getSession().createQuery("from Order where instituteId = :inst " +
                                           (dateCreated != null ? " AND dateCreated >= '" + DateConverter.convertDateToString(dateCreated) +
                                                                  "'" : "") + " ORDER BY id DESC");
        q.setLong("inst", idInst);
        return (List<Order>) getList(q);
    }

    public List<OrderAction> getOrderAction (Long idOrder) {
        Query q = getSession().createQuery("from OrderAction  where idOrder = :idOrder");
        q.setLong("idOrder", idOrder);
        return (List<OrderAction>) getList(q);
    }

    public List<OrderActionStudent> getOasByOrder (Long idOrder) {
        Query q = getSession().createQuery("from OrderActionStudent where idOrder = :idOrder");
        q.setLong("idOrder", idOrder);
        return (List<OrderActionStudent>) getList(q);
    }

    public List<StudentMove> getStudentMoveByOrderAndInst (String orderNumber, Long idInst) {
        Query q = getSession().createQuery("from StudentMove where orderNumber LIKE :orderNumber AND idInstituteFrom = :idInst");
        q.setString("orderNumber", orderNumber).setLong("idInst", idInst);
        return (List<StudentMove>) getList(q);
    }

    public boolean deleteOrder (Long idOrder) {
        Query q = getSession().createQuery("delete from Order where id = :idOrder");
        q.setLong("idOrder", idOrder);
        return execute(q);
    }
}