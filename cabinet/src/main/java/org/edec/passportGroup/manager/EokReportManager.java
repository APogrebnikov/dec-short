package org.edec.passportGroup.manager;

import org.edec.dao.DAO;
import org.edec.passportGroup.model.passportGroupReport.EokModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;

public class EokReportManager extends DAO {

    public List<EokModel> getListEokByChair (Long idSem, Long idChair){
        String query = "SELECT DISTINCT ON(LGS.course, CUR.qualification, DG.groupname, DS.subjectname) CAST(tempSSS.count AS INTEGER) AS countStudent,\n" +
                "  LGSS.id_esocourse2 AS idEsoCourse, DG.groupname, CUR.specialitytitle AS speciallity, DS.subjectname, ESO.fullname AS eokName,\n" +
                "  S.is_exam = 1 AS exam, S.is_pass = 1 AS pass, S.is_courseproject = 1 AS cp, S.is_coursework = 1 AS cw, S.is_practic = 1 AS practic,\n" +
                "  STRING_AGG(HF.family||' '||SUBSTRING(HF.name FROM 1 FOR 1)||'.'||SUBSTRING(HF.patronymic FROM 1 FOR 1)||'.',', ') AS teacher,\n" +
                "  LGS.course||' курс' AS courseName\n" +
                "FROM link_group_semester LGS\n" +
                "  INNER JOIN (SELECT id_link_group_semester, COUNT(*) AS count FROM student_semester_status GROUP BY id_link_group_semester) AS tempSSS USING (id_link_group_semester)\n" +
                "  INNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester)\n" +
                "    INNER JOIN subject S USING (id_subject)\n" +
                "    INNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "    LEFT JOIN chair C USING (id_chair)\n" +
                "    LEFT JOIN department DEP ON C.id_chair = DEP.id_chair AND DEP.is_main = TRUE\n" +
                "    INNER JOIN dic_group DG USING (id_dic_group)\n" +
                "    INNER JOIN curriculum CUR ON DG.id_curriculum = CUR.id_curriculum\n" +
                "    LEFT JOIN esocourse2 ESO USING (id_esocourse2)\n" +
                "    LEFT JOIN link_employee_subject_group LESG USING (id_link_group_semester_subject)\n" +
                "    LEFT JOIN employee EMP USING (id_employee)\n" +
                "    LEFT JOIN humanface HF USING (id_humanface)\n" +
                "WHERE LGS.id_semester = "+idSem+" and c.id_chair = "+idChair+
                " GROUP BY LGSS.id_link_group_semester_subject, DG.id_dic_group, CUR.id_curriculum, DS.id_dic_subject, S.id_subject, LGS.id_link_group_semester, tempSSS.count, ESO.id_esocourse2\n" +
                " ORDER BY LGS.course, CUR.qualification, DG.groupname, DS.subjectname\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("countStudent")
                .addScalar("idEsoCourse", LongType.INSTANCE)
                .addScalar("groupname")
                .addScalar("speciallity")
                .addScalar("subjectname")
                .addScalar("eokName")
                .addScalar("exam")
                .addScalar("pass")
                .addScalar("cp")
                .addScalar("cw")
                .addScalar("practic")
                .addScalar("teacher")
                .addScalar("courseName")
                .setResultTransformer(Transformers.aliasToBean(EokModel.class));

        return q.list();

    }
}
