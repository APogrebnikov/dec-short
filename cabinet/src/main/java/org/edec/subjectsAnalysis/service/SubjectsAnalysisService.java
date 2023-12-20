package org.edec.subjectsAnalysis.service;

import org.edec.subjectsAnalysis.model.SubjectsAnalysisModel;
import org.edec.subjectsAnalysis.model.SubjectsInfoModel;

import java.util.List;

public interface SubjectsAnalysisService {
    List<SubjectsAnalysisModel> getExamsSubjectsList(Long idSem, Integer course);
    List<SubjectsAnalysisModel> getPassSubjectsList(Long idSem, Integer course);
    List<SubjectsInfoModel> getSubjectsInfo(String subjectname, Long idSem, Integer course, Integer foc, Integer idChair);
    double getStDev(List<SubjectsAnalysisModel> list, int type);
    double getAvg(List<SubjectsAnalysisModel> list, int type);
    double getHQ(List<SubjectsAnalysisModel> list, int type);
}
