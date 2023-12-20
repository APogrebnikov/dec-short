package org.edec.utility.report.manager;

import org.edec.dao.DAO;
import org.edec.utility.report.model.order.IndividualOrderModel;
import org.edec.utility.report.model.order.MainOrderModel;
import org.edec.utility.report.model.order.dao.*;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import java.util.List;

/**
 * Created by dmmax
 */
public class OrderReportDAO extends DAO {
    public MainOrderModel getMainOrderInfoByID (Long id_oh) {
        String query = "SELECT\n" + "\tINST.fulltitle AS institute, OH.dateofend AS datesign, OH.ordernumber,\n" +
                       "\tORR.head_description AS typeorder, ORR.description AS descriptiontitle,\n" +
                       "\tORR.description2 AS descriptiontitle2, ORR.id_order_rule AS idOrderRule,\n" + "\tCASE\n" +
                       "\t\tWHEN SEM.formofstudy = 1 THEN 'очная форма обучения'\n" +
                       "\t\tWHEN SEM.formofstudy = 2 THEN 'заочная форма обучения'\n" +
                       "\tEND AS formofstudy, SEM.formofstudy AS formOfStudyId, OH.certnumber, OH.certfio\n" + "FROM\n" +
                       "\torder_head OH\n" + "\tINNER JOIN order_rule ORR ON OH.id_order_rule = ORR.id_order_rule\n" +
                       "\tINNER JOIN semester SEM ON OH.semester = SEM.id_semester\n" +
                       "\tINNER JOIN institute INST ON SEM.id_institute = INST.id_institute\n" + "WHERE\n" +
                       "\tOH.id_order_head = :idOrder";
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
                              .setResultTransformer(Transformers.aliasToBean(MainOrderModel.class));
        q.setLong("idOrder", id_oh);
        return (MainOrderModel) getList(q).get(0);
    }

