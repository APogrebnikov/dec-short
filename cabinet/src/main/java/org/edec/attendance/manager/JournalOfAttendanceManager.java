package org.edec.attendance.manager;

import org.edec.attendance.model.MonthAttendSubjectsModel;
import org.edec.attendance.model.StudentAttendanceModel;
import org.edec.dao.DAO;
import org.edec.model.AttendanceModel;
import org.edec.model.GroupSemesterModel;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.Date;
import java.util.List;


public class JournalOfAttendanceManager extends DAO {
    public GroupSemesterModel getGroupSemester(Long idHum) {
        String query = "SELECT\tDG.groupname, LGS.course, LGS.id_link_group_semester AS idLGS,\n" +
                "\tLGS.dateofbeginsemester, LGS.dateofendsemester\n" +
                "FROM\tstudentcard SC\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_studentcard) \n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SEM USING (id_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "WHERE\tSC.id_humanface = :idHum AND SEM.is_current_sem = 1 AND SSS.is_academicleave = 0 AND SSS.is_deducted = 0";
        Query q = getSession().createSQLQuery(query)
                .addScalar("groupname").addScalar("course").addScalar("idLGS", LongType.INSTANCE)
                .addScalar("dateOfBeginSemester").addScalar("dateOfEndSemester")
                .setResultTransformer(Transformers.aliasToBean(GroupSemesterModel.class));
        q.setLong("idHum", idHum);
        List list = getList(q);
        return list.size() == 0 ? null : (GroupSemesterModel) list.get(0);
    }

    public List<GroupSemesterModel> getGroupModelBySem(Long idSem, String qualification, String courses) {
        String query = "SELECT\tDG.groupname, LGS.course, LGS.id_link_group_semester AS idLGS,\n" +
                "\tLGS.dateofbeginsemester, LGS.dateofendsemester\n" +
                "FROM link_group_semester LGS\n" +
                "\tINNER JOIN semester SEM USING (id_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN curriculum CUR USING (id_curriculum)\n" +
                "WHERE\n" +
                "\tLGS.id_semester = :idSem\n" +
                "\tAND CUR.qualification IN ("+ qualification + ")\n" +
                "\tAND LGS.course IN ("+ courses + ")\n" +
                "ORDER BY\n" +
                "\tLGS.course, CUR.qualification, DG.groupname";

        Query q = getSession().createSQLQuery(query)
                .addScalar("groupname").addScalar("course").addScalar("idLGS", LongType.INSTANCE)
                .addScalar("dateOfBeginSemester").addScalar("dateOfEndSemester")
                .setResultTransformer(Transformers.aliasToBean(GroupSemesterModel.class));
        q.setLong("idSem", idSem);
        return (List<GroupSemesterModel>) getList(q);
    }

    public List<MonthAttendSubjectsModel> getMonthAttendModel(Long idLGS, Date dateOfBegin, Date dateOfEnd) {
        String query = "SELECT\tCAST(mDay AS DATE) AS day,count(AT.*)\n" +
                "FROM \tgenerate_series('" + DateConverter.convertDateToSQLStringFormat(dateOfBegin) + "', '" + DateConverter.convertDateToSQLStringFormat(dateOfEnd) + "', CAST('1 day' AS interval)) mDay\n" +
                "\tLEFT JOIN attendance AT ON CAST(mDay AS DATE) = AT.visitdate\n" +
                "\t\tAND AT.id_link_group_semester_subject IN (SELECT id_link_group_semester_subject FROM link_group_semester_subject WHERE id_link_group_semester = :idLGS) and AT.attend <> -1\n" +
                "GROUP BY mDay\n" +
                "ORDER BY mDay";
        Query q = getSession().createSQLQuery(query)
                .addScalar("day").addScalar("count", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(MonthAttendSubjectsModel.class));
        q.setLong("idLGS", idLGS);
        return (List<MonthAttendSubjectsModel>) getList(q);
    }

