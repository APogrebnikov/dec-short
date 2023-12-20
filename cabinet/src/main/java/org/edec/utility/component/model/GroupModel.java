package org.edec.utility.component.model;

import java.util.ArrayList;
import java.util.List;


public class GroupModel extends org.edec.model.GroupModel {
    private Integer qualification;

    private List<SubjectModel> subjects = new ArrayList<>();

    public GroupModel () {
    }

    public List<SubjectModel> getSubjects () {
        return subjects;
    }

    public void setSubjects (List<SubjectModel> subjects) {
        this.subjects = subjects;
    }

    public Integer getQualification () {
        return qualification;
    }

    public void setQualification (Integer qualification) {
        this.qualification = qualification;
    }
}
