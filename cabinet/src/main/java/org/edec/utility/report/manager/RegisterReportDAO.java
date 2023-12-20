package org.edec.utility.report.manager;

import org.edec.dao.DAO;
import org.edec.newOrder.model.report.ProtocolComissionerModel;
import org.edec.utility.report.model.register.RegisterJasperModel;
import org.edec.utility.report.model.register.StudentModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import java.util.List;


public class RegisterReportDAO extends DAO {
    public RegisterJasperModel getRegisterReportModel(Long idRegister, Long idHumanface) {
        String query = "SELECT\n" +
                "\tDISTINCT ON (R.id_register)\n" +
                "\tINST.shorttitle AS institute, CAST(LGS.course AS TEXT), CAST(LGS.semesternumber AS TEXT) AS semester, DG.groupname, \n" +
                "\tDS.subjectname AS subject, CUR.specialitytitle AS speciality, CUR.directioncode AS major,\n" +
                "\tDIR.title AS direction, DIR.code AS dircode,\n" +
                "\tTO_CHAR(COALESCE(R.signdate, now()), 'dd.MM.yyyy') AS signdate,\n" +
                "\tCASE\n" +
                "\t\tWHEN abs(SRH.retake_count) <> 1 THEN TO_CHAR(COALESCE(R.begindate, now()), 'dd.MM.yyyy')\n" +
                "\t\tWHEN SRH.is_exam = 1 THEN TO_CHAR(LGSS.examdate, 'dd.MM.yyyy')\n" +
                "\t\tWHEN SRH.is_pass = 1 THEN TO_CHAR(LGSS.passdate, 'dd.MM.yyyy')\n" +
                "\t\tWHEN SRH.is_courseproject = 1 THEN TO_CHAR(LGSS.tmpcourseprojectdate, 'dd.MM.yyyy')\n" +
                "\t\tWHEN SRH.is_coursework = 1 THEN TO_CHAR(LGSS.tmpcourseworkdate, 'dd.MM.yyyy')\n" +
                "\t\tWHEN SRH.is_practic = 1 THEN TO_CHAR(LGSS.practicdate, 'dd.MM.yyyy')\n" +
                "\tEND AS dateOfExamination,\n" +
                "\tCASE\n" +
                "\t\tWHEN S.hourscount is not null and S.hourscount > 0 and (SRH.is_exam = 1 OR SRH.is_pass = 1 OR SRH.is_practic = 1) THEN S.hourscount||'/'||trunc((CAST(S.hourscount/CAST(36 AS FLOAT) AS NUMERIC)), 1)\n" +
                "\tELSE '0/0.0' END AS totalHours,\n" +
                "\tHF.family||' '||HF.name||' '||HF.patronymic AS teacher,\n" +
                "\tCASE\n" +
                "\t\tWHEN SRH.is_exam = 1 THEN 1\n" +
                "\t\tWHEN SRH.is_pass = 1 THEN 2\n" +
                "\t\tWHEN SRH.is_courseproject = 1 THEN 3\n" +
                "\t\tWHEN SRH.is_coursework = 1 THEN 4\n" +
                "\t\tWHEN SRH.is_practic = 1 THEN 5\n" +
                "\tEND AS formOfControl, SRH.retake_count AS retakeCount, \n" +
                "\tR.register_number AS registerNumber, R.certnumber, R.signatorytutor, SRH.type,\n" +
                "\tDE.shorttitle AS chair,\n" +
                "\tCUR.formofstudy AS formofstudy\n" +
                "FROM\n" +
                "\tlink_group_semester LGS\n" +
                "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "\tINNER JOIN institute INST USING (id_institute)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN curriculum CUR USING (id_curriculum)\n" +
                "\tINNER JOIN direction DIR USING (id_direction)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_link_group_semester)\n" +
                "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" +
                "\tINNER JOIN sessionratinghistory SRH USING (id_sessionrating)\n" +
                "\tINNER JOIN register R USING (id_register)\n" +
                "\tINNER JOIN subject S USING (id_subject)\n" +
                "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "\tINNER JOIN link_group_semester_subject LGSS USING (id_subject)\n" +
                "\tINNER JOIN link_employee_subject_group LESG USING (id_link_group_semester_subject)\n" +
                "\tINNER JOIN employee EMP USING (id_employee)\n" +
                "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "\tINNER JOIN department DE ON DE.id_chair = S.id_chair AND DE.is_main = true\n" +
                "WHERE\n" +
                "\tR.id_register = :idRegister AND CAST(HF.id_humanface AS TEXT) ILIKE :idHum";
        Query q = getSession().createSQLQuery(query)
                .addScalar("institute").addScalar("course").addScalar("semester").addScalar("groupname").addScalar("subject")
                .addScalar("speciality").addScalar("major")
                .addScalar("direction").addScalar("dircode")
                .addScalar("signdate").addScalar("dateOfExamination").addScalar("totalHours")
                .addScalar("formOfControl").addScalar("retakeCount").addScalar("registerNumber")
                .addScalar("certnumber").addScalar("signatorytutor").addScalar("type")
                .addScalar("teacher").addScalar("chair")
                .addScalar("formofstudy", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(RegisterJasperModel.class));
        q.setLong("idRegister", idRegister).setParameter("idHum", idHumanface == null ? "%%" : String.valueOf(idHumanface), StringType.INSTANCE);
        List<?> list = getList(q);
        return list.size() == 0 ? null : (RegisterJasperModel) list.get(0);
    }

