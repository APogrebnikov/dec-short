package org.edec.synchroMine.service.impl;

import org.edec.model.SemesterModel;
import org.edec.synchroMine.manager.groupSync.CabinetGroupSyncDAO;
import org.edec.synchroMine.manager.groupSync.MineGroupSyncDAO;
import org.edec.synchroMine.model.GroupCompareResult;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.edec.synchroMine.service.GroupSyncService;
import org.edec.utility.compare.compareResult.CompareResult;
import org.edec.utility.compare.compareResult.CompareResultForTwoList;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.converter.DateConverter;

import java.util.List;

public class GroupSyncImpl implements GroupSyncService {

    private CabinetGroupSyncDAO cabinetGroupSyncDAO = new CabinetGroupSyncDAO();
    private MineGroupSyncDAO mineGroupSyncDAO = new MineGroupSyncDAO();

    @Override
    public List<CompareResult<GroupCompareResult>> compareGroupInMineAndCabinet(InstituteModel institute, SemesterModel semester, FormOfStudy formOfStudy) {

        CompareResultForTwoList<GroupCompareResult> compareResultBetweenTwoLists = new CompareResultForTwoList<>(GroupCompareResult.class);

        return compareResultBetweenTwoLists.compareTwoList(
                mineGroupSyncDAO.getGroupsByInstYearStudyAndFormOfStudy2(institute.getIdInstMine(), getStudyYearBySemesterModel(semester), formOfStudy.getMineTypes()),
                cabinetGroupSyncDAO.getGroupsBySem2(semester.getIdSem())
        );
    }

    @Override
    public List<GroupMineModel> getMineGroupsByInstSemesterAndFormOfStudy(InstituteModel institute, SemesterModel semester, FormOfStudy formOfStudy) {
        String studyYear = getStudyYearBySemesterModel(semester);
        return mineGroupSyncDAO.getGroupsByInstYearStudyAndFormOfStudy(institute.getIdInstMine(), studyYear, formOfStudy.getMineTypes());
    }

    @Override
    public List<GroupMineModel> getCabinetGroupsBySemester(SemesterModel semester) {
        return cabinetGroupSyncDAO.getGroupsBySem(semester.getIdSem());
    }

    @Override
    public void linkCabinetAndMineGroups(List<GroupMineModel> cabinetGroups, List<GroupMineModel> mineGroups) {
        cabinetGroups.forEach(cabinetGroup -> {
            GroupMineModel foundMineGroup = mineGroups.stream()
                    .filter(mineGroup -> checkMineGroupByNotLinkedGroupAndEqualsName(cabinetGroup.getGroupname(), mineGroup))
                    .findFirst().orElse(null);
            if (foundMineGroup != null) {
                cabinetGroup.setLinkedGroup(foundMineGroup);
                foundMineGroup.setLinkedGroup(cabinetGroup);
            }
        });
    }

    private String getStudyYearBySemesterModel(SemesterModel semester) {
        return DateConverter.convertDateToYearString(semester.getDateOfBegin()) + "-" + DateConverter.convertDateToYearString(semester.getDateOfEnd());
    }

    private boolean checkMineGroupByNotLinkedGroupAndEqualsName(String cabinetGroupName, GroupMineModel mineGroup) {
        return mineGroup.getLinkedGroup() == null &&
                mineGroup.getGroupname().equals(cabinetGroupName);
    }
}
