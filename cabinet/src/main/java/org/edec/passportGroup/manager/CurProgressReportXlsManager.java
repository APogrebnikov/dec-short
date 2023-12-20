package org.edec.passportGroup.manager;

import org.edec.dao.DAO;
import org.edec.passportGroup.model.passportGroupReport.curProgressXls.CurProgressGroupModel;
import org.edec.passportGroup.model.passportGroupReport.curProgressXls.CurProgressStudentModel;
import org.edec.passportGroup.model.passportGroupReport.curProgressXls.CurProgressSubjectModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;

public class CurProgressReportXlsManager extends DAO {
    public List<CurProgressGroupModel> getGroups (List<Long> idLgsList) {
        String query = "select dg.groupname as groupname,  id_link_group_semester as idLgs \n" +
                "from dic_group dg\n" +
                "join link_group_semester using (id_dic_group)\n" +
                "where id_link_group_semester in (:listIdLgs)";
        Query q  = getSession().createSQLQuery(query)
                .addScalar("groupname").addScalar("idLgs", LongType.INSTANCE)
                .setParameterList("listIdLgs", idLgsList)
                .setResultTransformer(Transformers.aliasToBean(CurProgressGroupModel.class));
        return q.list();
    }

    public List<CurProgressStudentModel> getStudentsByGroup (Long idLgs) {
        String query = "select hf.family||' '||hf.name||' '||hf.patronymic as fioStudent, sss.id_student_semester_status as idSSS \n" +
                "from humanface hf\n" +
                "join studentcard sc using (id_humanface)\n" +
                "join student_semester_status sss using (id_studentcard)\n" +
                "join link_group_semester lgs using (id_link_group_semester)\n" +
                "where lgs.id_link_group_semester = " + idLgs +
                " order by hf.family";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fioStudent").addScalar("idSSS", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(CurProgressStudentModel.class));
        return q.list();
    }

    public List<CurProgressSubjectModel> getSubjects (Long idLgs, Long idSSS) {
        String query = "with attendanceStudent as (SELECT HF.family||' '||SUBSTRING(HF.name, 1, 1)||'. '||SUBSTRING(HF.patronymic, 1, 1)||'.' AS fio, DS.subjectname,   count(at.attend ) as counAttend, s.id_subject, \n" +
                "lgss.id_link_group_semester_subject as idLgss\n" +
                "FROM humanface HF\n" +
                "INNER JOIN studentcard SC USING (id_humanface)\n" +
                "INNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "INNER JOIN attendance AT USING (id_student_semester_status)\n" +
                "right  JOIN link_group_semester_subject LGSS USING (id_link_group_semester_subject)\n" +
                "right JOIN subject S USING (id_subject)\n" +
                "right JOIN dic_subject DS USING (id_dic_subject)\n" +
                "WHERE\n" +
                "SSS.id_link_group_semester = "+idLgs+" and sss.id_student_semester_status = "+idSSS+"  and at.attend = 0\n" +
                "group by  HF.family, HF.name, HF.patronymic, DS.subjectname ,  s.id_subject, lgss.id_link_group_semester_subject\n" +
                "order by HF.family) \n" +
                "\n" +
                "SELECT   DS.subjectname, CAST((esogradecurrent/greatest(esogrademax, 1))*100 AS INTEGER) AS progress, coalesce(attendanceStudent.counAttend, 0) as attend\n" +
                "FROM\n" +
                "humanface HF\n" +
                "INNER JOIN studentcard SC USING (id_humanface)\n" +
                "INNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "INNER JOIN sessionrating SR on sr.id_student_semester_status = sss.id_student_semester_status\n" +
                "INNER JOIN subject S USING (id_subject)\n" +
                "INNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "left join attendanceStudent  on attendanceStudent.id_subject = s.id_subject\n" +
                "WHERE SSS.id_link_group_semester =  "+idLgs+" and sss.id_student_semester_status = " + idSSS +
                " group by  HF.family, HF.name, HF.patronymic, DS.subjectname,  esogradecurrent,esogrademax, attendanceStudent.counAttend \n" +
                "order by subjectname";

        Query q = getSession().createSQLQuery(query)
                .addScalar("subjectname")
                .addScalar("progress")
                .addScalar("attend", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(CurProgressSubjectModel.class));

        return q.list();
    }

}
