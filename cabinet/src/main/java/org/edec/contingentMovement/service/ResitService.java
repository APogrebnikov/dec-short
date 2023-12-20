package org.edec.contingentMovement.service;

import org.edec.contingentMovement.model.ResitRatingModel;
import org.edec.studentPassport.model.StudentStatusModel;

import java.util.List;

public interface ResitService {
    List<ResitRatingModel> getListResitRatingModel(Long idStudent, Long idGroup, Boolean currentGroup);
    void autoResit(List<ResitRatingModel> listRatingOldGroup, List<ResitRatingModel> listRatingCurrentGroup);
    boolean deleteAllResit(StudentStatusModel student);
    boolean manualResit(ResitRatingModel oldRating, ResitRatingModel currentRating);
    boolean saveResit(List<ResitRatingModel> listOfResitRating, Long idCurrentUser);
    void fillDirectionCodeAndPreviousGroup(StudentStatusModel student);
}
