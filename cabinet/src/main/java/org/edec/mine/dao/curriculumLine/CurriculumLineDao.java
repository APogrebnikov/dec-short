package org.edec.mine.dao.curriculumLine;

import org.edec.cloud.sync.mine.api.curriculumLine.data.response.CurriculumLineResponse;
import org.edec.dao.DAO;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import java.util.List;

public class CurriculumLineDao extends DAO {


    public CurriculumLineResponse getSubjectByParams(String groupName, Integer course, Integer semesterNumber,
                                                     String subjectName) {

        String query = "select ds.subjectname as subjectName, s.subjectcode as subjectCode, s.is_facultative as isFacultative, s.practictype = 1 as isPracticeType,\n" +
                "       s.is_exam = 1 as exam, s.is_pass = 1 as pass, s.is_courseproject = 1 as cp, s.is_coursework = 1 as cw, s.is_practic = 1 as practic, s.type,\n" +
                "       s.hourslection as hoursLection, s.hourslabor as hoursLabor, s.hourspractic as hoursPractice, s.hoursaudcount as hoursAudCount, s.hourscount as hoursCount\n" +
                "from dic_group dg\n" +
                "  inner join link_group_semester lgs using (id_dic_group)\n" +
                "  inner join link_group_semester_subject lgss using (id_link_group_semester)\n" +
                "  inner join subject s using (id_subject)\n" +
                "  inner join dic_subject ds using (id_dic_subject)\n" +
                "where dg.groupname = '" + groupName + "'\n" +
                "    and lgs.course = " + course + "\n" +
                "    and lgs.semesternumber = " + semesterNumber + "\n" +
                "    and ds.subjectname = '" + subjectName + "'";
        Query q = getSession().createSQLQuery(query)
                .addScalar("subjectName").addScalar("subjectCode").addScalar("isFacultative").addScalar("isPracticeType")
                .addScalar("exam").addScalar("pass").addScalar("cp").addScalar("cw").addScalar("practic").addScalar("type")
                .addScalar("hoursLection").addScalar("hoursLabor").addScalar("hoursPractice").addScalar("hoursAudCount").addScalar("hoursCount")
                .setResultTransformer(Transformers.aliasToBean(CurriculumLineResponse.class));
        List<CurriculumLineResponse> list = (List<CurriculumLineResponse>) getList(q);
        return list.isEmpty() ? null : list.get(0);
    }

}
