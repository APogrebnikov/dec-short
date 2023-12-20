package org.edec.teacher.manager;

import org.edec.dao.DAO;
import org.edec.teacher.model.attendanceProgress.StudentModel;
import org.edec.teacher.model.attendanceProgress.dao.AttendanceEsoModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.Collections;
import java.util.List;


public class EntityManagerAttendProgress extends DAO {
    public List<StudentModel> getStudentsByIdLGSS (Long idLGSS) {
        String query = "SELECT\n" + "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio, SSS.id_student_semester_status AS idSSS,\n" +
                       "\tCAST(MAX(SR.esogradecurrent)/GREATEST(MAX(SR.esogrademax), 1)*100 AS INTEGER) AS progress\n" + "FROM\n" +
                       "\thumanface HF\n" + "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN sessionrating SR ON SR.id_student_semester_status = SSS.id_student_semester_status AND SR.id_subject = LGSS.id_subject\n" +
                       "WHERE\n" +
                       "\tLGSS.id_link_group_semester_subject = :idLGSS AND SSS.is_deducted = 0 AND SSS.is_academicleave = 0\n" +
                       "GROUP BY\n" + "\tHF.id_humanface, SSS.id_student_semester_status\n" + "ORDER BY\n" + "\tfio";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("idSSS", LongType.INSTANCE)
                              .addScalar("progress")
                              .setResultTransformer(Transformers.aliasToBean(StudentModel.class));
        q.setLong("idLGSS", idLGSS);
        return (List<StudentModel>) getList(q);
    }

    public List<AttendanceEsoModel> getModelStudentAttend (Long idLGSS) {
        String query = "SELECT\tid_student_semester_status AS idSSS, attend=1 AS attend, visitdate\n" + "FROM\tattendance\n" +
                       "WHERE\tid_link_group_semester_subject = :idLGSS\n" + "ORDER BY visitdate";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("attend")
                              .addScalar("visitdate")
                              .addScalar("idSSS", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(AttendanceEsoModel.class));
        q.setLong("idLGSS", idLGSS);
        return (List<AttendanceEsoModel>) getList(q);
    }
}