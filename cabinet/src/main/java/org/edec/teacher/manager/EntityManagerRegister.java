package org.edec.teacher.manager;

import org.edec.dao.DAO;
import org.edec.teacher.model.register.DateModel;
import org.edec.teacher.model.register.RatingModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RegisterType;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;

import java.util.Date;
import java.util.List;

/**
 * Created by antonskripacev on 24.02.17.
 */
public class EntityManagerRegister extends DAO {
    public List<Long> getListRegisterIdsBySubject(Long idLGSS, FormOfControlConst foc, RegisterType registerType) {
        String query = "select \n" + "\treg.id_register \n" + "from \n" + "\tregister reg\n" +
                "\tinner join sessionratinghistory srh using(id_register)\n" +
                "\tinner join sessionrating sr using(id_sessionrating)\n" +
                "\tinner join student_semester_status sss using(id_student_semester_status)\n" +
                "\tinner join link_group_semester lgs using(id_link_group_semester)\n" +
                "\tinner join dic_group dg using(id_dic_group)\n" +
                "\tinner join link_group_semester_subject lgss ON lgs.id_link_group_semester = lgss.id_link_group_semester and sr.id_subject = lgss.id_subject\n" +
                "where \n" + "\tid_link_group_semester_subject = :idLGSS\n" +
                "\tand" + getPartFocCondition(foc) + "\n" +
                "\tand" + getPartTypeRegisterCondition(registerType) + "\n" +
                "group by id_register";

        return getSession().createSQLQuery(query).setParameter("idLGSS", idLGSS).list();
    }

