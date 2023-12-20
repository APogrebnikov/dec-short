package org.edec.questionnaire.model.dao;

/**
 * @author Max Dimukhametov
 */
public class QuestionnaireEsoModel {
    private Long idChair;
    private Long idDG;
    private Long idSubj;

    private String groupname;
    private String subjectname;

    public QuestionnaireEsoModel () {
    }

    public Long getIdDG () {
        return idDG;
    }

    public void setIdDG (Long idDG) {
        this.idDG = idDG;
    }

    public Long getIdSubj () {
        return idSubj;
    }

    public void setIdSubj (Long idSubj) {
        this.idSubj = idSubj;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public String getSubjectname () {
        return subjectname;
    }

    public void setSubjectname (String subjectname) {
        this.subjectname = subjectname;
    }

    public Long getIdChair () {
        return idChair;
    }

    public void setIdChair (Long idChair) {
        this.idChair = idChair;
    }
}