    public List<StudentAttendanceModel> getStudentByLGS(Long idLGS) {
        String query = "SELECT HF.family, HF.name, HF.patronymic, SSS.id_student_semester_status AS idSSS\n" +
                "FROM humanface HF\n" +
                "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "WHERE SSS.is_deducted = 0 AND SSS.is_academicleave = 0 AND SSS.id_link_group_semester = :idLGS\n" +
                "ORDER BY family, name, patronymic";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family").addScalar("name").addScalar("patronymic")
                .addScalar("idSSS", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(StudentAttendanceModel.class));
        q.setLong("idLGS", idLGS);
        return (List<StudentAttendanceModel>) getList(q);
    }

    public List<AttendanceModel> getAttendanceByLGSAndDate(Long idLGS, Date visitDate) {
        String query = "SELECT\n" +
                "\tAT.id_student_semester_status AS idSSS, AT.id_link_group_semester_subject AS idLGSS,\n" +
                "\tDS.subjectname, AT.pos, AT.attend, AT.lesson, AT.id_attendance AS idAttendance\n" +
                "FROM\n" +
                "\tattendance AT\n" +
                "\tINNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester_subject)\n" +
                "\tINNER JOIN subject S USING (id_subject)\n" +
                "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "WHERE\n" +
                "\tLGSS.id_link_group_semester = :idLGS AND AT.visitdate = '" + DateConverter.convertDateToSQLStringFormat(visitDate) + "'";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idSSS", LongType.INSTANCE).addScalar("idLGSS", LongType.INSTANCE)
                .addScalar("subjectname").addScalar("pos").addScalar("attend").addScalar("lesson")
                .addScalar("idAttendance", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(AttendanceModel.class));
        q.setLong("idLGS", idLGS);
        return (List<AttendanceModel>) getList(q);
    }

    /**
     * Функция возвращает список предметов с позицией за день
     *
     * @param idLGS
     * @param visitDate
     * @return
     */
    public List<AttendanceModel> getSubjectAttendanceByLGSAndDate(Long idLGS, Date visitDate) {
        String query = "SELECT\tDISTINCT AT.id_link_group_semester_subject AS idLGSS, DS.subjectname, AT.pos, AT.lesson\n" +
                "FROM\tattendance AT\n" +
                "\tINNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester_subject)\n" +
                "\tINNER JOIN subject S USING (id_subject)\n" +
                "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "WHERE\tLGSS.id_link_group_semester = :idLGS AND AT.visitdate = '" + DateConverter.convertDateToSQLStringFormat(visitDate) + "'\n" +
                "ORDER BY pos";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idLGSS", LongType.INSTANCE).addScalar("subjectname").addScalar("pos").addScalar("lesson")
                .setResultTransformer(Transformers.aliasToBean(AttendanceModel.class));
        q.setLong("idLGS", idLGS);
        return (List<AttendanceModel>) getList(q);
    }

    public void createAttendance(AttendanceModel attendance) {
        String query = "INSERT INTO attendance (id_link_group_semester_subject, id_student_semester_status, pos, attend, visitdate, lesson)\n" +
                "VALUES (:idLGSS, :idSSS, :pos, :attend, :visitdate, :lesson)";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idLGSS", attendance.getIdLGSS()).setLong("idSSS", attendance.getIdSSS())
                .setInteger("pos", attendance.getPos()).setInteger("attend", attendance.getAttend())
                .setDate("visitdate", attendance.getVisitDate()).setBoolean("lesson", attendance.getLesson());
        executeUpdate(q);
    }

    public void updateAttendance(AttendanceModel attendance) {
        String query = "UPDATE attendance SET attend = :attend WHERE id_attendance = :idAttend";
        Query q = getSession().createSQLQuery(query);
        q.setInteger("attend", attendance.getAttend()).setLong("idAttend", attendance.getIdAttendance());
        executeUpdate(q);
    }

    public void deleteAttendanceByDate(Date day, Long idLGS) {
        String query = "DELETE FROM attendance\n" +
                "WHERE visitdate = :selectedDay AND id_link_group_semester_subject IN (" +
                "SELECT id_link_group_semester_subject FROM link_group_semester_subject\n" +
                "\tWHERE id_link_group_semester = :idLGS)";
        Query q = getSession().createSQLQuery(query)
                .setDate("selectedDay", day).setLong("idLGS", idLGS);
        executeUpdate(q);
    }
}
