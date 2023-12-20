package org.edec.subjectsAnalysis.manager;

import org.edec.dao.DAO;
import org.edec.subjectsAnalysis.model.SubjectsAnalysisModel;
import org.edec.subjectsAnalysis.model.SubjectsInfoModel;
import org.edec.subjectsAnalysis.model.SubjectsRetakeCountModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;

import java.util.List;

public class SubjectsAnalysisManager extends DAO {

    public List<SubjectsAnalysisModel> getExamsSubjects(Long idSem, Integer course) {
//        String query = "with rating as (select subjectname,\n" +
//                "                       s.id_chair    as chair,\n" +
//                "                       srh.newrating as newrating,\n" +
//                "                       count(*)      as countRating\n" +
//                "                from subject s\n" +
//                "                         join link_group_semester_subject lgss on lgss.id_subject = s.id_subject\n" +
//                "                         join link_group_semester lgs using (id_link_group_semester)\n" +
//                "                         join sessionrating sr on sr.id_subject = s.id_subject\n" +
//                "                         join sessionratinghistory srh using (id_sessionrating)\n" +
//                "                         join dic_subject ds using (id_dic_subject)\n" +
//                "                where lgs.id_semester = " + idSem + "\n" +
//                "                  and srh.is_exam = 1\n" +
//                "                group by subjectname, srh.newrating, chair\n" +
//                "                order by subjectname, srh.newrating, chair desc)\n" +
//                "select distinct subjectname,\n" +
//                "       sum(case when newrating = 5 then countRating else 0 end) over (partition by subjectname)  as fiveCount,\n" +
//                "       sum(case when newrating = 4 then countRating else 0 end) over (partition by subjectname)  as fourCount,\n" +
//                "       sum(case when newrating = 3 then countRating else 0 end) over (partition by subjectname)  as threeCount,\n" +
//                "       sum(case when newrating = 2 then countRating else 0 end) over (partition by subjectname)  as twoCount,\n" +
//                "       sum(case when newrating = -3 then countRating else 0 end) over (partition by subjectname) as absenceCount,\n" +
//                "       max(chair)                                                as idChair\n" +
//                "from rating\n" +
//                "group by subjectname, newrating, countRating\n" +
//                "order by subjectname;";
        String query = "with rating as (select subject2.subjectname,\n" +
                "                       s6.id_chair   as chair,\n" +
                "                       srh.newrating as newrating,\n" +
                "                       count(*)      as countRating\n" +
                "                from humanface\n" +
                "                         inner join studentcard sc on humanface.id_humanface = sc.id_humanface\n" +
                "                         inner join student_semester_status as sss\n" +
                "                                    on sc.id_studentcard = sss.id_studentcard\n" +
                "                         inner join link_group_semester semester\n" +
                "                                    on sss.id_link_group_semester = semester.id_link_group_semester\n" +
                "                         inner join sessionrating s2\n" +
                "                                    on sss.id_student_semester_status =\n" +
                "                                       s2.id_student_semester_status\n" +
                "                         inner join sessionratinghistory srh on s2.id_sessionrating = srh.id_sessionrating\n" +
                "                         inner join link_group_semester s3\n" +
                "                                    on sss.id_link_group_semester = s3.id_link_group_semester\n" +
                "                         inner join semester s4 on s3.id_semester = s4.id_semester\n" +
                "                         inner join schoolyear s5 on s4.id_schoolyear = s5.id_schoolyear\n" +
                "                         inner join subject s6 on s2.id_subject = s6.id_subject\n" +
                "                         inner join dic_subject subject2 on s6.id_dic_subject = subject2.id_dic_subject\n" +
                "                where semester.id_semester = " + idSem + "\n" +
                "                  and srh.is_exam = 1\n" +
                "                  and sss.is_deducted <> 1\n" +
                "                group by subjectname, srh.newrating, chair\n" +
                "                order by subjectname, srh.newrating, chair desc)\n" +
                "select distinct subjectname,\n" +
                "                sum(case when newrating = 5 then countRating else 0 end) over (partition by subjectname) as fiveCount,\n" +
                "                sum(case when newrating = 4 then countRating else 0 end) over (partition by subjectname) as fourCount,\n" +
                "                sum(case when newrating = 3 then countRating else 0 end) over (partition by subjectname) as threeCount,\n" +
                "                sum(case when newrating = 2 then countRating else 0 end) over (partition by subjectname) as twoCount,\n" +
                "                sum(case when newrating = -3 then countRating else 0 end)\n" +
                "                over (partition by subjectname)                                                          as absenceCount,\n" +
                "                sum(countRating) over (partition by subjectname)                                         as moda,\n" +
                "                max(chair)                                                                               as idChair\n" +
                "from rating\n" +
                "group by rating.subjectname, rating.newrating, rating.countRating\n" +
                "order by rating.subjectname;";
        Query q = getSession().createSQLQuery(query)
                .addScalar("subjectname")
                .addScalar("fiveCount", DoubleType.INSTANCE)
                .addScalar("fourCount", DoubleType.INSTANCE)
                .addScalar("threeCount", DoubleType.INSTANCE)
                .addScalar("twoCount", DoubleType.INSTANCE)
                .addScalar("absenceCount", DoubleType.INSTANCE)
                .addScalar("idChair", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(SubjectsAnalysisModel.class));

        return q.list();
    }

