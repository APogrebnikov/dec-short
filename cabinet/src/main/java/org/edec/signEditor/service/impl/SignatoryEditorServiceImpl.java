package org.edec.signEditor.service.impl;

import org.edec.signEditor.manager.SignatoryEditorManager;
import org.edec.signEditor.model.EmployeeModel;
import org.edec.signEditor.model.RuleModel;
import org.edec.signEditor.model.SignatoryModel;
import org.edec.signEditor.model.dao.EmployeeModelEso;
import org.edec.signEditor.model.dao.SignatoryModelEso;
import org.edec.signEditor.service.SignatoryEditorService;
import org.edec.utility.constants.ActionRuleConst;

import java.util.*;

public class SignatoryEditorServiceImpl implements SignatoryEditorService {

    private SignatoryEditorManager manager = new SignatoryEditorManager();

    private SignatoryModel createSignatoryModel (SignatoryModelEso eso) {
        SignatoryModel model = new SignatoryModel();
        EmployeeModel employeeModel = new EmployeeModel();

        employeeModel.setIdEmp(eso.getIdEmp());
        employeeModel.setFio(eso.getFamily() + " " + eso.getName() + " " + eso.getPatronymic());

        model.setRole(eso.getRole());
        model.setEmployee(employeeModel);
        model.setIdLre(eso.getIdLre());
        model.setIdRule(eso.getIdRule());
        model.setRule(eso.getRule());
        model.setPosition(eso.getPos());
        model.setPost(eso.getPost());
        model.setPrint(eso.getPrint());
        model.setSubquery(eso.getSubquery());
        model.setSign(eso.getSign());
        model.setFos(eso.getFos());

        return model;
    }

    private EmployeeModel createEmployeeModel (EmployeeModelEso eso) {
        EmployeeModel model = new EmployeeModel();
        model.setIdEmp(eso.getIdEmp());
        model.setFio(eso.getFamily() + " " + eso.getName() + " " + eso.getPatronymic());
        return model;
    }

    @Override
    public List<RuleModel> getRuleList (long idInstitute) {
        return manager.getRuleList(idInstitute);
    }

    @Override
    public List<EmployeeModel> getEmployeeList () {
        List<EmployeeModel> employeeModelList = new ArrayList<>();
        List<EmployeeModelEso> employeeModelEsoList = manager.getEmployeeList();
        for (EmployeeModelEso employeeModelEso : employeeModelEsoList) {
            employeeModelList.add(createEmployeeModel(employeeModelEso));
        }
        return employeeModelList;
    }

    @Override
    public List<RuleModel> getRuleList (List<SignatoryModel> signatoryList) {
        List<RuleModel> ruleList = new ArrayList<>();

        for (SignatoryModel signatoryModel : signatoryList) {

            RuleModel ruleModel = new RuleModel();
            ruleModel.setIdRule(signatoryModel.getIdRule());
            ruleModel.setRule(signatoryModel.getRule());

            if (!ruleList.contains(ruleModel)) {
                ruleList.add(ruleModel);
            }
        }

        return ruleList;
    }

    @Override
    public List<SignatoryModel> getSignatoryList (long idRule) {
        List<SignatoryModel> signatoryList = new ArrayList<>();
        List<SignatoryModelEso> signatoryEsoList = manager.getSignatoryList(idRule);
        for (SignatoryModelEso signatoryModelEso : signatoryEsoList) {
            signatoryList.add(createSignatoryModel(signatoryModelEso));
        }
        return signatoryList;
    }

    @Override
    public List<SignatoryModel> filterSignatoryList (List<SignatoryModel> signatoryList, String fio, String role,
                                                     String position) {

        List<SignatoryModel> filteredSignatoryList = new ArrayList<>(signatoryList);

        for (int i = 0; i < filteredSignatoryList.size(); i++) {
            if (!fio.equals("")) {
                if (!filteredSignatoryList.get(i).getEmployee().getFio().toLowerCase().contains(fio.toLowerCase())) {
                    filteredSignatoryList.remove(i);
                    i--;
                    continue;
                }
            }

            if (!role.equals("")) {
                if (!ActionRuleConst.getName(filteredSignatoryList.get(i).getRole()).getName().toLowerCase().contains(role.toLowerCase())) {
                    filteredSignatoryList.remove(i);
                    i--;
                    continue;
                }
            }

            if (!position.equals("")) {
                if (filteredSignatoryList.get(i).getPosition() != Integer.parseInt(position)) {
                    filteredSignatoryList.remove(i);
                    i--;
                    continue;
                }
            }
        }

        return filteredSignatoryList;
    }

    @Override
    public boolean addSignatory (SignatoryModel signatory) {
        return manager.addSignatory(signatory);
    }

    @Override
    public boolean updateSignatory (SignatoryModel signatory) {
        return manager.updateSignatory(signatory);
    }

    @Override
    public boolean deleteSignatory (long idLre) {
        return manager.removeSignatory(idLre);
    }
}
