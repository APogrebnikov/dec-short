package org.edec.register.manager;

import org.edec.dao.DAO;
import org.edec.register.model.dao.SubjectModelEso;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;

public class SubjectManager extends DAO {

    public List<SubjectModelEso> getListSubjects (Long idInstitute, int formOfStudy, Long idSemester, int course, boolean isBachelor,
                                                  boolean isEngineer, boolean isMaster) {
        String query = "SELECT\n" + "\tCUR.qualification as qualification, " + "\tDS.subjectname as subjectName,\n" +
                       "\tS.id_subject as idSubject,\n" + "\tLGSS.id_link_group_semester_subject as idLgss,\n" +
                       "\tDG.id_dic_group as idGroup," + "\tDG.groupname as groupName, \n" + "\tS.is_exam = 1 as exam," +
                       "\tS.is_pass = 1 as pass," + "\tS.is_courseproject = 1 as cp," + "\tS.is_coursework = 1 as cw," +
                       "\tS.is_practic = 1 as practic," + "\tS.type as type," +
                       "\tHF.family as familyTeacher, HF.name as nameTeacher, HF.patronymic as patronymicTeacher, \n" +
                       "\tLGS.course as course \n" + "FROM\n" + "\tlink_group_semester LGS \n" +
                       "\tINNER JOIN dic_group DG USING (id_dic_group) \n"

                       + "    INNER JOIN curriculum CUR ON DG.id_curriculum = CUR.id_curriculum" +
                       "\tINNER JOIN semester SEM ON SEM.id_semester = LGS.id_semester\n" +
                       "\tINNER JOIN link_group_semester_subject LGSS ON LGSS.id_link_group_semester = LGS.id_link_group_semester\n" +
                       "\tINNER JOIN subject S USING (id_subject)\n" +
                       "\tINNER JOIN link_employee_subject_group LESG ON LESG.id_link_group_semester_subject = LGSS.id_link_group_semester_subject\n" +
                       "\tINNER JOIN employee EMP USING(id_employee)\n" +
                       "\tINNER JOIN humanface HF ON EMP.id_humanface = HF.id_humanface\n" +
                       "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" + "WHERE\n" + "\tLGS.id_semester = " + idSemester + "\n" +
                       "AND CUR.qualification IN (" + (isBachelor ? "2" : "null") + ", " + (isMaster ? "3" : "null") + ", " +
                       (isEngineer ? "1" : "null") + ")" + "\tAND SEM.id_institute = " + idInstitute + "\n" + "\tAND SEM.formofstudy " +
                       (formOfStudy == 3 ? " in (1,2)" : " = " + formOfStudy) + " \n" +
                       (course != 0 ? " AND    LGS.course = " + course : "") + "\tORDER BY\n" + "\tLGS.course,subjectName,groupName";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("subjectName")
                              .addScalar("idSubject", LongType.INSTANCE)
                              .addScalar("idGroup", LongType.INSTANCE)
                              .addScalar("idLgss", LongType.INSTANCE)
                              .addScalar("exam")
                              .addScalar("pass")
                              .addScalar("cp")
                              .addScalar("cw")
                              .addScalar("practic")
                              .addScalar("familyTeacher")
                              .addScalar("type")
                              .addScalar("groupName")
                              .addScalar("nameTeacher")
                              .addScalar("patronymicTeacher")
                              .addScalar("course")
                              .addScalar("qualification")
                              .setResultTransformer(Transformers.aliasToBean(SubjectModelEso.class));
        return (List<SubjectModelEso>) getList(q);
    }
}
