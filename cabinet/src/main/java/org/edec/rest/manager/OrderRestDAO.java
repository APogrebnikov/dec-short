package org.edec.rest.manager;

import org.edec.dao.DAO;
import org.edec.synchroMine.model.eso.entity.Order;
import org.edec.synchroMine.model.eso.entity.OrderStatusType;
import org.edec.utility.constants.OrderStatusConst;
import org.edec.utility.constants.ScholarshipTypeConst;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.type.DateType;

import java.sql.Types;
import java.util.Date;
import java.util.List;


public class OrderRestDAO extends DAO {

    public boolean updateOrderAfterLouts(Long idOrder, String ordernumber, String idLotus) {

        String query = "UPDATE order_head SET\n" +
                "ordernumber = :ordernumber,\n" +
                "lotus_id = :idLotus,\n" +
                "dateofend = now()\n" +
                "WHERE id_order_head = :idOrder";
        Query q = getSession().createSQLQuery(query)
                .setParameter("idOrder", idOrder)
                .setParameter("ordernumber", ordernumber)
                .setParameter("idLotus", idLotus);
        return executeUpdate(q);
    }

    public boolean updateOrderStatus(Long idOrder, Long idStatus, Long idHum, String fio,
                                     String certnumber, String operation) {

        String query = "UPDATE order_head SET\n" +
                "id_order_status_type = :idStatus,\n" +
                "current_hum = :idHum,\n" +
                "dateoffinish = :dateOfFinish,\n" +
                "certfio = :certfio,\n" +
                "certnumber = :certnumber,\n" +
                "operation = :operation\n" +
                "WHERE id_order_head = :idOrder";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idStatus", idStatus)
                .setParameter("idHum", idHum == null ? Types.NULL : idHum)
                .setLong("idOrder", idOrder)
                .setParameter("dateOfFinish",
                        OrderStatusConst.getOrderStatusConstById(idStatus) == OrderStatusConst.AGREED
                                ? new Date()
                                : null,
                        DateType.INSTANCE)
                .setString("certfio", fio)
                .setString("certnumber", certnumber)
                .setString("operation", operation);
        return executeUpdate(q);
    }

    public boolean updateSSSdeduction(Long idOrder) {
        return updateStatus("is_deducted", idOrder);
    }

    public boolean updateSSSacademic(Long idOrder) {
        return updateScholarshipDates(ScholarshipTypeConst.ACADEMIC, idOrder);
    }

    public boolean updateSSSacademicIncreased(Long idOrder){
        return updateScholarshipDates(ScholarshipTypeConst.ACADEMIC_INCREASED, idOrder);
    }

    /**
     * Удаление информации о том что студенты, содержащиеся в приказе получали академическую стипендию
     */
    public boolean updateSSScancelAcademic(Long idOrder) {
        return removeScholarshipDates(ScholarshipTypeConst.ACADEMIC, idOrder);
    }

    /**
     * Удаление информации о получении социальной повышенной стипендии (если такая была)
     * и добавление информации о получении студентами социальной стипендии
     */
    public boolean updateSSSsocial(Long idOrder) {
        return removeScholarshipDates(ScholarshipTypeConst.SOCIAL_INCREASED, idOrder)
                && updateScholarshipDates(ScholarshipTypeConst.SOCIAL, idOrder);
    }

    /**
     * Удаление информации о получении социальной стипендии (если такая была)
     * и добавление информации о получении студентами социальной повышенной стипендии
     */
    public boolean updateSSSsocialIncreased(Long idOrder) {
        return removeScholarshipDates(ScholarshipTypeConst.SOCIAL, idOrder)
                && updateScholarshipDates(ScholarshipTypeConst.SOCIAL_INCREASED, idOrder);
    }

    private boolean updateStatus(String columnName, Long idOrder) {
        String query = "UPDATE student_semester_status SET " + columnName + " = 1\n" +
                "WHERE id_student_semester_status IN (\n" +
                "\tSELECT LOSS.id_student_semester_status\n" +
                "FROM link_order_student_status LOSS \n" +
                "\t\tINNER JOIN link_order_section USING (id_link_order_section)\n" +
                "\tWHERE id_order_head = :idOrder)";
        Query q = getSession().createSQLQuery(query)
                .setLong("idOrder", idOrder);
        return executeUpdate(q);
    }

