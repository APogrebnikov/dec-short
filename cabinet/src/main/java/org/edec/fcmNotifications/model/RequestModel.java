package org.edec.fcmNotifications.model;

import java.util.List;

public class RequestModel {
    private NotificationModel notification;
    private List<RecipientModel> recipientTokens;

    public NotificationModel getNotification() {
        return notification;
    }

    public void setNotification(NotificationModel notification) {
        this.notification = notification;
    }

    public List<RecipientModel> getRecipientTokens() {
        return recipientTokens;
    }

    public void setRecipientTokens(List<RecipientModel> recipientTokens) {
        this.recipientTokens = recipientTokens;
    }
}
