package org.edec.utility.component.service;

import org.edec.utility.component.model.GroupModel;
import org.edec.utility.component.model.StudentModel;

import java.util.List;


public interface StudentComponentService {
    List<GroupModel> getGroupSemByGroupname (String groupname);
    List<StudentModel> getStudentByIdHumAndGroupname (Long idHum, String groupname);
}
