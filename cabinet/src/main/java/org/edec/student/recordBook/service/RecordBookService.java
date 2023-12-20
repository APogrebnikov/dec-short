package org.edec.student.recordBook.service;

import org.edec.efficiency.model.ConfigurationEfficiency;
import org.edec.student.recordBook.manager.EntityManagerRecordBook;
import org.edec.student.recordBook.model.GradeBookModel;
import org.edec.student.recordBook.model.GroupModel;
import org.edec.student.recordBook.model.StudentSemesterModel;
import org.edec.student.recordBook.model.dao.GroupSemesterEsoModel;
import org.edec.student.recordBook.model.dao.RatingEsoModel;

import java.util.ArrayList;
import java.util.List;


public class RecordBookService {
    private EntityManagerRecordBook emRecordBook = new EntityManagerRecordBook();

    public List<GroupModel> getGroupByHum (Long idHum) {
        return getGroupsByModel(emRecordBook.getGroupSemester(idHum));
    }

    public List<GradeBookModel> getGradeBookBySSS (StudentSemesterModel student) {
        return getGradeBookByRatingModel(emRecordBook.getRatingEsoModelBySSS(student.getIdSSS()), student.getConfigurationEfficiency());
    }

    private List<GroupModel> getGroupsByModel (List<GroupSemesterEsoModel> modelList) {
        List<GroupModel> result = new ArrayList<>();

        for (GroupSemesterEsoModel model : modelList) {
            boolean addGroup = true;
            for (GroupModel group : result) {
                if (group.getGroupname().equals(model.getGroupname())) {
                    StudentSemesterModel studentSemester = new StudentSemesterModel();
                    studentSemester.setIdSSS(model.getIdSSS());
                    studentSemester.setIdSem(model.getIdSem());
                    studentSemester.setSemesterNumber(model.getSemesterNumber());
                    addConfigurationEfficiencyForStudent(studentSemester, model);
                    group.getSemesters().add(studentSemester);
                    addGroup = false;
                    break;
                }
            }
            if (addGroup) {
                GroupModel group = new GroupModel();
                group.setGroupname(model.getGroupname());
                StudentSemesterModel studentSemester = new StudentSemesterModel();
                studentSemester.setIdSSS(model.getIdSSS());
                studentSemester.setIdSem(model.getIdSem());
                studentSemester.setSemesterNumber(model.getSemesterNumber());
                addConfigurationEfficiencyForStudent(studentSemester, model);
                group.getSemesters().add(studentSemester);
                result.add(group);
            }
        }

        return result;
    }

    private void addConfigurationEfficiencyForStudent (StudentSemesterModel student, GroupSemesterEsoModel model) {
        if (model.getAttendance() != null) {
            ConfigurationEfficiency configuration = new ConfigurationEfficiency();
            configuration.setAttendance(model.getAttendance());
            configuration.setEok(model.getEok());
            configuration.setPerformance(model.getPerformance());
            configuration.setPhyscul(model.getPhyscul());
            configuration.setMaxRedLevel(model.getMaxRedLevel());
            configuration.setMinGreenLevel(model.getMinGreenLevel());

            student.setConfigurationEfficiency(configuration);
        }
    }

