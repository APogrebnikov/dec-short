package org.edec.order.manager;

import org.edec.dao.DAO;
import org.edec.order.model.*;
import org.edec.order.model.dao.ReferenceModelESO;
import org.edec.utility.converter.DateConverter;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class CreateOrderManagerESO extends DAO {
    public List<OrderRuleModel> getListOrderRule (Long idInst) {
        String query = "SELECT\n" + "\t id_order_rule AS id,\n" + "\t name AS name,\n" + "\t id_order_type AS idOrderType\n" +
                       "FROM order_rule WHERE is_displayed = TRUE AND id_institute = :idInst";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("idOrderType", LongType.INSTANCE)
                              .addScalar("name", StringType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(OrderRuleModel.class));

        q.setLong("idInst", idInst);
        return (List<OrderRuleModel>) getList(q);
    }

    public List<OrderSectionModel> getListOrderSection (Long idRule) {
        String query = "select\n" + "\t id_order_section as id,\n" + "\t name as name,\n" + "\t foundation as foundation\n" +
                       "from order_section where id_order_rule = " + idRule;

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("name", StringType.INSTANCE)
                              .addScalar("foundation", StringType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(OrderSectionModel.class));
        return (List<OrderSectionModel>) getList(q);
    }

    /******************** АКАДЕМИЧЕСКИЕ ПРИКАЗЫ **********************************/

    public List<StudentToCreateModel> getStudentsForAcademicalInSession (Date firstDate, Date secondDate, long idSemester,
                                                                         long sessionresult) {
        String strFirstDate = new SimpleDateFormat("yyyy-MM-dd").format(firstDate);
        String strSecondDate = new SimpleDateFormat("yyyy-MM-dd").format(secondDate);

        Long nextSemester = idSemester;

        String subquery = "";
        // TODO убрать условие
        if (nextSemester != null) {
            subquery = ", " + "\t(\n" + "\t\tselect dateofendsession from link_group_semester lgs2 \n" +
                       "\t\twhere lgs2.id_dic_group = lgs.id_dic_group and id_semester = " + idSemester + "\n" +
                       "\t) as dateNextEndOfSession\n";
        }

        String query = "select\n" + "\tsss.id_student_semester_status as id,\n" + "\t(\n" +
                       "\t\tselect is_government_financed from student_semester_status sss2\n" +
                       "\t\tinner join link_group_semester lgs2 using(id_link_group_semester)\n" +
                       "\t\tinner join semester sem2 using(id_semester)\n" +
                       "\t\twhere lgs2.id_dic_group = sc.id_current_dic_group and sss2.id_studentcard = sss.id_studentcard and sem2.is_current_sem = 1\n" +
                       "\t) as nextGovernmentFinanced,\n" + "\t(\n" + "\t\tselect is_deducted from student_semester_status sss2\n" +
                       "\t\tinner join link_group_semester lgs2 using(id_link_group_semester)\n" +
                       "\t\tinner join semester sem2 using(id_semester)\n" +
                       "\t\twhere lgs2.id_dic_group = sc.id_current_dic_group and sss2.id_studentcard = sss.id_studentcard and sem2.is_current_sem = 1\n" +
                       "\t) as deductedCurSem,\n" + "\tdg.groupname\n" + subquery + "from student_semester_status sss\n" +
                       "inner join studentcard sc using(id_studentcard)\n" +
                       "inner join link_group_semester lgs using(id_link_group_semester)\n" +
                       "inner join dic_group dg using(id_dic_group)\n" + "where\n" +
                       "\tid_semester = " + getPrevSemester(idSemester) + "\n" + "\tand sessionresult = " + sessionresult + "\n" +
                       "\tand date_complete_session >= '" + strFirstDate + "'\n" + "\tand date_complete_session <= '" + strSecondDate +
                       "'\n" + "\tand sss.is_government_financed = 1\n" + "\tand is_deducted = 0\n" + "\tand is_academicleave = 0\n" +
                       "\tand is_get_academic = 0\n" + "\tand sc.id_current_dic_group = lgs.id_dic_group\n" +
                       "\tand dg.dateofend >= now()\n" + "\tand lgs.dateofendsemester >= '" + strFirstDate + "'";

        SQLQuery q = getSession().createSQLQuery(query)
                                 .addScalar("id", LongType.INSTANCE)
                                 .addScalar("groupname", StringType.INSTANCE)
                                 .addScalar("deductedCurSem", BooleanType.INSTANCE)
                                 .addScalar("nextGovernmentFinanced", BooleanType.INSTANCE);

        if (nextSemester != null) {
            q.addScalar("dateNextEndOfSession", DateType.INSTANCE);
        }
        q.setResultTransformer(Transformers.aliasToBean(StudentToCreateModel.class));
        return (List<StudentToCreateModel>) getList(q);
    }

    public List<StudentToCreateModel> getStudentsForAcademicalNotInSession (Date firstDate, Date secondDate, long idSemester,
                                                                            long sessionresult) {
        String strFirstDate = new SimpleDateFormat("yyyy-MM-dd").format(firstDate);
        String strSecondDate = new SimpleDateFormat("yyyy-MM-dd").format(secondDate);

        String query = "select\n" + "\tsss.id_student_semester_status as id,\n" + "        (\n" +
                       "\t\tselect groupname from student_semester_status sss2\n" +
                       "\t\tinner join link_group_semester lgs2 ON sss2.id_link_group_semester = lgs2.id_link_group_semester\n" +
                       "\t\tinner join dic_group dg2 ON dg2.id_dic_group = lgs2.id_dic_group\n" +
                       "                where sss2.id_studentcard = sss.id_studentcard and sss2.is_deducted = 0 and sss2.is_academicleave = 0 and id_semester = " +
                       idSemester + " order by sss2.id_student_semester_status desc limit 1\n" + "        ) as groupname,\n" +
                       "        (\n" + "\t\tselect dateofendsession from student_semester_status sss2\n" +
                       "\t\tinner join link_group_semester lgs2 ON sss2.id_link_group_semester = lgs2.id_link_group_semester\n" +
                       "                where sss2.id_studentcard = sss.id_studentcard and sss2.is_deducted = 0 and sss2.is_academicleave = 0 and id_semester = " +
                       idSemester + " order by sss2.id_student_semester_status desc limit 1\n" + "        ) as dateNextEndOfSession,\n" +
                       "        ( " + "\t\tselect is_government_financed from student_semester_status sss2\n" +
                       "\t\tinner join link_group_semester lgs2 ON sss2.id_link_group_semester = lgs2.id_link_group_semester\n" +
                       "    where sss2.id_studentcard = sss.id_studentcard and sss2.is_deducted = 0 and sss2.is_academicleave = 0 and id_semester = " +
                       idSemester + " order by sss2.id_student_semester_status desc limit 1\n" + "        ) as nextGovernmentFinanced\n" +
                       "from \n" + "\tstudent_semester_status sss\n" + "\tinner join studentcard sc using(id_studentcard)\n" +
                       "\tinner join link_group_semester lgs using(id_link_group_semester)\n" +
                       "        inner join dic_group dg using(id_dic_group)\n" + "where\n" + "        id_semester = " +
                       getPrevSemester(idSemester) + "\n" + "        and sessionresult = " + sessionresult + "\n" +
                       "        and date_complete_session >= '" + strFirstDate + "'\n" + "        and date_complete_session <= '" +
                       strSecondDate + "'" + "        and is_deducted = 0\n" + "        and is_get_academic = 0\n" +
                       "        and is_academicleave = 0\n" + "        and sc.id_current_dic_group = lgs.id_dic_group\n" +
                       "        and id_student_semester_status not in \n" + "        (\n" +
                       "\t\tselect id_student_semester_status from link_order_student_status\n" +
                       "\t\tinner join link_order_section los using(id_link_order_section)\n" +
                       "\t\tinner join order_head oh using (id_order_head)\n" + "\t\tinner join order_rule orule using (id_order_rule)\n" +
                       "\t\twhere ((id_semester = " + getPrevSemester(idSemester) +
                       " and id_order_section in (14,15,17)) or (id_semester = " + idSemester +
                       " and id_order_section in (51,52,53))) and id_order_type = 1 and additional <> null and additional <> ''\n" + "\t)";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("groupname", StringType.INSTANCE)
                              .addScalar("dateNextEndOfSession", DateType.INSTANCE)
                              .addScalar("nextGovernmentFinanced", BooleanType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentToCreateModel.class));
        return (List<StudentToCreateModel>) getList(q);
    }

    public List<StudentToCreateModel> getStudentsForAcademicalInSessionWithProlongation (Date firstDate, Date secondDate, long idSemester) {
        String strFirstDate = new SimpleDateFormat("yyyy-MM-dd").format(firstDate);
        String strSecondDate = new SimpleDateFormat("yyyy-MM-dd").format(secondDate);

        String query = "select\n" + "\tsss.id_student_semester_status as id,\n" + "\tdg.groupname,\n" + "\t(\n" +
                       "\t\tselect is_government_financed from student_semester_status sss2\n" +
                       "\t\tinner join link_group_semester lgs2 using(id_link_group_semester)\n" +
                       "\t\tinner join semester sem2 using(id_semester)\n" +
                       "\t\twhere lgs2.id_dic_group = sc.id_current_dic_group and sss2.id_studentcard = sss.id_studentcard and sem2.is_current_sem = 1\n" +
                       "\t) as nextGovernmentFinanced,\n" + "\t(\n" + "\t\tselect is_deducted from student_semester_status sss2\n" +
                       "\t\tinner join link_group_semester lgs2 using(id_link_group_semester)\n" +
                       "\t\tinner join semester sem2 using(id_semester)\n" +
                       "\t\twhere lgs2.id_dic_group = sc.id_current_dic_group and sss2.id_studentcard = sss.id_studentcard and sem2.is_current_sem = 1\n" +
                       "\t) as deductedCurSem,\n" + "\t(\n" +
                       "\t\tselect sessionresult as sessionresult2 from student_semester_status sss2\n" +
                       "\t\tinner join link_group_semester lgs2 ON lgs2.id_link_group_semester = sss2.id_link_group_semester\n" +
                       "\t\twhere lgs2.id_dic_group = lgs.id_dic_group and lgs2.id_semester = " + getPrevSemester(idSemester) +
                       " and sss2.id_studentcard = sss.id_studentcard\n" + "        ) as sessionResultPrev,\n" +
                       "        sss.prolongationenddate as prolongationEndDate\n" + "from student_semester_status sss\n" +
                       "inner join studentcard sc using(id_studentcard)\n" +
                       "inner join link_group_semester lgs using(id_link_group_semester)\n" +
                       "inner join dic_group dg using(id_dic_group)\n" + "where\n" + "\tid_semester = " + idSemester + "\n" +
                       "\tand sessionresult < 0\n" + "\tand sss.prolongationenddate > now()\n" + "\tand dateofendsession >= '" +
                       strFirstDate + "'\n" + "\tand dateofendsession <= '" + strSecondDate + "'\n" +
                       "\tand sss.is_government_financed = 1\n" + "\tand is_deducted = 0\n" +
                       "\tand sc.id_current_dic_group = lgs.id_dic_group\n" + "\tand is_academicleave = 0\n" +
                       "\tand is_academicleave = 0\n" + "\tand is_academicleave = 0\n" + "\tand is_get_academic = 0";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("groupname", StringType.INSTANCE)
                              .addScalar("sessionResultPrev", IntegerType.INSTANCE)
                              .addScalar("prolongationEndDate", DateType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentToCreateModel.class));
        return (List<StudentToCreateModel>) getList(q);
    }

    /******************** КОНЕЦ АКАДЕМИЧЕСКИХ ПРИКАЗОВ ***************************/

    /******************** СОЦИАЛЬНЫЕ ПОВЫШЕННЫЕ ПРИКАЗЫ **************************/

    public List<StudentWithReference> getStudentsWithReferenceFromSocialIncreasedOrder (Long idOrder) {
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

    public List<StudentWithReference> getStudentsWithReferenceFromSocialIncreasedOrder (Long idOrder, List<StudentToAddModel> listToAdd) {
        String query =
                "SELECT\n" + "\tSC.id_studentcard AS idStudentcard,\n" + "\tMAX(RF.id_reference) AS idReference, MAX(RF.url) AS url,\n" +
                "\tname, family, patronymic\n" + "from \n" + "\tstudent_semester_status sss\n" +
                "\tinner join studentcard sc using(id_studentcard)\n" + "\tINNER JOIN reference RF USING (id_studentcard)\n" +
                "\tinner join humanface hf using(id_humanface)\n" +
                "\tinner join link_order_student_status lgss using(id_student_semester_status)\n" +
                "\tinner join link_order_section los using(id_link_order_section)\n" + "\tinner join order_head oh using(id_order_head)\n" +
                "where \n" + "\tid_order_head = " + idOrder + " and id_order_section in (59,61)\n" +
                "\tand id_student_semester_status in (" + getIdStrByList(listToAdd) + ")\n" + "GROUP BY\n" +
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

    public List<StudentToCreateModel> getStudentsForSocialIncreasedOrderNewReference (Date firstDate, Date secondDate, long idSemester) {
        String strFirstDate = new SimpleDateFormat("yyyy-MM-dd").format(firstDate);
        String strSecondDate = new SimpleDateFormat("yyyy-MM-dd").format(secondDate);
        String query = "select\n" + "\tsss.id_student_semester_status as id,\n" + "\tsss.id_studentcard as idStudentcard,\n" +
                       "\tdg.groupname,\n" + "\tsss.sessionresult as sessionResult,\n" + "\tsc.is_sirota as sirota,\n" +
                       "\tsc.is_invalid as invalid,\n" + "\tlgs.semesternumber as semesternumber,\n" +
                       "\tsc.type_invalid as typeInvalid,\n" + "\tcur.periodofstudy as periodOfStudy,\n" +
                       "\tscy.dateofbegin as dateOfBeginSchoolYear,\n" + "\t(\n" +
                       "\t\tselect sessionresult as sessionresult2 from student_semester_status sss2 \n" +
                       "\t\tinner join link_group_semester lgs2 ON lgs2.id_link_group_semester = sss2.id_link_group_semester\n" +
                       "\t\twhere lgs2.id_dic_group = lgs.id_dic_group and lgs2.id_semester = " + idSemester +
                       " and sss2.id_studentcard = sss.id_studentcard\n" + "\t) as sessionResultPrev,\n" + "        (\n" +
                       "\t\tselect dateofendsession from student_semester_status sss2\n" +
                       "\t\tinner join link_group_semester lgs2 ON sss2.id_link_group_semester = lgs2.id_link_group_semester\n" +
                       "                where sss2.id_studentcard = sss.id_studentcard and sss2.is_deducted = 0 and sss2.is_academicleave = 0 and id_semester = " +
                       getNextSemester(idSemester) + " order by sss2.id_student_semester_status desc limit 1\n" +
                       "        ) as dateNextEndOfSession,\n" + "\trf.date_start as firstDate,\n" + "\trf.date_finish as secondDate,\n" +
                       "\tdateofendsession as dateOfEndSession\n" + "from student_semester_status sss\n" +
                       "inner join link_group_semester lgs on sss.id_link_group_semester = lgs.id_link_group_semester\n" +
                       "inner join semester sem on sem.id_semester = lgs.id_semester\n" +
                       "inner join schoolyear scy on scy.id_schoolyear = sem.id_schoolyear\n" +
                       "inner join dic_group dg using(id_dic_group)\n" + "inner join curriculum cur using(id_curriculum)\n" +
                       "inner join studentcard sc using(id_studentcard)\n" + "inner join humanface hf using(id_humanface)\n" +
                       "LEFT JOIN reference rf ON rf.id_studentcard = sc.id_studentcard\n" +
                       "where (rf.date_finish > now() or (rf.date_finish is null and sc.is_invalid = 1)) and rf.date_start >= '" +
                       strFirstDate + "' and rf.date_start <= '" + strSecondDate + "'\n" + "and sem.id_semester = " + idSemester + "\n" +
                       "and is_deducted = 0 \n" + "and sc.id_current_dic_group = lgs.id_dic_group\n" + "and is_academicleave = 0 \n" +
                       "and lgs.semesternumber < 4\n" + "and lgs.semesternumber >= 1\n" + "and cur.qualification in (1,2)\n" +
                       "and cur.formofstudy = 1";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("idStudentcard", LongType.INSTANCE)
                              .addScalar("groupname")
                              .addScalar("sirota", BooleanType.INSTANCE)
                              .addScalar("invalid", BooleanType.INSTANCE)
                              .addScalar("typeInvalid", IntegerType.INSTANCE)
                              .addScalar("sessionResult", IntegerType.INSTANCE)
                              .addScalar("sessionResultPrev", IntegerType.INSTANCE)
                              .addScalar("firstDate", DateType.INSTANCE)
                              .addScalar("secondDate", DateType.INSTANCE)
                              .addScalar("periodOfStudy", DoubleType.INSTANCE)
                              .addScalar("dateOfBeginSchoolYear")
                              .addScalar("dateOfEndSession", DateType.INSTANCE)
                              .addScalar("dateNextEndOfSession", DateType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentToCreateModel.class));
        return (List<StudentToCreateModel>) getList(q);
    }

    public List<StudentToCreateModel> getStudentsForSocialIncreasedOrderInSession (Date firstDate, Date secondDate, long idSemester) {
        String strFirstDate = new SimpleDateFormat("yyyy-MM-dd").format(firstDate);
        String strSecondDate = new SimpleDateFormat("yyyy-MM-dd").format(secondDate);
        String query = "select\n" + "\tsss.id_student_semester_status as id,\n" + "\tsss.id_studentcard as idStudentcard,\n" +
                       "\tdg.groupname,\n" + "\tsss.sessionresult as sessionResult,\n" + "\tlgs.semesternumber as semesternumber,\n" +
                       "\tsc.is_sirota as sirota,\n" + "\tsc.is_invalid as invalid,\n" + "\tsc.type_invalid as typeInvalid,\n" +
                       "\tcur.periodofstudy as periodOfStudy,\n" + "\tscy.dateofbegin as dateOfBeginSchoolYear,\n" + "\t(\n" +
                       "\t\tselect sessionresult as sessionresult2 from student_semester_status sss2 \n" +
                       "\t\tinner join link_group_semester lgs2 ON lgs2.id_link_group_semester = sss2.id_link_group_semester\n" +
                       "\t\twhere lgs2.id_dic_group = lgs.id_dic_group and lgs2.id_semester = " + getPrevSemester(idSemester) +
                       " and sss2.id_studentcard = sss.id_studentcard\n" + "\t) as sessionResultPrev,\n" + "\t(\n" +
                       "\t\tselect is_get_social as getSocialPrev from student_semester_status sss2 \n" +
                       "\t\tinner join link_group_semester lgs2 ON lgs2.id_link_group_semester = sss2.id_link_group_semester\n" +
                       "\t\twhere lgs2.id_dic_group = lgs.id_dic_group and lgs2.id_semester = " + idSemester +
                       " and sss2.id_studentcard = sss.id_studentcard\n" + "\t) as getSocialPrev,\n" + "\t(\n" +
                       "\t\tselect dateofendsession from link_group_semester lgs2 \n" +
                       "\t\twhere lgs2.id_dic_group = lgs.id_dic_group and id_semester = " + getNextSemester(idSemester) + "\n" +
                       "\t) as dateNextEndOfSession,\n" + "\trf.date_start as firstDate,\n" + "\trf.date_finish as secondDate,\n" +
                       "\tdateofendsession as dateOfEndSession\n" + "from student_semester_status sss\n" +
                       "inner join link_group_semester lgs on sss.id_link_group_semester = lgs.id_link_group_semester\n" +
                       "inner join dic_group dg using(id_dic_group)\n" + "inner join semester sem on sem.id_semester = lgs.id_semester\n" +
                       "inner join schoolyear scy on scy.id_schoolyear = sem.id_schoolyear\n" +
                       "inner join curriculum cur using(id_curriculum)\n" + "inner join studentcard sc using(id_studentcard)\n" +
                       "inner join humanface hf using(id_humanface)\n" +
                       "LEFT JOIN reference rf ON rf.id_studentcard = sc.id_studentcard\n" +
                       "where (rf.date_finish > now() or (rf.date_finish is null and (sc.is_sirota = 1 or sc.is_invalid = 1)))\n" +
                       "and sem.id_semester = " + idSemester + "\n" + "and date_complete_session >= '" + strFirstDate +
                       "' and date_complete_session <= '" + strSecondDate + "'\n" + "and is_deducted = 0 \n" +
                       "and sc.id_current_dic_group = lgs.id_dic_group\n" + "and is_academicleave = 0 \n" + "and lgs.semesternumber < 4\n" +
                       "and sss.sessionresult > 1\n" + "and cur.qualification in (1,2)\n" + "and cur.formofstudy = 1\n" +
                       "and id_student_semester_status not in \n" + "        (\n" +
                       "\t\tselect id_student_semester_status from link_order_student_status\n" +
                       "\t\tinner join link_order_section los using(id_link_order_section)\n" +
                       "\t\tinner join order_head oh using (id_order_head)\n" + "\t\tinner join order_rule orule using (id_order_rule)\n" +
                       "\t\twhere ((sem.id_semester = " + getPrevSemester(idSemester) +
                       " and id_order_section in (14,15,17)) or (sem.id_semester = " + idSemester +
                       " and id_order_section in (51,52,53))) and id_order_type = 1\n" + "\t)\n" +
                       "order by sss.id_studentcard, date_start";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("idStudentcard", LongType.INSTANCE)
                              .addScalar("groupname", StringType.INSTANCE)
                              .addScalar("sirota", BooleanType.INSTANCE)
                              .addScalar("invalid", BooleanType.INSTANCE)
                              .addScalar("getSocialPrev", BooleanType.INSTANCE)
                              .addScalar("typeInvalid", IntegerType.INSTANCE)
                              .addScalar("sessionResult", IntegerType.INSTANCE)
                              .addScalar("sessionResultPrev", IntegerType.INSTANCE)
                              .addScalar("periodOfStudy", DoubleType.INSTANCE)
                              .addScalar("dateOfBeginSchoolYear")
                              .addScalar("semesternumber")
                              .addScalar("firstDate")
                              .addScalar("secondDate")
                              .addScalar("dateOfEndSession", DateType.INSTANCE)
                              .addScalar("dateNextEndOfSession", DateType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentToCreateModel.class));

        return (List<StudentToCreateModel>) getList(q);
    }

    public List<StudentToCreateModel> getStudentsForSocialIncreasedOrderNotInSession (Date firstDate, Date secondDate, long idSemester) {
        String strFirstDate = new SimpleDateFormat("yyyy-MM-dd").format(firstDate);
        String strSecondDate = new SimpleDateFormat("yyyy-MM-dd").format(secondDate);
        String query =
                "select\n" + "\tsss.id_student_semester_status as id,\n" + "\tdg.groupname,\n" + "\tsss.sessionresult as sessionResult,\n" +
                "\tlgs.semesternumber as semesternumber,\n" + "\tsc.is_sirota as sirota,\n" + "\tsc.is_invalid as invalid,\n" +
                "\tsc.type_invalid as typeInvalid,\n" + "\tcur.periodofstudy as periodOfStudy,\n" +
                "\tscy.dateofbegin as dateOfBeginSchoolYear,\n" + "\t(\n" +
                "\t\tselect sessionresult as sessionresult2 from student_semester_status sss2 \n" +
                "\t\tinner join link_group_semester lgs2 ON lgs2.id_link_group_semester = sss2.id_link_group_semester\n" +
                "\t\twhere lgs2.id_dic_group = lgs.id_dic_group and lgs2.id_semester = " + idSemester +
                " and sss2.id_studentcard = sss.id_studentcard\n" + "\t) as sessionResultPrev,\n" + "\t(\n" +
                "\t\tselect dateofendsession from link_group_semester lgs2 \n" +
                "\t\twhere lgs2.id_dic_group = lgs.id_dic_group and id_semester = " + this.getNextSemester(idSemester) + "\n" +
                "\t) as dateNextEndOfSession,\n" + "\trf.date_start as firstDate,\n" + "\trf.date_finish as secondDate,\n" +
                "\tdateofendsession as dateOfEndSession\n" + "from student_semester_status sss\n" +
                "inner join link_group_semester lgs on sss.id_link_group_semester = lgs.id_link_group_semester\n" +
                "inner join semester sem on sem.id_semester = lgs.id_semester\n" +
                "inner join schoolyear scy on scy.id_schoolyear = sem.id_schoolyear\n" + "inner join dic_group dg using(id_dic_group)\n" +
                "inner join curriculum cur using(id_curriculum)\n" + "inner join studentcard sc using(id_studentcard)\n" +
                "inner join humanface hf using(id_humanface)\n" + "LEFT JOIN reference rf ON rf.id_studentcard = sc.id_studentcard\n" +
                "where (rf.date_finish > now() or (rf.date_finish is null and (sc.is_sirota = 1 or (sc.is_invalid = 1 and sc.type_invalid in (1,2)))))\n" +
                "and sem.id_semester = " + idSemester + "\n" + "and date_complete_session >= '" + strFirstDate +
                "' and date_complete_session <= '" + strSecondDate + "'\n" + "and is_deducted = 0 \n" + "and is_academicleave = 0 \n" +
                "and sc.id_current_dic_group = lgs.id_dic_group\n" + "and is_get_social_increased = 0 \n" + "and lgs.semesternumber < 4\n" +
                "and sss.sessionresult > 1\n" + "and cur.qualification in (1,2)\n" + "and cur.formofstudy = 1";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("groupname", StringType.INSTANCE)
                              .addScalar("sirota", BooleanType.INSTANCE)
                              .addScalar("invalid", BooleanType.INSTANCE)
                              .addScalar("typeInvalid", IntegerType.INSTANCE)
                              .addScalar("sessionResult", IntegerType.INSTANCE)
                              .addScalar("sessionResultPrev", IntegerType.INSTANCE)
                              .addScalar("periodOfStudy", DoubleType.INSTANCE)
                              .addScalar("dateOfBeginSchoolYear")
                              .addScalar("semesternumber")
                              .addScalar("firstDate", DateType.INSTANCE)
                              .addScalar("secondDate", DateType.INSTANCE)
                              .addScalar("dateOfEndSession", DateType.INSTANCE)
                              .addScalar("dateNextEndOfSession", DateType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentToCreateModel.class));
        return (List<StudentToCreateModel>) getList(q);
    }

    /******************** КОНЕЦ СОЦИАЛЬНЫХ ПОВЫШЕННЫХ ПРИКАЗОВ *******************/

    /******************** СОЦИАЛЬНЫЕ ПРИКАЗЫ **************************/

    public List<StudentWithReference> getStudentsWithReferenceFromSocialOrder (Long idOrder) {
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

    public List<StudentWithReference> getStudentsWithReferenceFromSocialOrder (Long idOrder, List<StudentToAddModel> listToAdd) {
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

    public List<StudentToCreateModel> getStudentsForSocialOrderInSession (Date firstDate, Date secondDate, long idSemester) {
        String strFirstDate = new SimpleDateFormat("yyyy-MM-dd").format(firstDate);
        String strSecondDate = (secondDate == null ? "null" : new SimpleDateFormat("yyyy-MM-dd").format(secondDate));

        //TODO опущена первая дата в запросе, исправить после исправления функции подсчета даты сдачи сессии
        //language=GenericSQL
        String query =
                "select\n" + "\tsss.id_student_semester_status as id,\n" + "\tdg.groupname,\n" + "\tsss.sessionresult as sessionResult,\n" +
                "\tsc.is_sirota as sirota,\n" + "\tsc.is_invalid as invalid,\n" + "\tsc.type_invalid as typeInvalid,\n" +
                "\tscy.dateofbegin as dateOfBeginSchoolYear,\n" + "\tlgs.semesternumber as semesternumber,\n" +
                "\tcur.periodofstudy as periodOfStudy,\n" + "\t(\n" +
                "\t\tselect sessionresult as sessionresult2 from student_semester_status sss2 \n" +
                "\t\tinner join link_group_semester lgs2 ON lgs2.id_link_group_semester = sss2.id_link_group_semester\n" +
                "\t\twhere lgs2.id_dic_group = lgs.id_dic_group and lgs2.id_semester = " + getPrevSemester(getPrevSemester(idSemester)) +
                " and sss2.id_studentcard = sss.id_studentcard\n" + "\t) as sessionResultPrev,\n" + "\t(\n" +
                "\t\tselect dateofendsession from link_group_semester lgs2 \n" +
                "\t\twhere lgs2.id_dic_group = lgs.id_dic_group and id_semester = " + idSemester + "\n" + "\t) as dateNextEndOfSession,\n" +
                "\trf.date_finish as secondDate,\n" + "\trf.date_start as firstDate,\n" + "\tdateofendsession as dateOfEndSession\n" +
                "from student_semester_status sss\n" +
                "inner join link_group_semester lgs on sss.id_link_group_semester = lgs.id_link_group_semester\n" +
                "inner join dic_group dg using(id_dic_group)\n" + "inner join curriculum cur using(id_curriculum)\n" +
                "inner join semester sem on sem.id_semester = lgs.id_semester\n" +
                "inner join schoolyear scy on scy.id_schoolyear = sem.id_schoolyear\n" +
                "inner join studentcard sc using(id_studentcard)\n" + "inner join humanface hf using(id_humanface)\n" +
                "LEFT JOIN reference rf ON rf.id_studentcard = sc.id_studentcard\n" +
                "where ((rf.date_finish > now() and rf.date_start < now()) or sc.is_sirota = 1 or (sc.is_invalid = 1 and rf.date_finish is null)) \n" +
                "and lgs.id_semester = " + getPrevSemester(idSemester) + "\n" + "and is_deducted = 0\n" +
                "and sc.id_current_dic_group = lgs.id_dic_group\n" + "and dg.dateofend >= now()\n" + "and cur.formofstudy = 1 \n" +
                "and dateofendsession >= '" + strFirstDate + "' and dateofendsession <= '" + strSecondDate + "'\n" + "and\n" + "((\n" +
                "lgs.semesternumber < 4\n" + "and cur.qualification in (1,2)\n" + "and sessionresult <= 1\n" + ")\n" + "or\n" + "(\n" +
                "lgs.semesternumber = 4\n" + "and cur.qualification in (1,2)\n" + ")\n" + "or cur.qualification = 3\n" + ")\n" +
                "order by sss.id_studentcard, date_start";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("groupname", StringType.INSTANCE)
                              .addScalar("sirota", BooleanType.INSTANCE)
                              .addScalar("invalid", BooleanType.INSTANCE)
                              .addScalar("typeInvalid", IntegerType.INSTANCE)
                              .addScalar("sessionResult", IntegerType.INSTANCE)
                              .addScalar("sessionResultPrev", IntegerType.INSTANCE)
                              .addScalar("semesternumber", IntegerType.INSTANCE)
                              .addScalar("periodOfStudy", DoubleType.INSTANCE)
                              .addScalar("dateOfBeginSchoolYear")
                              .addScalar("firstDate", DateType.INSTANCE)
                              .addScalar("secondDate", DateType.INSTANCE)
                              .addScalar("dateOfEndSession", DateType.INSTANCE)
                              .addScalar("dateNextEndOfSession", DateType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentToCreateModel.class));

        return (List<StudentToCreateModel>) getList(q);
    }

    public List<StudentToCreateModel> getStudentsForSocialOrderNewReference (Date firstDate, Date secondDate, long idSemester) {
        String strFirstDate = new SimpleDateFormat("yyyy-MM-dd").format(firstDate);
        String strSecondDate = (secondDate == null ? "null" : new SimpleDateFormat("yyyy-MM-dd").format(secondDate));

        //language=GenericSQL
        String query = "select\n" + "\tsss.id_student_semester_status as id,\n" +
                       "\thf.family || ' ' || hf.name || ' ' || hf.patronymic as fio,\n" + "\tdg.groupname,\n" +
                       "\tsss.sessionresult as sessionResult,\n" + "\tsc.is_sirota as sirota,\n" + "\tsc.is_invalid as invalid,\n" +
                       "\tsc.type_invalid as typeInvalid,\n" + "\tlgs.semesternumber as semesternumber,\n" +
                       "\tlgs.dateofendsession as dateOfEndSession,\n" + "\tcur.periodofstudy as periodOfStudy,\n" +
                       "\tscy.dateofbegin as dateOfBeginSchoolYear,\n" + "\t(\n" +
                       "\t\tselect sessionresult as sessionresult2 from student_semester_status sss2 \n" +
                       "\t\tinner join link_group_semester lgs2 ON lgs2.id_link_group_semester = sss2.id_link_group_semester\n" +
                       "\t\twhere lgs2.id_dic_group = lgs.id_dic_group and lgs2.id_semester = " + idSemester +
                       " and sss2.id_studentcard = sss.id_studentcard\n" + "\t) as sessionResultPrev,\n" +
                       "\trf.date_finish as secondDate,\n" + "\trf.date_start as firstDate,\n" + "cur.qualification as qualification,\n" +
                       "sss.is_transfer_student as transfer,\n" + "sss.id_studentcard as idStudentcard\n" +
                       "from student_semester_status sss\n" +
                       "inner join link_group_semester lgs on sss.id_link_group_semester = lgs.id_link_group_semester\n" +
                       "inner join semester sem on sem.id_semester = lgs.id_semester\n" +
                       "inner join schoolyear scy on scy.id_schoolyear = sem.id_schoolyear\n" +
                       "inner join dic_group dg using(id_dic_group)\n" + "inner join curriculum cur using(id_curriculum)\n" +
                       "inner join studentcard sc using(id_studentcard)\n" + "inner join humanface hf using(id_humanface)\n" +
                       "LEFT JOIN reference rf ON rf.id_studentcard = sc.id_studentcard\n" +
                       "where ((rf.date_finish > now() or (sc.is_invalid = 1 and date_finish is null)) and rf.date_start >= '" +
                       strFirstDate + "' and rf.date_start <= '" + strSecondDate + "') \n" + "and sem.id_semester = " + idSemester + "\n" +
                       "and is_deducted = 0\n" + "and sc.id_current_dic_group = lgs.id_dic_group\n" + "and\n" + "(\n" + "(\n" +
                       "cur.qualification in (1,2)\n" + ")\n" + "or\n" + "(\n" + "lgs.semesternumber <= 4\n" +
                       "and cur.qualification in (3)\n" + ")\n" + ")";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("fio", StringType.INSTANCE)
                              .addScalar("idStudentcard", LongType.INSTANCE)
                              .addScalar("groupname", StringType.INSTANCE)
                              .addScalar("sirota", BooleanType.INSTANCE)
                              .addScalar("invalid", BooleanType.INSTANCE)
                              .addScalar("transfer", BooleanType.INSTANCE)
                              .addScalar("typeInvalid", IntegerType.INSTANCE)
                              .addScalar("sessionResult", IntegerType.INSTANCE)
                              .addScalar("sessionResultPrev", IntegerType.INSTANCE)
                              .addScalar("semesternumber", IntegerType.INSTANCE)
                              .addScalar("periodOfStudy", DoubleType.INSTANCE)
                              .addScalar("dateOfBeginSchoolYear")
                              .addScalar("qualification", IntegerType.INSTANCE)
                              .addScalar("firstDate", DateType.INSTANCE)
                              .addScalar("dateOfEndSession")
                              .addScalar("secondDate", DateType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentToCreateModel.class));

        return (List<StudentToCreateModel>) getList(q);
    }

    public List<StudentToCreateModel> getListSirotsForSocial (Long idSemester) {
        String query = "select\n" + "\tsss.id_student_semester_status as id,\n" + "\tdg.groupname as groupname,\n" +
                       "\thf.birthday as birthDate,\n" + "sss.id_studentcard as idStudentcard\n" + "from student_semester_status sss\n" +
                       "inner join link_group_semester lgs on sss.id_link_group_semester = lgs.id_link_group_semester\n" +
                       "inner join dic_group dg using(id_dic_group)\n" + "inner join curriculum cur using(id_curriculum)\n" +
                       "inner join studentcard sc using(id_studentcard)\n" + "inner join humanface hf using(id_humanface)\n" +
                       "LEFT JOIN reference rf ON rf.id_studentcard = sc.id_studentcard\n" + "where sc.is_sirota = 1 and lgs.semesternumber = 1\n" +
                       "and id_semester = " + idSemester + "\n";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("idStudentcard", LongType.INSTANCE)
                              .addScalar("groupname", StringType.INSTANCE)
                              .addScalar("birthDate", DateType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentToCreateModel.class));
        return (List<StudentToCreateModel>) getList(q);
    }

    public List<StudentToCreateModel> getListFirstCourseForSocial(Long idSemester) {
        String query = "select\n" + "\tsss.id_student_semester_status as id,\n" +
                       "\thf.family || ' ' || hf.name || ' ' || hf.patronymic as fio,\n" + "\tdg.groupname,\n" +
                       "\tsss.sessionresult as sessionResult,\n" + "\tsc.is_sirota as sirota,\n" + "\tsc.is_invalid as invalid,\n" +
                       "\tsc.type_invalid as typeInvalid,\n" + "\tlgs.semesternumber as semesternumber,\n" +
                       "\tlgs.dateofendsession as dateOfEndSession,\n" + "\tcur.periodofstudy as periodOfStudy,\n" +
                       "\tscy.dateofbegin as dateOfBeginSchoolYear,\n" + "\t(\n" +
                       "\t\tselect sessionresult as sessionresult2 from student_semester_status sss2 \n" +
                       "\t\tinner join link_group_semester lgs2 ON lgs2.id_link_group_semester = sss2.id_link_group_semester\n" +
                       "\t\twhere lgs2.id_dic_group = lgs.id_dic_group and lgs2.id_semester = " + idSemester +
                       " and sss2.id_studentcard = sss.id_studentcard\n" + "\t) as sessionResultPrev,\n" +
                       "\trf.date_finish as secondDate,\n" + "\trf.date_start as firstDate,\n" + "cur.qualification as qualification,\n" +
                       "sss.is_transfer_student as transfer,\n" + "sss.id_studentcard as idStudentcard\n" +
                       "from student_semester_status sss\n" +
                       "inner join link_group_semester lgs on sss.id_link_group_semester = lgs.id_link_group_semester\n" +
                       "inner join semester sem on sem.id_semester = lgs.id_semester\n" +
                       "inner join schoolyear scy on scy.id_schoolyear = sem.id_schoolyear\n" +
                       "inner join dic_group dg using(id_dic_group)\n" + "inner join curriculum cur using(id_curriculum)\n" +
                       "inner join studentcard sc using(id_studentcard)\n" + "inner join humanface hf using(id_humanface)\n" +
                       "inner join reference rf ON rf.id_studentcard = sc.id_studentcard\n" +
                       "where lgs.id_semester = " + idSemester + " and lgs.semesternumber = 1";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("fio", StringType.INSTANCE)
                              .addScalar("idStudentcard", LongType.INSTANCE)
                              .addScalar("groupname", StringType.INSTANCE)
                              .addScalar("sirota", BooleanType.INSTANCE)
                              .addScalar("invalid", BooleanType.INSTANCE)
                              .addScalar("transfer", BooleanType.INSTANCE)
                              .addScalar("typeInvalid", IntegerType.INSTANCE)
                              .addScalar("sessionResult", IntegerType.INSTANCE)
                              .addScalar("sessionResultPrev", IntegerType.INSTANCE)
                              .addScalar("semesternumber", IntegerType.INSTANCE)
                              .addScalar("periodOfStudy", DoubleType.INSTANCE)
                              .addScalar("dateOfBeginSchoolYear")
                              .addScalar("qualification", IntegerType.INSTANCE)
                              .addScalar("firstDate", DateType.INSTANCE)
                              .addScalar("dateOfEndSession")
                              .addScalar("secondDate", DateType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentToCreateModel.class));

        return (List<StudentToCreateModel>) getList(q);
    }

    /******************** КОНЕЦ СОЦИАЛЬНЫХ ПРИКАЗОВ *******************/

    /******************** ПЕРЕВОДНЫЕ ПРИКАЗЫ **************************/

    public List<StudentToCreateModel> getStudentsForTransferOrder (long idSemester, String groups) {
        //language=GenericSQL
        String query = "select\n" + "    \n" + "\tsss.id_student_semester_status as id,\n" + "\tdg.groupname as groupname,\n" +
                       "\tsss.is_government_financed as governmentFinanced,\n" + "\tcr.periodofstudy as periodOfStudy,\n" +
                       "\tlgs.semesternumber as semesternumber\n" + "from student_semester_status sss\n" +
                       "inner join studentcard sc using(id_studentcard)\n" +
                       "inner join link_group_semester lgs using(id_link_group_semester)\n" +
                       "inner join dic_group dg using(id_dic_group)\n" + "inner join curriculum cr using(id_curriculum)\n" + "where\n" +
                       "\tid_semester = " + idSemester + "\n" + "\tand sessionresult > 0\n" + "\tand is_deducted = 0\n" +
                       "\tand is_academicleave = 0\n" + "\tand is_transfered = 0\n" + "\tand is_transfered_conditionally = 0\n" +
                       "\tand sc.id_current_dic_group = lgs.id_dic_group\n" + "\tand groupname in (" + groups + ")\n";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("groupname", StringType.INSTANCE)
                              .addScalar("semesternumber", IntegerType.INSTANCE)
                              .addScalar("periodOfStudy", DoubleType.INSTANCE)
                              .addScalar("governmentFinanced", BooleanType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentToCreateModel.class));
        return (List<StudentToCreateModel>) getList(q);
    }

    public List<StudentToCreateModel> getStudentsForTransferConditionallyOrder (long idSemester, String groups) {
        String query = "select\n" + "\tsss.id_student_semester_status as id,\n" + "\tdg.groupname as groupname,\n" +
                       "\tcr.periodofstudy as periodOfStudy,\n" + "\tsss.is_government_financed as governmentFinanced,\n" +
                       "\tlgs.semesternumber as semesternumber\n" + "from student_semester_status sss\n" +
                       "inner join studentcard sc using(id_studentcard)\n" +
                       "inner join link_group_semester lgs using(id_link_group_semester)\n" +
                       "inner join dic_group dg using(id_dic_group)\n" + "inner join curriculum cr using(id_curriculum)\n" + "where\n" +
                       "\tid_semester = " + idSemester + "\n" + "\tand sessionresult < 0\n" + "\tand is_deducted = 0\n" +
                       "\tand is_academicleave = 0\n" + "\tand is_transfered = 0\n" + "\tand is_transfered_conditionally = 0\n" +
                       "\tand sc.id_current_dic_group = lgs.id_dic_group\n" + "\tand groupname in (" + groups + ")\n";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("groupname", StringType.INSTANCE)
                              .addScalar("semesternumber", IntegerType.INSTANCE)
                              .addScalar("periodOfStudy", DoubleType.INSTANCE)
                              .addScalar("governmentFinanced", BooleanType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentToCreateModel.class));
        return (List<StudentToCreateModel>) getList(q);
    }

    public List<StudentToCreateModel> getStudentsForTransferAfterTransfer (long idSemester, String groups, boolean isRespectful) {
        String id_order_rule = "45";
        String id_order_section = "(96,97)";
        if(!isRespectful){
            id_order_rule = "25";
            id_order_section = "(75,76)";
        }

        String query = "select\n" +
                       "\tsss.id_student_semester_status as id,\n" +
                       "\tdg.groupname as groupname,\n" +
                       "\tcr.periodofstudy as periodOfStudy,\n" +
                       "\tsss.is_government_financed as governmentFinanced,\n" +
                       "\tlgs.semesternumber as semesternumber,\n" +
                       "\t(\n" +
                       "\t\tselect is_deducted from student_semester_status sss2\n" +
                       "\t\tinner join link_group_semester lgs2 using(id_link_group_semester)\n" +
                       "\t\tinner join semester sem2 using(id_semester)\n" +
                       "\t\twhere lgs2.id_dic_group = sc.id_current_dic_group and sss2.id_studentcard = sss.id_studentcard and sem2.is_current_sem = 1\n" +
                       "\t) as deductedCurSem,\n" +
                       "\t(select ordernumber from order_head\n" +
                       "inner join order_rule orru using(id_order_rule)\n" +
                       "inner join link_order_section los using(id_order_head)\n" +
                       "inner join link_order_student_status loss using(id_link_order_section)\n" +
                       "inner join student_semester_status sss2 using(id_student_semester_status)\n" +
                       "where id_order_type = 2 " +
                       " and id_order_rule = " + id_order_rule +
                       " and id_order_status_type = 3 and" +
                       " sss2.id_studentcard = sss.id_studentcard\n" +
                       "order by id_order_head desc limit 1) as prevOrderNumber," +
                       "\t(select dateofend from order_head\n" +
                       "inner join order_rule orru using(id_order_rule)\n" +
                       "inner join link_order_section los using(id_order_head)\n" +
                       "inner join link_order_student_status loss using(id_link_order_section)\n" +
                       "inner join student_semester_status sss2 using(id_student_semester_status)\n" +
                       "where id_order_type = 2 " +
                       "and id_order_rule = " + id_order_rule +
                       " and id_order_status_type = 3 " +
                       "and sss2.id_studentcard = sss.id_studentcard\n" +
                       "order by id_order_head desc limit 1) as prevOrderDateSign\n" +
                       "from student_semester_status sss\n" +
                       "inner join studentcard sc using(id_studentcard)\n" +
                       "inner join link_group_semester lgs using(id_link_group_semester)\n" +
                       "inner join dic_group dg using(id_dic_group)\n" +
                       "inner join curriculum cr using(id_curriculum)\n" + "where\n" +
                       "\tid_semester = " + getPrevSemester(getPrevSemester(idSemester)) + "\n" +
                       "\tand is_deducted = 0\n" +
                       "\tand is_academicleave = 0\n" +
                       "\tand is_transfered = 0\n" +
                       "\tand sessionresult2 > 0\n" +
                       "\tand sc.id_current_dic_group = lgs.id_dic_group\n" +
                       "\tand is_transfered_conditionally = 1\n" +
                       "\tand \n" +
                       "(select id_order_section from link_order_section\n" +
                       "inner join order_head oh using(id_order_head)\n" +
                       "inner join link_order_student_status loss using(id_link_order_section)\n" +
                       "inner join student_semester_status sss2 using(id_student_semester_status)\n" +
                       "where id_order_rule in (25,45)"+
                       " and id_order_status_type = 3 " +
                       "and sss.id_studentcard = sss2.id_studentcard\n" +
                       "order by id_order_head desc limit 1) in " + id_order_section +
                       "\tand groupname in (" + groups + ")\n";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("groupname", StringType.INSTANCE)
                              .addScalar("semesternumber", IntegerType.INSTANCE)
                              .addScalar("periodOfStudy", DoubleType.INSTANCE)
                              .addScalar("governmentFinanced", BooleanType.INSTANCE)
                              .addScalar("deductedCurSem", BooleanType.INSTANCE)
                              .addScalar("prevOrderDateSign", DateType.INSTANCE)
                              .addScalar("prevOrderNumber", StringType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentToCreateModel.class));

        return (List<StudentToCreateModel>) getList(q);
    }

    public List<StudentToCreateModel> getStudentsForTransferProlongation (long idSemester, String groups) {
        String query = "select\n" + "\tsss.id_student_semester_status as id,\n" + "\tdg.groupname as groupname,\n" +
                       "\tcr.periodofstudy as periodOfStudy,\n" + "\tsss.is_government_financed as governmentFinanced,\n" +
                       "\tlgs.semesternumber as semesternumber,\n" + "\t(select ordernumber from order_head\n" +
                       "inner join order_rule orru using(id_order_rule)\n" + "inner join link_order_section los using(id_order_head)\n" +
                       "inner join link_order_student_status loss using(id_link_order_section)\n" +
                       "inner join student_semester_status sss2 using(id_student_semester_status)\n" +
                       "where id_order_type = 2 and id_order_rule = 25 and id_order_status_type = 3 and sss2.id_studentcard = sss.id_studentcard\n" +
                       "order by id_order_head desc limit 1) as prevOrderNumber," + "\t(select dateofend from order_head\n" +
                       "inner join order_rule orru using(id_order_rule)\n" + "inner join link_order_section los using(id_order_head)\n" +
                       "inner join link_order_student_status loss using(id_link_order_section)\n" +
                       "inner join student_semester_status sss2 using(id_student_semester_status)\n" +
                       "where id_order_type = 2 and id_order_rule = 25 and id_order_status_type = 3 and sss2.id_studentcard = sss.id_studentcard\n" +
                       "order by id_order_head desc limit 1) as prevOrderDateSign,\n" + "\t(select loss.second_date from order_head\n" +
                       "inner join order_rule orru using(id_order_rule)\n" + "inner join link_order_section los using(id_order_head)\n" +
                       "inner join link_order_student_status loss using(id_link_order_section)\n" +
                       "inner join student_semester_status sss2 using(id_student_semester_status)\n" +
                       "where id_order_type = 2 and id_order_status_type = 3 and sss2.id_studentcard = sss.id_studentcard\n" +
                       "order by id_order_head desc limit 1) as prevOrderTransferTo,\n" + "\t(select loss.first_date from order_head\n" +
                       "inner join order_rule orru using(id_order_rule)\n" + "inner join link_order_section los using(id_order_head)\n" +
                       "inner join link_order_student_status loss using(id_link_order_section)\n" +
                       "inner join student_semester_status sss2 using(id_student_semester_status)\n" +
                       "where id_order_type = 2 and id_order_status_type = 3 and sss2.id_studentcard = sss.id_studentcard\n" +
                       "order by id_order_head desc limit 1) as prevOrderTransferToProl\n" + "from student_semester_status sss\n" +
                       "inner join studentcard sc using(id_studentcard)\n" +
                       "inner join link_group_semester lgs using(id_link_group_semester)\n" +
                       "inner join dic_group dg using(id_dic_group)\n" + "inner join curriculum cr using(id_curriculum)\n" + "where\n" +
                       "\tid_semester = " + getPrevSemester(idSemester) + "\n" + "\tand sessionresult2 < 0\n" + "\tand is_deducted = 0\n" +
                       "\tand is_academicleave = 0\n" + "\tand is_transfered = 0\n" + "\tand \n" +
                       "\t\t(select is_deducted from student_semester_status\n" +
                       "\t\t inner join link_group_semester using(id_link_group_semester)" + "\t\t inner join semester using(id_semester)" +
                       "\t\t where id_studentcard = sss.id_studentcard and id_dic_group = lgs.id_dic_group and is_current_sem = 1) = 0\n" +
                       "\tand \n" + "\t\t(select is_academicleave from student_semester_status\n" +
                       "\t\t inner join link_group_semester using(id_link_group_semester)" + "\t\t inner join semester using(id_semester)" +
                       "\t\t where id_studentcard = sss.id_studentcard and id_dic_group = lgs.id_dic_group and is_current_sem = 1) = 0\n" +
                       "\tand sc.id_current_dic_group = lgs.id_dic_group\n" + "\tand is_transfered_conditionally = 1\n" +
                       "\tand groupname in (" + groups + ")\n";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("groupname", StringType.INSTANCE)
                              .addScalar("semesternumber", IntegerType.INSTANCE)
                              .addScalar("periodOfStudy", DoubleType.INSTANCE)
                              .addScalar("governmentFinanced", BooleanType.INSTANCE)
                              .addScalar("prevOrderDateSign", DateType.INSTANCE)
                              .addScalar("prevOrderTransferTo", DateType.INSTANCE)
                              .addScalar("prevOrderTransferToProl", DateType.INSTANCE)
                              .addScalar("prevOrderNumber", StringType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentToCreateModel.class));
        return (List<StudentToCreateModel>) getList(q);
    }

    /************************* КОНЕЦ ПЕРЕВОДНЫХ ПРИКАЗОВ ******************/

    /********************* ПРИКАЗЫ НА УСТАНОВЛЕНИЕ СРОКОВ ЛАЗ *************/

    public List<StudentToCreateModel> getStudentsForSetEliminationAfterPassWeek (long idSemester) {
        String query = "select \n" + "\tsss.id_student_semester_status as id, \n" +
                       "\tgroupname, \n" +
                       "\tis_government_financed as governmentFinanced,\n" +
                       "\tmax(date_finish) as dateOfEndElimination, \n" +
                       "\tid_dic_action as idDicAction\n" +
                       "from student_semester_status sss\n" +
                       "inner join studentcard sc using(id_studentcard)\n" +
                       "left join order_action oa ON oa.id_studentcard = sc.id_studentcard and id_dic_action in (5,6)\n" +
                       "inner join humanface hf using(id_humanface)\n" +
                       "inner join link_group_semester lgs using(id_link_group_semester)\n" +
                       "inner join dic_group dg using(id_dic_group)\n" + "where \n" + "\tlgs.id_semester = " + idSemester + " \n" +
                       "\tand is_deducted = 0 \n" +
                       "\tand is_academicleave = 0\n" +
                       //"\tand dateofendpassweek <= now()\n" +
                       "\tand sc.id_current_dic_group = dg.id_dic_group\n" +
                       "\tand count_debts_in_pass_week(id_student_semester_status) > 0\n" +
                       "\tgroup by oa.id_studentcard, family, name, sss.id_student_semester_status, groupname, id_dic_action, is_government_financed";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("groupname", StringType.INSTANCE)
                              .addScalar("governmentFinanced", BooleanType.INSTANCE)
                              .addScalar("dateOfEndElimination", DateType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentToCreateModel.class));

        return (List<StudentToCreateModel>) getList(q);
    }

    public List<StudentToCreateModel> getStudentsForSetEliminationAfterSession (long idSemester) {
        String query = "select \n" + "\tsss.id_student_semester_status as id, \n" + "\tgroupname, \n" +
                       "\tis_government_financed as governmentFinanced\n" +
                       "from student_semester_status sss\n" + "inner join studentcard sc using(id_studentcard)\n" +
                       "inner join humanface hf using(id_humanface)\n" +
                       "inner join link_group_semester lgs using(id_link_group_semester)\n" +
                       "inner join dic_group dg using(id_dic_group)\n" + "where \n" + "\tlgs.id_semester = " + getPrevSemester(idSemester) + " \n" +
                       "\tand is_deducted = 0 \n" + "\tand is_academicleave = 0\n" + "\tand dateofendsession <= now()\n" +
                       "\tand sc.id_current_dic_group = dg.id_dic_group\n" +
                       "\tand count_debts_in_session(id_student_semester_status) > 0 " +
                       "\tand isstudentacademicleavecurrentsem(sc.id_studentcard) = 0 " +
                       "\tand isstudentdeductedcurrentsem(sc.id_studentcard) = 0 \n " +
                       "and (\n" +
                       "    CAST(sc.order_info #>> '{dateElimination," + getPrevSemester(idSemester) + ", dateTo}' AS DATE) is null or \n" +
                       "    CAST(sc.order_info #>> '{dateElimination," + getPrevSemester(idSemester) + ", dateTo}' AS DATE) < now()\n" +
                       ")\n" +
                       "\tgroup by sc.id_studentcard, family, name, sss.id_student_semester_status, groupname, is_government_financed\n";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("groupname", StringType.INSTANCE)
                              .addScalar("governmentFinanced", BooleanType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentToCreateModel.class));
        return (List<StudentToCreateModel>) getList(q);
    }

    /***************** КОНЕЦ ПРИКАЗОВ НА УСТАНОВЛЕНИЕ СРОКОВ ЛАЗ **********/

    public Long createEmptyOrder (Long idOrderRule, Date dateOfBegin, Long idSemester, Long idHumanface, String description) {
        String strDate = new SimpleDateFormat("yyyy.MM.dd").format(dateOfBegin);
        String query =
                "insert into order_head (id_order_rule, id_order_status_type, dateofbegin, semester, id_humanface, descriptionspec )\n" +
                "values  \t        (" + idOrderRule + ", 1, '" + strDate + "', " + idSemester + ", " + idHumanface + ", '" + description +
                "')\n" + "RETURNING id_order_head";

        Query q = getSession().createSQLQuery(query);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public List<ReferenceModelESO> getAllReferenceByStudentcard (Long idStudentcard) {
        String query =
                "select id_reference as id, date_get as dateGet, date_start as dateStart, date_finish as dateFinish from reference rf\n" +
                "where \n" + "id_studentcard = " + idStudentcard + " \n" + "order by id_reference desc";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("dateGet", DateType.INSTANCE)
                              .addScalar("dateStart", DateType.INSTANCE)
                              .addScalar("dateFinish", DateType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(ReferenceModelESO.class));
        return (List<ReferenceModelESO>) getList(q);
    }

    public ReferenceModelESO getPrevReferenceForStudent (Long idSSS, Date dateGet, Date dateFinish) {
        DateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String query =
                "select id_reference as id, date_get as dateGet, date_start as dateStart, date_finish as dateFinish from reference rf\n" +
                "inner join studentcard sc using (id_studentcard)\n" +
                "inner join student_semester_status sss ON sss.id_studentcard = sc.id_studentcard\n" + "where \n" +
                "id_student_semester_status = " + idSSS + " \n" + "and date_get < '" + format.format(dateGet) + "' \n" +
                "and date_finish < '" + format.format(dateFinish) + "' \n " + "order by id_reference desc limit 1";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("dateGet", DateType.INSTANCE)
                              .addScalar("dateStart", DateType.INSTANCE)
                              .addScalar("dateFinish", DateType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(ReferenceModelESO.class));

        List<?> list = getList(q);
        return list.size() == 0 ? null : (ReferenceModelESO) list.get(0);
    }

    public Long createLinkOrderSection (Long idOrderHead, Long idOrderSection, String foundation, Date firstDate, Date secondDate) {
        String strFirstDate = new SimpleDateFormat("yyyy.MM.dd").format(firstDate);
        String strSecondDate = (secondDate == null ? "" : new SimpleDateFormat("yyyy.MM.dd").format(secondDate));
        String query = "";

        if (!strSecondDate.equals("")) {
            query = "insert into link_order_section (id_order_head, id_order_section, first_date, second_date, foundation)\n" +
                    "values  \t        (" + idOrderHead + ", " + idOrderSection + ", '" + strFirstDate + "', '" + strSecondDate + "', '" +
                    foundation + "')\n" + "RETURNING id_link_order_section";
        } else {
            query = "insert into link_order_section (id_order_head, id_order_section, first_date, second_date, foundation)\n" +
                    "values  \t        (" + idOrderHead + ", " + idOrderSection + ", '" + strFirstDate + "', null, '" + foundation +
                    "')\n" + "RETURNING id_link_order_section";
        }
        Query q = getSession().createSQLQuery(query);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long createLinkOrderStudentStatus (Long idLinkOrderSection, Long idSSS, Date firstDate, Date secondDate, String groupname,
                                              String additional) {
        String strFirstDate = new SimpleDateFormat("yyyy.MM.dd").format(firstDate);
        String strSecondDate = (secondDate == null ? "" : new SimpleDateFormat("yyyy.MM.dd").format(secondDate));
        String query;

        if (!strSecondDate.equals("")) {
            query = "insert into link_order_student_status (id_link_order_section, id_student_semester_status, first_date, second_date, groupname, additional)\n" +
                    "values  \t        (" + idLinkOrderSection + ", " + idSSS + ", '" + strFirstDate + "', '" + strSecondDate + "', '" +
                    groupname + "', '" + additional + "')\n" + "RETURNING id_link_order_student_status";
        } else {
            query = "insert into link_order_student_status (id_link_order_section, id_student_semester_status, first_date, second_date, groupname,  additional)\n" +
                    "values  \t        (" + idLinkOrderSection + ", " + idSSS + ", '" + strFirstDate + "', null, '" + groupname + "', '" +
                    additional + "')\n" + "RETURNING id_link_order_student_status";
        }
        Query q = getSession().createSQLQuery(query);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long getCurrentSemester (long idInstitute, int formOfStudy) {
        String query =
                "Select id_semester as idSemester from semester where id_institute = " + idInstitute + " and formofstudy = " + formOfStudy +
                " and is_current_sem = 1 limit 1";
        Query q = getSession().createSQLQuery(query);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long getPrevSemester (long idSemester) {
        String query =
                "select id_semester from semester where " + " id_institute = (select id_institute from semester where id_semester = " +
                idSemester + ")" + " and formofstudy = (select formofstudy from semester where id_semester = " + idSemester + ")" +
                " and id_semester < " + idSemester + " order by id_semester desc limit 1";

        Query q = getSession().createSQLQuery(query);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long getNextSemester (long idSemester) {
        String query =
                "select id_semester from semester where " + " id_institute = (select id_institute from semester where id_semester = " +
                idSemester + ")" + " and formofstudy = (select formofstudy from semester where id_semester = " + idSemester + ")" +
                " and id_semester > " + idSemester + " order by id_semester limit 1";

        Query q = getSession().createSQLQuery(query);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public boolean updateOrderModel (OrderModel orderModel) {
        String query = "UPDATE order_head SET order_url = :orderUrl WHERE id_order_head = :idOrder";
        Query q = getSession().createSQLQuery(query);
        q.setString("orderUrl", orderModel.getUrl()).setLong("idOrder", orderModel.getIdOrder());
        return executeUpdate(q);
    }

    public String getIdStrByList (List<StudentToAddModel> listToAdd) {
        if (listToAdd.size() == 0) {
            return "";
        }

        String result = "";

        for (int i = 0; i < listToAdd.size(); i++) {
            result += listToAdd.get(i).getId();

            if (i != listToAdd.size() - 1) {
                result += ", ";
            }
        }

        return result;
    }

    public List<SemesterModel> getSemesterByInstitute (Long idInstitute, int formOfStudy) {
        String query = "select\n" + "\tid_semester as id,\n" + "\tseason as season,\n" + "\tdateofbegin as dateOfBeginYear,\n" +
                       "\tdateofend as dateOfEndYear,\n" + "\tis_current_sem as currentSemester\n" + "from\n" + "\tsemester\n" +
                       "\tinner join schoolyear using(id_schoolyear)\n" + "where id_institute = " + idInstitute + " and formofstudy = " +
                       formOfStudy + " order by id_semester desc";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("season")
                              .addScalar("dateOfBeginYear", DateType.INSTANCE)
                              .addScalar("dateOfEndYear", DateType.INSTANCE)
                              .addScalar("currentSemester", BooleanType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(SemesterModel.class));
        return (List<SemesterModel>) getList(q);
    }

    public void changeSectionForStudentInTransfer (Long idLgss, Date dateProlongation) {
        String strDate = new SimpleDateFormat("yyyy.MM.dd").format(dateProlongation);
        String query = "select changeSectionForStudentInTransfer(" + idLgss + ", '" + strDate + "')";
        getList(getSession().createSQLQuery(query));
    }

    public void updateFirstDateStudent (Long idLgss, Date firstDate) {
        String strDate = new SimpleDateFormat("yyyy.MM.dd").format(firstDate);
        String query =
                "update link_order_student_status set first_date = '" + strDate + "'" + " where id_link_order_student_status = " + idLgss;
        executeUpdate(getSession().createSQLQuery(query));
    }
}
