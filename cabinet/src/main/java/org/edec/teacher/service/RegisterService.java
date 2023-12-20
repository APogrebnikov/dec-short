package org.edec.teacher.service;

import org.edec.teacher.model.register.DateModel;
import org.edec.teacher.model.register.RatingModel;
import org.edec.teacher.model.register.RegisterModel;
import org.edec.utility.constants.FormOfControlConst;

import java.util.List;

/**
 * Created by antonskripacev on 24.02.17.
 */
public interface RegisterService {
    RegisterModel getMainRegister(Long idLGSS, FormOfControlConst formOfControl);
    RegisterModel getMainRetakeRegister(Long idLGSS, FormOfControlConst formOfControl);
    List<RegisterModel> getListIndividualRegisters(Long idLGSS, FormOfControlConst formOfControl);
    RegisterModel getRegisterByIdRegister(Long idRegister);
    void setCourseProjectTheme(String theme, long isSessionRating);
    void setCourseWorkTheme(String theme, long isSessionRating);
    void updateSRHDateAndRating(long idSessionRatingHistory, int rating);
    boolean checkSRHSign(long idSessionRatingHistory);
    boolean checkExistSRH(long idSessionRating, int retakeCount);
    long createSRH(boolean exam, boolean pass, boolean cp, boolean cw, boolean practic,
                          int type, String status, int newRating, long idSessionRating, long idSystemUser, int retakeCount);
    DateModel  getDatePassweekAndSession (Long idRegister);
    boolean isHasSign(Long idHumanface);
}
