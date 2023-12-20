package org.edec.passportGroup.model;

public class DepartmentModel {

    private long idChair;
    private long otherDbID;
    private String fullTitle;

    public long getOtherDbID () {
        return otherDbID;
    }

    public void setOtherDbID (long otherDbID) {
        this.otherDbID = otherDbID;
    }

    public String getFullTitle () {
        return fullTitle;
    }

    public void setFullTitle (String fullTitle) {
        this.fullTitle = fullTitle;
    }

    public long getIdChair () {
        return idChair;
    }

    public void setIdChair (long idChair) {
        this.idChair = idChair;
    }
}
