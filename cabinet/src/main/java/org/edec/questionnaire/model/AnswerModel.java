package org.edec.questionnaire.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * * Опрос->Блок->Вопрос->Ответ
 * Модель ответа
 *
 * @author Max Dimukhametov
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AnswerModel {
    private Long id;

    /**
     * Кастомный ли элемент? Если да, то в случае с комбобокс и радиобаттон добавляется возможность ввода текста
     **/
    private int custom;

    /**
     * Название ответа
     **/
    private String name;
    private Integer score;

    public AnswerModel (int custom, String name, Integer score) {
        this.custom = custom;
        this.name = name;
        this.score = score;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public int getCustom () {
        return custom;
    }

    public void setCustom (int custom) {
        this.custom = custom;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public Integer getScore () {
        return score;
    }

    public void setScore (Integer score) {
        this.score = score;
    }
}
