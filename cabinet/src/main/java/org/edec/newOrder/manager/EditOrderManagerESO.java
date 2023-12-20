package org.edec.newOrder.manager;

import org.edec.dao.DAO;
import org.edec.newOrder.model.dao.LinkOrderSectionEditESO;
import org.edec.newOrder.model.dao.OrderModelESO;
import org.edec.newOrder.model.EmployeeOrderModel;
import org.edec.newOrder.model.dao.SearchGroupModelESO;
import org.edec.newOrder.model.dao.SearchStudentModelESO;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.report.model.OrderLineModel;
import org.edec.order.model.OrderModel;
import org.edec.studentPassport.model.dao.RatingEsoModel;
import org.edec.utility.constants.OrderStatusConst;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import java.util.List;

public class EditOrderManagerESO extends DAO {
    public List<OrderModelESO> getListOrderModelESO (long idOrder) {
        String query = "SELECT\n" + "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio,\n" + "\tSC.recordbook AS recordbook,\n" +
                       "\tLOSS.groupname AS groupname,\n" + "\tOS.name AS sectionname,\n" + "\tOS.id_order_section AS idOS,\n" +
                       "\tLOS.foundation AS foundationLos,\n" + "\tOS.foundation AS foundation,\n" +
                       "\tLOS.first_date AS firstDateSection,\n" + "\tLOS.second_date AS secondDateSection,\n" +
                       "\tLOSS.first_date AS firstDate,\n" + "\tLOSS.second_date AS secondDate,\n" + "\tLOSS.third_date AS thirdDate,\n" +
                       "\tLOS.id_link_order_section AS idSection,\n" + "\tLOSS.id_link_order_student_status AS idStudent,\n" +
                       "\tLOSS.additional AS additionalInfo, sc.other_dbuid as idMine\n" + "FROM \n" + "\torder_head OH\n" +
                       "\tINNER JOIN link_order_section los USING(id_order_head)\n" +
                       "\tINNER JOIN link_order_student_status loss USING(id_link_order_section)\n" +
                       "\tINNER JOIN order_section os USING(id_order_section)\n" +
                       "\tINNER JOIN student_semester_status sss USING(id_student_semester_status)\n" +
                       "\tINNER JOIN link_group_semester lgs USING(id_link_group_semester)\n" +
                       "\tINNER JOIN dic_group dg USING(id_dic_group)\n" + "\tINNER JOIN studentcard sc USING(id_studentcard)\n" +
                       "\tINNER JOIN humanface hf ON sc.id_humanface = hf.id_humanface \n" + "WHERE OH.id_order_head = :idOrder\n" +
                       "\tORDER BY sectionname, groupname, fio";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("firstDate")
                              .addScalar("secondDate")
                              .addScalar("thirdDate")
                              .addScalar("firstDateSection")
                              .addScalar("secondDateSection")
                              .addScalar("fio")
                              .addScalar("recordbook")
                              .addScalar("groupname")
                              .addScalar("sectionname")
                              .addScalar("foundation")
                              .addScalar("foundationLos")
                              .addScalar("idStudent", LongType.INSTANCE)
                              .addScalar("idSection", LongType.INSTANCE)
                              .addScalar("idOS", LongType.INSTANCE)
                              .addScalar("additionalInfo", StringType.INSTANCE)
                              .addScalar("idMine", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(OrderModelESO.class));
        q.setLong("idOrder", idOrder);
        return (List<OrderModelESO>) getList(q);
    }

