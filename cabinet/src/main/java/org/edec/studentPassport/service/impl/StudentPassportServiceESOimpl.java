package org.edec.studentPassport.service.impl;

import org.edec.studentPassport.manager.StudentPassportEsoDAO;
import org.edec.studentPassport.model.filter.StudentPassportFilter;
import org.edec.utility.component.model.RatingModel;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.studentPassport.model.dao.RatingEsoModel;
import org.edec.studentPassport.service.StudentPassportService;
import org.edec.utility.httpclient.manager.ReportHttpService;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class StudentPassportServiceESOimpl implements StudentPassportService {

    private SimpleDateFormat formatYYYY = new SimpleDateFormat("YYYY");
    private StudentPassportEsoDAO studentPassportEsoDAO = new StudentPassportEsoDAO();
    private ReportHttpService reportHttpService = new ReportHttpService();

    @Override
    public List<StudentStatusModel> getStudentsByFilter(StudentPassportFilter filter) {
        return studentPassportEsoDAO.getStudentByFilter(filter);
    }

    @Override
    public boolean saveStudentInfo(StudentStatusModel studentStatusModel) {
        return studentPassportEsoDAO.saveStudent(studentStatusModel);
    }

    @Override
    public List<RatingModel> getRatingByHumAndDG(Long idHum, Long idDG, boolean debt) {
        List<RatingEsoModel> listEsoModel = studentPassportEsoDAO.getRatingByIdHumAndDigGroup(idHum, idDG);
        List<RatingModel> result = divideEsoModelForRatingModel(listEsoModel, debt);
        return result;
    }

    @Override
    public ByteArrayOutputStream generateIndexCard(Long idStudentCardMine, String groupName) {
        return reportHttpService.getStudentIndexCard("Учетная карточка студента", idStudentCardMine, groupName);
    }

    private List<RatingModel> divideEsoModelForRatingModel(List<RatingEsoModel> listEsoModel, boolean debt) {
        final List<RatingModel> result = new ArrayList<>();
        List<RatingModel> examList = new ArrayList<>();
        List<RatingModel> passList = new ArrayList<>();
        List<RatingModel> cpList = new ArrayList<>();
        List<RatingModel> cwList = new ArrayList<>();
        List<RatingModel> practicList = new ArrayList<>();

        for (RatingEsoModel esoModel : listEsoModel) {
            if (esoModel.getExam() && (!debt || (debt && esoModel.getExamrating() < 3))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getExamrating());
                ratingModel.setFoc("Экзамен");
                examList.add(ratingModel);
            }
            if (esoModel.getPass() && (!debt || (debt && (esoModel.getType() == 0 && esoModel.getPassrating() != 1 || esoModel.getType() == 1 && esoModel.getPassrating() < 3)))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getPassrating());
                ratingModel.setFoc("Зачет");
                passList.add(ratingModel);
            }
            if (esoModel.getCp() && (!debt || (debt && esoModel.getCprating() < 3))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getCprating());
                ratingModel.setFoc("КП");
                cpList.add(ratingModel);
            }
            if (esoModel.getCw() && (!debt || (debt && esoModel.getCwrating() < 3))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getCwrating());
                ratingModel.setFoc("КР");
                cwList.add(ratingModel);
            }
            if (esoModel.getPractic() && (!debt || (debt && esoModel.getPracticrating() < 3))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getPracticrating());
                ratingModel.setFoc("Практика");
                practicList.add(ratingModel);
            }
        }

        result.addAll(examList);
        result.addAll(passList);
        result.addAll(cpList);
        result.addAll(cwList);
        result.addAll(practicList);

        result.sort((o1, o2) -> {
            try {
                if (formatYYYY.parse(o1.getSemester()).compareTo(formatYYYY.parse(o2.getSemester())) == 0) {
                    return o2.getSemester().compareTo(o1.getSemester());
                }
                return formatYYYY.parse(o1.getSemester()).compareTo(formatYYYY.parse(o2.getSemester()));
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });

        return result;
    }

    private RatingModel getRatingModelByEsoModel(RatingEsoModel esoModel) {
        RatingModel ratingModel = new RatingModel();
        ratingModel.setSemester(esoModel.getSemester());
        ratingModel.setSubjectname(esoModel.getSubjectname());
        return ratingModel;
    }
}
