package org.edec.utility.component.model;


public class RatingModel {
    private Integer rating;

    private String foc;
    private String semester;
    private String subjectname;

    public RatingModel () {
    }

    public Integer getRating () {
        return rating;
    }

    public void setRating (Integer rating) {
        this.rating = rating;
    }

    public String getFoc () {
        return foc;
    }

    public void setFoc (String foc) {
        this.foc = foc;
    }

    public String getSemester () {
        return semester;
    }

    public void setSemester (String semester) {
        this.semester = semester;
    }

    public String getSubjectname () {
        return subjectname;
    }

    public void setSubjectname (String subjectname) {
        this.subjectname = subjectname;
    }
}
