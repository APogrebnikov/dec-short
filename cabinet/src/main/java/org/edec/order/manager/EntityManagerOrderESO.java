package org.edec.order.manager;

import org.edec.dao.DAO;
import org.edec.order.model.*;
import org.edec.order.model.dao.OrderModelESO;
import org.edec.order.model.dao.SearchGroupModelESO;
import org.edec.order.model.dao.SearchStudentModelESO;
import org.edec.order.model.dao.SectionModelEso;
import org.edec.studentPassport.model.dao.RatingEsoModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class EntityManagerOrderESO extends DAO {
    public OrderModel getOrderById(Long idOrder) {
        List<OrderModel> orders = getOrderByFilter(null, 3, null, null, "", idOrder);
        return orders.size() == 0 ? null : orders.get(0);
    }

    public List<OrderModel> getOrderByFilter(Long idInst, int formofstudy, Long idStatus, Long idType, String fioStudent, Long idOrder) {
        String query =
                "SELECT OH.dateofbegin AS datecreated, OH.dateofend AS datesign, OH.descriptionspec AS description, OH.id_order_rule as idOrderRule,\n" +
                        "\tOH.ordernumber AS number, OST.name AS status, ORR.name AS type, ORR.id_order_type AS orderType, OH.order_url AS url,\n" +
                        //"\tCAST(REGEXP_REPLACE(OH.ordernumber, '(/С)|(/C)', '') AS INTEGER) AS sort_num,\n" +
                        "\t(SELECT family||' '||SUBSTRING(name, 1, 1)||'. '||SUBSTRING(patronymic, 1, 1)||'.' FROM humanface HF WHERE id_humanface = OH.current_hum) AS currenthumanface,\n" +
                        "\tCASE WHEN SEM.season = 0 THEN 'осеннего' ELSE 'весеннего' END AS semesterSeason,\n" +
                        "\tOH.id_order_head AS idOrder, (select count(*) from link_order_student_status inner join link_order_section using(id_link_order_section) where id_order_head = OH.id_order_head) as countStudents,\n" +
                        "\tOH.lotus_id AS idLotus, OH.semester AS idSemester, OH.dateoffinish AS datefinish, OH.operation\n" +
                        "FROM order_head OH\n" + "\tINNER JOIN order_status_type OST USING (id_order_status_type)\n" +
                        "\tINNER JOIN order_rule ORR USING (id_order_rule)\n" + "\tINNER JOIN semester SEM ON OH.semester = SEM.id_semester\n" +
                        "\tLEFT JOIN link_order_section OS USING (id_order_head)\n" +
                        "\tLEFT JOIN link_order_student_status LOSS USING (id_link_order_section)\n" +
                        "\tLEFT JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                        "\tLEFT JOIN studentcard SC USING (id_studentcard)\n" + "\tLEFT JOIN humanface HF ON SC.id_humanface = HF.id_humanface\n" +
                        "WHERE CAST(SEM.id_institute AS TEXT) ILIKE :idInst\n" + "\tAND CAST(SEM.formofstudy AS TEXT) ILIKE :formOfStudy\n" +
                        "\tAND CAST(ORR.id_order_type AS TEXT) ILIKE :idType\n" + "\tAND CAST(OH.id_order_status_type AS TEXT) ILIKE :idStatus\n" +
                        "\tAND CAST(OH.id_order_head AS TEXT) ILIKE :idOrder\n" +
                        (fioStudent.equals("") ? "" : "\tAND (HF.family||' '||HF.patronymic||' '||HF.name ILIKE :fio)\n") +
                        "GROUP BY OH.id_order_head, OST.name, ORR.name, ORR.id_order_type, SEM.id_semester\n" +
                        "ORDER BY OH.semester DESC, datecreated desc, OH.id_order_status_type = 1 DESC, OH.id_order_status_type = 2 DESC, " +
                        "OH.id_order_status_type = 4 DESC, OH.id_order_status_type = 3 DESC\n";

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
                .addScalar("datefinish")
                .addScalar("operation")
                .setResultTransformer(Transformers.aliasToBean(OrderModel.class));
        q.setParameter("idInst", idInst == null ? "%%" : String.valueOf(idInst), StringType.INSTANCE)
                .setParameter("formOfStudy", formofstudy == 3 ? "%%" : String.valueOf(formofstudy), StringType.INSTANCE)
                .setParameter("idType", idType == null ? "%%" : String.valueOf(idType), StringType.INSTANCE)
                .setParameter("idStatus", idStatus == null ? "%%" : String.valueOf(idStatus), StringType.INSTANCE)
                .setParameter("idOrder", idOrder == null ? "%%" : String.valueOf(idOrder), StringType.INSTANCE);

        if (!fioStudent.equals("")) {
            q.setParameter("fio", "%" + fioStudent + "%", StringType.INSTANCE);
        }
        return (List<OrderModel>) getList(q);
    }

    public List<OrderTypeModel> getDistinctOrderType() {
        String query = "SELECT \n" + "\tDISTINCT id_order_type AS idType, OT.name\n" + "FROM\n" + "\torder_head\n" +
                "\tINNER JOIN order_rule USING (id_order_rule)\n" + "\tINNER JOIN order_type OT USING (id_order_type)";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idType", LongType.INSTANCE)
                .addScalar("name")
                .setResultTransformer(Transformers.aliasToBean(OrderTypeModel.class));
        return (List<OrderTypeModel>) getList(q);
    }

    public List<OrderStatusModel> getAllStatus() {
        String query = "SELECT id_order_status_type AS idStatus, name FROM order_status_type";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idStatus", LongType.INSTANCE)
                .addScalar("name")
                .setResultTransformer(Transformers.aliasToBean(OrderStatusModel.class));
        return (List<OrderStatusModel>) getList(q);
    }

    public List<OrderModelESO> getListOrderModelESO(long idOrder) {
        String query = "SELECT\n" +
                       "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio,\n" + "\tSC.recordbook AS recordbook,\n" +
                "\tDG.groupname AS groupname,\n" +
                "\tOS.name AS sectionname,\n" +
                "\tLOS.foundation AS foundationLos,\n" +
                "\tOS.foundation AS foundation,\n" +
                "\tLOS.first_date AS firstDateSection,\n" +
                "\tLOS.second_date AS secondDateSection,\n" +
                "\tLOSS.first_date AS firstDate,\n" +
                "\tLOSS.second_date AS secondDate,\n" +
                "\tLOSS.third_date AS thirdDate,\n" +
                "\tLOS.id_link_order_section AS idSection,\n" +
                "\tLOSS.id_link_order_student_status AS idStudent,\n" +
                "\tLOSS.additional AS additionalInfo,\n" +
                "\tSSS.is_government_financed as isGovernmentFinanced\n" +
                "FROM \n" + "\torder_head OH\n" +
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
                .addScalar("additionalInfo", StringType.INSTANCE)
                .addScalar("isGovernmentFinanced", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderModelESO.class));
        q.setLong("idOrder", idOrder);
        return (List<OrderModelESO>) getList(q);
    }

    public List<EmployeeOrderModel> getEmployeeForEnsemble(Long idOrder) {
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

    public EmployeeOrderModel getApprovalModel(Long idOrder) {
        String query = "SELECT HF.family||' '||HF.name||' '||HF.patronymic AS fio,\n" +
                "  HF.id_humanface AS idHum, LRE.sign, LRE.actionrule, HF.email\n" +
                "FROM humanface HF\n" +
                "  INNER JOIN employee EMP USING (id_humanface)\n" +
                "  INNER JOIN link_rule_employee LRE USING (id_employee)\n" +
                "  INNER JOIN order_rule ORR ON LRE.id_rule = ORR.id_order_rule\n" +
                "  INNER JOIN order_head OH USING (id_order_rule)\n" +
                "  INNER JOIN semester SEM ON OH.semester = SEM.id_semester\n" +
                "WHERE OH.id_order_head = :idOrder AND LRE.actionrule = 1";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio").addScalar("idHum", LongType.INSTANCE)
                .addScalar("sign").addScalar("actionrule").addScalar("email")
                .setResultTransformer(Transformers.aliasToBean(EmployeeOrderModel.class));
        q.setParameter("idOrder", idOrder);

        return (EmployeeOrderModel) getList(q).get(0);
    }

    public void removeStudentFromOrder(Long idLoss) {
        String query = "DELETE FROM link_order_student_status WHERE id_link_order_student_status = :idLoss";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idLoss", idLoss);
        executeUpdate(q);
    }

    public List<RatingEsoModel> getMarksForStudentInOrder(Long idLoss) {
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

    public List<SearchGroupModelESO> getGroupsBySemester(long idSemester) {
        Long prevSemester = new CreateOrderManagerESO().getPrevSemester(idSemester);

        String query = "SELECT dg.groupname AS name, dg.id_dic_group AS id FROM link_group_semester lgs\n" +
                "INNER JOIN dic_group dg USING(id_dic_group)\n" + "WHERE lgs.id_semester = :idSemester";

        Query q = getSession().createSQLQuery(query)
                .addScalar("name", StringType.INSTANCE)
                .addScalar("id", LongType.INSTANCE)
                .setParameter("idSemester", prevSemester)
                .setResultTransformer(Transformers.aliasToBean(SearchGroupModelESO.class));
        return (List<SearchGroupModelESO>) getList(q);
    }

    public List<SearchStudentModelESO> getStudentsByFilterNotInOrder(String family, String name, String patronymic, String groupname,
                                                                     long idSemester, long idOrder) {
        //long sem = new CreateOrderManagerESO().getPrevSemester(idSemester);

        String query = "SELECT \n" + "\thf.name, \n" + "    hf.family AS surname,\n" + "    hf.patronymic,\n" + "    dg.groupname,\n" +
                "    sss.id_student_semester_status AS idSSS\n" + "FROM student_semester_status sss\n" +
                "INNER JOIN studentcard sc USING(id_studentcard)\n" + "INNER JOIN humanface hf USING(id_humanface)\n" +
                "INNER JOIN link_group_semester lgs USING(id_link_group_semester)\n" +
                "INNER JOIN dic_group dg USING(id_dic_group)\n" + "WHERE \n" + "\tlgs.id_semester = :idSemester\n" +
                "    AND hf.name ILIKE :name\n" + "    AND hf.family ILIKE :family \n" +
                "    AND hf.patronymic ILIKE :patronymic\n" + "    AND dg.groupname ILIKE :groupname\n" +
                "    AND sss.id_student_semester_status NOT IN (\n" +
                "    SELECT id_student_semester_status FROM link_order_student_status\n" +
                "    INNER JOIN link_order_section los USING(id_link_order_section)\n" + "    WHERE id_order_head = :idOrder)";

        Query q = getSession().createSQLQuery(query)
                .addScalar("name", StringType.INSTANCE)
                .addScalar("surname", StringType.INSTANCE)
                .addScalar("patronymic", StringType.INSTANCE)
                .addScalar("groupname", StringType.INSTANCE)
                .addScalar("idSSS", LongType.INSTANCE)
                .setParameter("family", "%" + family + "%", StringType.INSTANCE)
                .setParameter("name", "%" + name + "%", StringType.INSTANCE)
                .setParameter("patronymic", "%" + patronymic + "%", StringType.INSTANCE)
                .setParameter("groupname", "%" + groupname + "%", StringType.INSTANCE)
                .setParameter("idSemester", idSemester, LongType.INSTANCE)
                .setParameter("idOrder", idOrder, LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(SearchStudentModelESO.class));

        return (List<SearchStudentModelESO>) getList(q);
    }

    public List<SearchStudentModel> getStudentsByFilter(String family, long idSemester) {
        String query = "SELECT \n" + "\thf.name, \n" + "    hf.family AS surname,\n" + "    hf.patronymic,\n" + "    dg.groupname,\n" +
                "    sss.is_government_financed AS governmentFinanced,\n" + "    sss.id_student_semester_status AS id\n" +
                "FROM student_semester_status sss\n" + "INNER JOIN studentcard sc USING(id_studentcard)\n" +
                "INNER JOIN humanface hf USING(id_humanface)\n" +
                "INNER JOIN link_group_semester lgs USING(id_link_group_semester)\n" +
                "INNER JOIN dic_group dg USING(id_dic_group)\n" + "WHERE \n" +
                "\tis_deducted = 0 AND is_academicleave = 0 AND lgs.id_semester = :idSemester\n" +
                "    AND hf.family ILIKE :family \n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("name", StringType.INSTANCE)
                .addScalar("surname", StringType.INSTANCE)
                .addScalar("patronymic", StringType.INSTANCE)
                .addScalar("groupname", StringType.INSTANCE)
                .addScalar("governmentFinanced", BooleanType.INSTANCE)
                .addScalar("id", LongType.INSTANCE)
                .setParameter("family", "%" + family + "%", StringType.INSTANCE)
                .setParameter("idSemester", idSemester, LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(SearchStudentModel.class));
        return (List<SearchStudentModel>) getList(q);
    }

    public List<SectionModelEso> getSectionsByIdOrder(long idOrder) {
        String query =
                "SELECT \n" + "    OS.name, \n" + "    LOS.id_link_order_section AS idLOS,\n" + "    LOS.first_date AS firstDate,\n" +
                        "    LOS.second_date AS secondDate\n" + "FROM link_order_section los\n" +
                        "INNER JOIN order_section OS USING(id_order_section)\n" + "WHERE \n" + "\tid_order_head = :idOrder";

        Query q = getSession().createSQLQuery(query)
                .addScalar("name", StringType.INSTANCE)
                .addScalar("idLOS", LongType.INSTANCE)
                .addScalar("firstDate", DateType.INSTANCE)
                .addScalar("secondDate", DateType.INSTANCE)
                .setParameter("idOrder", idOrder)
                .setResultTransformer(Transformers.aliasToBean(SectionModelEso.class));
        return (List<SectionModelEso>) getList(q);
    }

    public void insertLinkOrderStudentStatus(SectionModel sectionModel, List<StudentToAddModel> list) {
        String query = "INSERT INTO link_order_student_status \n" +
                "(id_student_semester_status, id_link_order_section, first_date, second_date, third_date, groupname)\n" +
                "VALUES \n" + "(:idSSS, :idLOS, :firstDate, :secondDate, :thirdDate, (SELECT dg.groupname FROM dic_group dg\n" +
                "INNER JOIN link_group_semester lgs USING(id_dic_group)\n" + "INNER JOIN semester sem USING(id_semester)\n" +
                "INNER JOIN student_semester_status sss USING(id_link_group_semester)\n" +
                "INNER JOIN studentcard sc USING(id_studentcard)\n" +
                "WHERE sem.is_current_sem = 1 AND id_studentcard IN (SELECT id_studentcard FROM student_semester_status WHERE id_student_semester_status = :idSSS) LIMIT 1 ))";

        for (StudentToAddModel item : list) {
            Query q = getSession().createSQLQuery(query)
                    .setParameter("idSSS", item.getId(), LongType.INSTANCE)
                    .setParameter("idLOS", sectionModel.getId(), LongType.INSTANCE)
                    .setParameter("firstDate", item.getFirstDate(), DateType.INSTANCE)
                    .setParameter("secondDate", item.getSecondDate(), DateType.INSTANCE)
                    .setParameter("thirdDate", null, DateType.INSTANCE);
            executeUpdate(q);
        }
    }

    public void updateOrderDesc(String desc, long idOrder) {
        String query = "update order_head set descriptionspec = '" + desc + "' where id_order_head = " + idOrder;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public void saveFoundationStudent(Long id, String foundation) {
        String query = "update link_order_student_status set additional = '" + foundation + "' where id_link_order_student_status = " + id;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public void saveFoundation(Long id, String foundation) {
        String query = "update link_order_section set foundation = '" + foundation + "' where id_link_order_section = " + id;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public void deleteOrderWithId(long idOrder) throws Exception {
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

    public void updateDateForLoss(Long idLoss, Date date, int numDate) throws Exception {
        String subQuery = "";
        switch (numDate) {
            case 1:
                subQuery = " first_date = '" + new SimpleDateFormat("yyyy-MM-dd").format(date) + "'";
                break;
            case 2:
                subQuery = " second_date = '" + new SimpleDateFormat("yyyy-MM-dd").format(date) + "'";
                break;
            case 3:
                subQuery = " third_date = '" + new SimpleDateFormat("yyyy-MM-dd").format(date) + "'";
                break;
        }
        String query = "update link_order_student_status set " + subQuery + " where id_link_order_student_status = " + idLoss;

        executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean updateSessionresult(long idSemester) {
        try {
            begin();

            String query = "select updatesessionresult(id_student_semester_status) from student_semester_status\n" +
                    "inner join link_group_semester using(id_link_group_semester)\n" + "where id_semester = " + idSemester;

            getSession().createSQLQuery(query).list();

            query = "select updatedatecompletesession(id_student_semester_status) from student_semester_status\n" +
                    "inner join link_group_semester using(id_link_group_semester)\n" + "where id_semester = " + idSemester;

            getSession().createSQLQuery(query).list();

            commit();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            close();
        }
    }

    public List<Long> getOrdersByStudentFio(String fio) {
        String query = "SELECT los.id_order_head FROM order_head\n" +
                "INNER JOIN link_order_section los ON order_head.id_order_head = los.id_order_head\n" +
                "INNER JOIN link_order_student_status status ON los.id_link_order_section = status.id_link_order_section\n" +
                "INNER JOIN student_semester_status sss ON status.id_student_semester_status = sss.id_student_semester_status\n" +
                "INNER JOIN studentcard s2 ON sss.id_studentcard = s2.id_studentcard\n" +
                "INNER JOIN humanface h2 ON s2.id_humanface = h2.id_humanface\n" +
                "WHERE UPPER(h2.family||' '||h2.name||' '||h2.patronymic) LIKE UPPER('%" + fio + "%')";

        Query q = getSession().createSQLQuery(query);

        return (List<Long>) getList(q);
    }
}
