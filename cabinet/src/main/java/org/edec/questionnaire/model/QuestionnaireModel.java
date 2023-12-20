package org.edec.questionnaire.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;
import java.util.List;

/**
 * Модель опросника
 *
 * @author Max Dimukhametov
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class QuestionnaireModel {
    /**
     * Идентификатор
     */
    private Long id;

    /*** Дата отправления*/
    private Date posted;

    /**
     * Дополнение
     **/
    private String additional;
    /**
     * Описание
     */
    private String description;
    /**
     * Тема
     */
    private String subject;

    /**
     * Идентификатор кластера
     */
    private Long idTopQuest;
    /**
     * Id отправителя
     */
    private Long sender;

    /**
     * Получатели
     **/
    private List<String> recipients;

    private List<BlockModel> blocks;

    public QuestionnaireModel (Date posted, String additional, String description, String subject, Long idTopQuest, Long sender,
                               List<String> recipients, List<BlockModel> blocks) {
        this.posted = posted;
        this.additional = additional;
        this.description = description;
        this.subject = subject;
        this.idTopQuest = idTopQuest;
        this.sender = sender;
        this.recipients = recipients;
        this.blocks = blocks;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public Date getPosted () {
        return posted;
    }

    public void setPosted (Date posted) {
        this.posted = posted;
    }

    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public String getSubject () {
        return subject;
    }

    public void setSubject (String subject) {
        this.subject = subject;
    }

    public Long getIdTopQuest () {
        return idTopQuest;
    }

    public void setIdTopQuest (Long idTopQuest) {
        this.idTopQuest = idTopQuest;
    }

    public Long getSender () {
        return sender;
    }

    public void setSender (Long sender) {
        this.sender = sender;
    }

    public List<String> getRecipients () {
        return recipients;
    }

    public void setRecipients (List<String> recipients) {
        this.recipients = recipients;
    }

    public List<BlockModel> getBlocks () {
        return blocks;
    }

    public void setBlocks (List<BlockModel> blocks) {
        this.blocks = blocks;
    }

    public String getAdditional () {
        return additional;
    }

    public void setAdditional (String additional) {
        this.additional = additional;
    }
}
