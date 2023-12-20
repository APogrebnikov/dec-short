package org.edec.register.model.dao;

/**
 * @author Anton Skripachev
 */
public class RegisterWithSignDateModelESO {
    private Long idRegister;
    private Long idSRH;
    private Long idSessionRatingHistory;
    private String pathToFile;

    public Long getIdRegister () {
        return idRegister;
    }

    public void setIdRegister (Long idRegister) {
        this.idRegister = idRegister;
    }

    public String getPathToFile () {
        return pathToFile;
    }

    public void setPathToFile (String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public Long getIdSessionRatingHistory () {
        return idSessionRatingHistory;
    }

    public void setIdSessionRatingHistory (Long idSessionRatingHistory) {
        this.idSessionRatingHistory = idSessionRatingHistory;
    }

    public Long getIdSRH () {
        return idSRH;
    }

    public void setIdSRH (Long idSRH) {
        this.idSRH = idSRH;
    }
}
