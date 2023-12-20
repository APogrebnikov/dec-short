package org.edec.commission.report.model.schedule;

import java.util.ArrayList;
import java.util.List;

public class CourseModel {
    private Integer course;

    private List<GroupModel> groups = new ArrayList<>();

    public Integer getCourse () {
        return course;
    }

    public void setCourse (Integer course) {
        this.course = course;
    }

    public List<GroupModel> getGroups () {
        return groups;
    }

    public void setGroups (List<GroupModel> groups) {
        this.groups = groups;
    }
}
