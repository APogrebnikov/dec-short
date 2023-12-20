package org.edec.student.recordBook.manager;

import org.edec.dao.DAO;
import org.edec.student.recordBook.model.dao.GroupSemesterEsoModel;
import org.edec.student.recordBook.model.dao.RatingEsoModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;


public class EntityManagerRecordBook extends DAO {
    public List<GroupSemesterEsoModel> getGroupSemester(Long idHum) {
        String query =
                "SELECT DG.groupname, SSS.id_student_semester_status AS idSSS, LGS.semesterNumber||' семестр' AS semesterNumber,\n" +
                        "\tCONF.is_physcul AS physcul, CONF.is_attendance AS attendance, CONF.is_eok AS eok, \n" +
                        "\tCONF.is_performance AS performance, CONF.max_red_level AS maxRedLevel, CONF.min_green_level AS minGreenLevel, SEM.id_semester AS idSem\n" +
                        "FROM studentcard SC\n" +
                        "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                        "\tINNER JOIN link_group_semester LGS ON sss.id_link_group_semester = LGS.id_link_group_semester and lgs.id_dic_group = sc.id_current_dic_group\n" +
                        "\tINNER JOIN dic_group DG ON lgs.id_dic_group = dg.id_dic_group\n" +
                        "\tLEFT JOIN curriculum CUR USING (id_curriculum)\n" +
                        "\tINNER JOIN semester SEM USING (id_semester)\n" +
                        "\tLEFT JOIN configuration_efficiency CONF ON SEM.id_semester = CONF.id_semester AND SEM.is_current_sem = 1 AND LGS.id_dic_group = SC.id_current_dic_group\n" +
                        "\t\tAND (CONF.is_master = TRUE OR (CONF.is_master = FALSE AND CUR.qualification IN (1,2)))\n" +
                        "WHERE SC.id_humanface = :idHum\n" + "ORDER BY\tSC.id_current_dic_group DESC, LGS.id_link_group_semester DESC";

        Query q = getSession().createSQLQuery(query)
                .addScalar("groupname")
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("semesterNumber")
                .addScalar("physcul")
                .addScalar("attendance")
                .addScalar("eok")
                .addScalar("performance")
                .addScalar("maxRedLevel")
                .addScalar("minGreenLevel")
                .addScalar("idSem")
                .setResultTransformer(Transformers.aliasToBean(GroupSemesterEsoModel.class));
        q.setLong("idHum", idHum);
        return (List<GroupSemesterEsoModel>) getList(q);
    }