    public List<SubjectsRetakeCountModel> getPartRetake(Long idSem, int retakeCount, int foc) {
        String retakes = (retakeCount == 2 ? " 2 " : " 3,4,5 ");
//        String query = "with students as (select count(*) as allstudents, subjectname \n" +
//                "from subject s\n" +
//                "join dic_subject ds using (id_dic_subject)\n" +
//                "join link_group_semester_subject lgss on lgss.id_subject = s.id_subject\n" +
//                "join link_group_semester lgs on lgs.id_link_group_semester = lgss.id_link_group_semester\n" +
//                "join student_semester_status sss  on lgs.id_link_group_semester = sss.id_link_group_semester\n" +
//                "where  sss.is_deducted <>1 and subjectname = '"+subjectname+"' " +
//                "and lgs.id_semester = "+idSem+ (foc == 1 ? " and lgss.examdate is not null " : " and lgss.passdate is not null ") +
//                "group by subjectname)\n" +
//                "select cast(count(id_sessionratinghistory) as float) / cast(allstudents as float)*100 as retake\n" +
//                "from subject s\n" +
//                "join link_group_semester_subject lgss on lgss.id_subject = s.id_subject\n" +
//                "join link_group_semester lgs on lgs.id_link_group_semester = lgss.id_link_group_semester\n" +
//                "join sessionrating sr on sr.id_subject = s.id_subject\n" +
//                "join sessionratinghistory srh using (id_sessionrating)\n" +
//                "join dic_subject ds using (id_dic_subject)\n" +
//                "join students st on st.subjectname = ds.subjectname\n" +
//                "where ds.subjectname = '"+subjectname+"' and lgs.id_semester = "+idSem+" and srh.retake_count in ("+ retakes +") "+
//                (foc == 1 ? "  and sr.is_exam = 1 " : " and sr.is_pass = 1 ") +
//                " group by allstudents";

//        String query = "select count(*) as retake\n" +
//                "                  from subject s\n" +
//                "                           join dic_subject ds using (id_dic_subject)\n" +
//                "                           join link_group_semester_subject lgss on lgss.id_subject = s.id_subject\n" +
//                "                           join link_group_semester lgs on lgs.id_link_group_semester = lgss.id_link_group_semester\n" +
//                "                           join sessionrating sr on sr.id_subject = s.id_subject\n" +
//                "                           join sessionratinghistory srh using (id_sessionrating)\n" +
//                "                  where ds.subjectname = '" + subjectname + "'\n" +
//                "                    and lgs.id_semester = " + idSem + "\n" +
//                "                    and srh.retake_count in (" + retakes + ")\n" +
//                                     (foc == 1 ? " and lgss.examdate is not null " : " and lgss.passdate is not null ") + "\n" +
//                                     (foc == 1 ? " and sr.is_exam = 1 " : " and sr.is_pass = 1 ");

        String query = "select subject2.subjectname, count(*)\n" +
                "from humanface\n" +
                "         join studentcard sc on humanface.id_humanface = sc.id_humanface\n" +
                "         join student_semester_status as sss\n" +
                "              on sc.id_studentcard = sss.id_studentcard\n" +
                "         join link_group_semester semester\n" +
                "              on sss.id_link_group_semester = semester.id_link_group_semester\n" +
                "         join sessionrating s2\n" +
                "              on sss.id_student_semester_status =\n" +
                "                 s2.id_student_semester_status\n" +
                "         join sessionratinghistory srh on s2.id_sessionrating = srh.id_sessionrating\n" +
                "         join link_group_semester s3\n" +
                "              on sss.id_link_group_semester = s3.id_link_group_semester\n" +
                "         join semester s4 on s3.id_semester = s4.id_semester\n" +
                "         join schoolyear s5 on s4.id_schoolyear = s5.id_schoolyear\n" +
                "         join subject s6 on s2.id_subject = s6.id_subject\n" +
                "         join dic_subject subject2 on s6.id_dic_subject = subject2.id_dic_subject\n" +
                "where semester.id_semester = " + idSem + "\n" +
                (foc == 1 ? " and srh.is_exam = 1 " : " and srh.is_pass = 1 ") +
                "  and sss.is_deducted <> 1\n" +
                "  and srh.retake_count in (" + retakes + ")\n" +
                "group by subject2.subjectname\n" +
                "order by subject2.subjectname";
        Query q = getSession()
                .createSQLQuery(query)
                .addScalar("subjectname")
                .addScalar("count", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(SubjectsRetakeCountModel.class));

        return q.list();
    }

