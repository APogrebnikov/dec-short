package org.edec.curriculumSchedule.service.iml;

import org.edec.curriculumSchedule.manager.CurriculumScheduleManager;
import org.edec.curriculumSchedule.model.GroupModel;
import org.edec.curriculumSchedule.service.CurriculumScheduleService;

import java.util.List;

public class CurriculumScheduleServiceESO implements CurriculumScheduleService {
    CurriculumScheduleManager manager = new CurriculumScheduleManager();

    @Override
    public List<GroupModel> getGroupsByFilter (long idSemester, int course, boolean isBachelor, boolean isMaster, boolean isEngineer) {
        return manager.getGroupsByFilter(idSemester, course, isBachelor, isMaster, isEngineer);
    }

    @Override
    public boolean updateSemesterGroupInformation (GroupModel group) {
        return manager.updateSemesterGroupInformation(group);
    }

    @Override
    public boolean updateGroupInformation (GroupModel group) {
        return manager.updateGroupInformation(group);
    }
}
