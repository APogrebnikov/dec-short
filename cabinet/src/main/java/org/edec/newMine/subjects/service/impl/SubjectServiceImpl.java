package org.edec.newMine.subjects.service.impl;

import lombok.extern.log4j.Log4j;
import org.edec.main.model.DepartmentModel;
import org.edec.newMine.subjects.manager.SubjectASUManager;
import org.edec.newMine.subjects.manager.SubjectMineManager;
import org.edec.newMine.subjects.model.GroupsEsoModel;
import org.edec.newMine.subjects.model.SubjectMineModel;
import org.edec.newMine.subjects.model.SubjectWorkloadModel;
import org.edec.newMine.subjects.model.SubjectsModel;
import org.edec.newMine.subjects.service.SubjectService;
import org.edec.passportGroup.manager.PassportGroupManager;
import org.edec.utility.constants.PracticeType;
import org.hibernate.type.IntegerType;

import java.util.*;
@Log4j
public class SubjectServiceImpl implements SubjectService {

    SubjectASUManager managerASU = new SubjectASUManager();
    SubjectMineManager managerMine = new SubjectMineManager();
    PassportGroupManager passportGroupManager = new PassportGroupManager();

    @Override
    public List<String> getRegisterNumberByLGSandSubj(Long idLGS, Long idSubj) {
        return managerASU.getRegisterNumberByLGSandSubj(idLGS, idSubj);
    }

    @Override
    public boolean deleteSubjectByLGSS(Long idLGSS) {
        return passportGroupManager.deleteLESG(idLGSS) && passportGroupManager.deleteLGSS(idLGSS)
                && passportGroupManager.deleteSubject(idLGSS) && passportGroupManager.deleteSR(idLGSS);
    }

    @Override
    public List<GroupsEsoModel> getESOGroupsBySemester(Long idSem) {
        return managerASU.getGroupsBySemester(idSem);
    }

    @Override
    public boolean createSubjectSRforLGS(Long idLGS, SubjectsModel subjectModelMine) {
        return managerASU.createSubject(idLGS, subjectModelMine);
    }

