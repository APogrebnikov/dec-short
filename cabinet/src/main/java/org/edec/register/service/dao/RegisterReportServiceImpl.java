package org.edec.register.service.dao;

import org.edec.register.manager.RegisterManager;
import org.edec.register.model.dao.RegisterReportModelEso;
import org.edec.register.model.report.RegisterDateModel;
import org.edec.register.model.report.RegisterModel;
import org.edec.register.service.RegisterReportService;
import org.edec.utility.converter.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegisterReportServiceImpl implements RegisterReportService {
    private RegisterManager manager = new RegisterManager();

    @Override
    public List<RegisterDateModel> getRegistersByPeriod(Date from, Date to, long idSem) {
        List<RegisterReportModelEso> registerReportModelEso =  manager.getListRegisterReport(from, to, idSem);

        return transformRegisterEsoToRegisterDateModel(registerReportModelEso);
    }

    private List<RegisterDateModel> transformRegisterEsoToRegisterDateModel(List<RegisterReportModelEso> listRegisterEso){
        RegisterDateModel registerDateModel = null;
        List<RegisterDateModel> listRegisterDateModel = new ArrayList<>();
        for (RegisterReportModelEso modelEso: listRegisterEso){
            if (registerDateModel == null || !registerDateModel.getDate().equals(DateConverter.convertDateToString(modelEso.getSignDate()))) {
                registerDateModel = new RegisterDateModel();
                registerDateModel.setDate(DateConverter.convertDateToString(modelEso.getSignDate()));
                registerDateModel.setRegisters(new ArrayList<>());

                listRegisterDateModel.add(registerDateModel);
            }

            RegisterModel registerModel = new RegisterModel();
            registerModel.setChair(modelEso.getFullTitle());
            registerModel.setExaminationDate(DateConverter.convertDateToString(modelEso.getExaminationDate()));
            registerModel.setFormOfCtrl(modelEso.getFoc());
            registerModel.setGroup(modelEso.getGroupName());
            registerModel.setRegisterNumber(modelEso.getRegisterNumber());
            registerModel.setSubject(modelEso.getSubjectName());
            registerModel.setSignDate(DateConverter.convertDateToString(modelEso.getSignDate()));
            registerModel.setTutor(modelEso.getSignatoryTutor());

            registerDateModel.getRegisters().add(registerModel);
        }

        return listRegisterDateModel;
    }
}