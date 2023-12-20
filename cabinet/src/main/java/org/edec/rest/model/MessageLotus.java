package org.edec.rest.model;


public class MessageLotus {
    private String iddoc;
    private String path;
    private String subject;

    public MessageLotus () {
    }

    public String getIddoc () {
        return iddoc;
    }

    public void setIddoc (String iddoc) {
        this.iddoc = iddoc;
    }

    public String getPath () {
        return path;
    }

    public void setPath (String path) {
        this.path = path;
    }

    public String getSubject () {
        return subject;
    }

    public void setSubject (String subject) {
        this.subject = subject;
    }
}
