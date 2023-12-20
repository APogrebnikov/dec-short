package org.edec.newOrder.manager;

import org.edec.dao.DAO;
import org.edec.newOrder.model.OrderRuleFilterModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.OrderStatusModel;
import org.edec.newOrder.model.OrderTypeModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.synchroMine.model.eso.entity.OrderRule;
import org.edec.utility.constants.OrderActionConst;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import java.util.Collections;
import java.util.List;

public class OrderMainManagerESO extends DAO {
    public OrderEditModel getOrderById (Long idOrder) {
        List<OrderEditModel> orders = getOrderByFilter(null, 3, null, null, "", idOrder, null);
        return orders.size() == 0 ? null : orders.get(0);
    }

    //TODO: добавить хранение и получение session ID
    public List<OrderEditModel> getOrderByFilter (Long idInst, int formofstudy, Long idStatus, Long idType, String fioStudent,
                                                  Long idOrder, Long idOrderRule) {
        String query = "SELECT\n" +
                       "\tSEM.id_institute as idInstitute, SEM.formofstudy as formOfStudy, OH.dateofbegin AS datecreated, OH.dateofend AS datesign, OH.descriptionspec AS description, OH.id_order_rule as idOrderRule,\n" +
                       "\tOH.ordernumber AS number, OST.name AS status, ORR.name AS type, ORR.id_order_type AS orderType, OH.order_url AS url,\n" +
                       //"\tCAST(REGEXP_REPLACE(OH.ordernumber, '(/С)|(/C)', '') AS INTEGER) AS sort_num,\n" +
                       "\t(SELECT family||' '||SUBSTRING(name, 1, 1)||'. '||SUBSTRING(patronymic, 1, 1)||'.' FROM humanface HF WHERE id_humanface = OH.current_hum) AS currenthumanface,\n" +
                       "\tCASE WHEN SEM.season = 0 THEN 'осеннего' ELSE 'весеннего' END AS semesterSeason,\n" +
                       "\tOH.id_order_head AS idOrder, (select count(*) from link_order_student_status inner join link_order_section using(id_link_order_section) where id_order_head = OH.id_order_head) as countStudents,\n" +
                       "\tOH.lotus_id AS idLotus, OH.semester AS idSemester, OH.dateoffinish AS datefinish\n" + "FROM\n" +
                       "\torder_head OH\n" + "\tINNER JOIN order_status_type OST USING (id_order_status_type)\n" +
                       "\tINNER JOIN order_rule ORR USING (id_order_rule)\n" +
                       "\tINNER JOIN semester SEM ON OH.semester = SEM.id_semester\n" +
                       "\tLEFT JOIN link_order_section OS USING (id_order_head)\n" +
                       "\tLEFT JOIN link_order_student_status LOSS USING (id_link_order_section)\n" +
                       "\tLEFT JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                       "\tLEFT JOIN studentcard SC USING (id_studentcard)\n" +
                       "\tLEFT JOIN humanface HF ON SC.id_humanface = HF.id_humanface\n" + "WHERE\n" +
                       "\tCAST(SEM.id_institute AS TEXT) ILIKE :idInst\n" + "\tAND CAST(SEM.formofstudy AS TEXT) ILIKE :formOfStudy\n" +
                       "\tAND CAST(ORR.id_order_type AS TEXT) ILIKE :idType\n" +
                       "\tAND CAST(ORR.id_order_rule AS TEXT) ILIKE :idOrderRule\n" +
                       "\tAND CAST(OH.id_order_status_type AS TEXT) ILIKE :idStatus\n" +
                       "\tAND CAST(OH.id_order_head AS TEXT) ILIKE :idOrder\n" +
                       (fioStudent.equals("") ? "" : "\tAND (HF.family||' '||HF.patronymic||' '||HF.name ILIKE :fio)\n") + "GROUP BY\n" +
                       "\tOH.id_order_head, OST.name, ORR.name, ORR.id_order_type, SEM.id_semester\n" + "ORDER BY\n" +
                       "\tOH.semester DESC, datecreated desc, OH.id_order_status_type = 1 DESC, OH.id_order_status_type = 2 DESC, OH.id_order_status_type = 4 DESC, OH.id_order_status_type = 3 DESC\n";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("number")
                              .addScalar("datesign")
                              .addScalar("type")
                              .addScalar("datecreated")
                              .addScalar("semesterSeason")
                              .addScalar("description")
                              .addScalar("status")
                              .addScalar("url")
                              .addScalar("countStudents", LongType.INSTANCE)
                              .addScalar("idOrder", LongType.INSTANCE)
                              .addScalar("orderType", LongType.INSTANCE)
                              .addScalar("idLotus")
                              .addScalar("currenthumanface")
                              .addScalar("idOrderRule", LongType.INSTANCE)
                              .addScalar("idSemester", LongType.INSTANCE)
                              .addScalar("idInstitute", LongType.INSTANCE)
                              .addScalar("formOfStudy")
                              .addScalar("datefinish")
                              .setResultTransformer(Transformers.aliasToBean(OrderEditModel.class));
        q.setParameter("idInst", idInst == null ? "%%" : String.valueOf(idInst), StringType.INSTANCE)
         .setParameter("idOrderRule", idOrderRule == null ? "%%" : String.valueOf(idOrderRule), StringType.INSTANCE)
         .setParameter("formOfStudy", formofstudy == 3 ? "%%" : String.valueOf(formofstudy), StringType.INSTANCE)
         .setParameter("idType", idType == null ? "%%" : String.valueOf(idType), StringType.INSTANCE)
         .setParameter("idStatus", idStatus == null ? "%%" : String.valueOf(idStatus), StringType.INSTANCE)
         .setParameter("idOrder", idOrder == null ? "%%" : String.valueOf(idOrder), StringType.INSTANCE);

        if (!fioStudent.equals("")) {
            q.setParameter("fio", "%" + fioStudent + "%", StringType.INSTANCE);
        }
        return (List<OrderEditModel>) getList(q);
    }

