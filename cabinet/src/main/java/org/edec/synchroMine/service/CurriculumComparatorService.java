package org.edec.synchroMine.service;

import org.edec.curriculumScan.model.Subject;
import org.edec.synchroMine.model.CurriculumCompareScanResult;

import java.util.List;

public interface CurriculumComparatorService {
    List<CurriculumCompareScanResult> compareSubjectsFromFile(List<Subject> subjectsESO, List<Subject> subjectsDBO);
    List<CurriculumCompareScanResult> compareSubjectsFromDB(List<Subject> subjectsESO, List<Subject> subjectsDBO, String groupName);
    String generateCompareReport(List<CurriculumCompareScanResult> inLis);
}
