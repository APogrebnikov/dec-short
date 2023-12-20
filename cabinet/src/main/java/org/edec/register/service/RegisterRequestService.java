package org.edec.register.service;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.register.model.RegisterRequestModel;
import org.edec.register.model.SimpleRatingModel;
import org.edec.register.model.SubjectModel;
import org.edec.teacher.model.correctRequest.CorrectRequestModel;

import java.util.Date;
import java.util.List;

public interface RegisterRequestService {

    /*Запросы на открытие ведомостей*/

    List<RegisterRequestModel> getRegisterRequests (long idInstitute, int fos);

    List<RegisterRequestModel> getNewRegisterRequests (long idInstitute, int fos);

    boolean acceptRequest (long idRegisterRequest, int status);

    boolean denyRequest (long idRegisterRequest, String additionalInformation);

    List<RegisterRequestModel> filterRequestHistory (List<RegisterRequestModel> registerHistory, boolean isApproved, boolean isDenied,
                                                     boolean isOnlyUnderConsideration, String fioTeacher, String fioStudent,
                                                     String groupName, String subjectName);

    boolean openRetake (List<RegisterRequestModel> requests, Date dateOfBegin, Date dateOfEnd, String userFio);

    void sendTeacherNotification (String email, String msg, boolean getNotification);

    /*Корректировка оценок*/

    List<CorrectRequestModel> getCorrectRegisterRequests (long idInstitute, int fos, int retakeCount);

    boolean acceptCorrectRequest (CorrectRequestModel crm);

    boolean denyCorrectRequest (CorrectRequestModel crm);

    List<CorrectRequestModel> filterCorrectHistory (List<CorrectRequestModel> correctRequestHistory, boolean isApproved, boolean isDenied,
                                                     boolean isOnlyUnderConsideration, String fioTeacher, String fioStudent,
                                                     String groupName, String subjectName);

    List<SimpleRatingModel> getRatingHistoryForStudent(long idSRH);

    boolean setDeleteStatus (CorrectRequestModel correctRequest, TemplatePageCtrl template);
}
