package org.edec.notificationCenter.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class NotificationModel {
    private Long id;
    private Long idLinkNotificationHumanface;
    private Long idSender;

    private Date datePosted;
    private Date dateSent;

    private String header;
    private String text;

    private String receiverType;

    private List<ReceiverModel> receivers;
    private List<CommentaryModel> commentaries;
    private List<String> attachedFiles;

    private Integer unreadMessageCounter = 0;
    private Boolean isRead;
    private Boolean isSent = false;
}
