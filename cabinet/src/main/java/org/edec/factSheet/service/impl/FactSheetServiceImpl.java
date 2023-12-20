package org.edec.factSheet.service.impl;

import org.edec.factSheet.manager.FactSheetManager;
import org.edec.factSheet.model.FactSheetAddModel;
import org.edec.factSheet.model.TypeFactSheetModel;
import org.edec.factSheet.model.FactSheetTableModel;
import org.edec.factSheet.service.FactSheetService;

import java.util.Date;
import java.util.List;

public class FactSheetServiceImpl implements FactSheetService {

    private FactSheetManager factSheetManager = new FactSheetManager();

    @Override
    public boolean updateStatus (int idFactSheetStatus, Long idFactSheet) {
        return factSheetManager.updateStatus(idFactSheetStatus, idFactSheet);
    }

    @Override
    public boolean updateReceipt (Long idFactSheet) {
        return factSheetManager.updateReceipt(idFactSheet);
    }

    @Override
    public boolean deleteFactSheet (Long idFactSheet) {
        return factSheetManager.deleteFactSheet(idFactSheet);
    }

    @Override
    public boolean updateCompletion (Long idFactSheet) {
        return factSheetManager.updateCompletion(idFactSheet);
    }

    @Override
    public boolean setRegisterNumber (Long idFactSheet, String groupname) {
        return factSheetManager.setRegisterNumber(idFactSheet, groupname);
    }

    @Override
    public List<TypeFactSheetModel> getTypeFactSheet () {
        return factSheetManager.getTypeFactSheet();
    }

    @Override
    public List<FactSheetAddModel> getHumanInfo (Long idHum) {
        return factSheetManager.getHumanInfo(idHum);
    }

    @Override
    public boolean addFactSheet (Long idHum, int idTypeFactSheet, boolean officialSeal, boolean createdByStudent, String groupname) {
        return factSheetManager.addFactSheet(idHum, idTypeFactSheet, officialSeal, createdByStudent, groupname);
    }

    @Override
    public List<FactSheetTableModel> getAllFactSheetsByFilter (String fullName, String groupname, String title, String status,
                                                               String regNumber, Date dateCreateFrom, Date dateCreateTo, Long idInst,
                                                               Integer formOfStudy, Date year) {
        return factSheetManager.getAllFactSheetsByFilter(
                fullName, groupname, title, status, regNumber, dateCreateFrom, dateCreateTo, idInst, formOfStudy, year);
    }

    @Override
    public List<FactSheetTableModel> getFactSheetsHistory (Long idHum) {
        return factSheetManager.getFactSheetsHistory(idHum);
    }

    @Override
    public List<FactSheetAddModel> getSearchStudents (String fullName, String groupName, String recordBook, Long idInst,
                                                      Integer formOfStudy) {
        return factSheetManager.getSearchStudents(fullName, groupName, recordBook, idInst, formOfStudy);
    }

    @Override
    public List<String> getStudentGroups(Long idHumanFace) {
        return factSheetManager.getStudentGroups(idHumanFace);
    }
}
