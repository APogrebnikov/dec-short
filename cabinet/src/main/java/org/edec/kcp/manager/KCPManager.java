package org.edec.kcp.manager;

import org.edec.dao.DAO;
import org.edec.kcp.model.KCPFullModel;
import org.edec.kcp.model.StudentShortModel;
import org.edec.subjectsAnalysis.model.SubjectsAnalysisModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;

import java.util.List;

public class KCPManager extends DAO {
    public List<KCPFullModel> getFullModel(String year, List<Integer> qualification, List<Integer> formofstudy) {
        String query = "SELECT\n" +
                "DISTINCT ON (directioncode, date_part('year',sy.dateofbegin), fos) \n" +
                "cu.directioncode as directioncode,\n" +
                "cu.formofstudy as fos,\n" +
                "di.code as code,\n" +
                "di.title as direction,\n" +
                "date_part('year',sy.dateofbegin) as startyear,\n" +
                "trunc(date_part('year',sy.dateofbegin) + cu.periodofstudy) as endyear,\n" +
                "cu.kcp_dogovor as kcpDogovor,\n" +
                "cu.kcp_budget as kcpBudget\n" +
                "FROM\n" +
                "curriculum cu\n" +
                "INNER JOIN schoolyear sy ON cu.enter_school_year = sy.id_schoolyear\n" +
                "INNER JOIN direction di ON di.id_direction = cu.id_direction\n" +
                "WHERE trunc(date_part('year',sy.dateofbegin) + cu.periodofstudy) >= '" + year + "'\n" +
                "AND cu.qualification IN :qualification\n" +
                "AND cu.formofstudy IN :formofstudy\n" +
                "ORDER BY date_part('year',sy.dateofbegin), directioncode, fos";

        Query q = getSession().createSQLQuery(query)
                .addScalar("directioncode")
                .addScalar("fos", IntegerType.INSTANCE)
                .addScalar("code")
                .addScalar("direction")
                .addScalar("startyear", StringType.INSTANCE)
                .addScalar("endyear", StringType.INSTANCE)
                .addScalar("kcpDogovor", IntegerType.INSTANCE)
                .addScalar("kcpBudget", IntegerType.INSTANCE)
                .setParameterList("qualification", qualification)
                .setParameterList("formofstudy", formofstudy)
                .setResultTransformer(Transformers.aliasToBean(KCPFullModel.class));

        return q.list();
    }

    public List<StudentShortModel> getStudentsForDir(String directioncode, String year, List<Integer> qualification, List<Integer> formofstudy) {
        String query = "SELECT\n" +
                "sss.id_student_semester_status AS id,\n" +
                "dg.groupname AS groupName,\n" +
                "sc.recordbook AS recordBook,\n" +
                "hf.family || ' ' || hf.name || ' ' || hf.patronymic AS fio, \n" +
                "sss.is_government_financed AS financed\n" +
                "FROM \n" +
                "student_semester_status sss\n" +
                "INNER JOIN studentcard sc ON sc.id_studentcard = sss.id_studentcard\n" +
                "INNER JOIN humanface hf ON hf.id_humanface = sc.id_humanface\n" +
                "INNER JOIN link_group_semester lgs ON lgs.id_link_group_semester = sss.id_link_group_semester\n" +
                "INNER JOIN semester se ON se.id_semester = lgs.id_semester AND se.is_current_sem = 1\n" +
                "INNER JOIN dic_group dg ON dg.id_dic_group = lgs.id_dic_group\n" +
                "INNER JOIN curriculum cu ON cu.id_curriculum = dg.id_curriculum\n" +
                "INNER JOIN schoolyear sy ON cu.enter_school_year = sy.id_schoolyear\n" +
                "INNER JOIN direction di ON di.id_direction = cu.id_direction\n" +
                "WHERE \n" +
                "cu.directioncode = '" + directioncode + "' \n" +
                "AND date_part('year',sy.dateofbegin) = '" + year + "' \n" +
                "AND cu.qualification IN :qualification\n" +
                "AND cu.formofstudy IN :formofstudy\n" +
                "AND sss.is_academicleave = 0\n" +
                "AND sss.is_deducted = 0\n" +
                "AND sss.is_transfered = 0";

        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("groupName")
                .addScalar("recordBook")
                .addScalar("fio")
                .addScalar("financed", BooleanType.INSTANCE)
                .setParameterList("qualification", qualification)
                .setParameterList("formofstudy", formofstudy)
                .setResultTransformer(Transformers.aliasToBean(StudentShortModel.class));

        return q.list();
    }

