package org.edec.questionnaire.model;

/**
 * @author Max Dimukhametov
 */
public class SubjectModel {
    private Long idSubj;

    private String subjectname;

    public SubjectModel () {
    }

    public SubjectModel (Long idSubj, String subjectname) {
        this.idSubj = idSubj;
        this.subjectname = subjectname;
    }

    public Long getIdSubj () {
        return idSubj;
    }

    public void setIdSubj (Long idSubj) {
        this.idSubj = idSubj;
    }

    public String getSubjectname () {
        return subjectname;
    }

    public void setSubjectname (String subjectname) {
        this.subjectname = subjectname;
    }
}
