package org.edec.questionnaire.manager;

import org.edec.dao.DAO;
import org.edec.questionnaire.model.dao.QuestionnaireEsoModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.Collections;
import java.util.List;

public class QuestionnaireManager extends DAO {

    public List<QuestionnaireEsoModel> getGroupsWithSubjectsBySemester(Long idSem) {
        String query = "SELECT DISTINCT DG.groupname, DS.subjectname, S.id_subject AS idSubj, DG.id_dic_group AS idDG, CUR.id_chair AS idChair\n" +
                "FROM dic_group DG \n" +
                "\tINNER JOIN curriculum CUR USING (id_curriculum)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_dic_group)\n" +
                "\tINNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester)\n" +
                "\tINNER JOIN subject S USING (id_subject)\n" +
                "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n"
                + "WHERE LGS.id_semester = :idSem\n"
                + "ORDER BY\n" + "\tDG.groupname, DS.subjectname";
        Query q = getSession().createSQLQuery(query)
                .addScalar("groupname").addScalar("subjectname")
                .addScalar("idSubj", LongType.INSTANCE)
                .addScalar("idDG", LongType.INSTANCE)
                .addScalar("idChair", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(QuestionnaireEsoModel.class));
        q.setLong("idSem", idSem);
        return (List<QuestionnaireEsoModel>) getList(q);
    }

    public List<String> getRecipientsBySemesterAndGroup(Long idSem, Long idDG) {
        String query = "SELECT CAST(SC.id_humanface AS TEXT) AS recipients\n" +
                "FROM link_group_semester LGS \n" +
                "\tINNER JOIN student_semester_status SSS USING (id_link_group_semester)\n" +
                "\tINNER JOIN studentcard SC USING (id_studentcard)\n"
                + "WHERE LGS.id_semester = :idSem AND LGS.id_dic_group = :idDG\n" +
                "\tAND SSS.is_deducted = 0";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idSem", idSem).setLong("idDG", idDG);
        return (List<String>) getList(q);
    }
}