    public List<ProtocolComissionerModel> getTeachersByRegister(Long idRegister){
        String query = "SELECT\n" +
                "\tDISTINCT HF.family||' '||HF.name||' '||HF.patronymic AS fio\n" +
                "FROM\n" +
                "\tlink_group_semester LGS\n" +
                "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "\tINNER JOIN institute INST USING (id_institute)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN curriculum CUR USING (id_curriculum)\n" +
                "\tINNER JOIN direction DIR USING (id_direction)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_link_group_semester)\n" +
                "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" +
                "\tINNER JOIN sessionratinghistory SRH USING (id_sessionrating)\n" +
                "\tINNER JOIN register R USING (id_register)\n" +
                "\tINNER JOIN subject S USING (id_subject)\n" +
                "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "\tINNER JOIN link_group_semester_subject LGSS USING (id_subject)\n" +
                "\tINNER JOIN link_employee_subject_group LESG USING (id_link_group_semester_subject)\n" +
                "\tINNER JOIN employee EMP USING (id_employee)\n" +
                "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "\tINNER JOIN department DE ON DE.id_chair = S.id_chair AND DE.is_main = true\n" +
                "WHERE\n" +
                "\tR.id_register = :idRegister";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .setResultTransformer(Transformers.aliasToBean(ProtocolComissionerModel.class));
        q.setLong("idRegister", idRegister);
        List<?> list = getList(q);
        return (List<ProtocolComissionerModel>) list;
    }

    public List<StudentModel> getStudentByRegister(Long idRegister) {

        String query = "SELECT\n" +
                "\tHF.family||' '||SUBSTRING(HF.name FROM 1 FOR 1)||'. '||SUBSTRING(HF.patronymic FROM 1 FOR 1)||'.' AS fio,\n" +
                "\tSC.recordbook AS recordBook, SRH.newrating AS rating,\n" +
                "\tCASE\n" +
                "\t\tWHEN SRH.is_courseproject = 1 THEN COALESCE(SR.esocourseprojecttheme, '')\n" +
                "\t\tWHEN SRH.is_coursework = 1 THEN COALESCE(SR.esocourseworktheme, '')\n" +
                "\tELSE '' END AS themeOfWork\n" +
                "FROM sessionratinghistory SRH\n" +
                "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "WHERE SRH.id_register = :idRegister\n" +
                "\tAND (SRH.retake_count <> 1 OR (SRH.retake_count = 1 AND SC.is_debtor = false))\n" +
                "AND  (SSS.is_deducted <> 1  or (sss.is_deducted = 1 and (select certnumber from register where id_register = :idRegister) is not null) and (select certnumber from register where id_register = :idRegister) <> '  ' and SRH.newrating <> 0)\n" + // КОСТЫЛИЩЕ. При подтверждении без ЭП там откуда-то два пробела
                "AND ((SELECT is_academicleave FROM student_semester_status sss2 where sss2.id_studentcard = sc.id_studentcard order by id_student_semester_status desc limit 1) <> 1 or\n" + // Если на текущий момент студент не академист
                "\t\t    (sss.is_academicleave = 1 and (select certnumber from register where id_register = :idRegister) is not null) and (select certnumber from register where id_register = :idRegister) <> '  ' and SRH.newrating <> 0)" + // Если академист, но ведомость уже подписана
                "ORDER BY fio";
                // Костыли условий:
                // Если создали ведомость, а потом отчислили или ушел в академ - студент не попадает в ведомость SRH.retake_count <> 1 SSS.is_deducted <> 1
                // Если подписали ведомость, в которой был отчисленный или академист - не отображаем его в ведомости SRH.newrating <> 0
                // Если подтвердили ведомость certnumber = '  ', потом отчислили - не отображаем в ведомости.
                // Если подпписали, а уже после студент ушел в академ или отчислился, в ведомости он будет числиться, так как SRH.newrating <> 0
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio").addScalar("recordBook").addScalar("rating").addScalar("themeOfWork")
                .setResultTransformer(Transformers.aliasToBean(StudentModel.class));
        q.setLong("idRegister", idRegister);

        return (List<StudentModel>) getList(q);
    }

    public List<StudentModel> getStudentByRegisterWithDeducted(Long idRegister) {

        String query = "SELECT\n" +
                "\tHF.family||' '||SUBSTRING(HF.name FROM 1 FOR 1)||'. '||SUBSTRING(HF.patronymic FROM 1 FOR 1)||'.' AS fio,\n" +
                "\tSC.recordbook AS recordBook, SRH.newrating AS rating,\n" +
                "\tCASE\n" +
                "\t\tWHEN SRH.is_courseproject = 1 THEN COALESCE(SR.esocourseprojecttheme, '')\n" +
                "\t\tWHEN SRH.is_coursework = 1 THEN COALESCE(SR.esocourseworktheme, '')\n" +
                "\tELSE '' END AS themeOfWork\n" +
                "FROM sessionratinghistory SRH\n" +
                "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "WHERE SRH.id_register = :idRegister\n" +
                "AND (SSS.is_deducted <> 1 or (sss.is_deducted = 1 and (select signdate from register where id_register = :idRegister) is not null))\n" + // КОСТЫЛИЩЕ. При подтверждении без ЭП там откуда-то два пробела
                "AND SRH.newrating <> 0\n" +
                "ORDER BY fio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio").addScalar("recordBook").addScalar("rating").addScalar("themeOfWork")
                .setResultTransformer(Transformers.aliasToBean(StudentModel.class));
        q.setLong("idRegister", idRegister);

        return (List<StudentModel>) getList(q);
    }
}
