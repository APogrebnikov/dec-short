package org.edec.successful.model;

import org.edec.utility.constants.FormOfControlConst;

import java.util.ArrayList;
import java.util.List;

public class SubjectModel {
    private String subjectName;
    private FormOfControlConst foc;
    private Long idSubject;
    private Long idChair;
    private String chairFulltitle;
    private List<RatingModel> ratings = new ArrayList<>();

    /**
     * Получение последней оценки за дисциплину
     * ВАЖНО: работает только благодаря SRH.retake_count DESC, SRH.newrating DESC
     *
     * @return
     */
    public RatingModel getLastRating () {
        RatingModel maxModel = null;
        for (RatingModel rating : ratings) {
            if (maxModel == null || maxModel.getRating() == null) {
                maxModel = rating;
            } else {
                if (maxModel.getSigndate() != null && rating.getSigndate() != null) {
                    if (maxModel.getSigndate().compareTo(rating.getSigndate()) < 0) {
                        maxModel = rating;
                    }
                } else if (maxModel.getRating() < rating.getRating()) {
                    //Экстренный случай перезачета считается в пользу студента
                    maxModel = rating;
                }
            }
        }
        return maxModel;
    }

    public String getSubjectName () {
        return subjectName;
    }

    public void setSubjectName (String subjectName) {
        this.subjectName = subjectName;
    }

    public FormOfControlConst getFoc () {
        return foc;
    }

    public void setFoc (FormOfControlConst foc) {
        this.foc = foc;
    }

    public List<RatingModel> getRatings () {
        return ratings;
    }

    public void setRatings (List<RatingModel> ratings) {
        this.ratings = ratings;
    }

    public Long getIdSubject () {
        return idSubject;
    }

    public void setIdSubject (Long idSubject) {
        this.idSubject = idSubject;
    }

    public Long getIdChair () {
        return idChair;
    }

    public void setIdChair (Long idChair) {
        this.idChair = idChair;
    }

    public String getChairFulltitle () {
        return chairFulltitle;
    }

    public void setChairFulltitle (String chairFulltitle) {
        this.chairFulltitle = chairFulltitle;
    }
}