    private boolean updateScholarshipDates(ScholarshipTypeConst typeScholarship, Long idOrder) {
        //language=GenericSQL
        String query = "UPDATE studentcard\n" +
                       "SET order_info = jsonb_set(order_info, CAST('{" + typeScholarship.getJsonName() + "}' AS text[]),\n" +
                       "                           jsonb_build_object('dateFrom',\n" +
                       "                                              (SELECT LOSS.first_date\n" +
                       "                                               FROM link_order_student_status LOSS\n" +
                       "                                                      join link_order_section USING (id_link_order_section)\n" +
                       "                                                      join student_semester_status s on LOSS.id_student_semester_status = s.id_student_semester_status\n" +
                       "                                               WHERE id_order_head = :idOrder and s.id_studentcard = studentcard.id_studentcard and LOSS.second_date is not null)\n" +
                       "                             , 'dateTo'\n" +
                       "                             , (SELECT LOSS.second_date\n" +
                       "                                FROM link_order_student_status LOSS\n" +
                       "                                       join link_order_section USING (id_link_order_section)\n" +
                       "                                       join student_semester_status sss2 on LOSS.id_student_semester_status = sss2.id_student_semester_status\n" +
                       "                                WHERE id_order_head = :idOrder  and sss2.id_studentcard = studentcard.id_studentcard and LOSS.second_date is not null)\n" +
                       "                             ), true)\n" +
                       "WHERE id_studentcard IN (\n" +
                       "  SELECT SSS.id_studentcard\n" +
                       "      from link_order_student_status LOSS\n" +
                       "         join student_semester_status SSS using (id_student_semester_status)\n" +
                       "         join link_order_section USING (id_link_order_section)\n" +
                       "  WHERE id_order_head = :idOrder and id_order_section not in (48,49,50))";

        //language=GenericSQL
        String query2 = "UPDATE studentcard\n" +
                        "SET order_info = jsonb_set(order_info, CAST('{" + typeScholarship.getJsonName() + "}' AS text[]),\n" +
                        "                           jsonb_build_object('dateFrom',\n" +
                        "                                              (SELECT LOSS.first_date\n" +
                        "                                               FROM link_order_student_status LOSS\n" +
                        "                                                      join link_order_section USING (id_link_order_section)\n" +
                        "                                                      join student_semester_status s on LOSS.id_student_semester_status = s.id_student_semester_status\n" +
                        "                                               WHERE id_order_head = :idOrder and s.id_studentcard = studentcard.id_studentcard and LOSS.second_date is not null)\n" +
                        "                             , 'dateTo'\n" +
                        "                             , (SELECT LOSS.second_date\n" +
                        "                                FROM link_order_student_status LOSS\n" +
                        "                                       join link_order_section USING (id_link_order_section)\n" +
                        "                                       join student_semester_status sss2 on LOSS.id_student_semester_status = sss2.id_student_semester_status\n" +
                        "                                WHERE id_order_head = :idOrder  and sss2.id_studentcard = studentcard.id_studentcard and LOSS.second_date is not null)\n" +
                        "                             ), 'isProlongation', 'true', true)\n" +
                        "WHERE id_studentcard IN (\n" +
                        "  SELECT SSS.id_studentcard\n" +
                        "      from link_order_student_status LOSS\n" +
                        "         join student_semester_status SSS using (id_student_semester_status)\n" +
                        "         join link_order_section USING (id_link_order_section)\n" +
                        "  WHERE id_order_head = :idOrder and id_order_section in (48,49,50))";


        Query q = getSession().createSQLQuery(query);
        Query q2 = getSession().createSQLQuery(query2);
        q.setLong("idOrder", idOrder);
        q2.setLong("idOrder", idOrder);
        return executeUpdate(q) && executeUpdate(q2);
    }

    public boolean updateEliminationDate(Long idOrder) {

        //language=GenericSQL
        String query = "UPDATE studentcard\n" +
                       "SET order_info = jsonb_set(order_info, CAST('{dateFirstElimination}' AS text[]),\n" +
                       "                           jsonb_build_object('dateTo'\n" +
                       "                             , (SELECT LOSS.first_date\n" +
                       "                                FROM link_order_student_status LOSS\n" +
                       "                                       join link_order_section USING (id_link_order_section)\n" +
                       "                                       join student_semester_status sss2 on LOSS.id_student_semester_status = sss2.id_student_semester_status\n" +
                       "                                WHERE id_order_head = :idOrder  " +
                       "and sss2.id_studentcard = studentcard.id_studentcard " +
                       "and LOSS.first_date is not null)), true)\n" +
                       "WHERE id_studentcard IN (\n" +
                       "  SELECT SSS.id_studentcard\n" +
                       "      from link_order_student_status LOSS\n" +
                       "         join student_semester_status SSS using (id_student_semester_status)\n" +
                       "         join link_order_section USING (id_link_order_section)\n" +
                       "  WHERE id_order_head = :idOrder)";

        Query q = getSession().createSQLQuery(query);
        q.setLong("idOrder", idOrder);
        return executeUpdate(q);
    }

    public boolean updateTransferConditionally(Long idOrder){

        //language=GenericSQL
        String query = "UPDATE studentcard\n" +
                       "SET order_info = jsonb_set(order_info, CAST('{TransferConditionally}' AS text[]),\n" +
                       "                           jsonb_build_object('orderNumber'\n" +
                       "                             , (SELECT ordernumber\n" +
                       "                                FROM order_head\n" +
                       "                                WHERE id_order_head = :idOrder), 'orderDate'" +
                       "                             , (SELECT dateofend FROM order_head " +
                       "                                WHERE id_order_head = :idOrder)), true)\n" +
                       "WHERE id_studentcard IN (\n" +
                       "  SELECT SSS.id_studentcard\n" +
                       "      from link_order_student_status LOSS\n" +
                       "         join student_semester_status SSS using (id_student_semester_status)\n" +
                       "         join link_order_section USING (id_link_order_section)\n" +
                       "  WHERE id_order_head = :idOrder)";

        Query q = getSession().createSQLQuery(query);
        q.setLong("idOrder", idOrder);
        return executeUpdate(q);
    }

