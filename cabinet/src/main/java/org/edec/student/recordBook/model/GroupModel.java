package org.edec.student.recordBook.model;

import java.util.ArrayList;
import java.util.List;


public class GroupModel {
    private String groupname;

    private List<StudentSemesterModel> semesters = new ArrayList<>();

    public GroupModel () {
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public List<StudentSemesterModel> getSemesters () {
        return semesters;
    }

    public void setSemesters (List<StudentSemesterModel> semesters) {
        this.semesters = semesters;
    }
}
