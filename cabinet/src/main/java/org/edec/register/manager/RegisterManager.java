package org.edec.register.manager;

import org.edec.dao.DAO;
import org.edec.register.model.RegisterModel;
import org.edec.register.model.RetakeModel;
import org.edec.register.model.StudentModel;
import org.edec.register.model.dao.RegisterModelEso;
import org.edec.register.model.dao.RegisterReportModelEso;
import org.edec.register.model.dao.RegisterWithSignDateModelESO;
import org.edec.register.model.dao.RetakeModelEso;
import org.edec.register.service.RegisterService;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RegisterConst;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


/**
 * Created by antonskripacev on 10.06.17.
 */
public class RegisterManager extends DAO {
    public List<RegisterModelEso> getListRetakes (Long idInstitute, int formOfStudy, Long idSemester, String groupname, boolean[] checkBoxes) {
        // На случай если не требуется показывать отчисленных
        /*
        String deductedFilter = "\tAND SSS.is_deducted = 0 AND SSS.is_academicleave = 0\n";

        if (checkBoxes[RegisterService.DEDUCTED]) {
            deductedFilter = "";
        }
        */
        String deductedFilter = "";

        String query = "SELECT\n" +
                       "\tDS.subjectname as subjectName,  lgss.examdate as examDate, lgss.passdate as passDate, \n" +
                "lgss.tmpcourseworkdate as cwDate, lgss.tmpcourseprojectdate as cpDate,\n" +
                "lgss.practicdate as practicDate, lgss.id_link_group_semester_subject as idLgss, \n" +
                       "\tR.id_register as idRegister, lgss.dateBegin as beginDateMainRegister, lgss.dateEnd as endDateMainRegister,\n" +
                       "\tR.otherdbid as idRegisterMine,\n" +
                       "\tSEM.id_institute as idInstitute,\n"+
                       "\tR.synchstatus as synchStatus,\n" +
                       "\tSRH.retake_count as retakeCount,\n" +
                       "\tR.register_number as registerNumber,\n" +
                       "\tSR.id_subject as idSubject,\n" +
                       "\tSR.type as type,\n" +
                       "\tCUR.qualification as qualification,\n" +
                       "\tSRH.id_sessionratinghistory as idSRH,\n" +
                       "\tSRH.changedatetime as changeDateTime,\n" +
                       "\tSSS.id_student_semester_status as idSSS,\n" +
                       "\tCASE\n" +
                       "\t\tWHEN SRH.is_exam = 1 THEN 1\n" +
                       "\t\tWHEN SRH.is_pass = 1 THEN 2\n" +
                       "\t\tWHEN SRH.is_courseproject = 1 THEN 3\n" +
                       "\t\tWHEN SRH.is_coursework = 1 THEN 4\n" +
                       "\t\tWHEN SRH.is_practic = 1 THEN 5\n" +
                       "\tEND AS foc, DG.groupname as groupName, \n" +
                       "\tHF.family as family, Hf.name as name, Hf.patronymic as patronymic, \n" +
                       "\tHF2.family as familyTeacher, Hf2.name as nameTeacher, Hf2.patronymic as patronymicTeacher, \n" +
                       "\tSRH.newrating AS rating,\n" +
                       "\tCASE\n" +
                       "\t\tWHEN SEM.season = 0 THEN TO_CHAR(SY.dateofbegin, 'yyyy')||'-'||TO_CHAR(SY.dateofend, 'yyyy')||' осень'\n" +
                       "\tELSE TO_CHAR(SY.dateofbegin, 'yyyy')||'-'||TO_CHAR(SY.dateofend, 'yyyy')||' весна' END AS semester, \n" +
                       "\tSEM.id_semester AS idSemester,\n" +
                       "\tRC.dateofendcomission as dateOfEndComission, \n" +
                       "\tR.signdate as signDate, \n"+
                       "\tR.begindate as dateOfBegin, \n" +
                       "\tR.second_sign_date_begin as dateOfSecondSignBegin, \n" +
                       "\tR.second_sign_date_end as dateOfSecondSignEnd, \n" +
                       "\tR.enddate as dateOfEnd, \n" +
                       "\tR.register_url as registerUrl, R.certnumber as certNumber,\n" +
                       "\tDG.dateofend as dateOfGroupFinish,\n" +
                       "\tSEM.formofstudy as fos\n" +
                       "FROM\n" +
                       "\tlink_group_semester LGS\n" +
                       "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                       "\tINNER JOIN curriculum CUR USING (id_curriculum)\n" +
                       "\tINNER JOIN semester SEM ON SEM.id_semester = LGS.id_semester\n" +
                       "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                       "\tINNER JOIN humanface HF ON SC.id_humanface = HF.id_humanface\n" +
                       "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" +
                       "\tLEFT JOIN sessionratinghistory SRH USING (id_sessionrating)\n" +
                       "\tLEFT JOIN register R USING (id_register)\n" +
                       "\tINNER JOIN subject S USING (id_subject)\n" +
                       "\tLEFT JOIN register_comission RC ON R.id_register = RC.id_register\n" +
                       "\tINNER JOIN link_group_semester_subject LGSS ON LGSS.id_subject = S.id_subject\n" +
                       "\tINNER JOIN link_employee_subject_group LESG ON LESG.id_link_group_semester_subject = LGSS.id_link_group_semester_subject\n" +
                       "\tLEFT JOIN employee EMP USING(id_employee)\n" +
                       "\tLEFT JOIN humanface HF2 ON EMP.id_humanface = HF2.id_humanface\n" +
                       "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                       "WHERE\n" +
                       "\tSRH.retake_count IN (-1, -2,-3,-4,1,2,3,4) \n" +
                       (idSemester != null ? "AND LGS.id_semester = " + idSemester + "\n" : "") +
                       " AND SEM.id_institute = " + idInstitute + "\n" +
                       "\tAND SEM.formofstudy " + (formOfStudy == 3 ? " in (1,2)" : " = " + formOfStudy) + " \n" +
                       "\tAND DG.groupname ilike '%" + groupname + "%'\n" +
                        deductedFilter +
                       "\tORDER BY\n" +
                       "\tsemester desc, groupname, subjectname, foc, srh.retake_count, idRegister, family asc, name asc, patronymic asc";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("subjectName")
                              .addScalar("idRegister", LongType.INSTANCE)
                              .addScalar("idRegisterMine", LongType.INSTANCE)
                              .addScalar("idInstitute", LongType.INSTANCE)
                              .addScalar("retakeCount")
                              .addScalar("synchStatus", IntegerType.INSTANCE)
                              .addScalar("registerNumber")
                              .addScalar("idSubject", LongType.INSTANCE)
                              .addScalar("changeDateTime")
                              .addScalar("idSSS", LongType.INSTANCE)
                              .addScalar("idLgss", LongType.INSTANCE)
                              .addScalar("foc")
                              .addScalar("groupName")
                              .addScalar("family")
                              .addScalar("name")
                              .addScalar("patronymic")
                              .addScalar("rating")
                              .addScalar("certNumber")
                              .addScalar("semester")
                              .addScalar("idSemester", LongType.INSTANCE)
                              .addScalar("dateOfEnd")
                              .addScalar("dateOfBegin")
                              .addScalar("registerUrl")
                              .addScalar("idSRH", LongType.INSTANCE)
                              .addScalar("type")
                              .addScalar("qualification")
                              .addScalar("familyTeacher")
                              .addScalar("nameTeacher")
                              .addScalar("patronymicTeacher")
                              .addScalar("dateOfEndComission")
                              .addScalar("signDate")
                              .addScalar("dateOfSecondSignBegin")
                              .addScalar("dateOfSecondSignEnd")
                              .addScalar("examDate")
                              .addScalar("passDate")
                              .addScalar("cwDate")
                              .addScalar("cpDate")
                              .addScalar("practicDate")
                              .addScalar("endDateMainRegister")
                              .addScalar("beginDateMainRegister")
                              .addScalar("dateOfGroupFinish")
                              .addScalar("fos")
                .setResultTransformer(Transformers.aliasToBean(RegisterModelEso.class));
        return (List<RegisterModelEso>) getList(q);
    }

