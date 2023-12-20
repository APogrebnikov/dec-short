package org.edec.notification.model;

import java.util.Date;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class NotificationModel {
    /*** Дата отправления*/
    private Date date;

    /*** Приоритет*/
    private Integer priority;

    /*** Идентификатор*/
    private Long id;
    /*** Id отправителя*/
    private Long senderId;

    /*** Содержимое*/
    private String message;
    /*** Тема*/
    private String subject;
    /*** Тип сообщения*/
    private String type;

    /**
     * Получатели сообщения
     */
    private List<String> recipients;

    public NotificationModel () {
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public String getSubject () {
        return subject;
    }

    public void setSubject (String subject) {
        this.subject = subject;
    }

    public String getMessage () {
        return message;
    }

    public void setMessage (String message) {
        this.message = message;
    }

    public Date getDate () {
        return date;
    }

    public void setDate (Date date) {
        this.date = date;
    }

    public Integer getPriority () {
        return priority;
    }

    public void setPriority (Integer priority) {
        this.priority = priority;
    }

    public Long getSenderId () {
        return senderId;
    }

    public void setSenderId (Long senderId) {
        this.senderId = senderId;
    }

    public String getType () {
        return type;
    }

    public void setType (String type) {
        this.type = type;
    }

    public List<String> getRecipients () {
        return recipients;
    }

    public void setRecipients (List<String> recipients) {
        this.recipients = recipients;
    }
}
