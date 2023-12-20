package org.edec.synchroMine.manager.subjectSynch;

import org.edec.curriculumScan.model.Subject;
import org.edec.dao.DAO;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.FloatType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import java.util.List;


public class EntityManagerSubjectESO extends DAO {

    // TODO: Изменить входные параметры на Имя группы + Номер серестра (если null - то все)
    public List<Subject> getSubjectsByLGS(String groupName, Integer semesterNumber) {
        String semNumberCondition = semesterNumber != null ? "AND LGS.semesternumber = "+semesterNumber : "";

        String query =
                "SELECT\n" +
                        "\tS.id_subject AS id,\n" +
                        "\tDS.subjectname AS name, \n" +
                        "\tS.id_dic_subject AS idDicSubject,\n" +
                        "\tLGS.semesternumber AS semesterNumber,\n" +
                        "\tS.hourslection AS lecHours,\n" +
                        "\tS.hourslabor AS labHours,\n" +
                        "\tS.hourspractic AS praHours,\n" +
                        "\tS.subjectcode AS code,\n" +
                        "\tS.hourscount AS allHours,\n" +
                        "\tS.is_exam AS isExam,\n" +
                        "\tS.is_pass AS isPass,\n" +
                        "\tS.is_courseproject AS isCP,\n" +
                        "\tS.is_coursework AS isCW,\n" +
                        "\tCONCAT(\n" +
                        "\tCASE WHEN SE.season = 0 THEN 'осень' ELSE 'весна' END,\n" +
                        "\t' ',\n" +
                        "\tdate_part('year',SY.dateofbegin),\n" +
                        "\t'/',\n" +
                        "\tdate_part('year',SY.dateofend)\n" +
                        "\t) AS season\n" +
                "FROM\n" +
                        "\tlink_group_semester_subject LGSS\n" +
                        "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                        "\tINNER JOIN subject S USING (id_subject)\n" +
                        "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                        "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                        "\tINNER JOIN semester SE USING (id_semester)\n" +
                        "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +

                "WHERE \n" +
                        "\tDG.groupname = :groupname "+semNumberCondition+"\n" +
                "ORDER BY subjectname";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("name")
                .addScalar("idDicSubject", LongType.INSTANCE)
                .addScalar("semesterNumber", IntegerType.INSTANCE)
                .addScalar("lecHours", FloatType.INSTANCE)
                .addScalar("labHours", FloatType.INSTANCE)
                .addScalar("praHours", FloatType.INSTANCE)
                .addScalar("code")
                .addScalar("allHours", FloatType.INSTANCE)
                .addScalar("isExam", BooleanType.INSTANCE)
                .addScalar("isPass", BooleanType.INSTANCE)
                .addScalar("isCP", BooleanType.INSTANCE)
                .addScalar("isCW", BooleanType.INSTANCE)
                .addScalar("season")
                .setResultTransformer(Transformers.aliasToBean(Subject.class));
        q.setParameter("groupname", groupName);
        return (List<Subject>) getList(q);
    }
}