    public List<OrderTypeModel> getOrderTypeList() {
        String query = "SELECT id_order_type AS idType, name FROM order_type order by position";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idType", LongType.INSTANCE)
                              .addScalar("name")
                              .setResultTransformer(Transformers.aliasToBean(OrderTypeModel.class));
        return (List<OrderTypeModel>) getList(q);
    }

    public List<OrderStatusModel> getAllStatus () {
        String query = "SELECT id_order_status_type AS idStatus, name FROM order_status_type";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idStatus", LongType.INSTANCE)
                              .addScalar("name")
                              .setResultTransformer(Transformers.aliasToBean(OrderStatusModel.class));
        return (List<OrderStatusModel>) getList(q);
    }

    public void deleteOrderWithId (long idOrder) throws Exception {
        try {
            begin();

            String deleteLoss = "delete from\n" + "\tlink_order_student_status\n" + "where\n" + "\tid_link_order_section in \n" + "\t(\n" +
                                "\t\tselect id_link_order_section from link_order_section where id_order_head = " + idOrder + "\n" + "\t)";

            String deleteLos = "delete from\n" + "\tlink_order_section\n" + "where\n" + "\tid_order_head = " + idOrder;

            String deleteOrder = "delete from\n" + "\torder_head\n" + "where\n" + "\tid_order_head = " + idOrder;

            getSession().createSQLQuery(deleteLoss).executeUpdate();
            getSession().createSQLQuery(deleteLos).executeUpdate();
            getSession().createSQLQuery(deleteOrder).executeUpdate();

            commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            rollback();
            throw new Exception();
        } finally {
            close();
        }
    }

    public List<OrderRuleFilterModel> getOrderRuleFilter () {
        String query = "select id_order_rule as idOrderRule, name from order_rule where id_institute = 1";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idOrderRule", LongType.INSTANCE)
                .addScalar("name")
                .setResultTransformer(Transformers.aliasToBean(OrderRuleFilterModel.class));
        return (List<OrderRuleFilterModel>) getList(q);
    }
}
