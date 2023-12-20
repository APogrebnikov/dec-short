package org.edec.newMine.groups.service;

import org.edec.utility.component.model.SemesterModel;
import org.edec.newMine.groups.model.GroupsModel;
import org.edec.utility.constants.FormOfStudy;

import java.util.List;

public interface GroupsService {
    List<GroupsModel> getMineGroupsByInstSemesterAndFormOfStudy(Long id, SemesterModel semester, Integer formOfStudy);
    List<GroupsModel> getCabinetGroupsBySemester(Long idSem);

    boolean updateChair(Long idCurriculum, Integer idChairMine);
    boolean updatePlanFileName(Long idCurriculum, String planFileName);
    boolean updateGroup(Long idLGS, Integer idGroupMine);
    void createGroup(GroupsModel data, Long idInst, SemesterModel semester);
    void transferGroupsInAutumnSemester(SemesterModel semester);
}
