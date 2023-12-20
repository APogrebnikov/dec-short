package org.edec.efficiency.model;

import org.edec.model.GroupModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class ProblemGroup extends GroupModel {
    private Boolean isEfficiency;

    private List<ProblemStudent> students = new ArrayList<>();
    private List<ProblemSubjectGroup> subjects = new ArrayList<>();

    public ProblemGroup () {
    }

    public Boolean getEfficiency () {
        return isEfficiency;
    }

    public void setEfficiency (Boolean efficiency) {
        isEfficiency = efficiency;
    }

    public List<ProblemSubjectGroup> getSubjects () {
        return subjects;
    }

    public void setSubjects (List<ProblemSubjectGroup> subjects) {
        this.subjects = subjects;
    }

    public List<ProblemStudent> getStudents () {
        return students;
    }

    public void setStudents (List<ProblemStudent> students) {
        this.students = students;
    }
}
