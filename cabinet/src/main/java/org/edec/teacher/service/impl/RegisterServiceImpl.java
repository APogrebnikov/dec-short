package org.edec.teacher.service.impl;

import org.edec.teacher.manager.EntityManagerRegister;
import org.edec.teacher.model.register.DateModel;
import org.edec.teacher.model.register.RatingModel;
import org.edec.teacher.model.register.RegisterModel;
import org.edec.teacher.model.register.RegisterRowModel;
import org.edec.teacher.service.RegisterService;
import org.edec.utility.DateUtility;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RegisterType;
import org.edec.utility.converter.DateConverter;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RegisterServiceImpl implements RegisterService {
    private EntityManagerRegister manager = new EntityManagerRegister();
    public String ERROR = "error";

    @Override
    public RegisterModel getMainRegister(Long idLGSS, FormOfControlConst foc) {
        List<Long> listRegId = manager.getListRegisterIdsBySubject(idLGSS, foc, RegisterType.MAIN);
        assert listRegId.size() <= 1;

        RegisterModel registerModel;

        if (listRegId.size() == 1) {
            registerModel = getRegisterByIdRegister(listRegId.get(0));
        } else {
            registerModel = getRegisterBySubjectAndFoc(idLGSS, foc, RegisterType.MAIN);
        }

        if (!registerModel.isRegisterSigned()) {
            filterStudentsInRegister(registerModel);
        } else {
            // Если преподаватель поставил оценки студентам, которые в следствии были отчислены/переведены/ушли в академ, srh на них остаются, убираем их
            registerModel.setListRegisterRow(
                    registerModel.getListRegisterRow().stream().filter(el -> el.getRetakeCount() != null && (el.getRetakeCount() > 0 || el.getRetakeCount() == -1))
                            .collect(Collectors.toList())
            );
        }

        return registerModel;
    }

    public boolean isMainRegisterSignable(Long idLGSS, Date complitionDate, Date dateBeginMainRegister, Date dateEndMainRegister) {
        Date today = new Date();


        if (complitionDate == null) {
            ERROR = "Не заполнена дата экзамена/зачета! Обратитесь в учебный отдел.";
            return false;
        } else if  (DateUtility.isSameDayOrNextWithWeekends(today, complitionDate)
                || DateUtility.isDayBelongsToPeriod(today, complitionDate, 2)
                ||  (dateBeginMainRegister != null && dateEndMainRegister != null && DateUtility.isBetweenDate(today, dateBeginMainRegister, dateEndMainRegister))) {
            return true;
        }else if (today.before(complitionDate)
                || (dateBeginMainRegister != null && today.before(dateBeginMainRegister))) {
            ERROR = "Дата сдачи предмета еще не наступила.";
            return false;
        } else if (today.after(DateUtility.getNextDateExcludeWeekend(complitionDate))
                || (dateEndMainRegister != null && today.after(DateUtility.getNextDateExcludeWeekend(dateEndMainRegister) ))){
            ERROR = "Срок подписнаия ведомости истек. Обратитесь в Учебный отдел!";
            return false;
        }
        return  false;
    }

    private void filterStudentsInRegister(RegisterModel registerModel) {
        if (registerModel == null) {
            return;
        }

        registerModel.setListRegisterRow(
                registerModel.getListRegisterRow().stream()
                        .filter(e -> !e.getDeducted() && !e.getAcademicLeave())
                        .filter(e -> e.getCurrentMark() == null || !(e.getCurrentMark() >= 3 || e.getCurrentMark() == 1))
                        .filter(e -> e.getIdDicGroup().equals(e.getIdCurrentDicGroup()))
                        .map(registerRowModel -> {
                             registerRowModel.setIsOutOfDate(registerModel.isRetakeOutOfDate());
                             registerRowModel.setSecondSignDateBegin(registerModel.getSecondSignDateBegin());
                             registerRowModel.setSecondSignDateEnd(registerModel.getSecondSignDateEnd());
                             registerRowModel.setIsSecondSignPeriodAvailable(registerModel.isSecondSignPeriodAvailable());
                             return registerRowModel;
                        })
                        .collect(Collectors.toList()));
    }

    private RegisterModel getRegisterBySubjectAndFoc(Long idLGSS, FormOfControlConst foc, RegisterType type) {
        List<RatingModel> ratings = manager.getListRatingsBySubjectAndType(idLGSS, foc, type);
        return transformRatingModelsToRegisterModel(ratings, foc);
    }

    @Override
    public RegisterModel getRegisterByIdRegister(Long idRegister) {
        List<RatingModel> ratings = manager.getListRatingsByIdRegister(idRegister);
        return transformRatingModelsToRegisterModel(ratings, null);
    }

    public Date updateSignDate(Long idRegister) {
        manager.updateSignDate(idRegister);
        return new Date();
    }

    public RegisterModel transformRatingModelsToRegisterModel(List<RatingModel> ratings, FormOfControlConst foc) {
        RegisterModel registerModel = new RegisterModel();

        if (ratings.size() != 0) {
            RatingModel ratingModel = ratings.get(0);
            registerModel.setCertNumber(ratingModel.getCertNumber());
            registerModel.setCompletionDate(ratingModel.getCompletionDate());
            registerModel.setCourse(ratingModel.getCourse());
            registerModel.setFinishDate(ratingModel.getStatusFinishDate());
            registerModel.setFoc(FormOfControlConst.getName(ratingModel.getFoc()));
            registerModel.setHoursCount(ratingModel.getHoursCount());
            registerModel.setIdRegisterESO(ratingModel.getIdRegister());
            registerModel.setIdRegisterMine(ratingModel.getIdRegisterMine());
            registerModel.setIdSemester(ratingModel.getIdSemester());
            registerModel.setIsCanceled(ratingModel.getIsCanceled());
            registerModel.setRegisterNumber(ratingModel.getRegisterNumber());
            registerModel.setRegisterURL(ratingModel.getRegisterUrl());
            registerModel.setSignatoryTutor(ratingModel.getSignatoryTutor());
            registerModel.setSignDate(ratingModel.getSignDate());
            registerModel.setStartDate(ratingModel.getStatusBeginDate());
            registerModel.setSecondSignDateBegin(ratingModel.getSecondSignBeginDate());
            registerModel.setSecondSignDateEnd(ratingModel.getSecondSignEndDate());
            registerModel.setSubjectName(ratingModel.getSubjectName());
            registerModel.setSynchStatus(ratingModel.getSynchStatus());
            registerModel.setThumbPrint(ratingModel.getThumbPrint());
            registerModel.setType(ratingModel.getType());
            registerModel.setGroupName(ratingModel.getGroupName());
            registerModel.setBeginDateMainRegister(ratingModel.getBeginDateMainRegister());
            registerModel.setEndDateMainRegister(ratingModel.getEndDateMainRegister());
            registerModel.setRegisterType(
                    ratingModel.getRetakeCount() != null ? RegisterType.getRegisterTypeByRetakeCount(ratingModel.getRetakeCount()) : null);
            registerModel.setSemesterStr(ratingModel.getSemesterStr());

            for (RatingModel rating : ratings) {
                if (foc == null) {
                    foc = registerModel.getFoc();
                }

                RegisterRowModel registerRowModel = new RegisterRowModel();

                registerRowModel.setIdSR(rating.getIdSessionRating());
                registerRowModel.setRetakeCount(rating.getRetakeCount());
                registerRowModel.setIdSRH(rating.getIdSessionRatingHistory());
                registerRowModel.setMark(rating.getNewRating());
                registerRowModel.setStudentFullName(rating.getStudentFIO());
                registerRowModel.setTheme(foc == FormOfControlConst.CP
                        ? rating.getCourseProjectTheme()
                        : (foc == FormOfControlConst.CW ? rating.getCourseWorkTheme() : null));
                registerRowModel.setAcademicLeave(rating.getAcademicLeaveStatus());
                registerRowModel.setChangeDateTime(rating.getChangeDateTime());
                registerRowModel.setCurrentMark(rating.getCurrentRating());
                registerRowModel.setDeducted(rating.getDeductedStatus());
                registerRowModel.setIdCurrentDicGroup(rating.getIdCurrentDicGroup());
                registerRowModel.setIdDicGroup(rating.getIdDicGroup());
                registerRowModel.setIdSSS(rating.getIdStudentSemesterStatus());
                registerRowModel.setNotActual(rating.getNotActual());
                registerRowModel.setRecordbookNumber(rating.getRecordbookNumber());
                registerRowModel.setVisitcount(ratingModel.getVisitcount());
                registerRowModel.setSkipcount(ratingModel.getSkipcount());
                registerRowModel.setEsogradecurrent(ratingModel.getEsogradecurrent());
                registerRowModel.setEsogrademax(ratingModel.getEsogrademax());

                registerModel.getListRegisterRow().add(registerRowModel);
            }
        }

        return registerModel;
    }

    @Override
    public RegisterModel getMainRetakeRegister(Long idLGSS, FormOfControlConst foc) {
        List<Long> listRegId = manager.getListRegisterIdsBySubject(idLGSS, foc, RegisterType.MAIN_RETAKE);
        assert listRegId.size() <= 1;

        RegisterModel registerModel;

        if (listRegId.size() == 1) {
            registerModel = getRegisterByIdRegister(listRegId.get(0));

            if (!registerModel.isRegisterSigned()) {
                filterStudentsInRegister(registerModel);
            } else {
                // Если преподаватель поставил оценки студентам, которые в следствии были отчислены/переведены/ушли в академ, srh на них остаются, убираем их
                // Условие проверки на случай подписи без ЭП
                if (!registerModel.getCertNumber().trim().equals("")) {
                    registerModel.setListRegisterRow(
                            registerModel.getListRegisterRow().stream()
                                    .filter(el -> el.getRetakeCount() != null && el.getRetakeCount() > 0)
                                    .map(registerRowModel -> {
                                        registerRowModel.setIsOutOfDate(registerModel.isRetakeOutOfDate());
                                        registerRowModel.setSecondSignDateBegin(registerModel.getSecondSignDateBegin());
                                        registerRowModel.setSecondSignDateEnd(registerModel.getSecondSignDateEnd());
                                        registerRowModel.setIsSecondSignPeriodAvailable(registerModel.isSecondSignPeriodAvailable());
                                        return registerRowModel;
                                    })
                                    .collect(Collectors.toList())
                    );
                }
            }

            return registerModel;
        }

        return new RegisterModel();
    }

    @Override
    public List<RegisterModel> getListIndividualRegisters(Long idLGSS, FormOfControlConst foc) {
        List<Long> listRegId = manager.getListRegisterIdsBySubject(idLGSS, foc, RegisterType.INDIVIDUAL_RETAKE);
        List<RegisterModel> registers = new ArrayList<>();

        for (Long id : listRegId) {
            registers.add(getRegisterByIdRegister(id));
        }

        return registers;
    }

    @Override
    public void setCourseProjectTheme(String theme, long isSessionRating) {
        manager.updateCPTheme(theme, isSessionRating);
    }

    @Override
    public void setCourseWorkTheme(String theme, long isSessionRating) {
        manager.updateCWTheme(theme, isSessionRating);
    }

    @Override
    public void updateSRHDateAndRating(long idSessionRatingHistory, int rating) {
        manager.updateSRHWithDateAndRating(idSessionRatingHistory, rating);
    }

    @Override
    public boolean checkSRHSign(long idSessionRatingHistory) {
        return manager.checkSRHSign(idSessionRatingHistory);
    }

    @Override
    public boolean checkExistSRH(long idSessionRating, int retakeCount) {
        return manager.checkExistSRH(idSessionRating, retakeCount);
    }

    @Override
    public long createSRH(boolean exam, boolean pass, boolean cp, boolean cw, boolean practic, int type, String status, int newRating,
                          long idSessionRating, long idSystemUser, int retakeCount) {
        return manager.createSRH(exam, pass, cp, cw, practic, type, status, newRating, idSessionRating, idSystemUser, retakeCount);
    }

    @Override
    public boolean isHasSign(Long idHumanface) {

        return manager.isHasSign(idHumanface);
    }

    @Override
    public DateModel getDatePassweekAndSession(Long idLGSS) {
        return manager.getDatePassweekAndSession(idLGSS);
    }

    public boolean setRegisterNumber(RegisterModel registerModel, String tutor, RegisterType registerType) {
        String listSRH = registerModel.getListRegisterRow().stream().map(RegisterRowModel::getIdSRH).collect(Collectors.toList()).toString()
                .replace("[", "{").replace("]", "}");

        return manager.setRegisterNumber(registerModel.getIdRegisterESO(), tutor, listSRH, registerModel.getIdSemester(),
                registerType.getSuffix()
        );
    }

    public EntityManagerRegister getManager() {
        return manager;
    }
}
