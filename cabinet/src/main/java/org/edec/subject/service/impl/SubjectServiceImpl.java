package org.edec.subject.service.impl;

import org.edec.subject.manager.SubjectManager;
import org.edec.subject.model.SubjectModel;
import org.edec.subject.model.TeacherModel;
import org.edec.subject.model.eso.SubjectModelEso;
import org.edec.subject.model.eso.TeacherModelEso;
import org.edec.subject.service.SubjectService;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.converter.DateConverter;

import java.util.*;

public class SubjectServiceImpl implements SubjectService {

    SubjectManager manager = new SubjectManager();

    @Override
    public List<SubjectModel> getSubjectsBySem (long idDepartment, long idSem) {
        List<SubjectModelEso> listSubjectESO = manager.getSubjectsByFilter(idDepartment, idSem);
        return groupByTeacher(listSubjectESO);
    }

    private List<SubjectModel> groupByTeacher (List<SubjectModelEso> listEso) {
        List<SubjectModel> listSubjects = new ArrayList<>();
        SubjectModel prevModel = new SubjectModel();

        for (int i = 0; i < listEso.size(); i++) {
            SubjectModelEso subjectModelEso = listEso.get(i);
            if (subjectModelEso.getIdDg().equals(prevModel.getIdDg()) && subjectModelEso.getIdSubject().equals(prevModel.getIdSubject())) {
                if (!prevModel.getTeachers().isEmpty()) {
                    if (subjectModelEso.getIdEmp().equals(prevModel.getTeachers().get(prevModel.getTeachers().size() - 1).getIdTeacher()) &&
                        (!prevModel.getTeachers()
                                   .get(prevModel.getTeachers().size() - 1)
                                   .getDepTitles()
                                   .contains(subjectModelEso.getDepTitle()))) {
                        prevModel.getTeachers().get(prevModel.getTeachers().size() - 1).getDepTitles().add(subjectModelEso.getDepTitle());
                        continue;
                    }
                }

                TeacherModel teacher = new TeacherModel();
                teacher.setIdTeacher(subjectModelEso.getIdEmp());
                teacher.setFullName(subjectModelEso.getFioTeacher());
                teacher.getDepTitles().add(subjectModelEso.getDepTitle());
                teacher.setIdLesg(subjectModelEso.getIdLesg());
                prevModel.getTeachers().add(teacher);
            } else {
                prevModel = constructSubject(subjectModelEso);
                listSubjects.add(prevModel);
            }
        }

        return listSubjects;
    }

    private SubjectModel constructSubject (SubjectModelEso subjectModelEso) {
        SubjectModel subject = new SubjectModel();

        subject.setIdLgss(subjectModelEso.getIdLgss());
        subject.setIdSem(subjectModelEso.getIdSem());
        subject.setSubjectName(subjectModelEso.getSubjectName());
        subject.setIdDg(subjectModelEso.getIdDg());
        subject.setGroupName(subjectModelEso.getGroupName());
        subject.setIdSubject(subjectModelEso.getIdSubject());

        TeacherModel teacher = new TeacherModel();
        if (subjectModelEso.getIdEmp() != null) {
            teacher.setIdTeacher(subjectModelEso.getIdEmp());
            teacher.setFullName(subjectModelEso.getFioTeacher());
            teacher.getDepTitles().add(subjectModelEso.getDepTitle());
            teacher.setIdLesg(subjectModelEso.getIdLesg());
            subject.getTeachers().add(teacher);
        }

        return subject;
    }

    public List<SubjectModel> filterSubjects (List<SubjectModel> subjects, String subjectName, String groupName) {
        List<SubjectModel> filteredList = new ArrayList<>(subjects);

        for (int i = 0; i < filteredList.size(); i++) {

            if (!subjectName.equals("")) {
                if (!filteredList.get(i).getSubjectName().toLowerCase().contains(subjectName.toLowerCase())) {
                    filteredList.remove(i);
                    i--;
                    continue;
                }
            }

            if (!groupName.equals("")) {
                if (!filteredList.get(i).getGroupName().toLowerCase().contains(groupName.toLowerCase())) {
                    filteredList.remove(i);
                    i--;
                    continue;
                }
            }
        }

        return filteredList;
    }

