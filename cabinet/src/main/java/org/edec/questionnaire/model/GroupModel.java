package org.edec.questionnaire.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class GroupModel {
    private Long idChair;
    private Long idDG;

    private String groupname;

    private List<SubjectModel> subjects = new ArrayList<>();

    public GroupModel (Long idChair, Long idDG, String groupname) {
        this.idChair = idChair;
        this.idDG = idDG;
        this.groupname = groupname;
    }

    public Long getIdDG () {
        return idDG;
    }

    public void setIdDG (Long idDG) {
        this.idDG = idDG;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public List<SubjectModel> getSubjects () {
        return subjects;
    }

    public void setSubjects (List<SubjectModel> subjects) {
        this.subjects = subjects;
    }

    public Long getIdChair () {
        return idChair;
    }

    public void setIdChair (Long idChair) {
        this.idChair = idChair;
    }
}
