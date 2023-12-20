package org.edec.newOrder.manager;

import org.edec.dao.DAO;
import org.edec.newOrder.model.ScholarshipModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateRuleModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.createOrder.OrderCreateTypeModel;
import org.edec.order.model.StudentToAddModel;
import org.edec.newOrder.model.createOrder.StudentWithReference;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.newOrder.model.createOrder.OrderSectionModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.LongAccumulator;

/**
 * Created by antonskripacev on 08.01.17.
 */
public class CreateOrderManagerESO extends DAO {

    public List<OrderCreateTypeModel> getListOrderType() {
        String query = "SELECT id_order_type as id, name from order_type order by position";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("name")
                .setResultTransformer(Transformers.aliasToBean(OrderCreateTypeModel.class));
        return (List<OrderCreateTypeModel>) getList(q);
    }

    public List<OrderCreateRuleModel> getListOrderRule(Long idInst, Long type) {
        String query = "SELECT\n" +
                "\t id_order_rule AS id,\n" +
                "\t name AS name,\n" +
                "\t is_automatic AS isAutomatic,\n" +
                "\t id_order_type AS idOrderType\n" +
                "FROM order_rule " +
                "WHERE is_displayed = TRUE " +
                "AND id_institute = :idInst " +
                "AND id_order_type = :idOrderType";

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("idOrderType", LongType.INSTANCE)
                .addScalar("name", StringType.INSTANCE)
                .addScalar("isAutomatic", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateRuleModel.class));

        q.setLong("idInst", idInst);
        q.setLong("idOrderType", type);

