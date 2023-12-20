package org.edec.register.service.impl;

import lombok.extern.log4j.Log4j;
import org.edec.fcmNotifications.model.NotificationModel;
import org.edec.fcmNotifications.service.fcmNotificationService;
import org.edec.fcmNotifications.service.impl.fcmNotificationServiceImpl;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.register.manager.RegisterManager;
import org.edec.register.manager.RegisterRequestManager;
import org.edec.register.model.*;
import org.edec.register.model.dao.RetakeModelEso;
import org.edec.register.service.RegisterRequestService;
import org.edec.teacher.model.correctRequest.CorrectRequestModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RegisterConst;
import org.edec.utility.constants.RegisterRequestStatusConst;
import org.edec.utility.email.Sender;
import org.edec.utility.report.manager.CorrectReportDAO;
import org.zkoss.zk.ui.Executions;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.edec.register.service.RegisterService.INDIVIDUAL_RETAKE;

@Log4j
public class RegisterRequestServiceImpl implements RegisterRequestService {

    private Sender sender;
    {
        try {
            sender = new Sender();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    RegisterRequestManager manager = new RegisterRequestManager();
    private RegisterManager registerManager = new RegisterManager();
    private fcmNotificationService fcmService = new fcmNotificationServiceImpl();

    @Override
    public List<RegisterRequestModel> getRegisterRequests(long idInstitute, int fos) {
        return manager.getAllRegisterRequests(idInstitute, fos, RegisterRequestStatusConst.ALL);
    }

    @Override
    public List<RegisterRequestModel> getNewRegisterRequests(long idInstitute, int fos) {
        return manager.getAllRegisterRequests(idInstitute, fos, RegisterRequestStatusConst.UNDER_CONSIDERATION);
    }

    @Override
    public boolean acceptRequest(long idRegisterRequest, int status) {
        return manager.updateRequestStatus(idRegisterRequest, status, new Date(), "");
    }

    @Override
    public boolean denyRequest(long idRegisterRequest, String additionalInformation) {
        return manager.updateRequestStatus(idRegisterRequest, RegisterRequestStatusConst.DENIED, new Date(), additionalInformation);
    }

    @Override
    public List<RegisterRequestModel> filterRequestHistory(List<RegisterRequestModel> registerHistory, boolean isApproved, boolean isDenied,
                                                           boolean isOnlyUnderConsideration, String fioTeacher, String fioStudent,
                                                           String groupName, String subjectName) {

        List<RegisterRequestModel> filteredRegisterHistory = new ArrayList<>(registerHistory);

        for (int i = 0; i < filteredRegisterHistory.size(); i++) {

            if (!isApproved && !isDenied && !isOnlyUnderConsideration) {
            } else {
                if (!isOnlyUnderConsideration &&
                    (filteredRegisterHistory.get(i).getStatus() == RegisterRequestStatusConst.UNDER_CONSIDERATION)) {
                    filteredRegisterHistory.remove(i);
                    i--;
                    continue;
                }

                if (!isApproved && (filteredRegisterHistory.get(i).getStatus() == RegisterRequestStatusConst.APPROVED)) {
                    filteredRegisterHistory.remove(i);
                    i--;
                    continue;
                }

                if (!isDenied && (filteredRegisterHistory.get(i).getStatus() == RegisterRequestStatusConst.DENIED)) {
                    filteredRegisterHistory.remove(i);
                    i--;
                    continue;
                }
            }

            if (!fioTeacher.equals("")) {
                if (!filteredRegisterHistory.get(i).getTeacherFullName().toLowerCase().contains(fioTeacher.toLowerCase())) {
                    filteredRegisterHistory.remove(i);
                    i--;
                    continue;
                }
            }

            if (!fioStudent.equals("")) {
                if (!filteredRegisterHistory.get(i).getStudentFullName().toLowerCase().contains(fioStudent.toLowerCase())) {
                    filteredRegisterHistory.remove(i);
                    i--;
                    continue;
                }
            }

            if (!groupName.equals("")) {
                if (!filteredRegisterHistory.get(i).getGroupName().toLowerCase().contains(groupName.toLowerCase())) {
                    filteredRegisterHistory.remove(i);
                    i--;
                    continue;
                }
            }

            if (!subjectName.equals("")) {
                if (!filteredRegisterHistory.get(i).getSubjectName().toLowerCase().contains(subjectName.toLowerCase())) {
                    filteredRegisterHistory.remove(i);
                    i--;
                    continue;
                }
            }
        }

        return filteredRegisterHistory;
    }

    @Override
    public boolean openRetake(List<RegisterRequestModel> requests, Date dateOfBegin, Date dateOfEnd, String userFio) {

        List<RegisterRequestModel> acceptedRequests = new ArrayList<>();

        requestCycle:
        for (RegisterRequestModel requestModel : requests) {

            if (requestModel.getStatus() == RegisterRequestStatusConst.APPROVED) {
                continue requestCycle;
            }

            List<RetakeModel> listRetakes = separateListRetakesByIdSRH(registerManager.getListRatingByListGroupSubjects(
                    Long.toString(requestModel.getIdLgss()), getFokQueryForSubject(requestModel.getFoc()),
                    getFocQueryForLeftJoin(requestModel.getFoc())
            ));

            retakeCycle:
            for (RetakeModel retakeModel : listRetakes) {

                if (!retakeModel.getIdSSS().equals(requestModel.getIdSss())) {
                    continue retakeCycle;
                }

                for (SessionRatingHistoryModel historyModel : retakeModel.getListSRH()) {
                    if (historyModel.getIdSRH() != null && historyModel.getRetakeCount() == RegisterConst.TYPE_RETAKE_MAIN_NOT_SIGNED) {
                        continue retakeCycle;
                    }
                }

                if (retakeModel.getDeductedCurSem() == null) {
                    continue;
                }

                if (retakeModel.getDeductedCurSem() == true) {
                    continue;
                }

                for (SessionRatingHistoryModel historyModel : retakeModel.getListSRH()) {
                    if (historyModel.getIdSRH() != null &&
                        historyModel.getRetakeCount().intValue() == RegisterConst.TYPE_RETAKE_INDIV_NOT_SIGNED) {
                        if (!(retakeModel.isRetakeOutOfDate() && retakeModel.getNewRating() != null)) {
                            continue retakeCycle;
                        }
                    }
                }

                switch (FormOfControlConst.getName(requestModel.getFoc())) {
                    case EXAM:
                        if (!isMarkNegative(retakeModel.getExamRating())) {
                            continue;
                        }
                        break;
                    case PASS:
                        if (!isMarkNegative(retakeModel.getPassRating())) {
                            continue;
                        }
                        break;
                    case CP:
                        if (!isMarkNegative(retakeModel.getCpRating())) {
                            continue;
                        }
                        break;
                    case CW:
                        if (!isMarkNegative(retakeModel.getCwRating())) {
                            continue;
                        }
                        break;
                    case PRACTIC:
                        if (!isMarkNegative(retakeModel.getPracticRating())) {
                            continue;
                        }
                        break;
                    default:
                        continue;
                }

                if (!retakeModel.getIdCurDicGroup().equals(requestModel.getIdGroup())) {
                    continue;
                }

                if (retakeModel.getAcademicLeaveCurSem() == true) {
                    continue;
                }

                //записываем модели студента и пересдач для вызова метода открытия ведомости
                List<RetakeModel> retakesForOpen = new ArrayList<>();
                List<StudentModel> studentsForRetake = new ArrayList<>();
                StudentModel student = new StudentModel();

                student.setIdSR(retakeModel.getIdSR());
                studentsForRetake.add(student);

                RetakeModel retake = new RetakeModel();
                retake.setStudents(studentsForRetake);
                retake.setIdSemester(retakeModel.getIdSemester());
                retake.setType(retakeModel.getType());
                retake.setTypeRetake(RegisterConst.TYPE_RETAKE_INDIV_NOT_SIGNED);

                retakesForOpen.add(retake);

                if (!registerManager
                        .createRetakeForModel(FormOfControlConst.getName(requestModel.getFoc()), retakesForOpen, dateOfBegin, dateOfEnd)) {
                    log.error("Не удалось открыть индивидуальную пересдачу" + "; студенту " + retakeModel.getFio() + "; группа " +
                              requestModel.getGroupName() + "; по предмету " + requestModel.getSubjectName() + "; пользователь " + userFio);
                    return false;
                } else {
                    log.info("Пользователь " + userFio + " открыл индивидуальную пересдачу" + "; студенту " + retakeModel.getFio() +
                             "; группа " + requestModel.getGroupName() + "; по предмету " + requestModel.getSubjectName());

                    acceptRequest(requestModel.getIdRegisterRequest(), RegisterRequestStatusConst.APPROVED);

                    acceptedRequests.add(requestModel);
                    return true;
                }
            }
        }

        //   createNotificationForTeacher(acceptedRequests);

        return false;
    }

    private void createNotificationForTeacher(List<RegisterRequestModel> acceptedRequests) {

        while (acceptedRequests.size() != 0) {
            StringBuilder msg = new StringBuilder(
                    "Ваша заявка на открытие ведомостей одобрена!\nВедомость открыта у следующих студентов:\n");
            String fio = acceptedRequests.get(0).getTeacherFullName();
            String email = acceptedRequests.get(0).getEmail();
            Boolean getNotification = acceptedRequests.get(0).isGetNotification();
            int studentIndex = 1;

            for (int i = 0; i < acceptedRequests.size(); i++) {
                if (acceptedRequests.get(i).getTeacherFullName().equals(fio)) {
                    msg.append("\t").append(studentIndex).append(". ").append(acceptedRequests.get(i).getStudentFullName()).append(" - \"")
                       .append(acceptedRequests.get(i).getSubjectName()).append("\"\n");

                    acceptedRequests.remove(i);
                    i--;

                    studentIndex++;
                }
            }

            sendTeacherNotification(email, msg.toString(), getNotification);
        }
    }

    @Override
    public void sendTeacherNotification(String email, String msg, boolean getNotification) {
        if (sender != null && getNotification && email != null && !email.equals("") &&
            !Executions.getCurrent().getServerName().equals("localhost")) {
            try {
                sender.sendSimpleMessage(email, "Заявка на открытие пересдачи", msg);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<CorrectRequestModel> getCorrectRegisterRequests(long idInstitute, int fos, int retakeCount) {
        return manager.getAllCorrectRequests(idInstitute, fos, 0, retakeCount);
    }

    @Override
    public boolean acceptCorrectRequest(CorrectRequestModel crm) {
        try {
            // Update Correction
            crm.setStatus(RegisterRequestStatusConst.APPROVED);
            crm.setNotificationStatus(1);

            NotificationModel model = new NotificationModel();
            model.setBody("Заявка на открытие ведомости для студента " + crm.getStudentFIO() + " группы " + crm.getGroupName() +
                          " по предмету '" + crm.getSubjectName() + "' одобрена.");
            model.setClickAction("http://dec.sfu-kras.ru/cabinet/teacher");
            model.setIcon("Icon");
            model.setTitle("Заявка на открытие ведомости одобрена.");

            fcmService.sendMessageToPerson(crm.getIdHumanface(), model);

            manager.updateCorrectRequest(crm);
            // Update register
            manager.updateSRHforCorrect(crm);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean denyCorrectRequest(CorrectRequestModel crm) {
        crm.setStatus(RegisterRequestStatusConst.DENIED);
        crm.setNotificationStatus(2);

        NotificationModel model = new NotificationModel();
        model.setBody(
                "Заявка на открытие ведомости для студента " + crm.getStudentFIO() + " группы " + crm.getGroupName() + " по предмету '" +
                crm.getSubjectName() + "' отклонена.");
        model.setClickAction("http://dec.sfu-kras.ru/cabinet/teacher");
        model.setIcon("Icon");
        model.setTitle("Заявка на открытие ведомости отклонена.");

        fcmService.sendMessageToPerson(crm.getIdHumanface(), model);

        manager.updateCorrectRequest(crm);
        return true;
    }

    @Override
    public List<CorrectRequestModel> filterCorrectHistory(List<CorrectRequestModel> correctRequestHistory, boolean isApproved,
                                                          boolean isDenied, boolean isOnlyUnderConsideration, String fioTeacher,
                                                          String fioStudent, String groupName, String subjectName) {
        List<CorrectRequestModel> filteredCorrectHistory = new ArrayList<>(correctRequestHistory);

        for (int i = 0; i < filteredCorrectHistory.size(); i++) {

            if (!isApproved && !isDenied && !isOnlyUnderConsideration) {
            } else {
                if (!isOnlyUnderConsideration &&
                    (filteredCorrectHistory.get(i).getStatus() == RegisterRequestStatusConst.UNDER_CONSIDERATION)) {
                    filteredCorrectHistory.remove(i);
                    i--;
                    continue;
                }

                if (!isApproved && (filteredCorrectHistory.get(i).getStatus() == RegisterRequestStatusConst.APPROVED)) {
                    filteredCorrectHistory.remove(i);
                    i--;
                    continue;
                }

                if (!isDenied && (filteredCorrectHistory.get(i).getStatus() == RegisterRequestStatusConst.DENIED)) {
                    filteredCorrectHistory.remove(i);
                    i--;
                    continue;
                }
            }

            if (!fioTeacher.equals("")) {
                if (!filteredCorrectHistory.get(i).getTeacherFIO().toLowerCase().contains(fioTeacher.toLowerCase())) {
                    filteredCorrectHistory.remove(i);
                    i--;
                    continue;
                }
            }

            if (!fioStudent.equals("")) {
                if (!filteredCorrectHistory.get(i).getStudentFIO().toLowerCase().contains(fioStudent.toLowerCase())) {
                    filteredCorrectHistory.remove(i);
                    i--;
                    continue;
                }
            }

            if (!groupName.equals("")) {
                if (!filteredCorrectHistory.get(i).getGroupName().toLowerCase().contains(groupName.toLowerCase())) {
                    filteredCorrectHistory.remove(i);
                    i--;
                    continue;
                }
            }

            if (!subjectName.equals("")) {
                if (!filteredCorrectHistory.get(i).getSubjectName().toLowerCase().contains(subjectName.toLowerCase())) {
                    filteredCorrectHistory.remove(i);
                    i--;
                    continue;
                }
            }
        }

        return filteredCorrectHistory;
    }

    @Override
    public List<SimpleRatingModel> getRatingHistoryForStudent(long idSRH) {
        return manager.getRatingHistoryForStudent(idSRH);
    }

    private String getFokQueryForSubject(int foc) {
        switch (FormOfControlConst.getName(foc)) {
            case EXAM:
                return "is_exam = 1";
            case PASS:
                return "is_pass = 1";
            case CP:
                return "is_courseproject = 1";
            case CW:
                return "is_coursework = 1";
            case PRACTIC:
                return "is_practic = 1";
            default:
                return null;
        }
    }

    private List<RetakeModel> separateListRetakesByIdSRH(List<RetakeModelEso> listESO) {
        List<RetakeModel> retakeModels = new ArrayList<>();
        RetakeModel prevModel = null;
        for (RetakeModelEso retakeModelEso : listESO) {
            if (prevModel != null && prevModel.getIdSR().equals(retakeModelEso.getIdSR())) {
                prevModel.getListSRH().add(createSessionRatingHistoryModel(retakeModelEso));
            } else {
                prevModel = createRetakeModel(retakeModelEso);
                prevModel.getListSRH().add(createSessionRatingHistoryModel(retakeModelEso));
                retakeModels.add(prevModel);
            }
        }

        return retakeModels;
    }

    private SessionRatingHistoryModel createSessionRatingHistoryModel(RetakeModelEso retakeModelEso) {
        SessionRatingHistoryModel srhModel = new SessionRatingHistoryModel();
        srhModel.setIdSRH(retakeModelEso.getIdSRH());
        srhModel.setRetakeCount(retakeModelEso.getRetakeCount());
        return srhModel;
    }

    private RetakeModel createRetakeModel(RetakeModelEso retakeModelEso) {
        RetakeModel retakeModel = new RetakeModel();
        retakeModel.setAcademicLeaveCurSem(retakeModelEso.getAcademicLeave());
        retakeModel.setIdSemester(retakeModelEso.getIdSemester());
        retakeModel.setCpRating(retakeModelEso.getCpRating());
        retakeModel.setCwRating(retakeModelEso.getCwRating());
        retakeModel.setDeductedCurSem(retakeModelEso.getDeductedCurSem());
        retakeModel.setAcademicLeaveCurSem(retakeModelEso.getAcademicLeaveCurSem());
        retakeModel.setExamRating(retakeModelEso.getExamRating());
        retakeModel.setFio(retakeModelEso.getFio());
        retakeModel.setIdCurDicGroup(retakeModelEso.getIdCurDicGroup());
        retakeModel.setIdSR(retakeModelEso.getIdSR());
        retakeModel.setIdSSS(retakeModelEso.getIdSSS());
        retakeModel.setListenerCurSem(retakeModelEso.getListenerCurSem());
        retakeModel.setTransferedStudent(retakeModelEso.getTransferedStudent());
        retakeModel.setTransferedStudentCurSem(retakeModelEso.getTransferedStudentCurSem());
        retakeModel.setPracticRating(retakeModelEso.getPracticRating());
        retakeModel.setPassRating(retakeModelEso.getPassRating());
        retakeModel.setType(retakeModelEso.getType());
        retakeModel.setBeginDate(retakeModelEso.getBeginDate());
        retakeModel.setFinishDate(retakeModelEso.getFinishDate());
        retakeModel.setNewRating(retakeModelEso.getNewRating());
        return retakeModel;
    }

    private boolean isMarkNegative(Integer mark) {
        if (mark == null) {
            return true;
        }

        if (mark.intValue() < 3 && mark.intValue() != 1) {
            return true;
        }

        return false;
    }

    private String getFocQueryForLeftJoin(int foc) {
        switch (FormOfControlConst.getName(foc)) {
            case EXAM:
                return "srh.is_exam = 1";
            case PASS:
                return "srh.is_pass = 1";
            case CP:
                return "srh.is_courseproject = 1";
            case CW:
                return "srh.is_coursework = 1";
            case PRACTIC:
                return "srh.is_practic = 1";
            default:
                return null;
        }
    }

    @Override
    public boolean setDeleteStatus(CorrectRequestModel correctRequest, TemplatePageCtrl template) {
        if (manager.setDeleteStatus(correctRequest.getId())) {
            log.info("Заявка на корректировку с id " + correctRequest.getId() + " по дисциплине " + correctRequest.getSubjectName() +
                     " для студента " + correctRequest.getStudentFIO() + " успешно удалена." + " Удалил заявку: " +
                     template.getCurrentUser().getFio());
            return true;
        } else {
            log.warn("Не удалось удалить заявку на корректировку с id " + correctRequest.getId() + " по дисциплине " +
                     correctRequest.getSubjectName() + " для студента " + correctRequest.getStudentFIO() + ". Попытался удалить заявку: " +
                     template.getCurrentUser().getFio());
            return false;
        }
    }
}
