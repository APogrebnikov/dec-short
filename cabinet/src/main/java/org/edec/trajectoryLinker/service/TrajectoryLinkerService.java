package org.edec.trajectoryLinker.service;

import org.edec.trajectoryLinker.model.BlockModel;
import org.edec.trajectoryLinker.model.SubjectModel;
import org.edec.trajectoryLinker.model.TrajectoryModel;

import java.util.List;

public interface TrajectoryLinkerService {

    List<TrajectoryModel> getAllTrajectories ();
    List<SubjectModel> getAllSubjects ();
    List<SubjectModel> getSelectedTrajectorySubjects (long idTrajectory);
    boolean updateTrajectory (long idTrajectory, SubjectModel newSubject);
    boolean deleteSubject (long idTrajectorySubjectLink);

    List<SubjectModel> filterAllSubjectsList (List<SubjectModel> linkedSubjects, List<SubjectModel> allSubjects);

    List<BlockModel> groupSubjectsByBlocks (List<SubjectModel> subjects);

    boolean updateTrajectorySubject (long idTrajectory, List<SubjectModel> trajectorySubjects, List<SubjectModel> selectedSubjects);
}
