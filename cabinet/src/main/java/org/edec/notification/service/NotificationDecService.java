package org.edec.notification.service;

import org.edec.chat.model.Message;
import org.edec.notification.model.Incoming;

import java.util.HashSet;
import java.util.List;


public interface NotificationDecService {

    List<Incoming> getAllIncoming ();

    Boolean sendNotification (Message message);

    Boolean updateLastMessage (Long userDialog, Long messageId);

    List<Message> getUnreadMessages (Long userDialog);
}
