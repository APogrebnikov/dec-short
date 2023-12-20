package org.edec.trajectoryLinker.service.impl;

import org.edec.trajectoryLinker.manager.TrajectoryLinkerManager;
import org.edec.trajectoryLinker.model.BlockModel;
import org.edec.trajectoryLinker.model.SubjectModel;
import org.edec.trajectoryLinker.model.TrajectoryModel;
import org.edec.trajectoryLinker.service.TrajectoryLinkerService;

import java.util.ArrayList;
import java.util.List;

public class TrajectoryLinkerServiceImpl implements TrajectoryLinkerService {

    TrajectoryLinkerManager manager = new TrajectoryLinkerManager();

    @Override
    public List<TrajectoryModel> getAllTrajectories () {
        return manager.getAllTrajectories();
    }

    @Override
    public List<SubjectModel> getAllSubjects () {
        return manager.getAllSubjects();
    }

    @Override
    public List<SubjectModel> getSelectedTrajectorySubjects (long idTrajectory) {
        return manager.getSelectedTrajectorySubjects(idTrajectory);
    }

    @Override
    public boolean updateTrajectory (long idTrajectory, SubjectModel newSubject) {
        return manager.linkTrajectorySubject(idTrajectory, newSubject.getIdSubject());
    }

    @Override
    public boolean deleteSubject (long idTrajectorySubjectLink) {
        return manager.deleteSubject(idTrajectorySubjectLink);
    }

    @Override
    public List<SubjectModel> filterAllSubjectsList (List<SubjectModel> linkedSubjects, List<SubjectModel> allSubjects) {

        for (SubjectModel subject : linkedSubjects) {
            for (int i = 0; i < allSubjects.size(); i++) {
                if (subject.getSubjectName().equals(allSubjects.get(i).getSubjectName())) {
                    allSubjects.get(i).setLinked(true);
                }
            }
        }

        return allSubjects;
    }

    @Override
    public List<BlockModel> groupSubjectsByBlocks (List<SubjectModel> subjects) {

        List<BlockModel> list = new ArrayList<>();

        if (subjects.size() == 0) {
            return list;
        }

        String prevBlockName = subjects.get(0).getSubjectCurBlock();
        BlockModel block = new BlockModel();
        block.setBlockName(subjects.get(0).getSubjectCurBlock());

        for (SubjectModel subject : subjects) {
            if (prevBlockName.equals(subject.getSubjectCurBlock())) {
                block.getSubjects().add(subject);
                if (subject.getLinked()) {
                    block.setSubjectWasLinked(true);
                }
            } else {
                list.add(block);
                block = new BlockModel();
                if (subject.getLinked()) {
                    block.setSubjectWasLinked(true);
                }
                block.setBlockName(subject.getSubjectCurBlock());
                block.getSubjects().add(subject);
                prevBlockName = subject.getSubjectCurBlock();
            }
        }

        list.add(block);

        return list;
    }

    @Override
    public boolean updateTrajectorySubject (long idTrajectory, List<SubjectModel> trajectorySubjects, List<SubjectModel> selectedSubjects) {
        //Проверяем нужно ли удалять предмет из траектории
        for (SubjectModel subject : trajectorySubjects) {
            boolean isContained = false;
            for (SubjectModel linkedSubject : selectedSubjects) {
                if (subject.getIdSubject().equals(linkedSubject.getIdSubject())) {
                    isContained = true;
                    break;
                }
            }
            if (!isContained) {
                if (!deleteSubject(subject.getIdCurSubjectTrajectory())) {
                    return false;
                }
                break;
            }
        }

        //Проверяем какой предмет нужно добавить в траекторию
        for (SubjectModel linkedSubject : selectedSubjects) {
            boolean isContained = false;
            for (SubjectModel subject : trajectorySubjects) {
                if (linkedSubject.getIdSubject().equals(subject.getIdSubject())) {
                    isContained = true;
                    break;
                }
            }
            if (!isContained) {
                if (!updateTrajectory(idTrajectory, linkedSubject)) {
                    return false;
                }
                break;
            }
        }

        return true;
    }
}