    private boolean removeScholarshipDates(ScholarshipTypeConst typeScholarship, Long idOrder) {
        //language=GenericSQL
        String query = "UPDATE studentcard\n" +
                       "SET order_info = order_info - '" + typeScholarship.getJsonName() + "'\n" +
                       "WHERE id_studentcard IN (\n" +
                       "  SELECT SSS.id_studentcard\n" +
                       "      from link_order_student_status LOSS\n" +
                       "         join student_semester_status SSS using (id_student_semester_status)\n" +
                       "         join link_order_section USING (id_link_order_section)\n" +
                       "  WHERE id_order_head = :idOrder);";

        Query q = getSession().createSQLQuery(query);
        q.setLong("idOrder", idOrder);
        return executeUpdate(q);
    }


    public Order getOrderById(Long id) {
        Query q = getSession().createQuery("from Order where id = :id")
                .setParameter("id", id);
        List<?> list = getList(q);
        return list.size() == 0 ? null : (Order) list.get(0);
    }

    public OrderStatusType getOrderStatusById(Long id) {
        Query q = getSession().createQuery("from OrderStatusType where id = :id")
                .setParameter("id", id);
        List<?> list = getList(q);
        return list.size() == 0 ? null : (OrderStatusType) list.get(0);
    }

    public void updateOrder(Order order) {
        try {
            begin();
            getSession().update(order);
            commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            rollback();
        } finally {
            close();
        }
    }

    public boolean updateSSSTransfer(Long idOrder) {
        return updateStatus("is_transfered", idOrder);
    }

    public boolean updateSSSTransferConditionally(Long idOrder) {
        return updateStatus("is_transfered_conditionally", idOrder);
    }

    public boolean updateTransferInfo(Long idOrder){

        //Создать поле transfer в order_info если его не было до этого времени
        //language=GenericSQL
        getSession().createSQLQuery("UPDATE studentcard " +
                                    "SET order_info = jsonb_set(order_info, CAST('{transferInfo}' AS text[])," +
                                    "'{}',true) where order_info->'transferInfo' is null");
        String query = "UPDATE studentcard\n" +
                       "SET order_info =\n" +
                       "        jsonb_set(order_info, '{transferInfo}',\n" +
                       "                  jsonb_set(CAST(order_info #> '{transferInfo}' AS jsonb)," +
                       " ('{' || (SELECT lgs.course + 1\n" +
                       "                           FROM link_order_student_status LOSS\n" +
                       "                           join link_order_section USING (id_link_order_section)\n" +
                       "                           join student_semester_status s on LOSS.id_student_semester_status = s.id_student_semester_status\n" +
                       "                           join link_group_semester lgs on s.id_link_group_semester = lgs.id_link_group_semester\n" +
                       "                           WHERE id_order_head = :idOrder\n" +
                       "                           and s.id_studentcard = studentcard.id_studentcard)::text '}')::text[],\n" +
                       "                           jsonb_build_object('dateFrom', " +
                       "                                CAST((SELECT LOSS.first_date\n" +
                       "                                FROM link_order_student_status LOSS\n" +
                       "                                join link_order_section USING (id_link_order_section)\n" +
                       "                                                                          join student_semester_status s\n" +
                       "                                                                               on LOSS.id_student_semester_status = s.id_student_semester_status\n" +
                       "                                                                 WHERE id_order_head = :idOrder\n" +
                       "                                                                   and s.id_studentcard = studentcard.id_studentcard) AS text)),\n" +
                       "                            true), true)\n" +
                       "WHERE id_studentcard IN " +
                       "( SELECT SSS.id_studentcard\n" +
                       "    from link_order_student_status LOSS\n" +
                       "             join student_semester_status SSS using (id_student_semester_status)\n" +
                       "             join link_order_section USING (id_link_order_section)\n" +
                       "    WHERE id_order_head = :idOrder);";

        Query q = getSession().createSQLQuery(query);
        q.setLong("idOrder", idOrder);

        return executeUpdate(q);
    }

    public boolean deleteOrderInfo(String field, Long idOrder){

        //language=GenericSQL
        String query = "update studentcard\n" +
                       "set order_info = order_info - '" + field + "'\n" +
                       "where id_studentcard in (select s.id_studentcard\n" +
                       "                         from order_head\n" +
                       "                                  join link_order_section los on order_head.id_order_head = los.id_order_head\n" +
                       "                                  join link_order_student_status loss\n" +
                       "                                       on los.id_link_order_section = loss.id_link_order_section\n" +
                       "                                  join student_semester_status sss\n" +
                       "                                       on loss.id_student_semester_status = sss.id_student_semester_status\n" +
                       "                                  join studentcard s on sss.id_studentcard = s.id_studentcard\n" +
                       "                         where order_head.id_order_head = :idOrderHead)\n";


        Query q = getSession().createSQLQuery(query);
        q.setLong("idOrder", idOrder);

        return executeUpdate(q);
    }


}
