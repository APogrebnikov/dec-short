package org.edec.contingentMovement.report.sevice.impl;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.contingentMovement.report.manager.ResitMarkManager;
import org.edec.contingentMovement.report.model.ResitMarkModel;
import org.edec.contingentMovement.report.model.ResitModel;
import org.edec.contingentMovement.report.sevice.ResitMarkService;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.utility.constants.RatingConst;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ResitMarkImpl implements ResitMarkService {

    private ResitMarkManager manager = new ResitMarkManager();

    @Override
    public JRBeanCollectionDataSource getResitReport(StudentStatusModel student) {
        ResitModel model = new ResitModel();

        model.setFio(student.getFio());
        model.setDirectioncode(student.getDirection());
        model.setGroupname(student.getGroupname());
        model.setPrevgroupname(student.getPrevGroupName());
        model.setShortfio(student.getShortFioInverse());

        List<ResitMarkModel> marks = manager.getResitMark(student.getIdStudentCard(), student.getIdDG())
                .stream().filter(el -> !(el.getFoc().equals("Экзамен") && el.getFocOldGroup().equals("Зачет"))).collect(Collectors.toList());
        model.setMarks(marks);

        for (ResitMarkModel mark : model.getMarks()) {
            String rating = "";
                switch (mark.getFoc()) {
                    case "Экзамен":
                        rating = RatingConst.getNameByRating(mark.getExamRating());
                        break;
                    case "Зачет":
                        if (mark.getPassRating() != 0) {
                            rating = RatingConst.getNameByRating(mark.getPassRating());
                        } else if (mark.getPracticRating() != 0) {
                            rating = RatingConst.getNameByRating(mark.getPracticRating());
                        } else {
                            rating = RatingConst.getNameByRating(mark.getExamRating());
                        }
                        break;
                    case "Курсовой проект":
                        if (mark.getCourseProjectRating() != 0) {
                            rating = RatingConst.getNameByRating(mark.getCourseProjectRating());
                        } else {
                            rating = RatingConst.getNameByRating(mark.getCourseWorkRating());
                        }
                        break;
                    case "Курсовая работа":
                        if (mark.getCourseWorkRating() != 0) {
                            rating = RatingConst.getNameByRating(mark.getCourseWorkRating());
                        } else {
                            rating = RatingConst.getNameByRating(mark.getCourseProjectRating());
                        }
                        break;
                    case "Практика":
                        rating = RatingConst.getNameByRating(mark.getPracticRating());
                        break;
                }
                mark.setRating(RatingConst.getNameByRating(mark.getNewRating()));
                mark.setInfoFrom(mark.getInfoFrom() + rating + ")");
                mark.setInfoTo(mark.getInfoTo() + " " + mark.getFoc() + ")");


        }
        return new JRBeanCollectionDataSource(Collections.singletonList(model));
    }
}
