package org.edec.eok.manager;

import org.edec.dao.DAO;
import org.edec.eok.model.dao.SubjectEsoModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.Collections;
import java.util.List;


public class EokManager extends DAO {
    public List<SubjectEsoModel> getSubjectModel (String idLGS) {
        String query = "SELECT\n" +
                       "\tDG.groupname, HF.family||' '||HF.name||' '||HF.patronymic AS fio, COALESCE(DEP.shorttitle,DEP.fulltitle,'') AS department, \n" +
                       "\tS.is_exam = 1 AS exam, S.is_pass = 1 AS pass, S.is_courseproject = 1 AS cp, S.is_coursework =1 AS cw, S.is_practic = 1 AS practic,\n" +
                       "\tS.hourscount, LGSS.id_esocourse2 AS idEsoCourse, DS.subjectname, LGSS.id_link_group_semester_subject AS idLGSS\n" +
                       "FROM\n" + "\tdic_group DG\n" + "\tINNER JOIN link_group_semester LGS USING (id_dic_group)\n" +
                       "\tINNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                       "\tLEFT JOIN link_employee_subject_group LESG USING (id_link_group_semester_subject)\n" +
                       "\tLEFT JOIN employee EMP USING (id_employee)\n" + "\tLEFT JOIN humanface HF USING (id_humanface)\n" +
                       "\tLEFT JOIN chair CH USING (id_chair)\n" + "\tLEFT JOIN department DEP USING (id_chair)\n" +
                       "\tINNER JOIN curriculum CUR ON DG.id_curriculum = CUR.id_curriculum\n" + "WHERE\n" +
                       "\tLGS.id_link_group_semester IN (" + idLGS + ")\n" + "ORDER BY\n" +
                       "\tLGS.course, CUR.qualification, groupname, DS.subjectname, DEP.is_main DESC";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("groupname")
                              .addScalar("fio")
                              .addScalar("department")
                              .addScalar("exam")
                              .addScalar("pass")
                              .addScalar("cp")
                              .addScalar("cw")
                              .addScalar("practic")
                              .addScalar("hourscount")
                              .addScalar("idEsoCourse", LongType.INSTANCE)
                              .addScalar("subjectname")
                              .addScalar("idLGSS", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(SubjectEsoModel.class));
        return (List<SubjectEsoModel>) getList(q);
    }
}