    @Override
    public List<SemesterModel> getSemester (Long idInst, Integer fos, Integer season) {
        return manager.getSemester(idInst, fos, season);
    }

    @Override
    public boolean changeTeacherVisibility (Long idLed, boolean isHide) {
        return manager.changeTeacherVisibility(idLed, isHide);
    }

    @Override
    public List<TeacherModel> filterTeachers (List<TeacherModel> teachers, String teacherFio, Boolean showHiddenTeachers) {
        List<TeacherModel> filteredTeachers = new ArrayList<>(teachers);

        for (int i = 0; i < filteredTeachers.size(); i++) {
            if (!teacherFio.isEmpty()) {
                if (!filteredTeachers.get(i).getFullName().toLowerCase().contains(teacherFio.toLowerCase())) {
                    filteredTeachers.remove(i);
                    i--;
                    continue;
                }
            }

            if (!showHiddenTeachers) {
                if (filteredTeachers.get(i).getHidden()) {
                    filteredTeachers.remove(i);
                    i--;
                    continue;
                }
            }
        }

        return filteredTeachers;
    }

    // Список преподавателей
    public List<TeacherModel> getTeachers (String teacher, long idDep) {
        List<TeacherModelEso> teachersESO = manager.getTeachers(idDep);
        List<TeacherModel> listTeachers = new ArrayList<>();

        TeacherModel prevModel = null;
        for (int i = 0; i < teachersESO.size(); i++) {
            TeacherModel currModel = createTeacher(teachersESO.get(i));
            if (prevModel != null && currModel.getIdTeacher().equals(prevModel.getIdTeacher())) {
                prevModel.getDepTitles().add(currModel.getDepTitles().get(0));
            } else {
                prevModel = currModel;
                listTeachers.add(prevModel);
            }
        }

        for (int k = 0; k < listTeachers.size(); k++) {
            Collections.sort(listTeachers.get(k).getDepTitles(), Comparator.comparing(String::toString));

            for (int l = 0; l < listTeachers.get(k).getDepTitles().size(); l++) {
                if (l != 0) {
                    if ((listTeachers.get(k).getDepTitles().size() > 1) &&
                        (listTeachers.get(k).getDepTitles().get(l).equals(listTeachers.get(k).getDepTitles().get(l - 1)))) {
                        listTeachers.get(k).getDepTitles().remove(l);
                        if (listTeachers.get(k).getDepTitles().size() > 1) {
                            l = 0;
                        }
                    }
                }
            }
        }

        Collections.sort(listTeachers, Comparator.comparing(TeacherModel::getFullName));

        return listTeachers;
    }

    private TeacherModel createTeacher (TeacherModelEso teacherESO) {
        TeacherModel teacher = new TeacherModel();

        teacher.setIdLesg(teacherESO.getIdLesg());
        teacher.setIdTeacher(teacherESO.getIdTeacher());
        teacher.setFullName(teacherESO.getFullName());
        teacher.setDepTitles(new ArrayList<>());
        teacher.getDepTitles().add(teacherESO.getDepTitle());
        teacher.setIdLed(teacherESO.getIdLed());
        teacher.setHidden(teacherESO.getHidden());

        return teacher;
    }

    // Прикрепление преподавателя
    public boolean addTeacherToSubject (Long idTeacher, Long idLgss) {
        return manager.addTeacher(idTeacher, idLgss);
    }

    // Открепление преподавателя
    public boolean removeTeacherFromSubject (Long idLesg) {
        return manager.removeTeacherFromSubject(idLesg);
    }
}
