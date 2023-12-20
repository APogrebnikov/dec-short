package org.edec.utility.report.manager;

import org.edec.dao.DAO;
import org.edec.utility.report.model.eok.SubjectModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.LongType;

import java.util.Collections;
import java.util.List;


public class EokReportDAO extends DAO {
    public List<SubjectModel> getEokModels (Long idSem) {
        String query = "SELECT\n" + "\tDISTINCT ON(LGS.course, CUR.qualification, DG.groupname, DS.subjectname)\n" +
                       "\tCAST(tempSSS.count AS INTEGER) AS countStudent, \n" +
                       "\tLGSS.id_esocourse2 AS idEsoCourse, DG.groupname, CUR.specialitytitle AS speciallity, DS.subjectname, ESO.fullname AS eokName,\n" +
                       "\tSPLIT_PART(STRING_AGG(DEP.fulltitle, 'xyz,'), 'xyz,', 1) AS department,\n" +
                       "\tS.is_exam = 1 AS exam, S.is_pass = 1 AS pass, S.is_courseproject = 1 AS cp, S.is_coursework = 1 AS cw, S.is_practic = 1 AS practic,\n" +
                       "\tSTRING_AGG(HF.family||' '||SUBSTRING(HF.name FROM 1 FOR 1)||'.'||SUBSTRING(HF.patronymic FROM 1 FOR 1)||'.',', ') AS teacher,\n" +
                       "\tLGS.course||' курс' AS courseName\n" + "FROM\n" + "\tlink_group_semester LGS\n" +
                       "\tINNER JOIN (SELECT id_link_group_semester, COUNT(*) AS count FROM student_semester_status GROUP BY id_link_group_semester) AS tempSSS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                       "\tLEFT JOIN chair C USING (id_chair)\n" +
                       "\tLEFT JOIN department DEP ON C.id_chair = DEP.id_chair AND DEP.is_main = TRUE\n" +
                       "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                       "\tINNER JOIN curriculum CUR ON DG.id_curriculum = CUR.id_curriculum \n" +
                       "\tLEFT JOIN esocourse2 ESO USING (id_esocourse2)\n" +
                       "\tLEFT JOIN link_employee_subject_group LESG USING (id_link_group_semester_subject)\n" +
                       "\tLEFT JOIN employee EMP USING (id_employee)\n" + "\tLEFT JOIN humanface HF USING (id_humanface)\n" + "WHERE\n" +
                       "\tLGS.id_semester = :idSem\n" + "GROUP BY\n" +
                       "\tLGSS.id_link_group_semester_subject, DG.id_dic_group, CUR.id_curriculum, DS.id_dic_subject, S.id_subject, LGS.id_link_group_semester, tempSSS.count, ESO.id_esocourse2\n" +
                       "ORDER BY\n" + "\tLGS.course, CUR.qualification, DG.groupname, DS.subjectname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("countStudent")
                              .addScalar("idEsoCourse", LongType.INSTANCE)
                              .addScalar("groupname")
                              .addScalar("courseName")
                              .addScalar("speciallity")
                              .addScalar("subjectname")
                              .addScalar("eokName")
                              .addScalar("department")
                              .addScalar("teacher")
                              .addScalar("exam")
                              .addScalar("pass")
                              .addScalar("cp")
                              .addScalar("cw")
                              .addScalar("practic")
                              .setResultTransformer(Transformers.aliasToBean(SubjectModel.class));
        q.setLong("idSem", idSem);
        return (List<SubjectModel>) getList(q);
    }

    public List<SubjectModel> getProgressByWeek (Long idSem) {
        String query = "SELECT SU.id_subject, DS.subjectname, DG.groupname, LGS.course, SE.date_synch as synchProgressDate, EC.fullname, \n" +
                "'https://e.sfu-kras.ru/course/view.php?id='||EC.id_esocourse2 AS url, round(cast((AVG(SE.performance)/sr.esogrademax) as  numeric),4) as progress," +
                " sr.esogrademax as esogrademax\n" +
                "FROM sessionrating_efficiency SE\n" +
                "INNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "INNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "INNER JOIN studentcard SC USING (id_studentcard)\n" +
                "INNER JOIN humanface HF USING (id_humanface)\n" +
                "INNER JOIN subject SU USING (id_subject)\n" +
                "INNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "INNER JOIN link_group_semester_subject LGSS USING (id_subject)\n" +
                "INNER JOIN esocourse2 EC USING (id_esocourse2)\n" +
                "INNER JOIN link_group_semester LGS ON LGSS.id_link_group_semester = LGS.id_link_group_semester\n" +
                "INNER JOIN dic_group DG USING (id_dic_group)\n" +
                "WHERE LGS.id_semester= " + idSem +
                " GROUP BY SU.id_subject, DS.subjectname, DG.groupname, LGS.course, SE.date_synch, EC.fullname, EC.id_esocourse2, sr.esogrademax\n" +
                "ORDER BY su.id_subject, se.date_synch";
        Query q = getSession().createSQLQuery(query).addScalar("synchProgressDate")
                .addScalar("progress", DoubleType.INSTANCE)
                .addScalar("groupname")
                .addScalar("subjectname").setResultTransformer(Transformers.aliasToBean(SubjectModel.class));
        return q.list();
    }
}