    public List<RatingEsoModel> getRatingEsoModelBySSS(Long idSSS) {
        String query = "SELECT CASE WHEN LGSS.examdate IS NOT NULL THEN LGSS.examdate\n" +
                "\t\tWHEN LGSS.passdate IS NOT NULL THEN LGSS.passdate END AS dateOfPass, \n" +
                "\t\tLGSS.tmpcourseprojectdate as cpDate, \n" +
                "\tLGSS.consultationdate, MAX(R.begindate) AS statusBeginDate, MAX(R.enddate) AS statusFinishDate, SR.status,\n" +
                "\tSR.examrating, SR.passrating, SR.courseprojectrating AS cprating, SR.courseworkrating AS cwrating, SR.practicrating,\n" +
                "\tSR.esoexamrating, SR.esopassrating, SR.esocourseprojectrating AS esocprating, SR.esocourseworkrating AS esocwrating,\n" +
                "\tSR.is_exam = 1 AS exam, SR.is_Pass = 1 AS pass, SR.is_courseproject = 1 AS cp, SR.is_coursework = 1 AS cw, SR.is_practic = 1 AS practic,\n" +
                "\tCOALESCE((SELECT signatorytutor FROM sessionratinghistory SRH INNER JOIN register R USING (id_register) WHERE SRH.id_sessionrating = SR.id_sessionrating AND SRH.is_exam = 1 LIMIT 1), SR.esotmpexamtutor, '') AS examTutor, \n" +
                "\tCOALESCE((SELECT signatorytutor FROM sessionratinghistory SRH INNER JOIN register R USING (id_register) WHERE SRH.id_sessionrating = SR.id_sessionrating AND SRH.is_pass = 1 LIMIT 1), SR.esotmppasstutor, '') AS passTutor, \n" +
                "\tCOALESCE((SELECT signatorytutor FROM sessionratinghistory SRH INNER JOIN register R USING (id_register) WHERE SRH.id_sessionrating = SR.id_sessionrating AND SRH.is_courseproject = 1 LIMIT 1), SR.esotmpcourseprojecttutor, '') AS cpTutor, \n" +
                "\tCOALESCE((SELECT signatorytutor FROM sessionratinghistory SRH INNER JOIN register R USING (id_register) WHERE SRH.id_sessionrating = SR.id_sessionrating AND SRH.is_coursework = 1 LIMIT 1), SR.esotmpcourseworktutor, '') AS cwTutor, \n" +
                "\t(SELECT signatorytutor FROM sessionratinghistory INNER JOIN register R USING (id_register) WHERE id_sessionrating = SR.id_sessionrating AND SR.is_practic = 1 LIMIT 1) AS practicTutor,\n" +
                "\tHF.family||' '||HF.name||' '||HF.patronymic AS teacher,\n" +
                "\tSR.esogradecurrent, SR.esogrademax, SR.retake_count AS retakeCount, SR.status, S.hoursCount, DS.subjectName, LGSS.id_esocourse2 AS idEsoCourse,\n" +
                "\tSR.lesson_count AS lessonCount, SR.visit_count AS visitCount\n" +
                "FROM sessionrating SR\n" +
                "\tLEFT JOIN sessionratinghistory SRH USING (id_sessionrating)\n" +
                "\tLEFT JOIN register R USING (id_register)\n" +
                "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "\tINNER JOIN link_group_semester_subject LGSS USING (id_subject)\n" +
                "\tLEFT JOIN link_employee_subject_group LESG ON LGSS.id_link_group_semester_subject = LESG.id_link_group_semester_subject \n" +
                "\t\tAND LESG.id_link_employee_subject_group = (SELECT MAX(LESG2.id_link_employee_subject_group)\n" +
                "\t\t\tFROM \tlink_employee_subject_group LESG2\n" +
                "\t\t\tWHERE\tLESG2.id_link_group_semester_subject = LGSS.id_link_group_semester_subject)\n" +
                "\tLEFT JOIN employee EMP USING (id_employee)\n" +
                "\tLEFT JOIN humanface HF USING (id_humanface)\t \n" +
                "WHERE id_student_semester_status = :idSSS\n" +
                "GROUP BY SR.id_sessionrating, LGSS.id_link_group_semester_subject, S.id_subject, DS.subjectName, HF.id_humanface\n" +
                "ORDER BY subjectName";
        Query q = getSession().createSQLQuery(query)
                .addScalar("dateOfPass").addScalar("cpDate").addScalar("consultationDate").addScalar("statusBeginDate").addScalar("statusFinishDate")
                .addScalar("examrating").addScalar("passrating").addScalar("cprating").addScalar("cwrating").addScalar("practicrating")
                .addScalar("esoexamrating").addScalar("esopassrating").addScalar("esocprating").addScalar("esocwrating")
                .addScalar("exam").addScalar("pass").addScalar("cp").addScalar("cw").addScalar("practic")
                .addScalar("examTutor").addScalar("passTutor").addScalar("cpTutor").addScalar("cwTutor").addScalar("practicTutor")
                .addScalar("teacher")
                .addScalar("esogradecurrent").addScalar("esogrademax")
                .addScalar("retakeCount").addScalar("status")
                .addScalar("hoursCount").addScalar("subjectName")
                .addScalar("idEsoCourse", LongType.INSTANCE)
                .addScalar("lessonCount", LongType.INSTANCE)
                .addScalar("visitCount", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(RatingEsoModel.class));
        q.setLong("idSSS", idSSS);
        return (List<RatingEsoModel>) getList(q);
    }
}