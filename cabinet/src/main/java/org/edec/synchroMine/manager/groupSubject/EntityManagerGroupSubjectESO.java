package org.edec.synchroMine.manager.groupSubject;

import org.edec.dao.DAO;
import org.edec.model.SemesterModel;
import org.edec.synchroMine.model.dao.SubjectGroupModel;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class EntityManagerGroupSubjectESO extends DAO {
    public List<SubjectGroupModel> getSubjectsByLGS(Long idLGS) {
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
                .setResultTransformer(Transformers.aliasToBean(SubjectGroupModel.class));
        q.setLong("idLGS", idLGS);
        return (List<SubjectGroupModel>) getList(q);
    }

    public List<String> getRegisterNumberByLGSandSubj(Long idLGS, Long idSubj) {
        String query = "SELECT\tDISTINCT R.register_number\n" + "FROM sessionrating SR\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN sessionratinghistory SRH USING (id_sessionrating)\n" +
                "\tINNER JOIN register  R USING (id_register)\n" +
                "WHERE\tR.certnumber IS NOT NULL AND R.register_number IS NOT NULL AND SSS.id_link_group_semester = :idLGS AND SR.id_subject = :idSubj\t";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idLGS", idLGS).setLong("idSubj", idSubj);
        return (List<String>) getList(q);
    }

    public Long getEmpByFIO(String family, String name, String patronymic) {
        String query = "SELECT\tid_employee\n" + "FROM\thumanface HF\n" + "\tINNER JOIN employee EMP USING (id_humanface)\n" +
                "WHERE\tHF.family||' '||HF.name||' '||HF.patronymic ILIKE :fio\n" +
                "\tOR HF.family||' '||SUBSTRING(HF.name, 1, 1)||'. '||SUBSTRING(HF.patronymic, 1, 1)||'.' ILIKE :fioShort";
        String fio = family + " " + name + " " + patronymic;
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

    public boolean updateGroup(Long idLGS, Integer idGroupMine) {
        String query = "UPDATE link_group_semester SET otherdbid = :idGroupMine WHERE id_link_group_semester = :idLGS";
        Query q = getSession().createSQLQuery(query).setLong("idGroupMine", idGroupMine).setLong("idLGS", idLGS);
        return executeUpdate(q);
    }

    public boolean updateChairs(Long idCurriculum, Integer idChairMine) {
        String query = "update curriculum set id_chair = (select id_chair from department where otherdbid = "+idChairMine+") \n" +
                "where id_curriculum = " + idCurriculum;
        Query q = getSession().createSQLQuery(query);
        return executeUpdate(q);
    }

    public boolean updateSubjectFacultative(Long idSubject, Boolean facultative) {
        String query = "UPDATE subject SET is_facultative = " + facultative + " WHERE id_subject = " + idSubject;
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean updateSubjectCode(Long idSubject, String subjectCode) {
        String query = "UPDATE subject SET subjectcode = '" + subjectCode + "' WHERE id_subject = " + idSubject;
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean updateSubjectPracticeType(Long idSubject, String practiceType, String practiceTypeDative) {
        String query = "UPDATE subject SET practic_type = '" + practiceType + "', practic_type_dative = '" + practiceTypeDative + "' WHERE id_subject = " + idSubject;
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean updateLGSPracticeDate(Long idLGS, Date dateOfBeginPractice, Date dateOfEndPractice) {
        String query = "UPDATE link_group_semester SET dateofbeginpractic = '" + DateConverter.convertDateToString(dateOfBeginPractice) + "',\n" +
                "\tdateofendpractic = '" + DateConverter.convertDateToString(dateOfEndPractice) + "' WHERE id_link_group_semester = " + idLGS;
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean updateSubject(SubjectGroupModel subjectGroupDec, SubjectGroupModel subjectGroupMine) {
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

    public boolean updateAudSubject(SubjectGroupModel subjectGroupDec, SubjectGroupModel subjectGroupMine) {
        String query = "UPDATE subject SET hoursaudcount = :hoursaudcount WHERE id_subject = :idSubj";
        Query q = getSession().createSQLQuery(query)
                .setDouble("hoursaudcount", subjectGroupMine.getHoursLabaratory() + subjectGroupMine.getHoursLecture() +
                        subjectGroupMine.getHoursPractice())
                .setLong("idSubj", subjectGroupDec.getIdSubj());
        return executeUpdate(q);
    }

    public boolean createSubject(Long idLGS, SubjectGroupModel subjectMine) {
        String query = "SELECT * FROM create_subject_sr_by_lgs(" + idLGS + ",'" + subjectMine.getSubjectcode() + "','" + subjectMine.getSubjectname() + "'," +
                subjectMine.getHoursCount() + "," + subjectMine.getHoursLabaratory() + "," + subjectMine.getHoursLecture() + "," +
                subjectMine.getHoursPractice() + "," + subjectMine.getIdChair() + "," + subjectMine.getIdSubjMine() + "," +
                (subjectMine.getExam() ? 1 : 0) + "," + (subjectMine.getPass() ? 1 : 0) + "," + (subjectMine.getCp() ? 1 : 0) + "," +
                (subjectMine.getCw() ? 1 : 0) + ",0," + subjectMine.getType() + ", " + +(subjectMine.getPracticType() ? 1 : 0) +
                ", " + subjectMine.getFacultative() + ", '" + castListToStringArray(new ArrayList(subjectMine.getEmployees())) +
                "'::BIGINT[])";
        return callFunction(query);
    }

    public Long getIdSchoolYearByBeginDate(Date dateBegin) {
        String query = "SELECT id_schoolyear FROM schoolyear WHERE EXTRACT(YEAR FROM dateofbegin) = " +
                DateConverter.convertDateToYearString(dateBegin);
        Query q = getSession().createSQLQuery(query).addScalar("id_schoolyear", LongType.INSTANCE);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long getIdChairByNameAndODI(String fulltitle, Integer odi) {
        String query = "SELECT id_chair FROM department WHERE otherdbid = :odi OR fulltitle LIKE :fulltitle";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id_chair", LongType.INSTANCE)
                .setLong("odi", odi)
                .setString("fulltitle", fulltitle);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long getIdDirectionByTitleAndCode(String title, String code) {
        String query = "SELECT id_direction FROM direction WHERE title LIKE :title AND code LIKE :code";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id_direction", LongType.INSTANCE)
                .setString("title", title)
                .setString("code", code);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long createOrGetCurriculum(GroupMineModel data, Long idCreatedSchoolYear, Long idEnterSchoolYear) {
        String query =
                "SELECT create_curriculum(" + idEnterSchoolYear + ", " + idCreatedSchoolYear + ",'" + data.getPlanfileName() + "', " +
                        data.getQualification() + ", '" +
                        (data.getQualificationCode() == null ? data.getDirectionCode() : data.getQualificationCode()) + "', '" +
                        (data.getDirectionTitle() == null ? data.getSpecialityTitle() : data.getDirectionTitle()) + "', " + data.getIdChair() +
                        ", " + data.getIdDirection() + ", " + data.getFormOfStudy() + ", " + data.getPeriodOfStudy() + ", " + data.getGeneration() +
                        ") AS idCurriculum";
        Query q = getSession().createSQLQuery(query).addScalar("idCurriculum", LongType.INSTANCE);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long createOrGetDicGroup(Long idCurriculum, Long idInst, Boolean military, String groupname) {
        String query = "SELECT create_dic_group(" + idCurriculum + ", " + idInst + ", " + (military ? 0 : 1) + ", '" + groupname +
                "') AS idDicGroup";
        Query q = getSession().createSQLQuery(query).addScalar("idDicGroup", LongType.INSTANCE);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }

    public Long createOrGetLGS(Integer course, Integer semesterNumber, Long idDicGroup, Long idSem, Integer idGroupMine) {
        String query = "SELECT create_link_group_semester(" + course + ", " + semesterNumber + ", " + idDicGroup + ", " + idSem + ", " +
                idGroupMine + ") AS idLGS";
        Query q = getSession().createSQLQuery(query).addScalar("idLGS", LongType.INSTANCE);
        List<Long> list = (List<Long>) getList(q);
        return list.size() == 0 ? null : list.get(0);
    }
}