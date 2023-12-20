package org.edec.newOrder.model.editOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 29.11.16.
 */
public class GroupModel {
    private String name;

    private List<SectionModel> sections = new ArrayList<>();
    private List<StudentModel> studentModels = new ArrayList<>();

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public List<SectionModel> getSections () {
        return sections;
    }

    public void setSections (List<SectionModel> sections) {
        this.sections = sections;
    }

    public List<StudentModel> getStudentModels () {
        return studentModels;
    }

    public void setStudentModels (List<StudentModel> studentModels) {
        this.studentModels = studentModels;
    }
}
