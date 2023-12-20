package org.edec.register.service.impl;

import org.edec.register.comparator.SubjectComparator;
import org.edec.register.manager.SubjectManager;
import org.edec.register.model.SubjectModel;
import org.edec.register.model.dao.SubjectModelEso;
import org.edec.register.service.SubjectService;
import org.edec.utility.constants.FormOfControlConst;
import org.zkoss.zul.Listitem;

import java.util.*;

public class SubjectServiceImpl implements SubjectService {

    private SubjectManager manager = new SubjectManager();

    private SubjectModel createSubjectModel (SubjectModelEso eso, int foc) {
        SubjectModel model = new SubjectModel();

        model.setIdSubject(eso.getIdSubject());
        model.setSubjectName(eso.getSubjectName());
        model.setGroupName(eso.getGroupName());
        model.setIdGroup(eso.getIdGroup());
        model.setIdLgss(eso.getIdLgss());
        model.setFoc(foc);
        model.setType(eso.getType());
        model.setCourse(eso.getCourse());
        model.setTeachers(new HashSet<>());
        model.getTeachers().add(eso.getFamilyTeacher() + " " + eso.getNameTeacher() + " " + eso.getPatronymicTeacher() + "\n");

        return model;
    }

    @Override
    public List<SubjectModel> filterSubjectList (List<SubjectModel> subjects, String fioTeacher, String subjectName, String groupName) {
        List<SubjectModel> filteredSubjects = new ArrayList<>(subjects);
        for (int i = 0; i < filteredSubjects.size(); i++) {

            if (!subjectName.equals("")) {
                if (!filteredSubjects.get(i).getSubjectName().toLowerCase().contains(subjectName.toLowerCase())) {
                    filteredSubjects.remove(i);
                    i--;
                    continue;
                }
            }

            if (!fioTeacher.equals("")) {
                if (!filteredSubjects.get(i).getTeachers().toString().toLowerCase().contains(fioTeacher.toLowerCase())) {
                    filteredSubjects.remove(i);
                    i--;
                    continue;
                }
            }

            if (!groupName.equals("")) {
                if (!filteredSubjects.get(i).getGroupName().toString().toLowerCase().contains(groupName.toLowerCase())) {
                    filteredSubjects.remove(i);
                    i--;
                    continue;
                }
            }
        }

        return filteredSubjects;
    }

    @Override
    public List<String> getGroupNameList (List<SubjectModel> subjects) {
        List<String> groupNames = new ArrayList<>();
        Collections.sort(subjects, new SubjectComparator(SubjectComparator.CompareMethods.BY_COURSE_AND_GROUP));
        String prevModel = null;
        for (SubjectModel subject : subjects) {
            if (prevModel == null || !subject.getGroupName().equals(prevModel)) {
                prevModel = subject.getGroupName();
                groupNames.add(prevModel);
            }
        }
        return groupNames;
    }

    @Override
    public List<SubjectModel> getSubjects (Long idInstitute, int formOfStudy, Long idSemester, Integer course, String fioTeacher,
                                           String subjectName, boolean isBachelor, boolean isEngineer, boolean isMaster) {
        List<SubjectModel> subjects = groupBySubjectAndFoc(
                manager.getListSubjects(idInstitute, formOfStudy, idSemester, course, isBachelor, isEngineer, isMaster));
        return filterSubjectList(subjects, fioTeacher, subjectName, "");
    }

    @Override
    public List<SubjectModel> getSubjectsBySelectedGroups (List<SubjectModel> subjects, List<Listitem> groupNames) {
        List<SubjectModel> filteredSubjects = new ArrayList<>(subjects);
        List<String> groups = new ArrayList<>();

        for (Listitem listitem : groupNames) {
            groups.add(listitem.getValue().toString());
        }
        for (int i = 0; i < filteredSubjects.size(); i++) {
            if (!groups.contains(filteredSubjects.get(i).getGroupName())) {
                filteredSubjects.remove(i);
                i--;
            }
        }

        return filteredSubjects;
    }

    private List<SubjectModel> groupBySubjectAndFoc (List<SubjectModelEso> listEso) {
        List<SubjectModel> listSubjects = new ArrayList<>();
        SubjectModel prevModel = null;
        SubjectModel prevPrevModel = null;
        for (SubjectModelEso item : listEso) {
            if (prevModel != null && item.getIdSubject().equals(prevModel.getIdSubject()) &&
                item.getGroupName().equals(prevModel.getGroupName())) {
                prevModel.getTeachers()
                         .add(item.getFamilyTeacher() + " " + item.getNameTeacher() + " " + item.getPatronymicTeacher() + "\n");
                if (prevPrevModel.getFoc() != prevModel.getFoc()) {
                    prevPrevModel.getTeachers()
                                 .add(item.getFamilyTeacher() + " " + item.getNameTeacher() + " " + item.getPatronymicTeacher() + "\n");
                }
            } else {
                int foc;
                if (item.getExam()) {
                    foc = FormOfControlConst.EXAM.getValue().intValue();
                    prevModel = createSubjectModel(item, foc);
                    prevPrevModel = prevModel;
                    listSubjects.add(prevModel);
                } else if (item.getPass()) {
                    foc = FormOfControlConst.PASS.getValue().intValue();
                    prevModel = createSubjectModel(item, foc);
                    prevPrevModel = prevModel;
                    listSubjects.add(prevModel);
                }
                if (item.getCp()) {
                    foc = FormOfControlConst.CP.getValue().intValue();
                    prevModel = createSubjectModel(item, foc);
                    listSubjects.add(prevModel);
                } else if (item.getCw()) {
                    foc = FormOfControlConst.CW.getValue().intValue();
                    prevModel = createSubjectModel(item, foc);
                    listSubjects.add(prevModel);
                }
                if (item.getPractic()) {
                    foc = FormOfControlConst.PRACTIC.getValue().intValue();
                    prevModel = createSubjectModel(item, foc);
                    listSubjects.add(prevModel);
                }
            }
        }
        return listSubjects;
    }
}
