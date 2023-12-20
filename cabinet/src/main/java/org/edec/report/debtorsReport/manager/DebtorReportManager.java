package org.edec.report.debtorsReport.manager;

import org.edec.dao.DAO;
import org.edec.report.debtorsReport.model.DebtorReportModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DebtorReportManager extends DAO {

    public List<DebtorReportModel> getDebtorsBySemesters(List<Long> idSemesters) {
        String semStr = idSemesters.stream().map(Object::toString).collect(Collectors.joining(", "));

        String query = "with students as (select sc.other_dbuid as idStudentMine, lgs.id_dic_group as idLgs, sc.id_studentcard as idSc,\n" +
                "                                 hf.family || ' ' || hf.name || ' ' || hf.patronymic as fio,\n" +
                "                                 count(sr.is_exam + sr.is_pass + sr.is_coursework + sr.is_courseproject + sr.is_practic) OVER (PARTITION BY id_studentcard) as countDebts, " +
                "                          groupname, subjectname, lgs.id_semester as idSemester, cast(round(cast(hourscount/36 as numeric), 1) as text) as ZE,\n" +
                "                                 case when sem.season = 1 then 'весна ' else 'осень ' end\n" +
                "                                 || extract(year from sch.dateofbegin) || '/' || extract(year from sch.dateofend)\n" +
                "                                                                                     as semester,\n" +
                "                                 case when isstudentbudgetcurrentsem(id_studentcard) = 1 then '' else '+' end as payment,\n" +
                "                                 string_agg(h2.family || ' ' || h2.name || ' ' || h2.patronymic, ', ') as teachers,\n" +
                "                                 sc.other_dbuid as idShahty,\n" +
                "                                 department.fulltitle as subjectDepartment,\n" + "                    case\n" +
                "                      when sr.is_exam = 1 then 'Экзамен'\n" +
                "                      when sr.is_pass = 1 then 'Зачет'\n" +
                "                      when sr.is_coursework = 1 then 'КР'\n" +
                "                      when sr.is_courseproject = 1 then 'КП'\n" +
                "                      when sr.is_practic = 1 then 'Практика'\n" + "                    else '' end as foc,\n" +
                "sr.is_exam as is_exam, sr.is_pass as is_pass, sr.is_coursework as is_cw, sr.is_courseproject as is_cp, sr.is_practic as is_practice,sr.examrating, sr.passrating, sr.courseworkrating, sr.courseprojectrating, sr.practicrating, \n" +
                "   EXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory WHERE is_exam = 1 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS examComm,\n" +
                "   EXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory WHERE is_pass = 1 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS passComm,\n" +
                "   EXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory WHERE is_courseproject = 1 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS cpComm,\n" +
                "   EXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory WHERE is_coursework = 1 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS cwComm,\n" +
                "   EXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory WHERE is_practic = 1 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS practicComm " +
                "                  from studentcard sc\n" + "                    inner join humanface hf using (id_humanface)\n" +
                "                    inner join student_semester_status sss using (id_studentcard)\n" +
                "                    inner join link_group_semester lgs using (id_link_group_semester)\n" +
                "                    inner join semester sem using (id_semester)\n" +
                "                    inner join schoolyear sch using (id_schoolyear)\n" +
                "                    inner join dic_group dg ON sc.id_current_dic_group = dg.id_dic_group and lgs.id_dic_group = dg.id_dic_group\n" +
                "                    inner join curriculum cur using(id_curriculum)\n" +
                "                    inner join sessionrating sr on (\n" +
                "                      sss.id_student_semester_status = sr.id_student_semester_status\n" +
                "                      and (\n" + "                        (is_pass = 1 and passrating not in (1, 3, 4, 5) and esopassrating <> -1)\n" +
                "                        or (is_exam = 1 and examrating not in (3, 4, 5) and esoexamrating <> -1)\n" +
                "                        or (is_coursework = 1 and courseworkrating not in (3, 4, 5) and esocourseworkrating <> -1)\n" +
                "                        or (is_courseproject = 1 and courseprojectrating not in (3, 4, 5) and esocourseprojectrating <> -1)\n" +
                "                        or is_practic = 1 and (practicrating not in (1, 3, 4, 5) and passrating not in (1, 3, 4, 5))\n" + "                      )\n" +
                "                      )\n" +
                "                    inner join subject s2 on sr.id_subject = s2.id_subject\n" +
                "                    join department on s2.id_chair = department.id_chair and department.is_main = TRUE\n" +
                "                    inner join dic_subject s3 on s2.id_dic_subject = s3.id_dic_subject\n" +
                "                    inner join link_group_semester_subject s4 on s4.id_subject = s2.id_subject\n" +
                "                    inner join link_employee_subject_group g on s4.id_link_group_semester_subject = g.id_link_group_semester_subject\n" +
                "                    inner join employee using(id_employee)\n" +
                "                    inner join humanface h2 on employee.id_humanface = h2.id_humanface\n" +
                "                  where lgs.id_semester in (" + semStr + ")\n" +
                "                        and getcurrentsemesterforstudent(sc.id_studentcard) is not null\n" +
                "                        and isstudentdeductedcurrentsem(sc.id_studentcard) = 0\n" +
                "                        and isstudentacademicleavecurrentsem(sc.id_studentcard) = 0\n" +
                "                        and cur.formofstudy = sem.formofstudy\n" +
                "                        and groupname not like 'ВЦ%'\n" +
                "                  group by hf.family, hf.name, hf.patronymic, groupname, subjectname, sss.id_student_semester_status, department.fulltitle, sc.other_dbuid,\n" +
                "                    id_studentcard, sem.season, sch.dateofbegin, sch.dateofend, lgs.id_semester, dg.id_dic_group, sem.id_semester, lgs.id_dic_group, " +
                "                    s2.hourscount, sr.is_exam, sr.is_pass, sr.is_courseproject, sr.is_coursework, sr.is_practic,sr.examrating, sr.passrating, sr.courseworkrating, sr.courseprojectrating, sr.practicrating,  examComm, passComm, cpComm,  cwComm, practicComm\n" +
                "                  order by groupname, hf.family, hf.name, hf.patronymic\n" + ")     select\n" +
                "        students.fio,\n" + "        students.groupname,\n" + "        students.subjectname, students.ZE as ZE,\n" +
                "        students.subjectDepartment,\n" + "        students.semester,\n" + "        students.teachers,\n" +
                "        students.payment as notBudget,\n" + "        students.countDebts,\n" + "        students.idSemester,\n" +
                "        students.foc, students.idStudentMine,\n" + "students.examComm , students.passComm , students.cpComm , students.cwComm, students.practicComm, " +
                "               students.is_exam, students.is_pass, students.is_cw, students.is_cp,  students.is_practice, students.examrating, students.passrating, students.courseworkrating, students.courseprojectrating, students.practicrating " +
                " from students order by groupname, fio;\n" + "\n" + "\n";

        Query q = getSession()
                .createSQLQuery(query)
                .addScalar("fio")
                .addScalar("groupname")
                .addScalar("subjectname")
                .addScalar("subjectDepartment")
                .addScalar("semester")
                .addScalar("teachers")
                .addScalar("notBudget")
                .addScalar("ZE")
                .addScalar("idStudentMine", LongType.INSTANCE)
                .addScalar("foc")
                .addScalar("idSemester", LongType.INSTANCE)
                .addScalar("countDebts", StringType.INSTANCE)
                .addScalar("is_exam", IntegerType.INSTANCE)
                .addScalar("is_pass", IntegerType.INSTANCE)
                .addScalar("is_cw", IntegerType.INSTANCE)
                .addScalar("is_cp", IntegerType.INSTANCE)
                .addScalar("is_practice", IntegerType.INSTANCE)
                .addScalar("practicComm", BooleanType.INSTANCE)
                .addScalar("examComm", BooleanType.INSTANCE)
                .addScalar("passComm", BooleanType.INSTANCE)
                .addScalar("cpComm", BooleanType.INSTANCE)
                .addScalar("cwComm", BooleanType.INSTANCE)
                .addScalar("examrating", IntegerType.INSTANCE)
                .addScalar("passrating", IntegerType.INSTANCE)
                .addScalar("courseworkrating", IntegerType.INSTANCE)
                .addScalar("courseprojectrating", IntegerType.INSTANCE)
                .addScalar("practicrating", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(DebtorReportModel.class));

        return (List<DebtorReportModel>) getList(q);
    }

    public List<DebtorReportModel> getDebtorsBySemestersWithUnsignedRegisters(List<Long> idSemesters) {
        String semStr = idSemesters.stream().map(Object::toString).collect(Collectors.joining(", "));

        String query = "with students as (select sc.other_dbuid as idStudentMine, lgs.id_dic_group as idLgs, sc.id_studentcard as idSc,\n" +
                "                         hf.family || ' ' || hf.name || ' ' || hf.patronymic as fio,\n" +
                "                    count(sr.is_exam + sr.is_pass + sr.is_coursework + sr.is_courseproject + sr.is_practic) OVER (PARTITION BY id_studentcard) as countDebts,\n" +
                "                    groupname,\n" + "                    subjectname, lgs.id_semester as idSemester,\n" +
                "                    case when sem.season = 1 then 'весна ' else 'осень ' end\n" +
                "                    || extract(year from sch.dateofbegin) || '/' || extract(year from sch.dateofend)\n" +
                "                      as semester, cast(round(cast(hourscount/36 as numeric), 1) as text) as ZE,\n" +
                "                    case when isstudentbudgetcurrentsem(id_studentcard) = 1 then '' else '+' end as payment,\n" +
                "                    string_agg(h2.family || ' ' || h2.name || ' ' || h2.patronymic, ', ') as teachers,\n" +
                "                    sc.other_dbuid as idShahty,\n" +
                "                    department.fulltitle as subjectDepartment,\n" + "                    case\n" +
                "                    when sr.is_exam = 1 then 'Экзамен'\n" +
                "                    when sr.is_pass = 1 then 'Зачет'\n" +
                "                    when sr.is_coursework = 1 then 'КР'\n" +
                "                    when sr.is_courseproject = 1 then 'КП'\n" +
                "                    when sr.is_practic = 1 then 'Практика'\n" + "                    else '' end as foc, " +
                " sr.is_exam as is_exam, sr.is_pass as is_pass, sr.is_coursework as is_cw, sr.is_courseproject as is_cp, sr.is_practic as is_practice, sr.examrating, sr.passrating, sr.courseworkrating, sr.courseprojectrating, sr.practicrating, \n" +
                " EXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory WHERE is_exam = 1 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS examComm,\n" +
                "\tEXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory WHERE is_pass = 1 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS passComm,\n" +
                "\tEXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory WHERE is_courseproject = 1 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS cpComm,\n" +
                "\tEXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory WHERE is_coursework = 1 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS cwComm,\n" +
                "\tEXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory WHERE is_practic = 1 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS practicComm\n" +
                "                  from studentcard sc\n" + "                    inner join humanface hf using (id_humanface)\n" +
                "                    inner join student_semester_status sss using (id_studentcard)\n" +
                "                    inner join link_group_semester lgs using (id_link_group_semester)\n" +
                "                    inner join semester sem using (id_semester)\n" +
                "                    inner join schoolyear sch using (id_schoolyear)\n" +
                "                    inner join dic_group dg ON sc.id_current_dic_group = dg.id_dic_group and lgs.id_dic_group = dg.id_dic_group\n" +
                "                    inner join curriculum cur using(id_curriculum)\n" +
                "                    inner join sessionrating sr on (\n" +
                "                      sss.id_student_semester_status = sr.id_student_semester_status\n" +
                "                      and (\n" + "                        (is_pass = 1 and passrating not in (1, 3, 4, 5) and esopassrating <> -1)\n" +
                "                        or (is_exam = 1 and examrating not in (3, 4, 5) and esoexamrating <> -1)\n" +
                "                        or (is_coursework = 1 and courseworkrating not in (3, 4, 5) and esocourseworkrating <> -1)\n" +
                "                        or (is_courseproject = 1 and courseprojectrating not in (3, 4, 5) and esocourseprojectrating <> -1)\n" +
                "                        or is_practic = 1 and practicrating not in (1, 3, 4, 5)\n" + "                      )\n" +
                "                    )\n" +
                "                    left join sessionratinghistory srh on srh.id_sessionratinghistory = (select max(id_sessionratinghistory) from sessionratinghistory where id_sessionrating = sr.id_sessionrating)\n" +
                "                    inner join subject s2 on sr.id_subject = s2.id_subject\n" +
                "                    join department on s2.id_chair = department.id_chair and department.is_main = TRUE\n" +
                "                    inner join dic_subject s3 on s2.id_dic_subject = s3.id_dic_subject\n" +
                "                    inner join link_group_semester_subject s4 on s4.id_subject = s2.id_subject\n" +
                "                    inner join link_employee_subject_group g on s4.id_link_group_semester_subject = g.id_link_group_semester_subject\n" +
                "                    inner join employee using(id_employee)\n" +
                "                    inner join humanface h2 on employee.id_humanface = h2.id_humanface\n" +
                "                  where lgs.id_semester in (" + semStr + ")\n" +
                "                        and getcurrentsemesterforstudent(sc.id_studentcard) is not null\n" +
                "                        and isstudentdeductedcurrentsem(sc.id_studentcard) = 0\n" +
                "                        and isstudentacademicleavecurrentsem(sc.id_studentcard) = 0\n" +
                "                        and cur.formofstudy = sem.formofstudy\n" +
                "                        and groupname not like 'ВЦ%'\n" +
                "                        and (newrating not in (1,3,4,5) or newrating is null)\n" +
                "                  group by hf.family, hf.name, hf.patronymic, groupname, subjectname, sss.id_student_semester_status, department.fulltitle, sc.other_dbuid,\n" +
                "                    id_studentcard, sem.season, sch.dateofbegin, sch.dateofend, lgs.id_semester, dg.id_dic_group, sem.id_semester, lgs.id_dic_group, sr.is_exam,\n" +
                "                   s2.hourscount, sr.examrating, sr.passrating, sr.courseworkrating, sr.courseprojectrating, sr.practicrating, \n" +
                "                    sr.is_pass, sr.is_courseproject, sr.is_coursework, sr.is_practic, srh.id_sessionratinghistory, examComm, passComm, cpComm,  cwComm,practicComm\n" +
                "                  order by groupname, hf.family, hf.name, hf.patronymic)\n" +
                "        select\n" +
                "        students.fio, students.groupname, students.subjectname, students.subjectDepartment, students.semester,  students.teachers,\n" +
                "        students.payment as notBudget, students.ZE as ZE, students.countDebts, students.idSemester, students.is_exam, students.is_pass, students.is_cw, students.is_cp,  students.is_practice,\n" +
                "       students.examrating, students.passrating, students.courseworkrating, students.courseprojectrating, students.practicrating ,\n" +
                "        students.foc, students.idStudentMine, students.examComm , students.passComm , students.cpComm , students.cwComm, students.practicComm\n" +
                "        from students order by groupname, fio;\n";

        Query q = getSession()
                .createSQLQuery(query)
                .addScalar("fio")
                .addScalar("groupname")
                .addScalar("subjectname")
                .addScalar("subjectDepartment")
                .addScalar("semester")
                .addScalar("teachers")
                .addScalar("notBudget")
                .addScalar("ZE")
                .addScalar("idStudentMine", LongType.INSTANCE)
                .addScalar("foc")
                .addScalar("idSemester", LongType.INSTANCE)
                .addScalar("countDebts", StringType.INSTANCE)
                .addScalar("is_exam", IntegerType.INSTANCE)
                .addScalar("is_pass", IntegerType.INSTANCE)
                .addScalar("is_cw", IntegerType.INSTANCE)
                .addScalar("is_cp", IntegerType.INSTANCE)
                .addScalar("is_practice", IntegerType.INSTANCE)
                .addScalar("practicComm", BooleanType.INSTANCE)
                .addScalar("examComm", BooleanType.INSTANCE)
                .addScalar("passComm", BooleanType.INSTANCE)
                .addScalar("cpComm", BooleanType.INSTANCE)
                .addScalar("cwComm", BooleanType.INSTANCE)
                .addScalar("examrating", IntegerType.INSTANCE)
                .addScalar("passrating", IntegerType.INSTANCE)
                .addScalar("courseworkrating", IntegerType.INSTANCE)
                .addScalar("courseprojectrating", IntegerType.INSTANCE)
                .addScalar("practicrating", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(DebtorReportModel.class));

        return (List<DebtorReportModel>) getList(q);
    }
}
