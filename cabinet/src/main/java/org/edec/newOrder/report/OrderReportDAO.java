package org.edec.newOrder.report;

import org.edec.dao.DAO;
import org.edec.newOrder.model.report.OrderReportMainModel;
import org.edec.newOrder.model.dao.EmployeeOrderEsoModel;
import org.edec.newOrder.model.report.*;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import java.util.List;

public class OrderReportDAO extends DAO {
    public OrderReportMainModel getMainOrderInfoByID(Long idOrderHead) {
        //language=PostgreSQL
        String query = "SELECT\n" +
                "    INST.fulltitle AS institute, " +
                "    OH.dateofend AS datesign, " +
                "    OH.ordernumber,\n" +
                "    ORR.head_description AS typeorder, " +
                "    ORR.description AS descriptiontitle,\n" +
                "    ORR.description2 AS descriptiontitle2," +
                "    ORR.id_order_rule as idOrderRule,\n" +
                "    CASE\n" +
                "        WHEN CUR.formofstudy = 1 THEN 'очная форма обучения'\n" +
                "        WHEN CUR.formofstudy = 2 AND DG.groupname ilike '%ЗКИ%' THEN 'заочная форма обучения'\n" +
                "        WHEN SEM.formofstudy = 2 AND DG.groupname ilike '%ВКИ%' THEN 'очно-заочная форма обучения'\n" +
                "        END AS formofstudy, " +
                "    SEM.formofstudy as formOfStudyId, " +
                "    OH.certnumber, " +
                "    OH.certfio\n" +
                "FROM order_head OH\n" +
                "        JOIN order_rule ORR ON OH.id_order_rule = ORR.id_order_rule\n" +
                "        JOIN semester SEM ON OH.semester = SEM.id_semester\n" +
                "        JOIN institute INST ON SEM.id_institute = INST.id_institute\n" +
                "        LEFT JOIN link_order_section los on OH.id_order_head = los.id_order_head\n" +
                "        LEFT JOIN link_order_student_status loss on los.id_link_order_section = loss.id_link_order_section\n" +
                "        LEFT JOIN student_semester_status sss on loss.id_student_semester_status = sss.id_student_semester_status\n" +
                "        LEFT JOIN link_group_semester lgs on sss.id_link_group_semester = lgs.id_link_group_semester\n" +
                "        LEFT JOIN dic_group dg on lgs.id_dic_group = dg.id_dic_group\n" +
                "        LEFT JOIN curriculum cur on dg.id_curriculum = cur.id_curriculum\n" +
                "WHERE OH.id_order_head = :idOrder";

        Query q = getSession().createSQLQuery(query)
                .addScalar("institute")
                .addScalar("datesign")
                .addScalar("ordernumber")
                .addScalar("descriptiontitle2")
                .addScalar("typeorder")
                .addScalar("descriptiontitle")
                .addScalar("formofstudy")
                .addScalar("formOfStudyId")
                .addScalar("certnumber")
                .addScalar("certfio")
                .addScalar("idOrderRule", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderReportMainModel.class));

        q.setLong("idOrder", idOrderHead);

