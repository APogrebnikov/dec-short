package org.edec.fcmNotifications.model;


public class RecipientModel {
    private long idHumanface;
    private String token;

    public RecipientModel(long idHumanface, String token) {
        this.idHumanface = idHumanface;
        this.token = token;
    }

    public long getIdHumanface() {
        return idHumanface;
    }

    public String getToken() {
        return token;
    }
}
