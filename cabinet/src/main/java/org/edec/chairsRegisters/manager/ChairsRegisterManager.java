package org.edec.chairsRegisters.manager;

import org.edec.chairsRegisters.model.ChairsDepartmentModel;
import org.edec.chairsRegisters.model.ChairsRegisterModel;
import org.edec.dao.DAO;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.Date;
import java.util.List;

public class ChairsRegisterManager extends DAO {

    public List<ChairsRegisterModel> getMainRegisters(Long idDepartment, Long idSemester) {
    String query = "select distinct ds.subjectname as subjectname, dg.groupname as groupname, \n" +
            "\t\t\t\t  lgss.examdate as examdate, lgss.passdate as passdate, reg.register_number as registerNumber, \n" +
            "\t\t\t\t  reg.signdate as signdate, reg.id_register as idRegister, lgss.id_link_group_semester_subject as idLgss,second_sign_date_end as dateOfSecondSignEnd,\n" +
            "case\n" +
            "when srh.is_practic = 1  then 'Практика' \n" +
            "when srh.is_coursework = 1  then 'КР'\n" +
            "when srh.is_courseproject = 1  then 'КП'\n" +
            "when srh.is_exam = 1 then 'Экзамен'\n" +
            "when srh.is_pass = 1  then 'Зачет' end as foc,\n" +
            "reg.signatorytutor as signatorytutor,\n" +
            "srh.retake_count as retakeCount, d.id_department as idDepartment, lgs.id_semester as idSemester, " +
            "sem.id_institute as idInstitute, reg.certnumber as certnumber, cur.qualification as qualification\n" +
            "\n" +
            "from department d \n" +
            "\n" +
            "join subject sub on sub.id_chair = d.id_chair\n" +
            "join dic_subject ds on ds.id_dic_subject = sub.id_dic_subject\n" +
            "join link_group_semester_subject lgss on lgss.id_subject = sub.id_subject\n" +
            "join link_group_semester lgs using (id_link_group_semester)\n" +
            "left join semester sem on lgs.id_semester = sem.id_semester\n" +
            "left join dic_group dg using (id_dic_group)\n" +
            "join curriculum cur on cur.id_curriculum= dg.id_curriculum\n" +
            "left join sessionrating sr on sub.id_subject = sr.id_subject\n" +
            "left join sessionratinghistory srh on sr.id_sessionrating = srh.id_sessionrating\n" +
            "left join register reg on reg.id_register = srh.id_register\n" +
            "\n" +
            "where lgs.id_semester = "+idSemester+" and d.id_department = "+idDepartment+" and srh.retake_count in (1, -1)\n" +
            "order by subjectname";

        Query q = getSession().createSQLQuery(query)
                .addScalar("subjectname")
                .addScalar("retakeCount")
                .addScalar("idDepartment", LongType.INSTANCE)
                .addScalar("signdate")
                .addScalar("registerNumber")
                .addScalar("idRegister", LongType.INSTANCE)
                .addScalar("idSemester", LongType.INSTANCE)
                .addScalar("idInstitute", LongType.INSTANCE)
                .addScalar("idLgss", LongType.INSTANCE)
                .addScalar("dateOfSecondSignEnd")
                .addScalar("groupname")
                .addScalar("certnumber")
                .addScalar("foc")
                .addScalar("signatorytutor")
                .addScalar("qualification")
                .addScalar("examdate")
                .addScalar("passdate")
                .setResultTransformer(Transformers.aliasToBean(ChairsRegisterModel.class));

        return (List<ChairsRegisterModel>) q.list() ;
    }

    public String getTeachersByIdRegister (Long idLgss){
        String query = "select string_agg(distinct hf.family||' '||hf.name||' '||hf.patronymic, ', ')  as fioTeacher\n" +
                "from \n" +
                "humanface hf\n" +
                "join employee em using (id_humanface)\n" +
                "join link_employee_subject_group lesg on em.id_employee = lesg.id_employee\n" +
                "join link_group_semester_subject lgss using (id_link_group_semester_subject)\n" +
                "where lgss.id_link_group_semester_subject = " + idLgss;

        Query q = getSession().createSQLQuery(query);
        return (String) q.uniqueResult();
    }
    public String getTeachersForComission(Long idRegisterComission) {
        String query = "select string_agg(distinct hf.family||' '||hf.name||' '||hf.patronymic, ', ')  as fioTeacher\n" +
                "from humanface hf\n" +
                "join employee em using (id_humanface)\n" +
                "join link_employee_department led on em.id_employee = led.id_employee\n" +
                "join led_comission lc on lc.id_link_employee_department = led.id_link_employee_department\n" +
                "join link_employee_subject_group lesg on lesg.id_employee = em.id_employee\n" +
                "join link_group_semester_subject lgss using (id_link_group_semester_subject)\n" +
                "join subject sub on sub.id_subject = lgss.id_subject\n" +
                "join dic_subject ds using (id_dic_subject)\n" +
                "where lc.id_register_comission = " + idRegisterComission;

        Query q = getSession().createSQLQuery(query);

        return (String) q.uniqueResult();
    }