    public List<RatingModel> getListRatingsByIdRegister(Long idRegister) {
        String query = "SELECT " +
                "    LGS.id_semester AS idSemester, lgss.dateBegin as beginDateMainRegister, lgss.dateEnd as endDateMainRegister, " +
                "    SR.id_sessionrating AS idSessionRating, " +
                "    concat(HF.family,(' '||HF.name),(' '||HF.patronymic)) AS studentFIO, " +
                "    DG.groupname AS groupName, " +
                "    SSS.is_deducted AS deductedStatus, " +
                "    SC.recordbook AS recordbookNumber, " +
                "    SSS.is_academicleave AS academicLeaveStatus," +
                "    reg.certnumber AS certNumber," +
                "    sbj.hourscount AS hoursCount," +
                "    reg.otherdbid AS idRegisterMine," +
                "    reg.is_canceled AS isCanceled," +
                "    reg.signatorytutor AS signatoryTutor," +
                "    reg.synchstatus AS synchStatus," +
                "    reg.thumbprint AS thumbPrint," +
                "    DSBJ.subjectname AS subjectName," +
                "    case " +
                "            when SRH.is_exam = 1 then sr.examrating\n" +
                "            when SRH.is_pass = 1 then sr.passrating\n" +
                "            when SRH.is_coursework = 1 then sr.courseworkrating\n" +
                "            when SRH.is_courseproject = 1 then sr.courseprojectrating\n" +
                "            when SRH.is_practic = 1 then sr.practicrating\n" +
                "    end as currentRating, " +
                "    case " +
                "            when SRH.is_exam = 1 then " + FormOfControlConst.EXAM.getValue() + "\n" +
                "            when SRH.is_pass = 1 then " + FormOfControlConst.PASS.getValue() + "\n" +
                "            when SRH.is_coursework = 1 then " + FormOfControlConst.CW.getValue() + "\n" +
                "            when SRH.is_courseproject = 1 then " + FormOfControlConst.CP.getValue() + "\n" +
                "            when SRH.is_practic = 1 then " + FormOfControlConst.PRACTIC.getValue() + "\n" +
                "    end as foc, " +
                "    SR.status, " +
                "    SR.type, " +
                "    SR.is_notactual as notActual, " +
                "    REG.begindate AS statusBeginDate," +
                "    REG.enddate AS statusFinishDate," +
                "    REG.second_sign_date_begin AS secondSignBeginDate," +
                "    REG.second_sign_date_end AS secondSignEndDate, " +
                "    case " +
                "            when SRH.is_exam = 1 then LGSS.examdate\n" +
                "            when SRH.is_pass = 1 then LGSS.passdate\n" +
                "            when SRH.is_coursework = 1 then LGSS.tmpcourseworkdate\n" +
                "            when SRH.is_courseproject = 1 then LGSS.tmpcourseprojectdate\n" +
                "            when SRH.is_practic = 1 then LGSS.practicdate\n" +
                "    end as completionDate, " +
                "    SR.esocourseprojecttheme AS courseProjectTheme,"
                + "    SR.esocourseworktheme AS courseWorkTheme, "
                + "    LGS.course AS course,"
                + "    DG.id_dic_group AS idDicGroup,"
                + "    SC.id_current_dic_group AS idCurrentDicGroup,"
                + "    SRH.newrating AS newRating,"
                + "    SRH.retake_count AS retakeCount,"
                + "    REG.id_register AS idRegister,"
                + "    REG.register_url AS registerUrl,"
                + "    REG.register_number AS registerNumber,"
                + "    SRH.changedatetime AS changeDateTime,"
                + "    SRH.id_sessionratinghistory AS idSessionRatingHistory,"
                + "    SRH.signDate AS signDate,"
                + "    SSS.id_student_semester_status as idStudentSemesterStatus,"
                + "    SRH.is_signed_backdate as signedBackDate"
                + " FROM "
                + "    sessionratinghistory SRH "
                + "    INNER JOIN sessionrating sr ON srh.id_sessionrating = sr.id_sessionrating"
                + "    INNER JOIN subject SBJ ON SR.id_subject = SBJ.id_subject "
                + "    INNER JOIN dic_subject DSBJ ON DSBJ.id_dic_subject = SBJ.id_dic_subject "
                + "    INNER JOIN student_semester_status SSS ON SSS.id_student_semester_status = SR.id_student_semester_status "
                + "    INNER JOIN link_group_semester_subject LGSS ON LGSS.id_link_group_semester = SSS.id_link_group_semester AND LGSS.id_subject = SR.id_subject "
                + "    INNER JOIN studentcard SC ON SC.id_studentcard = SSS.id_studentcard "
                + "    INNER JOIN humanface HF ON HF.id_humanface = SC.id_humanface "
                + "    INNER JOIN link_group_semester LGS ON LGS.id_link_group_semester = SSS.id_link_group_semester "
                + "    INNER JOIN dic_group DG ON DG.id_dic_group = LGS.id_dic_group "
                + "    INNER JOIN register reg using(id_register) "
                + "WHERE "
                + " reg.id_register = :idRegister"
                + " ORDER BY HF.family , SC.id_studentcard, SSS.id_student_semester_status, SRH.id_sessionratinghistory";

        Query q = getSession()
                .createSQLQuery(query)
                .addScalar("idSemester", LongType.INSTANCE)
                .addScalar("idSessionRating", LongType.INSTANCE)
                .addScalar("studentFIO")
                .addScalar("groupName")
                .addScalar("deductedStatus", BooleanType.INSTANCE)
                .addScalar("recordbookNumber")
                .addScalar("academicLeaveStatus", BooleanType.INSTANCE)
                .addScalar("currentRating")
                .addScalar("status")
                .addScalar("type")
                .addScalar("endDateMainRegister").addScalar("beginDateMainRegister")
                .addScalar("notActual", BooleanType.INSTANCE)
                .addScalar("statusBeginDate")
                .addScalar("statusFinishDate")
                .addScalar("secondSignBeginDate")
                .addScalar("secondSignEndDate")
                .addScalar("completionDate")
                .addScalar("courseProjectTheme")
                .addScalar("courseWorkTheme")
                .addScalar("course")
                .addScalar("idDicGroup", LongType.INSTANCE)
                .addScalar("idCurrentDicGroup", LongType.INSTANCE)
                .addScalar("newRating")
                .addScalar("retakeCount")
                .addScalar("idRegister", LongType.INSTANCE)
                .addScalar("registerUrl")
                .addScalar("registerNumber")
                .addScalar("changeDateTime")
                .addScalar("idSessionRatingHistory", LongType.INSTANCE)
                .addScalar("signDate")
                .addScalar("idStudentSemesterStatus", LongType.INSTANCE)
                .addScalar("signedBackDate", BooleanType.INSTANCE)
                .addScalar("certNumber")
                .addScalar("hoursCount")
                .addScalar("idRegisterMine", LongType.INSTANCE)
                .addScalar("isCanceled", BooleanType.INSTANCE)
                .addScalar("signatoryTutor").addScalar("foc")
                .addScalar("subjectName")
                .addScalar("synchStatus", IntegerType.INSTANCE)
                .addScalar("thumbPrint")
                .setParameter("idRegister", idRegister)
                .setResultTransformer(Transformers.aliasToBean(RatingModel.class));

        return (List<RatingModel>) getList(q);
    }


