package org.edec.student.curriculum.manager;

import org.edec.dao.DAO;
import org.edec.student.curriculum.model.SubjectCurriculumModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;

public class StudentsCurriculumManager extends DAO {

    public Long getOtheridStudentcard(Long idHumanface){
        String query = "select other_dbuid\n" +
                "from humanface hf\n" +
                "join studentcard sc using (id_humanface)\n" +
                "join student_semester_status sss using (id_studentcard)\n" +
                "join link_group_semester lgs using (id_link_group_semester)\n" +
                "join semester sem using (id_semester)\n" +
                "where " +
                "sc.id_current_dic_group = lgs.id_dic_group" +
                "AND id_humanface = "+idHumanface+" AND sem.is_current_sem = 1";

        Query q = getSession().createSQLQuery(query);
        return (Long) q.uniqueResult();
    }

    public List<SubjectCurriculumModel> getListSubjectFromISIT (Long idStudentcard){
        String query = "select distinct ds.subjectname, lgs.id_semester as idSem, lgs.semesternumber as semesterNumber, sub.id_subject as idSubject, sub.otherdbid as otherIdSubject,\n" +
                "dg.groupname as groupname, lgs.dateofbeginsemester, lgs.dateofendsemester,\n" +
                "case \n" +
                "when srh.is_exam = 1 then 'Экзамен'\n" +
                "when srh.is_pass = 1 then 'Зачет'\n" +
                "when srh.is_coursework = 1 then 'Курсовая работа'\n" +
                "when srh.is_courseproject = 1 then 'Курсовой проект'\n" +
                "when srh.is_practic = 1 then 'Практика'\n" +
                "end as foc, sr.examrating as examRating, sr.passrating as passRating, sr.courseworkrating as cwRating, \n" +
                "sr.courseprojectrating as cpRating, sr.practicrating as practicRting,\n" +
                "sr.is_exam as isExam, sr.is_pass as isPass, sr.is_coursework as isCw, sr.is_courseproject as isCp, sr.is_practic as isPractic\n" +
                "from subject sub\n" +
                "join dic_subject ds using (id_dic_subject)\n" +
                "left join sessionrating sr on sr.id_subject = sub.id_subject\n" +
                "left join sessionratinghistory srh on srh.id_sessionrating =sr.id_sessionrating\n" +
                "left join register reg  using (id_register)\n" +
                "left join  student_semester_status sss on sss.id_student_semester_status = sr.id_student_semester_status\n" +
                "left join link_group_semester lgs on lgs.id_link_group_semester = sss.id_link_group_semester\n" +
                "left join dic_group dg using (id_dic_group)\n" +
                "left join studentcard sc using (id_studentcard)\n" +
                "where sc.other_dbuid = "+idStudentcard+" and dg.id_dic_group = sc.id_current_dic_group\n" +
                "order by lgs.id_semester";

        Query q = getSession().createSQLQuery(query)
                .addScalar("subjectname")
                .addScalar("idSem", LongType.INSTANCE)
                .addScalar("idSubject", LongType.INSTANCE)
                .addScalar("otherIdSubject", LongType.INSTANCE)
                .addScalar("groupname")
                .addScalar("examRating")
                .addScalar("passRating")
                .addScalar("cwRating")
                .addScalar("cpRating")
                .addScalar("practicRting")
                .addScalar("isExam")
                .addScalar("isPass")
                .addScalar("isCw")
                .addScalar("isCp")
                .addScalar("isPractic")
                .addScalar("foc")
                .addScalar("semesterNumber")
                .addScalar("dateofbeginsemester")
                .addScalar("dateofendsemester")
                .setResultTransformer(Transformers.aliasToBean(SubjectCurriculumModel.class));

        return  q.list();
    }

    public Long getCurrentSem(int fos) {
        String query = "select id_semester \n" +
                "from semester\n" +
                "where is_current_sem = 1 and  formofstudy = "+fos+" and id_institute = 1";
        Query q = getSession().createSQLQuery(query);
        return (Long) q.uniqueResult();
    }

}
