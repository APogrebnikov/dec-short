package org.edec.model;

import java.util.Date;


public class GroupSemesterModel extends GroupModel {
    private Date dateOfBeginSemester;
    private Date dateOfEndSemester;

    public GroupSemesterModel () {
    }

    public Date getDateOfBeginSemester () {
        return dateOfBeginSemester;
    }

    public void setDateOfBeginSemester (Date dateOfBeginSemester) {
        this.dateOfBeginSemester = dateOfBeginSemester;
    }

    public Date getDateOfEndSemester () {
        return dateOfEndSemester;
    }

    public void setDateOfEndSemester (Date dateOfEndSemester) {
        this.dateOfEndSemester = dateOfEndSemester;
    }
}