    public void saveFoundationStudent (Long id, String foundation) {
        String query = "update link_order_student_status set additional = '" + foundation + "' where id_link_order_student_status = " + id;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public void saveFoundation (Long id, String foundation) {
        String query = "update link_order_section set foundation = '" + foundation + "' where id_link_order_section = " + id;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public void removeStudentFromOrder (Long idLoss) {
        String query = "DELETE FROM link_order_student_status WHERE id_link_order_student_status = :idLoss";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idLoss", idLoss);
        executeUpdate(q);
    }

    public void updateLossParam (Long idLoss, String paramQuery) {
        String query = "update link_order_student_status set " + paramQuery + " where id_link_order_student_status = " + idLoss;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public List<EmployeeOrderModel> getEmployeeForEnsemble (Long idOrder) {
        String query = "SELECT HF.family||' '||HF.name||' '||HF.patronymic AS fio, LRE.subquery,\n" +
                "  HF.id_humanface AS idHum, LRE.sign, LRE.actionrule, HF.email\n" +
                "FROM humanface HF\n" +
                "  INNER JOIN employee EMP USING (id_humanface)\n" +
                "  INNER JOIN link_rule_employee LRE USING (id_employee)\n" +
                "  INNER JOIN order_rule ORR ON LRE.id_rule = ORR.id_order_rule\n" +
                "  INNER JOIN order_head OH USING (id_order_rule)\n" +
                "  INNER JOIN semester SEM ON OH.semester = SEM.id_semester\n" +
                "WHERE OH.id_order_head = :idOrder AND (LRE.formofstudy IS NULL OR LRE.formofstudy = SEM.formofstudy)\n" +
                "ORDER BY actionrule DESC, pos;";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("idHum", LongType.INSTANCE)
                .addScalar("sign")
                .addScalar("actionrule")
                .addScalar("email")
                .addScalar("subquery")
                .setResultTransformer(Transformers.aliasToBean(EmployeeOrderModel.class));
        q.setParameter("idOrder", idOrder);
        return (List<EmployeeOrderModel>) getList(q);
    }

    public List<RatingEsoModel> getMarksForStudentInOrder (Long idLoss) {
        String query = "SELECT \n" +
                       "\tSR.is_exam = 1 AS exam, SR.is_pass = 1 AS pass, SR.is_courseproject = 1 AS cp, SR.is_coursework = 1 AS cw, SR.is_practic = 1 AS practic,\n" +
                       "\tSR.examrating, SR.passrating, SR.courseprojectrating AS cprating, SR.courseworkrating AS cwrating, SR.practicrating,\n" +
                       "\tDS.subjectname,\n" + "\tCASE\n" +
                       "\t\tWHEN SEM.season = 0 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend)||' (осень)'\n" +
                       "\t\tWHEN SEM.season = 1 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend)||' (весна)'\n" +
                       "\tEND AS semester\n" + "FROM\n" + "\tstudentcard SC\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN semester SEM USING (id_semester)\n" + "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                       "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" + "\tINNER JOIN subject USING (id_subject)\n" +
                       "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" + "WHERE\n" + "\tid_dic_group = \n" +
                       "\t\t(SELECT id_dic_group FROM link_group_semester \n" +
                       "\t\t INNER JOIN student_semester_status sss USING(id_link_group_semester)\n" +
                       "\t\t INNER JOIN link_order_student_status loss USING(id_student_semester_status)\n" +
                       "\t\t WHERE id_link_order_student_status = :idLoss)\n" + "\tAND id_studentcard = \n" +
                       "\t\t(SELECT id_studentcard FROM student_semester_status sss\n" +
                       "\t\t INNER JOIN link_order_student_status loss USING(id_student_semester_status)\n" +
                       "\t\t WHERE id_link_order_student_status = :idLoss) ORDER BY id_semester, subjectname DESC\n";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("exam")
                              .addScalar("pass")
                              .addScalar("cp")
                              .addScalar("cw")
                              .addScalar("practic")
                              .addScalar("examrating")
                              .addScalar("passrating")
                              .addScalar("cprating")
                              .addScalar("cwrating")
                              .addScalar("practicrating")
                              .addScalar("subjectname")
                              .addScalar("semester")
                              .setResultTransformer(Transformers.aliasToBean(RatingEsoModel.class));
        q.setParameter("idLoss", idLoss);
        return (List<RatingEsoModel>) getList(q);
    }

    public List<LinkOrderSectionEditESO> getLinkOrderSections (Long idOrder) {
        String query =
                "SELECT \n" + "    OS.name, \n" + "    LOS.id_link_order_section AS idLOS,\n" + "    LOS.id_order_section AS idOS,\n" +
                "    LOS.first_date AS firstDate,\n" + "    LOS.second_date AS secondDate\n" + "FROM link_order_section los\n" +
                "INNER JOIN order_section OS USING(id_order_section)\n" + "WHERE \n" + "\tid_order_head = :idOrder";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("name", StringType.INSTANCE)
                              .addScalar("idLOS", LongType.INSTANCE)
                              .addScalar("idOS", LongType.INSTANCE)
                              .setParameter("idOrder", idOrder)
                              .setResultTransformer(Transformers.aliasToBean(LinkOrderSectionEditESO.class));
        return (List<LinkOrderSectionEditESO>) getList(q);
    }

    public List<SearchGroupModelESO> getGroupsForOrderSearch (long idOrder) {
        String query = "SELECT dg.groupname AS name, dg.id_dic_group AS id FROM link_group_semester lgs\n" +
                       "INNER JOIN dic_group dg USING(id_dic_group)\n" +
                       "WHERE lgs.id_semester = :idSemester";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("name", StringType.INSTANCE)
                              .addScalar("id", LongType.INSTANCE)
                              .setParameter("idSemester", getIdSemesterByIdOrder(idOrder))
                              .setResultTransformer(Transformers.aliasToBean(SearchGroupModelESO.class));
        return (List<SearchGroupModelESO>) getList(q);
    }

    public Long getIdSemesterByIdOrder(Long idOrder) {
        Long idSemester;
        //language=GenericSQL
        String listSemQuery = "select semester.id_semester from link_order_section\n" +
                              "    inner join link_order_student_status status on link_order_section.id_link_order_section = status.id_link_order_section\n" +
                              "    inner join student_semester_status sss2 on status.id_student_semester_status = sss2.id_student_semester_status\n" +
                              "    inner join link_group_semester semester on sss2.id_link_group_semester = semester.id_link_group_semester\n" +
                              "    where id_order_head = :idOrder group by semester.id_semester order by semester.id_semester desc ";
        List<Long> semesters = (List<Long>)getSession().createSQLQuery(listSemQuery).setParameter("idOrder", idOrder).list();

        if(semesters.size() == 0) {
            String query = "select currentSem.id_semester from order_head oh\n" +
                           "  inner join semester s on oh.semester = s.id_semester\n" +
                           "  inner join semester currentSem on s.formofstudy = currentSem.formofstudy and s.id_institute = currentSem.id_institute and currentSem.is_current_sem = 1\n" +
                           "where id_order_head = :idOrder";

            idSemester = (Long) getSession().createSQLQuery(query).setParameter("idOrder", idOrder).list().get(0);
        } else {
            idSemester = semesters.get(0);
        }

        return idSemester;
    }

    public List<SearchStudentModelESO> getStudents(String family, String name, String patronymic, String groupname, long idOrder) {


        String query = "SELECT \n" +
                       "\t  hf.name, \n" +
                       "    hf.family AS surname,\n" +
                       "    hf.patronymic,\n" +
                       "    COALESCE(dgCurrent.groupname, dg.groupname) as nameGroup,\n" +
                       "    max(sss.id_student_semester_status) AS idSSS," +
                       "    sc.other_dbuid as idMine,\n" +
                       "    getcurrentsemesterforstudent(id_studentcard) as numberSemester\n" +
                       "FROM student_semester_status sss\n" +
                       "INNER JOIN studentcard sc USING(id_studentcard)\n" +
                       "INNER JOIN humanface hf USING(id_humanface)\n" +
                       "INNER JOIN link_group_semester lgs USING(id_link_group_semester)\n" +
                       "INNER JOIN dic_group dg USING(id_dic_group)\n" +
                       "LEFT JOIN dic_group dgCurrent ON sc.id_current_dic_group = dgCurrent.id_dic_group\n" +
                       "WHERE \n" +
                       "\tlgs.id_semester = :idSemester\n" +
                       "  AND hf.name ILIKE :name\n" +
                       "  AND hf.family ILIKE :family \n" +
                       "  AND hf.patronymic ILIKE :patronymic\n" +
                       "  AND dg.groupname ILIKE :groupname\n" +
                       "GROUP BY name, surname, patronymic, nameGroup, numberSemester, sc.other_dbuid\n";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("name", StringType.INSTANCE)
                              .addScalar("surname", StringType.INSTANCE)
                              .addScalar("patronymic", StringType.INSTANCE)
                              .addScalar("nameGroup", StringType.INSTANCE)
                              .addScalar("idSSS", LongType.INSTANCE)
                              .addScalar("numberSemester", IntegerType.INSTANCE)
                              .addScalar("idMine", LongType.INSTANCE)
                              .setParameter("family", "%" + family + "%", StringType.INSTANCE)
                              .setParameter("name", "%" + name + "%", StringType.INSTANCE)
                              .setParameter("patronymic", "%" + patronymic + "%", StringType.INSTANCE)
                              .setParameter("groupname", "%" + groupname + "%", StringType.INSTANCE)
                              .setParameter("idSemester", getIdSemesterByIdOrder(idOrder), LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(SearchStudentModelESO.class));
        return (List<SearchStudentModelESO>) getList(q);
    }

    public String getFioByIdLoss(Long idLoss) {
        String query = "select hf.family || ' ' ||  hf.name || ' ' || hf.patronymic from humanface hf\n" +
                "inner join studentcard sc using(id_humanface)\n" +
                "inner join student_semester_status sss using(id_studentcard)\n" +
                "inner join link_order_student_status loss using(id_student_semester_status)\n" +
                "where id_link_order_student_status = :idLoss limit 1";

        return getUnique(getSession().createSQLQuery(query).setParameter("idLoss", idLoss));
    }

    public Long getIdOrder(Long idLoss){
        String query = "select los.id_order_head from link_order_student_status\n" +
                       "join link_order_section los on link_order_student_status.id_link_order_section = los.id_link_order_section\n" +
                       "where link_order_student_status.id_link_order_student_status = :idLoss";

        Query q = getSession().createSQLQuery(query).setLong("idLoss", idLoss);

        return ((List<Long>) getList(q)).get(0);
    }

    /*Работа со строками шаблона*/

    public List<OrderLineModel> getOrderLines(Long idOrder){
        String query = "select \n" +
                       "text as lineInfo, \n" +
                       "type as lineType, \n" +
                       "number as lineNumber, \n" +
                       "page as linePage \n" +
                       "from order_line \n" +
                       "where id_order_head = :idOrder \n" +
                       "order by page, number";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("lineInfo")
                              .addScalar("lineNumber", IntegerType.INSTANCE)
                              .addScalar("lineType", IntegerType.INSTANCE)
                              .addScalar("linePage", IntegerType.INSTANCE)
                              .setLong("idOrder", idOrder)
                              .setResultTransformer(Transformers.aliasToBean(OrderLineModel.class));

        return (List<OrderLineModel>) getList(q);
    }

    public boolean saveOrderLines(Long idOrder, OrderLineModel orderLine){
        String query = "INSERT INTO order_line \n" +
                       "(text, number, id_order_head, type, page) \n" +
                       "values (:text, :number, :idOrder, :type, :page) \n";

        Query q = getSession().createSQLQuery(query)
                .setText("text", orderLine.getLineInfo())
                .setInteger("number", orderLine.getLineNumber())
                .setLong("idOrder", idOrder)
                .setInteger("page", orderLine.getLinePage())
                .setInteger("type", orderLine.getLineType());

        return executeUpdate(q);
    }

    public boolean deleteOrderLines(Long idOrder){
        String query = "DELETE FROM order_line \n" +
                       "WHERE id_order_head = :idOrder";

        Query q = getSession().createSQLQuery(query)
                    .setLong("idOrder", idOrder);

        return executeUpdate(q);
    }

    public boolean updateOrderInfo(OrderEditModel orderEditModel){
        String query = "UPDATE order_head \n" +
                       "SET ordernumber = :orderNumber, \n" +
                       "dateofend = :orderDate, \n " +
                       "dateoffinish = :signDate \n " +
                       "WHERE id_order_head = :idOrder \n";

        Query q = getSession().createSQLQuery(query)
                .setLong("idOrder", orderEditModel.getIdOrder())
                .setParameter("orderNumber", orderEditModel.getNumber())
                .setDate("orderDate", orderEditModel.getDatesign())
                .setDate("signDate", orderEditModel.getDatesign());

        return executeUpdate(q);
    }

    public boolean updateOrderInfo(OrderModel orderModel){
        String query = "UPDATE order_head \n" +
                       "SET ordernumber = :orderNumber, \n" +
                       "dateofend = :orderDate \n " +
                       "WHERE id_order_head = :idOrder \n";

        Query q = getSession().createSQLQuery(query)
                              .setLong("idOrder", orderModel.getIdOrder())
                              .setParameter("orderNumber", orderModel.getNumber())
                              .setDate("orderDate", orderModel.getDatesign());

        return executeUpdate(q);
    }

    public boolean updateOrderStatus(Long idOrder, OrderStatusConst orderStatusConst){
        String query = "UPDATE order_head \n" +
                       "SET id_order_status_type = :idOrderStatusType \n" +
                       "WHERE id_order_head = :idOrder \n";

        Query q = getSession().createSQLQuery(query)
                              .setLong("idOrder", idOrder)
                              .setLong("idOrderStatusType", orderStatusConst.getId());

        return executeUpdate(q);
    }

    public boolean updateStudentOrderInfo(String field, Long idOrder){
        String query = "update studentcard\n" +
                       "set order_info = jsonb_set(order_info, CAST('{" + field + "}' AS text[]),\n" +
                       "                           jsonb_build_object('1', '1'), true)\n" +
                       "where id_studentcard in (select s.id_studentcard\n" +
                       "                         from order_head\n" +
                       "                                  join link_order_section los on order_head.id_order_head = los.id_order_head\n" +
                       "                                  join link_order_student_status loss\n" +
                       "                                       on los.id_link_order_section = loss.id_link_order_section\n" +
                       "                                  join student_semester_status sss\n" +
                       "                                       on loss.id_student_semester_status = sss.id_student_semester_status\n" +
                       "                                  join studentcard s on sss.id_studentcard = s.id_studentcard\n" +
                       "                         where order_head.id_order_head = :idOrder)";

        Query q = getSession().createSQLQuery(query)
                              .setLong("idOrder", idOrder);

        return executeUpdate(q);
    }

}
