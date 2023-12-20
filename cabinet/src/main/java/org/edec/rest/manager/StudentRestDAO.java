package org.edec.rest.manager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.edec.dao.DAO;
import org.edec.model.AttendanceModel;
import org.edec.rest.ctrl.HttpClientUtil;
import org.edec.rest.model.student.Efficiency;
import org.edec.rest.model.student.Progress;
import org.edec.rest.model.student.ProgressDetail;
import org.edec.rest.model.student.request.AttendanceResultMsg;
import org.edec.rest.model.student.response.*;
import org.edec.teacher.model.attendanceProgress.dao.AttendanceEsoModel;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.postgresql.jdbc2.TimestampUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class StudentRestDAO extends DAO {

    public Student getStudentInfoByLogin(String login) {
        String query = "SELECT \n" +
                "\tsc.id_studentcard AS idStudent, \n" +
                "\thf.id_humanface AS idHumanface, \n" +
                "\tdg.id_dic_group AS idGroup, \n" +
                "\thf.family as surname, \n" +
                "\thf.name AS name, \n" +
                "\thf.patronymic AS patronymic,\n" +
                "\ti.shorttitle as institute,\n" +
                "\tdg.groupname AS groupname,\n" +
                "\tsc.recordbook AS recordbook,\n" +
                "\thf.get_notification as notification,\n" +
                "\thf.email AS email,\n" +
                "\thf.birthday AS birthday,\n" +
                "\tsss.is_group_leader AS groupLeader,\n" +
                "\tlgs.course AS course,\n" +
                "CASE \n" +
                "WHEN sss.formofstudy = 1 THEN 'очная'\n" +
                "WHEN sss.formofstudy = 2 THEN 'заочная'\n" +
                "WHEN sss.formofstudy = 3 THEN 'вечерняя'\n" +
                "END AS formofstudy,\n" +
                "\tcu.specialitytitle AS directionName,\n" +
                "\tcu.directioncode AS directionCode,\n" +
                "\thf.start_page AS startPage,\n" +
                "DE.fulltitle AS chairFulltitle,\n" +
                "(SELECT date_finish FROM reference WHERE id_studentcard = sc.id_studentcard ORDER BY date_finish DESC LIMIT 1) AS refDateFinish,\n" +
                "sc.other_esoid AS otherEsoid\n" +
                "FROM studentcard sc \n" +
                "FULL JOIN dic_group dg ON dg.id_dic_group = sc.id_current_dic_group\n" +
                "INNER JOIN link_group_semester lgs ON dg.id_dic_group = lgs.id_dic_group\n" +
                "INNER JOIN semester se ON LGS.id_semester = se.id_semester AND se.is_current_sem = 1\n" +
                "INNER JOIN student_semester_status SSS ON LGS.id_link_group_semester = SSS.id_link_group_semester AND SSS.id_studentcard = SC.id_studentcard\n" +
                "INNER JOIN institute i ON i.id_institute = dg.id_institute\n" +
                "INNER JOIN curriculum cu ON cu.id_curriculum = dg.id_curriculum\n" +
                "LEFT JOIN direction DR ON CU.id_direction = DR.id_direction\n" +
                "INNER JOIN chair CH ON CU.id_chair=CH.id_chair\n" +
                "LEFT JOIN department DE ON CH.id_chair=DE.id_chair AND DE.is_main = true\n" +
                "INNER JOIN humanface hf using (id_humanface)\n" +
                "WHERE ldap_login ilike :login";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("login", login);
        ((SQLQuery) q)
                .addScalar("idStudent", LongType.INSTANCE)
                .addScalar("idHumanface", LongType.INSTANCE)
                .addScalar("idGroup", LongType.INSTANCE)
                .addScalar("surname")
                .addScalar("name")
                .addScalar("patronymic")
                .addScalar("institute")
                .addScalar("groupname")
                .addScalar("recordbook")
                .addScalar("notification", BooleanType.INSTANCE)
                .addScalar("email")
                .addScalar("birthday", DateType.INSTANCE)
                .addScalar("groupLeader", BooleanType.INSTANCE)
                .addScalar("course", IntegerType.INSTANCE)
                .addScalar("formofstudy")
                .addScalar("directionName")
                .addScalar("directionCode")
                .addScalar("startPage")
                .addScalar("chairFulltitle")
                .addScalar("refDateFinish", DateType.INSTANCE)
                .addScalar("otherEsoid", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(Student.class));
        List<Student> list = (List<Student>) getList(q);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    public List<Student> getStudentClassmates(String login) {
        String query = "SELECT  \n" +
                "sc.id_studentcard AS idStudent, \n" +
                "hf.id_humanface AS idHumanface, \n" +
                "dg.id_dic_group AS idGroup, \n" +
                "hf.family as surname, \n" +
                "hf.name AS name, \n" +
                "hf.patronymic AS patronymic, \n" +
                "i.shorttitle as institute, \n" +
                "dg.groupname AS groupname, \n" +
                "sc.recordbook AS recordbook, \n" +
                "hf.get_notification as notification, \n" +
                "hf.email AS email, \n" +
                "hf.birthday AS birthday  \n" +
                "FROM studentcard sc  \n" +
                "FULL JOIN dic_group dg ON dg.id_dic_group = sc.id_current_dic_group AND dg.id_dic_group = " +
                "(SELECT sci.id_current_dic_group FROM studentcard sci WHERE sci.ldap_login ilike :login)\n" +
                "INNER JOIN institute i using (id_institute)  \n" +
                "INNER JOIN humanface hf using (id_humanface)";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("login", login);
        ((SQLQuery) q)
                .addScalar("idStudent", LongType.INSTANCE)
                .addScalar("idHumanface", LongType.INSTANCE)
                .addScalar("idGroup", LongType.INSTANCE)
                .addScalar("surname")
                .addScalar("name")
                .addScalar("patronymic")
                .addScalar("institute")
                .addScalar("groupname")
                .addScalar("recordbook")
                .addScalar("notification", BooleanType.INSTANCE)
                .addScalar("email")
                .addScalar("birthday", DateType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(Student.class));
        List<Student> list = (List<Student>) getList(q);
        return (list != null && list.size() > 0) ? list : null;
    }


    public List<Rating> getAVGProgressOnSemester(String login, Long idLGS) {
        String query = "SELECT " +
                "sc.ldap_login AS login,\n" +
                "sr.is_exam = 1 AS isExam,\n" +
                "sr.is_Pass = 1 AS isPass,\n" +
                "sr.is_courseproject = 1 AS isCP,\n" +
                "sr.is_coursework = 1 AS isCW,\n" +
                "sr.is_practic = 1 AS isPractice,\n" +
                "sr.examrating AS examrating,\n" +
                "sr.passrating AS passrating,\n" +
                "sr.courseprojectrating AS cprating,\n" +
                "sr.courseworkrating AS cwrating,\n" +
                "sr.practicrating AS practicerating\n" +
                "FROM studentcard sc \n" +
                "INNER JOIN student_semester_status sss USING (id_studentcard)\n" +
                "INNER JOIN sessionrating sr USING (id_student_semester_status)\n" +
                "INNER JOIN link_group_semester lgs ON lgs.id_link_group_semester = sss.id_link_group_semester\n" +
                "INNER JOIN subject s USING (id_subject)\n" +
                "INNER JOIN dic_subject ds USING (id_dic_subject)\n" +
                "INNER JOIN link_group_semester_subject lgss USING (id_subject)\n" +
                "WHERE \n" +
                "lgs.id_dic_group = (SELECT id_current_dic_group FROM studentcard WHERE ldap_login ilike :login)\n" +
                "AND\n" +
                "sc.id_current_dic_group = lgs.id_dic_group\n" +
                "AND\n" +
                "sss.is_deducted=0 AND sss.is_academicleave=0\n" +
                "AND\n" +
                "lgs.semesternumber <= (select lgsi.semesternumber FROM link_group_semester lgsi WHERE lgsi.id_link_group_semester = :semester)\n" +
                "ORDER BY lgs.semesternumber, isExam DESC";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("login", login);
        q.setLong("semester", idLGS);
        ((SQLQuery) q)
                .addScalar("login")
                .addScalar("isExam", BooleanType.INSTANCE)
                .addScalar("isPass", BooleanType.INSTANCE)
                .addScalar("isCP", BooleanType.INSTANCE)
                .addScalar("isCW", BooleanType.INSTANCE)
                .addScalar("isPractice", BooleanType.INSTANCE)
                .addScalar("examrating", IntegerType.INSTANCE)
                .addScalar("passrating", IntegerType.INSTANCE)
                .addScalar("cprating", IntegerType.INSTANCE)
                .addScalar("cwrating", IntegerType.INSTANCE)
                .addScalar("practicerating", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(Rating.class));
        List<Rating> list = (List<Rating>) getList(q);
        return (list != null && list.size() > 0) ? list : null;
    }

    public double calcAVGRating(List<Rating> ratings) {
        double res;
        int count = 0;
        int cur = 0;
        for (Rating rating : ratings) {
            if (rating.getIsExam()) {
                count++;
                cur += rating.getExamrating();
            }
            if (rating.getIsPass()) {
                /// Игнорируем зачеты
                //count++;
                //cur += rating.getPassrating() * 5;
            }
            if (rating.getIsCW()) {
                count++;
                cur += rating.getCwrating();
            }
            if (rating.getIsCP()) {
                count++;
                cur += rating.getCprating();
            }
            if (rating.getIsPractice()) {
                count++;
                cur += rating.getPracticerating();
            }
        }
        res = cur * 1.0 / count;
        return res;
    }

    public List<Rating> getProgressBySemester(String login, Long idLGS) {
        String query = "SELECT\n" +
                "lgs.id_link_group_semester AS idLGS,\n" +
                "lgs.id_dic_group AS idGroup,\n" +
                "CASE\n" +
                "WHEN lgss.examdate IS NOT NULL THEN lgss.examdate\n" +
                "WHEN lgss.passdate IS NOT NULL THEN lgss.passdate\n" +
                "END AS dateOfPass, lgss.consultationdate,\n" +
                "sr.is_exam = 1 AS isExam,\n" +
                "sr.is_Pass = 1 AS isPass,\n" +
                "sr.is_courseproject = 1 AS isCP,\n" +
                "sr.is_coursework = 1 AS isCW,\n" +
                "sr.is_practic = 1 AS isPractice,\n" +
                "COALESCE((SELECT newrating FROM sessionratinghistory srh WHERE srh.id_sessionrating = sr.id_sessionrating AND srh.is_exam = 1 AND newrating < 0 ORDER BY retake_count DESC LIMIT 1), sr.examrating, 0) AS examrating,\n" +
                "COALESCE((SELECT newrating FROM sessionratinghistory srh WHERE srh.id_sessionrating = sr.id_sessionrating AND srh.is_pass = 1 AND newrating < 0 ORDER BY retake_count DESC LIMIT 1), sr.passrating, 0) AS passrating,\n" +
                "COALESCE((SELECT newrating FROM sessionratinghistory srh WHERE srh.id_sessionrating = sr.id_sessionrating AND srh.is_courseproject = 1 AND newrating < 0 ORDER BY retake_count DESC LIMIT 1), sr.courseprojectrating, 0) AS cprating,\n" +
                "COALESCE((SELECT newrating FROM sessionratinghistory srh WHERE srh.id_sessionrating = sr.id_sessionrating AND srh.is_coursework = 1 AND newrating < 0 ORDER BY retake_count DESC LIMIT 1), sr.courseworkrating, 0) AS cwrating,\n" +
                "COALESCE((SELECT newrating FROM sessionratinghistory srh WHERE srh.id_sessionrating = sr.id_sessionrating AND srh.is_practic = 1 AND newrating < 0 ORDER BY retake_count DESC LIMIT 1), sr.practicrating, 0) AS practicerating,\n" +
                "COALESCE((SELECT signatorytutor FROM sessionratinghistory srh INNER JOIN register r USING (id_register) WHERE srh.id_sessionrating = sr.id_sessionrating AND srh.is_exam = 1 LIMIT 1), sr.esotmpexamtutor, CASE WHEN sr.is_exam=1 THEN HF.family||' '||HF.name||' '||HF.patronymic ELSE '' END) AS examTutor,\n" +
                "COALESCE((SELECT signatorytutor FROM sessionratinghistory srh INNER JOIN register r USING (id_register) WHERE srh.id_sessionrating = sr.id_sessionrating AND srh.is_pass = 1 LIMIT 1), sr.esotmppasstutor, CASE WHEN sr.is_pass=1 THEN HF.family||' '||HF.name||' '||HF.patronymic ELSE '' END) AS passTutor,\n" +
                "COALESCE((SELECT signatorytutor FROM sessionratinghistory srh INNER JOIN register r USING (id_register) WHERE srh.id_sessionrating = sr.id_sessionrating AND srh.is_courseproject = 1 LIMIT 1), sr.esotmpcourseprojecttutor,CASE WHEN sr.is_courseproject=1 THEN HF.family||' '||HF.name||' '||HF.patronymic ELSE '' END) AS cpTutor,\n" +
                "COALESCE((SELECT signatorytutor FROM sessionratinghistory srh INNER JOIN register r USING (id_register) WHERE srh.id_sessionrating = sr.id_sessionrating AND srh.is_coursework = 1 LIMIT 1), sr.esotmpcourseworktutor, CASE WHEN sr.is_coursework=1 THEN HF.family||' '||HF.name||' '||HF.patronymic ELSE '' END) AS cwTutor,\n" +
                "(SELECT signatorytutor\n" +
                "FROM sessionratinghistory\n" +
                "INNER JOIN register r USING (id_register)\n" +
                "WHERE id_sessionrating = sr.id_sessionrating\n" +
                "AND sr.is_practic = 1 LIMIT 1) AS practiceTutor,\n" +
                "lgs.semesterNumber AS semester,\n" +
                "s.hoursCount,\n" +
                "ds.subjectName,\n " +
                // Поля из электронного журнала и электронных курсов
                "sr.visitcount, " +
                "sr.skipcount, " +
                "sr.esogradecurrent, " +
                "sr.esogrademax, " +
                "lgss.id_link_group_semester_subject AS idLGSS " +
                "FROM student_semester_status sss\n" +
                "INNER JOIN studentcard sc USING (id_studentcard)\n" +
                "INNER JOIN sessionrating sr USING (id_student_semester_status)\n" +
                "INNER JOIN link_group_semester lgs ON lgs.id_link_group_semester = sss.id_link_group_semester\n" +
                "INNER JOIN subject s USING (id_subject)\n" +
                "INNER JOIN dic_subject ds USING (id_dic_subject)\n" +
                "INNER JOIN link_group_semester_subject lgss ON lgs.id_link_group_semester = lgss.id_link_group_semester AND lgss.id_subject = s.id_subject\n" +
                "LEFT JOIN link_employee_subject_group lesg ON lgss.id_link_group_semester_subject = lesg.id_link_group_semester_subject\n" +
                "AND LESG.id_link_employee_subject_group = (SELECT MAX(lesg2.id_link_employee_subject_group)\n" +
                "FROM  link_employee_subject_group lesg2\n" +
                "WHERE lesg2.id_link_group_semester_subject = lgss.id_link_group_semester_subject)\n" +
                "LEFT JOIN employee emp USING (id_employee)\n" +
                "LEFT JOIN humanface hf ON emp.id_humanface = hf.id_humanface\n" +
                "WHERE sc.ldap_login ilike :login AND lgs.id_link_group_semester = :semester\n" +
                "AND sc.id_current_dic_group = lgs.id_dic_group\n" +
                "ORDER BY lgs.semesternumber, isExam DESC";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("login", login);
        q.setLong("semester", idLGS);
        ((SQLQuery) q)
                .addScalar("idLGS", LongType.INSTANCE)
                .addScalar("idGroup", LongType.INSTANCE)
                .addScalar("dateOfPass", DateType.INSTANCE)
                .addScalar("consultationDate", DateType.INSTANCE)
                .addScalar("isExam", BooleanType.INSTANCE)
                .addScalar("isPass", BooleanType.INSTANCE)
                .addScalar("isCP", BooleanType.INSTANCE)
                .addScalar("isCW", BooleanType.INSTANCE)
                .addScalar("isPractice", BooleanType.INSTANCE)
                .addScalar("examrating", IntegerType.INSTANCE)
                .addScalar("passrating", IntegerType.INSTANCE)
                .addScalar("cprating", IntegerType.INSTANCE)
                .addScalar("cwrating", IntegerType.INSTANCE)
                .addScalar("practicerating", IntegerType.INSTANCE)
                .addScalar("examTutor")
                .addScalar("passTutor")
                .addScalar("cpTutor")
                .addScalar("cwTutor")
                .addScalar("practiceTutor")
                .addScalar("semester", IntegerType.INSTANCE)
                .addScalar("hoursCount", LongType.INSTANCE)
                .addScalar("subjectName")
                .addScalar("visitcount", IntegerType.INSTANCE)
                .addScalar("skipcount", IntegerType.INSTANCE)
                .addScalar("esogradecurrent", LongType.INSTANCE)
                .addScalar("esogrademax", LongType.INSTANCE)
                .addScalar("idLGSS", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(Rating.class));
        List<Rating> list = (List<Rating>) getList(q);
        return (list != null && list.size() > 0) ? list : null;
    }


    public List<StudentSemester> getStudentSemesters(String login) {
        // TODO: change to ilike
        String query = "SELECT\n" +
                "LGS.id_link_group_semester AS idLGS,\n" +
                "LGS.semesternumber AS semesterNumber,\n" +
                "SSS.id_student_semester_status AS idSSS,\n" +
                "LGS.semesternumber AS semesterNumber,\n" +
                "DG.groupname AS groupname,\n" +
                "LGS.id_dic_group AS idGroup,\n" +
                "CASE \n" +
                "WHEN SE.season = 0 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' осенний'\n" +
                "WHEN SE.season = 1 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' весенний' \n" +
                "END AS semesterName,\n" +
                "SY.dateofbegin, SY.dateofend,\n" +
                "LGS.dateofbeginsession, LGS.dateofendsession,\n" +
                "LGS.dateofbeginpassweek, LGS.dateofendpassweek\n " +
                "FROM studentcard SC\n" +
                "INNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "INNER JOIN dic_group DG USING (id_dic_group)\n" +
                "INNER JOIN semester SE USING (id_semester)\n" +
                "INNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                "WHERE SC.ldap_login ilike :login\n" +
                "AND sc.id_current_dic_group = lgs.id_dic_group\n" +
                "ORDER BY LGS.semesternumber";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("login", login);
        ((SQLQuery) q)
                .addScalar("idLGS", LongType.INSTANCE)
                .addScalar("semesterNumber", IntegerType.INSTANCE)
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("groupname")
                .addScalar("idGroup", LongType.INSTANCE)
                .addScalar("semesterName")
                .addScalar("dateofbegin", DateType.INSTANCE)
                .addScalar("dateofend", DateType.INSTANCE)
                .addScalar("dateofbeginsession", DateType.INSTANCE)
                .addScalar("dateofendsession", DateType.INSTANCE)
                .addScalar("dateofbeginpassweek", DateType.INSTANCE)
                .addScalar("dateofendpassweek", DateType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(StudentSemester.class));
        List<StudentSemester> list = (List<StudentSemester>) getList(q);
        return (list != null && list.size() > 0) ? list : null;
    }

    public List<Shedule> getSheduleForGroupByDate(String login, Integer week, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String query = "SELECT (ROW_NUMBER() OVER (ORDER BY lgschs.id_dic_time_lesson) - 1) AS pos, \n" +
                "\tlgss.id_link_group_semester_subject AS idLGSS,\n" +
                "\tds.subjectname AS subjectName,\n" +
                "\tlgschs.lesson AS lesson\n" +
                "      FROM link_group_semester lgs\n" +
                "INNER JOIN dic_group dg ON dg.id_dic_group = lgs.id_dic_group AND dg.id_dic_group =\n" +
                "   (SELECT sci.id_current_dic_group FROM studentcard sci WHERE sci.ldap_login ilike :login)\n" +
                "INNER JOIN semester sem USING (id_semester)\n" +
                "INNER JOIN link_group_semester_subject lgss USING (id_link_group_semester)\n" +
                "INNER JOIN link_group_schedule_subject lgschs USING (id_link_group_semester_subject)\n" +
                "INNER JOIN subject s USING (id_subject)\n" +
                "INNER JOIN dic_subject ds USING (id_dic_subject)\n" +
                "   WHERE sem.is_current_sem = 1\n" +
                "       AND lgschs.week = :week\n" +
                "       AND lgschs.id_dic_day_lesson = (SELECT EXTRACT(ISODOW FROM DATE (:date)))";

        Query q = getSession().createSQLQuery(query);
        q.setParameter("login", login);
        q.setParameter("date", dateFormat.format(date));
        q.setParameter("week", week);
        ((SQLQuery) q)
                .addScalar("pos", IntegerType.INSTANCE)
                .addScalar("idLGSS", LongType.INSTANCE)
                .addScalar("subjectName")
                .addScalar("lesson", BooleanType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(Shedule.class));
        List<Shedule> list = (List<Shedule>) getList(q);
        return (list != null && list.size() > 0) ? list : null;
    }

    public List<Attendance> getAttendForGroupByDate(String login, Long idLGSS, Integer pos, Date date) {
        String query = "SELECT all_s.sss_id AS idSSS, all_s.hf_name AS name, all_s.hf_family AS family, \n" +
                "\tall_s.hf_patronymic AS patronymic, atd_s.id_attendance AS idAttendance, CASE WHEN atd_s.attend IS NULL THEN -1 ELSE atd_s.attend END AS attend\n" +
                "FROM (\n" +
                "SELECT sss.id_student_semester_status AS sss_id, hf.name AS hf_name,\n" +
                "\thf.family AS hf_family, hf.patronymic AS hf_patronymic\n" +
                "FROM link_group_semester lgs\n" +
                "INNER JOIN semester sem USING (id_semester)\n" +
                "INNER JOIN student_semester_status sss USING (id_link_group_semester)\n" +
                "INNER JOIN studentcard sc USING (id_studentcard)\n" +
                "INNER JOIN humanface hf USING (id_humanface)\n" +
                "WHERE lgs.id_dic_group = (SELECT sci.id_current_dic_group FROM studentcard sci WHERE sci.ldap_login ilike :login)\n" +
                "\tAND sem.is_current_sem = 1\n" +
                ") AS all_s\n" +
                "LEFT OUTER JOIN (\n" +
                "SELECT sss.id_student_semester_status AS sss_id, \n" +
                "\tatd.id_attendance, atd.attend\n" +
                "FROM link_group_semester lgs   \n" +
                "INNER JOIN student_semester_status sss USING (id_link_group_semester) \n" +
                "INNER JOIN attendance atd USING (id_student_semester_status) \n" +
                "WHERE atd.id_link_group_semester_subject = :idLGSS\n" +
                "\tAND atd.visitdate = :date\n" +
                "\tAND atd.pos = :pos\n" +
                ") AS atd_s\n" +
                "ON all_s.sss_id = atd_s.sss_id";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("login", login);
        q.setLong("idLGSS", idLGSS);
        q.setDate("date", date);
        q.setInteger("pos", pos);
        ((SQLQuery) q)
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("name").addScalar("family").addScalar("patronymic")
                .addScalar("idAttendance", LongType.INSTANCE)
                .addScalar("attend", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(Attendance.class));
        List<Attendance> list = (List<Attendance>) getList(q);
        return (list != null && list.size() > 0) ? list : null;
    }

    public List<Progress> getGroupProgress(Long idLGS) {
        String query = "SELECT \n" +
                "\tSC.ldap_login AS login, LGSS.id_link_group_semester_subject AS idLGSS,\n" +
                "\tDS.subjectname AS subjectName, SR.esogradecurrent AS esogradecurrent, SR.esogrademax AS esogrademax, CAST((SR.esogradecurrent/greatest(SR.esogrademax, 1))*100 AS INTEGER) AS progress \n" +
                "FROM\n" +
                "\thumanface HF\n" +
                "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" +
                "\tINNER JOIN subject S USING (id_subject)\n" +
                "\tINNER JOIN link_group_semester_subject LGSS USING (id_subject)\n" +
                "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "WHERE\n" +
                "\tSSS.id_link_group_semester = " + idLGS;
        Query q = getSession().createSQLQuery(query);
        ((SQLQuery) q)
                .addScalar("login")
                .addScalar("idLGSS", LongType.INSTANCE)
                .addScalar("subjectName")
                .addScalar("esogradecurrent", LongType.INSTANCE)
                .addScalar("esogrademax", LongType.INSTANCE)
                .addScalar("progress", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(Progress.class));
        List<Progress> list = (List<Progress>) getList(q);
        return (list != null && list.size() > 0) ? list : null;
    }

    public List<ProgressDetail> getGroupProgressDetails(Long idLGS) {
        String query = "SELECT \n" +
                "ds.subjectname as subjectName,\n" +
                "lgss.id_link_group_semester_subject AS idLGSS,\n" +
                "MAX(se.performance) AS max,\n" +
                "AVG(se.performance) AS avg,\n" +
                "MIN(se.performance) AS min,\n" +
                "se.date_synch AS dateSync,\n" +
                "extract(week from cast(se.date_synch as date))-extract(week from cast(lgs.dateofbeginsemester as date))+1 AS week\n" +
                "FROM \n" +
                "public.sessionrating_efficiency se\n" +
                "INNER JOIN sessionrating sr ON sr.id_sessionrating = se.id_sessionrating\n" +
                "INNER JOIN subject su ON su.id_subject = sr.id_subject\n" +
                "INNER JOIN dic_subject ds ON ds.id_dic_subject = su.id_dic_subject\n" +
                "INNER JOIN link_group_semester_subject lgss ON lgss.id_subject = su.id_subject\n" +
                "INNER JOIN link_group_semester lgs ON lgs.id_link_group_semester = lgss.id_link_group_semester\n" +
                "INNER JOIN dic_group dg ON dg.id_dic_group = lgs.id_dic_group\n" +
                "WHERE lgs.id_link_group_semester=:idLGS\n" +
                "AND se.date_synch <= lgs.dateofendsession\n" +
                "AND se.date_synch>=lgs.dateofbeginsemester\n" +
                "GROUP BY subjectname, id_link_group_semester_subject, date_synch, lgs.dateofbeginsemester\n" +
                "ORDER BY date_synch, subjectname";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idLGS", idLGS);
        ((SQLQuery) q)
                .addScalar("subjectName")
                .addScalar("idLGSS", LongType.INSTANCE)
                .addScalar("max", LongType.INSTANCE)
                .addScalar("avg", LongType.INSTANCE)
                .addScalar("min", LongType.INSTANCE)
                .addScalar("dateSync", DateType.INSTANCE)
                .addScalar("week", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ProgressDetail.class));
        List<ProgressDetail> list = (List<ProgressDetail>) getList(q);
        return (list != null && list.size() > 0) ? list : null;

    }

    public boolean updateAttendance(AttendanceResultMsg atResult, Attendance student) {
        Transaction tx = getSession().beginTransaction();
        String query = "UPDATE attendance SET " +
                "id_student_semester_status = :idSSS, " +
                "id_link_group_semester_subject = :idLGSS, " +
                "attend=:attend, " +
                "pos = :pos, visitdate = :visitdate, lesson = :lesson " +
                "WHERE id_attendance = :idAttendance";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idSSS", student.getIdSSS());
        q.setLong("idLGSS", atResult.getScheduleSubject().getIdLGSS());
        q.setInteger("attend", student.getAttend());
        q.setInteger("pos", atResult.getScheduleSubject().getPos());
        q.setDate("visitdate", atResult.getDate());
        q.setBoolean("lesson", atResult.getScheduleSubject().getLesson());
        q.setLong("idAttendance", student.getIdAttendance());
        int res = q.executeUpdate();
        tx.commit();
        return (res > 0) ? true : false;
    }

    public boolean insertAttendance(AttendanceResultMsg atResult, Attendance student) {
        Transaction tx = getSession().beginTransaction();
        String query = "INSERT INTO attendance " +
                "(id_student_semester_status, id_link_group_semester_subject, attend, pos, visitdate,lesson) VALUES " +
                "(:idSSS, :idLGSS, :attend, :pos, :visitdate, :lesson)";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idSSS", student.getIdSSS());
        q.setLong("idLGSS", atResult.getScheduleSubject().getIdLGSS());
        q.setInteger("attend", student.getAttend());
        q.setInteger("pos", atResult.getScheduleSubject().getPos());
        q.setDate("visitdate", atResult.getDate());
        q.setBoolean("lesson", atResult.getScheduleSubject().getLesson());
        int res = q.executeUpdate();
        tx.commit();
        return (res > 0) ? true : false;
    }

    public Long getDayAttendanceId(AttendanceResultMsg atResult, Attendance student) {
        String query = "SELECT id_attendance\n" +
                "FROM attendance ata\n" +
                "WHERE " +
                "ata.visitdate = :visitdate " +
                "AND ata.id_student_semester_status = :idSSS " +
                "AND ata.id_link_group_semester_subject = :idLGSS " +
                "AND ata.pos = :pos ORDER BY id_attendance LIMIT 1";
        Query q = getSession().createSQLQuery(query);
        q.setDate("visitdate",atResult.getDate());
        q.setLong("idSSS", student.getIdSSS());
        q.setLong("idLGSS", atResult.getScheduleSubject().getIdLGSS());
        q.setInteger("pos", atResult.getScheduleSubject().getPos());
        return (Long) q.uniqueResult();
    }

    public Long getOtherIdStudentcardByToken(String token) {
        String query = "select sc.other_dbuid\n" +
                "from api_session api\n" +
                "join studentcard sc on sc.ldap_login ilike api.username\n" +
                "where api.token like '" + token + "%'";
        Query q = getSession().createSQLQuery(query);
        return (Long) q.uniqueResult();
    }

    public Long getEokIdSubject(Long idLGSS) {
        String query = "select id_esocourse2\n" +
                "from link_group_semester_subject lgss\n" +
                "where id_link_group_semester_subject = :idLGSS";
        Query q = getSession().createSQLQuery(query).setParameter("idLGSS", idLGSS);
        return (Long) q.uniqueResult();
    }

    public List<Efficiency> getHistoryEfficiency(Long idLGSS, List<Integer> weeks) {
        //String weeksSting = String.join(",", weeks.stream().map(String::valueOf).collect(Collectors.joining(",")));
        String query = "WITH cur_group AS (\n" +
                "    SELECT DISTINCT date_part('year', ilgs.dateofbeginsemester) AS year, id_esocourse2, icu.formofstudy AS formofstudy, isr.is_exam, isr.is_pass\n" +
                "    FROM link_group_semester_subject ilgss\n" +
                "\tINNER JOIN link_group_semester ilgs USING (id_link_group_semester)\n" +
                "\tINNER JOIN dic_group idg USING (id_dic_group)\n" +
                "\tINNER JOIN curriculum icu USING (id_curriculum)\n" +
                "\tINNER JOIN sessionrating isr USING (id_subject)\n" +
                "    WHERE ilgss.id_link_group_semester_subject = :idLGSS\n" +
                ")\n" +
                "SELECT \n" +
                "sss.id_student_semester_status AS idSSS,\n" +
                "sre.performance AS progress,\n" +
                "sre.date_synch,\n" +
                "extract(week from cast(sre.date_synch as date))-extract(week from cast(lgs.dateofbeginsemester as date))+1 AS week,\n" +
                "MAX(srh.retake_count) AS retake,\n" +
                "MAX(srh.newrating) AS rating,\n" +
                "sr.is_pass AS pass,\n" +
                "sr.is_exam AS exam\n" +
                "FROM\n" +
                "dic_group dg\n" +
                "INNER JOIN link_group_semester lgs ON lgs.id_dic_group = dg.id_dic_group\n" +
                "INNER JOIN link_group_semester_subject lgss ON lgss.id_link_group_semester = lgs.id_link_group_semester\n" +
                "INNER JOIN subject su ON su.id_subject = lgss.id_subject\n" +
                "INNER JOIN dic_subject ds ON ds.id_dic_subject = su.id_dic_subject\n" +
                "INNER JOIN sessionrating sr ON sr.id_subject = su.id_subject\n" +
                "INNER JOIN student_semester_status sss ON sss.id_student_semester_status = sr.id_student_semester_status\n" +
                "INNER JOIN studentcard sc ON sc.id_studentcard = sss.id_studentcard\n" +
                "INNER JOIN humanface hf ON hf.id_humanface = sc.id_humanface\n" +
                "INNER JOIN sessionrating_efficiency sre ON sre.id_sessionrating = sr.id_sessionrating\n" +
                "INNER JOIN curriculum cu ON cu.id_curriculum = dg.id_curriculum\n" +
                "INNER JOIN sessionratinghistory srh ON srh.id_sessionrating = sr.id_sessionrating\n" +
                "WHERE\n" +
                "lgss.id_esocourse2 = (SELECT id_esocourse2 FROM cur_group)\n" +
                "AND\n" +
                "date_part('year', sre.date_synch) = (SELECT cur_group.year-1 FROM cur_group)\n" +
                "AND\n" +
                "cu.formofstudy = (SELECT formofstudy FROM cur_group)\n" +
                "AND\n" +
                "sr.is_pass = (SELECT is_pass FROM cur_group)\n" +
                "AND\n" +
                "sr.is_exam = (SELECT is_exam FROM cur_group)\n" +
                "AND\n" +
                "(extract(week from cast(sre.date_synch as date))-extract(week from cast(lgs.dateofbeginsemester as date))+1) IN (:weeks)\n" +
                "GROUP BY idSSS, progress, date_synch, lgs.dateofbeginsemester, sr.is_pass, sr.is_exam\n" +
                "ORDER BY week, idSSS";
        Query q = getSession().createSQLQuery(query);
        ((SQLQuery) q)
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("progress", LongType.INSTANCE)
                .addScalar("week", IntegerType.INSTANCE)
                .addScalar("retake", IntegerType.INSTANCE)
                .addScalar("rating", IntegerType.INSTANCE)
                .addScalar("pass", BooleanType.INSTANCE)
                .addScalar("exam", BooleanType.INSTANCE)
                .setParameter("idLGSS", idLGSS)
                .setParameterList("weeks", weeks)
                .setResultTransformer(Transformers.aliasToBean(Efficiency.class));
        List<Efficiency> list = (List<Efficiency>) getList(q);
        return (list != null && list.size() > 0) ? list : null;
    }

    public List<Efficiency> getCurrentEfficiency(String login, Long idLGSS, List<Integer> weeks) {
        //String weeksSting = String.join(",", weeks.stream().map(String::valueOf).collect(Collectors.joining(",")));
        String query = "SELECT \n" +
                "sss.id_student_semester_status AS idSSS,\n" +
                "sre.performance AS progress,\n" +
                "sre.date_synch,\n" +
                "extract(week from cast(sre.date_synch as date))-extract(week from cast(lgs.dateofbeginsemester as date))+1 AS week,\n" +
                "sr.is_pass AS pass,\n" +
                "sr.is_exam AS exam\n" +
                "FROM\n" +
                "dic_group dg\n" +
                "INNER JOIN link_group_semester lgs ON lgs.id_dic_group = dg.id_dic_group\n" +
                "INNER JOIN link_group_semester_subject lgss ON lgss.id_link_group_semester = lgs.id_link_group_semester\n" +
                "INNER JOIN subject su ON su.id_subject = lgss.id_subject\n" +
                "INNER JOIN dic_subject ds ON ds.id_dic_subject = su.id_dic_subject\n" +
                "INNER JOIN sessionrating sr ON sr.id_subject = su.id_subject\n" +
                "INNER JOIN student_semester_status sss ON sss.id_student_semester_status = sr.id_student_semester_status\n" +
                "INNER JOIN studentcard sc ON sc.id_studentcard = sss.id_studentcard\n" +
                "INNER JOIN humanface hf ON hf.id_humanface = sc.id_humanface\n" +
                "INNER JOIN sessionrating_efficiency sre ON sre.id_sessionrating = sr.id_sessionrating\n" +
                "INNER JOIN curriculum cu ON cu.id_curriculum = dg.id_curriculum\n" +
                "LEFT JOIN sessionratinghistory srh ON srh.id_sessionrating = sr.id_sessionrating\n" +
                "WHERE\n" +
                "sc.ldap_login = :login AND lgss.id_link_group_semester_subject = :idLGSS\n" +
                "AND\n" +
                "(extract(week from cast(sre.date_synch as date))-extract(week from cast(lgs.dateofbeginsemester as date))+1) IN (:weeks)\n" +
                "ORDER BY week";
        Query q = getSession().createSQLQuery(query);
        ((SQLQuery) q)
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("progress", LongType.INSTANCE)
                .addScalar("week", IntegerType.INSTANCE)
                .addScalar("pass", BooleanType.INSTANCE)
                .addScalar("exam", BooleanType.INSTANCE)
                .setParameter("login", login)
                .setParameter("idLGSS", idLGSS)
                .setParameterList("weeks", weeks)
                .setResultTransformer(Transformers.aliasToBean(Efficiency.class));
        List<Efficiency> list = (List<Efficiency>) getList(q);
        return (list != null && list.size() > 0) ? list : null;
    }

}