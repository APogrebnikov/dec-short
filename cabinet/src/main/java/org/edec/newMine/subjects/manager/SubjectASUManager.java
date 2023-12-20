package org.edec.newMine.subjects.manager;

import org.edec.dao.DAO;
import org.edec.main.model.DepartmentModel;
import org.edec.newMine.subjects.model.GroupsEsoModel;
import org.edec.newMine.subjects.model.SubjectsModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.ArrayList;
import java.util.List;

public class SubjectASUManager  extends DAO {

    public List<SubjectsModel> getSubjectsByLGS (Long idLGS) {
        String query =
                "SELECT\tDS.subjectname, S.hourscount AS hoursCount, S.hoursaudcount AS hoursAudCount, S.id_chair AS idChair, S.otherdbid AS idSubjMine, S.id_subject AS idSubj,\n" +
                        "\tS.is_exam = 1 AS exam, S.is_pass = 1 AS pass, S.is_courseproject = 1 AS cp, S.is_coursework = 1 AS cw, S.is_practic = 1 AS practic,\n" +
                        "\tS.type, S.is_facultative AS facultative, LGSS.id_link_group_semester_subject AS idLGSS,\n" +
                        "\tS.subjectcode, S.practic_type AS practiceType, S.practic_type_dative AS practiceTypeDative, LGS.dateofbeginpractic AS datePracticeBegin, LGS.dateofendpractic AS datePracticeEnd\n" +
                        "FROM\tlink_group_semester_subject LGSS\n" +
                        "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                        "\tINNER JOIN subject S USING (id_subject)\n" +
                        "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                        "WHERE\tLGSS.id_link_group_semester = :idLGS\n" +
                        "ORDER BY subjectname";
        Query q = getSession().createSQLQuery(query)
                .addScalar("subjectname").addScalar("subjectcode")
                .addScalar("hoursCount")
                .addScalar("hoursAudCount")
                .addScalar("idChair", LongType.INSTANCE).addScalar("idSubjMine", LongType.INSTANCE).addScalar("idLGSS", LongType.INSTANCE)
                .addScalar("exam").addScalar("pass").addScalar("cp").addScalar("cw").addScalar("practic")
                .addScalar("idSubj", LongType.INSTANCE)
                .addScalar("type").addScalar("facultative")
                .addScalar("practiceType").addScalar("practiceTypeDative").addScalar("datePracticeBegin").addScalar("datePracticeEnd")
                .setResultTransformer(Transformers.aliasToBean(SubjectsModel.class));
        q.setLong("idLGS", idLGS);
        return (List<SubjectsModel>) getList(q);
    }

    public List<GroupsEsoModel> getGroupsBySemester (Long idSem) {
        String query = "SELECT DG.groupname AS groupname, LGS.id_link_group_semester AS idLGS, LGS.semesternumber AS semester, cur.id_chair as idChair, \n" +
                "\tLGS.course AS course, CAST(LGS.otherdbid AS Integer) AS idGroupMine, cur.id_curriculum as idCurriculum, \n" +
                "\tdep.otherdbid as idChairMineCabinet, cur.directioncode as directionCode, dep.fulltitle as chairName\n" +
                "FROM dic_group DG \n" +
                "\tINNER JOIN link_group_semester LGS USING (id_dic_group)\n" +
                "\tjoin curriculum cur using (id_curriculum)\n" +
                "\tjoin department dep on cur.id_chair = dep.id_chair\n" +
                "WHERE LGS.id_semester = "+idSem+" and dep.otherdbid is not null and dep.is_main = true \n" +
                "ORDER BY LGS.course, groupname";
        Query q = getSession().createSQLQuery(query)
                .addScalar("groupname")
                .addScalar("idLGS", LongType.INSTANCE)
                .addScalar("semester")
                .addScalar("idChair", LongType.INSTANCE)
                .addScalar("course")
                .addScalar("idGroupMine")
                .addScalar("idCurriculum", LongType.INSTANCE)
                .addScalar("idChairMineCabinet", LongType.INSTANCE)
                .addScalar("directionCode")
                .addScalar("chairName")
                .setResultTransformer(Transformers.aliasToBean(GroupsEsoModel.class));
        return q.list();
    }

    public List<String> getRegisterNumberByLGSandSubj(Long idLGS, Long idSubj) {
        String query = "SELECT\tDISTINCT R.register_number\n" + "FROM sessionrating SR\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN sessionratinghistory SRH USING (id_sessionrating)\n" +
                "\tINNER JOIN register  R USING (id_register)\n" +
                "WHERE\tR.certnumber IS NOT NULL AND R.register_number IS NOT NULL " +
                "AND SSS.id_link_group_semester = :idLGS AND SR.id_subject = :idSubj\t";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idLGS", idLGS).setLong("idSubj", idSubj);
        return (List<String>) getList(q);
    }

    public Long getEmpByFIO(String fio) {
        String query = "SELECT\tid_employee\n" +
                "FROM\thumanface HF INNER JOIN employee EMP USING (id_humanface)\n" +
                "WHERE\tHF.family||' '||HF.name||' '||HF.patronymic ILIKE :fio\n" +
                "\tOR HF.family||' '||SUBSTRING(HF.name, 1, 1)||'. '||SUBSTRING(HF.patronymic, 1, 1)||'.' ILIKE :fioShort";
        Query q = getSession().createSQLQuery(query).addScalar("id_employee", LongType.INSTANCE);
        q.setString("fio", fio).setString("fioShort", fio);
        List list = q.list();
        return list.size() == 0 ? null : (Long) list.get(0);
    }

