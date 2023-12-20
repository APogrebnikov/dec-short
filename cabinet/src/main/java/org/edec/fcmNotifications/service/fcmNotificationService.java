package org.edec.fcmNotifications.service;

import org.edec.fcmNotifications.model.NotificationModel;

import java.util.List;

public interface fcmNotificationService {

    boolean createTokenByidHumanface (long idHumanface, String token);

    boolean sendMessageToPerson (long idHumanface, NotificationModel notificationModel);

    boolean sendMessageToGroup (List<Long> idHumanface, NotificationModel notificationModel);

    List<String> getTokenByIdHumanface(long idHumanface);
}
