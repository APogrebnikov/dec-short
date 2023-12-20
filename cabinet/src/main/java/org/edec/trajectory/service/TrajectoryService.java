package org.edec.trajectory.service;

import org.edec.commons.entity.dec.DicTrajectory;
import org.edec.commons.model.DirectionModel;
import org.edec.commons.model.SchoolYearModel;
import org.edec.trajectory.model.TrajectoryModel;

import java.util.List;

/**
 * @author Max Dimukhametov
 */
public interface TrajectoryService {
    List<DicTrajectory> getDicTrajectories (Long idDirection);

    List<DirectionModel> getDirectionBySchoolYear (Long idSchoolYear);

    List<SchoolYearModel> getAllSchoolYears ();

    List<TrajectoryModel> getTrajectoryByDirection (DirectionModel direction);

    TrajectoryModel updateOrCreateTrajectory (TrajectoryModel trajectory);

    DicTrajectory createDicTrajectory (DicTrajectory dicTrajectory);
}
