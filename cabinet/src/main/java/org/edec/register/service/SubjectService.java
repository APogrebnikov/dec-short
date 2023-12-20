package org.edec.register.service;

import org.edec.register.model.SubjectModel;
import org.zkoss.zul.Listitem;

import java.util.List;

public interface SubjectService {

    List<String> getGroupNameList (List<SubjectModel> subjects);

    List<SubjectModel> filterSubjectList (List<SubjectModel> subjects, String fioTeacher, String subjectName, String groupName);

    List<SubjectModel> getSubjects (Long idInstitute, int formOfStudy, Long idSemester, Integer course, String fioTeacher,
                                    String subjectName, boolean isBachelor, boolean isMaster, boolean isEngineer);

    List<SubjectModel> getSubjectsBySelectedGroups (List<SubjectModel> subjects, List<Listitem> groupNames);
}
