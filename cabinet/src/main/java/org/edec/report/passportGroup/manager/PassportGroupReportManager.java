package org.edec.report.passportGroup.manager;

import org.edec.dao.DAO;
import org.edec.model.HumanfaceModel;
import org.edec.report.passportGroup.model.SubjectReportModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import java.util.List;

public class PassportGroupReportManager extends DAO {
    public List<SubjectReportModel> getSubjectsByLgs (Long idLGS) {
        String query = "SELECT  DS.subjectname, S.hourscount,\n" +
                       "    S.is_exam = 1 AS exam, S.is_pass = 1 AS pass, S.is_courseproject = 1 AS cp, S.is_coursework = 1 AS cw, S.is_practic = 1 AS practic\n" +
                       "FROM    link_group_semester LGS\n" + "    INNER JOIN dic_group DG USING (id_dic_group)\n" +
                       "    INNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester)\n" +
                       "    INNER JOIN subject S USING (id_subject)\n" + "    INNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                       "WHERE   LGS.id_link_group_semester = " + idLGS + "\n" + "ORDER BY subjectname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("subjectname")
                              .addScalar("hourscount")
                              .addScalar("exam")
                              .addScalar("pass")
                              .addScalar("cp")
                              .addScalar("cw")
                              .addScalar("practic")
                              .setResultTransformer(Transformers.aliasToBean(SubjectReportModel.class));
        return (List<SubjectReportModel>) getList(q);
    }

    public List<HumanfaceModel> getHumanfacesByIdLGS (Long idLGS, Long idDicGroup) {
        String query = "SELECT  HF.family, HF.name, HF.patronymic\n" + "FROM    link_group_semester LGS\n" +
                       "    INNER JOIN student_semester_status SSS USING (id_link_group_semester)\n" +
                       "    INNER JOIN studentcard SC USING (id_studentcard)\n" +
                       "    join dic_group dg on dg.id_dic_group = sc.id_current_dic_group\n" +
                "    INNER JOIN humanface HF USING (id_humanface)\n" +
                       "WHERE   LGS.id_link_group_semester = " + idLGS + "\n and dg.id_dic_group = '"+idDicGroup+"' ORDER BY family, name, patronymic";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("family")
                              .addScalar("name")
                              .addScalar("patronymic")
                              .setResultTransformer(Transformers.aliasToBean(HumanfaceModel.class));
        return (List<HumanfaceModel>) getList(q);
    }
}
