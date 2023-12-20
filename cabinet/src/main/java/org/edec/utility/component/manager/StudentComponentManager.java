package org.edec.utility.component.manager;

import org.edec.dao.DAO;
import org.edec.utility.component.model.dao.GroupSubjectDAOmodel;
import org.edec.utility.component.model.dao.StudentSubjectDAOmodel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;


public class StudentComponentManager extends DAO {
    public List<GroupSubjectDAOmodel> getGroupSubjectModel (String groupname) {
        String query = "SELECT\n" +
                       "\tLGS.course, LGS.semesternumber AS semester, LGS.id_dic_group AS idDG, LGS.id_link_group_semester AS idLGS, DG.groupname,\n" +
                       "\tDS.subjectname, S.id_subject AS idSubj, S.hoursCount, S.type, \n" +
                       "\tS.is_exam = 1 AS exam, S.is_pass=1 AS pass, S.is_courseproject=1 AS cp, S.is_coursework=1 AS cw, S.is_practic=1 AS practic\n" +
                       "FROM\n" + "\tdic_group DG \n" + "\tINNER JOIN link_group_semester LGS USING (id_dic_group)\n" +
                       "\tINNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" + "WHERE\n" +
                       "\tDG.groupname LIKE :groupname\n" + "ORDER BY\n" + "\tid_semester, subjectname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("course")
                              .addScalar("semester")
                              .addScalar("idDG", LongType.INSTANCE)
                              .addScalar("idLGS", LongType.INSTANCE)
                              .addScalar("groupname")
                              .addScalar("subjectname")
                              .addScalar("idSubj", LongType.INSTANCE)
                              .addScalar("hoursCount")
                              .addScalar("type")
                              .addScalar("exam")
                              .addScalar("pass")
                              .addScalar("cp")
                              .addScalar("cw")
                              .addScalar("practic")
                              .setResultTransformer(Transformers.aliasToBean(GroupSubjectDAOmodel.class));
        q.setString("groupname", groupname);
        return (List<GroupSubjectDAOmodel>) getList(q);
    }

    public List<StudentSubjectDAOmodel> getStudentsModel (String groupname, Long idHum) {
        String query = "SELECT\n" + "\tLGS.semesternumber AS semester, DS.subjectname, S.id_subject AS idSubj, S.hoursCount, SR.type, \n" +
                       "\tSR.is_exam = 1 AS exam, SR.is_pass=1 AS pass, SR.is_courseproject=1 AS cp, SR.is_coursework=1 AS cw, SR.is_practic=1 AS practic,\n" +
                       "\tSSS.id_student_semester_status AS idSSS\n" + "FROM\n" + "\tdic_group DG \n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_dic_group)\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                       "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" +
                       "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" + "WHERE\n" +
                       "\tDG.groupname LIKE :groupname AND SC.id_humanface = :idHum\n" + "ORDER BY\n" + "\tid_semester, subjectname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("semester")
                              .addScalar("subjectname")
                              .addScalar("idSubj", LongType.INSTANCE)
                              .addScalar("hoursCount")
                              .addScalar("type")
                              .addScalar("exam")
                              .addScalar("pass")
                              .addScalar("cp")
                              .addScalar("cw")
                              .addScalar("practic")
                              .addScalar("idSSS", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentSubjectDAOmodel.class));
        q.setString("groupname", groupname).setLong("idHum", idHum);
        return (List<StudentSubjectDAOmodel>) getList(q);
    }
}
