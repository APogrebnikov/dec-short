package org.edec.reference.service;

import org.edec.reference.model.ExcelReportModel;
import org.edec.reference.model.ReferenceModel;
import org.edec.reference.model.StudentModel;
import org.zkoss.util.media.Media;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface ReferenceService {

    List<StudentModel> getStudents (String studentFio, Long idInst);

    List<ReferenceModel> getReferences (long idStudentcard);

    boolean updateStudentStatus (boolean isInvalid, boolean isIndigent, boolean isOrphan, boolean isVeteran, int typeInvalid, long idStudentcard);

    boolean updateStudentDateOfBirth (long idHumanface, Date dateOfBirth);

    Long createReference (ReferenceModel reference);

    boolean updateReference (ReferenceModel reference);

    boolean deleteReference (long idRef);

    String createFiles (ReferenceModel reference, long idInst, Media mediaFileRefScan, Media mediaFileApplicationScan);

    void deleteFiles (ReferenceModel reference);

    void writeIntoExcel () throws IOException;
}