    private List<GradeBookModel> getGradeBookByRatingModel (List<RatingEsoModel> ratingEsoModels, ConfigurationEfficiency configuration) {
        List<GradeBookModel> result = new ArrayList<>();

        List<GradeBookModel> examList = new ArrayList<>();
        List<GradeBookModel> passList = new ArrayList<>();
        List<GradeBookModel> cpList = new ArrayList<>();
        List<GradeBookModel> cwList = new ArrayList<>();
        List<GradeBookModel> practicList = new ArrayList<>();

        for (RatingEsoModel model : ratingEsoModels) {
            if (model.getExam()) {
                GradeBookModel gradeBookModel = getGradeBookByModel(model, configuration);
                if (model.getExamTutor() != null && !model.getExamTutor().equals("")) {
                    gradeBookModel.setTeacher(model.getExamTutor());
                } else {
                    gradeBookModel.setTeacher(model.getTeacher());
                }
                gradeBookModel.setRating(model.getExamrating() > 2 ? model.getExamrating() : model.getEsoexamrating());
                gradeBookModel.setFoc("Экзамен");
                examList.add(gradeBookModel);
            }
            if (model.getPass()) {
                GradeBookModel gradeBookModel = getGradeBookByModel(model, configuration);
                if (model.getPassTutor() != null && !model.getPassTutor().equals("")) {
                    gradeBookModel.setTeacher(model.getPassTutor());
                } else {
                    gradeBookModel.setTeacher(model.getTeacher());
                }
                gradeBookModel.setRating(
                        model.getPassrating() == 1 || model.getPassrating() > 2 ? model.getPassrating() : model.getEsopassrating());
                gradeBookModel.setFoc("Зачет");
                passList.add(gradeBookModel);
            }
            if (model.getCp()) {
                GradeBookModel gradeBookModel = getGradeBookByModel(model, configuration);
                if (model.getCpTutor() != null && !model.getCpTutor().equals("")) {
                    gradeBookModel.setTeacher(model.getCpTutor());
                } else {
                    gradeBookModel.setTeacher(model.getTeacher());
                }
                gradeBookModel.setRating(model.getCprating() > 2 ? model.getCprating() : model.getEsocprating());
                gradeBookModel.setFoc("КП");
                cpList.add(gradeBookModel);
            }
            if (model.getCw()) {
                GradeBookModel gradeBookModel = getGradeBookByModel(model, configuration);
                if (model.getCwTutor() != null && !model.getCwTutor().equals("")) {
                    gradeBookModel.setTeacher(model.getCwTutor());
                } else {
                    gradeBookModel.setTeacher(model.getTeacher());
                }
                gradeBookModel.setRating(model.getCwrating() > 2 ? model.getCwrating() : model.getEsocwrating());
                gradeBookModel.setFoc("КР");
                cwList.add(gradeBookModel);
            }
            if (model.getPractic() && !model.getPass()) {
                GradeBookModel gradeBookModel = getGradeBookByModel(model, configuration);
                if (model.getPracticTutor() != null && !model.getPracticTutor().equals("")) {
                    gradeBookModel.setTeacher(model.getPracticTutor());
                } else {
                    gradeBookModel.setTeacher(model.getTeacher());
                }
                gradeBookModel.setRating(model.getPracticrating());
                gradeBookModel.setFoc("Практика");
                practicList.add(gradeBookModel);
            }
        }

        result.addAll(examList);
        result.addAll(passList);
        result.addAll(cpList);
        result.addAll(cwList);
        result.addAll(practicList);

        return result;
    }

    private GradeBookModel getGradeBookByModel (RatingEsoModel model, ConfigurationEfficiency configuration) {
        GradeBookModel gradeBookModel = new GradeBookModel();
        gradeBookModel.setConfiguration(configuration);
        gradeBookModel.setConsultationDate(model.getConsultationDate());
        gradeBookModel.setDateOfPass(model.getDateOfPass());
        gradeBookModel.setStatusBeginDate(model.getStatusBeginDate());
        gradeBookModel.setStatusFinishDate(model.getStatusFinishDate());
        gradeBookModel.setStatus(model.getStatus());
        gradeBookModel.setEsoGradeCurrent(model.getEsogradecurrent());
        gradeBookModel.setEsoGradeMax(model.getEsogrademax());
        gradeBookModel.setHoursCount(model.getHoursCount());
        gradeBookModel.setIdEsoCourse(model.getIdEsoCourse());
        gradeBookModel.setSubjectname(model.getSubjectName());
        gradeBookModel.setLessonCount(model.getLessonCount());
        gradeBookModel.setVisitCount(model.getVisitCount());
        gradeBookModel.setCpDate(model.getCpDate());
        return gradeBookModel;
    }
}