package org.edec.teacher.service.impl;

import org.edec.teacher.manager.EntityManagerCompletion;
import org.edec.teacher.model.*;
import org.edec.teacher.model.dao.CompletionESOModel;
import org.edec.teacher.model.dao.CourseHistoryESOModel;
import org.edec.teacher.service.CompletionService;

import java.util.*;


public class CompletionServiceImpl implements CompletionService {
    private EntityManagerCompletion emCompletion = new EntityManagerCompletion();

    @Override
    public List<SemesterModel> getSemesterByHumanface (Long idHum, boolean unSignedRegister) {
        return divideSemesterModelByFoc(emCompletion.getCompletionESOModel(idHum, unSignedRegister));
    }

    @Override
    public Set<String> getInstitutesByModelSemester (List<SemesterModel> semesters) {
        Set<String> result = new HashSet<>();
        for (SemesterModel semester : semesters) {
            result.add(semester.getInstitute());
        }
        return result;
    }

    @Override
    public Set<String> getFormOfStudyByInst (String inst, List<SemesterModel> semesters) {
        Set<String> result = new HashSet<>();
        for (SemesterModel semester : semesters) {
            if (semester.getInstitute().equals(inst)) {
                result.add(semester.getFormofstudy() == 1 ? "Очная" : "Заочная");
            }
        }
        return result;
    }

    @Override
    public List<SemesterModel> getSemesterByFOSandInst (String inst, String formOfStudy, List<SemesterModel> semesters) {
        List<SemesterModel> result = new ArrayList<>();
        Integer fosInt = formOfStudy.equals("Очная") ? 1 : 2;
        for (SemesterModel semester : semesters) {
            if (semester.getInstitute().equals(inst) && semester.getFormofstudy() == fosInt) {
                result.add(semester);
            }
        }
        return result;
    }

    @Override
    public List<EsoCourseModel> getEsoCourses () {
        return emCompletion.getEsoCourses();
    }

    @Override
    public List<RetakeModel> getRetakesForHum (Long idHum, Integer signFilter) {
        return emCompletion.getRetakesForHum(idHum, signFilter);
    }

    @Override
    public List<EsoCourseModel> getFilteredEsoCourses (String fullname, Long idEsoCourse, List<EsoCourseModel> esoCourses) {
        if (fullname == null && idEsoCourse == null) {
            return esoCourses;
        }
        List<EsoCourseModel> result = new ArrayList<>();
        for (EsoCourseModel course : esoCourses) {
            if (fullname != null && course.getFullname().toLowerCase().contains(fullname.toLowerCase()) ||
                (idEsoCourse != null && course.getIdEsoCourse().equals(idEsoCourse))) {
                result.add(course);
            }
        }
        return result;
    }

    @Override
    public boolean updateESOcourse (Long idLGSS, Long idESOcourse) {
        return emCompletion.updateIdESOcourse(idLGSS, idESOcourse);
    }

    @Override
    public boolean updateRating (Integer rating, Long idSRH) {
        return emCompletion.updateRating(rating, idSRH);
    }

    @Override
    public boolean updateRegisterNumber (Long idReg, Long idSem, String suffix) {
        return emCompletion.updateRegisterNumber(idReg, idSem, suffix);
    }

    @Override
    public boolean updateRegisterAfterConfirmWithDigitalSign(Long idReg, String serialNumber, String thumbPrint) {

        return emCompletion.updateRegisterAfterConfirmWithDigitalSign(idReg, serialNumber, thumbPrint);
    }

    @Override
    public boolean updateRegisterAfterSign (Long idReg, String url, String serialNumber, String thumbPrint, String fio, String statusSR,
                                            String statusSRH) {
        return emCompletion.updateRegisterSrSrhAfterSign(idReg, url, serialNumber, thumbPrint, fio, statusSR, statusSRH);
    }

