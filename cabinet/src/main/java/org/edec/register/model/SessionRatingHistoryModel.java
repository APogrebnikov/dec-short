package org.edec.register.model;

public class SessionRatingHistoryModel {
    private Long idSRH;
    private Integer retakeCount;

    public Long getIdSRH () {
        return idSRH;
    }

    public void setIdSRH (Long idSRH) {
        this.idSRH = idSRH;
    }

    public Integer getRetakeCount () {
        return retakeCount;
    }

    public void setRetakeCount (Integer retakeCount) {
        this.retakeCount = retakeCount;
    }
}
