package org.edec.newOrder.manager;

import org.edec.dao.DAO;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.order.model.StudentToCreateModel;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.constants.SemesterConst;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StudentsGetterForCreationManager extends DAO {
    protected CreateOrderManagerESO managerESO = new CreateOrderManagerESO();

    public List<OrderCreateStudentModel> getStudentForProlongationAfterSetElimination(Long idSemester, boolean governmentFinanced) {
        String query = "select\n" + "\tsss.id_student_semester_status as id,\n" + "\tdg.groupname as groupname,\n" +
                "\tsss.is_government_financed as governmentFinanced\n" + "from \n" + "\tstudent_semester_status sss\n" +
                "\tinner join studentcard sc using(id_studentcard)\n" +
                "\tinner join link_group_semester lgs using(id_link_group_semester)\n" +
                "\tinner join dic_group dg using(id_dic_group)\n" + "\tinner join curriculum cr using(id_curriculum)\n" + "\t\n" +
                "where\n" + "\tid_semester = " + idSemester + "\n" + "\tand sessionresult2 < 0\n" + "\tand is_deducted = 0\n" +
                "\tand is_academicleave = 0\n" + "\tand is_transfered = 0\n" + "\tand \n" +
                "\t\t(select is_deducted from student_semester_status\n" +
                "\t\t inner join link_group_semester using(id_link_group_semester)\t\t \n" +
                "\t\t inner join semester using(id_semester)\t\t \n" + "\t\t where id_studentcard = sss.id_studentcard \n" +
                "\t\t and id_dic_group = lgs.id_dic_group and is_current_sem = 1\n" + "\t\t ) = 0\n" + "\tand \n" +
                "\t\t(select is_academicleave from student_semester_status\n" +
                "\t\t inner join link_group_semester using(id_link_group_semester)\t\t \n" +
                "\t\t inner join semester using(id_semester)\t\t \n" +
                "\t\t where id_studentcard = sss.id_studentcard and id_dic_group = lgs.id_dic_group \n" +
                "\t\t and is_current_sem = 1\n" + "\t\t ) = 0\n" + "\tand \n" +
                "\t\t(select loss2.first_date from link_order_student_status loss2 \n" +
                "\t\t inner join link_order_section los using(id_link_order_section)\n" +
                "\t\t inner join order_head oh using(id_order_head)\n" + "\t\t where id_order_rule in (32,33) and attr1 = '" +
                idSemester + "' \n" + "\t\t and loss2.id_student_semester_status = sss.id_student_semester_status\n" +
                "\t\t) < now()\n" + "\tand \n" + "\t\t(select loss2.first_date from link_order_student_status loss2 \n" +
                "\t\t inner join link_order_section los using(id_link_order_section)\n" +
                "\t\t inner join order_head oh using(id_order_head)\n" + "\t\t where id_order_rule = 34 and attr1 = '" + idSemester +
                "'\n" + "\t\t and loss2.id_student_semester_status = sss.id_student_semester_status\n" + "\t\t) is null\n" +
                "\tand sc.id_current_dic_group = lgs.id_dic_group\n" + "\tand sss.is_government_financed = " +
                (governmentFinanced ? 1 : 0) + "\n";
        //"\tand groupname in (" + idsGroup + ")";

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("groupname", StringType.INSTANCE)
                .addScalar("governmentFinanced", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));
        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentForCancelAcademicalScholarshipInSession(Long idSemester,
                                                                                           Date dateMarksFrom,
                                                                                           Date dateMarksTo,
                                                                                           String season) {
        String firstDateStr = DateConverter.convertDateToStringByFormat(dateMarksFrom, "yyyy-MM-dd");
        String secondDateStr = DateConverter.convertDateToStringByFormat(dateMarksTo, "yyyy-MM-dd");

        //language=SQL
        String query = "select sss.id_student_semester_status                                      as id,\n" +
                "\tfamily,\n" +
                "\tname,\n" +
                "\tgroupname,\n" +
                "\tsrh.newrating as rating,\n" +
                "\tsss.is_sessionprolongation as isSessionProlongation,\n" +
                " CAST(sc.order_info -> 'academicalScholarship' ->> 'dateTo' as date) as firstDate\n" + "\n" +
                "from student_semester_status sss\n" +
                "       join studentcard sc using (id_studentcard)\n" +
                "       join humanface hf using (id_humanface)\n" +
                "       join link_group_semester lgs using (id_link_group_semester)\n" +
                "       join dic_group dg using (id_dic_group)\n" +
                "       join sessionrating sr on sss.id_student_semester_status = sr.id_student_semester_status\n" +
                "       join sessionratinghistory srh on sr.id_sessionrating = srh.id_sessionrating\n" +
                "       join subject s2 on sr.id_subject = s2.id_subject\n" +
                "       join link_group_semester_subject lgss on (lgs.id_link_group_semester = lgss.id_link_group_semester and lgss.id_subject = s2.id_subject)\n" +
                "       join register reg on srh.id_register = reg.id_register \n" +
                //"where lgs.id_semester = " + (season.equals(SemesterConst.SPRING) ? idSemester : getPrevSemester(idSemester)) + "\n" +
                "where lgs.id_semester = " + idSemester + "\n" + // Хочется надеяться что это не сломается весной
                "  and s2.practice = false \n" +
                "  and sss.is_deducted = 0\n" +
                "  and sss.is_academicleave = 0\n" +
                "  and sc.id_current_dic_group = dg.id_dic_group\n" +
                "  and CAST(sc.order_info -> 'academicalScholarship' ->> 'dateTo' as date) > now()\n" +
                "  and srh.signdate is not null \n" +
                "  and (examdate is not null\n" +
                "           and examdate >= '" + firstDateStr + "'\n" +
                "           and examdate <= '" + secondDateStr + "'\n" +
                "    or\n" + "       passdate is not null\n" +
                "           and passdate >= '" + firstDateStr + "'\n" +
                "           and passdate <= '" + secondDateStr + "' \n" +
                "   or\n" + "       practicdate is not null\n" +
                "           and practicdate >= '" + firstDateStr + "'\n" +
                "           and practicdate <= '" + secondDateStr + "'\n" +
                "      )" +
                "order by groupname, family, name";

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("family", StringType.INSTANCE)
                .addScalar("name", StringType.INSTANCE)
                .addScalar("groupname", StringType.INSTANCE)
                .addScalar("firstDate", DateType.INSTANCE)
                .addScalar("rating", IntegerType.INSTANCE)
                .addScalar("isSessionProlongation", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentsWithSocialIncreasedScholarshipAssign() {
        String query = "SELECT sc.id_studentcard as idStudentcard, hf.family || ' ' || hf.name || ' ' || hf.patronymic as fio,\n" +
                "\tdg.groupname as groupname,\n" +
                "\tsss.sessionresult as sessionResult\n" +
                "FROM order_action oa\n" +
                "INNER JOIN link_order_student_status loss USING (id_link_order_student_status)\n" +
                "INNER JOIN link_order_section los USING (id_link_order_section)\n" +
                "INNER JOIN order_section os USING (id_order_section)\n" +
                "INNER JOIN order_rule USING (id_order_rule)\n" +
                "INNER JOIN student_semester_status sss USING (id_student_semester_status)\n" +
                "INNER JOIN link_group_semester lgs USING (id_link_group_semester)\n" +
                "INNER JOIN dic_group dg USING (id_dic_group)\n" +
                "INNER JOIN studentcard sc on sss.id_studentcard = sc.id_studentcard\n" +
                "INNER JOIN humanface hf using (id_humanface)\n" +
                "WHERE oa.id_dic_action = 4\n" +
                "AND oa.date_finish > now()" +
                "AND id_order_rule in (22,23)\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idStudentcard", LongType.INSTANCE)
                .addScalar("fio")
                .addScalar("groupname")
                .addScalar("sessionResult")
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentsWithAcademicScholarshipChanging(Date dateFrom, Date dateTo, String season) {
        //language=GenericSQL
        Long currentSem = getCurrentSemester();
        Long prevSem = getPrevSemester();

        String query = "SELECT distinct s.id_semester,\n" +
                "                sc.id_studentcard                                   as idStudentcard,\n" +
                "                hf.family || ' ' || hf.name || ' ' || hf.patronymic as fio,\n" +
                "                dg.groupname                                        as groupname,\n" +
                "                (select sss10.sessionresult2\n" + "                 from sessionratinghistory srh10\n" +
                "                          join sessionrating s10 on srh10.id_sessionrating = s10.id_sessionrating\n" +
                "                          join student_semester_status sss10\n" +
                "                               on s10.id_student_semester_status = sss10.id_student_semester_status\n" +
                "                          join studentcard sc10 on sc10.id_studentcard = sss10.id_studentcard\n" +
                "                          join link_group_semester lgs10 on sss10.id_link_group_semester = lgs10.id_link_group_semester\n" +
                "                          join link_group_semester_subject lgss10\n" +
                "                               on lgs10.id_link_group_semester = lgss10.id_link_group_semester\n" +
                "                 where lgs10.id_semester = " + (season.equals(SemesterConst.SPRING) ? currentSem : prevSem) + "\n" + "                   and srh10.retake_count = 1\n" +
                "                   and sc10.id_studentcard = sc.id_studentcard\n" +
                "                 group by sss10.id_student_semester_status, newrating\n" +
                "                 order by srh10.newrating desc\n" +
                "                 limit 1)                                           as sessionResultPrev,\n" +
                "                (select srh10.newrating\n" + "                 from sessionratinghistory srh10\n" +
                "                          join sessionrating s10 on srh10.id_sessionrating = s10.id_sessionrating\n" +
                "                          join student_semester_status sss10\n" +
                "                               on s10.id_student_semester_status = sss10.id_student_semester_status\n" +
                "                          join link_group_semester lgs10 on sss10.id_link_group_semester = lgs10.id_link_group_semester\n" +
                "                          join link_group_semester_subject lgss10\n" +
                "                               on lgs10.id_link_group_semester = lgss10.id_link_group_semester\n" +
                "                          join subject s2 on lgss10.id_subject = s2.id_subject\n" +
                "                          join semester sem2 on lgs10.id_semester = sem2.id_semester\n" +
                "                 where sem2.is_current_sem = 1\n" +
                "                   and (srh10.signdate between :dateFrom and :dateTo)\n" +
                "                   and s2.practice = true\n" + "                   and sss10.id_studentcard = sc.id_studentcard\n" +
                "                 group by sss10.id_student_semester_status, newrating\n" +
                "                 order by srh10.newrating desc\n" +
                "                 limit 1)                                           as sessionResult,\n" +
                "                (select lgs2.dateofendsession\n" + "                 from link_group_semester lgs2\n" +
                "                          inner join student_semester_status sss2\n" +
                "                                     on lgs2.id_link_group_semester = sss2.id_link_group_semester\n" +
                "                          inner join studentcard sc2 on sss2.id_studentcard = sc2.id_studentcard\n" +
                "                          inner join semester sem2 on lgs2.id_semester = sem2.id_semester\n" +
                "                 where sem2.is_current_sem = 1\n" +
                "                   and sc2.id_studentcard = sc.id_studentcard\n" +
                "                   and sc.id_current_dic_group = lgs2.id_dic_group\n" +
                "                 order by id_student_semester_status desc\n" +
                "                 limit 1)                                           as dateOfEndSession,\n" +
                "                (select sss2.id_student_semester_status\n" + "                 from link_group_semester lgs2\n" +
                "                          inner join student_semester_status sss2\n" +
                "                                     on lgs2.id_link_group_semester = sss2.id_link_group_semester\n" +
                "                          inner join studentcard sc2 on sss2.id_studentcard = sc2.id_studentcard\n" +
                "                          inner join semester sem2 on lgs2.id_semester = sem2.id_semester\n" +
                "                 where sem2.is_current_sem = 1\n" +
                "                   and sc2.id_studentcard = sc.id_studentcard\n" +
                "                   and sc.id_current_dic_group = lgs2.id_dic_group\n" +
                "                 order by id_student_semester_status desc\n" + "                 limit 1\n" +
                "                )                                                   as id,\n" + "                sc.order_info\n" +
                "FROM student_semester_status sss\n" + "         JOIN link_group_semester lgs USING (id_link_group_semester)\n" +
                "         JOIN link_group_semester_subject lgss USING (id_link_group_semester)\n" +
                "         JOIN subject subj using (id_subject)\n" +
                "         JOIN semester s ON (lgs.id_semester = s.id_semester)\n" +
                "         JOIN studentcard sc on sss.id_studentcard = sc.id_studentcard\n" +
                "         JOIN dic_group dg on sc.id_current_dic_group = dg.id_dic_group\n" +
                "         JOIN humanface hf using (id_humanface)\n" +
                "WHERE CAST(sc.order_info -> 'academicalScholarship' ->> 'dateTo' as date) > now()\n" +
                "  AND s.id_semester = " + currentSem + "\n" + "  and (select subject.practice\n" + "       from subject\n" +
                "                join link_group_semester_subject l on subject.id_subject = l.id_subject\n" +
                "                join link_group_semester lgs3 on l.id_link_group_semester = lgs3.id_link_group_semester\n" +
                "                join student_semester_status sss3 on lgs3.id_link_group_semester = sss3.id_link_group_semester\n" +
                "                join semester sem2 on lgs3.id_semester = sem2.id_semester\n" +
                "       where sss3.id_studentcard = sc.id_studentcard\n" +
                "         and sem2.is_current_sem = 1 order by subject.practice desc\n" +
                "       limit 1) = true\n" + "  and (select srh10.newrating\n" +
                "               from link_group_semester lgs10\n" +
                "               inner join dic_group dg USING (id_dic_group)\n" +
                "               inner join student_semester_status sss10 USING (id_link_group_semester)\n" +
                "               inner join sessionrating s10 USING (id_student_semester_status)\n" +
                "               inner join sessionratinghistory srh10 USING(id_sessionrating)\n" +
                "               left join register R USING (id_register)\n" +
                "               inner join subject s2 USING(id_subject)\n" +
                "               inner join dic_subject ds USING (id_dic_subject)\n" +
                "               inner join semester sem2 on lgs10.id_semester = sem2.id_semester\n" +
                "                     where sem2.is_current_sem = 1\n" +
                "                     and (srh10.signdate between :dateFrom and :dateTo)\n" +
                "                     and s2.practice = true\n" +
                "         and sss10.id_studentcard = sc.id_studentcard\n" +
                "       group by sss10.id_student_semester_status, newrating\n" +
                "       order by srh10.newrating desc\n" +
                "       limit 1) is not null";

//        String query = "SELECT distinct s.id_semester,\n" +
//                       "       sc.id_studentcard                                   as idStudentcard,\n" +
//                       "       hf.family || ' ' || hf.name || ' ' || hf.patronymic as fio,\n" +
//                       "       dg.groupname                                        as groupname,\n" +
//                       "       (select sss10.sessionresult\n" +
//                       "        from sessionratinghistory srh10\n" +
//                       "                 join sessionrating s10 on srh10.id_sessionrating = s10.id_sessionrating\n" +
//                       "                 join student_semester_status sss10 on s10.id_student_semester_status = sss10.id_student_semester_status\n" +
//                       "                 join studentcard sc10 on sc10.id_studentcard = sss10.id_studentcard\n" +
//                       "                 join link_group_semester lgs10 on sss10.id_link_group_semester = lgs10.id_link_group_semester\n" +
//                       "                 join link_group_semester_subject lgss10 on lgs10.id_link_group_semester = lgss10.id_link_group_semester\n" +
//                       "        where lgs10.id_semester = " + prevSem + "\n" +
//                       "          and srh10.retake_count = 1\n" +
//                       "          and sc10.id_studentcard = sc.id_studentcard\n" +
//                       "        group by sss10.id_student_semester_status, newrating\n" + "        order by srh10.newrating desc\n" +
//                       "        limit 1)                                           as sessionResultPrev,\n" +
//                       "       (select srh10.newrating\n" + "        from sessionratinghistory srh10\n" +
//                       "                 join sessionrating s10 on srh10.id_sessionrating = s10.id_sessionrating\n" +
//                       "                 join student_semester_status sss10 on s10.id_student_semester_status = sss10.id_student_semester_status\n" +
//                       "                 join link_group_semester lgs10 on sss10.id_link_group_semester = lgs10.id_link_group_semester\n" +
//                       "                 join link_group_semester_subject lgss10 on lgs10.id_link_group_semester = lgss10.id_link_group_semester\n" +
//                       "        where lgs10.id_semester = " + (season.equals(SemesterConst.SPRING) ? currentSem : prevSem) + "\n" +
//                       "          and (srh10.signdate between :dateFrom and :dateTo)\n" +
//                       "          and srh10.retake_count = 1\n" +
//                       "          and sss10.id_student_semester_status = sss.id_student_semester_status\n" +
//                       "        group by sss10.id_student_semester_status, newrating\n" + "        order by srh10.newrating desc\n" +
//                       "        limit 1)                                           as sessionResult,\n" +
//                       "       (select lgs2.dateofendsession\n" + "        from link_group_semester lgs2\n" +
//                       "                 inner join student_semester_status sss2\n" +
//                       "                            on lgs2.id_link_group_semester = sss2.id_link_group_semester\n" +
//                       "                 inner join studentcard sc2 on sss2.id_studentcard = sc2.id_studentcard\n" +
//                       "                 inner join semester sem2 on lgs2.id_semester = sem2.id_semester\n" +
//                       "        where sem2.is_current_sem = 1\n" + "          and sc2.id_studentcard = sc.id_studentcard\n" +
//                       "          and sc.id_current_dic_group = lgs2.id_dic_group\n" +
//                       "        order by id_student_semester_status desc\n" +
//                       "        limit 1)                                           as dateOfEndSession,\n" +
//                       "       (select sss2.id_student_semester_status\n" + "        from link_group_semester lgs2\n" +
//                       "                 inner join student_semester_status sss2\n" +
//                       "                            on lgs2.id_link_group_semester = sss2.id_link_group_semester\n" +
//                       "                 inner join studentcard sc2 on sss2.id_studentcard = sc2.id_studentcard\n" +
//                       "                 inner join semester sem2 on lgs2.id_semester = sem2.id_semester\n" +
//                       "        where sem2.is_current_sem = 1\n" + "          and sc2.id_studentcard = sc.id_studentcard\n" +
//                       "          and sc.id_current_dic_group = lgs2.id_dic_group\n" +
//                       "        order by id_student_semester_status desc\n" + "        limit 1\n" +
//                       "       )                                                   as id,\n" + "       sc.order_info\n" +
//                       "FROM student_semester_status sss\n" + "         JOIN link_group_semester lgs USING (id_link_group_semester)\n" +
//                       "         JOIN link_group_semester_subject lgss USING (id_link_group_semester)\n" +
//                       "         JOIN subject subj using (id_subject)\n" +
//                       "         JOIN semester s ON (lgs.id_semester = s.id_semester)\n" +
//                       "         JOIN studentcard sc on sss.id_studentcard = sc.id_studentcard\n" +
//                       "         JOIN dic_group dg on sc.id_current_dic_group = dg.id_dic_group\n" +
//                       "         JOIN humanface hf using (id_humanface)\n" +
//                       "WHERE CAST(sc.order_info -> 'academicalScholarship' ->> 'dateTo' as date) > now()\n" +
//                       "  and s.id_semester = " + currentSem + "\n" +
//                       "  and (select subject.practice from subject\n" +
//                       "       join link_group_semester_subject l on subject.id_subject = l.id_subject\n" +
//                       "       join link_group_semester lgs3 on l.id_link_group_semester = lgs3.id_link_group_semester\n" +
//                       "       join student_semester_status sss3 on lgs3.id_link_group_semester = sss3.id_link_group_semester\n" +
//                       "       where sss3.id_studentcard = sc.id_studentcard and lgs3.id_semester = " + prevSem + " limit 1) = true";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idStudentcard", LongType.INSTANCE)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("fio")
                .addScalar("groupname")
                .addScalar("sessionResult")
                .addScalar("sessionResultPrev")
                .addScalar("dateOfEndSession")
                .setDate("dateFrom", dateFrom)
                .setDate("dateTo", dateTo)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

//        System.out.println(q);

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentsForSetEliminationAfterTransferProlongation(long idSemester, String groups, boolean respectfull) {
        String query = "select\n" + "\tsss.id_student_semester_status as id,\n" + "\tdgCurrent.groupname as groupname,\n" +
                "\tcr.periodofstudy as periodOfStudy,\n" + "\tsss.is_government_financed as governmentFinanced,\n" +
                "\tlgs.semesternumber as semesternumber, getCurrentSemesterForStudent(id_studentcard) as curSemester,\n" +
                "\t(select ordernumber from order_head\n" + "inner join order_rule orru using(id_order_rule)\n" +
                "inner join link_order_section los using(id_order_head)\n" +
                "inner join link_order_student_status loss using(id_link_order_section)\n" +
                "inner join student_semester_status sss2 using(id_student_semester_status)\n" +
                "where id_order_type = 2 and id_order_rule = 25 and id_order_status_type = 3 and sss2.id_studentcard = sss.id_studentcard\n" +
                "order by id_order_head desc limit 1) as prevOrderNumber,\t(select dateofend from order_head\n" +
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
                "inner join dic_group dg using(id_dic_group)\n" + "inner join curriculum cr using(id_curriculum)\n" +
                "inner join dic_group dgCurrent ON sc.id_current_dic_group = dgCurrent.id_dic_group\n" + "where\n" +
                "\tid_semester = " + managerESO.getPrevSemester(idSemester) + "\n" + "\tand sessionresult2 < 0\n" +
                "\tand is_deducted = 0\n" + "\tand is_academicleave = 0\n" + "\tand is_transfered = 0\n" + "\tand \n" +
                "\t\t(select is_deducted from student_semester_status\n" +
                "\t\t inner join link_group_semester using(id_link_group_semester)\t\t \n" + "      \n" +
                "      inner join semester using(id_semester)\t\t \n" +
                "      where id_studentcard = sss.id_studentcard and id_dic_group = dgCurrent.id_dic_group and is_current_sem = 1) = 0\n" +
                "\tand \n" + "\t\t(select is_academicleave from student_semester_status\n" +
                "\t\t inner join link_group_semester using(id_link_group_semester)\t\t inner join semester using(id_semester)\t\t where id_studentcard = sss.id_studentcard and id_dic_group = dgCurrent.id_dic_group and is_current_sem = 1) = 0\n" +
                "\t\n" + "\tand is_transfered_conditionally = 1 and is_transfered = 0 and isGroupSameFosAsSemester(dgCurrent.id_dic_group, id_semester)\n" +
                "  and id_student_semester_status in (\n" +
                "    select id_student_semester_status from link_order_student_status\n" +
                "      inner join link_order_section los on link_order_student_status.id_link_order_section = los.id_link_order_section\n" +
                "      inner join order_head o on los.id_order_head = o.id_order_head\n" + "    where id_order_rule = " + (respectfull ? 45 : 25) + "\n" +
                "  )\n" + "\tand (dg.groupname in (" + groups + ") or dgCurrent.groupname in (" + groups + "))\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("groupname", StringType.INSTANCE)
                .addScalar("semesternumber", IntegerType.INSTANCE)
                .addScalar("curSemester", IntegerType.INSTANCE)
                .addScalar("periodOfStudy", DoubleType.INSTANCE)
                .addScalar("governmentFinanced", BooleanType.INSTANCE)
                .addScalar("prevOrderDateSign", DateType.INSTANCE)
                .addScalar("prevOrderTransferTo", DateType.INSTANCE)
                .addScalar("prevOrderTransferToProl", DateType.INSTANCE)
                .addScalar("prevOrderNumber", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));
        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentsForSetEliminationAfterPractice(Date dateFrom,
                                                                                   Date dateTo,
                                                                                   Integer course,
                                                                                   Boolean isMaster) {
        //language=GenericSQL
        String query = "select student_semester_status.id_student_semester_status as id, \n" +
                "     (select d.groupname from student_semester_status\n" +
                "                 join link_group_semester lgs2\n" +
                "                      on student_semester_status.id_link_group_semester = lgs2.id_link_group_semester\n" +
                "                 join dic_group d on lgs2.id_dic_group = d.id_dic_group\n" +
                "        where student_semester_status.id_studentcard = sc.id_studentcard\n" +
                "          and lgs2.id_semester = " + getCurrentSemester() + "\n" +
                "        order by student_semester_status.id_studentcard desc limit 1)\n" +
                "       as groupname,\n" +
                "       subjectname as practicName,\n" +
                "       practic_type as practicType,\n" +
                "       is_government_financed as governmentFinanced,\n" +
                "       lgs.dateofbeginpractic as datePracticFrom,\n" +
                "       lgs.dateofendpractic as datePracticTo\n" +
                "       from student_semester_status" +
                "         join studentcard sc on student_semester_status.id_studentcard = sc.id_studentcard\n" +
                "         join link_group_semester lgs on lgs.id_link_group_semester = student_semester_status.id_link_group_semester\n" +
                "         join link_group_semester_subject lgss on lgs.id_link_group_semester = lgss.id_link_group_semester\n" +
                "         join sessionrating s on student_semester_status.id_student_semester_status = s.id_student_semester_status\n" +
                "         join sessionratinghistory s2 on s.id_sessionrating = s2.id_sessionrating\n" +
                "         join dic_group dg on dg.id_dic_group = lgs.id_dic_group\n" +
                "         join curriculum cur on dg.id_curriculum = cur.id_curriculum\n" +
                "         join subject s3 on lgss.id_subject = s3.id_subject\n" +
                "         join dic_subject ds on s3.id_dic_subject = ds.id_dic_subject\n" +
                "where s3.practice is true\n" +
                "  and lgs.course = " + course + "\n" +
                "  and lgs.id_semester  = " + (getCurrentSeason().equals(SemesterConst.SPRING) ? getCurrentSemester() : getPrevSemester()) + "\n" +
                "  and s2.signdate >= '" + DateConverter.convertDateToStringByFormat(dateFrom, "yyyy-MM-dd") + "'\n" +
                "  and s2.signdate <= '" + DateConverter.convertDateToStringByFormat(dateTo, "yyyy-MM-dd") + "'\n" +
                "  and lgss.passdate >= '" + DateConverter.convertDateToStringByFormat(dateFrom, "yyyy-MM-dd") + "'" + "\n" +
                "  and lgs.dateofendpractic <= now()" + "\n" +
                "  and cur.qualification in (" + (isMaster ? "3" : "1,2") + ")\n" +
                "  and  (newrating < 1 or newrating = 2) \n" +
                "   and (select sss.is_deducted\n" +
                "       from student_semester_status sss\n" +
                "                join link_group_semester l on l.id_link_group_semester = sss.id_link_group_semester\n" +
                "       where sss.id_studentcard = sc.id_studentcard\n" +
                "         and l.id_semester = " + (getCurrentSeason().equals(SemesterConst.SPRING) ? getCurrentSemester() : getPrevSemester()) + "\n" +
                "  order by sss.id_studentcard desc limit 1) <> 1 \n" +
                "  and student_semester_status.is_academicleave <> 1\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("groupname", StringType.INSTANCE)
                .addScalar("datePracticFrom")
                .addScalar("datePracticTo")
                .addScalar("practicName")
                .addScalar("practicType")
                .addScalar("governmentFinanced", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        //System.out.println(q);

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentsForSocialNewReference(Date firstDate, Date secondDate) {
        //language=GenericSQL
        String query = "select\n" +
                "       getcurrentsemesterforstudent(s2.id_studentcard) as semesternumber,\n" +
                "       c2.qualification as qualification,\n" +
                "       r.date_start as dateReferenceFrom,\n" +
                "       r.date_finish as dateReferenceTo,\n" +
                "       r.date_get as dateReferenceApply,\n" +
                "       cast(s2.order_info->'socialScholarship'->>'dateFrom' as date) as dateSocialScholarshipFrom,\n" +
                "       cast(s2.order_info->'socialScholarship'->>'dateTo' as date) as dateSocialScholarshipTo,\n" +
                "       g.dateofend as dateOfEndEducation,\n" +
                "       sss.id_student_semester_status as id,\n" +
                "       r.id_reference_subtype as refType,\n" +
                "       g.groupname as groupname,\n" +
                "       sss.sessionresult as sessionResult,\n" +
                "       g.dateofcertificationto as dateOfCertificationTo\n" +
                "from student_semester_status sss\n" +
                "       inner join studentcard s2 on sss.id_studentcard = s2.id_studentcard\n" +
                "       inner join humanface h2 on s2.id_humanface = h2.id_humanface\n" +
                "       inner join reference r on s2.id_studentcard = r.id_studentcard\n" +
                "       inner join link_group_semester semester on sss.id_link_group_semester = semester.id_link_group_semester\n" +
                "       inner join dic_group g on semester.id_dic_group = g.id_dic_group\n" +
                "       inner join curriculum c2 on g.id_curriculum = c2.id_curriculum\n" +
                "where r.date_start >= :firstDate and r.date_start <= :secondDate\n" +
                "and s2.id_current_dic_group = semester.id_dic_group " +
                "and (id_semester = " + getPrevSemester(getCurrentSemester(1L, FormOfStudy.FULL_TIME.getType())) +
                " or id_semester = " + getCurrentSemester(1L, FormOfStudy.FULL_TIME.getType()) +
                " and semesternumber = 1)";

        Query q = getSession().createSQLQuery(query)
                .addScalar("sessionResult", IntegerType.INSTANCE)
                .addScalar("semesternumber", IntegerType.INSTANCE)
                .addScalar("qualification", IntegerType.INSTANCE)
                .addScalar("dateReferenceFrom", DateType.INSTANCE)
                .addScalar("dateReferenceTo", DateType.INSTANCE)
                .addScalar("dateReferenceApply", DateType.INSTANCE)
                .addScalar("dateSocialScholarshipFrom", DateType.INSTANCE)
                .addScalar("dateSocialScholarshipTo", DateType.INSTANCE)
                .addScalar("dateOfEndEducation", DateType.INSTANCE)
                .addScalar("dateOfCertificationTo", DateType.INSTANCE)
                .addScalar("refType", IntegerType.INSTANCE)
                .addScalar("groupname", StringType.INSTANCE)
                .addScalar("id", LongType.INSTANCE)
                .setDate("firstDate", firstDate)
                .setDate("secondDate", secondDate)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentsForSocialIncreasedNewReference(Date firstDate, Date secondDate) {
        //language=GenericSQL
        String query = "select \n" +
                "       getcurrentsemesterforstudent(s2.id_studentcard) as semesternumber,\n" +
                "(select periodofstudy from curriculum cur\n" +
                "     inner join dic_group g2 on cur.id_curriculum = g2.id_curriculum\n" +
                "     where g2.id_dic_group = g.id_dic_group) as periodOfStudy," +
                "       c2.qualification as qualification,\n" +
                "       r.date_start as dateReferenceFrom,\n" +
                "       r.date_finish as dateReferenceTo,\n" +
                "       r.date_get as dateReferenceApply,\n" +
                "       cast (s2.order_info->'socialIncreasedScholarship'->>'dateFrom' as date) as dateSocialScholarshipFrom,\n" +
                "       cast (s2.order_info->'socialIncreasedScholarship'->>'dateTo' as date) as dateSocialScholarshipTo,\n" +
                "       g.dateofend as dateOfEndEducation,\n" +
                "       sss.id_student_semester_status as id,\n" +
                "       g.groupname as groupname,\n" +
                "       getdateofendcurrentsessionbystudent(s2.id_studentcard) as dateOfEndSession,\n" +
                "       sss.sessionresult as sessionResult\n" +
                "       from student_semester_status sss\n" +
                "       inner join studentcard s2 on sss.id_studentcard = s2.id_studentcard\n" +
                "       inner join humanface h2 on s2.id_humanface = h2.id_humanface\n" +
                "       inner join reference r on s2.id_studentcard = r.id_studentcard\n" +
                "       inner join link_group_semester semester on sss.id_link_group_semester = semester.id_link_group_semester\n" +
                "       inner join dic_group g on semester.id_dic_group = g.id_dic_group\n" +
                "       inner join curriculum c2 on g.id_curriculum = c2.id_curriculum\n" +
                "  where r.date_start >= :firstDate\n" +
                "  and r.date_start <= :secondDate\n" +
                "  and s2.id_current_dic_group = semester.id_dic_group " +
                "and id_semester = " + getPrevSemester() + "\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("sessionResult", IntegerType.INSTANCE)
                .addScalar("semesternumber", IntegerType.INSTANCE)
                .addScalar("qualification", IntegerType.INSTANCE)
                .addScalar("dateReferenceFrom", DateType.INSTANCE)
                .addScalar("dateReferenceTo", DateType.INSTANCE)
                .addScalar("dateReferenceApply", DateType.INSTANCE)
                .addScalar("dateSocialScholarshipFrom", DateType.INSTANCE)
                .addScalar("dateSocialScholarshipTo", DateType.INSTANCE)
                .addScalar("dateOfEndEducation", DateType.INSTANCE)
                .addScalar("dateOfEndSession", DateType.INSTANCE)
                .addScalar("groupname", StringType.INSTANCE)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("periodOfStudy", DoubleType.INSTANCE)
                .setDate("firstDate", firstDate)
                .setDate("secondDate", secondDate)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentsForSocialIncreasedAfterSession(Date firstDate, Date secondDate, String season) {
        //language=GenericSQL
        String query = "select distinct getcurrentsemesterforstudent(s2.id_studentcard)                 as semesternumber,\n" +
                "       (select periodofstudy\n" + "        from curriculum cur\n" +
                "                 inner join dic_group g2 on cur.id_curriculum = g2.id_curriculum\n" +
                "        where g2.id_dic_group = g.id_dic_group)                        as periodOfStudy,\n" +
                "       c2.qualification                                                as qualification,\n" +
                "       s2.is_sirota                                                    as sirota,\n" +
                "       s2.is_indigent                                                  as indigent,\n" +
                "       s2.is_invalid                                                   as invalid,\n" +
                "       s2.is_veteran                                                   as veteran,\n" +
                "       r.date_finish                                                   as dateReferenceTo,\n" +
                "       g.dateofend                                                     as dateOfEndEducation,\n" +
                "       sss.id_student_semester_status                                  as id,\n" +
                "       g.groupname                                                     as groupname,\n" +
                "       (select lgs.dateofendsession from link_group_semester lgs where lgs.id_semester = " + (season.equals(SemesterConst.SPRING) ? getNextSemester() : getCurrentSemester()) +
                " and lgs.id_dic_group = s2.id_current_dic_group)         " +
                "       as dateOfEndSession,\n" +
                "       sss.date_complete_session                                       as dateCompleteSession,\n" +
                "       cast(s2.order_info -> 'socialScholarship' ->> 'dateTo' as date) as dateSocialScholarshipTo,\n" +
                "       sss.sessionresult                                               as sessionResult,\n" +
                "       semester.dateofendsemester                                      as dateOfEndSemester\n" +
                "from student_semester_status sss\n" +
                "         inner join sessionrating sr on sss.id_student_semester_status = sr.id_student_semester_status\n" +
                "         inner join sessionratinghistory srh on srh.id_sessionrating = sr.id_sessionrating\n" +
                "         inner join register reg on srh.id_register = reg.id_register\n" +
                "         inner join studentcard s2 on sss.id_studentcard = s2.id_studentcard\n" +
                "         inner join humanface h2 on s2.id_humanface = h2.id_humanface\n" +
                "         left join reference r on r.id_reference =\n" +
                "                                  (select max(id_reference) from reference where id_studentcard = s2.id_studentcard)\n" +
                "         inner join link_group_semester semester on sss.id_link_group_semester = semester.id_link_group_semester\n" +
                "         inner join dic_group g on semester.id_dic_group = g.id_dic_group\n" +
                "         inner join curriculum c2 on g.id_curriculum = c2.id_curriculum\n" +
                "where (sss.date_complete_session >= :firstDate \n" +
                "           and sss.date_complete_session <= :secondDate or\n" +
                "       begindate >= :firstDate and enddate <= :secondDate)\n" +
                "  and ((r.date_finish is null and (s2.is_sirota = 1 or s2.is_invalid = 1)) or r.date_finish > now())\n" +
                "  and s2.id_current_dic_group = semester.id_dic_group\n" +
                "  and is_deducted = 0\n" +
                "  and is_academicleave = 0\n" +
                "  and isstudentdeductedcurrentsem(s2.id_studentcard) = 0\n" +
                "  and isstudentacademicleavecurrentsem(s2.id_studentcard) = 0\n" +
                "  and sessionresult > 1\n" +
                "  and semester.id_semester = " + ((season.equals(SemesterConst.SPRING)) ? getCurrentSemester() : getPrevSemester()) + "\n" +
                "  and (s2.order_info -> 'socialIncreasedScholarship' ->> 'dateTo' is null\n" +
                "    or CAST(s2.order_info -> 'socialIncreasedScholarship' ->> 'dateTo' as date) < now()\n" +
                "    or (CAST(s2.order_info -> 'socialIncreasedScholarship' ->> 'dateTo' as date) < (now() + interval '1 month')\n" +
                "    or (CAST(s2.order_info -> 'socialIncreasedScholarship' ->> 'dateTo' as date) > now()\n" +
                "        and s2.order_info -> 'socialIncreasedScholarship' ->> 'isProlongation' is not null)))";

        Query q = getSession().createSQLQuery(query)
                .addScalar("sirota", BooleanType.INSTANCE)
                .addScalar("invalid", BooleanType.INSTANCE)
                .addScalar("indigent", BooleanType.INSTANCE)
                .addScalar("veteran", BooleanType.INSTANCE)
                .addScalar("sessionResult", IntegerType.INSTANCE)
                .addScalar("semesternumber", IntegerType.INSTANCE)
                .addScalar("qualification", IntegerType.INSTANCE)
                .addScalar("dateReferenceTo", DateType.INSTANCE)
                .addScalar("dateSocialScholarshipTo", DateType.INSTANCE)
                .addScalar("dateCompleteSession", DateType.INSTANCE)
                .addScalar("dateOfEndEducation", DateType.INSTANCE)
                .addScalar("dateOfEndSession", DateType.INSTANCE)
                .addScalar("groupname", StringType.INSTANCE)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("periodOfStudy", DoubleType.INSTANCE)
                .addScalar("dateOfEndSemester", DateType.INSTANCE)
                .setDate("firstDate", firstDate)
                .setDate("secondDate", secondDate)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentsForSocialInSession(Date firstDate, Date secondDate, Long idSemester, String season) {

        //language=GenericSQL
        String query = "select \n" +
                "       getcurrentsemesterforstudent(s2.id_studentcard) as semesternumber,\n" +
                "(select periodofstudy from curriculum cur\n" +
                "     inner join dic_group g2 on cur.id_curriculum = g2.id_curriculum\n" +
                "     where g2.id_dic_group = g.id_dic_group) as periodOfStudy, " +
                "       c2.qualification as qualification,\n" +
                "       s2.is_sirota as sirota, s2.is_indigent as indigent, " +
                "       s2.is_invalid as invalid, s2.is_veteran as veteran,\n" +
                "       r.date_finish as dateReferenceTo,\n" +
                "       g.dateofend as dateOfEndEducation,\n" +
                "       sss.id_student_semester_status as id,\n" +
                "       g.groupname as groupname,\n" +
                "       sch.dateofbegin as dateOfBeginSchoolYear,\n" +
                "       getdateofendcurrentsemesterbystudent(s2.id_studentcard) as dateOfEndSemester," +
                "       sss.date_complete_session as dateCompleteSession,\n" +
                "       cast (s2.order_info->'socialIncreasedScholarship'->>'dateTo' as date) as dateSocialScholarshipTo,\n" +
                "       sss.sessionresult as sessionResult\n" +
                "       from student_semester_status sss\n" +
                "       inner join studentcard s2 on sss.id_studentcard = s2.id_studentcard\n" +
                "       inner join humanface h2 on s2.id_humanface = h2.id_humanface\n" +
                "       left join reference r on r.id_reference = (select max(id_reference) from reference where id_studentcard = s2.id_studentcard)\n" +
                "       inner join link_group_semester semester on sss.id_link_group_semester = semester.id_link_group_semester\n" +
                "       inner join semester sem using (id_semester)\n" +
                "       inner join schoolyear sch using (id_schoolyear) \n" +
                "       inner join dic_group g on semester.id_dic_group = g.id_dic_group\n" +
                "       inner join curriculum c2 on g.id_curriculum = c2.id_curriculum\n" +
                "  where semester.dateofendsession >= :firstDate " +
                "  and semester.dateofendsession <= :secondDate\n" +
                "  and ((r.date_finish is null " +
                "  and (s2.is_sirota = 1 or s2.is_invalid = 1)) or r.date_finish > now())" +
                "  and s2.id_current_dic_group = semester.id_dic_group " +
                "  and is_deducted = 0 and is_academicleave = 0" +
                "  and isstudentdeductedcurrentsem(s2.id_studentcard) = 0\n" +
                "  and isstudentacademicleavecurrentsem(s2.id_studentcard) = 0\n" +
                "  and cast (s2.order_info->'socialIncreasedScholarship'->>'dateTo' as date) is not null \n" +
                //"and id_semester = " + getPrevSemester(idSemester);
                "and id_semester = " + (season.equals(SemesterConst.AUTUMN) ? getPrevSemester() : getCurrentSemester());

        Query q = getSession().createSQLQuery(query)
                .addScalar("sirota", BooleanType.INSTANCE)
                .addScalar("invalid", BooleanType.INSTANCE)
                .addScalar("indigent", BooleanType.INSTANCE)
                .addScalar("veteran", BooleanType.INSTANCE)
                .addScalar("sessionResult", IntegerType.INSTANCE)
                .addScalar("semesternumber", IntegerType.INSTANCE)
                .addScalar("dateReferenceTo", DateType.INSTANCE)
                .addScalar("dateSocialScholarshipTo", DateType.INSTANCE)
                .addScalar("dateCompleteSession", DateType.INSTANCE)
                .addScalar("dateOfBeginSchoolYear", DateType.INSTANCE)
                .addScalar("dateOfEndEducation", DateType.INSTANCE)
                .addScalar("dateOfEndSemester", DateType.INSTANCE)
                .addScalar("groupname", StringType.INSTANCE)
                .addScalar("qualification", IntegerType.INSTANCE)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("periodOfStudy", DoubleType.INSTANCE)
                .setDate("firstDate", firstDate)
                .setDate("secondDate", secondDate)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);

    }

    public List<OrderCreateStudentModel> getStudentsForAcademicalScholarshipNotInSession(Date firstDate, Date secondDate, Long idSemester) {
        //language=GenericSQL
        String query = "SELECT DISTINCT on (id_studentcard) * FROM \n" +
                "(SELECT " +
                "sss.date_complete_session as dateCompleteSession, " +
                "id_studentcard, " +
                "hf.family, " +
                "dgCurrent.groupname, " +
                "sss.id_student_semester_status as id, " +
                "count_session_result_by_srh(sss.id_student_semester_status) as sessionResult, " +
                "getcurrentsemesterforstudent(sc.id_studentcard) as semesternumber,\n" +
                "    (\n" + "        select dateofendsession from student_semester_status sss2\n" +
                "                                         inner join link_group_semester lgs2 using(id_link_group_semester)\n" +
                "        where sss2.id_studentcard = sss.id_studentcard and sss2.is_deducted = 0 and sss2.is_academicleave = 0 and id_semester = " + idSemester + " order by sss2.id_student_semester_status desc limit 1\n" +
                "    ) as dateNextEndOfSession,\n" + "    (\n" +
                "        select is_government_financed from student_semester_status sss2\n" +
                "                                               inner join link_group_semester lgs2 ON sss2.id_link_group_semester = lgs2.id_link_group_semester\n" +
                "        where sss2.id_studentcard = sss.id_studentcard and sss2.is_deducted = 0 and sss2.is_academicleave = 0 and id_semester = " + idSemester + " order by sss2.id_student_semester_status desc limit 1\n" +
                "    ) as nextGovernmentFinanced,\n" +
                "    sss.is_sessionprolongation as isSessionProlongation " +
                "FROM\n" + "    link_group_semester lgs\n" +
                "        join student_semester_status sss using(id_link_group_semester)\n" +
                "        join sessionrating s on sss.id_student_semester_status = s.id_student_semester_status\n" +
                "        join sessionratinghistory s2 on s.id_sessionrating = s2.id_sessionrating\n" +
                "        join register r on s2.id_register = r.id_register\n" +
                "        join studentcard sc using(id_studentcard)\n" +
                "        join humanface hf using(id_humanface)\n" +
                "        join dic_group dgCurrent ON sc.id_current_dic_group = dgCurrent.id_dic_group\n" +
                "        join semester sem on sem.id_semester = lgs.id_semester\n" +  // Берем formofstudy из semester
                "WHERE\n" +
                "  lgs.id_semester = " + getPrevSemester(idSemester) + "\n" +
                "  and is_government_financed = 1\n" +
                "  and sem.formofstudy = :formOfStudy\n" + // Только студенты очной формы могут получать ГАС
                "  and date_complete_session >= :firstDate " +
                "  and date_complete_session  <= :secondDate\n" +
                "  and is_deducted = 0\n" +
                "  and is_academicleave = 0\n" +
                "  and isstudentdeductedcurrentsem(id_studentcard) = 0\n" +
                "  and isstudentacademicleavecurrentsem(id_studentcard) = 0\n" +
                "  and count_session_result_by_srh(sss.id_student_semester_status) > 1\n" +
                "  and ((begindate is not null and enddate is not null) or r.signdate is not null)" +
                "  and (sc.order_info -> 'academicalScholarship' ->> 'dateTo' is null\n" +
                "        or CAST(sc.order_info -> 'academicalScholarship' ->> 'dateTo' as date) < now()    " +
                "or (CAST(sc.order_info -> 'academicalScholarship' ->> 'dateTo' as date) > now()\n" +
                "    and sc.order_info -> 'academicalScholarship' ->> 'isProlongation' is not null)\n" +
                "    ) order by id desc) t";

        Query q = getSession().createSQLQuery(query)
                .addScalar("dateCompleteSession", DateType.INSTANCE)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("semesternumber", IntegerType.INSTANCE)
                .addScalar("groupname")
                .addScalar("sessionResult")
                .addScalar("dateNextEndOfSession")
                .addScalar("isSessionProlongation", BooleanType.INSTANCE)
                .addScalar("nextGovernmentFinanced", BooleanType.INSTANCE)
                .setDate("firstDate", firstDate)
                .setDate("secondDate", secondDate)
                .setInteger("formOfStudy", FormOfStudy.FULL_TIME.getType()) // 1 - Очная форма обучения
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentsForAcademicalScholarshipInSession(Date firstDate,
                                                                                      Date secondDate,
                                                                                      String season,
                                                                                      Integer course,
                                                                                      Boolean isGraduate) {

        String courseFilter = course != null ? " and course = " + course + "\n" +
                " and c.qualification <> 3\n" : "";
        String query = "SELECT\n" +
                "       dgCurrent.groupname,\n" +
                "       id_student_semester_status                      as id,\n" +
                "       sessionresult                                  as sessionResult,\n" +
                "       getcurrentsemesterforstudent(sc.id_studentcard) as semesternumber,\n" +
                "       (\n" +
                "           select lgs2.dateofendsemester\n" + "           from link_group_semester lgs2\n" +
                "           where lgs2.id_dic_group = sc.id_current_dic_group\n" +
                "             and " +
                "  lgs2.id_semester = " + (isGraduate ? getCurrentSemester() : ((season.equals(SemesterConst.AUTUMN) ? getCurrentSemester() : getNextSemester()))) + "\n" +
                "       )                                               as dateOfEndSemester,\n" +
                "       (\n" +
                "           select lgs2.dateofendsession\n" + "           from link_group_semester lgs2\n" +
                "           where lgs2.id_dic_group = sc.id_current_dic_group\n" +
                "             and " +
                "  lgs2.id_semester = " + (isGraduate ? getCurrentSemester() : (season.equals(SemesterConst.AUTUMN) ? getCurrentSemester() : getNextSemester())) + "\n" +
                "       )                                               as dateOfEndCurSession,\n" +
                "       (\n" +
                "           select is_government_financed\n" + "           from student_semester_status sss2\n" +
                "                    inner join link_group_semester lgs2 ON sss2.id_link_group_semester = lgs2.id_link_group_semester\n" +
                "           where sss2.id_studentcard = sss.id_studentcard\n" +
                "             and sss2.is_deducted = 0\n" +
                "             and sss2.is_academicleave = 0\n" +
                "             and " +
                "  id_semester = " + (isGraduate ? getCurrentSemester() : (season.equals(SemesterConst.AUTUMN) ? getPrevSemester() : getCurrentSemester())) + "\n" +
                "           order by sss2.id_student_semester_status desc\n" + "           limit 1\n" +
                "       )                                               as nextGovernmentFinanced,\n" +
                //"       sss.is_sessionprolongation                      as isSessionProlongation,\n" +
                "(CASE WHEN (prolongationenddate >= '" + DateConverter.convertDateToStringByFormat(secondDate, "yyyy-MM-dd") + "') THEN true ELSE false END) as isSessionProlongation,\n" +
                "       dgCurrent.dateofcertificationto                 as dateOfCertificationTo,\n" +
                "       lgs.dateofendsession                            as dateOfEndSession\n" +
                "       FROM link_group_semester lgs\n" +
                "         join student_semester_status sss using (id_link_group_semester)\n" +
                "         join studentcard sc using (id_studentcard)\n" +
                "         join dic_group dgCurrent ON sc.id_current_dic_group = dgCurrent.id_dic_group\n" +
                "          join curriculum c on dgCurrent.id_curriculum = c.id_curriculum\n" +
                "WHERE " +
                "  id_semester = " + (isGraduate ? getCurrentSemester() : (season.equals(SemesterConst.AUTUMN) ? getPrevSemester() : getCurrentSemester())) + "\n" +
                "  and is_deducted = 0\n" +
                "  and is_academicleave = 0\n" +
                "  and isstudentdeductedcurrentsem(id_studentcard) = 0\n" +
                "  and isstudentacademicleavecurrentsem(id_studentcard) = 0\n" +
                "  and (sessionresult > 0 or (CASE WHEN (prolongationenddate >= '" + DateConverter.convertDateToStringByFormat(secondDate, "yyyy-MM-dd") + "') THEN true ELSE false END) )\n" +
                "  and cast(sc.order_info as text) not like '%academicIncreasedOrder%'\n" +
                "  and NOT EXISTS (select sri.id_sessionrating from sessionrating sri INNER JOIN sessionratinghistory srh ON srh.id_sessionrating=sri.id_sessionrating WHERE sri.id_student_semester_status = sss.id_student_semester_status AND srh.signdate > '" + DateConverter.convertDateToStringByFormat(secondDate, "yyyy-MM-dd") + "')\n" +
                "  and  (select lgs2.dateofendsemester from link_group_semester lgs2 \n" +
                "           where lgs2.id_dic_group = sc.id_current_dic_group \n" +
                "           and lgs2.id_semester = " + (isGraduate ? getCurrentSemester() : (season.equals(SemesterConst.AUTUMN) ?
                getCurrentSemester() :
                getNextSemester())) + ") is not null \n" +
                "  and lgs.dateofendsession >= '" + DateConverter.convertDateToStringByFormat(firstDate, "yyyy-MM-dd") +
                "' and lgs.dateofendsession <= '" + DateConverter.convertDateToStringByFormat(secondDate, "yyyy-MM-dd") + "'" +

                "  and (\n" +
                "        sc.order_info -> 'academicalScholarship' ->> 'dateTo' is null\n" +
                "        or CAST(case when sc.order_info -> 'academicalScholarship' ->> 'dateTo' = '' then null else sc.order_info -> 'academicalScholarship' ->> 'dateTo' end as date) < now()\n" +
                "        or (CAST(case when sc.order_info -> 'academicalScholarship' ->> 'dateTo' = '' then null else sc.order_info -> 'academicalScholarship' ->> 'dateTo' end as date) < (now() + interval '1 month')))\n" +
                "  and ((sc.order_info -> 'academicalIncreasedScholarship' ->> 'dateTo' is null) or\n" +
                "       (CAST(case when sc.order_info -> 'academicalIncreasedScholarship' ->> 'dateTo' = '' then null else sc.order_info -> 'academicalIncreasedScholarship' ->> 'dateTo' end as date) < now())\n" +
                "    or (CAST(case when sc.order_info -> 'academicalIncreasedScholarship' ->> 'dateTo' = '' then null else sc.order_info -> 'academicalIncreasedScholarship' ->> 'dateTo' end as date) < (now() + interval '1 month')))"
                + courseFilter;

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("semesternumber", IntegerType.INSTANCE)
                .addScalar("groupname")
                .addScalar("sessionResult")
                .addScalar("dateOfEndSemester")
                .addScalar("dateOfEndSession")
                .addScalar("dateOfEndCurSession")
                .addScalar("dateOfCertificationTo")
                .addScalar("isSessionProlongation", BooleanType.INSTANCE)
                .addScalar("nextGovernmentFinanced", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentsForAcademicalScholarshipInSessionWithProlongation(Date firstDate,
                                                                                                      Date secondDate,
                                                                                                      String season) {
        //Данные, которые нужны для вычисления продления
        //1. Продление и даты продления за текущий семестр
        //2. Все оценки за предыдущий семестр
        //3. SR за препредыдущий семестр

        //language=GenericSQL
        String query = "SELECT\n" +
                "  dgCurrent.groupname, " +
                "  id_student_semester_status as id," +
                "  getcurrentsemesterforstudent(sc.id_studentcard) as semesternumber,\n" +
                "  (\n" +
                "    select dateofendsession from student_semester_status sss2\n" +
                "                                   inner join link_group_semester lgs2 using(id_link_group_semester)\n" +
                "    where sss2.id_studentcard = sss.id_studentcard and sss2.is_deducted = 0 " +
                "and sss2.is_academicleave = 0 and " +
                "  id_semester = " + (season.equals(SemesterConst.AUTUMN) ? getCurrentSemester() : getNextSemester()) + "\n" +
                " order by sss2.id_student_semester_status desc limit 1\n" +
                "  ) as dateNextEndOfSession,\n" +
                "  (\n" +
                "    select is_government_financed from student_semester_status sss2\n" +
                "                                         inner join link_group_semester lgs2 ON sss2.id_link_group_semester = lgs2.id_link_group_semester\n" +
                "    where sss2.id_studentcard = sss.id_studentcard " +
                "and sss2.is_deducted = 0 " +
                "and sss2.is_academicleave = 0 " +
                "and " +
                "  id_semester = " + (season.equals(SemesterConst.AUTUMN) ? getPrevSemester() : getCurrentSemester()) + "\n" +
                "order by sss2.id_student_semester_status desc limit 1\n" +
                "  ) as nextGovernmentFinanced,\n" +
                "       srh.newrating as rating,\n" +
                "   (select sss2.is_sessionprolongation\n" +
                "        from student_semester_status sss2\n" +
                "      join link_group_semester lgs2 using (id_link_group_semester)\n" +
                "        where " +
                " lgs2.id_semester = " + (season.equals(SemesterConst.AUTUMN) ? getPrevSemester() : getCurrentSemester()) + "\n" +
                "          and sss2.id_studentcard = sc.id_studentcard\n" +
                "        order by sss2.id_student_semester_status desc\n" +
                "        limit 1\n" +
                "       ) " +
                "as isSessionProlongation,\n" +
                "  (select sss2.sessionresult\n" +
                "   from student_semester_status sss2\n" +
                "          join link_group_semester lgs2 using (id_link_group_semester)\n" +
                "   where lgs2.id_semester = " + (season.equals(SemesterConst.SPRING) ? getPrevSemester() : getPrevSemester(getPrevSemester())) + "\n" +
                "     and sss2.id_studentcard = sc.id_studentcard\n" +
                "   order by sss2.id_student_semester_status desc\n" +
                "   limit 1\n" + "  )  " +
                "as sessionResult, \n" +
                "sr.id_sessionrating as idSessionRating, \n" +
                "srh.newrating as rating," +
                "(select sss2.prolongationenddate\n" +
                "        from student_semester_status sss2\n" +
                "        join link_group_semester lgs2 using (id_link_group_semester)\n" +
                "        where lgs2.id_semester = " + (season.equals(SemesterConst.AUTUMN) ? getPrevSemester() : getCurrentSemester()) + "\n" +
                "        and sss2.id_studentcard = sc.id_studentcard\n" +
                "        order by sss2.id_student_semester_status desc\n" +
                "        limit 1\n" + "       )  " +
                " as prolongationEndDate, " +
                "lgs.dateofendsession as dateOfEndSession \n " +
                " FROM\n" + "  link_group_semester lgs\n" +
                "    join semester using (id_semester)" +
                "    join student_semester_status sss using(id_link_group_semester)\n" +
                "    join sessionrating sr using (id_student_semester_status)\n" +
                "    join sessionratinghistory srh using (id_sessionrating)\n" +
                "    join studentcard sc using(id_studentcard)\n" +
                "    join dic_group dgCurrent ON sc.id_current_dic_group = dgCurrent.id_dic_group\n" +
                "WHERE\n" +
                "  id_semester = " + (season.equals(SemesterConst.AUTUMN) ? getPrevSemester() : getCurrentSemester()) + "\n" +
                "  and is_deducted = 0\n" +
                "  and is_academicleave = 0\n" +
                "  and cast (sc.order_info as text) not like '%academicIncreasedOrder%' \n" +
                "  and isstudentdeductedcurrentsem(id_studentcard) = 0\n" +
                "  and isstudentacademicleavecurrentsem(id_studentcard) = 0\n" +
                "  and dateofendsession >= '" + DateConverter.convertDateToStringByFormat(firstDate, "yyyy-MM-dd") + "' \n" +
                "  and dateofendsession <= '" + DateConverter.convertDateToStringByFormat(secondDate, "yyyy-MM-dd") + "' \n" +
                " and (\n" + "        sc.order_info -> 'academicalScholarship' ->> 'dateTo' is null\n" +
                "        or CAST(sc.order_info -> 'academicalScholarship' ->> 'dateTo' as date) < now()\n" +
                "        or (CAST(sc.order_info -> 'academicalScholarship' ->> 'dateTo' as date) < (now() + interval '1 month')))\n" +
                "  and ((sc.order_info -> 'academicalIncreasedScholarship' ->> 'dateTo' is null) or\n" +
                "       (CAST(sc.order_info -> 'academicalIncreasedScholarship' ->> 'dateTo' as date) < now())\n" +
                "    or (CAST(sc.order_info -> 'academicalIncreasedScholarship' ->> 'dateTo' as date) < (now() + interval '1 month')))\n" +
                "  and is_sessionprolongation = 1";

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("semesternumber", IntegerType.INSTANCE)
                .addScalar("groupname")
                .addScalar("dateNextEndOfSession")
                .addScalar("dateOfEndSession")
                .addScalar("isSessionProlongation", BooleanType.INSTANCE)
                .addScalar("nextGovernmentFinanced", BooleanType.INSTANCE)
                .addScalar("rating", IntegerType.INSTANCE)
                .addScalar("sessionResult", IntegerType.INSTANCE)
                .addScalar("prolongationEndDate", DateType.INSTANCE)
                .addScalar("idSessionRating", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
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

    public Long getPrevSemester() {
        return getPrevSemester(getCurrentSemester());
    }

    public Long getNextSemester() {
        return getNextSemester(getCurrentSemester());
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

    public Long getCurrentSemester(long idInstitute, int formOfStudy) {
        String query =
                "Select id_semester as idSemester from semester where id_institute = " + idInstitute + " and formofstudy = " + formOfStudy +
                        " and is_current_sem = 1 limit 1";
        Query q = getSession().createSQLQuery(query);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long getCurrentSemester() {
        String query =
                "Select id_semester as idSemester from semester where id_institute = 1 " +
                        "and formofstudy = 1 " +
                        "and is_current_sem = 1 " +
                        "limit 1";
        Query q = getSession().createSQLQuery(query);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public String getCurrentSeason() {
        String query =
                "Select season from semester where id_institute = 1 " +
                        "and formofstudy = 1 " +
                        "and is_current_sem = 1 " +
                        "limit 1";
        Query q = getSession().createSQLQuery(query);
        List<Integer> list = (List<Integer>) getList(q);
        if (list.size() == 0) {
            return null;
        } else if (list.get(0) == 0) {
            return SemesterConst.AUTUMN;
        } else {
            return SemesterConst.SPRING;
        }
    }

    public List<OrderCreateStudentModel> getStudentsForDeductionByComission(Long idCurSem) {
        String query = "select distinct on (hf.family, hf.name, hf.patronymic, dg.groupname)\n" +
                "                getsssforstudentincursem(id_studentcard) as id, getcurrentsemesterforstudent(id_studentcard) as semesternumber,\n" +
                "                hf.family || ' ' || hf.name || ' ' || hf.patronymic as fio, dg.groupname, sss.is_government_financed as governmentFinanced, hf.foreigner as foreigner\n" +
                "                from\n" +
                "                  humanface hf\n" +
                "                  join studentcard sc using (id_humanface)\n" +
                "                  join student_semester_status sss using (id_studentcard)\n" +
                "                  join link_group_semester lgs using (id_link_group_semester)\n" +
                "                  join dic_group dg using (id_dic_group)\n" +
                "                  join sessionrating sr on sss.id_student_semester_status = sr.id_student_semester_status\n" +
                "                  join sessionratinghistory srh using (id_sessionrating)\n" +
                "                  join register reg on srh.id_register = reg.id_register\n" +
                "                  join register_comission rc on rc.id_register = reg.id_register\n" +
                "\n" +
                "                where (isstudentacademicleavecurrentsem(id_studentcard) = 0) and (isstudentdeductedcurrentsem(id_studentcard) = 0)\n" +
                "                      AND rc.dateofbegincomission = (select dateofbegincomission\n" +
                "                                                     from register_comission rc\n" +
                "                                                       join register reg using (id_register)\n" +
                "                                                       join semester sem using (id_semester)\n" +
                "                                                     where sem.formofstudy = (select formofstudy from semester where id_semester = " + idCurSem + " )\n" +
                "\n" +
                "                                                     order by dateofbegincomission desc limit 1)\n" +
                "                     AND rc.dateofendcomission = (select dateofendcomission\n" +
                "                                                  from register_comission rc\n" +
                "                                                    join register reg using (id_register)\n" +
                "                                                    join semester sem using (id_semester)\n" +
                "                                                  where sem.formofstudy = (select formofstudy from semester where id_semester = " + idCurSem + " )\n" +
                "\n" +
                "                                                  order by dateofendcomission desc limit 1)\n" +
                "                      and srh.newrating in (2, -2, -3)\n" +
                "\n" +
                "                order by hf.family, hf.name, hf.patronymic";

        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("groupname")
                .addScalar("semesternumber", IntegerType.INSTANCE)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("governmentFinanced", BooleanType.INSTANCE)
                .addScalar("foreigner", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentsInfoForAcademicFirstCourse(String subquery) {

        //TODO: исправить на is_current_sem = 1 в конце
        String query = "select " +
                "distinct on (family || ' ' || name || ' ' || patronymic) \n" +
                "family || ' ' || name || ' ' || patronymic as fio,\n" +
                "       dg.groupname as groupname,\n" +
                "       sss.id_student_semester_status as id,\n" +
                "       c.directioncode as specialityCode\n" +
                "       from humanface\n" +
                "join studentcard s on humanface.id_humanface = s.id_humanface\n" +
                "join student_semester_status sss on s.id_studentcard = sss.id_studentcard\n" +
                "join link_group_semester lgs on sss.id_link_group_semester = lgs.id_link_group_semester\n" +
                "join semester sem on lgs.id_semester = sem.id_semester\n" +
                "join dic_group dg on lgs.id_dic_group = dg.id_dic_group\n" +
                "join curriculum c on dg.id_curriculum = c.id_curriculum\n" +
                "where lgs.id_semester = 64 \n" +
//                       "where sem.is_current_sem = 1 \n" +
                "  and sem.formofstudy = 1 \n" +
                "  and family || ' ' || name || ' ' || patronymic similar to '(" + subquery + ")%'";

        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("groupname")
                .addScalar("specialityCode")
                .addScalar("id", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);

    }

    public List<OrderCreateStudentModel> getStudentsForAcademicFirstCourseExceptingIncrease(String subquery) {

        String query = "select family || ' ' || name || ' ' || patronymic as fio,\n" +
                "       dg.groupname as groupname,\n" +
                "       sss.id_student_semester_status as id,\n" +
                "       c.directioncode as specialityCode,\n" +
                "       c.qualification as qualification\n" +
                "from humanface\n" +
                "         join studentcard s on humanface.id_humanface = s.id_humanface\n" +
                "         join student_semester_status sss on s.id_studentcard = sss.id_studentcard\n" +
                "         join link_group_semester lgs on sss.id_link_group_semester = lgs.id_link_group_semester\n" +
                "         join semester sem on lgs.id_semester = sem.id_semester\n" +
                "         join dic_group dg on lgs.id_dic_group = dg.id_dic_group\n" +
                "         join curriculum c on dg.id_curriculum = c.id_curriculum\n" +
                "where lgs.id_semester = 64 \n" +
                //                       "where sem.is_current_sem = 1 \n" +
                "  and sem.formofstudy = 1 \n" +
                "  and lgs.course = 1 \n" +
                "  and s.id_studentcard not in (" + subquery + ")";

        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("groupname")
                .addScalar("specialityCode")
                .addScalar("id", LongType.INSTANCE)
                .addScalar("qualification", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);

    }

    public List<OrderCreateStudentModel> getStudentsForTransfer(Long idSemester, String groups) {

        String query = "select family,\n" +
                "       id_studentcard,\n" +
                "       sss.id_student_semester_status as id,\n" +
                "       dg.groupname                   as groupname,\n" +
                "       sss.is_government_financed     as governmentFinanced,\n" +
                "       cr.periodofstudy               as periodOfStudy,\n" +
                "       lgs.semesternumber             as semesternumber,\n" +
                "       lgs.course as course,\n" +
                " CAST (order_info->'transferInfo' AS TEXT) as transferInfo,\n" +
                " CAST (order_info->'transferInfoConditionally' AS TEXT) as transferConditionallyInfo \n" +
                "from student_semester_status sss\n" +
                "         inner join studentcard sc using (id_studentcard)\n" +
                "         join humanface hf using (id_humanface)\n" +
                "         inner join link_group_semester lgs using (id_link_group_semester)\n" +
                "         inner join dic_group dg using (id_dic_group)\n" +
                "         inner join curriculum cr using (id_curriculum)\n" +
                "where id_semester = " + idSemester + "\n" +
                "  and sessionresult > 0\n" +
                "  and is_deducted = 0\n" +
                "  and is_academicleave = 0\n" +
                "  and sc.id_current_dic_group = lgs.id_dic_group\n" +
                "  and groupname in (" + groups + ")\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("groupname")
                .addScalar("governmentFinanced", BooleanType.INSTANCE)
                .addScalar("periodOfStudy", DoubleType.INSTANCE)
                .addScalar("semesternumber", IntegerType.INSTANCE)
                .addScalar("course", IntegerType.INSTANCE)
                .addScalar("transferInfo")
                .addScalar("transferConditionallyInfo")
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getSocialIncreasedStudentsWithScholarshipChanging() {
        //TODO:Привязал конкретно к осеннему приказу (подумать, как сделать автоматически)
        //language=GenericSQL
        String query = "SELECT s.id_semester,\n" +
                "       sc.id_studentcard                                   as idStudentcard,\n" +
                "       hf.family || ' ' || hf.name || ' ' || hf.patronymic as fio,\n" +
                "       dg.groupname                                        as groupname,\n" +
                "       (select sss2.id_student_semester_status\n" +
                "        from link_group_semester lgs2\n" +
                "                 inner join student_semester_status sss2\n" +
                "                            on lgs2.id_link_group_semester = sss2.id_link_group_semester\n" +
                "                 inner join studentcard sc2 on sss2.id_studentcard = sc2.id_studentcard\n" +
                "                 inner join semester sem2 on lgs2.id_semester = sem2.id_semester\n" +
                "        where sem2.is_current_sem = 1\n" +
                "          and sc2.id_studentcard = sc.id_studentcard\n" +
                "          and sc.id_current_dic_group = lgs2.id_dic_group\n" +
                "        order by id_student_semester_status desc\n" +
                "        limit 1\n" +
                "       )                                                   as id,\n" +
                " coalesce((select reference.date_finish\n" +
                "                 from reference\n" +
                "                 where sc.id_studentcard = reference.id_studentcard\n" +
                "                 order by id_reference desc\n" +
                "                 limit 1), dg.dateofend) as dateSocialScholarshipTo\n" +
                "FROM student_semester_status sss\n" +
                "         INNER JOIN link_group_semester lgs USING (id_link_group_semester)\n" +
                "         join semester s ON (lgs.id_semester = s.id_semester)\n" +
                "         INNER JOIN studentcard sc on sss.id_studentcard = sc.id_studentcard\n" +
                "         INNER JOIN dic_group dg on sc.id_current_dic_group = dg.id_dic_group\n" +
                "         INNER JOIN humanface hf using (id_humanface)\n" +
                "WHERE CAST(sc.order_info -> 'socialIncreasedScholarship' ->> 'dateTo' as date) > now()\n" +
                "  and s.id_semester = 67\n" +
                "  and (select srh10.newrating\n" +
                "              from sessionratinghistory srh10\n" +
                "                       join sessionrating s10 on srh10.id_sessionrating = s10.id_sessionrating\n" +
                "                       join student_semester_status sss10 on s10.id_student_semester_status = sss10.id_student_semester_status\n" +
                "                       join link_group_semester lgs10 on sss10.id_link_group_semester = lgs10.id_link_group_semester\n" +
                "                       join link_group_semester_subject lgss10 on lgs10.id_link_group_semester = lgss10.id_link_group_semester\n" +
                "              where lgs10.id_semester = 67\n" +
                "                and (srh10.signdate between '2019-09-01' and now())\n" +
                "                and srh10.retake_count = 1\n" +
                "                and sss10.id_student_semester_status = sss.id_student_semester_status\n" +
                "              group by sss10.id_student_semester_status, newrating\n" +
                "              order by srh10.newrating desc\n" +
                "              limit 1) not in (1,4,5)";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idStudentcard", LongType.INSTANCE)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("fio")
                .addScalar("groupname")
                .addScalar("dateSocialScholarshipTo")
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentForCancelSocialIncreasedScholarshipInSession(Long idSemester, Date dateMarksFrom, Date dateMarksTo) {
        String firstDateStr = DateConverter.convertDateToStringByFormat(dateMarksFrom, "yyyy-MM-dd");
        String secondDateStr = DateConverter.convertDateToStringByFormat(dateMarksTo, "yyyy-MM-dd");

        String query = "select sss.id_student_semester_status                                      as id,\n" +
                "\tfamily,\n" +
                "\tname,\n" +
                "\tgroupname,\n" +
                "coalesce((select reference.date_finish\n" +
                "                 from reference\n" +
                "                 where sc.id_studentcard = reference.id_studentcard\n" +
                "                 order by id_reference desc\n" +
                "                 limit 1), dg.dateofend) as dateSocialScholarshipTo\n" +
                "from student_semester_status sss\n" +
                "       join studentcard sc using (id_studentcard)\n" +
                "       join humanface hf using (id_humanface)\n" +
                "       join link_group_semester lgs using (id_link_group_semester)\n" +
                "       join dic_group dg using (id_dic_group)\n" +
                "       join sessionrating sr on sss.id_student_semester_status = sr.id_student_semester_status\n" +
                "       join sessionratinghistory srh on sr.id_sessionrating = srh.id_sessionrating\n" +
                "       join subject s2 on sr.id_subject = s2.id_subject\n" +
                "       join link_group_semester_subject lgss on (lgs.id_link_group_semester = lgss.id_link_group_semester and lgss.id_subject = s2.id_subject)\n" +
                "       join register reg on srh.id_register = reg.id_register \n" +
                "where lgs.id_semester = " + idSemester + "\n" +
                "  and sss.is_deducted = 0\n" +
                "  and sss.is_academicleave = 0\n" +
                "  and sc.id_current_dic_group = dg.id_dic_group\n" +
                "  and CAST(sc.order_info -> 'socialIncreasedScholarship' ->> 'dateTo' as date) > '" + secondDateStr + "'\n" + // возможно стоит внести дату отмены, но пока так сойдет
                "  and srh.signdate is not null \n" +
                "  and (consultationdate is not null\n" +
                "         and consultationdate >= (case when dateofbeginpassweek > '" + firstDateStr + "'\n" +
                "           then dateofbeginpassweek else '" + firstDateStr + "' end)\n" +
                "         and consultationdate <= (case when dateofendpassweek < cast('" + secondDateStr + "' as date)\n" +
                "                                              then dateofendpassweek else '" + secondDateStr + "' end)\n" +
                "       or\n" +
                "       examdate is not null\n" +
                "         and examdate >= (case when dateofbeginpassweek > cast('" + firstDateStr + "' as date)\n" +
                "                                                then dateofbeginpassweek else '" + firstDateStr + "' end)\n" +
                "         and examdate <= (case when dateofendpassweek < cast('" + secondDateStr + "' as date)\n" +
                "                                      then dateofendpassweek else '" + secondDateStr + "' end)\n" +
                "       or\n" +
                "       passdate is not null\n" +
                "         and passdate >= (case when dateofbeginpassweek > cast('" + firstDateStr + "' as date)\n" +
                "                                      then dateofbeginpassweek else '" + firstDateStr + "' end)\n" +
                "         and passdate <= (case when dateofendpassweek < cast('" + secondDateStr + "' as date)\n" +
                "                                      then dateofendpassweek else '" + secondDateStr + "' end)\n" +
                "       or\n" +
                "       practicdate is not null\n" +
                "         and practicdate >= (case when dateofbeginpassweek > cast('" + firstDateStr + "' as date)\n" +
                "                                         then dateofbeginpassweek else '" + firstDateStr + "' end)\n" +
                "         and practicdate <= (case when dateofendpassweek < cast('" + secondDateStr + "' as date)\n" +
                "                                         then dateofendpassweek else '" + secondDateStr + "' end))\n" +
                " and ((srh.newrating in (3,2,-2)) or (sss.is_sessionprolongation <> 1 and srh.newrating = -3 )) \n" +
                "order by groupname, family, name";

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("family", StringType.INSTANCE)
                .addScalar("name", StringType.INSTANCE)
                .addScalar("groupname", StringType.INSTANCE)
                .addScalar("dateSocialScholarshipTo", DateType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentForFirstEliminationAfterSession(Date dateFrom, Date dateTo, String season, Integer course) {
        //language=GenericSQL
        String query = "select distinct id_student_semester_status as id,\n" +
                "                groupname,\n" +
                "                is_government_financed as governmentFinanced\n" +
                "from link_group_semester lgs\n" +
                "         join student_semester_status sss using (id_link_group_semester)\n" +
                "         join studentcard sc using (id_studentcard)\n" +
                "         join dic_group dgCurrent ON sc.id_current_dic_group = dgCurrent.id_dic_group\n" +
                "         join curriculum c on dgCurrent.id_curriculum = c.id_curriculum\n" +
                "where id_semester = " + (season.equals(SemesterConst.SPRING) ? getCurrentSemester()
                : getPrevSemester()) + "\n" +
                "  and is_deducted = 0\n" +
                "  and is_academicleave = 0\n" +
                "  and dateofendsession >= '" + DateConverter.convertDateToStringByFormat(dateFrom, "yyyy-MM-dd") + "'\n" +
                "  and dateofendsession <= '" + DateConverter.convertDateToStringByFormat(dateTo, "yyyy-MM-dd") + "'\n" +
                "  and (prolongationenddate <= '" + DateConverter.convertDateToStringByFormat(dateTo, "yyyy-MM-dd") +
                "' or prolongationenddate is NULL)\n" + // в 1ППА не попадают студенты с продлением
                "  and course = " + course + "\n" +
                "  and c.formofstudy = 1\n" +
                "  and sss.is_sessionprolongation = 0\n" +
                "  and lgs.id_dic_group = dgCurrent.id_dic_group" +
                "  and count_debts_in_session(id_student_semester_status) > 0\n" + //смотрим только крайнюю сессию
                "  and c.qualification <> 3\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("groupname")
                .addScalar("governmentFinanced", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentForSecondEliminationAfterSession(Date dateFrom, Date dateTo, String season, Integer course) {
        //language=GenericSQL
        String query = "select distinct id_student_semester_status as id,\n" +
                "                groupname,\n" +
                "                is_government_financed as governmentFinanced\n" +
                "from link_group_semester lgs\n" +
                "     join student_semester_status sss using (id_link_group_semester)\n" +
                "     join sessionrating sr using (id_student_semester_status)\n" +
                "     join sessionratinghistory srh using (id_sessionrating)\n" +
                "     join register r using (id_register)\n" +
                "     join studentcard sc using (id_studentcard)\n" +
                "     join dic_group dgCurrent ON sc.id_current_dic_group = dgCurrent.id_dic_group\n" +
                "     join curriculum c on dgCurrent.id_curriculum = c.id_curriculum\n" +
                "where lgs.id_semester = " + (season.equals(SemesterConst.SPRING) ? getCurrentSemester()
                : getPrevSemester()) + "\n" +
                "and (select count (sss2.id_student_semester_status) from link_group_semester lgs2\n" +
                "        join student_semester_status sss2 using (id_link_group_semester)\n" +
                "        join sessionrating s2 using (id_student_semester_status)\n" +
                "        join studentcard sc2 using (id_studentcard)\n" +
                "        where lgs2.id_semester = " + getCurrentSemester() + "\n" +
                "        and sc2.id_studentcard = sc.id_studentcard and (is_deducted = 1 or is_academicleave = 1)) = 0\n" +
                "and id_student_semester_status in (\n" +
                "        select id_student_semester_status from order_head\n" +
                "        join link_order_section using (id_order_head)\n" +
                "        join link_order_student_status using (id_link_order_section)\n" +
                "        join student_semester_status using (id_student_semester_status)\n" +
                "        join link_group_semester using (id_link_group_semester)\n" +
                "        where id_order_rule = " + OrderRuleConst.SET_FIRST_ELIMINATION.getId() + "\n" +
                /// Не самая адекватная строчка, т.к. в осеннем должен смотреть в предыдущий, но тогда осенний не получится сделать весной
                "        and semester = " + (season.equals(SemesterConst.AUTUMN) ? getCurrentSemester() : getPrevSemester()) + "\n" +
                "        and course = " + course + ")\n" +
                "  and r.signdate >= '" + DateConverter.convertDateToStringByFormat(dateFrom, "yyyy-MM-dd") + "'\n" +
                "  and r.signdate <= '" + DateConverter.convertDateToStringByFormat(dateTo, "yyyy-MM-dd") + "'\n" +
                "  and course = " + course + "\n" +
                "  and c.formofstudy = 1\n" +
                "  and sss.is_sessionprolongation = 0\n" +
                "  and lgs.id_dic_group = dgCurrent.id_dic_group" +
                "  and count_debts_in_session(id_student_semester_status) > 0\n" +
                "  and c.qualification <> 3\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("groupname")
                .addScalar("governmentFinanced", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentsForTransferConditionally(Date dateFrom, Date dateTo, Integer course) {
        //language=GenericSQL
        String query = "select distinct id_student_semester_status as id,\n" +
                "                groupname,\n" +
                "                is_government_financed as governmentFinanced\n" +
                "from link_group_semester lgs\n" +
                "         join student_semester_status sss using (id_link_group_semester)\n" +
                "         join studentcard sc using (id_studentcard)\n" +
                "         join dic_group dgCurrent ON sc.id_current_dic_group = dgCurrent.id_dic_group\n" +
                "         join curriculum c on dgCurrent.id_curriculum = c.id_curriculum\n" +
                "where id_semester = " + (getCurrentSeason().equals(SemesterConst.SPRING) ? getCurrentSemester() : getPrevSemester()) + "\n" +
                "  and is_deducted = 0\n" +
                "  and is_academicleave = 0\n" +
                "  and dateofendpractic  >= '" + DateConverter.convertDateToStringByFormat(dateFrom, "yyyy-MM-dd") + "'\n" +
                "  and dateofendpractic  <= '" + DateConverter.convertDateToStringByFormat(dateTo, "yyyy-MM-dd") + "'\n" +
                "  and (prolongationenddate <= now() or prolongationenddate is NULL)\n" + // в условный перевод не попадают студенты с продлением
                "  and dateofendsession <= now()\n" +
                "  and course = " + course + "\n" +
                "  and c.formofstudy = 1\n" +
                "  and sss.is_sessionprolongation = 0\n" +
                "  and lgs.id_dic_group = dgCurrent.id_dic_group" +
                "  and sss.sessionresult <= 0\n" +
                "  and c.qualification <> 3\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("groupname")
                .addScalar("governmentFinanced", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentForTransfer(Date dateFrom, Date dateTo, Integer course) {
        //language=GenericSQL
        String query = "select distinct id_student_semester_status as id,\n" +
                "                groupname,\n" +
                "                is_government_financed as nextGovernmentFinanced\n" +
                "from link_group_semester lgs\n" +
                "         join student_semester_status sss using (id_link_group_semester)\n" +
                "         join studentcard sc using (id_studentcard)\n" +
                "         join dic_group dgCurrent ON sc.id_current_dic_group = dgCurrent.id_dic_group\n" +
                "         join curriculum c on dgCurrent.id_curriculum = c.id_curriculum\n" +
                "where id_semester = " + (getCurrentSeason().equals(SemesterConst.SPRING) ? getCurrentSemester() : getPrevSemester()) + "\n" +
                "  and is_deducted = 0\n" +
                "  and is_academicleave = 0\n" +
                "  and dateofendpractic >= '" + DateConverter.convertDateToStringByFormat(dateFrom, "yyyy-MM-dd") + "'\n" +
                "  and dateofendpractic <= '" + DateConverter.convertDateToStringByFormat(dateTo, "yyyy-MM-dd") + "'\n" +
                "  and dateofendsession <= now()\n" +
                "  and course = " + course + "\n" +
                "  and c.formofstudy = 1\n" +
                "  and sss.is_sessionprolongation = 0\n" +
                "  and lgs.id_dic_group = dgCurrent.id_dic_group" +
                "  and sss.sessionresult >= 0\n" +
                "  and c.qualification <> 3\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("groupname")
                .addScalar("nextGovernmentFinanced", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentForFirstEliminationAfterPractice(Date dateFrom, Date dateTo, String season, Integer course) {
        //language=GenericSQL
        String query = "select distinct id_student_semester_status as id,\n" +
                "                groupname,\n" +
                "                is_government_financed as governmentFinanced\n" +
                "from link_group_semester lgs\n" +
                "         join student_semester_status sss using (id_link_group_semester)\n" +
                "         join studentcard sc using (id_studentcard)\n" +
                "         join dic_group dgCurrent ON sc.id_current_dic_group = dgCurrent.id_dic_group\n" +
                "         join curriculum c on dgCurrent.id_curriculum = c.id_curriculum\n" +
                "where id_semester = " + (season.equals(SemesterConst.SPRING) ? getCurrentSemester()
                : getPrevSemester()) + "\n" +
                "  and is_deducted = 0\n" +
                "  and is_academicleave = 0\n" +
                "  and dateofendsession >= '" + DateConverter.convertDateToStringByFormat(dateFrom, "yyyy-MM-dd") + "'\n" +
                "  and dateofendsession <= '" + DateConverter.convertDateToStringByFormat(dateTo, "yyyy-MM-dd") + "'\n" +
                "  and course = " + course + "\n" +
                "  and c.formofstudy = 1\n" +
                //"  and sss.is_sessionprolongation = 0\n" +
                "  and (prolongationenddate <= '" + DateConverter.convertDateToStringByFormat(dateTo, "yyyy-MM-dd") +
                "' or prolongationenddate is NULL)\n" + // в 1ППА не попадают студенты с продлением
                "  and lgs.id_dic_group = dgCurrent.id_dic_group" +
                "  and count_debts_in_session(id_student_semester_status) > 0\n" +
                "  and c.qualification <> 3\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("groupname")
                .addScalar("governmentFinanced", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentForTransferAfterTransferConditionally(Integer course, Date date) {
        //language=GenericSQL
        String query = "with oldsc (id_studentcard, ordernumber, dateofend, groupname) as \n" +
                "\t(select sc.id_studentcard, oh.ordernumber, oh.dateofend, loss.groupname  \n" +
                "\tfrom order_head oh\n" +
                " \tjoin order_section os on oh.id_order_rule = os.id_order_rule\n" +
                " \tjoin link_order_section los on oh.id_order_head = los.id_order_head\n" +
                " \tjoin link_order_student_status loss on los.id_link_order_section = loss.id_link_order_section\n" +
                "\tjoin student_semester_status sss on loss.id_student_semester_status = sss.id_student_semester_status  \n" +
                " \tjoin link_group_semester lgs on sss.id_link_group_semester = lgs.id_link_group_semester\n" +
                " \tjoin studentcard sc using(id_studentcard)\n" +
                "\t\twhere oh.semester in (" + getPrevSemester() + ", " + getCurrentSemester() + ")\n" +
                "\t\tand os.id_order_rule = 25\n" +
                "\t\tand lgs.course = " + (course - 1) + " )\t\n" +
                "select\n" +
                "       distinct\n" +
                "       sss.id_student_semester_status as id,\n" +
                "       oldsc2.groupname,\n" +
                "       is_government_financed as governmentFinanced,\n" +
                "       count_debts(sss.id_student_semester_status) as debtsAmount,\n" +
                "       oldsc2.ordernumber as prevOrderNumber,\n" +
                "       oldsc2.dateofend as prevOrderDateSign,\n" +
                "\t\thf.family\n" +
                "from \n" +
                "\tstudent_semester_status sss\n" +
                "\tjoin studentcard sc using(id_studentcard)\n" +
                "\tjoin link_group_semester lgs on sss.id_link_group_semester = lgs.id_link_group_semester and sc.id_current_dic_group = lgs.id_dic_group\t\n" +
                "\tjoin humanface hf on sc.id_humanface = hf.id_humanface\n" +
                "\tinner join oldsc oldsc2 on oldsc2.id_studentcard = sc.id_studentcard\n" +
                "\tjoin dic_group dg on dg.id_dic_group = sc.id_current_dic_group\n" +
                "where id_semester in (" + getPrevSemester() + ", " + getCurrentSemester() + ")\n" +
                "  and is_deducted = 0\n" +
                "  and is_academicleave = 0\n" +
                "  and course = " + (course - 1) +
                " and sss.date_complete_session < '" + DateConverter.convertDateToStringByFormat(date, "yyyy-MM-dd") + "'\n" + // не учитываются те кто сдал посл даты перевода
                "  and count_debts(sss.id_student_semester_status) = 0" +
                "  and (select count(*) \n" +
                "\tfrom order_head oh2\n" +
                " \tjoin order_section os2 on oh2.id_order_rule = os2.id_order_rule\n" +
                " \tjoin link_order_section los2 on oh2.id_order_head = los2.id_order_head\n" +
                " \tjoin link_order_student_status loss2 on los2.id_link_order_section = loss2.id_link_order_section\n" +
                "\tjoin student_semester_status sss2 on loss2.id_student_semester_status = sss2.id_student_semester_status\n" +
                "\twhere sss2.id_student_semester_status  = sss.id_student_semester_status and os2.id_order_rule = 30) = 0"; // Не учитываются те, кто уже переведены

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("groupname")
                .addScalar("debtsAmount")
                .addScalar("governmentFinanced", BooleanType.INSTANCE)
                .addScalar("prevOrderNumber")
                .addScalar("prevOrderDateSign")
                .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }

}
