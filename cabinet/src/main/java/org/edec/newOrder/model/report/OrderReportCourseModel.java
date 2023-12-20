package org.edec.newOrder.model.report;

import java.util.ArrayList;
import java.util.List;


public class OrderReportCourseModel {
    /**
     * номер курса
     */
    private int course;
    private String fullcourse;

    /**
     * Список групп
     */
    private List<OrderReportGroupModel> groups = new ArrayList<>();

    public OrderReportCourseModel () {
    }

    public int getCourse () {
        return course;
    }

    public void setCourse (int course) {
        this.course = course;
    }

    public List<OrderReportGroupModel> getGroups () {
        return groups;
    }

    public void setGroups (List<OrderReportGroupModel> groups) {
        this.groups = groups;
    }

    public String getFullcourse () {
        return fullcourse;
    }

    public void setFullcourse (String fullcourse) {
        this.fullcourse = fullcourse;
    }
}