    public void updateSrDates (Date dateBegin, Date dateEnd, Long idSRH) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        getList(getSession().createSQLQuery("select create_register_for_srh(" + idSRH + ")"));

        String query = "with registerQuery as (\n" +
                " select * from register\n" +
                " join sessionratinghistory using(id_register)\n" +
                " where id_sessionratinghistory = "+ idSRH +")\n" +
                " update register set begindate = '"+ format.format(dateBegin) +"', enddate = '"+ format.format(dateEnd) + "'" +
                "from registerQuery where registerQuery.id_register = register.id_register";
            executeUpdate(getSession().createSQLQuery(query));

    }

    public void updateDateMainRegister (Date dateBegin, Date dateEnd, Long idLgss) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String query = "update link_group_semester_subject set dateBegin = '"+format.format(dateBegin)+"' where id_link_group_semester_subject = "+idLgss+";\n" +
                "update link_group_semester_subject set dateEnd = '"+format.format(dateEnd)+"' where id_link_group_semester_subject = "+idLgss+";";
        executeUpdate(getSession().createSQLQuery(query));
    }

    public void updateCancelSignInTransaction (RegisterModel register) {
        try {
            begin();
            updateSrCancelSignInTransaction(register);
            updateSrhCancelSignInTransaction(register);
            updateRegisterCancelSignInTransaction(register);
            commit();
        } catch (Exception e) {
            rollback();
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void updateSrCancelSignInTransaction (RegisterModel register) {
        String subQuery = "";

        switch (FormOfControlConst.getName(register.getFoc())) {
            case EXAM:
                subQuery = " examrating = 0, esoexamrating = 0, ";
                break;
            case PASS:
                subQuery = " passrating = 0, esopassrating = 0, ";
                break;
            case CP:
                subQuery = " courseprojectrating = 0, esocourseprojectrating = 0, ";
                break;
            case CW:
                subQuery = " courseworkrating = 0, esocourseworkrating = 0, ";
                break;
            case PRACTIC:
                subQuery = " practicrating = 0, ";
                break;
        }

        String query = "update sessionrating set " + subQuery + " status = '0.0.0' where id_sessionrating in (" +
                       " select id_sessionrating from sessionratinghistory where id_register = " + register.getIdRegister() + ")";

        getSession().createSQLQuery(query).executeUpdate();
    }

    private void updateSrhCancelSignInTransaction (RegisterModel register) throws Exception {
        int retakeCount = 1;

        switch (Math.abs(register.getRetakeCount())) {
            case RegisterConst.TYPE_MAIN:
                retakeCount = -1;
                break;
            case RegisterConst.TYPE_RETAKE_MAIN:
                retakeCount = -2;
                break;
            case RegisterConst.TYPE_COMMISSION:
                retakeCount = -3;
                break;
            case RegisterConst.TYPE_RETAKE_INDIV:
                retakeCount = -4;
                break;
            default:
                throw new Exception();
        }

        String query = "update sessionratinghistory set status = '0.0.0', signdate = null, retake_count = " + retakeCount + " where id_sessionratinghistory in (" +
                       " select id_sessionratinghistory from sessionratinghistory where id_register = " + register.getIdRegister() + ")";

        getSession().createSQLQuery(query).executeUpdate();
    }

    private void updateRegisterCancelSignInTransaction (RegisterModel register) {
        String query = "update register " +
                       "set is_canceled = 1, " +
                       "certnumber = null," +
                       "signdate = null, " +
                       "thumbprint = null, " +
                       "signatorytutor = null, " +
                       "register_url = null," +
                       "synchstatus = null " +
                       "where id_register = " + register.getIdRegister();

        getSession().createSQLQuery(query).executeUpdate();
    }

    public List<RetakeModelEso> getListRatingByListGroupSubjects (String id, String focQuery, String focLeftJoinQuery) {
        String query = "select \n" +
                       "\tlgs.id_semester as idSemester, hf.family || ' ' || hf.name || ' ' || hf.patronymic as fio,\n" +
                       "\tsss.id_student_semester_status as idSSS,\n" +
                       "\tsr.id_sessionrating as idSR,\n" +
                       "\tsrh.id_sessionratinghistory as idSRH,\n" +
                       "\tsss.is_transfer_student as transferedStudent,\n" +
                       "\tsc.id_current_dic_group as idCurDicGroup,\n" +
                       "\tsc.is_debtor as isDebtor,\n" +
                       "\tsr.passrating as passRating,\n" +
                       "\tsr.examrating as examRating,\n" +
                       "\tsr.courseprojectrating as cpRating,\n" +
                       "\tsr.courseworkrating as cwRating,\n" +
                       "\tsr.practicrating as practicRating,\n" +
                       "\tsr.esopassrating as esoPassRating,\n" +
                       "\tsr.esoexamrating as esoExamRating,\n" +
                       "\tsr.esocourseprojectrating as esoCpRating,\n" +
                       "\tsr.esocourseworkrating as esoCwRating,\n" +
                       "\tsr.type as type,\n" +
                       "\treg.begindate as beginDate,\n" +
                       "\treg.enddate as finishDate,\n" +
                       "\tsrh.retake_count as retakeCount,\n" +
                       "\tsrh.newrating as newRating,\n" +
                       "\t(\n" + "\t\tselect is_deducted from student_semester_status sss2\n" +
                       "\t\tinner join link_group_semester lgs2 using(id_link_group_semester)\n" +
                       "\t\tinner join semester sem2 using(id_semester)\n" +
                       "\t\twhere lgs2.id_dic_group = sc.id_current_dic_group " +
                       "and sss2.id_studentcard = sss.id_studentcard and sem2.is_current_sem = 1\n" +
                       "\t) as deductedCurSem,\n" + "\t(\n"
                       + "\t\tselect is_listener from student_semester_status sss2\n" +
                       "\t\tinner join link_group_semester lgs2 using(id_link_group_semester)\n" +
                       "\t\tinner join semester sem2 using(id_semester)\n" +
                       "\t\twhere lgs2.id_dic_group = sc.id_current_dic_group and sss2.id_studentcard = sss.id_studentcard and sem2.is_current_sem = 1\n" +
                       "\t) as listenerCurSem,\n" + "\t(\n" + "\t\tselect is_transfer_student from student_semester_status sss2\n" +
                       "\t\tinner join link_group_semester lgs2 using(id_link_group_semester)\n" +
                       "\t\tinner join semester sem2 using(id_semester)\n" +
                       "\t\twhere lgs2.id_dic_group = sc.id_current_dic_group and sss2.id_studentcard = sss.id_studentcard and sem2.is_current_sem = 1\n" +
                       "\t) as transferedStudentCurSem,\n" + "\t(\n" + "\t\tselect is_academicleave from student_semester_status sss2\n" +
                       "\t\tinner join link_group_semester lgs2 using(id_link_group_semester)\n" +
                       "\t\tinner join semester sem2 using(id_semester)\n" +
                       "\t\twhere lgs2.id_dic_group = sc.id_current_dic_group and sss2.id_studentcard = sss.id_studentcard and sem2.is_current_sem = 1\n" +
                       "\t) as academicLeaveCurSem\n" +
                       "from\n" + "\tlink_group_semester_subject lgss\n" +
                       "\tinner join link_group_semester lgs on lgss.id_link_group_semester = lgs.id_link_group_semester\n" +
                       "\tinner join student_semester_status sss on lgs.id_link_group_semester = sss.id_link_group_semester\n" +
                       "\tinner join studentcard sc using(id_studentcard)\n" +
                       "\tinner join humanface hf using(id_humanface)\n" +
                       "\tinner join sessionrating sr on sr.id_subject = lgss.id_subject and sss.id_student_semester_status = sr.id_student_semester_status\n" +
                       "\tleft join sessionratinghistory srh on sr.id_sessionrating = srh.id_sessionrating and " + focLeftJoinQuery + "\n" +
                       "\tleft join register reg on reg.id_register = srh.id_register \n" +
                       "where " +
                       "lgss.id_link_group_semester_subject = " + id + " " +
                       "and is_deducted = 0 " +
                       "and is_debtor <> true " +
                       "and is_academicleave = 0 " +
                       "and (select s.is_listener\n" +
                       "        from student_semester_status s\n" +
                       "                 join link_group_semester l on s.id_link_group_semester = l.id_link_group_semester\n" +
                       "                 join semester s2 on l.id_semester = s2.id_semester\n" +
                       "        where is_current_sem = 1 and s.id_studentcard = sc.id_studentcard limit 1) = 0\n" +
                       "and is_notactual = 0 " +
                       "order by sr.id_sessionrating";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("idSSS", LongType.INSTANCE)
                              .addScalar("idSemester", LongType.INSTANCE)
                              .addScalar("idSR", LongType.INSTANCE)
                              .addScalar("idSRH", LongType.INSTANCE)
                              .addScalar("transferedStudent", BooleanType.INSTANCE)
                              .addScalar("idCurDicGroup", LongType.INSTANCE)
                              .addScalar("passRating")
                              .addScalar("examRating")
                              .addScalar("cpRating")
                              .addScalar("cwRating")
                              .addScalar("esoPassRating")
                              .addScalar("esoExamRating")
                              .addScalar("esoCpRating")
                              .addScalar("esoCwRating")
                              .addScalar("practicRating")
                              .addScalar("retakeCount")
                              .addScalar("type")
                              .addScalar("beginDate")
                              .addScalar("finishDate")
                              .addScalar("newRating")
                              .addScalar("isDebtor", BooleanType.INSTANCE)
                              .addScalar("deductedCurSem", BooleanType.INSTANCE)
                              .addScalar("listenerCurSem", BooleanType.INSTANCE)
                              .addScalar("transferedStudentCurSem", BooleanType.INSTANCE)
                              .addScalar("academicLeaveCurSem", BooleanType.INSTANCE)

                              .setResultTransformer(Transformers.aliasToBean(RetakeModelEso.class));

        return (List<RetakeModelEso>) getList(q);
    }

    public boolean createRetakeForModel (FormOfControlConst foc, List<RetakeModel> retakeModels, Date dateOfBegin, Date dateOfEnd) {
        try {
            begin();

            String strDateOfBegin = new SimpleDateFormat("yyyy-MM-dd").format(dateOfBegin);
            String strDateOfEnd = new SimpleDateFormat("yyyy-MM-dd").format(dateOfEnd);

            for (RetakeModel retake : retakeModels) {
                String insertRegisterQuery = "insert into register (id_semester, begindate, enddate) values ('" + retake.getIdSemester() +
                        "','" + strDateOfBegin + "','" + strDateOfEnd + "') returning id_register";
                Long idRegister = (Long) getSession().createSQLQuery(insertRegisterQuery).uniqueResult();

                if (idRegister == null) {
                    throw new Exception();
                } else {
                    for (StudentModel student : retake.getStudents()) {
                        String query = "insert into sessionratinghistory (id_register, retake_count, changedatetime, is_courseproject, " +
                                "is_coursework, is_exam, is_pass, is_practic, newrating, oldrating, status, " +
                                "type, id_sessionrating,  id_systemuser) " +
                                "values (" + idRegister + ", " + retake.getTypeRetake() + ", now(), " +
                                (foc == FormOfControlConst.CP ? "1, " : "0, ") +
                                (foc == FormOfControlConst.CW ? "1, " : "0, ") +
                                (foc == FormOfControlConst.EXAM ? "1, " : "0, ") +
                                (foc == FormOfControlConst.PASS ? "1, " : "0, ") +
                                (foc == FormOfControlConst.PRACTIC ? "1, " : "0, ") + "0, 0, '0.0.0'," +
                                (retake.getType() != null ? retake.getType().toString() : 0) + ", " + student.getIdSR() + ", 17)";

                        getSession().createSQLQuery(query).executeUpdate();

                        query = "update sessionrating set status = '0.1.1' where id_sessionrating = " + student.getIdSR();

                        getSession().createSQLQuery(query).executeUpdate();
                    }
                }

            }
            commit();
            return true;
        } catch (Exception e) {
            rollback();
            e.printStackTrace();
            return false;
        } finally {
            close();
        }
    }

    public boolean removeSRH (Long idSRH) {
        try {
            begin();

            long idSr = (Long) getSession().createSQLQuery(
                    "select id_sessionrating from sessionratinghistory where id_sessionratinghistory = " + idSRH)
                    .uniqueResult();
            int countDelete = getSession().createSQLQuery(
                    "WITH students AS (\n" +
                            "    select id_sessionratinghistory from sessionratinghistory\n" +
                            "      inner join register using(id_register)\n" +
                            "    where id_sessionratinghistory = "+idSRH+" and  (register_number is null or register_number = '')\n" +
                            ") delete from sessionratinghistory using students where sessionratinghistory.id_sessionratinghistory = students.id_sessionratinghistory")
                                          .executeUpdate();

            // если не было удалено, значит у ведомости появился номер ведомости
            if (countDelete == 0) {
                lastMessage = "во время удаления у ведомости появился номер.";
                commit();
                return false;
            }

            getSession().createSQLQuery("update sessionrating set status = '0.0.0' where id_sessionrating = " + idSr).executeUpdate();

            commit();

            return true;
        } catch (Exception e) {
            rollback();
            e.printStackTrace();
            lastMessage = "ошибка при удалении ведомости из базы данных.";
            return false;
        }
    }

    public List<RegisterWithSignDateModelESO> getAllCommissionsWithNullSignDate () {
        String query = "SELECT reg.id_register AS idRegister, register_url AS pathToFile, id_sessionratinghistory AS idSRH\n" +
                       "FROM register reg\n" + "INNER JOIN sessionratinghistory srh USING(id_register)\n" +
                       "WHERE reg.signdate IS NULL AND register_url IS NOT NULL AND register_url <> '' AND retake_count = 3";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idRegister", LongType.INSTANCE)
                              .addScalar("idSRH", LongType.INSTANCE)
                              .addScalar("pathToFile")
                              .setResultTransformer(Transformers.aliasToBean(RegisterWithSignDateModelESO.class));
        return (List<RegisterWithSignDateModelESO>) getList(q);
    }

    public void updateDateSignForRegister (Long idRegister, Long idSRH, Date dateSign) {
        try {
            begin();

            String dateFormat = new SimpleDateFormat("yyyy-MM-dd").format(dateSign);

            getSession().createSQLQuery("update register set signdate = '" + dateFormat + "' where id_register = " + idRegister)
                        .executeUpdate();
            getSession().createSQLQuery("update sessionratinghistory set signdate = '" + dateFormat + "' where id_register = " + idSRH)
                        .executeUpdate();

            commit();
        } catch (Exception e) {
            rollback();
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public List<RegisterReportModelEso> getListRegisterReport(Date from, Date to, long idSem){
        String fromStr = DateConverter.convertDateToStringByFormat(from, "yyyy-MM-dd");
        String toStr = DateConverter.convertDateToStringByFormat(to, "yyyy-MM-dd");

        String query = "select \n" +
                "\tregister_number as registerNumber,r.signdate as signDate, groupname as groupName, subjectname as subjectName," +
                "signatorytutor as signatoryTutor, shorttitle as fullTitle,\n" +
                "\tcase\n" +
                "\t\twhen srh.is_pass = 1 then 'Зачет'\n" +
                "\t\twhen srh.is_courseproject = 1 then 'Курсовой проект'\n" +
                "\t\twhen srh.is_exam = 1 then 'Экзамен'\n" +
                "\t\twhen srh.is_coursework = 1 then 'Курсовая работа'\n" +
                "\t\twhen srh.is_practic = 1 then 'Практика'\n" +
                "\tend as foc,\n" +
                "\tcase\n" +
                "\t\twhen srh.is_pass = 1 then lgss.passdate\n" +
                "\t\twhen srh.is_courseproject = 1 then lgss.tmpcourseprojectdate\n" +
                "\t\twhen srh.is_exam = 1 then examdate\n" +
                "\t\twhen srh.is_coursework = 1 then lgss.tmpcourseworkdate\n" +
                "\t\twhen srh.is_practic = 1 then lgss.practicdate\n" +
                "\tend as examinationDate\n" +
                "from\n" +
                "register r\n" +
                "inner join sessionratinghistory srh using(id_register)\n" +
                "inner join sessionrating using(id_sessionrating)\n" +
                "inner join student_semester_status using(id_student_semester_status)\n" +
                "inner join link_group_semester lgs using(id_link_group_semester)\n" +
                "inner join semester sem on sem.id_semester = lgs.id_semester\n" +
                "inner join dic_group using(id_dic_group)\n" +
                "inner join subject using (id_subject)\n" +
                "inner join chair ch on ch.id_chair = subject.id_chair\n" +
                "inner join department dp on dp.id_chair = ch.id_chair and dp.is_main = true\n" +
                "inner join dic_subject using(id_dic_subject)\n" +
                "inner join link_group_semester_subject lgss on lgss.id_subject = subject.id_subject and lgss.id_link_group_semester = lgs.id_link_group_semester\n" +
                "where r.signdate >= '" + fromStr + "' and r.signdate <= '" + toStr + "' and r.id_semester = " + idSem + "\n" +
                "group by register_number,r.signdate, groupname, subjectname,signatorytutor, shorttitle, srh.is_pass, srh.is_courseproject, \n" +
                "srh.is_exam, srh.is_coursework, srh.is_practic, lgss.passdate, lgss.tmpcourseprojectdate, lgss.examdate, lgss.tmpcourseworkdate, lgss.practicdate\n" +
                "order by signdate, registerNumber";

        Query q = getSession().createSQLQuery(query)
                .addScalar("registerNumber")
                .addScalar("signDate")
                .addScalar("groupName")
                .addScalar("subjectName")
                .addScalar("signatoryTutor")
                .addScalar("fullTitle")
                .addScalar("foc")
                .addScalar("examinationDate")
                .setResultTransformer(Transformers.aliasToBean(RegisterReportModelEso.class));

        return (List<RegisterReportModelEso>) getList(q);
    }

    public List<String> getListTeachersByRegister(Long idRegister) {
        String query = "select\n" + "  family || ' ' || name || ' ' || patronymic\n" + "from\n" + "  register r\n" +
                       "  join sessionratinghistory srh using(id_register)\n" + "  join sessionrating sr using(id_sessionrating)\n" +
                       "  join subject sbj using(id_subject)\n" + "  join link_group_semester_subject lgss using(id_subject)\n" +
                       "  join link_employee_subject_group lesg using(id_link_group_semester_subject)\n" +
                       "  join employee emp using(id_employee)\n" + "  join humanface hf using(id_humanface)\n" + "where\n" +
                       "  id_register = " + idRegister;

        return new ArrayList<>(new HashSet<String>(getSession().createSQLQuery(query).list()));
    }

    public void unSignRegister(Long idRegister) {

        String query = "update register set synchstatus = null where id_register = " + idRegister;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean setSecondSignDate(Long idRegister, Date dateOfBeginSecondSign, Date dateOfEndSecondSign){

        String fromStr = DateConverter.convertDateToStringByFormat(dateOfBeginSecondSign, "yyyy-MM-dd");
        String toStr = DateConverter.convertDateToStringByFormat(dateOfEndSecondSign, "yyyy-MM-dd");

        String query = "update register set" +
                       " second_sign_date_begin = '" + fromStr + "'," +
                       " second_sign_date_end = '" + toStr + "'" +
                       " where id_register = :idRegister";
        return executeUpdate(getSession().createSQLQuery(query)
                                  .setParameter("idRegister", idRegister));
    }

    public List<StudentModel> getStudentForPreview(Long idLgss){
        //language=GenericSQL
        String query = "SELECT DISTINCT\n" +
                "      hf.family, hf.name, hf.patronymic, sss.id_student_semester_status as idSSS, SR.id_sessionrating as idSR, SR.type as type, lgs.id_semester as idSemester\n" +
                "      FROM link_group_semester_subject LGSS\n" +
                "      INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "      INNER JOIN student_semester_status SSS USING (id_link_group_semester)\n" +
                "      INNER JOIN studentcard SC USING (id_studentcard)\n" +
                "      inner join humanface hf using (id_humanface)\n" +
                "      INNER JOIN (SELECT\n" +
                "                    SSS.id_studentcard AS idSC,\n" +
                "                    LGS.id_dic_group   AS idDG\n" +
                "                  FROM student_semester_status SSS\n" +
                "                    INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "                    INNER JOIN semester SEM USING (id_semester)\n" +
                "                  --текущий семестр, студент не отчислен и студент не в академе\n" +
                "                  WHERE SEM.is_current_sem = 1 AND SSS.is_deducted = 0 AND SSS.is_academicleave = 0) searchStudent\n" +
                "        ON SSS.id_studentcard = searchStudent.idSC AND LGS.id_dic_group = searchStudent.idDG\n" +
                "      INNER JOIN sessionrating SR ON SR.id_student_semester_status = SSS.id_student_semester_status AND SR.id_subject = LGSS.id_subject\n" +
                "    WHERE LGSS.id_link_group_semester_subject = " + idLgss+
                "          AND SSS.is_deducted = 0 AND SSS.is_academicleave = 0 --Не отчислен и не в академе в том семестре\n" +
                "          AND SC.id_current_dic_group = LGS.id_dic_group --Текущая группа студента\n" +
                "          AND SC.is_debtor <> true \n" +
                "          AND SSS.is_listener = 0 --Слушателям не открываем\n" +
                "          AND SR.is_notactual = 0 --Только актуальные оценки рассматриваем\n" +
                "          AND ((SR.is_exam = 1 AND SR.examrating < 3) --не должно быть оценки по предмету\n" +
                "               OR (SR.is_pass = 1 AND SR.passrating < 3 AND SR.passrating <> 1)\n" +
                "               OR (SR.is_courseproject = 1 AND SR.courseprojectrating < 3)\n" +
                "               OR (SR.is_coursework = 1 AND SR.courseworkrating < 3)\n" +
                "               OR (SR.is_practic = 1 AND SR.practicrating < 3 AND SR.practicrating <> 1 AND SR.is_pass = 0))";

        Query q = getSession().createSQLQuery(query)
               .addScalar("family")
                .addScalar("name")
                .addScalar("patronymic")
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("idSR", LongType.INSTANCE)
                .addScalar("type", IntegerType.INSTANCE)
                .addScalar("idSemester", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(StudentModel.class));

        return (List<StudentModel>) q.list();
    }
}

