package org.edec.synchroMine.service;

import org.edec.model.SemesterModel;
import org.edec.synchroMine.model.GroupCompareResult;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.edec.utility.compare.compareResult.CompareResult;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.constants.FormOfStudy;

import java.util.List;

public interface GroupSyncService {

    List<CompareResult<GroupCompareResult>> compareGroupInMineAndCabinet(InstituteModel institute, SemesterModel semester, FormOfStudy formOfStudy);

    List<GroupMineModel> getMineGroupsByInstSemesterAndFormOfStudy(InstituteModel institute, SemesterModel semester, FormOfStudy formOfStudy);
    List<GroupMineModel> getCabinetGroupsBySemester(SemesterModel semester);

    void linkCabinetAndMineGroups(List<GroupMineModel> cabinetGroups, List<GroupMineModel> mineGroups);
}