    public List<RatingModel> getListRatingsBySubjectAndType(Long idLGSS, FormOfControlConst foc, RegisterType type) {
        String query = "SELECT " +
                "    LGS.id_semester AS idSemester, lgss.dateBegin as beginDateMainRegister, lgss.dateEnd as endDateMainRegister," +
                "    SR.id_sessionrating AS idSessionRating, " +
                "    concat(HF.family,(' '||HF.name),(' '||HF.patronymic)) AS studentFIO, " +
                "    DG.groupname AS groupName, " +
                "    SSS.is_deducted AS deductedStatus, " +
                "    SC.recordbook AS recordbookNumber, " +
                "    SSS.is_academicleave AS academicLeaveStatus," +
                "    reg.certnumber AS certNumber," +
                "    sbj.hourscount AS hoursCount," +
                "    reg.otherdbid AS idRegisterMine," +
                "    reg.is_canceled AS isCanceled," +
                "    reg.signatorytutor AS signatoryTutor," +
                "    reg.synchstatus AS synchStatus," +
                "    reg.thumbprint AS thumbPrint," +
                "    DSBJ.subjectname AS subjectName," +
                getPartCurrentRating(foc) + " as currentRating, " +
                foc.getValue().toString() + " as foc, " +
                "    SR.status, " +
                "    SR.type, " +
                "    SR.is_notactual as notActual, " +
                "    REG.begindate AS statusBeginDate," +
                "    REG.enddate AS statusFinishDate," +
                getPartCompletionDate(foc) + " as completionDate, " +
                "    SR.esocourseprojecttheme AS courseProjectTheme,"
                + "    SR.esocourseworktheme AS courseWorkTheme, "
                + "    LGS.course AS course,"
                + "    DG.id_dic_group AS idDicGroup,"
                + "    SC.id_current_dic_group AS idCurrentDicGroup,"
                + "    SRH.newrating AS newRating,"
                + "    SRH.retake_count AS retakeCount,"
                + "    REG.id_register AS idRegister,"
                + "    REG.register_url AS registerUrl,"
                + "    REG.register_number AS registerNumber,"
                + "    SRH.changedatetime AS changeDateTime,"
                + "    SRH.id_sessionratinghistory AS idSessionRatingHistory,"
                + "    SRH.signDate AS signDate,"
                + "    SSS.id_student_semester_status as idStudentSemesterStatus,"
                + "    SRH.is_signed_backdate as signedBackDate, "
                // Поля из электронного журнала и электронных курсов
                + "    SR.visitcount, "
                + "    SR.skipcount, "
                + "    SR.esogradecurrent, "
                + "    SR.esogrademax, "
                + "    CASE "
                + "        WHEN SEM.season = 0 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' осенний'"
                + "        WHEN SEM.season = 1 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' весенний'"
                + "    END AS semesterStr"
                + " FROM "
                + "    sessionrating SR "
                + "    INNER JOIN subject SBJ ON SR.id_subject = SBJ.id_subject "
                + "    INNER JOIN dic_subject DSBJ ON DSBJ.id_dic_subject = SBJ.id_dic_subject "
                + "    INNER JOIN student_semester_status SSS ON SSS.id_student_semester_status = SR.id_student_semester_status "
                + "    INNER JOIN link_group_semester_subject LGSS ON LGSS.id_link_group_semester = SSS.id_link_group_semester AND LGSS.id_subject = SR.id_subject "
                + "    INNER JOIN studentcard SC ON SC.id_studentcard = SSS.id_studentcard "
                + "    INNER JOIN humanface HF ON HF.id_humanface = SC.id_humanface "
                + "    INNER JOIN link_group_semester LGS ON LGS.id_link_group_semester = SSS.id_link_group_semester "
                + "    INNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester "
                + "    INNER JOIN schoolyear SY ON SEM.id_schoolyear = SY.id_schoolyear "
                + "    INNER JOIN dic_group DG ON DG.id_dic_group = LGS.id_dic_group "
                + "    LEFT JOIN sessionratinghistory srh ON srh.id_sessionrating = sr.id_sessionrating and " + getPartFocCondition(foc)
                + "    LEFT JOIN register reg using(id_register) "
                + "WHERE "
                + " lgss.id_link_group_semester_subject = :idLgss\n"
                + " and " + (type == RegisterType.MAIN
                ? "SC.is_debtor = false AND (" + getPartTypeRegisterCondition(type) + " or SRH.retake_count is null )"
                : getPartTypeRegisterCondition(type))
                + " ORDER BY HF.family, SC.id_studentcard, SSS.id_student_semester_status, SRH.id_sessionratinghistory";

        Query q = getSession()
                .createSQLQuery(query)
                .addScalar("idSemester", LongType.INSTANCE).addScalar("idSessionRating", LongType.INSTANCE)
                .addScalar("studentFIO").addScalar("groupName").addScalar("deductedStatus", BooleanType.INSTANCE)
                .addScalar("recordbookNumber").addScalar("academicLeaveStatus", BooleanType.INSTANCE)
                .addScalar("currentRating").addScalar("status").addScalar("type").addScalar("notActual", BooleanType.INSTANCE)
                .addScalar("statusBeginDate").addScalar("statusFinishDate").addScalar("completionDate").addScalar("courseProjectTheme")
                .addScalar("endDateMainRegister", DateType.INSTANCE).addScalar("beginDateMainRegister", DateType.INSTANCE)
                .addScalar("courseWorkTheme").addScalar("course").addScalar("idDicGroup", LongType.INSTANCE)
                .addScalar("idCurrentDicGroup", LongType.INSTANCE).addScalar("newRating").addScalar("retakeCount")
                .addScalar("idRegister", LongType.INSTANCE).addScalar("registerUrl").addScalar("registerNumber")
                .addScalar("changeDateTime").addScalar("idSessionRatingHistory", LongType.INSTANCE).addScalar("signDate")
                .addScalar("idStudentSemesterStatus", LongType.INSTANCE).addScalar("signedBackDate", BooleanType.INSTANCE)
                .addScalar("certNumber").addScalar("hoursCount").addScalar("idRegisterMine", LongType.INSTANCE)
                .addScalar("isCanceled", BooleanType.INSTANCE).addScalar("signatoryTutor").addScalar("foc")
                .addScalar("subjectName").addScalar("synchStatus", IntegerType.INSTANCE).addScalar("thumbPrint")
                .addScalar("visitcount", IntegerType.INSTANCE).addScalar("skipcount", IntegerType.INSTANCE)
                .addScalar("esogradecurrent", LongType.INSTANCE).addScalar("esogrademax", LongType.INSTANCE)
                .addScalar("semesterStr")
                .setParameter("idLgss", idLGSS)
                .setResultTransformer(Transformers.aliasToBean(RatingModel.class));

        return (List<RatingModel>) getList(q);
    }