    public List<SubjectsAnalysisModel> getPassSubjects(Long idSem, Integer course) {
        String query = "with rating as (select subject2.subjectname,\n" +
                "                       s6.id_chair   as chair,\n" +
                "                       srh.newrating as newrating,\n" +
                "                       count(*)      as countRating\n" +
                "                from humanface\n" +
                "                         join studentcard sc on humanface.id_humanface = sc.id_humanface\n" +
                "                         join student_semester_status as sss\n" +
                "                              on sc.id_studentcard = sss.id_studentcard\n" +
                "                         join link_group_semester semester\n" +
                "                              on sss.id_link_group_semester = semester.id_link_group_semester\n" +
                "                         join sessionrating s2\n" +
                "                              on sss.id_student_semester_status =\n" +
                "                                 s2.id_student_semester_status\n" +
                "                         join sessionratinghistory srh on s2.id_sessionrating = srh.id_sessionrating\n" +
                "                         join link_group_semester s3\n" +
                "                              on sss.id_link_group_semester = s3.id_link_group_semester\n" +
                "                         join semester s4 on s3.id_semester = s4.id_semester\n" +
                "                         join schoolyear s5 on s4.id_schoolyear = s5.id_schoolyear\n" +
                "                         join subject s6 on s2.id_subject = s6.id_subject\n" +
                "                         join dic_subject subject2 on s6.id_dic_subject = subject2.id_dic_subject\n" +
                "                where semester.id_semester = " + idSem + "\n" +
                "                  and srh.is_pass = 1\n" +
                "                  and sss.is_deducted <> 1\n" +
                (course != 0 ? " and semester.course = " + course + "\n" : "") +
                "                group by subjectname, srh.newrating, chair\n" +
                "                order by subjectname, srh.newrating, chair desc)\n" +
                "select distinct subjectname,\n" +
                "                sum(case when newrating = 1 then countRating else 0 end) over (partition by subjectname)  as passCount,\n" +
                "                sum(case when newrating = -2 then countRating else 0 end)\n" +
                "                over (partition by subjectname)                                                           as notPassCount,\n" +
                "                sum(case when newrating = -3 then countRating else 0 end)\n" +
                "                over (partition by subjectname)                                                           as absenceCount,\n" +
                "                sum(case when newrating = 3 then countRating else 0 end)\n" +
                "                over (partition by subjectname)                                                           as passCountThree,\n" +
                "                sum(case when newrating = 4 then countRating else 0 end)\n" +
                "                over (partition by subjectname)                                                           as passCountFour,\n" +
                "                sum(case when newrating = 5 then countRating else 0 end)\n" +
                "                over (partition by subjectname)                                                           as passCountFive,\n" +
                "                max(chair)                                                                                as idChair\n" +
                "from rating\n" +
                "group by rating.subjectname, rating.newrating, rating.countRating\n" +
                "order by rating.subjectname;";
        Query q = getSession().createSQLQuery(query)
                .addScalar("subjectname")
                .addScalar("passCount", DoubleType.INSTANCE)
                .addScalar("notPassCount", DoubleType.INSTANCE)
                .addScalar("absenceCount", DoubleType.INSTANCE)
                .addScalar("passCountThree", DoubleType.INSTANCE)
                .addScalar("passCountFour", DoubleType.INSTANCE)
                .addScalar("passCountFive", DoubleType.INSTANCE)
                .addScalar("idChair", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(SubjectsAnalysisModel.class));

        return q.list();
    }


    public List<SubjectsInfoModel> getTeacherAndGroup(String subjectname, Long idSem, Integer course, Integer foc, Integer idChair) {
        String query = "select distinct subjectname, string_agg(hf.family||' '||hf.name||' '||hf.patronymic, ', ') over (partition by groupname) as fio, groupname, dep.fulltitle as chair\n" +
                "from humanface hf\n" +
                "join employee em using (id_humanface)\n" +
                "join link_employee_subject_group lesg using (id_employee)\n" +
                "join link_group_semester_subject lgss using (id_link_group_semester_subject)\n" +
                "join link_group_semester lgs using (id_link_group_semester)\n" +
                "join dic_group dg using (id_dic_group)\n" +
                "join subject s using (id_subject)\n" +
                "join department dep using (id_chair)\n" +
                "join dic_subject ds using (id_dic_subject)\n" +
                "where ds.subjectname = '" + subjectname + "' and lgs.id_semester = " + idSem + (course != 0 ? " and lgs.course = " + course : " ") +
                (foc == 1 ? " and lgss.examdate is not null " : " and lgss.passdate is not null ") +
                "and dep.shorttitle is not null and s.id_chair = " + idChair + " order by groupname";

        Query q = getSession().createSQLQuery(query).setResultTransformer(Transformers.aliasToBean(SubjectsInfoModel.class));
        return q.list();

    }
}