    private List<SemesterModel> divideSemesterModelByFoc (List<CompletionESOModel> models) {
        models = divideModelByFoc(models);
        List<SemesterModel> result = new ArrayList<>();
        for (CompletionESOModel model : models) {

            //Поиск семестра
            SemesterModel semester = result.stream()
                                           .filter(tmpSemester -> tmpSemester.getSemesterStr().equals(model.getSemesterStr()) &&
                                                                  tmpSemester.getFormofstudy().equals(model.getFormofstudy()) &&
                                                                  tmpSemester.getInstitute().equals(model.getInstitute()))
                                           .findFirst()
                                           .orElse(null);

            if (semester == null) {
                semester = new SemesterModel();
                semester.setCurSem(model.getCurSem());
                semester.setFormofstudy(model.getFormofstudy());
                semester.setInstitute(model.getInstitute());
                semester.setSeason(model.getSeason());
                semester.setIdInstitute(model.getIdInstitute());
                semester.setDateOfBeginYear(model.getDateofbegin());
                semester.setDateOfEndYear(model.getDateofend());
                semester.setSemesterStr(model.getSemesterStr());
                semester.setIdSemester(model.getIdSemester());

                result.add(semester);
            }

            //Поиск предмета
            SubjectModel subject = semester.getSubjects()
                                           .stream()
                                           .filter(tmpSubject -> tmpSubject.getSubjectname().equals(model.getSubjectname()) &&
                                                                 tmpSubject.getFormofcontrol().equals(model.getFormofcontrol()) &&
                                                                 tmpSubject.getTypePass().equals(model.getType()))
                                           .findFirst()
                                           .orElse(null);

            if (subject == null) {
                subject = new SubjectModel();
                subject.setFormofcontrol(model.getFormofcontrol());
                subject.setTypePass(model.getType());
                subject.setSubjectname(model.getSubjectname());
                subject.setSemester(semester);

                semester.getSubjects().add(subject);
            }

            //Добавление группы к предмету
            GroupModel group = new GroupModel();
            group.setIdESOcourse(model.getIdESOcourse());
            group.setIdLGSS(model.getIdLGSS());
            group.setGroupname(model.getGroupname());
            group.setHoursCount(model.getHoursCount());
            group.setCourse(model.getCourse());
            group.setSemesterNumber(model.getSemesterNumber());
            group.setCompletionDate(model.getCompletionDate());
            group.setSubject(subject);

            subject.getGroups().add(group);
        }

        return result;
    }

    private List<CompletionESOModel> divideModelByFoc (List<CompletionESOModel> models) {
        List<CompletionESOModel> result = new ArrayList<>();
        List<CompletionESOModel> listExam = new ArrayList<>();
        List<CompletionESOModel> listPass = new ArrayList<>();
        List<CompletionESOModel> listCP = new ArrayList<>();
        List<CompletionESOModel> listCW = new ArrayList<>();
        List<CompletionESOModel> listPractic = new ArrayList<>();

        for (CompletionESOModel model : models) {
            if (model.getExam()) {
                CompletionESOModel tempModel = getTempModel(model);
                tempModel.setFormofcontrol(1);
                tempModel.setCompletionDate(model.getExamdate());
                listExam.add(tempModel);
            }
            if (model.getPass()) {
                CompletionESOModel tempModel = getTempModel(model);
                tempModel.setFormofcontrol(2);
                tempModel.setCompletionDate(model.getPassdate());
                listPass.add(tempModel);
            }
            if (model.getCp()) {
                CompletionESOModel tempModel = getTempModel(model);
                tempModel.setFormofcontrol(3);
                tempModel.setCompletionDate(model.getCpdate());
                listCP.add(tempModel);
            }
            if (model.getCw()) {
                CompletionESOModel tempModel = getTempModel(model);
                tempModel.setFormofcontrol(4);
                tempModel.setCompletionDate(model.getCwdate());
                listCW.add(tempModel);
            }
            if (model.getPractic()) {
                CompletionESOModel tempModel = getTempModel(model);
                tempModel.setFormofcontrol(5);
                tempModel.setCompletionDate(model.getPracticdate());
                listPractic.add(tempModel);
            }
        }

        result.addAll(listExam);
        result.addAll(listPass);
        result.addAll(listCP);
        result.addAll(listCW);
        result.addAll(listPractic);

        result.sort((o1, o2) -> {
            if (o1.getDateofbegin().compareTo(o2.getDateofbegin()) == 0) {
                if (o1.getSeason().compareTo(o2.getSeason()) == 0) {
                    if (o1.getFormofstudy().compareTo(o2.getFormofstudy()) == 0) {
                        if (o1.getSubjectname().compareTo(o2.getSubjectname()) == 0) {
                            return o1.getGroupname().compareTo(o2.getGroupname());
                        } else {
                            return o1.getSubjectname().compareTo(o2.getSubjectname());
                        }
                    } else {
                        return o1.getFormofstudy().compareTo(o2.getFormofstudy());
                    }
                } else {
                    return o2.getSeason().compareTo(o1.getSeason());
                }
            } else {
                return o2.getDateofbegin().compareTo(o1.getDateofbegin());
            }
        });

        return result;
    }

