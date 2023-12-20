package org.edec.student.schedule.manager;

import org.edec.dao.DAO;
import org.edec.student.schedule.model.ScheduleDateModel;
import org.edec.student.schedule.model.ScheduleSubjectModel;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;

import java.util.Date;
import java.util.List;

public class ScheduleStudentManager extends DAO {

    public List<ScheduleSubjectModel> getSchedule (String groupname, String dayname, int week, String timeName){
        String query = "SELECT\n" +
                "\tLSCH.id_link_group_semester_subject AS idLGSS, LSCH.id_link_group_schedule_subject AS idLSCH, LSCH.week, LSCH.room, LSCH.teacher,\n" +
                "\tDD.day_name AS dayName, DT.name_time AS timeName,  DS.subjectname, LSCH.lesson\n" + "FROM\n" +
                "\tdic_group DG\n" + "\tINNER JOIN link_group_semester LGS USING (id_dic_group)\n" +
                "\tINNER JOIN semester SEM USING (id_semester)\n" +
                "\tINNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester)\n" +
                "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "\tINNER JOIN link_group_schedule_subject LSCH USING (id_link_group_semester_subject)\n" +
                "\tINNER JOIN dic_day_lesson DD USING (id_dic_day_lesson)\n" +
                "\tINNER JOIN dic_time_lesson DT USING (id_dic_time_lesson)\n" +
                "WHERE DG.groupname = '"+ groupname +"' AND SEM.is_current_sem = 1 and DD.day_name = '"
                + dayname +"' and week = "+ week + "and DT.name_time = '"+timeName+"' \n" +
                "ORDER BY DD.id_dic_day_lesson, DT.id_dic_time_lesson, week";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idLGSS", LongType.INSTANCE)
                .addScalar("idLSCH", LongType.INSTANCE)
                .addScalar("week")
                .addScalar("room")
                .addScalar("teacher")
                .addScalar("dayName")
                .addScalar("timeName")
                .addScalar("subjectname")
                .addScalar("lesson")
                .setResultTransformer(Transformers.aliasToBean(ScheduleSubjectModel.class));
        return (List<ScheduleSubjectModel>) getList(q);
    }


    public ScheduleDateModel getDateOfSemester(String groupname){
        String query = "select  lgs.dateOfBeginSemester as dateOfBeginSemester, lgs.dateOfEndSemester, sem.season\n" +
                "from link_group_semester lgs\n" +
                "join dic_group dg using (id_dic_group)\n" +
                "join semester sem using (id_semester)\n" +
                "where sem.is_current_sem = 1 and dg.groupname = '"+groupname+"'";

        Query q = getSession().createSQLQuery(query)
                .addScalar("dateOfBeginSemester")
                .addScalar("dateOfEndSemester")
                .addScalar("season")
                .setResultTransformer(Transformers.aliasToBean(ScheduleDateModel.class));
        return (ScheduleDateModel) q.uniqueResult();
    }

    public int getCurrentWeek(Date selectDay, Date dateOfBeginSemester){
        String query = "SELECT case cast(((date '"+selectDay+"' - date '"+dateOfBeginSemester+"') / 7) AS int) % 2 when 0 then 1 else 2 end AS WEEK";
        Query q = getSession().createSQLQuery(query).addScalar("week");
        return (int) q.uniqueResult();
    }

    public int getCurrentWeekAlt(Date selectDay) {
        // Возвращает первое сентября этого или прошлого года, в зависимости от переданной даты
        String querySep ="SELECT CASE WHEN TO_DATE(concat(date_part('year', date '"+selectDay+"'),'-09-01'),'YYYY-MM-DD') > date '"+selectDay+"' " +
                "THEN concat(date_part('year', date_trunc('year', date '"+selectDay+"' - interval '1 year')),'-09-01')\n" +
                "\t\tELSE concat(date_part('year', date '"+selectDay+"'),'-09-01')\n" +
                "\t\tEND as firstSept";
        Query preQ = getSession().createSQLQuery(querySep)
                .addScalar("firstSept", DateType.INSTANCE);
        Date firstSept = (Date) getList(preQ).get(0);

        String query = "SELECT case cast(((date '"+selectDay+"'+3 - date '"+firstSept+"') / 7) AS int) % 2 when 0 then 1 else 2 end AS WEEK";
        Query q = getSession().createSQLQuery(query).addScalar("week");
        return (int) q.uniqueResult();
    }

