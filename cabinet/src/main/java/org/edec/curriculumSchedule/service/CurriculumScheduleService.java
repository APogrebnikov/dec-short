package org.edec.curriculumSchedule.service;

import org.edec.curriculumSchedule.model.GroupModel;

import java.util.List;

public interface CurriculumScheduleService {

    List<GroupModel> getGroupsByFilter (long idSemester, int course, boolean isBachelor, boolean isMaster, boolean isEngineer);
    boolean updateSemesterGroupInformation (GroupModel group);
    boolean updateGroupInformation (GroupModel group);
}
