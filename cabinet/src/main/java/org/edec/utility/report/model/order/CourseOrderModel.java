package org.edec.utility.report.model.order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmmax
 */
public class CourseOrderModel {
    /**
     * номер курса
     */
    private int course;
    private String fullcourse;

    /**
     * Список групп
     */
    private List<GroupOrderModel> groups = new ArrayList<GroupOrderModel>();

    public CourseOrderModel () {
    }

    public int getCourse () {
        return course;
    }

    public void setCourse (int course) {
        this.course = course;
    }

    public List<GroupOrderModel> getGroups () {
        return groups;
    }

    public void setGroups (List<GroupOrderModel> groups) {
        this.groups = groups;
    }

    public String getFullcourse () {
        return fullcourse;
    }

    public void setFullcourse (String fullcourse) {
        this.fullcourse = fullcourse;
    }
}
