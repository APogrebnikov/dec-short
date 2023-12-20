package org.edec.contingentMovement.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.commons.model.RatingModel;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResitRatingModel extends RatingModel {
    private String position;
    private Boolean curSem;
    private Boolean selectedAsSecondTable = false;
    private String groupname, status;

    private ResitRatingModel resitRating;
    private String datepass;
    private Integer number;

    public static List<ResitRatingModel> getRatingByDoubleFoc(ResitRatingModel rating, List<ResitRatingModel> result) {
        if (rating.getExam()) {
            ResitRatingModel tmpModel = getTmpSubjectBySubjectModel(rating);
            tmpModel.setFoc("Экзамен");
            tmpModel.setFocInt(1);
            tmpModel.setRating(rating.getExamrating());
            result.add(tmpModel);
        }
        if (rating.getPass()) {
            ResitRatingModel tmpModel = getTmpSubjectBySubjectModel(rating);
            tmpModel.setFoc(rating.getType() == 0 ? "Зачет" : "Диф. зачет");
            tmpModel.setFocInt(2);
            tmpModel.setRating(rating.getPassrating());
            result.add(tmpModel);
        }
        if (rating.getCp()) {
            ResitRatingModel tmpModel = getTmpSubjectBySubjectModel(rating);
            tmpModel.setFoc("КП");
            tmpModel.setFocInt(3);
            tmpModel.setRating(rating.getCprating());
            result.add(tmpModel);
        }
        if (rating.getCw()) {
            ResitRatingModel tmpModel = getTmpSubjectBySubjectModel(rating);
            tmpModel.setFoc("КР");
            tmpModel.setFocInt(4);
            tmpModel.setRating(rating.getCwrating());
            result.add(tmpModel);
        }
        if (rating.getPractic()) {
            ResitRatingModel tmpModel = getTmpSubjectBySubjectModel(rating);
            tmpModel.setFoc("Практика");
            tmpModel.setFocInt(5);
            tmpModel.setRating(rating.getPracticrating());
            result.add(tmpModel);
        }

        return result;
    }

    private static ResitRatingModel getTmpSubjectBySubjectModel(ResitRatingModel rating) {
        ResitRatingModel model = new ResitRatingModel();
        model.setCurSem(rating.getCurSem());
        model.setGroupname(rating.getGroupname());
        model.setHoursCount(rating.getHoursCount());
        model.setIdDicSubj(rating.getIdDicSubj());
        model.setIdSR(rating.getIdSR());
        model.setIdSubj(rating.getIdSubj());
        model.setSemesternumber(rating.getSemesternumber());
        model.setStatus(rating.getStatus());
        model.setSubjectcode(rating.getSubjectcode());
        model.setSubjectname(rating.getSubjectname());
        model.setType(rating.getType());
        model.setHoursaudcount(rating.getHoursaudcount());
        return model;
    }
}
