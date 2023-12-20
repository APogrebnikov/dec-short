package org.edec.order.manager;

import org.edec.dao.DAO;
import org.edec.order.model.dao.OrderImportModel;
import org.edec.synchroMine.model.mine.OrderAction;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;

import java.util.Collections;
import java.util.List;


public class ImportOrderManager extends DAO {
    public List<OrderImportModel> getOrderDataById (Long idOrder) {
        if (idOrder == null) {
            return Collections.emptyList();
        }
        String query = "select \n" + "\tid_link_order_student_status as idLOSS,\n" + "\tsss.id_studentcard as idStudentcard,\n" + "\t(\n" +
                       "\t\tselect id_dic_group from dic_group where dic_group.groupname = loss.groupname order by id_dic_group desc limit 1\n" +
                       "\t) as idDicGroupFromOrder,\n" + "\tdg.id_dic_group as idDicGroup,\n" + "\tdg.id_institute as idInstitute,\n" +
                       "\tlgs.id_semester as idSemester,\n" + "\toh.dateofend as dateAction,\n" + "\tloss.first_date as dateStart,\n" +
                       "\tloss.second_date as dateFinish,\n" + "\toh.ordernumber as orderNumber\n" + "from \n" +
                       "\tlink_order_student_status loss\n" + "\tinner join link_order_section los using(id_link_order_section)\n" +
                       "\tinner join order_head oh using(id_order_head)\n" +
                       "\tinner join student_semester_status sss using(id_student_semester_status)\n" +
                       "\tinner join link_group_semester lgs using(id_link_group_semester)\n" +
                       "\tinner join dic_group dg using(id_dic_group)\n" + "\twhere id_order_head = " + idOrder;

        Query q = getSession().createSQLQuery(query)
                              .addScalar("idLOSS", LongType.INSTANCE)
                              .addScalar("idStudentcard", LongType.INSTANCE)
                              .addScalar("idDicGroupFromOrder", LongType.INSTANCE)
                              .addScalar("idDicGroup", LongType.INSTANCE)
                              .addScalar("idInstitute", LongType.INSTANCE)
                              .addScalar("idSemester", LongType.INSTANCE)
                              .addScalar("dateAction")
                              .addScalar("dateStart")
                              .addScalar("dateFinish")
                              .addScalar("orderNumber")
                              .setResultTransformer(Transformers.aliasToBean(OrderImportModel.class));
        return (List<OrderImportModel>) getList(q);
    }

    public boolean importStudentFromAcademOrder (OrderImportModel student) {
        String query = "insert into order_action (id_studentcard, id_link_order_student_status, id_dic_action, \n" +
                       "\t\t\t  id_dic_group_from, id_institute_from, id_semester, date_action,\n" +
                       "\t\t\t  date_start, date_finish, order_number) \n" + "values\n" + "(\n" + student.getIdStudentcard() + "," +
                       student.getIdLOSS() + "," + " 1," +
                       (student.getIdDicGroupFromOrder() == null ? student.getIdDicGroup() : student.getIdDicGroupFromOrder()) + "," +
                       student.getIdInstitute() + "," + student.getIdSemester() + "," + "'" + student.getDateAction() + "'," + "'" +
                       student.getDateStart() + "'," + "'" + student.getDateFinish() + "'," + "'" + student.getOrderNumber() + "'" + ")";
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean importStudentFromSetEliminationOrder (OrderImportModel student) {
        String query = "insert into order_action (id_studentcard, id_link_order_student_status, id_dic_action, \n" +
                       "\t\t\t  id_dic_group_from, id_institute_from, id_semester, date_action,\n" +
                       "\t\t\t  date_finish, order_number) \n" + "values\n" + "(\n" + student.getIdStudentcard() + "," +
                       student.getIdLOSS() + "," + " 5," +
                       (student.getIdDicGroupFromOrder() == null ? student.getIdDicGroup() : student.getIdDicGroupFromOrder()) + "," +
                       student.getIdInstitute() + "," + student.getIdSemester() + "," + "'" + student.getDateAction() + "'," + "'" +
                       student.getDateStart() + "'," + "'" + student.getOrderNumber() + "'" + ")";
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean importStudentFromSocialOrder(OrderImportModel student){
        String query = "insert into order_action (id_studentcard, id_link_order_student_status, id_dic_action, \n" +
                       "\t\t\t  id_dic_group_from, id_institute_from, id_semester, date_action,\n" +
                       "\t\t\t  date_start, date_finish, order_number, id_dic_scholarship) \n" + "values\n" + "(\n" + student.getIdStudentcard() + "," +
                       student.getIdLOSS() + "," + " 4," +
                       (student.getIdDicGroupFromOrder() == null ? student.getIdDicGroup() : student.getIdDicGroupFromOrder()) + "," +
                       student.getIdInstitute() + "," + student.getIdSemester() + "," + "'" + student.getDateAction() + "'," + "'" +
                       student.getDateStart() + "'," + "'" + student.getDateFinish() + "'," + "'" + student.getOrderNumber() + "', 11" +  ")";

        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean checkIfOrderWasAlreadyTransferred(Long idLOSS){
        String query = "select exists(select 1 from order_action where id_link_order_student_status = " + idLOSS +") as exists";

        Query q = getSession().createSQLQuery(query).addScalar("exists", BooleanType.INSTANCE);

        List<Boolean> list = (List<Boolean>) getList(q);

        return list.get(0);
    }
}
