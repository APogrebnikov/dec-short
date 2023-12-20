package org.edec.synchroMine.service.impl;

import org.edec.synchroMine.manager.groupSync.CabinetGroupSyncDAO;
import org.edec.synchroMine.manager.groupSync.MineGroupSyncDAO;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.edec.utility.constants.FormOfStudy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupSyncImplTest {

    private CabinetGroupSyncDAO cabinetGroupSyncDAO = new CabinetGroupSyncDAO();
    private MineGroupSyncDAO mineGroupSyncDAO = new MineGroupSyncDAO();

    private static FormOfStudy formOfStudy;
    private static String year;
    private static Long idInstituteMine, idSemesterCabinet;
    private List<GroupMineModel> mineGroups, cabinetGroups;

    @BeforeAll
    private static void initFields() {
        year = "2016-2017";
        formOfStudy = FormOfStudy.EXTRAMURAL;
        idInstituteMine = 27L; //ИД факультета ИКИТ в шахтах
        idSemesterCabinet = 56L;
    }

    @Test
    @DisplayName("Check on non empty list of mine groups")
    void getMineGroupsByInstSemesterAndFormOfStudy() {
        mineGroups = mineGroupSyncDAO.getGroupsByInstYearStudyAndFormOfStudy(idInstituteMine, year, formOfStudy.getMineTypes());
        mineGroups.forEach(group -> System.out.println(group.getGroupname() + ": " + group.getIdGroupMine()));
        assertFalse(mineGroups.size() == 0);
    }

    @Test
    @DisplayName("Check on non empty list of cabinet groups")
    void getCabinetGroupsBySemester() {
        cabinetGroups = cabinetGroupSyncDAO.getGroupsBySem(idSemesterCabinet);
        assertFalse(cabinetGroups.size() == 0);

    }

    @Test
    void linkCabinetAndMineGroups() {

    }
}