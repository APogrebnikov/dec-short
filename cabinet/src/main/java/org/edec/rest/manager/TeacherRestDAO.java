package org.edec.rest.manager;

import org.edec.dao.DAO;
import org.edec.rest.model.student.response.Teacher;
import org.edec.rest.model.student.response.TeacherSemester;
import org.edec.rest.model.student.response.TeacherSubject;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;

import java.util.List;


public class TeacherRestDAO extends DAO {

    /**
     * Получение базовой информации по преподавателю через ldap login
     * @param login - логин преподавателя
     * @return
     */
    public Teacher getTeacherInfoByLogin (String login) {
        String query = "SELECT\n" +
                "\tem.id_employee as idEmp,\n" +
                "\tem.id_humanface as idHum,\n" +
                "\thf.family as surname,\n" +
                "\thf.name AS name,\n" +
                "\thf.patronymic AS patronymic,\n" +
                "\thf.email AS email,\n" +
                "\tde.fulltitle AS department,\n" +
                "\ter.rolename AS rolename\n" +
                "FROM employee em\n" +
                "INNER JOIN link_employee_department led USING (id_employee)\n" +
                "INNER JOIN employee_role er ON er.id_employee_role = led.id_employee_role\n" +
                "INNER JOIN department de USING (id_department)\n" +
                "INNER JOIN humanface hf USING (id_humanface)\n" +
                "WHERE \n" +
                "em.other_ad ilike :login \n" +
                "AND \n" +
                "led.is_permanency = 1";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("login", login);
        ((SQLQuery) q)
                .addScalar("idEmp", LongType.INSTANCE)
                .addScalar("idHum", LongType.INSTANCE)
                .addScalar("surname")
                .addScalar("name")
                .addScalar("patronymic")
                .addScalar("email")
                .addScalar("department")
                .addScalar("rolename")
                .setResultTransformer(Transformers.aliasToBean(Teacher.class));
        List<Teacher> list = (List<Teacher>) getList(q);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    /**
     * Получение списка дисцпилин преподавателя за конкретный семестр
     * @param login - логин преподавателя
     * @param idSem - id семестра
     * @return
     */
    public List<TeacherSubject> getTeacherSubjectsBySemester (String login, Long idSem) {

        String query = "SELECT * FROM (SELECT\n" +
                "LGSS.id_link_group_semester_subject AS idLGSS,\n" +
                "SU.id_subject AS idSub,\n" +
                "DG.groupname,\n" +
                "DS.subjectname,\n" +
                "SU.is_exam AS isExam,\n" +
                "SU.is_pass AS isPass,\n" +
                "SU.is_courseproject AS isCP,\n" +
                "SU.is_coursework AS isCW,\n" +
                "SU.is_practic AS isPractic,\n" +
                "RE.certnumber AS certNumber,\n" +
                "RE.signdate AS signDate,\n" +
                "RE.id_register AS idRegister,\n" +
                "CASE    \n" +
                "   WHEN SRH.is_exam=1 THEN LGSS.examdate\n" +
                "   WHEN SRH.is_pass=1 THEN LGSS.passdate\n" +
                "   WHEN SRH.is_courseproject=1 THEN LGSS.tmpcourseprojectdate\n" +
                "   WHEN SRH.is_coursework=1 THEN LGSS.tmpcourseworkdate\n" +
                "   WHEN SRH.is_practic=1 THEN LGSS.practicdate\n" +
                "   ELSE null\n" +
                "END AS completionDate,\n" +
                "CASE    WHEN SRH.is_exam=1 THEN 1\n" +
                "   WHEN SRH.is_pass=1 THEN 2\n" +
                "   WHEN SRH.is_courseproject=1 THEN 3\n" +
                "   WHEN SRH.is_coursework=1 THEN 4\n" +
                "   WHEN SRH.is_practic=1 THEN 5\n" +
                "   ELSE null\n" +
                "END AS foc,SU.type AS passType,\n" +
                "CASE\n" +
                "   WHEN SE.season = 0 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' осенний'\n" +
                "   WHEN SE.season = 1 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' весенний'\n" +
                "END AS semesterStr\n" +
                "FROM employee EM\n" +
                "INNER JOIN link_employee_subject_group LESG USING (id_employee)\n" +
                "INNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester_subject)\n" +
                "INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "INNER JOIN student_semester_status SSS ON SSS.id_link_group_semester = LGS.id_link_group_semester\n" +
                "INNER JOIN subject SU USING (id_subject)\n" +
                "INNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "INNER JOIN dic_group DG USING (id_dic_group)\n" +
                "INNER JOIN semester SE USING (id_semester)\n" +
                "INNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                "INNER JOIN sessionrating SR ON SR.id_subject = SU.id_subject\n" +
                "LEFT JOIN sessionratinghistory SRH ON SRH.id_sessionrating = SR.id_sessionrating\n" +
                "LEFT JOIN register RE ON RE.id_register = SRH.id_register\n" +
                "WHERE \n" +
                "EM.other_ad ilike :login \n" +
                "AND SE.id_semester = :idSem \n" +
                "AND ((SRH.retake_count=1 OR SRH.retake_count=-1) OR SRH.id_sessionratinghistory is null)\n" +
                "AND SSS.is_deducted = 0 \n" +
                "AND SSS.is_academicleave = 0 \n" +
                "ORDER BY SRH.status DESC, SSS.is_academicleave DESC, SSS.is_deducted DESC, SRH.id_sessionratinghistory DESC) AS list\n" +
                "GROUP BY \n" +
                "idLGSS,\n" +
                "idSub,\n" +
                "groupname,\n" +
                "subjectname,\n" +
                "isExam,\n" +
                "isPass,\n" +
                "isCP,\n" +
                "isCW,\n" +
                "isPractic,\n" +
                "certnumber,\n" +
                "signDate,\n" +
                "idRegister,\n" +
                "completionDate,\n" +
                "foc,\n" +
                "passType,\n" +
                "semesterStr\n" +
                "HAVING\n" +
                "foc is not NULL\n" +
                "ORDER BY idLGSS";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("login", login);
        q.setLong("idSem", idSem);
        ((SQLQuery) q)
                .addScalar("idLGSS", LongType.INSTANCE)
                .addScalar("idSub", LongType.INSTANCE)
                .addScalar("groupname")
                .addScalar("subjectname")
                .addScalar("isExam", BooleanType.INSTANCE)
                .addScalar("isPass", BooleanType.INSTANCE)
                .addScalar("isCP", BooleanType.INSTANCE)
                .addScalar("isCW", BooleanType.INSTANCE)
                .addScalar("isPractic", BooleanType.INSTANCE)
                .addScalar("certNumber")
                .addScalar("signDate", DateType.INSTANCE)
                .addScalar("idRegister", LongType.INSTANCE)
                .addScalar("completionDate", DateType.INSTANCE)
                .addScalar("foc", IntegerType.INSTANCE)
                .addScalar("passType", IntegerType.INSTANCE)
                .addScalar("semesterStr")
                .setResultTransformer(Transformers.aliasToBean(TeacherSubject.class));
        List<TeacherSubject> list = (List<TeacherSubject>) getList(q);
        return (list != null && list.size() > 0) ? list : null;
    }

    /**
     * Получение списка семестров для преподавателя
     * @param login - логин преподавателя
     * @return
     */
    //TODO: Добавить обработку Очное/Заочное
    public List<TeacherSemester> getTeacherSemesters (String login) {
        String query = "SELECT\n" +
                "DISTINCT\n" +
                "SE.id_semester AS idSemester,\n" +
                "CASE\n" +
                "WHEN SE.season = 0 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' осенний'\n" +
                "WHEN SE.season = 1 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' весенний'\n" +
                "END AS semesterName,\n" +
                "se.id_institute AS idInstitute,\n" +
                "ins.fulltitle AS instituteName,\n" +
                "se.formofstudy AS formOfStudy\n" +
                "FROM employee EM\n" +
                "INNER JOIN link_employee_subject_group LESG USING (id_employee)\n" +
                "INNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester_subject)\n" +
                "INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "INNER JOIN dic_group DG USING (id_dic_group)\n" +
                "INNER JOIN semester SE USING (id_semester)\n" +
                "INNER JOIN institute INS ON INS.id_institute=SE.id_institute\n" +
                "INNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                "WHERE em.other_ad ilike :login";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("login", login);
        ((SQLQuery) q)
                .addScalar("idSemester", LongType.INSTANCE)
                .addScalar("semesterName")
                .addScalar("idInstitute", LongType.INSTANCE)
                .addScalar("instituteName")
                .addScalar("formOfStudy", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(TeacherSemester.class));
        List<TeacherSemester> list = (List<TeacherSemester>) getList(q);
        return (list != null && list.size() > 0) ? list : null;
    }


}