        return (OrderReportMainModel) getList(q).get(0);
    }

    public List<EmployeeOrderEsoModel> getEmployeesOrder(Long idOrder) {
        String query = "SELECT\n" + "\tHF.family ||' '|| HF.name ||' '|| HF.patronymic AS fio,\n" +
                "\tLRE.post, LRE.actionrule, LRE.subquery, LRE.formofstudy\n" + "FROM\n" + "\thumanface HF\n" +
                "\tINNER JOIN employee EMP ON HF.id_humanface = EMP.id_humanface\n" +
                "\tINNER JOIN link_rule_employee LRE ON EMP.id_employee = LRE.id_employee \n" +
                "\tINNER JOIN order_rule ORR ON LRE.id_rule = ORR.id_order_rule\n" +
                "\tINNER JOIN order_head USING (id_order_rule)\n" + "WHERE\n" + "\tid_order_head = :idOrder\n" + "ORDER BY\n" +
                "\tLRE.actionrule, LRE.pos DESC";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("actionrule")
                .addScalar("post")
                .addScalar("subquery")
                .addScalar("formofstudy")
                .setResultTransformer(Transformers.aliasToBean(EmployeeOrderEsoModel.class));
        q.setLong("idOrder", idOrder);
        return (List<EmployeeOrderEsoModel>) getList(q);
    }

    public List<OrderReportModel> getAcademicModel(Long idOrder) {
        String query = "SELECT\n" + "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio, SC.recordbook, \n" +
                " COALESCE(LOSS.groupname, DG.groupname, '') AS groupname, \n" +
                "\tOS.name ILIKE '%Прод%' AS prolongation, OS.foundation,\n" +
                "\tREPLACE (REPLACE(OS.description, CAST('$date1$' AS TEXT), COALESCE(TO_CHAR(LOSS.first_date, 'dd.MM.yyyy'), '')), \n" +
                "\t\tCAST('$date2$' AS TEXT), COALESCE(TO_CHAR(LOSS.second_date, 'dd.MM.yyyy'),'')) AS description,\n" + "\tCASE\n" +
                "\t\tWHEN (OS.name LIKE 'Хорошо' OR OS.name LIKE 'Продление(Хорошо)') THEN 'на хорошо:'\n" +
                "\t\tWHEN (OS.name LIKE '\"Отлично\" и \"хорошо\"' OR OS.name LIKE 'Продление(Хорошо и Отлично)') THEN 'на хорошо и отлично:'\n" +
                "\t\tWHEN (OS.name LIKE 'Отлично' OR OS.name LIKE 'Продление(Отлично)') THEN 'на отлично:'\n" +
                "\tEND AS subDescription\n" + "FROM\n" + "\thumanface HF\n" + "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN link_order_student_status LOSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_order_section LOS USING (id_link_order_section)\n" +
                "\tINNER JOIN order_section OS USING (id_order_section)\n" + "WHERE\n" + "\tLOS.id_order_head = :idOrder\n" +
                "ORDER BY\n" + "\tgroupname DESC, LOS.id_order_section, OS.name, fio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("recordbook")
                .addScalar("groupname")
                .addScalar("prolongation")
                .addScalar("foundation")
                .addScalar("description")
                .addScalar("subDescription")
                .setResultTransformer(Transformers.aliasToBean(OrderReportModel.class));
        q.setLong("idOrder", idOrder);
        return (List<OrderReportModel>) getList(q);
    }

    public List<OrderReportModel> getAcademicFirstCourseModel(Long idOrder) {
        String query = "SELECT\n" + "  HF.family||' '||HF.name||' '||HF.patronymic AS fio, SC.recordbook,\n" +
                " COALESCE(LOSS.groupname, DG.groupname, '') AS groupname,\n" +
                "  OS.name ILIKE '%Прод%' AS prolongation, OS.foundation,\n" +
                "  REPLACE (REPLACE(OS.description, CAST('$date1$' AS TEXT), COALESCE(TO_CHAR(LOSS.first_date, 'dd.MM.yyyy'), '')),\n" +
                "           CAST('$date2$' AS TEXT), COALESCE(TO_CHAR(LOSS.second_date, 'dd.MM.yyyy'),'')) AS description,\n" +
                "  OH.settings->>'page_header' as pageHeader\n" + "FROM\n" + "  humanface HF\n" +
                "  INNER JOIN studentcard SC USING (id_humanface)\n" +
                "  INNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "  INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "  INNER JOIN dic_group DG USING (id_dic_group)\n" +
                "  INNER JOIN link_order_student_status LOSS USING (id_student_semester_status)\n" +
                "  INNER JOIN link_order_section LOS USING (id_link_order_section)\n" +
                "  INNER JOIN order_section OS USING (id_order_section)\n" + "  INNER JOIN order_head OH USING (id_order_head)\n" +
                "WHERE\n" + "  LOS.id_order_head = :idOrder\n" + "ORDER BY\n" + "  groupname DESC, fio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("recordbook")
                .addScalar("groupname")
                .addScalar("prolongation")
                .addScalar("foundation")
                .addScalar("description")
                .addScalar("pageHeader")
                .setResultTransformer(Transformers.aliasToBean(OrderReportModel.class));
        q.setLong("idOrder", idOrder);
        return (List<OrderReportModel>) getList(q);
    }

    public List<OrderReportModel> getAcademicIncreaseReportModel(Long idOrder) {
        String query = "SELECT\n" + "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio,\n" +
                " SC.recordbook, COALESCE(LOSS.groupname, DG.groupname, '') AS groupname, OS.foundation, LGS.course\n" +
                "\t,REPLACE(\n" + "\t\tREPLACE(OS.description, '$date1$', COALESCE(TO_CHAR(LOSS.first_date, 'dd.MM.yyyy'), '')),\n" +
                "\t\t'$date2$', COALESCE(TO_CHAR(LOSS.second_date, 'dd.MM.yyyy'), '')) AS description,\n" +
                "\tLOSS.additional AS additional\n" + "FROM\n" +
                "\tlink_order_section LOS\n" + "\tINNER JOIN order_section OS USING (id_order_section)\n" +
                "\tINNER JOIN link_order_student_status LOSS USING (id_link_order_section)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" + "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                "\tINNER JOIN humanface HF USING (id_humanface)\n" + "WHERE\n" + "\tLOS.id_order_head = :idOrder\n" + "ORDER BY\n" +
                "\tdescription, LOSS.first_date, LOSS.second_date, course, groupname, fio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("recordbook")
                .addScalar("groupname")
                .addScalar("foundation")
                .addScalar("course")
                .addScalar("description")
                .addScalar("additional")
                .setResultTransformer(Transformers.aliasToBean(OrderReportModel.class));
        q.setLong("idOrder", idOrder);
        return (List<OrderReportModel>) getList(q);
    }

    public List<OrderReportModel> getMaterialSupportModel(Long idOrder) {
        String query = "SELECT\n" +
                "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio,\n" +
                "\tSC.recordbook," +
                " COALESCE(LOSS.groupname, DG.groupname, '') AS groupname," +
                " OS.foundation," +
                " LGS.course\n \t," +
                " REPLACE(\n" +
                "\t\tREPLACE(OS.description, '$date1$', COALESCE(TO_CHAR(LOSS.first_date, 'dd.MM.yyyy'), '')),\n" +
                "\t\t'$date2$', COALESCE(TO_CHAR(LOSS.second_date, 'dd.MM.yyyy'), '')) AS description,\n" +
                "\tLOSS.additional AS additional," +
                " SC.other_dbuid AS idStudentMine\t\n" +
                "FROM link_order_section LOS\n" +
                "\tINNER JOIN order_section OS USING (id_order_section)\n" +
                "\tINNER JOIN link_order_student_status LOSS USING (id_link_order_section)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "WHERE\n" +
                "\tLOS.id_order_head = :idOrder\n" +
                "ORDER BY\n" +
                "\tdescription, LOSS.first_date, LOSS.second_date, course, groupname, fio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("recordbook")
                .addScalar("groupname")
                .addScalar("foundation")
                .addScalar("course")
                .addScalar("description")
                .addScalar("additional")
                .addScalar("idStudentMine", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderReportModel.class));
        q.setLong("idOrder", idOrder);
        return (List<OrderReportModel>) getList(q);
    }

    public List<OrderReportModel> getListIndividuals(Long idOrder) {

        //language=PostgreSQL
        String query = "SELECT\n" + "\tCASE\n" + "\t\tWHEN HF.foreigner = TRUE THEN '* '||HF.family||' '||HF.name||' '||HF.patronymic\n" +
                "\t ELSE HF.family||' '||HF.name||' '||HF.patronymic END AS fio, \n" +
                "\tSC.recordbook, DG.groupname, LGS.course,SSS.is_government_financed, hf.sex as sex,\n" + "\tCASE\n" +
                "\t\tWHEN CUR.formofstudy = 1 THEN 'очная форма обучения'\n" +
                "\t\tWHEN CUR.formofstudy = 2 AND DG.groupname ilike '%ЗКИ%' THEN 'заочная форма обучения'\n" +
                "\t\tWHEN SEM.formofstudy = 2 AND DG.groupname ilike '%ВКИ%' THEN 'очно-заочная форма обучения'\n" +
                "\tEND AS formofstudy, " + "\tCUR.specialitytitle, CUR.directioncode AS specialitycode, CUR.qualification,\n" +
                "\tDIR.title AS directiontitle, DIR.code AS directioncode, " +
                "\tLOSS.first_date AS date1, LOSS.second_date AS date2, COALESCE(LOS.foundation, OS.foundation, '') as foundation,\n" +
                "\tLOSS.additional as additional\n" + "FROM\n" + "\tlink_order_section LOS\n" +
                "\tINNER JOIN order_section OS ON LOS.id_order_section = OS.id_order_section\n" +
                "\tINNER JOIN link_order_student_status LOSS ON LOS.id_link_order_section = LOSS.id_link_order_section\n" +
                "\tINNER JOIN student_semester_status SSS ON LOSS.id_student_semester_status = SSS.id_student_semester_status\n" +
                "\tINNER JOIN studentcard SC ON SSS.id_studentcard = SC.id_studentcard\n" +
                "\tINNER JOIN humanface HF ON SC.id_humanface = HF.id_humanface\n" +
                "\tINNER JOIN link_group_semester LGS ON SSS.id_link_group_semester = LGS.id_link_group_semester\n" +
                "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "\tINNER JOIN dic_group DG ON LGS.id_dic_group = DG.id_dic_group\n" +
                "\tINNER JOIN curriculum CUR ON DG.id_curriculum = CUR.id_curriculum\n" +
                "\tLEFT JOIN direction DIR ON CUR.id_direction = DIR.id_direction\n" + "WHERE\n" +
                "\tLOS.id_order_head = :idOrder\n" + "ORDER BY\n" + "\tcourse, groupname, HF.family, HF.name, HF.patronymic";

        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("recordbook")
                .addScalar("groupname")
                .addScalar("course")
                .addScalar("formofstudy")
                .addScalar("is_government_financed")
                .addScalar("specialitytitle")
                .addScalar("specialitycode")
                .addScalar("qualification")
                .addScalar("directiontitle")
                .addScalar("directioncode")
                .addScalar("date1")
                .addScalar("date2")
                .addScalar("foundation")
                .addScalar("additional")
                .addScalar("sex")
                .setResultTransformer(Transformers.aliasToBean(OrderReportModel.class));
        q.setLong("idOrder", idOrder);
        return (List<OrderReportModel>) getList(q);
    }

    public List<OrderReportModel> getListSocial(Long idOrder) {
        //language=PostgreSQL
        String query = "SELECT\n" +
                "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio,\n" +
                "SC.recordbook, COALESCE(LOSS.groupname, DG.groupname, '') AS groupname, OS.foundation, " +
                "(SELECT lgsin.course FROM link_group_semester lgsin INNER JOIN semester sein USING (id_semester) \n" +
                "WHERE id_dic_group = (\n" +
                "\tSELECT lgs.id_dic_group FROM \n" +
                "\tstudent_semester_status sssin\n" +
                "\tINNER JOIN link_group_semester lgs USING (id_link_group_semester)\n" +
                "\tWHERE \n" +
                "\tid_student_semester_status = SSS.id_student_semester_status\n" +
                ") AND is_current_sem = 1) AS course\n" +
                "\t,REPLACE(\n" +
                "\t\tREPLACE(OS.description, '$date1$', COALESCE(TO_CHAR(LOSS.first_date, 'dd.MM.yyyy'), '')),\n" +
                "\t\t'$date2$', COALESCE(TO_CHAR(LOSS.second_date, 'dd.MM.yyyy'), '')) AS description,\n" +
                "\t\tsc.is_sirota as sirota, sc.is_indigent as indigent, sc.is_invalid as invalid, sc.is_veteran as veteran \n" +
                "FROM\n" +
                "\tlink_order_section LOS\n" +
                "\tINNER JOIN order_section OS USING (id_order_section)\n" +
                "\tINNER JOIN link_order_student_status LOSS USING (id_link_order_section)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                "\tINNER JOIN humanface HF USING (id_humanface)\n"
                + "WHERE\n" + "\tLOS.id_order_head = :idOrder\n" +
                "ORDER BY\n" +
                "\tlayout, description, LOSS.first_date, LOSS.second_date, course, groupname, fio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("recordbook")
                .addScalar("groupname")
                .addScalar("foundation")
                .addScalar("course")
                .addScalar("description")
                .addScalar("sirota", BooleanType.INSTANCE)
                .addScalar("invalid", BooleanType.INSTANCE)
                .addScalar("indigent", BooleanType.INSTANCE)
                .addScalar("veteran", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderReportModel.class));
        q.setLong("idOrder", idOrder);
        return (List<OrderReportModel>) getList(q);
    }

    public List<OrderReportModel> getListSocialIncrease(Long idOrder) {
        String query = "SELECT\n" +
                "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio,\n" +
                " SC.recordbook, " +
                " COALESCE(LOSS.groupname, DG.groupname, '') AS groupname," +
                " OS.foundation," +
                " LGS.course,\n" +
                "\tCASE\n" +
                "\t\tWHEN (LOSS.second_date IS NOT NULL AND LOSS.first_date IS NOT NULL) " +
                "THEN 'С '||TO_CHAR(LOSS.first_date, 'dd.MM.yyyy')||' по '||TO_CHAR(LOSS.second_date,'dd.MM.yyyy')||''\n" +
                "\tELSE '' END AS descriptionDate,\n" +
                "\tCASE\n" +
                "\t\tWHEN LOSS.second_date IS NULL THEN REPLACE(OS.description, '$date1$'," +
                " COALESCE(TO_CHAR(LOSS.first_date, 'dd.MM.yyyy'), ''))\n" +
                "\tELSE OS.description END AS description, \n" +
                "\t\tsc.is_sirota as sirota, sc.is_indigent as indigent, sc.is_invalid as invalid, sc.is_veteran as veteran, \n" +
                "\tos.id_order_section as idOrderSection\n" +
                "FROM\n" + "\tlink_order_section LOS\n" +
                "\tINNER JOIN order_section OS USING (id_order_section)\n" +
                "\tINNER JOIN link_order_student_status LOSS USING (id_link_order_section)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "WHERE\n" + "\tLOS.id_order_head = :idOrder\n" +
                "ORDER BY\n" +
                "\tdescription, LOSS.first_date, LOSS.second_date, course, groupname, fio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("sirota", BooleanType.INSTANCE)
                .addScalar("invalid", BooleanType.INSTANCE)
                .addScalar("indigent", BooleanType.INSTANCE)
                .addScalar("veteran", BooleanType.INSTANCE)
                .addScalar("recordbook")
                .addScalar("groupname")
                .addScalar("foundation")
                .addScalar("course")
                .addScalar("description")
                .addScalar("descriptionDate")
                .addScalar("idOrderSection")
                .setResultTransformer(Transformers.aliasToBean(OrderReportModel.class));
        q.setLong("idOrder", idOrder);

        return (List<OrderReportModel>) getList(q);
    }

    public List<OrderReportModel> getTransferEsoModel(Long idOrder) {
        //language=PostgreSQL
        String query = "SELECT\n" +
                "\tCASE\n" +
                "\t\tWHEN HF.foreigner = TRUE THEN '* '||HF.family||' '||HF.name||' '||HF.patronymic\n" +
                "\t ELSE HF.family||' '||HF.name||' '||HF.patronymic END AS fio, SC.recordbook, \n" +
                "\tCOALESCE(LOSS.groupname, DG.groupname, '') AS groupname," +
                " LGS.course AS course,\n" +
                "\tREPLACE (REPLACE(OS.description, CAST('$date1$' AS TEXT), COALESCE(TO_CHAR(LOSS.first_date, 'dd.MM.yyyy'), '')), \n" +
                "\t\tCAST('$date2$' AS TEXT), COALESCE(TO_CHAR(LOSS.second_date, 'dd.MM.yyyy'),'')) AS description,\n" +
                "\tCOALESCE (LOS.foundation, OS.foundation) AS foundation," +
                "\tLOSS.additional AS additional, " +
                "sss.is_government_financed as governmentFinanced, \n" +
                "\tLOSS.first_date AS date1," +
                "\tLOSS.second_date AS date2," +
                " hf.foreigner as isForeigner,\n" +
                "\tOS.id_order_section as idOrderSection \n" +
                "FROM\n" +
                "\thumanface HF\n" +
                "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN link_order_student_status LOSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_order_section LOS USING (id_link_order_section)\n" +
                "\tINNER JOIN order_section OS USING (id_order_section)\n" +
                "\tINNER JOIN order_rule ORR USING (id_order_rule)\n" +
                "WHERE\n" +
                "\tLOS.id_order_head = :idOrder\n" +
                "ORDER BY OS.id_order_section, CASE\n" +
                "\twhen ORR.id_order_rule in (29,30,31) then (LOSS.additional) end,\n" +
                "\tLOSS.additional,LOSS.first_date, LOSS.second_date, semesternumber, course, groupname, HF.family, HF.name, HF.patronymic";

        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("recordbook")
                .addScalar("groupname")
                .addScalar("course")
                .addScalar("foundation")
                .addScalar("isForeigner", BooleanType.INSTANCE)
                .addScalar("description")
                .addScalar("additional")
                .addScalar("date1")
                .addScalar("date2")
                .addScalar("idOrderSection", LongType.INSTANCE)
                .addScalar("governmentFinanced", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderReportModel.class));
        q.setLong("idOrder", idOrder);

        return (List<OrderReportModel>) getList(q);
    }

    public List<OrderReportModel> getStudentsForCancelInSession(Long idOrder) {
        String query = "SELECT\n" +
                "\tCASE\n" +
                "\t\tWHEN HF.foreigner = TRUE THEN '* '||HF.family||' '||HF.name||' '||HF.patronymic\n" +
                "\t ELSE HF.family||' '||HF.name||' '||HF.patronymic END AS fio, " +
                "SC.recordbook, \n" +
                "\tCOALESCE(LOSS.groupname, DG.groupname, '') AS groupname, " +
                "LGS.course AS course,\n" +
                "\tREPLACE (REPLACE(OS.description, CAST('$date1$' AS TEXT), COALESCE(TO_CHAR(LOSS.first_date, 'dd.MM.yyyy'), '')), \n" +
                "\t\tCAST('$date2$' AS TEXT), COALESCE(TO_CHAR(LOSS.second_date, 'dd.MM.yyyy'),'')) AS description,\n" +
                "\tCOALESCE (LOS.foundation, OS.foundation) AS foundation \n" +
                "FROM\n" +
                "\thumanface HF\n" +
                "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN link_order_student_status LOSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_order_section LOS USING (id_link_order_section)\n" +
                "\tINNER JOIN order_section OS USING (id_order_section)\n" +
                "\tINNER JOIN order_rule ORR USING (id_order_rule)\n" +
                "WHERE\n" +
                "\tLOS.id_order_head = :idOrder\n" +
                "ORDER BY OS.id_order_section, course, groupname, HF.family, HF.name, HF.patronymic";

        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("recordbook")
                .addScalar("groupname")
                .addScalar("course")
                .addScalar("foundation")
                .addScalar("description")
                .setResultTransformer(Transformers.aliasToBean(OrderReportModel.class));
        q.setLong("idOrder", idOrder);

        return (List<OrderReportModel>) getList(q);
    }

    public List<OrderReportModel> getTransferEsoModelForServiceNote(Long idOrder) {
        String query = "SELECT\n" + "\tCASE\n" + "\t\tWHEN HF.foreigner = TRUE THEN '* '||HF.family||' '||HF.name||' '||HF.patronymic\n" +
                "\tELSE HF.family||' '||HF.name||' '||HF.patronymic END AS fio, SC.recordbook, \n" +
                "\tCOALESCE(LOSS.groupname, DG.groupname, '') AS groupname, LGS.course+1 AS course,\n" + "\tCASE\n" +
                "\t\tWHEN ORR.name ILIKE '%(условно)%' THEN REPLACE (REPLACE(OS.description, CAST('$date1$' AS TEXT), COALESCE(TO_CHAR(LOSS.first_date, 'dd.MM.yyyy'), '')), \n" +
                "\t\tCAST('$date2$' AS TEXT), COALESCE(TO_CHAR(LOSS.second_date, 'dd.MM.yyyy'),''))\n" + "\tELSE\n" +
                "\t\tREPLACE(OS.description, CAST('$date1$' AS TEXT), COALESCE(TO_CHAR(LOSS.first_date, 'dd.MM.yyyy'), '')) END AS description,\n" +
                "\tCOALESCE (LOS.foundation, OS.foundation) AS foundation,\n" + "\tLOSS.additional AS additional,\n" +
                "\tLOSS.first_date AS firstDateStudent\n" + "FROM\n" + "\thumanface HF\n" +
                "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN link_order_student_status LOSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_order_section LOS USING (id_link_order_section)\n" +
                "\tINNER JOIN order_section OS USING (id_order_section)\n" + "\tINNER JOIN order_rule ORR USING (id_order_rule)\n" +
                "WHERE\n" + "\tLOS.id_order_head = :idOrder\n" + "ORDER BY OS.id_order_section, \n" +
                "\tLOSS.first_date, LOSS.second_date, course, groupname, HF.family, HF.name, HF.patronymic";

        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("recordbook")
                .addScalar("groupname")
                .addScalar("course")
                .addScalar("foundation")
                .addScalar("description")
                .addScalar("additional")
                .addScalar("firstDateStudent")
                .setResultTransformer(Transformers.aliasToBean(OrderReportModel.class));
        q.setLong("idOrder", idOrder);
        return (List<OrderReportModel>) getList(q);
    }

    public List<OrderReportModel> getSetEliminationModel(Long idOrder) {
        String query = "SELECT\n" + "\tCASE\n" + "\t\tWHEN HF.foreigner = TRUE THEN '* '||HF.family||' '||HF.name||' '||HF.patronymic\n" +
                "\t ELSE HF.family||' '||HF.name||' '||HF.patronymic END AS fio, \n" +
                "\tSC.recordbook, DG.groupname, LGS.course, SSS.is_government_financed as governmentFinanced,\n" +
                "\tOS.id_order_section as idOrderSection,\n" +
                "\tREPLACE(OS.description, CAST('$date1$' AS TEXT), COALESCE(TO_CHAR(LOSS.first_date, 'dd.MM.yyyy'), '')) AS description,\n" +
                "\tCASE\n" + "\t\tWHEN SEM.formofstudy = 1 THEN 'очная форма обучения'\n" +
                "\t\tWHEN SEM.formofstudy = 2 THEN 'заочная форма обучения'\n" + "\tEND AS formofstudy,\n" +
                "\tCUR.specialitytitle, CUR.directioncode AS specialitycode," +
                " CUR.qualification,\n" +
                "\tDIR.title AS directiontitle, DIR.code AS directioncode,\n" +
                "\tDIR.title AS directiontitle, DIR.code AS directioncode,\n" +
                "\tSY.dateofbegin AS beginYear, SY.dateofend AS endYear,\n" +
                "\tLOSS.first_date AS firstDateStudent, COALESCE(LOS.foundation, OS.foundation, '') as foundation\n" + "FROM\n" +
                "\tlink_order_section LOS\n" + "\tINNER JOIN order_section OS ON LOS.id_order_section = OS.id_order_section\n" +
                "\tINNER JOIN link_order_student_status LOSS ON LOS.id_link_order_section = LOSS.id_link_order_section\n" +
                "\tINNER JOIN student_semester_status SSS ON LOSS.id_student_semester_status = SSS.id_student_semester_status\n" +
                "\tINNER JOIN studentcard SC ON SSS.id_studentcard = SC.id_studentcard\n" +
                "\tINNER JOIN humanface HF ON SC.id_humanface = HF.id_humanface\n" +
                "\tINNER JOIN link_group_semester LGS ON SSS.id_link_group_semester = LGS.id_link_group_semester\n" +
                "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "\tINNER JOIN schoolyear SY ON SEM.id_schoolyear = SY.id_schoolyear\n" +
                "\tINNER JOIN dic_group DG ON LGS.id_dic_group = DG.id_dic_group\n" +
                "\tINNER JOIN curriculum CUR ON DG.id_curriculum = CUR.id_curriculum\n" +
                "\tLEFT JOIN direction DIR ON CUR.id_direction = DIR.id_direction\n" + "WHERE\n" +
                "\tLOS.id_order_head = :idOrder\n" + "ORDER BY\n" +
                "\tidOrderSection, LOSS.first_date, course, groupname, HF.family, HF.name, HF.patronymic";

        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("recordbook")
                .addScalar("groupname")
                .addScalar("course")
                .addScalar("governmentFinanced", BooleanType.INSTANCE)
                .addScalar("firstDateStudent")
                .addScalar("foundation")
                .addScalar("beginYear")
                .addScalar("endYear")
                .addScalar("idOrderSection", LongType.INSTANCE)
                .addScalar("description", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderReportModel.class));
        q.setLong("idOrder", idOrder);
        return (List<OrderReportModel>) getList(q);
    }

    public boolean existsStudentsInOrderBySubquery(Long idOrder, String subquery) {
        String query = "SELECT\tCOUNT(*)>0\n" + "FROM\thumanface HF\n" + "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tINNER JOIN link_order_student_status LOSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_order_section LOS USING (id_link_order_section)\n" + "WHERE\tLOS.id_order_head = :idOrder AND " +
                subquery;

        Query q = getSession().createSQLQuery(query);
        q.setLong("idOrder", idOrder);
        return (boolean) q.uniqueResult();
    }

    public List<OrderReportModel> getListForAcademicIncreased(Long idOrder) {
        String query = "SELECT\n" +
                "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio,\n" +
                "SC.recordbook, " +
                "COALESCE(LOSS.groupname, DG.groupname, '') AS groupname, " +
                "OS.foundation, " +
                "LGS.course, " +
                "REPLACE(\n" +
                "\t\tREPLACE(OS.description, '$date1$', COALESCE(TO_CHAR(LOSS.first_date, 'dd.MM.yyyy'), '')),\n" +
                "\t\t'$date2$', COALESCE(TO_CHAR(LOSS.second_date, 'dd.MM.yyyy'), '')) AS description,\n" +
                "LOSS.additional as additional \n " +
                "FROM link_order_section LOS\n" +
                "\tINNER JOIN order_section OS USING (id_order_section)\n" +
                "\tINNER JOIN link_order_student_status LOSS USING (id_link_order_section)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "WHERE\n" +
                "\tLOS.id_order_head = :idOrder\n" +
                "ORDER BY description, groupname, additional, fio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("recordbook")
                .addScalar("groupname")
                .addScalar("foundation")
                .addScalar("course")
                .addScalar("description")
                .addScalar("additional")
                .setResultTransformer(Transformers.aliasToBean(OrderReportModel.class));
        q.setLong("idOrder", idOrder);
        return (List<OrderReportModel>) getList(q);
    }

    public List<OrderReportModel> getSetCertificationModel(Long idOrder) {
        String query = "SELECT\n" +
                "\tCASE\n" +
                "\t\tWHEN HF.foreigner = TRUE THEN '* '||HF.family||' '||HF.name||' '||HF.patronymic\n" +
                "\t ELSE HF.family||' '||HF.name||' '||HF.patronymic END AS fio, \n" +
                "\tSC.recordbook, DG.groupname, LGS.course, SSS.is_government_financed as governmentFinanced,\n" +
                "\tOS.id_order_section as idOrderSection,\n" +
                "\tCASE\n" +
                "\tWHEN sem.season = 0 THEN\n" +
                "\tREPLACE(REPLACE(REPLACE(REPLACE(OS.description, CAST('$date1$' AS TEXT), COALESCE(TO_CHAR(LOSS.first_date, 'dd.MM.yyyy'), '')),\n" +
                "\t\tCAST('$dateofbegin$' AS TEXT), COALESCE(TO_CHAR(sy.dateofbegin, 'yyyy'), '')),\n" +
                "\t\tCAST('$dateofend$' AS TEXT), COALESCE(TO_CHAR(sy.dateofend, 'yyyy'), '')),\n" +
                "\t\tCAST('$sem$' AS TEXT), 'осеннего')\n" +
                "\t\t ELSE\n" +
                "\t\tREPLACE(REPLACE(REPLACE(REPLACE(OS.description, CAST('$date1$' AS TEXT), COALESCE(TO_CHAR(LOSS.first_date, 'dd.MM.yyyy'), '')),\n" +
                "\tCAST('$dateofbegin$' AS TEXT), COALESCE(TO_CHAR(sy.dateofbegin, 'yyyy'), '')),\n" +
                "\tCAST('$dateofend$' AS TEXT), COALESCE(TO_CHAR(sy.dateofend, 'yyyy'), '')),\n" +
                "\tCAST('$sem$' AS TEXT), 'весеннего')\n" +
                "\tEND AS description,\n" +
                "\tCASE\n" +
                "\t\tWHEN SEM.formofstudy = 1 THEN 'очная форма обучения'\n" +
                "\t\tWHEN SEM.formofstudy = 2 THEN 'заочная форма обучения'\n" + "\tEND AS formofstudy,\n" +
                "\tCUR.specialitytitle, CUR.directioncode AS specialitycode, " +
                "CUR.qualification,\n" +
                "\tDIR.title AS directiontitle, DIR.code AS directioncode,\n" +
                "\tDIR.title AS directiontitle, DIR.code AS directioncode,\n" +
                "\tSY.dateofbegin AS beginYear, SY.dateofend AS endYear,\n" +
                "\tLOSS.first_date AS firstDateStudent, COALESCE(LOS.foundation, OS.foundation, '') as foundation\n" + "FROM\n" +
                "\tlink_order_section LOS\n" + "\tINNER JOIN order_section OS ON LOS.id_order_section = OS.id_order_section\n" +
                "\tINNER JOIN link_order_student_status LOSS ON LOS.id_link_order_section = LOSS.id_link_order_section\n" +
                "\tINNER JOIN student_semester_status SSS ON LOSS.id_student_semester_status = SSS.id_student_semester_status\n" +
                "\tINNER JOIN studentcard SC ON SSS.id_studentcard = SC.id_studentcard\n" +
                "\tINNER JOIN humanface HF ON SC.id_humanface = HF.id_humanface\n" +
                "\tINNER JOIN link_group_semester LGS ON SSS.id_link_group_semester = LGS.id_link_group_semester\n" +
                "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "\tINNER JOIN schoolyear SY ON SEM.id_schoolyear = SY.id_schoolyear\n" +
                "\tINNER JOIN dic_group DG ON LGS.id_dic_group = DG.id_dic_group\n" +
                "\tINNER JOIN curriculum CUR ON DG.id_curriculum = CUR.id_curriculum\n" +
                "\tLEFT JOIN direction DIR ON CUR.id_direction = DIR.id_direction\n" + "WHERE\n" +
                "\tLOS.id_order_head = :idOrder\n" +
                "ORDER BY\n" +
                "\tLOSS.first_date,idOrderSection,course, groupname, HF.family, HF.name, HF.patronymic";

        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("recordbook")
                .addScalar("groupname")
                .addScalar("course")
                .addScalar("governmentFinanced", BooleanType.INSTANCE)
                .addScalar("firstDateStudent")
                .addScalar("foundation")
                .addScalar("beginYear")
                .addScalar("endYear")
                .addScalar("idOrderSection", LongType.INSTANCE)
                .addScalar("description", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderReportModel.class));
        q.setLong("idOrder", idOrder);
        return (List<OrderReportModel>) getList(q);
    }

    public Long getOrderType(Long idOrder) {
        String query = "select id_order_rule from order_head where id_order_head = :idOrder\n";
        Query q = getSession().createSQLQuery(query).setLong("idOrder", idOrder);

        return ((List<Long>) getList(q)).get(0);
    }

    public String getOrderDescription(Long idOrder) {
        String query = "select description from order_head\n" +
                "join order_rule o on order_head.id_order_rule = o.id_order_rule\n" +
                "where id_order_head = :idOrder";

        Query q = getSession().createSQLQuery(query).setLong("idOrder", idOrder);

        return ((List<String>) getList(q)).get(0);
    }

    public String getOrderAttributes(Long idOrder) {
        String query = "select attr1 from order_head\n" +
                "join order_rule o on order_head.id_order_rule = o.id_order_rule\n" +
                "where id_order_head = :idOrder";

        Query q = getSession().createSQLQuery(query).setLong("idOrder", idOrder);

        return ((List<String>) getList(q)).get(0);
    }

    public String getSemester(Long idSemester) {
        String query = "select " +
                "(CASE WHEN SEM.season = 0 THEN 'осеннего семестра ' || TO_CHAR(SY.dateofbegin, 'yyyy')||'/'||TO_CHAR(SY.dateofend, 'yyyy')\n" +
                "                       ELSE 'весеннего семестра '||TO_CHAR(SY.dateofbegin, 'yyyy')||'/'||TO_CHAR(SY.dateofend, 'yyyy')\n" +
                "END) AS semester from semester SEM\n" +
                "join schoolyear SY on SEM.id_schoolyear = SY.id_schoolyear\n" +
                "where id_semester = :idSemester";

        Query q = getSession().createSQLQuery(query)
                .setLong("idSemester", idSemester);

        return ((List<String>) getList(q)).get(0);
    }

    public String getYear(Long idSemester) {
        String query = "select TO_CHAR(SY.dateofbegin, 'yyyy')||'/'||TO_CHAR(SY.dateofend, 'yyyy')\n" +
                "AS semester from semester SEM\n" +
                "join schoolyear SY on SEM.id_schoolyear = SY.id_schoolyear\n" +
                "where id_semester = :idSemester";

        Query q = getSession().createSQLQuery(query)
                .setLong("idSemester", idSemester);

        return ((List<String>) getList(q)).get(0);
    }
}