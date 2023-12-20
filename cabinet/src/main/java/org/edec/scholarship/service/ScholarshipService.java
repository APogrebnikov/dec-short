package org.edec.scholarship.service;

import org.edec.scholarship.model.ScholarshipModel;
import org.edec.scholarship.model.ScholarshipHistoryModel;

import java.util.List;

/**
 * Сервис по изменению информации о стипендиях у студентов
 */
public interface ScholarshipService {
    Boolean updateScholarship(ScholarshipModel scholarship);
    Boolean deleteScholarship(ScholarshipModel scholarship);
    List<ScholarshipModel> getScholarshipsByStudent(Long idStudentcard);

    List<ScholarshipHistoryModel> getScholarshipHistory(Long idStudentcard);
}
