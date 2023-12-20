package org.edec.directorNotion.service;

import org.edec.directorNotion.model.StudentModel;

import java.util.List;

public interface DirectorNotionService {

    List<StudentModel> getStudentsByFilter (String fio, String recordbook, String groupname);

    void getDirectorNotion (boolean isPdf, List<StudentModel> students, String notionDate);
}
