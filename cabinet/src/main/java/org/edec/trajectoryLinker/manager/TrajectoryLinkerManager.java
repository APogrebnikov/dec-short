package org.edec.trajectoryLinker.manager;

import org.edec.dao.DAO;
import org.edec.trajectoryLinker.model.SubjectModel;
import org.edec.trajectoryLinker.model.TrajectoryModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;

import java.util.List;

public class TrajectoryLinkerManager extends DAO {

    public List<TrajectoryModel> getAllTrajectories () {
        String query = "SELECT CT.id_trajectory as idTrajectory, CDT.name as trajectoryName" + "\n FROM curriculum_trajectory CT" +
                       "\n INNER JOIN curriculum_dic_trajectory CDT using (id_dic_trajectory)";

        return (List<TrajectoryModel>) getList(getSession().createSQLQuery(query)
                                                           .addScalar("idTrajectory", LongType.INSTANCE)
                                                           .addScalar("trajectoryName")
                                                           .setResultTransformer(Transformers.aliasToBean(TrajectoryModel.class)));
    }

    public List<SubjectModel> getAllSubjects () {
        String query = "\nSELECT CS.id_curriculum_subject AS idSubject,\n" + "DS.subjectname as subjectName,\n" +
                       "CS.semester_number as semesterNumber,\n" + "CB.name as subjectCurBlock\n" + "FROM curriculum_subject CS\n" +
                       "INNER JOIN dic_subject DS using(id_dic_subject)\n" + "INNER JOIN curriculum_block CB using(id_curriculum_block)\n" +
                       "INNER JOIN curriculum CUR using(id_curriculum)\n" +
//                       "WHERE CB.is_selectable = TRUE\n" +
                //                "AND (CS.semester_number = (date_part('year', now()) - EXTRACT(YEAR FROM CUR.enteryear)) * 2 + 1\n" +
                //                "\tOR CS.semester_number = (date_part('year', now()) - EXTRACT(YEAR FROM CUR.enteryear)) * 2 + 2)\n" +
                       "ORDER BY subjectCurBlock";

        return (List<SubjectModel>) getList(getSession().createSQLQuery(query)
                                                        .addScalar("idSubject", LongType.INSTANCE)
                                                        .addScalar("subjectName")
                                                        .addScalar("subjectCurBlock")
                                                        .addScalar("semesterNumber")
                                                        .setResultTransformer(Transformers.aliasToBean(SubjectModel.class)));
    }

    public List<SubjectModel> getSelectedTrajectorySubjects (long idTrajectory) {
        String query = "SELECT CTS.id_curriculum_trajectory_subject as idCurSubjectTrajectory," + "\n DS.subjectname as subjectName," +
                       "\n CS.id_curriculum_subject as idSubject," + "\nCS.semester_number as semesterNumber," +
                       "\n CB.name as subjectCurBlock" + "\n FROM curriculum_trajectory_subject CTS" +
                       "\n INNER JOIN curriculum_subject CS using(id_curriculum_subject)" +
                       "\n INNER JOIN dic_subject DS using(id_dic_subject)" +
                       "\n INNER JOIN curriculum_block CB using(id_curriculum_block)" + "\n WHERE id_trajectory = :idTrajectory";

        return (List<SubjectModel>) getList(getSession().createSQLQuery(query)
                                                        .addScalar("idSubject", LongType.INSTANCE)
                                                        .addScalar("subjectName")
                                                        .addScalar("idCurSubjectTrajectory", LongType.INSTANCE)
                                                        .addScalar("subjectCurBlock")
                                                        .addScalar("semesterNumber")
                                                        .setParameter("idTrajectory", idTrajectory)
                                                        .setResultTransformer(Transformers.aliasToBean(SubjectModel.class)));
    }

    public boolean linkTrajectorySubject (long idTrajectory, long idSubject) {
        Query q = getSession().createSQLQuery(
                "INSERT INTO curriculum_trajectory_subject (id_trajectory, id_curriculum_subject)\n" + "VALUES (:idTrajectory, :idSubject)")
                              .setParameter("idTrajectory", idTrajectory)
                              .setParameter("idSubject", idSubject);

        return executeUpdate(q);
    }

    public boolean deleteSubject (long idTrajectorySubject) {
        String query = "DELETE FROM\n" + "\tcurriculum_trajectory_subject\n" + "WHERE\n" +
                       "\tid_curriculum_trajectory_subject = :idTrajectorySubject";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idTrajectorySubject", idTrajectorySubject);

        return executeUpdate(q);
    }
}
