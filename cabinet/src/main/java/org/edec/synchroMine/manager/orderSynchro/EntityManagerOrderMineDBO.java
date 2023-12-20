package org.edec.synchroMine.manager.orderSynchro;

import org.edec.dao.MineDAO;
import org.edec.synchroMine.model.mine.*;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import java.util.Collections;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class EntityManagerOrderMineDBO extends MineDAO {
    public Order createOrder (Order order) {
        return (Order) create(order);
    }

    public OrderAction createOrderAction (OrderAction orderAction) {
        return (OrderAction) create(orderAction);
    }

    public OrderActionStudent createOrderActionStudent (OrderActionStudent oas) {
        return (OrderActionStudent) create(oas);
    }

    public Group getGroupByNameAndCourse (String groupname, Integer course) {
        Query q = getSession().createQuery("from Group where name like :groupname and course = :course");
        q.setString("groupname", groupname).setInteger("course", course);
        List<Group> groups = (List<Group>) getList(q);
        return groups.size() == 0 ? null : groups.get(0);
    }

    public List<Scholarship> getAllScholarship () {
        Query q = getSession().createQuery("from Scholarship");
        return (List<Scholarship>) getList(q);
    }

    public StudentMove createStudentMove (StudentMove studentMove) {
        return (StudentMove) create(studentMove);
    }

    public Student getStudent (Long idStudent) {
        Query q = getSession().createQuery("from Student where Код = :id");
        q.setLong("id", idStudent);
        List<Student> list = (List<Student>) getList(q);
        return list.size() > 0 ? list.get(0) : null;
    }

    public OrderActionType getOrderActionType (Long idOat) {
        Query q = getSession().createQuery("from OrderActionType where Код = :id");
        q.setLong("id", idOat);
        List<?> list = getList(q);
        return list.size() == 0 ? null : (OrderActionType) list.get(0);
    }
}
