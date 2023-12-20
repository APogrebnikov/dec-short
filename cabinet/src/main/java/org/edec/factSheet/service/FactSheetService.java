package org.edec.factSheet.service;

import org.edec.factSheet.model.FactSheetAddModel;
import org.edec.factSheet.model.TypeFactSheetModel;
import org.edec.factSheet.model.FactSheetTableModel;

import java.util.Date;
import java.util.List;

public interface FactSheetService {

    boolean updateStatus (int idFactSheetStatus, Long idFactSheet);
    boolean updateReceipt (Long idFactSheet);
    boolean deleteFactSheet (Long idFactSheet);
    boolean updateCompletion (Long idFactSheet);
    boolean setRegisterNumber (Long idFactSheet, String groupname);
    List<TypeFactSheetModel> getTypeFactSheet ();
    List<FactSheetAddModel> getHumanInfo (Long idHum);
    boolean addFactSheet (Long idHum, int idTypeFactSheet, boolean officialSeal, boolean createdByStudent, String groupname);
    List<FactSheetTableModel> getAllFactSheetsByFilter (String fullName, String groupname, String title, String status, String regNumber,
                                                        Date dateCreateFrom, Date dateCreateTo, Long idInst, Integer formOfStudy,
                                                        Date year);
    List<FactSheetTableModel> getFactSheetsHistory (Long idHum);
    List<FactSheetAddModel> getSearchStudents (String fullName, String groupName, String recordBook, Long idInst, Integer formOfStudy);

    List<String> getStudentGroups (Long idHumanFace);
}