    private String getPartCurrentRating(FormOfControlConst foc) {
        switch (foc) {
            case EXAM:
                return " sr.examrating";
            case PASS:
                return " sr.passrating";
            case CP:
                return " sr.courseprojectrating";
            case CW:
                return " sr.courseworkrating";
            case PRACTIC:
                return " sr.practicrating";
        }

        return null;
    }

    private String getPartCompletionDate(FormOfControlConst foc) {
        switch (foc) {
            case EXAM:
                return " LGSS.examdate ";
            case PASS:
                return " LGSS.passdate ";
            case CP:
                return " LGSS.tmpcourseprojectdate ";
            case CW:
                return " LGSS.tmpcourseworkdate ";
            case PRACTIC:
                return " LGSS.practicdate ";
        }

        return null;
    }

    private String getPartFocCondition(FormOfControlConst foc) {
        switch (foc) {
            case EXAM:
                return " srh.is_exam = 1 ";
            case PASS:
                return " srh.is_pass = 1 ";
            case CP:
                return " srh.is_courseproject = 1 ";
            case CW:
                return " srh.is_coursework = 1 ";
            case PRACTIC:
                return " srh.is_practic = 1 ";
        }

        return null;
    }

    private String getPartTypeRegisterCondition(RegisterType type) {
        switch (type) {
            case MAIN:
                return " srh.retake_count in (-1,1) ";
            case MAIN_RETAKE:
                return " srh.retake_count in (-2,2) ";
            case INDIVIDUAL_RETAKE:
                return " srh.retake_count in (-4,4) ";
        }

        return null;
    }

