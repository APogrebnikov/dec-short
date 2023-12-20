package org.edec.commission.report.model.schedule;

import java.util.ArrayList;
import java.util.List;

public class FormOfStudyModel {
    private String formOfStudy;
    private List<CourseModel> courses = new ArrayList<>();

    public FormOfStudyModel () {
    }

    public String getFormOfStudy () {
        return formOfStudy;
    }

    public void setFormOfStudy (String formOfStudy) {
        this.formOfStudy = formOfStudy;
    }

    public List<CourseModel> getCourses () {
        return courses;
    }

    public void setCourses (List<CourseModel> courses) {
        this.courses = courses;
    }
}
