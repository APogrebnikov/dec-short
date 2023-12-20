package org.edec.newMine.groups.service.impl;

import org.edec.utility.component.model.SemesterModel;
import org.edec.newMine.groups.manager.GroupsManagerESO;
import org.edec.newMine.groups.manager.GroupsManagerMine;
import org.edec.newMine.groups.model.GroupsModel;
import org.edec.newMine.groups.service.GroupsService;
import org.edec.utility.converter.DateConverter;
import org.hibernate.type.IntegerType;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupsServiceImpl implements GroupsService {
    private GroupsManagerESO managerESO = new GroupsManagerESO();
    private GroupsManagerMine managerMine = new GroupsManagerMine();

    private final Pattern pattern = Pattern.compile("((\\d{2,}\\.)+(\\d{2,})?){1}(.)*");

    @Override
    public List<GroupsModel> getMineGroupsByInstSemesterAndFormOfStudy(Long idInst, SemesterModel semester, Integer formOfStudy) {
        String year = DateConverter.convertDateToYearString(semester.getDateOfBegin()) + "-" + DateConverter.convertDateToYearString(semester.getDateOfEnd());
        return managerMine.getGroupsByInstYearStudyAndFormOfStudy(idInst, year, formOfStudy);
    }

    @Override
    public List<GroupsModel> getCabinetGroupsBySemester(Long idSem) {
        return managerESO.getGroupsBySem(idSem);
    }

    @Override
    public boolean updateChair(Long idCurriculum, Integer idChairMine) {
        return managerESO.updateChairs(idCurriculum, idChairMine);
    }

    @Override
    public boolean updatePlanFileName(Long idCurriculum, String planFileName) {
        return managerESO.updatePlanFileName(idCurriculum, planFileName);
    }

    @Override
    public boolean updateGroup(Long idLGS, Integer idGroupMine) {
        return managerESO.updateGroup(idLGS, idGroupMine);
    }

    @Override
    public void createGroup(GroupsModel data, Long idInst, SemesterModel semester) {
        Long idCreatedSchoolYear = managerESO.getIdSchoolYearByBeginDate(data.getCreatedSchoolYear());
        Long idEnterSchoolYear = managerESO.getIdSchoolYearByBeginDate(data.getEnterSchoolYear());
        if (data.getDirectionTitle() != null) {
            Matcher m = pattern.matcher(data.getDirectionTitle());
            if (m.find()) {
                //data.setDirectionCode(data.getDirectionTitle().substring(0, m.end(1)));
                data.setDirectionTitle(data.getDirectionTitle().substring(m.end(1)).trim());
            }
        }
        Long idChair = managerESO.getIdChairByNameAndODI(data.getChairName(), data.getIdChairMine());
        data.setIdChair(idChair);
        Long idDirection = managerESO.getIdDirectionByTitleAndCode(data.getSpecialityTitle(), data.getDirectionCode());
        data.setIdDirection(idDirection);

        Long idCurriculum = managerESO.createOrGetCurriculum(data, idCreatedSchoolYear, idEnterSchoolYear);
        Long idDicGroup = managerESO.createOrGetDicGroup(idCurriculum, idInst, data.getMilitary(), data.getGroupname());
        Integer semesterNumber = (data.getCourse() - 1) * 2 + (semester.getSeason() == 0 ? 1 : 2);
        Long idLGS = managerESO.createOrGetLGS(
                data.getCourse(), semesterNumber, idDicGroup, semester.getIdSem(), data.getIdGroupMine());
    }

    @Override
    public void transferGroupsInAutumnSemester(SemesterModel semester) {
        String year;
        Boolean isSpringSem = (semester.getSeason() == 1 ? true : false);
        // если переводим в весенний сесместр
        if (isSpringSem) {
            year = DateConverter.convertDateToYearString(semester.getDateOfEnd());
        } else {
            year = DateConverter.convertDateToYearString(semester.getDateOfBegin());
        }
        managerESO.transferGroupsInSemester(year, semester.getIdSem(), isSpringSem );
    }
}
