package org.edec.utility.component.model;

/**
 * Created by Alex
 */
public class ChairModel {
    private Long idChair;
    private Long idDepartment;

    private String fulltitle;
    private String shorttitle;
    private Long otherdbid;

    public ChairModel () {
    }

    public ChairModel (Long idChair, Long idDepartment, String fulltitle, String shorttitle, Long otherdbid) {
        this.idChair = idChair;
        this.idDepartment = idDepartment;
        this.fulltitle = fulltitle;
        this.shorttitle = shorttitle;
        this.otherdbid = otherdbid;
    }

    public String getFulltitle () {
        return fulltitle;
    }

    public void setFulltitle (String fulltitle) {
        this.fulltitle = fulltitle;
    }

    public String getShorttitle () {
        return shorttitle;
    }

    public void setShorttitle (String shorttitle) {
        this.shorttitle = shorttitle;
    }

    public Long getIdChair () {
        return idChair;
    }

    public void setIdChair (Long idChair) {
        this.idChair = idChair;
    }

    public Long getIdDepartment () {
        return idDepartment;
    }

    public void setIdDepartment (Long idDepartment) {
        this.idDepartment = idDepartment;
    }

    public Long getOtherdbid () {
        return otherdbid;
    }

    public void setOtherdbid (Long otherdbid) {
        this.otherdbid = otherdbid;
    }
}