    public List<StudentShortModel> getProblemStudents(String directioncode, String year, Integer debt, String conditionDebt, List<Integer> qualification, List<Integer> formofstudy) {
        if (conditionDebt == null) conditionDebt = ">=";
        String query = "WITH search AS (SELECT SSS.id_studentcard\n" +
                "FROM student_semester_status SSS \n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "WHERE SEM.id_institute = 1 AND SEM.is_current_sem = 1 AND SSS.is_deducted = 0 AND SSS.is_academicleave = 0)\n" +
                "SELECT *\n" +
                "FROM (SELECT HF.family||' '||HF.name||' '||HF.patronymic AS fio, " +
                "SC.id_studentcard AS idSC, " +
                "DG.id_dic_group AS idDG, " +
                "DG.groupname as groupName,\n" +
                "\t\tCAST((SUM(CASE WHEN (SR.is_exam = 1 AND SR.examrating < 3 and SR.esoexamrating <> -1) THEN 1 ELSE 0 END)\n" +
                "\t\t+SUM(CASE WHEN (SR.is_pass = 1 AND SR.passrating <> 1 AND SR.passrating < 3 and SR.esopassrating <> -1) THEN 1 ELSE 0 END)\n" +
                "\t\t+SUM(CASE WHEN (SR.is_courseproject = 1 AND SR.courseprojectrating < 3  and SR.esocourseprojectrating <> -1) THEN 1 ELSE 0 END)\n" +
                "\t\t+SUM(CASE WHEN (SR.is_coursework = 1 AND SR.courseworkrating < 3 and SR.esocourseworkrating <> -1) THEN 1 ELSE 0 END)\n" +
                "\t\t+SUM(CASE WHEN (SR.is_practic = 1 AND SR.is_pass <> 1 AND SR.practicrating < 3) THEN 1 ELSE 0 END)) AS INTEGER) AS debt,\n" +
                "\t  MAX(sss.is_government_financed) AS financed\n" +
                "\tFROM humanface HF \n" +
                "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SEM ON SEM.id_semester = LGS.id_semester\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN curriculum CUR USING (id_curriculum)\n" +
                "\tINNER JOIN direction DIR USING (id_direction)\n" +
                "\tINNER JOIN schoolyear SY ON CUR.enter_school_year = SY.id_schoolyear\n" +
                "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" +
                "\tINNER JOIN subject SU USING (id_subject)\n" +
                "\tINNER JOIN link_group_semester_subject LGSS USING (id_subject)\n" +
                "\tINNER JOIN search ON SSS.id_studentcard = search.id_studentcard\n" +
                "\tWHERE SC.id_current_dic_group = DG.id_dic_group\n" +
                "\t\tAND CUR.qualification IN (1,2,3)\n" +
                "\t\tAND CAST(LGS.course AS TEXT) LIKE '%%'\n" +
                "\t\tAND CAST(SSS.is_government_financed AS TEXT) LIKE '%%'\n" +
                "\t\tAND SEM.formofstudy = 1 \n" +
                "\t\tAND SSS.is_listener = 0\n" +
                "\tAND CUR.directioncode = :directioncode \n" +
                "\tAND date_part('year',sy.dateofbegin) = :year\n" +
                "\tAND LGS.id_semester < (SELECT id_semester FROM semester WHERE is_current_sem = 1 AND formofstudy = 1 AND id_institute = 1)\n" +
                "\tAND CUR.qualification IN :qualification\n" +
                "\tAND CUR.formofstudy IN :formofstudy\n" +
                "\tGROUP BY fio, idSC, idDG\n" +
                "\tHAVING SUM(SSS.is_deducted) = 0 AND SUM(SSS.is_academicleave) = 0\n" +
                "\tORDER BY fio) myTab\n" +
                "WHERE debt " + conditionDebt + " :debt";

        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("groupName")
                .addScalar("debt", IntegerType.INSTANCE)
                .addScalar("financed", BooleanType.INSTANCE)
                .setString("directioncode", directioncode)
                .setDouble("year", Double.parseDouble(year))
                .setParameterList("qualification", qualification)
                .setParameterList("formofstudy", formofstudy)
                .setParameter("debt", debt)
                .setResultTransformer(Transformers.aliasToBean(StudentShortModel.class));

        return q.list();
    }

    /**
     * Обноаляем показатель КЦП по коду и году
     *
     * @param kcpFullModel
     */
    public int updateKCPModel(KCPFullModel kcpFullModel) {
        begin();
        String query = "UPDATE curriculum SET " +
                "kcp_dogovor=" + kcpFullModel.getKcpDogovor() + ",\n" +
                "kcp_budget=" + kcpFullModel.getKcpBudget() + "\n" +
                "WHERE id_curriculum in (SELECT cui.id_curriculum\n" +
                "   FROM curriculum cui\n" +
                "   INNER JOIN direction dii ON dii.id_direction = cui.id_direction\n" +
                "   INNER JOIN schoolyear syi ON cui.enter_school_year = syi.id_schoolyear\n" +
                "   WHERE cui.directioncode = '" + kcpFullModel.getDirectioncode() + "'\n" +
                "   AND date_part('year',syi.dateofbegin) = '" + kcpFullModel.getStartyear() + "'\n" +
                ")";
        Query q = getSession().createSQLQuery(query);
        int res = q.executeUpdate();
        commit();
        return res;
    }
}
