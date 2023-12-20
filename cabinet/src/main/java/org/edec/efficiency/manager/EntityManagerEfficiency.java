package org.edec.efficiency.manager;

import org.edec.dao.DAO;
import org.edec.efficiency.model.ConfigurationEfficiency;
import org.edec.efficiency.model.ProblemGroup;
import org.edec.efficiency.model.ProblemStudent;
import org.edec.efficiency.model.ProblemSubjectGroup;
import org.edec.efficiency.model.dao.GroupStudentModel;
import org.edec.model.EmployeeModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;


public class EntityManagerEfficiency extends DAO {
    public Long getSemByInsyAndFormOfStudy(Long idInst, Integer formOfStudy) {
        String query = "SELECT id_semester FROM semester WHERE is_current_sem = 1 AND id_institute = :idInst AND formofstudy = :formofstudy";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id_semester", LongType.INSTANCE)
                .setLong("idInst", idInst)
                .setInteger("formofstudy", formOfStudy);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long getEmployeeByHum(Long idHum) {
        String query = "SELECT id_employee FROM employee WHERE id_humanface = :idHum";
        Query q = getSession().createSQLQuery(query).addScalar("id_employee", LongType.INSTANCE).setLong("idHum", idHum);
        return ((List<Long>) getList(q)).get(0);
    }

    public ConfigurationEfficiency getConfiguration(Long idSem) {
        String query = "SELECT\n" + "\tid_configuration_efficiency AS idConfigurationEfficiency, id_semester AS idSem,\n" +
                "\tis_attendance AS attendance, is_eok AS eok, is_performance AS performance,\n" +
                "\tis_master AS master, is_physcul AS physcul, max_red_level AS maxRedLevel, min_green_level AS minGreenLevel\n" +
                "FROM configuration_efficiency\n" + "WHERE id_semester = :idSem";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idConfigurationEfficiency", LongType.INSTANCE)
                .addScalar("idSem", LongType.INSTANCE)
                .addScalar("attendance")
                .addScalar("eok")
                .addScalar("performance")
                .addScalar("master")
                .addScalar("physcul")
                .addScalar("maxRedLevel")
                .addScalar("minGreenLevel")
                .setResultTransformer(Transformers.aliasToBean(ConfigurationEfficiency.class));
        q.setLong("idSem", idSem);
        List list = getList(q);
        return list.size() == 0 ? null : (ConfigurationEfficiency) list.get(0);
    }

    public Long createNewConfiguration(Long idSem) {
        Query q = getSession().createSQLQuery(
                "INSERT INTO configuration_efficiency (id_semester) VALUES (:idSem) RETURNING id_configuration_efficiency")
                .addScalar("id_configuration_efficiency", LongType.INSTANCE)
                .setLong("idSem", idSem);
        return ((List<Long>) getList(q)).get(0);
    }

    public List<ProblemGroup> getProblemGroupsConfiguration(Long idSem, String qualification) {
        String query = "SELECT\n" + "\tDG.groupname, LGS.course, LGS.id_link_group_semester AS idLGS, LGS.is_efficiency AS isEfficiency\n" +
                "FROM\n" + "\tdic_group DG\n" + "\tINNER JOIN curriculum CUR USING (id_curriculum)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_dic_group)\n" + "WHERE\n" +
                "\tLGS.id_semester = :idSem AND CUR.qualification IN (" + qualification + ")\n" + "ORDER BY\n" +
                "\tLGS.course, DG.groupname";
        Query q = getSession().createSQLQuery(query)
                .addScalar("groupname")
                .addScalar("course")
                .addScalar("idLGS", LongType.INSTANCE)
                .addScalar("isEfficiency")
                .setResultTransformer(Transformers.aliasToBean(ProblemGroup.class));
        q.setLong("idSem", idSem);
        return (List<ProblemGroup>) getList(q);
    }

    public List<ProblemSubjectGroup> getProblemSubjectGroups(Long idLGS) {
        String query = "SELECT\n" + "\tDS.subjectname, LGSS.id_link_group_semester_subject AS idLGSS,\n" +
                "\tLGSS.is_efficiency_performance AS performance, LGSS.is_efficiency_attendance AS attendance, LGSS.is_efficiency_eok AS eok,\n" +
                "\tS.is_exam = 1 AS exam, S.is_pass = 1 AS pass, S.is_courseproject = 1 AS cp, S.is_coursework = 1 AS cw, S.is_practic = 1 AS practic,\n" +
                "\tLGSS.id_esocourse2 AS idEok, STRING_AGG(HF.family||' '||HF.name||' '||HF.patronymic, ',') AS teacher\n" +
                "FROM\n" + "\tlink_group_semester_subject LGSS\n" +
                "\tLEFT JOIN link_employee_subject_group LESG USING (id_link_group_semester_subject)\n" +
                "\tLEFT JOIN employee EMP USING (id_employee)\n" + "\tLEFT JOIN humanface HF USING (id_humanface)\n" +
                "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" + "WHERE\n" +
                "\tLGSS.id_link_group_semester = :idLGS\n" + "GROUP BY\n" +
                "\tsubjectname, idLGSS, performance, attendance, eok, exam, pass, cp, cw, practic\n" + "ORDER BY\n" +
                "\texam DESC, pass DESC, DS.subjectname";
        Query q = getSession().createSQLQuery(query)
                .addScalar("subjectname")
                .addScalar("idLGSS", LongType.INSTANCE)
                .addScalar("performance").addScalar("attendance").addScalar("eok")
                .addScalar("idEok", LongType.INSTANCE).addScalar("teacher")
                .addScalar("exam").addScalar("pass").addScalar("cp").addScalar("cw").addScalar("practic")
                .setResultTransformer(Transformers.aliasToBean(ProblemSubjectGroup.class));
        q.setLong("idLGS", idLGS);
        return (List<ProblemSubjectGroup>) getList(q);
    }

    public List<ProblemStudent> getProblemStudent(Long idLGS, Long idEmp) {
        String query =
                "SELECT HF.family, HF.name, HF.patronymic, SSS.id_student_semester_status AS idSSS, EFF.id_efficiency_student AS idEfficiencyStudent,\n" +
                        "\tEFF.id_employee AS idEmp, EFF.status_efficiency AS statusEfficiency,\n" +
                        "\tEFF.attend, EFF.eok_activity AS eokActivity, EFF.performance,\n" +
                        "\tEFF.date_created AS dateCreated, EFF.date_confirm AS dateConfirm, EFF.date_completeted AS dateCompleted,\n" +
                        "\tEFF.comment, SSS.is_group_leader = 1 AS groupLeader\n" + "FROM efficiency_student EFF\n" +
                        "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                        "\tINNER JOIN studentcard SC USING (id_studentcard)\n" + "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                        "WHERE EFF.status_efficiency IN (0, 1) AND SSS.id_link_group_semester = :idLGS\n" +
                        "\tAND COALESCE(CAST(EFF.id_employee AS TEXT), '') ILIKE '" + (idEmp == null ? "%%" : String.valueOf(idEmp)) + "'\n" +
                        "ORDER BY family, name";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family")
                .addScalar("name")
                .addScalar("patronymic")
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("idEfficiencyStudent", LongType.INSTANCE)
                .addScalar("statusEfficiency")
                .addScalar("dateCreated")
                .addScalar("dateConfirm")
                .addScalar("dateCompleted")
                .addScalar("attend")
                .addScalar("eokActivity")
                .addScalar("performance")
                .addScalar("idEmp", LongType.INSTANCE)
                .addScalar("comment")
                .addScalar("groupLeader")
                .setResultTransformer(Transformers.aliasToBean(ProblemStudent.class));
        q.setLong("idLGS", idLGS);
        return (List<ProblemStudent>) getList(q);
    }

    public List<GroupStudentModel> getGroupStudentModel(Long idSem, Long idEmp, Integer course, String groupname, String student,
                                                        String statusEfficiency, boolean assigned) {
        String query =
                "SELECT HF.family, HF.name, HF.patronymic, SSS.id_student_semester_status AS idSSS, EFF.id_efficiency_student AS idEfficiencyStudent, EFF.comment,\n" +
                        "\tDG.groupname, LGS.id_link_group_semester AS idLGS, EFF.id_employee AS idEmp, EFF.status_efficiency AS statusEfficiency,\n" +
                        "\tEFF.attend, EFF.eok_activity AS eokActivity, EFF.performance,\n" +
                        "\tEFF.date_created AS dateCreated, EFF.date_confirm AS dateConfirm, EFF.date_completeted AS dateCompleted,\n" +
                        "\tSSS.is_group_leader = 1 AS groupLeader\n" + "FROM efficiency_student EFF\n" +
                        "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                        "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                        "\tINNER JOIN dic_group DG USING (id_dic_group)\n" + "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                        "\tINNER JOIN humanface HF USING (id_humanface)\n" + "WHERE EFF.status_efficiency IN (" + statusEfficiency +
                        ") AND LGS.id_semester = :idSem AND COALESCE(CAST(EFF.id_employee AS TEXT), '') ILIKE '" +
                        (idEmp == null ? "%%" : String.valueOf(idEmp)) + "'\n" +
                        "\tAND HF.family||' '||HF.name||' '||HF.patronymic ILIKE :student AND DG.groupname ILIKE :groupname AND CAST(LGS.course AS TEXT) ILIKE '" +
                        (course == 0 ? "%%" : course) + "'\n" + (assigned ? "\tAND EFF.id_employee IS NOT NULL\n" : "") +
                        "ORDER BY LGS.course, DG.groupname, family, name";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family")
                .addScalar("name")
                .addScalar("patronymic")
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("idEfficiencyStudent", LongType.INSTANCE)
                .addScalar("groupname")
                .addScalar("idLGS", LongType.INSTANCE)
                .addScalar("statusEfficiency")
                .addScalar("dateCreated")
                .addScalar("dateConfirm")
                .addScalar("dateCompleted")
                .addScalar("attend")
                .addScalar("eokActivity")
                .addScalar("performance")
                .addScalar("idEmp", LongType.INSTANCE)
                .addScalar("comment")
                .addScalar("groupLeader")
                .setResultTransformer(Transformers.aliasToBean(GroupStudentModel.class));
        q.setLong("idSem", idSem)
                .setParameter("student", student.equals("") ? "%%" : student)
                .setParameter("groupname", groupname.equals("") ? "%%" : groupname);
        return (List<GroupStudentModel>) getList(q);
    }

    public List<EmployeeModel> getEmployeeByDep(Long idDdepartment) {
        String query = "SELECT\tHF.family, HF.name, HF.patronymic, HF.id_humanface AS idHum,\n" + "\tEMP.id_employee AS idEmp\n" +
                "FROM\thumanface HF\n" + "\tINNER JOIN employee EMP USING (id_humanface)\n" +
                "\tINNER JOIN link_employee_department LED USING (id_employee)\n" + "WHERE\tLED.id_department = :idDep\n" +
                "ORDER BY family, name\n";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family")
                .addScalar("name")
                .addScalar("patronymic")
                .addScalar("idHum", LongType.INSTANCE)
                .addScalar("idEmp", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(EmployeeModel.class));
        q.setLong("idDep", idDdepartment);
        return (List<EmployeeModel>) getList(q);
    }

    public boolean updateEfficiencyGroup(Long idLGS, Boolean isEfficiency) {
        Query q = getSession().createSQLQuery(
                "UPDATE link_group_semester SET is_efficiency = :isEfficiency WHERE id_link_group_semester = :idLGS");
        q.setBoolean("isEfficiency", isEfficiency).setLong("idLGS", idLGS);
        return executeUpdate(q);
    }

    public boolean updateEfficiencySubject(Long idLGSS, Boolean attendance, Boolean eok, Boolean performance) {
        Query q = getSession().createSQLQuery(
                "UPDATE  link_group_semester_subject SET is_efficiency_attendance = :attendance, is_efficiency_eok = :eok, " +
                        "is_efficiency_performance = :performance WHERE id_link_group_semester_subject = :idLGSS");
        q.setBoolean("attendance", attendance).setBoolean("eok", eok).setBoolean("performance", performance).setLong("idLGSS", idLGSS);
        return executeUpdate(q);
    }

    public boolean updateConfigurationEfficiency(ConfigurationEfficiency configuration) {
        String query = "UPDATE configuration_efficiency SET is_attendance = :attendance, is_eok = :eok, is_performance = :performance, " +
                "is_master = :master, is_physcul = :physcul, max_red_level = :maxredlevel, min_green_level = :mingreenlevel WHERE id_configuration_efficiency = :idConfiguration";
        Query q = getSession().createSQLQuery(query)
                .setBoolean("attendance", configuration.getAttendance())
                .setBoolean("eok", configuration.getEok())
                .setBoolean("performance", configuration.getPerformance())
                .setBoolean("master", configuration.getMaster())
                .setBoolean("physcul", configuration.getPhyscul())
                .setInteger("maxredlevel", configuration.getMaxRedLevel())
                .setInteger("mingreenlevel", configuration.getMinGreenLevel())
                .setLong("idConfiguration", configuration.getIdConfigurationEfficiency());
        return executeUpdate(q);
    }

    public boolean updateEmployeeForGroup(Long idEmployee, Long idLGS) {
        String query = "UPDATE efficiency_student SET id_employee = :idEmp WHERE id_efficiency_student IN\n" +
                "(SELECT\tEFFST.id_efficiency_student\n" + "FROM\tstudent_semester_status SSS\n" +
                "\tINNER JOIN efficiency_student EFFST USING (id_student_semester_status)\n" +
                "WHERE\tEFFST.status_efficiency = 0 AND SSS.id_link_group_semester = :idLGS)";
        Query q = getSession().createSQLQuery(query).setLong("idEmp", idEmployee).setLong("idLGS", idLGS);
        return executeUpdate(q);
    }

    public boolean updateEmployeeForStudent(Long idEmp, Long idEfficiencyStudent) {
        String query = "UPDATE efficiency_student SET id_employee = :idEmp WHERE id_efficiency_student = :idEffSt";
        Query q = getSession().createSQLQuery(query).setLong("idEmp", idEmp).setLong("idEffSt", idEfficiencyStudent);
        return executeUpdate(q);
    }

    public boolean confirmGroupForManage(Long idEmp, Long idLGS) {
        String query =
                "UPDATE efficiency_student SET status_efficiency = 1, date_confirm = now() WHERE status_efficiency = 0 AND id_employee = :idEmp\n" +
                        "\tAND id_student_semester_status IN (SELECT id_student_semester_status FROM student_semester_status WHERE id_link_group_semester = :idLGS)";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idEmp", idEmp).setLong("idLGS", idLGS);
        return executeUpdate(q);
    }

    public boolean confirmStudentForManage(Long idEmp, Long idSSS) {
        String query = "UPDATE efficiency_student SET status_efficiency = 1, date_confirm = now() \n" +
                "\tWHERE status_efficiency = 0 AND id_employee = :idEmp AND id_student_semester_status = :idSSS";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idSSS", idSSS).setLong("idEmp", idEmp);
        return executeUpdate(q);
    }

    public boolean completeStudentForManage(Long idEmp, Long idSSS) {
        String query = "UPDATE efficiency_student SET status_efficiency = 2, date_completeted = now() \n" +
                "\tWHERE status_efficiency = 1 AND id_employee = :idEmp AND id_student_semester_status = :idSSS";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idEmp", idEmp).setLong("idSSS", idSSS);
        return executeUpdate(q);
    }

    public boolean createEfficiencyStudentBySem(Long idSem) {
        String query = "INSERT INTO efficiency_student (id_student_semester_status, eok_activity, performance, attend)\n" +
                "SELECT\tidSSS, eokActivity, grade, attend\n" + "FROM (SELECT\tidSSS, eokActivity, grade, attend, max_red_level,\n" +
                "\t(CASE WHEN eokActivity <> -1 THEN eokActivity ELSE 0 END + CASE WHEN grade <> -1 THEN grade ELSE 0 END + CASE WHEN attend <> -1 THEN attend ELSE 0 END) AS maxResult,\n" +
                "\t(CASE WHEN eokActivity <> -1 THEN 1 ELSE 0 END + CASE WHEN grade <> -1 THEN 1 ELSE 0 END + CASE WHEN attend <> -1 THEN 1 ELSE 0 END) AS count\n" +
                "FROM (SELECT\tSSS.id_student_semester_status AS idSSS, CONF.max_red_level,\n" +
                "    CASE WHEN is_eok = TRUE THEN -1 ELSE -1 END AS eokActivity,\n" +
                "    CASE WHEN is_performance = TRUE THEN CAST((SUM(SR.esogradecurrent)*100)/GREATEST(SUM(SR.esogrademax), 1) AS REAL) ELSE -1 END AS grade,\n" +
                "    CASE WHEN is_attendance = TRUE THEN CAST((SUM(SR.visit_count)*100)/GREATEST(SUM(SR.lesson_count),1) AS REAL) ELSE -1 END AS attend\n" +
                "FROM\thumanface HF\n" + "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tINNER JOIN link_group_semester LGS ON SSS.id_link_group_semester = LGS.id_link_group_semester\n" +
                "\tINNER JOIN semester SEM USING (id_semester)\n" +
                "\tINNER JOIN configuration_efficiency CONF USING (id_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN curriculum CUR ON DG.id_curriculum = CUR.id_curriculum\n" +
                "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" +
                "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "\tINNER JOIN link_group_semester_subject LGSS ON LGSS.id_subject = S.id_subject AND LGSS.id_link_group_semester = LGS.id_link_group_semester\n" +
                "\tLEFT JOIN efficiency_student ES ON SSS.id_student_semester_status = ES.id_student_semester_status AND ES.status_efficiency IN (0,1,2)\n" +
                "WHERE\tLGS.id_semester = :idSem\n" +
                "\tAND (CONF.is_master = TRUE OR (CONF.is_master = FALSE AND CUR.qualification IN (1,2)))\n" +
                "\tAND (CONF.is_physcul = TRUE OR (CONF.is_physcul = FALSE AND DS.subjectname NOT IN (\n" +
                "                'Физическая культура', \n" +
                "                'Элективные курсы по физической культуре', \n" +
                "                'Прикладная физическая культура',\n" +
                "                'Прикладная физическая культура (элективная)',\n" +
                "                'Прикладная физическая культура (элективный)',\n" +
                "                'Физическая культура/Прикладная физическая культура',\n" +
                "                'Физическая культура/Элективные курсы по физической культуре',\n" +
                "                'Элективные курсы по физической культуре и спорту',\n" +
                "                'Физическая культура')))\n" +
                "\tAND LGS.is_efficiency = TRUE AND LGSS.is_efficiency_attendance = TRUE AND LGSS.is_efficiency_performance = TRUE\n" +
                "\tAND SSS.is_deducted = 0 AND SSS.is_academicleave = 0\n" + "\tAND ES.id_efficiency_student IS NULL\n" +
                "GROUP BY\tSSS.id_student_semester_status, CONF.id_configuration_efficiency) myTab) AS myTab\n" +
                "WHERE (maxResult/count) <= max_red_level";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idSem", idSem);
        return executeUpdate(q);
    }

    public boolean deleteAllEfficiencyStudentWithStatusCreate() {
        return executeUpdate(getSession().createSQLQuery("DELETE FROM efficiency_student WHERE status_efficiency = 0"));
    }

    public boolean updateEfficiencyStudent(ProblemStudent student) {
        String query = "UPDATE efficiency_student SET comment = :comment WHERE id_efficiency_student = :idStudent";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idStudent", student.getIdEfficiencyStudent()).setString("comment", student.getComment());
        return executeUpdate(q);
    }
}