    public Long getDicSubjetBySubjectname(String subjectname) {
        String query = "SELECT dic_subject_create_or_get('" + subjectname + "') AS idDicSubject";
        Query q = getSession().createSQLQuery(query).addScalar("idDicSubject", LongType.INSTANCE);
        List<?> list = getList(q);
        return list.isEmpty() ? null : (Long) list.get(0);
    }

    public boolean updateSubject(SubjectsModel subjectGroupDec, SubjectsModel subjectGroupMine) {
        String query = "UPDATE subject SET subjectcode = :subjectcode, otherdbid = :idSubjMine ,\n " + "id_dic_subject = :idDicSubj,\n" + "id_chair = :idChair,\n" +
                "hourscount = :hourscount,\n" + "hourslabor = :hourslabor, \n" + "hourslection = :hourslection, \n" +
                "hourspractic = :hourspractic, \n" + "hoursaudcount = :hoursaudcount, \n" + "is_facultative = :facultative\n" +
                "WHERE id_subject = :idSubj";
        Query q = getSession().createSQLQuery(query)
                .setString("subjectcode", subjectGroupMine.getSubjectcode())
                .setLong("idSubjMine", subjectGroupMine.getIdSubjMine())
                .setLong("idDicSubj", subjectGroupDec.getIdDicSubj())
                .setLong("idChair", subjectGroupDec.getIdChair())
                .setDouble("hourscount", subjectGroupMine.getHoursCount())
                .setDouble("hourslabor", subjectGroupMine.getHoursLabaratory())
                .setDouble("hourslection", subjectGroupMine.getHoursLecture())
                .setDouble("hourspractic", subjectGroupMine.getHoursPractice())
                .setDouble("hoursaudcount", subjectGroupMine.getHoursLabaratory() + subjectGroupMine.getHoursLecture() +
                        subjectGroupMine.getHoursPractice())
                .setBoolean("facultative",
                        subjectGroupMine.getFacultative() == null ? false : subjectGroupMine.getFacultative()
                )
                .setLong("idSubj", subjectGroupDec.getIdSubj());
        return executeUpdate(q);
    }

    public boolean createSubject(Long idLGS, SubjectsModel subjectMine) {
        String query = "SELECT * FROM create_subject_sr_by_lgs(" + idLGS + ",'" + subjectMine.getSubjectcode() + "','" + subjectMine.getSubjectname() + "'," +
                subjectMine.getHoursCount() + "," + subjectMine.getHoursLabaratory() + "," + subjectMine.getHoursLecture() + "," +
                subjectMine.getHoursPractice() + "," + subjectMine.getIdChair() + "," + subjectMine.getIdSubjMine() + "," +
                (subjectMine.getExam() ? 1 : 0) + "," + (subjectMine.getPass() ? 1 : 0) + "," + (subjectMine.getCp() ? 1 : 0) + "," +
                (subjectMine.getCw() ? 1 : 0) + ",0," + subjectMine.getType() + ", " + +(subjectMine.getPracticType() ? 1 : 0) +
                ", " + subjectMine.getFacultative() + ", '" + castListToStringArray(new ArrayList(subjectMine.getEmployees())) +
                "'::BIGINT[])";
        return callFunction(query);
    }

    public boolean updateSubjectCode(Long idSubject, String subjectCode) {
        String query = "UPDATE subject SET subjectcode = '" + subjectCode + "' WHERE id_subject = " + idSubject;
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean updateAudSubject(SubjectsModel subjectGroupDec, SubjectsModel subjectGroupMine) {
        String query = "UPDATE subject SET hoursaudcount = :hoursaudcount WHERE id_subject = :idSubj";
        Query q = getSession().createSQLQuery(query)
                .setDouble("hoursaudcount", subjectGroupMine.getHoursLabaratory() + subjectGroupMine.getHoursLecture() +
                        subjectGroupMine.getHoursPractice())
                .setLong("idSubj", subjectGroupDec.getIdSubj());
        return executeUpdate(q);
    }

    public List<DepartmentModel> getAllDepartments () {
        String query = "SELECT \n" + "\tid_department AS idDepartment, fulltitle, shorttitle, otherdbid AS idDepartmentMine,\n" +
                "\tid_chair AS idChair\n" + "FROM department ORDER BY fulltitle";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idDepartment", LongType.INSTANCE)
                .addScalar("fulltitle")
                .addScalar("shorttitle")
                .addScalar("idDepartmentMine", LongType.INSTANCE)
                .addScalar("idChair", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(DepartmentModel.class));
        return (List<DepartmentModel>) getList(q);
    }

    public boolean linkTeacherForSubject(Long idEmp, long idLgss){
        String query = "INSERT INTO link_employee_subject_group (id_employee, id_link_group_semester_subject) values ("+idEmp+", "+idLgss+")";
        Query q = getSession().createSQLQuery(query);
        return  executeUpdate(q);
    }

    public List<SubjectsModel> getDecSubjectsByLgss(Long idLgs) {
        String query = "select id_link_group_semester_subject as idLGSS, ds.subjectname as subjectname, s.otherdbid as idSubjMine, s.subjectcode as subjectcode\n" +
                "from link_group_semester_subject\n" +
                "join subject s using (id_subject)\n" +
                "join dic_subject ds using (id_dic_subject)\n" +
                "where id_link_group_semester = " + idLgs;
        Query q = getSession().createSQLQuery(query)
                .addScalar("idLGSS", LongType.INSTANCE)
                .addScalar("subjectname")
                .addScalar("subjectcode")
                .addScalar("idSubjMine", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(SubjectsModel.class));
        return q.list();
    }

}