    private CompletionESOModel getTempModel (CompletionESOModel model) {
        CompletionESOModel tempModel = new CompletionESOModel();
        tempModel.setSemesterStr(model.getSemesterStr());
        tempModel.setDateofbegin(model.getDateofbegin());
        tempModel.setDateofend(model.getDateofend());
        tempModel.setInstitute(model.getInstitute());
        tempModel.setIdInstitute(model.getIdInstitute());
        tempModel.setSeason(model.getSeason());
        tempModel.setHoursCount(model.getHoursCount());
        tempModel.setType(model.getType());
        tempModel.setCourse(model.getCourse());
        tempModel.setSemesterNumber(model.getSemesterNumber());
        tempModel.setFormofstudy(model.getFormofstudy());
        tempModel.setCurSem(model.getCurSem());
        tempModel.setGroupname(model.getGroupname());
        tempModel.setIdESOcourse(model.getIdESOcourse());
        tempModel.setIdLGSS(model.getIdLGSS());
        tempModel.setSubjectname(model.getSubjectname());
        tempModel.setIdSemester(model.getIdSemester());

        return tempModel;
    }

    public List<CourseHistoryModel> getAvailableCourses (long idHumanface, long idCurSem) {
        List<CourseHistoryESOModel> listCourses = emCompletion.getAvailableCoursesForBinding(idHumanface, idCurSem);
        return getTransformCourseList(listCourses);
    }

    private List<CourseHistoryModel> getTransformCourseList (List<CourseHistoryESOModel> listCourses) {
        List<CourseHistoryModel> transformCourseList = new ArrayList<>();

        CourseHistoryModel prevModel = null;

        for (CourseHistoryESOModel listCourse : listCourses) {
            if (prevModel != null && listCourse.getSubjectname().equals(prevModel.getSubjectname())) {
                if (!prevModel.getGroupList().isEmpty() &&
                    !listCourse.getIdLGSS().equals(prevModel.getGroupList().get(prevModel.getGroupList().size() - 1).getIdLGSS())) {
                    GroupModel groupModel = new GroupModel();
                    groupModel.setIdLGSS(listCourse.getIdLGSS());
                    groupModel.setGroupname(listCourse.getGroupName());

                    prevModel.getGroupList().add(groupModel);
                }

                if (!prevModel.getUsedCourses().isEmpty() && !isGroupContained(prevModel.getUsedCourses(), listCourse.getIdEsoCourse2())) {

                    EsoCourseModel courseModel = new EsoCourseModel();
                    courseModel.setIdEsoCourse(listCourse.getIdEsoCourse2());
                    courseModel.setFullname(listCourse.getFullname());

                    prevModel.getUsedCourses().add(courseModel);
                }
            } else {
                prevModel = getTransformCourseHistory(listCourse);
                transformCourseList.add(prevModel);
            }
        }

        return transformCourseList;
    }

    private boolean isGroupContained (List<EsoCourseModel> list, Long idEsoCourse) {
        for (EsoCourseModel esoCourseModel : list) {
            if (esoCourseModel.getIdEsoCourse().equals(idEsoCourse)) {
                return true;
            }
        }
        return false;
    }

    private CourseHistoryModel getTransformCourseHistory (CourseHistoryESOModel courseHistoryESOModel) {
        CourseHistoryModel courseHistoryModel = new CourseHistoryModel();
        courseHistoryModel.setIdEmp(courseHistoryESOModel.getIdEmp());
        List<GroupModel> groups = new ArrayList<>();

        GroupModel group = new GroupModel();
        group.setGroupname(courseHistoryESOModel.getGroupName());
        group.setIdLGSS(courseHistoryESOModel.getIdLGSS());
        groups.add(group);

        courseHistoryModel.setGroupList(groups);
        courseHistoryModel.setSubjectname(courseHistoryESOModel.getSubjectname());

        List<EsoCourseModel> list = new ArrayList<>();

        EsoCourseModel courseModel = new EsoCourseModel();
        courseModel.setIdEsoCourse(courseHistoryESOModel.getIdEsoCourse2());
        courseModel.setFullname(courseHistoryESOModel.getFullname());

        list.add(courseModel);

        courseHistoryModel.setUsedCourses(list);

        return courseHistoryModel;
    }
}
