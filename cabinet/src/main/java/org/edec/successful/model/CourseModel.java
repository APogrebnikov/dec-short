package org.edec.successful.model;

import java.util.ArrayList;
import java.util.List;


public class CourseModel {
    private List<GroupModel> groups = new ArrayList<>();
    private Integer course;
    private Integer count;

    public List<GroupModel> getGroups () {
        return groups;
    }

    public void setGroups (List<GroupModel> groups) {
        this.groups = groups;
    }

    public Integer getCourse () {
        return course;
    }

    public void setCourse (Integer course) {
        this.course = course;
    }

    public Integer getCount () {
        return count;
    }

    public void setCount (Integer count) {
        this.count = count;
    }
}
