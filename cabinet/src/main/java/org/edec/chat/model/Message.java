package org.edec.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.model.HumanfaceModel;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by lunin on 26.09.2017.
 */
@Getter
@Setter
@NoArgsConstructor
public class Message {

    private Long id;
    private String information;
    private LocalDateTime createdAt;
    private Message reply;
    private Long idDialog;
    private Long idHuman;
    private Boolean important;

    private int userType; // new
    private Long replyTo; // new
    private String fullName; // new
    private Date posted; // new

    private Attachment attachment;
    private Long attachmentCount;

    Set<Long> recipients = new HashSet<>();

    /**
     * Конструктор сообщения
     *
     * @param information Текст сообщения
     * @param createdAt   Дата создания
     * @param reply       Идентификатор сообщения-ответа
     * @param idDialog    Идентификатор диалога
     * @param idHuman     Идентификатор отправителя
     * @param id          Идентификатор сообщения
     * @param important   Показатель важности сообщения
     */
    public Message (String information, LocalDateTime createdAt, Message reply, Long idDialog, Long idHuman, Long id, Boolean important) {
        this.information = information;
        this.createdAt = createdAt;
        this.reply = reply;
        this.idDialog = idDialog;
        this.idHuman = idHuman;
        this.id = id;
        this.important = important;
    }
}
