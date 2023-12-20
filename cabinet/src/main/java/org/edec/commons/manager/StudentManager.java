package org.edec.commons.manager;

import org.edec.commons.model.StudentGroupModel;
import org.edec.dao.DAO;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StandardBasicTypes;

import java.util.List;


public class StudentManager extends DAO {
    /**
     * @param filter либо фио, либо зачетка, либо группа
     * @param idInst - институт (если null, то институт не учитывается)
     * @param fos    - форма обучения (если null, то форма обучения не учитывается)
     * @return список студентов
     */
    public List<StudentGroupModel> getStudentGroupByFilter(String filter, Long idInst, Integer fos) {
        String query = "SELECT distinct SSS.is_government_financed as isGovernmentFinanced, HF.id_humanface,\n" +
                "HF.family, HF.name, HF.patronymic, HF.sex, SC.recordBook, DG.groupname,\n" +
                "cur.qualification, DG.id_dic_group AS idDG,\n" +
                "SC.id_studentcard as idStudentCard, dir.code as directionCode, dir.title AS directionName\n" +
                "FROM humanface HF\n" +
                "INNER JOIN studentcard SC USING (id_humanface)\n" +
                "INNER JOIN (SELECT max(id_studentcard) AS maxIdSC, id_humanface\n" +
                "\t\tFROM studentcard \n" +
                "\t\tGROUP BY id_humanface) AS maxSC\n" +
                "\tON HF.id_humanface = maxSC.id_humanface AND SC.id_studentcard = maxSC.maxIdSC\n" +
                "INNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "INNER JOIN semester SEM USING (id_semester)\n" +
                "INNER JOIN dic_group DG USING (id_dic_group)\n" +
                "INNER JOIN curriculum CUR USING (id_curriculum)\n" +
                "INNER JOIN direction dir USING (id_direction)\n" +
                "WHERE HF.family||' '||HF.name||' '||HF.patronymic||' '||SC.recordbook||' '||DG.groupname ILIKE '%" + filter +
                "%'\n" + (idInst == null ? "" : "AND SEM.id_institute = " + idInst + "\n") +
                (fos == null ? "" : "AND SEM.formofstudy = " + fos + "\n") +
                "ORDER BY HF.family, HF.name, DG.groupname";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family").addScalar("name").addScalar("patronymic").addScalar("recordBook").addScalar("sex")
                .addScalar("groupname").addScalar("directionCode").addScalar("directionName").addScalar("qualification")
                .addScalar("idDG", StandardBasicTypes.LONG)
                .addScalar("idStudentCard", StandardBasicTypes.LONG)
                .addScalar("isGovernmentFinanced", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(StudentGroupModel.class));
        return (List<StudentGroupModel>) getList(q);
    }
}