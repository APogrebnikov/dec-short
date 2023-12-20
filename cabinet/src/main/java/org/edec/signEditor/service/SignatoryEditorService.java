package org.edec.signEditor.service;

import org.edec.signEditor.model.EmployeeModel;
import org.edec.signEditor.model.RuleModel;
import org.edec.signEditor.model.SignatoryModel;

import java.util.List;

public interface SignatoryEditorService {

    List<RuleModel> getRuleList (long idInstitute);

    List<EmployeeModel> getEmployeeList ();

    List<RuleModel> getRuleList (List<SignatoryModel> signatoryList);

    List<SignatoryModel> getSignatoryList (long idRule);

    List<SignatoryModel> filterSignatoryList (List<SignatoryModel> signatoryList, String fio, String role, String position);

    boolean addSignatory (SignatoryModel signatory);

    boolean updateSignatory (SignatoryModel signatory);

    boolean deleteSignatory (long idLre);
}
