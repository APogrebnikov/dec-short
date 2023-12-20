package org.edec.notificationCenter.model;

import lombok.Data;

@Data
public class FeedbackModel {

    private Long idHumanface;
    private Long idLinkNotificationHumanface;
    private String fio;
    private Integer unreadMessageCount;

}