    public List<EmployeeOrderEsoModel> getEmployeesOrder (Long idOrder) {
        String query = "SELECT\n" + "\tSUBSTRING(HF.name, 1, 1)||'. '||SUBSTRING(HF.patronymic, 1, 1)||'. '||HF.family AS fio,\n" +
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

    public List<AcademicEsoModel> getAcademicModel (Long idOrder) {
        String query = "SELECT\n" + "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio, SC.recordbook, \n" +
                       "\tCOALESCE(LOSS.groupname, DG.groupname, '') AS groupname, \n" +
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
                              .setResultTransformer(Transformers.aliasToBean(AcademicEsoModel.class));
        q.setLong("idOrder", idOrder);
        return (List<AcademicEsoModel>) getList(q);
    }

    public List<IndividualOrderModel> getListIndividuals (Long idOrder) {
        String query = "SELECT\n" + "\tCASE\n" + "\t\tWHEN HF.foreigner = TRUE THEN '* '||HF.family||' '||HF.name||' '||HF.patronymic\n" +
                       "\tELSE HF.family||' '||HF.name||' '||HF.patronymic END AS fio, \n" +
                       "\tSC.recordbook, DG.groupname, LGS.course,SSS.is_government_financed,\n" + "\tCASE\n" +
                       // TODO исправить очная форма обучения и в новом модуле приказов
                       "\t\tWHEN CUR.formofstudy = 1 THEN 'очная форма обучения'\n" +
                       "\t\tWHEN CUR.formofstudy = 2 AND CUR.distancetype = 2 THEN 'заочная форма обучения'\n" +
                       "\t\tWHEN CUR.formofstudy = 2 AND CUR.distancetype = 1 THEN 'очно-заочная форма обучения'\n" +
                       "\tEND AS formofstudy, " + "\tCUR.specialitytitle, CUR.directioncode AS specialitycode, CUR.qualification,\n" +
                       "\tDIR.title AS directiontitle, DIR.code AS directioncode,\n" +
                       "\tLOSS.first_date AS date1, LOSS.second_date AS date2, COALESCE(LOS.foundation, OS.foundation, '') AS foundation,\n" +
                       "\tLOSS.additional AS additional, hf.sex as sex\n" + "FROM\n" + "\tlink_order_section LOS\n" +
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
                              .addScalar("sex", IntegerType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(IndividualOrderModel.class));
        q.setLong("idOrder", idOrder);
        return (List<IndividualOrderModel>) getList(q);
    }

    public List<SocialEsoModel> getListSocial (Long idOrder) {
        String query = "SELECT\n" + "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio,\n" +
                       "\tSC.recordbook, COALESCE(LOSS.groupname, DG.groupname, '') AS groupname, OS.foundation, LGS.course\n" +
                       "\t,REPLACE(\n" + "\t\tREPLACE(OS.description, '$date1$', COALESCE(TO_CHAR(LOSS.first_date, 'dd.MM.yyyy'), '')),\n" +
                       "\t\t'$date2$', COALESCE(TO_CHAR(LOSS.second_date, 'dd.MM.yyyy'), '')) AS description,\n" +
                       "\t\tsc.is_sirota as sirota, sc.is_indigent as indigent, sc.is_invalid as invalid\n" + "FROM\n" +
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
                              .addScalar("sirota", BooleanType.INSTANCE)
                              .addScalar("invalid", BooleanType.INSTANCE)
                              .addScalar("indigent", BooleanType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(SocialEsoModel.class));
        q.setLong("idOrder", idOrder);
        return (List<SocialEsoModel>) getList(q);
    }

    public List<SocialIncreaseEsoModel> getListSocialIncrease (Long idOrder) {
        String query = "SELECT\n" + "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio,\n" +
                       "\tSC.recordbook, COALESCE(LOSS.groupname, DG.groupname, '') AS groupname, OS.foundation, LGS.course,\n" +
                       "\tCASE\n" + "\t\tWHEN (LOSS.second_date IS NOT NULL AND LOSS.first_date IS NOT NULL) " +
                       "THEN 'С '||TO_CHAR(LOSS.first_date, 'dd.MM.yyyy')||' по '||TO_CHAR(LOSS.second_date,'dd.MM.yyyy')||''\n" +
                       "\tELSE '' END AS descriptionDate,\n" + "\tCASE\n" +
                       "\t\tWHEN LOSS.second_date IS NULL THEN REPLACE(OS.description, '$date1$', COALESCE(TO_CHAR(LOSS.first_date, 'dd.MM.yyyy'), ''))\n" +
                       "\tELSE OS.description END AS description \n" + "FROM\n" + "\tlink_order_section LOS\n" +
                       "\tINNER JOIN order_section OS USING (id_order_section)\n" +
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
                              .addScalar("descriptionDate")
                              .setResultTransformer(Transformers.aliasToBean(SocialIncreaseEsoModel.class));
        q.setLong("idOrder", idOrder);
        return (List<SocialIncreaseEsoModel>) getList(q);
    }

    public List<TransferEsoModel> getTransferEsoModel (Long idOrder) {
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
                       "WHERE\n" + "\tLOS.id_order_head = :idOrder\n" + "ORDER BY OS.id_order_section, CASE\n" +
                       "\tWHEN ORR.id_order_rule IN (29,30,31) THEN (LOSS.additional) END,\n" +
                       "\tLOSS.additional,LOSS.first_date, LOSS.second_date, semesternumber, course, groupname, HF.family, HF.name, HF.patronymic";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("recordbook")
                              .addScalar("groupname")
                              .addScalar("course")
                              .addScalar("foundation")
                              .addScalar("description")
                              .addScalar("additional")
                              .addScalar("firstDateStudent")
                              .setResultTransformer(Transformers.aliasToBean(TransferEsoModel.class));
        q.setLong("idOrder", idOrder);
        return (List<TransferEsoModel>) getList(q);
    }

    public List<TransferEsoModel> getTransferEsoModelForServiceNote (Long idOrder) {
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
                              .setResultTransformer(Transformers.aliasToBean(TransferEsoModel.class));
        q.setLong("idOrder", idOrder);
        return (List<TransferEsoModel>) getList(q);
    }

    public List<SetEliminationModel> getSetEliminationModel (Long idOrder) {
        String query = "SELECT\n" + "\tCASE\n" + "\t\tWHEN HF.foreigner = TRUE THEN '* '||HF.family||' '||HF.name||' '||HF.patronymic\n" +
                       "\tELSE HF.family||' '||HF.name||' '||HF.patronymic END AS fio, \n" +
                       "\tSC.recordbook, DG.groupname, LGS.course, SSS.is_government_financed AS governmentFinanced,\n" +
                       "\tREPLACE(OS.description, CAST('$date1$' AS TEXT), COALESCE(TO_CHAR(LOSS.first_date, 'dd.MM.yyyy'), '')) AS description,\n" +
                       "\tCASE\n" + "\t\tWHEN SEM.formofstudy = 1 THEN 'очная форма обучения'\n" +
                       "\t\tWHEN SEM.formofstudy = 2 THEN 'заочная форма обучения'\n" + "\tEND AS formofstudy,\n" +
                       "\tCUR.specialitytitle, CUR.directioncode AS specialitycode, CUR.qualification,\n" +
                       "\tDIR.title AS directiontitle, DIR.code AS directioncode,\n" +
                       "\tDIR.title AS directiontitle, DIR.code AS directioncode,\n" +
                       "\tSY.dateofbegin AS beginYear, SY.dateofend AS endYear,\n" +
                       "\tLOSS.first_date AS firstDateStudent, COALESCE(LOS.foundation, OS.foundation, '') AS foundation\n" + "FROM\n" +
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
                       "\tfirstDateStudent, governmentFinanced DESC, course, groupname, HF.family, HF.name, HF.patronymic";

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
                              .addScalar("description", StringType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(SetEliminationModel.class));
        q.setLong("idOrder", idOrder);
        return (List<SetEliminationModel>) getList(q);
    }

    public boolean existsStudentsInOrderBySubquery (Long idOrder, String subquery) {
        String query = "SELECT\tCOUNT(*)>0\n" + "FROM\thumanface HF\n" + "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                       "\tINNER JOIN link_order_student_status LOSS USING (id_student_semester_status)\n" +
                       "\tINNER JOIN link_order_section LOS USING (id_link_order_section)\n" + "WHERE\tLOS.id_order_head = :idOrder AND " +
                       subquery;

        Query q = getSession().createSQLQuery(query);
        q.setLong("idOrder", idOrder);
        return (boolean) q.uniqueResult();
    }
}