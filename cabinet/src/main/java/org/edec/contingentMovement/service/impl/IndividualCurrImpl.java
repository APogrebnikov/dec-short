package org.edec.contingentMovement.service.impl;

import javafx.util.Pair;
import org.edec.commons.manager.GroupManager;
import org.edec.commons.model.GroupDirectionModel;
import org.edec.contingentMovement.manager.ContingentMovementManager;
import org.edec.contingentMovement.model.ResitRatingModel;
import org.edec.contingentMovement.service.IndividualCurrService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class IndividualCurrImpl implements IndividualCurrService {

    private ContingentMovementManager contingentMovementManager = new ContingentMovementManager();
    private GroupManager groupManager = new GroupManager();

    @Override
    public List<GroupDirectionModel> getGroupDirectionBySemester (Long idSem) {
        return groupManager.getGroupDirectionBySemester(idSem);
    }

    @Override
    public List<ResitRatingModel> getRatingByStudentAndGroup (Long idSC, Long idDG) {
        List<ResitRatingModel> ratings = new ArrayList<>();
        for (ResitRatingModel ratingModel : contingentMovementManager.getRatingByStudentAndGroup(idSC, idDG)) {
            ResitRatingModel.getRatingByDoubleFoc(ratingModel, ratings);
        }

        return ratings;
    }

    @Override
    public List<ResitRatingModel> getSubjectByGroup (Long idDG) {
        List<ResitRatingModel> subjects = new ArrayList<>();
        for (ResitRatingModel rating : contingentMovementManager.getSubjectByGroup(idDG)) {
            ResitRatingModel.getRatingByDoubleFoc(rating, subjects);
        }
        return subjects;
    }

    @Override
    public void setRatingBySubject (ResitRatingModel ratingTo, List<ResitRatingModel> ratings) {
        ResitRatingModel foundRating = ratings.stream()
                                              .filter(ratingFrom -> ratingFrom.getSemesternumber().equals(ratingTo.getSemesternumber())
                                                                    //Совпадает семестр
                                                                    && ratingFrom.getSubjectname().equals(ratingTo.getSubjectname())
                                                                    //Совпадают назания
                                                                    && (ratingFrom.getRating() == 1 || ratingFrom.getRating() >= 3)
                                                                    //Только положительная оценка
                                                                    && ((ratingFrom.getHoursCount() / ratingTo.getHoursCount()) >= 0.7)
                                                                    // Количество часов может быть на 30% меньше
                                                                    && ((ratingFrom.getFoc().equals(ratingTo.getFoc()) &&
                                                                         ratingFrom.getType().equals(ratingTo.getType()))
                                                                        //Совпадение формы контроля
                                                                        || (ratingFrom.getSubjectname().equals("Экзамен") &&
                                                                            ratingTo.equals("Зачет")) ||
                                                                        (ratingFrom.getSubjectname().equals("Зачет") &&
                                                                         ratingFrom.getType() == 1 && ratingTo.equals("Практика")) ||
                                                                        (ratingFrom.getSubjectname().equals("Практика") &&
                                                                         ratingTo.getType() == 1 && ratingTo.equals("Зачет"))))
                                              .findFirst()
                                              .orElse(null);
        if (foundRating == null) {
            return;
        }
        if (foundRating.getSubjectname().equals("Экзамен") && ratingTo.equals("Зачет") && ratingTo.getType() == 0) {
            ratingTo.setRating(1);
        } else {
            ratingTo.setRating(foundRating.getRating());
        }
        ratingTo.setResitRating(foundRating);
    }

    @Override
    public Pair<Date, Date> getLastSessionDates(String fio){
        return contingentMovementManager.getLastSessionDates(fio);
    }
}
