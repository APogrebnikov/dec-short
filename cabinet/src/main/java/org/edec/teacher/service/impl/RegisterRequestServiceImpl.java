package org.edec.teacher.service.impl;

import org.edec.register.manager.RegisterManager;
import org.edec.register.model.RetakeModel;
import org.edec.register.model.SessionRatingHistoryModel;
import org.edec.register.model.dao.RetakeModelEso;
import org.edec.teacher.manager.EntityManagerCorrectRequest;
import org.edec.teacher.manager.EntityManagerRegisterRequest;
import org.edec.teacher.model.correctRequest.CorrectRequestModel;
import org.edec.teacher.model.dao.RegisterRequestESOModel;
import org.edec.teacher.model.register.RegisterRowModel;
import org.edec.teacher.model.registerRequest.RegisterRequestModel;
import org.edec.teacher.model.registerRequest.StudentModel;
import org.edec.teacher.service.RegisterRequestService;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RegisterConst;
import org.edec.utility.constants.RegisterRequestStatusConst;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RegisterRequestServiceImpl implements RegisterRequestService {

    private EntityManagerRegisterRequest manager = new EntityManagerRegisterRequest();
    private EntityManagerCorrectRequest managerCorrect = new EntityManagerCorrectRequest();

    private RegisterManager registerManager = new RegisterManager();

    @Override
    public List<StudentModel> getRequestAvailableStudents(long idLgss, int foc) {

        List<StudentModel> studentList = new ArrayList<>();

        List<RetakeModel> listRetakes = separateListRetakesByIdSRH(
                registerManager.getListRatingByListGroupSubjects(Long.toString(idLgss), getFokQueryForSubject(foc), getFocQueryForLeftJoin(foc)
        ));

        long idGroup = manager.getGroupByIdLgss(idLgss);

        retakeCycle:
        for (RetakeModel retakeModel : listRetakes) {

            for (SessionRatingHistoryModel historyModel : retakeModel.getListSRH()) {
                if (historyModel.getIdSRH() != null && historyModel.getRetakeCount() == RegisterConst.TYPE_RETAKE_MAIN_NOT_SIGNED) {
                    continue retakeCycle;
                }
            }

            // Человек должен существовать в текущем семестре
            if (retakeModel.getDeductedCurSem() == null) {
                continue;
            }
            //
            //            // Человек не должен быть отчислен в текущем семестре
            if (retakeModel.getDeductedCurSem() == true) {
                continue;
            }

            //
            //            // если уже открыта индивидуальная пересдача и она не (просрочена и с оценкой) - не открывать
            for (SessionRatingHistoryModel historyModel : retakeModel.getListSRH()) {
                if (historyModel.getIdSRH() != null &&
                    historyModel.getRetakeCount().intValue() == RegisterConst.TYPE_RETAKE_INDIV_NOT_SIGNED) {
                    if (!(retakeModel.isRetakeOutOfDate() && retakeModel.getNewRating() != null)) {
                        continue retakeCycle;
                    }
                }
            }

            //
            //            // Не должно быть оценки по предмету

            switch (FormOfControlConst.getName(foc)) {
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

            //
            //            // Если это не текущая группа
            if (!retakeModel.getIdCurDicGroup().equals(idGroup)) {
                continue;
            }
            //
            //            // Человек не должен быть в академическом отпуске в текущем семестре
            if (retakeModel.getAcademicLeaveCurSem() == true) {
                continue;
            }

            StudentModel student = new StudentModel();
            student.setIdSSS(retakeModel.getIdSSS());
            student.setFio(retakeModel.getFio());
            studentList.add(student);
        }

        return studentList;
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

    @Override
    public List<RegisterRequestModel> getRegisterRequestHistory(long idHum, long idLgss, int foc) {
        return getRequestList(manager.getRequestHistory(idHum, idLgss, foc));
    }

    @Override
    public boolean sendRequest(RegisterRequestModel registerRequestModel) {
        return manager.createRequest(registerRequestModel);
    }

    @Override
    public List<StudentModel> filterStudentsByRequests(List<StudentModel> students, List<RegisterRequestModel> registerHistory) {
        for (int i = 0; i < registerHistory.size(); i++) {
            if (registerHistory.get(i).getStatus() == RegisterRequestStatusConst.UNDER_CONSIDERATION) {
                for (int j = 0; j < students.size(); j++) {
                    if (students.get(j).getIdSSS().equals(registerHistory.get(i).getStudent().getIdSSS())) {
                        students.remove(j);
                        j--;
                        break;
                    }
                }
            }
        }
        return students;
    }

    @Override
    public List<RegisterRequestModel> filterRequestHistoryByStatus(List<RegisterRequestModel> registerHistory) {
        return registerHistory.stream().filter(registerRequestModel -> registerRequestModel.getStatus()
                                                                                           .equals(RegisterRequestStatusConst.UNDER_CONSIDERATION))
                              .collect(Collectors.toList());
    }

    public List<RegisterRequestModel> getRequestList(List<RegisterRequestESOModel> requestESOModels) {
        List<RegisterRequestModel> list = new ArrayList<>();
        for (RegisterRequestESOModel requestESOModel : requestESOModels) {
            RegisterRequestModel request = new RegisterRequestModel();

            StudentModel studentModel = new StudentModel();
            studentModel.setIdSSS(requestESOModel.getIdSss());
            studentModel.setFio(requestESOModel.getFamily() + " " + requestESOModel.getName() + " " + requestESOModel.getPatronymic());
            request.setStudent(studentModel);

            request.setApplyingDate(requestESOModel.getApplyingDate());
            request.setAdditionalInfo(requestESOModel.getAdditionalInfo());
            request.setStatus(requestESOModel.getStatus());

            list.add(request);
        }
        return list;
    }

    @Override
    public boolean sendCorrectRequest(CorrectRequestModel correctRequestModel) {
        if (correctRequestModel.getId() != null) {
            return managerCorrect.updateRequest(correctRequestModel);
        } else {
            return managerCorrect.createRequest(correctRequestModel);
        }
    }

    @Override
    public boolean updateCorrectAfterSign(CorrectRequestModel correctRequestModel) {
        return managerCorrect.updateCorrectAfterSign(correctRequestModel);
    }

    @Override
    public List<Long> checkRequestsForExisting(List<Long> innerIds) {
        return managerCorrect.checkRequestsForExisting(innerIds);
    }

    @Override
    public void fillCorrectRequests(List<RegisterRowModel> studentsList) {

        List<Long> listForCheckIds = studentsList.stream().map(RegisterRowModel::getIdSRH).collect(Collectors.toList());

        if(!listForCheckIds.isEmpty()) {
            List<CorrectRequestModel> existingCorrectRequestList = managerCorrect.getCorrectRequest(listForCheckIds);

            if (existingCorrectRequestList != null && !existingCorrectRequestList.isEmpty()) {

                for (CorrectRequestModel existingCorrectRequest : existingCorrectRequestList) {
                    for (RegisterRowModel student : studentsList) {
                        if (student.getIdSRH().equals(existingCorrectRequest.getIdSRH())) {
                            student.setCurCorrectRequest(existingCorrectRequest);
                        }
                    }
                }
            }
        }
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
}
