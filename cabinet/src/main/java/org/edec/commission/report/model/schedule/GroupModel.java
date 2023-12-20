package org.edec.commission.report.model.schedule;

import java.util.ArrayList;
import java.util.List;

public class GroupModel {
    private String groupname;
    private List<StudentModel> students = new ArrayList<>();

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public List<StudentModel> getStudents () {
        return students;
    }

    public void setStudents (List<StudentModel> students) {
        this.students = students;
    }
}
