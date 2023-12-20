package org.edec.teacher.manager;

import org.edec.dao.DAO;
import org.edec.teacher.model.*;
import org.edec.teacher.model.commission.CommissionModel;
import org.edec.teacher.model.commission.EmployeeModel;
import org.edec.teacher.model.commission.StudentModel;
import org.edec.teacher.model.dao.CompletionCommESOmodel;
import org.edec.teacher.model.dao.CompletionESOModel;
import org.edec.teacher.model.dao.CourseHistoryESOModel;
import org.edec.utility.constants.FormOfControlConst;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.zkoss.zhtml.Sub;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class EntityManagerCompletion extends DAO {
    public List<CompletionESOModel> getCompletionESOModel(Long idHum, boolean unSignedRegister) {
        String query =
                "SELECT\n \tCOALESCE(INST.shorttitle, INST.fulltitle) AS institute," +
                        " SEM.formofstudy, SEM.season, LGS.semesternumber, SEM.id_semester AS idSemester, \n" +
                        "\tCASE WHEN SEM.season = 0 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' осенний'\n" +
                        "\t\tWHEN SEM.season = 1 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' весенний' END AS semesterStr,\n" +
                        "\t(SEM.is_current_sem=1) AS curSem,INST.id_institute AS idInstitute, LGSS.id_link_group_semester_subject AS idLGSS, DG.groupname, DS.subjectname, \n" +
                        "\tS.is_exam=1 AS exam, S.is_pass=1 AS pass, S.is_courseproject=1 AS cp, S.is_coursework=1 AS cw, S.is_Practic=1 AS practic,\n" +
                        "\tSY.dateofbegin, LGSS.id_esocourse2 AS idESOcourse,LGSS.examdate as examdate, LGSS.passdate as passdate, LGSS.tmpcourseprojectdate as cpdate,\n" +
                        "\tLGSS.tmpcourseworkdate as cwdate, LGSS.practicdate as practicdate, SY.dateofend, S.hourscount AS hoursCount, S.type, LGS.course\n" +
                        "FROM employee EMP\n" +
                        "\tINNER JOIN link_employee_subject_group LESG USING (id_employee)\n" +
                        "\tINNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester_subject)\n" +
                        "\tINNER JOIN subject S USING (id_subject)\n" +
                        "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                        "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                        "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                        "\tINNER JOIN semester SEM USING (id_semester)\n" +
                        "\tINNER JOIN institute INST ON SEM.id_institute = INST.id_institute\n" +
                        "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                        "\tLEFT JOIN sessionrating SR USING (id_subject)\n" +
                        "\tLEFT JOIN student_semester_status SSS ON SR.id_student_semester_status = SSS.id_student_semester_status\n" +
                        "\tLEFT JOIN sessionratinghistory SRH ON SR.id_sessionrating = SRH.id_sessionrating" +
                        "\t\tAND SRH.status NOT IN ('1.4.0', '1.4.1', '1.5.0', '1.5.1')\n" +
                        "\t\tAND SRH.retake_count IN (" + (unSignedRegister ? "1,2,4" : "") + "-1, -2, -4)" +
                        "\t\tAND SSS.is_academicleave = 0 AND SSS.is_deducted = 0\n" +
                        (unSignedRegister ? "\tLEFT JOIN register R ON SRH.id_register = R.id_register\n" : "") +
                        "WHERE EMP.id_humanface = :idHumanface\n" +
                        "GROUP BY institute, SEM.formofstudy, SEM.season, LGS.semesternumber, idSemester, semesterStr,\n" +
                        "\tcurSem, idInstitute, idLGSS, DG.groupname, DS.subjectname,\n" +
                        "\texam, pass, cp, cw, practic, SY.dateofbegin, idESOcourse, SY.dateofend, hoursCount, S.type, LGS.course\n" +
                        (unSignedRegister ? "\tHAVING MIN(R.signdate) is null" : "");
        Query q = getSession().createSQLQuery(query)
                .addScalar("institute").addScalar("formofstudy").addScalar("semesterStr")
                .addScalar("season").addScalar("curSem").addScalar("idLGSS", LongType.INSTANCE)
                .addScalar("groupname").addScalar("subjectname")
                .addScalar("exam").addScalar("pass").addScalar("cp").addScalar("cw").addScalar("practic")
                .addScalar("examdate").addScalar("passdate").addScalar("cpdate")
                .addScalar("cwdate").addScalar("practicdate")
                .addScalar("dateofbegin").addScalar("idESOcourse", LongType.INSTANCE)
                .addScalar("dateofend").addScalar("idSemester", LongType.INSTANCE)
                .addScalar("hoursCount").addScalar("semesterNumber").addScalar("type")
                .addScalar("course").addScalar("idInstitute", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(CompletionESOModel.class));

        q.setLong("idHumanface", idHum);
        return (List<CompletionESOModel>) getList(q);
    }


    public List<CompletionCommESOmodel> getCompletionCommESOmodel(Long idHum, Long idRegister, boolean allCommission, boolean signed) {

        String additionalConditions = allCommission
                ? ""
                : idRegister == null && signed
                    ? " AND R.certnumber is not null"
                    : " AND R.certnumber is null";

                String query = "SELECT\n" +
                               "\tDISTINCT ON (R.id_register)\n" +
                "\tCOALESCE(INST.shorttitle, " +
                               "INST.fulltitle) AS institute, " +
                               "SEM.formofstudy, " +
                               "SEM.id_semester AS idSem,\n" +
                "\tCASE\n" +
                "\t\tWHEN SEM.season = 0 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' осенний'\n" +
                "\t\tWHEN SEM.season = 1 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' весенний'\n" +
                "\tEND AS semesterStr,(SEM.is_current_sem=1) AS curSem, SY.dateofbegin AS dateOfBeginSY, SEM.season, \n" +
                "\tDS.subjectname AS subjectName, RC.comission_date AS dateOfCommission, RC.id_register_comission AS idRC,\n" +
                "\tR.id_register AS idReg, R.signatorytutor,  R.certnumber, R.register_number AS regNumber,\n" +
                "\tCAST(LGS.course AS TEXT), SR.type, \n" + "\tCASE\n" + "\t\tWHEN SRH.is_exam = 1 THEN 1 \n" +
                "\t\tWHEN SRH.is_pass = 1 THEN 2\n" + "\t\tWHEN SRH.is_courseproject = 1 THEN 3\n" +
                "\t\tWHEN SRH.is_coursework = 1 THEN 4\n" + "\t\tWHEN SRH.is_practic = 1 THEN 5\n" +
                "\tELSE 0 END AS formOfControl, " +
                "LGS.semesternumber,  " +
                " substring(cast (RC.comission_date as text), 12, 5) as timeCom, " +
                "rc.classroom as classroom,\n" +
                "s.hourscount as hoursCount \n" +
                "FROM\n" +
                "\tled_comission LD\n" +
                "\tINNER JOIN link_employee_department LED USING (id_link_employee_department)\n" +
                "\tINNER JOIN employee EMP USING (id_employee)\n" +
                "\tINNER JOIN register_comission RC USING (id_register_comission)\n" +
                "\tINNER JOIN subject S USING (id_subject)\n" +
                "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "\tINNER JOIN register R USING (id_register)\n" +
                "\tINNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status )\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "\tINNER JOIN institute INST USING (id_institute)\n" +
                "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                "WHERE CAST(EMP.id_humanface AS TEXT) ILIKE :idHum \n" +
                "\tAND CAST(RC.id_register AS TEXT) ILIKE :idRegister\n" + additionalConditions;

        Query q = getSession().createSQLQuery(query)
                .addScalar("institute")
                .addScalar("formofstudy")
                .addScalar("idSem", LongType.INSTANCE)
                .addScalar("semesternumber")
                .addScalar("timeCom")
                .addScalar("classroom")
                .addScalar("season")
                .addScalar("semesterStr")
                .addScalar("curSem")
                .addScalar("subjectName")
                .addScalar("dateOfBeginSY")
                .addScalar("dateOfCommission")
                .addScalar("idRC", LongType.INSTANCE)
                .addScalar("idReg", LongType.INSTANCE)
                .addScalar("signatorytutor")
                .addScalar("certnumber")
                .addScalar("regNumber")
                .addScalar("type")
                .addScalar("course")
                .addScalar("formOfControl")
                              .addScalar("hoursCount")
                .setResultTransformer(Transformers.aliasToBean(CompletionCommESOmodel.class));
        q.setParameter("idHum", idHum == null ? "%%" : String.valueOf(idHum), StringType.INSTANCE)
                .setParameter("idRegister", idRegister == null ? "%%" : String.valueOf(idRegister), StringType.INSTANCE);
        return (List<CompletionCommESOmodel>) getList(q);
    }

    public List<EmployeeModel> getEmployeeByCommission(Long idRegister) {
        String query = "SELECT\n" + "\tHFteacher.family||' '||HFteacher.name||' '||HFteacher.patronymic AS fio, LD.leader AS chairman\n" +
                "FROM register_comission RC\n" +
                "\tINNER JOIN led_comission LD USING (id_register_comission)\n" +
                "\tINNER JOIN link_employee_department LED USING (id_link_employee_department)\n" +
                "\tINNER JOIN employee EMP USING (id_employee)\n" +
                "\tINNER JOIN humanface HFteacher USING (id_humanface)\n" +
                "WHERE id_register = :idRegister\n" +
                "ORDER BY\n" + "\tLD.leader DESC, fio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("chairman", NumericBooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(EmployeeModel.class));
        q.setLong("idRegister", idRegister);
        return (List<EmployeeModel>) getList(q);
    }

    public List<StudentModel> getStudentsByCommission(Long idRegister) {
        String query = "SELECT\n" +
                "\tHF.family||' '||SUBSTRING(HF.name, 1, 1)||'. '||SUBSTRING(HF.patronymic, 1, 1)||'.' AS fio,DG.groupname AS groupName,\n" +
                "\tCASE\n" + "\t\tWHEN newrating = 1 THEN 'Зачет'\n" + "\t\tWHEN newrating = 2 THEN 'Неудовл.'\n" +
                "\t\tWHEN newrating = 3 THEN 'Удовл.'\n" + "\t\tWHEN newrating = 4 THEN 'Хорошо'\n" +
                "\t\tWHEN newrating = 5 THEN 'Отлично'\n" + "\t\tWHEN newrating = -2 THEN 'Незачет'\n" +
                "\t\tWHEN newrating = -3 THEN 'Н.Я.'\n" +
                "\tELSE '' END AS ratingStr, SRH.newrating AS rating, " +
                "SRH.id_sessionratinghistory AS idSRH,\n" +
                "SRH.check_commission AS checkCommision\n" +
                "FROM\n" +
                "\tregister_comission RC\n" + "\tINNER JOIN subject S USING (id_subject)\n" +
                "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" + "\tINNER JOIN register R USING (id_register)\n" +
                "\tINNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "\tINNER JOIN institute INST USING (id_institute)\n" + "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN studentcard SC USING (id_studentcard)\n" + "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "WHERE\n" + "\tid_register = :idRegister\n" +
                "ORDER BY\n" + "\tfio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("groupname")
                .addScalar("rating")
                .addScalar("ratingStr")
                .addScalar("idSRH", LongType.INSTANCE)
                .addScalar("checkCommision", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(StudentModel.class));
        q.setLong("idRegister", idRegister);
        return (List<StudentModel>) getList(q);
    }

    public List<EsoCourseModel> getEsoCourses() {
        String query = "SELECT id_esocourse2 AS idEsoCourse, id_category AS idCategory, fullname, shortname \n" + "FROM esocourse2 \n" +
                "ORDER BY fullname";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idCategory")
                .addScalar("idEsoCourse", LongType.INSTANCE)
                .addScalar("fullname")
                .addScalar("shortname")
                .setResultTransformer(Transformers.aliasToBean(EsoCourseModel.class));

        return (List<EsoCourseModel>) getList(q);
    }

    public boolean updateRating(Integer rating, Long idSRH) {
        String query = "UPDATE sessionratinghistory " +
                       "SET newrating = " + rating + "," +
                       "changedatetime = now()" +
                       " WHERE id_sessionratinghistory =" + idSRH;

        Query q = getSession().createSQLQuery(query);
        return executeUpdate(q);
    }

    public boolean updateIdESOcourse(Long idLGSS, Long idESOcourse) {
        String query = "UPDATE link_group_semester_subject SET id_esocourse2 = :idESOcourse WHERE id_link_group_semester_subject = :idLGSS";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idLGSS", idLGSS).setParameter("idESOcourse", idESOcourse, LongType.INSTANCE);
        return executeUpdate(q);
    }

    public List<CourseHistoryESOModel> getAvailableCoursesForBinding(long idHumanface, long idCurSem) {
        String query =
                "select emp.id_employee as idEmp,\n" + "ds1.subjectname as subjectname,\n" + "lgss2.id_esocourse2 as idEsoCourse2,\n" +
                        "ec2.fullname as fullname,\n" + "lgss1.id_link_group_semester_subject as idLGSS,\n" + "dg.groupname as groupName\n" +
                        "from employee emp\n" + "inner join humanface hf using (id_humanface)\n" +
                        "inner join link_employee_subject_group lesg1 on emp.id_employee = lesg1.id_employee\n" +
                        "inner join link_group_semester_subject lgss1 on lesg1.id_link_group_semester_subject = lgss1.id_link_group_semester_subject\n" +
                        "inner join subject subj1 on lgss1.id_subject = subj1.id_subject\n" +
                        "inner join dic_subject ds1 on subj1.id_dic_subject = ds1.id_dic_subject\n" +
                        "inner join link_group_semester lgs1 on lgss1.id_link_group_semester = lgs1.id_link_group_semester\n" +
                        "inner join dic_group dg on lgs1.id_dic_group = dg.id_dic_group\n" +
                        "inner join link_employee_subject_group lesg2 on emp.id_employee = lesg2.id_employee\n" +
                        "inner join link_group_semester_subject lgss2 on lesg2.id_link_group_semester_subject = lgss2.id_link_group_semester_subject\n" +
                        "inner join subject subj2 on lgss2.id_subject = subj2.id_subject\n" +
                        "inner join dic_subject ds2 on subj2.id_dic_subject = ds2.id_dic_subject\n" +
                        "inner join esocourse2 ec2 on lgss2.id_esocourse2 = ec2.id_esocourse2\n" +
                        "inner join link_group_semester lgs2 on lgss2.id_link_group_semester = lgs2.id_link_group_semester\n" + "where \n" +
                        "id_humanface = " + idHumanface + "\n" + "AND lgss1.id_esocourse2 IS NULL\n" + "AND lgs1.id_semester = " + idCurSem + "\n" +
                        "AND lgss2.id_esocourse2 IS NOT NULL\n" + "AND lgs2.id_semester != " + idCurSem + "\n" +
                        "AND ds1.subjectname = ds2.subjectname\n" +
                        "GROUP BY lgss1.id_link_group_semester_subject, emp.id_employee, ds1.subjectname, lgss2.id_esocourse2, ec2.fullname,dg.groupname\n" +
                        "ORDER BY ds1.subjectname,dg.groupname";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idLGSS", LongType.INSTANCE)
                .addScalar("idEmp", LongType.INSTANCE)
                .addScalar("subjectname")
                .addScalar("idEsoCourse2", LongType.INSTANCE)
                .addScalar("fullname")
                .addScalar("groupName")
                .setResultTransformer(Transformers.aliasToBean(CourseHistoryESOModel.class));

        return (List<CourseHistoryESOModel>) getList(q);
    }

    public boolean updateRegisterNumber(Long idReg, Long idSem, String suffixForNumber) {
        String query = "SELECT * FROM updateRegisterNumber(" + idReg + ", " + idSem + ", '" + suffixForNumber + "')";
        return callFunction(query);
    }

    public boolean updateRegisterSrSrhAfterSign(Long idReg, String regUrl, String serialNumber, String thumbPrint, String FIO,
                                                String statusSR, String statusSRH) {
        String query = "SELECT register_sign(" + idReg + ",'" + regUrl + "','" + serialNumber + "','" + thumbPrint + "', '" + FIO + "', " +
                (statusSR == null ? "null" : ("'" + statusSR + "'")) + ", " +
                (statusSRH == null ? "null" : ("'" + statusSRH + "'")) + ");";

        return callFunction(query);
    }

    public boolean updateRegisterAfterConfirmWithDigitalSign (Long idReg, String serialNumber, String thumbprint) {
        String query = "update register\n" +
                "set certnumber = '"+serialNumber+"', thumbprint = '"+thumbprint+"'\n" +
                "where id_register = " + idReg;
        return  callFunction(query);
    }


    private String getFioStudentForIndRetake (Long idSem, Long idRegister) {
        String query = "select  hf.family||' '||hf.name||' '||hf.patronymic as fio\n" +
                "from humanface hf \n" +
                "join studentcard sc using (id_humanface)\n" +
                "join student_semester_status sss using (id_studentcard)\n" +
                "join sessionrating sr using (id_student_semester_status)\n" +
                "join sessionratinghistory srh using (id_sessionrating)\n" +
                "join register reg using (id_register)\n" +
                "where reg.id_register = " + idRegister +
                "and (reg.id_semester = "+idSem+" or reg.id_semester is null) and srh.retake_count in (4, -4)\n" +
                "limit 1";
        Query q = getSession().createSQLQuery(query);
        return q.uniqueResult().toString();
    }

    /**
     * Получение полного списка пересдач преподавателя
     * @param idHum
     * @return
     */
    public List<RetakeModel> getRetakesForHum(Long idHum, Integer signFilter) {
        List<RetakeModel> retakes = new ArrayList<>();
        List<CompletionESOModel> esoRetakes = getRetakeESOModel(idHum, signFilter);
        // Обработка базовых моделей, и превращение их в RetakeModel
        for (CompletionESOModel model : esoRetakes) {
            RetakeModel retake = new RetakeModel();
            retake.setDateOfRetake(model.getBegindate());
            retake.setBegindate(model.getBegindate());
            retake.setEnddate(model.getEnddate());
            retake.setSecondDateBegin(model.getSecondDateBegin());
            retake.setSecondDateEnd(model.getSecondDateEnd());
            retake.setFormofcontrol(model.getFormofcontrol());
            retake.setIdReg(model.getIdRegister());
            retake.setRetakeCount(model.getRetakeCount());
            retake.setSignDate(model.getSigndate());
            retake.setCertnumber(model.getCertnumber());
            if (model.getRetakeCount() == 4 || model.getRetakeCount() == -4){
                retake.setFioStudent(getFioStudentForIndRetake(model.getIdSemester(), model.getIdRegister()));
            }
            SemesterModel semester = new SemesterModel();
            semester.setCurSem(model.getCurSem());
            semester.setFormofstudy(model.getFormofstudy());
            semester.setInstitute(model.getInstitute());
            semester.setSeason(model.getSeason());
            semester.setIdInstitute(model.getIdInstitute());
            semester.setDateOfBeginYear(model.getDateofbegin());
            semester.setDateOfEndYear(model.getDateofend());
            semester.setSemesterStr(model.getSemesterStr());
            semester.setIdSemester(model.getIdSemester());

            GroupModel group = new GroupModel();
            group.setIdESOcourse(model.getIdESOcourse());
            group.setIdLGSS(model.getIdLGSS());
            group.setGroupname(model.getGroupname());
            group.setHoursCount(model.getHoursCount());
            group.setCourse(model.getCourse());
            group.setSemesterNumber(model.getSemesterNumber());
            group.setCompletionDate(model.getCompletionDate());

            SubjectModel subject = new SubjectModel();
            if(model.getCp()){
                subject.setFormofcontrol(FormOfControlConst.CP.getValue());
                retake.setFormofcontrol(FormOfControlConst.CP.getValue());
            }
            if(model.getCw()){
                subject.setFormofcontrol(FormOfControlConst.CW.getValue());
                retake.setFormofcontrol(FormOfControlConst.CW.getValue());
            }
            if(model.getPractic()){
                subject.setFormofcontrol(FormOfControlConst.PRACTIC.getValue());
                retake.setFormofcontrol(FormOfControlConst.PRACTIC.getValue());
            }
            if(model.getExam()){
                subject.setFormofcontrol(FormOfControlConst.EXAM.getValue());
                retake.setFormofcontrol(FormOfControlConst.EXAM.getValue());
            }
            if(model.getPass()){
                subject.setFormofcontrol(FormOfControlConst.PASS.getValue());
                retake.setFormofcontrol(FormOfControlConst.PASS.getValue());
            }
            subject.setTypePass(model.getType());
            subject.setSubjectname(model.getSubjectname());
            subject.setSemester(semester);
            group.setSubject(subject);

            retake.setGroup(group);
            retake.setSubject(subject);

            retakes.add(retake);
        }
        return retakes;
    }

    public List<CompletionESOModel> getRetakeESOModel(Long idHum, Integer signFilter) {
        String addition = "";
        if (signFilter == 1) {
            addition = " AND R.signdate is not null and R.certnumber is not null ";
        }
        if (signFilter == 2) {
            addition = " AND (R.signdate is null) ";
        }
        if (signFilter == 3) {
            addition = " and R.certnumber = '  '";
        }
        String query =
                "SELECT\n \tCOALESCE(INST.shorttitle, INST.fulltitle) AS institute," +
                        " SEM.formofstudy, SEM.season, LGS.semesternumber, SEM.id_semester AS idSemester, \n" +
                        "\tCASE WHEN SEM.season = 0 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' осенний'\n" +
                        "\t\tWHEN SEM.season = 1 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' весенний' END AS semesterStr,\n" +
                        "\t(SEM.is_current_sem=1) AS curSem,INST.id_institute AS idInstitute, LGSS.id_link_group_semester_subject AS idLGSS, DG.groupname, DS.subjectname, \n" +
                        "\tSRH.is_exam=1 AS exam, SRH.is_pass=1 AS pass, SRH.is_courseproject=1 AS cp, SRH.is_coursework=1 AS cw, SRH.is_Practic=1 AS practic,\n" +
                        "\tSY.dateofbegin, LGSS.id_esocourse2 AS idESOcourse,LGSS.examdate as examdate, LGSS.passdate as passdate, LGSS.tmpcourseprojectdate as cpdate,\n" +
                        "\tLGSS.tmpcourseworkdate as cwdate, LGSS.practicdate as practicdate, SY.dateofend, S.hourscount AS hoursCount, S.type, LGS.course\n" +
                        "\t,R.id_register as idRegister, R.begindate, R.enddate, R.second_sign_date_begin as secondDateBegin, R.second_sign_date_end as secondDateEnd, " +
                        "R.register_number as registerNumber,SRH.retake_count as retakeCount, R.signdate, R.certnumber\n" +
                        "FROM employee EMP\n" +
                        "\tINNER JOIN link_employee_subject_group LESG USING (id_employee)\n" +
                        "\tINNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester_subject)\n" +
                        "\tINNER JOIN subject S USING (id_subject)\n" +
                        "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                        "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                        "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                        "\tINNER JOIN semester SEM USING (id_semester)\n" +
                        "\tINNER JOIN institute INST ON SEM.id_institute = INST.id_institute\n" +
                        "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                        "\tINNER JOIN sessionrating SR USING (id_subject)\n" +
                        "\tINNER JOIN sessionratinghistory SRH ON SR.id_sessionrating = SRH.id_sessionrating" +
                        "\t\tAND SRH.retake_count IN (2,4,-2,-4)" +
                        "\tINNER JOIN register R ON SRH.id_register = R.id_register\n" +
                        "WHERE EMP.id_humanface = :idHumanface\n" +
                        addition +
                        "GROUP BY institute, SEM.formofstudy, SEM.season, LGS.semesternumber, idSemester, semesterStr,\n" +
                        "\tcurSem, idInstitute, idLGSS, DG.groupname, DS.subjectname,\n" +
                        "\texam, pass, cp, cw, practic, SY.dateofbegin, idESOcourse, SY.dateofend, hoursCount, S.type, LGS.course, R.id_register, SRH.retake_count, R.signdate ORDER BY R.begindate DESC";

        Query q = getSession().createSQLQuery(query)
                .addScalar("institute").addScalar("formofstudy").addScalar("semesterStr")
                .addScalar("season").addScalar("curSem").addScalar("idLGSS", LongType.INSTANCE)
                .addScalar("groupname").addScalar("subjectname")
                .addScalar("exam").addScalar("pass").addScalar("cp").addScalar("cw").addScalar("practic")
                .addScalar("examdate").addScalar("passdate").addScalar("cpdate")
                .addScalar("cwdate").addScalar("practicdate")
                .addScalar("dateofbegin").addScalar("idESOcourse", LongType.INSTANCE)
                .addScalar("dateofend").addScalar("idSemester", LongType.INSTANCE)
                .addScalar("hoursCount").addScalar("semesterNumber").addScalar("type")
                .addScalar("course").addScalar("idInstitute", LongType.INSTANCE)
                .addScalar("idRegister", LongType.INSTANCE)
                .addScalar("begindate", DateType.INSTANCE)
                .addScalar("enddate", DateType.INSTANCE)
                .addScalar("secondDateBegin", DateType.INSTANCE)
                .addScalar("secondDateEnd", DateType.INSTANCE)
                .addScalar("certnumber")
                .addScalar("registerNumber").addScalar("retakeCount", IntegerType.INSTANCE)
                .addScalar("signdate", DateType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(CompletionESOModel.class));

        q.setLong("idHumanface", idHum);
        return (List<CompletionESOModel>) getList(q);
    }

    public List<CommissionModel> getListCommissionByHum (Long idHum) {
        String query = "SELECT DISTINCT ON (R.id_register) COALESCE(INST.shorttitle, INST.fulltitle) AS institute, SEM.formofstudy, SEM.id_semester AS idSem,\n" +
                "\tCASE\n" +
                "\t\tWHEN SEM.season = 0 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' осенний'\n" +
                "\t\tWHEN SEM.season = 1 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' весенний'\n" +
                "\tEND AS semesterStr, (SEM.is_current_sem=1) AS curSem, SY.dateofbegin AS dateOfBeginSY, SEM.season, \n" +
                "\tDS.subjectname AS subjectName, RC.comission_date AS dateOfCommission, RC.id_register_comission AS idRC,\n" +
                "\tR.id_register AS idReg, R.signatorytutor,  R.certnumber, R.register_number AS regNumber,\n" +
                "\tCAST(LGS.course AS TEXT), SR.type, \n" +
                "\tCASE\n" +
                "\t\tWHEN SRH.is_exam = 1 THEN 1 \n" +
                "\t\tWHEN SRH.is_pass = 1 THEN 2\n" +
                "\t\tWHEN SRH.is_courseproject = 1 THEN 3\n" +
                "\t\tWHEN SRH.is_coursework = 1 THEN 4\n" +
                "\t\tWHEN SRH.is_practic = 1 THEN 5\n" +
                "\tELSE 0 END AS formOfControl, LGS.semesternumber,   (SELECT to_date(to_char(RC.comission_date, 'yyyy-MM-dd'), 'yyyy-MM-dd')) as comissionDate,\n" +
                "\tsubstring(cast (RC.comission_date as text), 12, 5) as timeCom, \n" +
                "\trc.classroom as classroom,\n" +
                "\ts.hourscount as hourCountDouble,\n" +
                "\tsrh.check_commission AS checkStatus,\n" +
                "\tR.signdate AS signDate,\n" +
                "\t(SELECT cast(array_agg(ingroup.groupname) as text) FROM (SELECT indg.groupname FROM register inreg \n" +
                "\t INNER JOIN sessionratinghistory insrh ON insrh.id_register = inreg.id_register \n" +
                "\t INNER JOIN sessionrating insr ON insr.id_sessionrating = insrh.id_sessionrating\n" +
                "\t INNER JOIN student_semester_status insss ON insss.id_student_semester_status = insr.id_student_semester_status\n" +
                "\t INNER JOIN link_group_semester inlgs ON inlgs.id_link_group_semester = insss.id_link_group_semester\n" +
                "\t INNER JOIN dic_group indg ON indg.id_dic_group = inlgs.id_dic_group\n" +
                "\t WHERE inreg.id_register = R.id_register GROUP BY indg.groupname) AS ingroup) AS groups\n" +
                "FROM\n" +
                "\tled_comission LD\n" +
                "\tINNER JOIN link_employee_department LED USING (id_link_employee_department)\n" +
                "\tINNER JOIN employee EMP USING (id_employee)\n" +
                "\tINNER JOIN register_comission RC USING (id_register_comission)\n" +
                "\tINNER JOIN subject S USING (id_subject)\n" +
                "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "\tINNER JOIN register R USING (id_register)\n" +
                "\tINNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status )\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "\tINNER JOIN institute INST USING (id_institute)\n" +
                "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                "WHERE EMP.id_humanface = \n" + idHum +
                "\t order by R.id_register desc, RC.comission_date ";
        Query q = getSession().createSQLQuery(query)
                .addScalar("institute").addScalar("formofstudy").addScalar("idSem", LongType.INSTANCE)
                .addScalar("semesterStr").addScalar("curSem", BooleanType.INSTANCE).addScalar("dateOfBeginSY")
                .addScalar("season").addScalar("subjectName").addScalar("dateOfCommission")
                .addScalar("idRC", LongType.INSTANCE).addScalar("idReg", LongType.INSTANCE).addScalar("signatorytutor")
                .addScalar("certnumber").addScalar("regNumber")
                .addScalar("course", IntegerType.INSTANCE).addScalar("type")
                .addScalar("formOfControl").addScalar("semesternumber").addScalar("comissionDate").addScalar("timeCom")
                .addScalar("classroom")
                .addScalar("hourCountDouble", DoubleType.INSTANCE)
                .addScalar("checkStatus",BooleanType.INSTANCE)
                .addScalar("signDate", DateType.INSTANCE)
                .addScalar("groups")
                .setResultTransformer(Transformers.aliasToBean(CommissionModel.class));
        return (List<CommissionModel>) q.list();
    }
}
