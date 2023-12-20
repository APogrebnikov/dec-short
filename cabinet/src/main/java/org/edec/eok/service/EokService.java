package org.edec.eok.service;

import org.edec.eok.model.SubjectModel;

import java.util.List;


public interface EokService {
    List<SubjectModel> getSubjects (String idLGS);
    List<SubjectModel> getSubjectsByFilter (List<SubjectModel> subjects, String department, String subject, boolean isNullEok,
                                            boolean isNullTeacher);
}