    @Override
    public List<SubjectsModel> getSubjectGroupMine(String groupname, Long idInstMine, Integer semester, Integer course, Date dateBeginSchoolYear, Integer fos, Boolean shortForm) {
        List<SubjectsModel> mineSubjects;
        if (fos == 1) {
            mineSubjects = managerMine.getMineSubjects(groupname, semester);
        } else {
            mineSubjects = managerMine.getExtramuralSubjects(groupname, course);
        }
        if (shortForm == true) {
            mineSubjects = managerMine.getMineSubjectsExtraShort(groupname, semester);
        }
        if (shortForm == false) {
            mineSubjects.stream().forEach(el -> el.setTeachers(managerMine.getTeachers(el.getIdCurriculum(), el.getYear(), el.getIdSubjMine(), el.getSessionNumber())));
        }
        for (SubjectsModel model : mineSubjects) {
            model.setHoursCount(model.getHoursExam() + model.getHoursIndependent() + model.getHoursLabaratory()
                    + model.getHoursLecture() + model.getHoursPractice());

            if (model.getTypeOfPractice() != null) {
                PracticeType practiceTypeByValue = PracticeType.getPracticeTypeByValue(model.getTypeOfPractice());
                model.setPracticeType(practiceTypeByValue.getName());
                model.setPracticeTypeDative(practiceTypeByValue.getDativeName());
            }
            if (model.getPracticStartWeek() != null && model.getPracticStartWeek() >= 40) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateBeginSchoolYear);
                calendar.add(Calendar.WEEK_OF_YEAR, model.getPracticStartWeek());
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                model.setDatePracticeBegin(calendar.getTime());
                calendar.add(Calendar.WEEK_OF_YEAR, model.getPracticWeekCount());
                calendar.add(Calendar.DATE, -1);
                model.setDatePracticeEnd(calendar.getTime());
            }
        }
        return mineSubjects;
    }

    @Override
    public List<SubjectsModel> getSubjectsByIdLGS(Long idLgs) {
        return managerASU.getSubjectsByLGS(idLgs);
    }

    @Override
    public void fillOtherSubjectsModelAndChair(List<SubjectsModel> subjectsESO, List<SubjectsModel> subjectsMine, List<DepartmentModel> departments) {
        for (SubjectsModel subjectMine : subjectsMine) {
            //Ищем подразделение по идентификатору шахт
            DepartmentModel foundDepartment = departments.stream()
                    .filter(departmentModel -> departmentModel.getIdDepartmentMine() != null &&
                            departmentModel.getIdDepartmentMine().equals(subjectMine.getIdChairMine()))
                    .findFirst()
                    .orElse(null);
            //Если нашли, то записываем  idChair в модель
            if (foundDepartment != null) {
                subjectMine.setIdChair(foundDepartment.getIdChair());
            }
            //Ищем предмет, у которого еще нет привязки к другому subject и у которых совпадают названия дисциплин
            SubjectsModel subjectGroupESO = subjectsESO.stream()
                    .filter(subjectESO -> subjectESO.getOtherSubject() == null &&
                            (subjectESO.getSubjectname().equals(subjectMine.getSubjectname()) || Objects.deepEquals(subjectESO.getIdSubjMine(), subjectMine.getIdSubjMine())))
                    .findFirst()
                    .orElse(null);
            if (subjectGroupESO != null) {
                subjectGroupESO.setOtherSubject(subjectMine);
                subjectMine.setOtherSubject(subjectGroupESO);
            }
        }
    }

    @Override
    public boolean updateAudSubject(SubjectsModel subjectGroupDec, SubjectsModel subjectGroupMine) {
        Long idDicSubj = subjectGroupDec.getSubjectname().equals(subjectGroupMine.getSubjectname()) ? subjectGroupDec.getIdDicSubj() : null;
        //Если ИД словаря предметов отсутствует, то нужно по @subjectName получить ИД или создать новый и получить у новог
        if (idDicSubj == null) {
            idDicSubj = managerASU.getDicSubjetBySubjectname(subjectGroupMine.getSubjectname());
            subjectGroupDec.setIdDicSubj(idDicSubj);
        }
        return managerASU.updateAudSubject(subjectGroupDec, subjectGroupMine);
    }

    @Override
    public boolean updateSubject(SubjectsModel subjectGroupDec, SubjectsModel subjectGroupMine) {
        Long idDicSubj = subjectGroupDec.getSubjectname().equals(subjectGroupMine.getSubjectname()) ? subjectGroupDec.getIdDicSubj() : null;
        //Если ИД словаря предметов отсутствует, то нужно по @subjectName получить ИД или создать новый и получить у новог
        if (idDicSubj == null) {
            idDicSubj = managerASU.getDicSubjetBySubjectname(subjectGroupMine.getSubjectname());
            subjectGroupDec.setIdDicSubj(idDicSubj);
        }
        return managerASU.updateSubject(subjectGroupDec, subjectGroupMine);
    }

    @Override
    public List<DepartmentModel> getDepartments() {
        return managerASU.getAllDepartments();
    }

    @Override
    public boolean updateSubjectCode(Long idSubject, String blocks) {
        return managerASU.updateSubjectCode(idSubject, blocks);

    }

    @Override
    public void createSubjects(List<SubjectsModel> subjectsMine, Long idLGS) {
        for (SubjectsModel subject : subjectsMine) {
            if (subject.getOtherSubject() != null) {
                continue;
            }
            createSubjectSRforLGS(idLGS, subject);
        }
    }

    @Override
    public void linkTeachersForSubjects(List<SubjectsModel> subjectsMine, Long idLGS) {
        List<SubjectsModel> subjectsASU = managerASU.getDecSubjectsByLgss(idLGS);
        if (!subjectsASU.isEmpty()) {
            for (SubjectsModel subjectMine : subjectsMine) {
                for (SubjectsModel subjectASU : subjectsASU) {
                    if (subjectASU.getSubjectname().equals(subjectMine.getSubjectname())
                            && subjectASU.getIdSubjMine().equals(subjectMine.getIdSubjMine())
                    && subjectASU.getSubjectcode().equals(subjectMine.getSubjectcode())) {
                        subjectMine.getTeachers().stream().forEach(teacher -> {
                            Long idEmployee = managerASU.getEmpByFIO(teacher.getFio());
                            if (idEmployee != null && managerASU.linkTeacherForSubject(idEmployee, subjectASU.getIdLGSS())) {
                                log.info("Прикреплен преподаватель "+teacher.getFio()+" к предмету " + subjectASU.getSubjectname());
                            }
                        });
                    }
                }
            }
        }
    }
}