    public int getCurrentWeekByUser(String login, Date selectDay){
        String preQuery = "SELECT lgs.dateofbeginsemester AS dateofbeginsemester\n" +
                "\tFROM link_group_semester lgs\n" +
                "\tINNER JOIN dic_group dg ON dg.id_dic_group = lgs.id_dic_group\n" +
                "\tAND dg.id_dic_group = (SELECT sci.id_current_dic_group FROM studentcard sci WHERE sci.ldap_login ilike :login)";
        Query preQ = getSession().createSQLQuery(preQuery);
        preQ.setParameter("login",login);
        Date dateOfBeginSemester = (Date) getList(preQ).get(0);

        String query = "SELECT case cast(((date '"+selectDay+"'+3 - date '"+dateOfBeginSemester+"') / 7) AS int) % 2 when 0 then 1 else 2 end AS WEEK";
        Query q = getSession().createSQLQuery(query).addScalar("week");
        return (int) q.uniqueResult();
    }

    public int getCurrentWeekNumberByUser(String login, Date selectDay){
        String preQuery = "SELECT lgs.dateofbeginsemester AS dateofbeginsemester\n" +
                "\tFROM link_group_semester lgs" +
                "\tINNER JOIN semester se ON lgs.id_semester = se.id_semester\n" +
                "\tINNER JOIN dic_group dg ON dg.id_dic_group = lgs.id_dic_group\n" +
                "\tAND dg.id_dic_group = (SELECT sci.id_current_dic_group FROM studentcard sci WHERE sci.ldap_login ilike :login)\n" +
                "\tWHERE se.is_current_sem=1";
        Query preQ = getSession().createSQLQuery(preQuery);
        preQ.setParameter("login",login);
        Date dateOfBeginSemester = (Date) getList(preQ).get(0);

        String query = "SELECT case cast(((date '"+selectDay+"'+1 - date '"+dateOfBeginSemester+"') / 7) AS int) AS WEEK";
        Query q = getSession().createSQLQuery(query).addScalar("week");
        return (int) q.uniqueResult();
    }

    public int getCurrentWeekNumberFos(Integer fos, Date selectDay){
        String preQuery = "SELECT  lgs.dateofbeginsemester AS dateofbeginsemester\n" +
                "FROM link_group_semester lgs\n" +
                "INNER JOIN semester se ON lgs.id_semester = se.id_semester\n" +
                "INNER JOIN schoolyear sy ON sy.id_schoolyear = se.id_schoolyear\n" +
                "WHERE \n" +
                "se.formofstudy=:fos \n" +
                "AND \n" +
                "se.id_institute = 1\n" +
                "AND\n" +
                "sy.dateofbegin<=date '"+selectDay+"'\n" +
                "AND\n" +
                "sy.dateofend>=date '"+selectDay+"'\n" +
                "AND\n" +
                "lgs.dateofbeginsemester is not null\n" +
                "AND\n" +
                "se.season = (CASE\n" +
                "    WHEN date_part('month', date '"+selectDay+"') < 9 THEN 1\n" +
                "    WHEN date_part('month', date '"+selectDay+"') >= 9 THEN 0\n" +
                "  END\n" +
                ")\n" +
                "LIMIT 1";
        Query preQ = getSession().createSQLQuery(preQuery);
        preQ.setParameter("fos",fos);
        Date dateOfBeginSemester = (Date) preQ.uniqueResult();
        if (dateOfBeginSemester == null) {
            return -1;
        }

        String query = "SELECT cast(((date '"+selectDay+"'+3 - date '"+dateOfBeginSemester+"') / 7) AS int) AS WEEK";
        Query q = getSession().createSQLQuery(query).addScalar("week");
        return (int) q.uniqueResult() + 1;
    }


    public List<String> getListTimeName(){
        String query = "select name_time as timeName from dic_time_lesson";
        Query q = getSession().createSQLQuery(query);

        return q.list();

    }
}
