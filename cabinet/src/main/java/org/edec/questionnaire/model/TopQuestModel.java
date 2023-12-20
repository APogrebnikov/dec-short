package org.edec.questionnaire.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * @autor Max Dimukhametov
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TopQuestModel {
    /**
     * Идентификатор
     **/
    private Long id;

    /**
     * Дата отправления
     **/
    private Date posted;

    /**
     * Описание
     **/
    private String description;
    /**
     * Название
     **/
    private String name;

    public TopQuestModel (String description, String name) {
        this.description = description;
        this.name = name;
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

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }
}
