package org.edec.trajectory.service.impl;

import org.edec.commons.model.CurriculumModel;
import org.edec.commons.entity.dec.DicTrajectory;
import org.edec.commons.model.DirectionModel;
import org.edec.commons.model.SchoolYearModel;
import org.edec.trajectory.manager.TrajectoryDAO;
import org.edec.trajectory.model.dao.DirectionCurrModel;
import org.edec.trajectory.model.TrajectoryModel;
import org.edec.trajectory.service.TrajectoryService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class TrajectoryImpl implements TrajectoryService {
    private TrajectoryDAO trajectoryDAO = new TrajectoryDAO();

    @Override
    public List<DicTrajectory> getDicTrajectories (Long idDirection) {
        return trajectoryDAO.getDicTrajectoriesByDirection(idDirection);
    }

    @Override
    public List<DirectionModel> getDirectionBySchoolYear (Long idSchoolYear) {
        List<DirectionModel> directions = new ArrayList<>();
        for (DirectionCurrModel directionCurr : trajectoryDAO.getDirectionCurrModels(idSchoolYear)) {
            //Ищем направлени по idDirection. Если не находим, то создаем
            DirectionModel direction = directions.stream()
                                                 .filter(directionModel -> directionModel.getIdDirection()
                                                                                         .equals(directionCurr.getIdDirection()))
                                                 .findFirst()
                                                 .orElse(null);

            if (direction == null) {
                direction = new DirectionModel();
                direction.setIdDirection(directionCurr.getIdDirection());
                direction.setCode(directionCurr.getCodeDirection());
                direction.setTitle(directionCurr.getTitleDirection());
                directions.add(direction);
            }

            //Добавляем учебный план к направлению
            CurriculumModel curriculum = new CurriculumModel();
            curriculum.setId(directionCurr.getIdCurriculum());
            curriculum.setCurrentYear(directionCurr.getCurrentYear());
            curriculum.setCreatedYear(directionCurr.getCreatedYear());
            curriculum.setFormOfStudy(directionCurr.getFormOfStudy());
            curriculum.setSpecialityTitle(directionCurr.getSpecialityTitle());
            curriculum.setDirectionCode(directionCurr.getDirectionCode());
            curriculum.setPlanFileName(directionCurr.getPlanFileName());
            curriculum.setProgramCode(directionCurr.getProgramCode());
            curriculum.setQualificationCode(directionCurr.getQualificationCode());

            direction.getListCurriculum().add(curriculum);
        }

        return directions;
    }

    @Override
    public List<SchoolYearModel> getAllSchoolYears () {
        return trajectoryDAO.getSchoolYearModelsByExistedCurriculum();
    }

    @Override
    public List<TrajectoryModel> getTrajectoryByDirection (DirectionModel direction) {
        List<TrajectoryModel> result = new ArrayList<>();
        for (CurriculumModel curriculumModel : direction.getListCurriculum()) {
            for (TrajectoryModel trajectoryModel : trajectoryDAO.getTrajectoryByDirection(curriculumModel.getId())) {
                trajectoryModel.setCurriculum(curriculumModel);
                result.add(trajectoryModel);
            }
        }
        return result;
    }

    @Override
    public TrajectoryModel updateOrCreateTrajectory (TrajectoryModel trajectory) {
        return trajectoryDAO.saveOrUpdateTrajectory(trajectory);
    }

    @Override
    public DicTrajectory createDicTrajectory (DicTrajectory dicTrajectory) {
        Long id = (Long) trajectoryDAO.create(dicTrajectory);
        dicTrajectory.setId(id);
        return dicTrajectory;
    }
}
