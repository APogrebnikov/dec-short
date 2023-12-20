package org.edec.notificationCenter.model;

import lombok.Data;

import java.util.Date;

@Data
public class CommentaryModel {

    private Long idCommentary;
    private Long idSender;

    private Date datePosted;

    private String message;
    private String nameSender;

    private Boolean isRead = false;

}
