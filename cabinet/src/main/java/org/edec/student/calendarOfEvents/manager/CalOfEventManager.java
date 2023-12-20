package org.edec.student.calendarOfEvents.manager;

import org.edec.dao.DAO;
import org.edec.model.AttendanceModel;
import org.edec.student.calendarOfEvents.model.MonthAttendStudentModel;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.Date;
import java.util.List;


public class CalOfEventManager extends DAO {
    public List<MonthAttendStudentModel> getAttendancesByDate (Long idSSS, Date dateOfBegin, Date dateOfEnd) {
        String query = "SELECT\tCAST(mDay AS DATE) AS day,count(AT.*),\n" +
                       "\tSUM(CASE WHEN (attend = 1 OR attend = 2 OR attend = 3) THEN 1 ELSE 0 END) AS attendCount\n" +
                       "FROM \tgenerate_series('" + (DateConverter.convertDateToSQLStringFormat(dateOfBegin)) + "', '" +
                       (DateConverter.convertDateToSQLStringFormat(dateOfEnd)) + "', CAST('1 day' AS interval)) mDay\n" +
                       "\tLEFT JOIN attendance AT ON CAST(mDay AS DATE) = AT.visitdate AND AT.id_student_semester_status = :idSSS\n" +
                       "GROUP BY mDay\n" + "ORDER BY mDay";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("day")
                              .addScalar("count", LongType.INSTANCE)
                              .addScalar("attendCount", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(MonthAttendStudentModel.class));
        q.setLong("idSSS", idSSS);
        return (List<MonthAttendStudentModel>) getList(q);
    }

    public Long getIdSSSbyHum (Long idHum, Long idSem) {
        String query = "SELECT\tid_student_semester_status\n" + "FROM\tstudentcard SC\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester AND SEM.id_semester = :idSem\n" +
                       "WHERE\tSC.id_humanface = :idHum";
        Query q = getSession().createSQLQuery(query).addScalar("id_student_semester_status", LongType.INSTANCE);
        q.setLong("idHum", idHum).setLong("idSem",idSem);
        List<Long> list = (List<Long>) getList(q);
        return list.size() > 0 ? list.get(0) : null;
    }

    public List<AttendanceModel> getAttendanceBySSS (Long idSSS, Date selectedDay) {
        String query = "SELECT AT.attend, DS.subjectname\n" + "FROM attendance AT\n" +
                       "\tINNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester_subject)\n" +
                       "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                       "WHERE\tAT.id_student_semester_status = :idSSS AND AT.visitdate = '" +
                       (DateConverter.convertDateToSQLStringFormat(selectedDay)) + "'\n" + "ORDER BY pos";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("attend")
                              .addScalar("subjectname")
                              .setResultTransformer(Transformers.aliasToBean(AttendanceModel.class));
        q.setLong("idSSS", idSSS);
        return (List<AttendanceModel>) getList(q);
    }
}