    public void updateSignDate(Long idRegister) {
        String query = "update register set signdate = now() where id_register = " + idRegister;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public void updateCPTheme(String theme, long idSessionRating) {
        String query = "update sessionrating set esocourseprojecttheme = '" + theme + "' where id_sessionrating = " + idSessionRating;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public void updateCWTheme(String theme, long idSessionRating) {
        String query = "update sessionrating set esocourseworktheme = '" + theme + "' where id_sessionrating = " + idSessionRating;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public void updateSRHWithDateAndRating(long idSessionRatingHistory, int rating) {
        String query = "update sessionratinghistory set changedatetime = now(), newRating = " + rating + " where id_sessionratinghistory = " + idSessionRatingHistory;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean checkSRHSign(long idSessionRatingHistory) {
        boolean signed = false;
        String query = "SELECT signdate FROM sessionratinghistory WHERE id_sessionratinghistory = :srh";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("srh", idSessionRatingHistory);
        Date signdate = (Date) q.uniqueResult();
        if (signdate != null) {
            signed = true;
        }
        return signed;
    }

    public boolean checkExistSRH(long idSessionRating, int retakeCount) {
        String query = "SELECT id_sessionratinghistory FROM sessionratinghistory WHERE id_sessionrating = :sr AND (retake_count = :rc OR retake_count*-1 = :rc)";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("sr", idSessionRating);
        q.setParameter("rc", retakeCount);
        Long id_sessionratinghistory = (Long) q.uniqueResult();
        return id_sessionratinghistory == null ? false : true;
    }

    public Long createSRH(boolean exam, boolean pass, boolean cp, boolean cw, boolean practic, int type, String status, int newRating, long idSessionRating, long idSystemUser, int retakeCount) {
        String query = "insert into sessionratinghistory (is_exam, is_pass, is_courseproject, is_coursework, is_practic, " +
                " type, status, newrating, changedatetime, id_sessionrating, id_systemuser, retake_count) values (" +
                (exam ? 1 : 0) + "," +
                (pass ? 1 : 0) + "," +
                (cp ? 1 : 0) + "," +
                (cw ? 1 : 0) + "," +
                (practic ? 1 : 0) + "," +
                type + "," +
                "'" + status + "'," +
                newRating + ","
                + "now()," +
                idSessionRating + "," +
                idSystemUser + "," +
                retakeCount +
                ") RETURNING id_sessionratinghistory";
        List<Long> list = (List<Long>) getList(
                getSession().createSQLQuery(query)
                        .addScalar("id_sessionratinghistory", LongType.INSTANCE));
        return list.size() == 0 ? null : list.get(0);
    }

    public void updateSRH(String status, long idSRH) {
        String query = "update sessionratinghistory set status = '" + status + "' where id_sessionratinghistory = " + idSRH;
        getSession().createSQLQuery(query).executeUpdate();
    }

    public boolean setRegisterNumber(Long idRegister, String tutor, String listSRH, Long idSemester, String suffix) {
        return callFunction("select register_create_or_update(" +
                idRegister + ", " +
                "'" + tutor + "', " +
                "'" + listSRH + "', " +
                idSemester + ", " +
                "'" + suffix + "'" +
                ")");
    }

    public DateModel getDatePassweekAndSession(Long idLGSS) {
        String query = "SELECT  LGS.dateofbeginsession as dateOfBeginSession,  LGS.dateofendsession as dateOfEndSession, \n" +
                "                LGS.dateofbeginpassweek as dateOfBeginPassweek, LGS.dateofendpassweek as dateOfEndPassweek\n" +
                "                \n" +
                "FROM link_group_semester LGS\n" +
                "join  link_group_semester_subject LGSS using (id_link_group_semester)\n" +
                "\n" +
                "WHERE LGSS.id_link_group_semester_subject = " + idLGSS;

        Query q = getSession().createSQLQuery(query)
                .addScalar("dateOfBeginSession")
                .addScalar("dateOfEndSession")
                .addScalar("dateOfBeginPassweek")
                .addScalar("dateOfEndPassweek")
                .setResultTransformer(Transformers.aliasToBean(DateModel.class));

        List<DateModel> list = (List<DateModel>) getList(q);
        return list.size() == 0 ? null : list.get(0);

    }

    public boolean isHasSign(Long idHumanface) {
        String query = "select hasSign\n" +
                "from employee \n" +
                "where id_humanface = " + idHumanface +
                " limit 1";
        Query q = getSession().createSQLQuery(query);
        return (boolean) q.uniqueResult();
    }
}