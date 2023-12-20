package org.edec.directorNotion.manager;

import org.edec.dao.DAO;
import org.edec.directorNotion.model.StudentModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import java.util.Collections;
import java.util.List;

public class DirectorNotionManager extends DAO {

    public List<StudentModel> getStudentByFilter (String fio, String recordbook, String groupname) {
        try {
            begin();
            String query =
                    "SELECT\n" + "\tHF.family, HF.name, HF.patronymic, SC.recordbook, max(LGS.course) AS course, \n" + "\tDG.groupname,\n" +
                    "\tCUR.specialitytitle AS directionName,\n " + "\tCUR.directioncode AS directionNumber,\n " +
                    "\tSSS.formofstudy AS formOfStudy\n" + "FROM\n" + "\thumanface HF\n" +
                    "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                    "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                    "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                    "\tINNER JOIN semester SE USING (id_semester)\n" + "\tINNER JOIN dic_group DG USING (id_dic_group) \n" +
                    "\tINNER JOIN curriculum CUR USING (id_curriculum)\n" + "WHERE\n" +
                    "\tHF.family||' '||HF.name||' '||HF.patronymic ILIKE :fio\n" + "\tAND SC.recordbook ILIKE :recordbook\n" +
                    "\tAND DG.groupname ILIKE :groupname\n" + "GROUP BY\n" +
                    "\tHF.id_humanface, SC.id_studentcard, DG.id_dic_group, SE.id_institute, CUR.specialitytitle, CUR.directioncode,SSS.formofstudy\n" +
                    "ORDER BY\n" + "\tHF.family, HF.name";
            Query q = getSession().createSQLQuery(query)
                                  .addScalar("family")
                                  .addScalar("name")
                                  .addScalar("patronymic")
                                  .addScalar("recordbook")
                                  .addScalar("groupname")
                                  .addScalar("course")
                                  .addScalar("directionName")
                                  .addScalar("directionNumber")
                                  .addScalar("formOfStudy")
                                  .setResultTransformer(Transformers.aliasToBean(StudentModel.class));
            q.setParameter("fio", "%" + fio + "%")
             .setParameter("recordbook", "%" + recordbook + "%")
             .setParameter("groupname", "%" + groupname + "%");
            List<StudentModel> list = q.list();
            commit();
            return list;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }
}