    //
    public ChairsDepartmentModel getIdDepartmentByIdHumanface (Long idHumanface){
        String query = "select distinct dep.id_department as idDepartment, dep.id_institute as idInst, dep.fulltitle as fulltitle,  dep.id_chair as idChair\n" +
                "from link_employee_department led\n" +
                "join employee em using (id_employee)\n" +
                "join department dep using (id_department)\n" +
                "join humanface hf using (id_humanface)\n" +
                "where led.id_employee_role IN (7,20,9) and hf.id_humanface = " + idHumanface + " and led.is_hide <> true";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idDepartment", LongType.INSTANCE)
                .addScalar("idInst", LongType.INSTANCE)
                .addScalar("fulltitle")
                .addScalar("idChair", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ChairsDepartmentModel.class));
        return (ChairsDepartmentModel) q.uniqueResult();
    }

    public List<ChairsRegisterModel> getRetakeRegister (Long idDepartment, Long idSemester){
        String query = "select distinct on  (reg.id_register) ds.subjectname as subjectname, srh.retake_count as retakeCount, reg.certnumber as certnumber, d.id_department as idDepartment,\n" +
                "       reg.signdate as signdate,   reg.register_number as registerNumber,reg.id_register as idRegister, dg.groupname as groupname,\n" +
                "       cur.qualification as qualification, reg.begindate as beginDate, reg.enddate as endDate,  sem.id_semester as idSemester, sem.id_institute as idInstitute, \n" +
                "     reg.second_sign_date_begin as secondBeginDate, reg.second_sign_date_end as secondEndDate, lgss.id_link_group_semester_subject as idLgss,\n" +
                "\n" +
                "       case\n" +
                "           when srh.is_practic = 1  then 'Практика' \n" +
                "           when srh.is_coursework = 1  then 'КР'\n" +
                "           when srh.is_courseproject = 1  then 'КП'\n" +
                "           when srh.is_exam = 1 then 'Экзамен'\n" +
                "           when srh.is_pass = 1  then 'Зачет' end as foc\n" +
                "from\n" +
                "link_group_semester lgs\n" +
                "    join semester sem on lgs.id_semester = sem.id_semester\n" +
                "join student_semester_status sss on lgs.id_link_group_semester = sss.id_link_group_semester\n" +
                "join sessionrating sr on sss.id_student_semester_status = sr.id_student_semester_status\n" +
                "join sessionratinghistory srh on sr.id_sessionrating = srh.id_sessionrating\n" +
                "join register reg on reg.id_register = srh.id_register\n" +
                "join dic_group dg on lgs.id_dic_group = dg.id_dic_group\n" +
                "join subject sub on sr.id_subject = sub.id_subject\n" +
                "    join link_group_semester_subject lgss on lgss.id_subject = sub.id_subject " +
                "join dic_subject ds on sub.id_dic_subject = ds.id_dic_subject\n" +
                "join chair ch on sub.id_chair = ch.id_chair\n" +
                "join department d on ch.id_chair = d.id_chair\n" +
                "join curriculum cur on cur.id_curriculum = dg.id_curriculum\n" +
                "where lgs.id_semester = "+idSemester+" and srh.retake_count in (2, 4 , -2, -4) and d.id_department = "+idDepartment+"\n" +
                "order by reg.id_register, ds.subjectname";

        Query q = getSession().createSQLQuery(query)
                .addScalar("subjectname")
                .addScalar("retakeCount")
                .addScalar("idDepartment", LongType.INSTANCE)
                .addScalar("signdate")
                .addScalar("registerNumber")
                .addScalar("idRegister", LongType.INSTANCE)
                .addScalar("idLgss", LongType.INSTANCE)
                .addScalar("groupname")
                .addScalar("certnumber")
                .addScalar("foc")
                .addScalar("qualification")
                .addScalar("secondEndDate")
                .addScalar("secondBeginDate")
                .addScalar("endDate")
                .addScalar("beginDate")
                .addScalar("idSemester", LongType.INSTANCE)
                .addScalar("idInstitute", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ChairsRegisterModel.class));

        return (List<ChairsRegisterModel>) q.list() ;
    }

    public List<ChairsRegisterModel> getRetakeRegisterForReport (Long idDepartment, Long idSemester, Date dateFrom, Date dateTo){
        String dateFromStr = DateConverter.convertDateToStringByFormat(dateFrom, "yyyy-MM-dd");
        String dateToStr = DateConverter.convertDateToStringByFormat(dateTo, "yyyy-MM-dd");

        String query = "select distinct on  (reg.id_register) ds.subjectname as subjectname, srh.retake_count as retakeCount, reg.certnumber as certnumber, d.id_department as idDepartment,\n" +
                "       reg.signdate as signdate,   reg.register_number as registerNumber,reg.id_register as idRegister, dg.groupname as groupname,\n" +
                "       EXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend)||' ('||CASE WHEN SEM.season = 0 THEN 'осень' ELSE 'весна' END||')' AS semesterStr,\n" +
                "       cur.qualification as qualification, reg.begindate as beginDate, reg.enddate as endDate,  sem.id_semester as idSemester, sem.id_institute as idInstitute, \n" +
                "     reg.second_sign_date_begin as secondBeginDate, reg.second_sign_date_end as secondEndDate, lgss.id_link_group_semester_subject as idLgss,\n" +
                "\n" +
                "       case\n" +
                "           when srh.is_practic = 1  then 'Практика' \n" +
                "           when srh.is_coursework = 1  then 'КР'\n" +
                "           when srh.is_courseproject = 1  then 'КП'\n" +
                "           when srh.is_exam = 1 then 'Экзамен'\n" +
                "           when srh.is_pass = 1  then 'Зачет' end as foc\n" +
                "from\n" +
                "link_group_semester lgs\n" +
                "    join semester sem on lgs.id_semester = sem.id_semester\n" +
                "join student_semester_status sss on lgs.id_link_group_semester = sss.id_link_group_semester\n" +
                "join sessionrating sr on sss.id_student_semester_status = sr.id_student_semester_status\n" +
                "join sessionratinghistory srh on sr.id_sessionrating = srh.id_sessionrating\n" +
                "join register reg on reg.id_register = srh.id_register\n" +
                "join dic_group dg on lgs.id_dic_group = dg.id_dic_group\n" +
                "join subject sub on sr.id_subject = sub.id_subject\n" +
                "    join link_group_semester_subject lgss on lgss.id_subject = sub.id_subject " +
                "join dic_subject ds on sub.id_dic_subject = ds.id_dic_subject\n" +
                "join schoolyear sy using (id_schoolyear) \n" +
                "join chair ch on sub.id_chair = ch.id_chair\n" +
                "join department d on ch.id_chair = d.id_chair\n" +
                "join curriculum cur on cur.id_curriculum = dg.id_curriculum\n" +
                "where  srh.retake_count in (-2, -4, 2, 4) and d.id_department = "+idDepartment+"\n" +
                "and ((reg.begindate >='"+dateFromStr+"' and reg.enddate <='"+dateToStr+"') or (reg.second_sign_date_begin >= '"+dateFromStr+"' and reg.second_sign_date_end <= '"+dateToStr+"')) " +
                "order by reg.id_register, reg.begindate,  reg.enddate , ds.subjectname";

        Query q = getSession().createSQLQuery(query)
                .addScalar("subjectname")
                .addScalar("retakeCount")
                .addScalar("idDepartment", LongType.INSTANCE)
                .addScalar("signdate")
                .addScalar("registerNumber")
                .addScalar("semesterStr")
                .addScalar("idRegister", LongType.INSTANCE)
                .addScalar("idLgss", LongType.INSTANCE)
                .addScalar("groupname")
                .addScalar("certnumber")
                .addScalar("foc")
                .addScalar("qualification")
                .addScalar("secondEndDate")
                .addScalar("secondBeginDate")
                .addScalar("endDate")
                .addScalar("beginDate")
                .addScalar("idSemester", LongType.INSTANCE)
                .addScalar("idInstitute", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ChairsRegisterModel.class));

        return (List<ChairsRegisterModel>) q.list() ;
    }
    public List<ChairsRegisterModel> getComissionRegisterList (Long idDepartment, Long idSemester){
        String query = "select distinct on  (reg.id_register) ds.subjectname as subjectname, srh.retake_count as RetakeCount, reg.certnumber as certnumber, " +
                "d.id_department as idDepartment, reg.signdate as signdate,   reg.register_number as registerNumber,reg.id_register as idRegister,  " +
                "sem.id_semester as idSemester, sem.id_institute as idInstitute,  string_agg(distinct dg.groupname, ', ') as groupname, " +
                "cur.qualification as qualification, rc.dateofbegincomission as dateBeginComission, rc.dateofendcomission as dateOfEndComission, " +
                "rc.id_register_comission as idComissionRegister,  (SELECT to_date(to_char(RC.comission_date, 'yyyy-MM-dd'), 'yyyy-MM-dd')) as comissionDate,\n" +
                "substring(cast (RC.comission_date as text), 12, 5) as timeCom, rc.classroom as classroom,\n" +
                " case\n" +
                "           when srh.is_practic = 1  then 'Практика' \n" +
                "           when srh.is_coursework = 1  then 'КР'\n" +
                "           when srh.is_courseproject = 1  then 'КП'\n" +
                "           when srh.is_exam = 1 then 'Экзамен'\n" +
                "           when srh.is_pass = 1  then 'Зачет' end as foc\n" +
                "from\n" +
                "link_group_semester lgs\n" +
                "    join semester sem on lgs.id_semester = sem.id_semester\n" +
                "join student_semester_status sss on lgs.id_link_group_semester = sss.id_link_group_semester\n" +
                "join sessionrating sr on sss.id_student_semester_status = sr.id_student_semester_status\n" +
                "join sessionratinghistory srh on sr.id_sessionrating = srh.id_sessionrating\n" +
                "join register reg on reg.id_register = srh.id_register\n" +
                "    join register_comission rc on reg.id_register = rc.id_register\n" +
                "join dic_group dg on lgs.id_dic_group = dg.id_dic_group\n" +
                "join subject sub on sr.id_subject = sub.id_subject\n" +
                "join dic_subject ds on sub.id_dic_subject = ds.id_dic_subject\n" +
                "join chair ch on sub.id_chair = ch.id_chair\n" +
                "join department d on ch.id_chair = d.id_chair\n" +
                "join curriculum cur on cur.id_curriculum = dg.id_curriculum\n" +
                "where lgs.id_semester = "+idSemester+" and srh.retake_count in (3, -3) and d.id_department = "+idDepartment+"\n" +
                "group by sem.id_semester, ds.subjectname, srh.retake_count, reg.certnumber, d.id_department, reg.signdate,   reg.register_number, reg.id_register,   sem.id_institute,  \n" +
                " cur.qualification, rc.dateofbegincomission, rc.dateofendcomission, rc.id_register_comission, RC.comission_date, RC.comission_date, rc.classroom,\n" +
                "srh.is_practic, srh.is_coursework, srh.is_courseproject, srh.is_exam, srh.is_pass \n"+
                "order by reg.id_register, ds.subjectname";

        Query q = getSession().createSQLQuery(query)
                .addScalar("subjectname")
                .addScalar("retakeCount")
                .addScalar("idDepartment", LongType.INSTANCE)
                .addScalar("idComissionRegister", LongType.INSTANCE)
                .addScalar("signdate")
                .addScalar("registerNumber")
                .addScalar("idRegister", LongType.INSTANCE)
                .addScalar("groupname")
                .addScalar("certnumber")
                .addScalar("foc")
                .addScalar("dateBeginComission")
                .addScalar("dateOfEndComission")
                .addScalar("qualification")
                .addScalar("comissionDate")
                .addScalar("timeCom")
                .addScalar("classroom")
                .addScalar("idSemester", LongType.INSTANCE)
                .addScalar("idInstitute", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ChairsRegisterModel.class));

        return (List<ChairsRegisterModel>) q.list() ;
    }

    public Long getCurrentSem(int fos, Long idInst) {
        String query = "select id_semester\n" +
                "from semester\n" +
                "where is_current_sem = 1 and formofstudy = "+fos+" and id_institute = " + idInst;
        Query q = getSession().createSQLQuery(query);
        return (Long) q.uniqueResult();
    }
}
