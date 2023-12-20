package org.edec.successful.model;

import java.util.ArrayList;
import java.util.List;


public class GroupModel {
    private List<StudentModel> students = new ArrayList<>();
    private Long idGroup;
    private String groupname;

    public List<StudentModel> getStudents () {
        return students;
    }

    public void setStudents (List<StudentModel> students) {
        this.students = students;
    }

    public Long getIdGroup () {
        return idGroup;
    }

    public void setIdGroup (Long idGroup) {
        this.idGroup = idGroup;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }
}
