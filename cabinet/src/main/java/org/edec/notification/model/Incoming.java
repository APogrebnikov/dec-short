package org.edec.notification.model;

public class Incoming {
    Long userDialog;

    Long idHuman;

    Long numberUnreadMessages;

    public Long getUserDialog () {
        return userDialog;
    }

    public void setUserDialog (Long userDialog) {
        this.userDialog = userDialog;
    }

    public Long getIdHuman () {
        return idHuman;
    }

    public void setIdHuman (Long idHuman) {
        this.idHuman = idHuman;
    }

    public Long getNumberUnreadMessages () {
        return numberUnreadMessages;
    }

    public void setNumberUnreadMessages (Long numberUnreadMessages) {
        this.numberUnreadMessages = numberUnreadMessages;
    }
}