        return (List<OrderCreateRuleModel>) getList(q);
    }

    public Long createEmptyOrder(Long idOrderRule, Date dateOfBegin, Long idSemester, Long idHumanface, String description) {
        String strDate = new SimpleDateFormat("yyyy.MM.dd").format(dateOfBegin);
        String query =
                "insert into order_head (id_order_rule, id_order_status_type, dateofbegin, semester, id_humanface, descriptionspec )\n" +
                        "values  \t        (" + idOrderRule + ", 1, '" + strDate + "', " + idSemester + ", " + idHumanface + ", '" + description +
                        "')\n" + "returning id_order_head";

        Query q = getSession().createSQLQuery(query);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long createEmptyOrder(Long idOrderRule, Date dateOfBegin, Long idSemester, Long idHumanface, String description, ScholarshipModel scholarshipInfo) {
        String strDate = new SimpleDateFormat("yyyy.MM.dd").format(dateOfBegin);
        String query =
                "insert into order_head (id_order_rule, id_order_status_type, dateofbegin, semester, id_humanface, descriptionspec, settings)\n" +
                        "values  \t        (" + idOrderRule + ", 1, '" + strDate + "', " + idSemester + ", " + idHumanface + ", '" + description + "', " +
                        "'{ \"type_scholarship\" : \"" + scholarshipInfo.getName() + "\" , \"page_header\" : \"" + scholarshipInfo.getText() + "\"}" +
                        "')\n" +
                        "returning id_order_head";

        Query q = getSession().createSQLQuery(query);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long createLinkOrderSection(Long idOrderHead, Long idOrderSection, String foundation, Date firstDate, Date secondDate) {
        String strFirstDate = (firstDate == null ? "" : new SimpleDateFormat("yyyy.MM.dd").format(firstDate));
        String strSecondDate = (secondDate == null ? "" : new SimpleDateFormat("yyyy.MM.dd").format(secondDate));
        String query = "insert into link_order_section (id_order_head, id_order_section, first_date, second_date, foundation)\n" +
                "values  \t        (" + idOrderHead + ", " + idOrderSection + ", " + "" +
                (strFirstDate.equals("") ? "null" : ("'" + strFirstDate + "'")) + ", " + "" +
                (strSecondDate.equals("") ? "null" : ("'" + strSecondDate + "'")) + ", " + "'" + foundation +
                "') returning id_link_order_section";
        Query q = getSession().createSQLQuery(query);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long createLinkOrderStudentStatus(Long idLinkOrderSection, Long idSSS, Date firstDate, Date secondDate, String groupname, Integer curSemester,
                                             String additional) {
        String strFirstDate = (firstDate == null ? "" : new SimpleDateFormat("yyyy.MM.dd").format(firstDate));
        String strSecondDate = (secondDate == null ? "" : new SimpleDateFormat("yyyy.MM.dd").format(secondDate));
        String query =
                "insert into link_order_student_status (id_link_order_section, id_student_semester_status, first_date, second_date, groupname,  additional, semester_number)\n" +
                        "values  \t        " + "(" + idLinkOrderSection + ", " + "" + idSSS + ", " + "" +
                        (strFirstDate.equals("") ? "null" : ("'" + strFirstDate + "'")) + ", " + "" +
                        (strSecondDate.equals("") ? "null" : ("'" + strSecondDate + "'")) + ", " + "'" + groupname + "', " + "'" + additional +
                        "', " + curSemester + ") returning id_link_order_student_status";
        Query q = getSession().createSQLQuery(query);

        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public List<OrderCreateOrderSectionModel> getListOrderSection(Long idRule) {
        String query = "select\n" + "\t id_order_section as id,\n" + "\t name as name,\n" + "\t foundation as foundation\n" +
                "from order_section where id_order_rule = " + idRule;

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("name", StringType.INSTANCE)
                .addScalar("foundation", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateOrderSectionModel.class));
        return (List<OrderCreateOrderSectionModel>) getList(q);
    }

    public boolean setUrlForOrder(Long idOrder, String url) {
        String query = "UPDATE order_head SET order_url = :orderUrl WHERE id_order_head = :idOrder";
        Query q = getSession().createSQLQuery(query);
        q.setString("orderUrl", url).setLong("idOrder", idOrder);
        return executeUpdate(q);
    }

    public Long getCurrentSemester(long idInstitute, int formOfStudy) {
        String query =
                "Select id_semester as idSemester from semester where id_institute = " + idInstitute + " and formofstudy = " + formOfStudy +
                        " and is_current_sem = 1 limit 1";
        Query q = getSession().createSQLQuery(query);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long getPrevSemester(long idSemester) {
        String query =
                "select id_semester from semester where " + " id_institute = (select id_institute from semester where id_semester = " +
                        idSemester + ")" + " and formofstudy = (select formofstudy from semester where id_semester = " + idSemester + ")" +
                        " and id_semester < " + idSemester + " order by id_semester desc limit 1";

        Query q = getSession().createSQLQuery(query);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long getNextSemester(long idSemester) {
        String query =
                "select id_semester from semester where " + " id_institute = (select id_institute from semester where id_semester = " +
                        idSemester + ")" + " and formofstudy = (select formofstudy from semester where id_semester = " + idSemester + ")" +
                        " and id_semester > " + idSemester + " order by id_semester limit 1";

        Query q = getSession().createSQLQuery(query);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Integer getSessionResult(long idSSS) {
        String query =
                "SELECT sessionresult\n" +
                        "FROM student_semester_status\n" +
                        "WHERE student_semester_status.id_student_semester_status = " + idSSS;

        Query q = getSession().createSQLQuery(query);

        return (Integer) getList(q).get(0);
    }

    public Date getDateOfEndSemester(Long idSem, String groupName) {
        String query =
                "SELECT dateofendsemester\n" +
                        "FROM link_group_semester\n" +
                        "WHERE  (id_semester, id_dic_group) IN ((" + idSem + ", (SELECT id_dic_group FROM dic_group WHERE groupname='" + groupName + "')))";

        Query q = getSession().createSQLQuery(query)
                .addScalar("dateofendsemester");


        return (Date) getList(q).get(0);
    }

    public Date getDateOfEndSession(Long idSem, String groupName) {
        String query =
                "SELECT dateofendsession\n" +
                        "FROM link_group_semester\n" +
                        "WHERE  (id_semester, id_dic_group) IN ((" + idSem + ", (SELECT id_dic_group FROM dic_group WHERE groupname='" + groupName + "')))";

        Query q = getSession().createSQLQuery(query)
                .addScalar("dateofendsession");

        return (Date) getList(q).get(0);
    }

    public void updateOrderDesc(String desc, long idOrder) {
        String query = "update order_head set descriptionspec = '" + desc + "' where id_order_head = " + idOrder;
        Query q = getSession().createSQLQuery(query);
        executeUpdate(q);
    }

    public List<OrderCreateStudentModel> getStudentsForSearch(Long idSemester, Long idNextSemester, String fio) {

        //language=PostgreSQL
        String query = "SELECT id_student_semester_status                                                                       AS id,\n" +
                "       groupname,\n" +
                "       family || ' ' || name || case when patronymic like '' then patronymic else ' ' || patronymic END AS fio,\n" +
                "       SSS.is_government_financed                                                                       AS governmentFinanced,\n" +
                "       LGS.dateofendsession dateOfEndSession, \n" +
                (idNextSemester != null ? "(select LGS2.dateofendsession from link_group_semester LGS2\n" +
                        "           where LGS2.id_dic_group = DG.id_dic_group AND LGS2.id_semester = " + idNextSemester + ") " : "NULL ") + " as dateNextEndOfSession," +
                "       LGS.course as course,\n" +
                "       C.qualification as qualification,\n" +
                "       sss.is_deducted as deductedCurSem\n" +
                "FROM student_semester_status SSS\n" +
                "         INNER JOIN studentcard SC using (id_studentcard)\n" +
                "         INNER JOIN humanface HF using (id_humanface)\n" +
                "         INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "         INNER JOIN dic_group DG using (id_dic_group)\n" +
                "         INNER JOIN curriculum c on DG.id_curriculum = c.id_curriculum\n" +
                "WHERE id_semester = " + idSemester + "\n" +
                "  AND family || ' ' || name || ' ' || patronymic ILIKE '%" + fio + "%'\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("groupname")
                .addScalar("governmentFinanced", BooleanType.INSTANCE)
                .addScalar("fio")
                .addScalar("dateOfEndSession")
                .addScalar("course")
                .addScalar("qualification")
                .addScalar("dateNextEndOfSession")
                .addScalar("deductedCurSem", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    // TODO пока хардкод для очной формы обучения и ИКИТа
    public List<String> getListGroupsForCurSemester() {
        return (List<String>) getSession()
                .createSQLQuery("select groupname from \"eDecanat\".public.dic_group\n" +
                        "  inner join link_group_semester semester on dic_group.id_dic_group = semester.id_dic_group\n" +
                        "  inner join semester s2 on semester.id_semester = s2.id_semester\n" +
                        "where formofstudy = 1 and s2.id_institute = 1 and s2.is_current_sem = 1")
                .list();
    }

    public List<OrderSectionModel> getSectionName(long idOrderRule) {
        String query = "select os.id_order_section as idOrderSection, os.name as sectionName\n" +
                "from\n" +
                "order_rule orr\n" +
                "inner join order_section os using (id_order_rule)\n" +
                "where id_order_rule = " + idOrderRule;

        Query q = getSession().createSQLQuery(query)
                .addScalar("idOrderSection", LongType.INSTANCE)
                .addScalar("sectionName")
                .setResultTransformer(Transformers.aliasToBean(OrderSectionModel.class));

        return (List<OrderSectionModel>) getList(q);
    }

    public List<StudentWithReference> getStudentsWithReferenceFromSocialOrder(Long idOrder) {
        String query =
                "SELECT\n" + "\tSC.id_studentcard AS idStudentcard,\n" + "\tMAX(RF.id_reference) AS idReference, MAX(RF.url) AS url,\n" +
                        "\tname, family, patronymic\n" + "from \n" + "\tstudent_semester_status sss\n" +
                        "\tinner join studentcard sc using(id_studentcard)\n" + "\tINNER JOIN reference RF USING (id_studentcard)\n" +
                        "\tinner join humanface hf using(id_humanface)\n" +
                        "\tinner join link_order_student_status lgss using(id_student_semester_status)\n" +
                        "\tinner join link_order_section los using(id_link_order_section)\n" + "\tinner join order_head oh using(id_order_head)\n" +
                        "where \n" + "\tid_order_head = " + idOrder + " and id_order_section in (57,58)\n" + "GROUP BY\n" +
                        "\tSC.id_studentcard, HF.id_humanface\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idStudentcard", LongType.INSTANCE)
                .addScalar("idReference", LongType.INSTANCE)
                .addScalar("name")
                .addScalar("family")
                .addScalar("patronymic")
                .addScalar("url")
                .setResultTransformer(Transformers.aliasToBean(StudentWithReference.class));
        return (List<StudentWithReference>) getList(q);
    }

    public List<StudentWithReference> getStudentsWithReferenceFromSocialOrder(Long idOrder, List<StudentToAddModel> listToAdd) {
        String query =
                "SELECT\n" + "\tSC.id_studentcard AS idStudentcard,\n" + "\tMAX(RF.id_reference) AS idReference, MAX(RF.url) AS url,\n" +
                        "\tname, family, patronymic\n" + "from \n" + "\tstudent_semester_status sss\n" +
                        "\tinner join studentcard sc using(id_studentcard)\n" + "\tINNER JOIN reference RF USING (id_studentcard)\n" +
                        "\tinner join humanface hf using(id_humanface)\n" +
                        "\tinner join link_order_student_status lgss using(id_student_semester_status)\n" +
                        "\tinner join link_order_section los using(id_link_order_section)\n" + "\tinner join order_head oh using(id_order_head)\n" +
                        "where \n" + "\tid_order_head = " + idOrder + " and id_order_section in (57,58)\n" +
                        "\tand id_student_semester_status in (" + getIdStrByList(listToAdd) + ")\n" + "GROUP BY\n" +
                        "\tSC.id_studentcard, HF.id_humanface\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idStudentcard", LongType.INSTANCE)
                .addScalar("idReference", LongType.INSTANCE)
                .addScalar("name")
                .addScalar("family")
                .addScalar("patronymic")
                .addScalar("url")
                .setResultTransformer(Transformers.aliasToBean(StudentWithReference.class));

        return (List<StudentWithReference>) getList(q);
    }

    // TODO REFACTOR
    private String getIdStrByList(List<StudentToAddModel> listToAdd) {
        if (listToAdd.size() == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < listToAdd.size(); i++) {
            result.append(listToAdd.get(i).getId());

            if (i != listToAdd.size() - 1) {
                result.append(", ");
            }
        }

        return result.toString();
    }

    public List<StudentWithReference> getStudentsWithReferenceFromSocialIncreasedOrder(Long idOrder) {
        String query =
                "SELECT\n" + "\tSC.id_studentcard AS idStudentcard,\n" + "\tMAX(RF.id_reference) AS idReference, MAX(RF.url) AS url,\n" +
                        "\tname, family, patronymic\n" + "from \n" + "\tstudent_semester_status sss\n" +
                        "\tinner join studentcard sc using(id_studentcard)\n" + "\tINNER JOIN reference RF USING (id_studentcard)\n" +
                        "\tinner join humanface hf using(id_humanface)\n" +
                        "\tinner join link_order_student_status lgss using(id_student_semester_status)\n" +
                        "\tinner join link_order_section los using(id_link_order_section)\n" + "\tinner join order_head oh using(id_order_head)\n" +
                        "where \n" + "\tid_order_head = " + idOrder + " and id_order_section in (59,61)\n" + "GROUP BY\n" +
                        "\tSC.id_studentcard, HF.id_humanface\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idStudentcard", LongType.INSTANCE)
                .addScalar("idReference", LongType.INSTANCE)
                .addScalar("name", StringType.INSTANCE)
                .addScalar("family", StringType.INSTANCE)
                .addScalar("patronymic", StringType.INSTANCE)
                .addScalar("url")
                .setResultTransformer(Transformers.aliasToBean(StudentWithReference.class));
        return (List<StudentWithReference>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentsForFilterDeductionByComission(Long idOrderHead) {
        String query = "select distinct hf.family || ' ' || hf.name || ' ' || hf.patronymic as fio, loss.groupname, " +
                "sss.is_government_financed as governmentFinanced, hf.foreigner as foreigner, loss.id_link_order_student_status as id\n" +
                "from\n" +
                "humanface hf \n" +
                "join studentcard sc using (id_humanface)\n" +
                "join student_semester_status sss using (id_studentcard)\n" +
                "join link_order_student_status loss using (id_student_semester_status)\n" +
                "join link_order_section los using (id_link_order_section)\n" +
                "join order_section os using (id_order_section)\n" +
                "join order_rule o on os.id_order_rule = os.id_order_rule\n" +
                "join order_head oh on o.id_order_rule = oh.id_order_rule\n" +
                "where los.id_order_head = " + idOrderHead + " order by fio";

        Query q = getSession().createSQLQuery(query)
                .addScalar("groupname")
                .addScalar("fio")
                .addScalar("governmentFinanced", BooleanType.INSTANCE)
                .addScalar("foreigner", BooleanType.INSTANCE)
                .addScalar("id", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public boolean deleteStudentByFilter(Long idLoss) {
        String query = "delete from link_order_student_status \n" +
                "where id_link_order_student_status =" + idLoss;
        Query q = getSession().createSQLQuery(query);
        return executeUpdate(q);
    }

    public boolean updateOrderAttr(Long idOrder, List<String> attrList) {
        String attributes = "'{";
        for (int i = 0; i < attrList.size(); i++) {
            attributes += "{attr" + ((Integer) (i + 1)).toString() + "," + attrList.get(i) + "}";
            if (i != attrList.size() - 1) {
                attributes += ",";
            }
        }
        attributes += "}'";
        String query = "UPDATE order_head\n" +
                "SET attr1 = json_object(" + attributes + ")\n" +
                "WHERE id_order_head = " + idOrder;
        Query q = getSession().createSQLQuery(query);
        return executeUpdate(q);
    }

    public String getSchoolYearBySemesterId(Long idSemester) {
        String query = "select \n" +
                "extract(year from dateofbegin) || '/' ||  extract(year from dateofend)\n" +
                "from schoolyear \n" +
                "join semester using (id_schoolyear)\n" +
                "where id_semester = :idSemester\n" +
                "limit 1";
        return getUnique(getSession().createSQLQuery(query).setParameter("idSemester", idSemester));
    }
}
