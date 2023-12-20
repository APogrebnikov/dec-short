package org.edec.utility.component.model;

/**
 * Created by dmmax
 */
public class InstituteModel {
    private Long idInst;
    private Long idInstMine;

    private String fulltitle;
    private String shorttitle;

    public InstituteModel () {
    }

    public InstituteModel (Long idInst, Long idInstMine, String fulltitle, String shorttitle) {
        this.idInst = idInst;
        this.idInstMine = idInstMine;
        this.fulltitle = fulltitle;
        this.shorttitle = shorttitle;
    }

    public Long getIdInstMine () {
        return idInstMine;
    }

    public void setIdInstMine (Long idInstMine) {
        this.idInstMine = idInstMine;
    }

    public Long getIdInst () {
        return idInst;
    }

    public void setIdInst (Long idInst) {
        this.idInst = idInst;
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
}
