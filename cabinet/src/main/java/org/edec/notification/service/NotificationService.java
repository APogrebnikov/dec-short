package org.edec.notification.service;

import org.edec.notification.model.NotificationModel;


public interface NotificationService {
    Long sendNotification (NotificationModel notification);
}
