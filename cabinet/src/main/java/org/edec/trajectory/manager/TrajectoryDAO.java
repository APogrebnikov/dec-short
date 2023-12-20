package org.edec.trajectory.manager;

import org.edec.commons.entity.dec.DicTrajectory;
import org.edec.commons.model.SchoolYearModel;
import org.edec.dao.DAO;
import org.edec.trajectory.model.dao.DirectionCurrModel;
import org.edec.trajectory.model.TrajectoryModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class TrajectoryDAO extends DAO {

    public List<SchoolYearModel> getSchoolYearModelsByExistedCurriculum () {
        String query =
                "SELECT DISTINCT SY.id_schoolyear AS idSchoolYear, SY.dateofbegin, SY.dateofend, SY.otherdbid\n" + "FROM schoolyear SY\n" +
                "INNER JOIN curriculum CUR ON SY.id_schoolyear = CUR.created_school_year\n" + "ORDER BY SY.dateofbegin DESC";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idSchoolYear", LongType.INSTANCE)
                              .addScalar("dateOfBegin")
                              .addScalar("dateOfEnd")
                              .addScalar("otherdbid", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(SchoolYearModel.class));
        return (List<SchoolYearModel>) getList(q);
    }

    public List<DirectionCurrModel> getDirectionCurrModels (Long idSchoolYear) {
        String query = "SELECT DISTINCT DIR.id_direction AS idDirection, DIR.code AS codeDirection, DIR.title AS titleDirection,\n" +
                       "CUR.id_curriculum AS idCurriculum, CUR.specialityTitle, CUR.directionCode,\n" +
                       "CUR.programCode, CUR.qualificationCode, CUR.formOfStudy, CUR.planfilename AS planFileName,\n" +
                       "EXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend) AS createdYear,\n" +
                       "SY.current_year AS currentYear\n" + "FROM direction DIR\n" + "INNER JOIN curriculum CUR USING (id_direction)\n" +
                       "INNER JOIN schoolyear SY ON CUR.created_school_year = SY.id_schoolyear\n" +
                       "WHERE CUR.created_school_year = :idSchoolYear\n" + "ORDER BY titleDirection";
        return (List<DirectionCurrModel>) getList(getSession().createSQLQuery(query)
                                                              .addScalar("idDirection", LongType.INSTANCE)
                                                              .addScalar("codeDirection")
                                                              .addScalar("titleDirection")
                                                              .addScalar("idCurriculum", LongType.INSTANCE)
                                                              .addScalar("specialityTitle")
                                                              .addScalar("directionCode")
                                                              .addScalar("programCode")
                                                              .addScalar("qualificationCode")
                                                              .addScalar("formOfStudy")
                                                              .addScalar("createdYear")
                                                              .addScalar("planFileName")
                                                              .addScalar("currentYear")
                                                              .setResultTransformer(Transformers.aliasToBean(DirectionCurrModel.class))
                                                              .setParameter("idSchoolYear", idSchoolYear));
    }

    public List<DicTrajectory> getDicTrajectoriesByDirection (Long idDirection) {
        String query = "SELECT DISTINCT DTR.*\n" + "FROM curriculum_dic_trajectory DTR\n" +
                       "INNER JOIN curriculum_trajectory TR USING (id_dic_trajectory)\n" +
                       "INNER JOIN curriculum CUR USING (id_curriculum)\n" + "WHERE CUR.id_direction = :idDirection";
        return (List<DicTrajectory>) getList(
                getSession().createSQLQuery(query).addEntity(DicTrajectory.class).setParameter("idDirection", idDirection));
    }

    public List<TrajectoryModel> getTrajectoryByDirection (Long idCurriculum) {
        String query = "SELECT TR.current_year AS currentYear, TR.id_trajectory AS id, DTR.name,\n" +
                       "DTR.id_dic_trajectory AS idDicTrajectory, TR.id_curriculum AS idCurriculum\n" + "FROM curriculum_trajectory TR\n" +
                       "INNER JOIN curriculum_dic_trajectory DTR USING (id_dic_trajectory)\n" + "WHERE TR.id_curriculum = :idCurriculum\n" +
                       "ORDER BY TR.id_trajectory";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("currentYear")
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("name")
                              .addScalar("idDicTrajectory", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(TrajectoryModel.class))
                              .setParameter("idCurriculum", idCurriculum);
        return (List<TrajectoryModel>) getList(q);
    }

    public TrajectoryModel saveOrUpdateTrajectory (TrajectoryModel trajectory) {
        if (trajectory.getId() == null) {
            Query q = getSession().createSQLQuery("INSERT INTO curriculum_trajectory (id_dic_trajectory, id_curriculum, current_year)\n" +
                                                  "VALUES (:idDicTrajectory, :idCurriculum, :currentYear)\n" +
                                                  "RETURNING id_trajectory AS idTrajectory")
                                  .addScalar("idTrajectory", LongType.INSTANCE)
                                  .setParameter("idDicTrajectory", trajectory.getIdDicTrajectory())
                                  .setParameter("idCurriculum", trajectory.getCurriculum().getId())
                                  .setParameter("currentYear", trajectory.getCurrentYear());
            List<Long> list = (List<Long>) getList(q);
            trajectory.setId(list.get(0));
        } else {
            executeUpdate(getSession().createSQLQuery("UPDATE curriculum_trajectory SET\n" +
                                                      "id_dic_trajectory = :idDicTrajectory, id_curriculum = :idCurriculum, current_year = :currentYear\n" +
                                                      "WHERE id_trajectory = :idTrajectory")
                                      .setParameter("idDicTrajectory", trajectory.getIdDicTrajectory())
                                      .setParameter("idCurriculum", trajectory.getCurriculum().getId())
                                      .setParameter("currentYear", trajectory.getCurrentYear())
                                      .setParameter("idTrajectory", trajectory.getId()));
        }
        return trajectory;
    }
}
