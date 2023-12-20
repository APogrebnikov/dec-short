package org.edec.questionnaire.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Опрос->Блок->Вопрос
 * Модель вопроса
 *
 * @author Max Dimukhametov
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class QuestionModel {
    /**
     * Идентификатор
     */
    @JsonIgnore
    private Long id;
    /**
     * Ссылка на словарь вопросов
     **/
    private Long idDicQuestion;

    /**
     * Затрудняюсь ответить
     **/
    private Integer optional;
    /**
     * Обязательный вопрос
     **/
    private int required;
    /**
     * Тип ответа на вопрос:
     * 0 - радио
     * 1 - чекбокс
     * 2 - текст
     * 3 - слайдер
     */
    private int type;

    /**
     * Максимальное значение для слайдера от 2 до 10
     */
    private String maxVal;
    /**
     * Название
     */
    private String name;

    /**
     * Ссылка на ответы
     */
    private List<AnswerModel> answers;

    public QuestionModel (Long idDicQuestion, Integer optional, int required, int type, String maxVal, String name,
                          List<AnswerModel> answers) {
        this.idDicQuestion = idDicQuestion;
        this.optional = optional;
        this.required = required;
        this.type = type;
        this.maxVal = maxVal;
        this.name = name;
        this.answers = answers;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public int getRequired () {
        return required;
    }

    public void setRequired (int required) {
        this.required = required;
    }

    public int getType () {
        return type;
    }

    public void setType (int type) {
        this.type = type;
    }

    public String getMaxVal () {
        return maxVal;
    }

    public void setMaxVal (String maxVal) {
        this.maxVal = maxVal;
    }

    public List<AnswerModel> getAnswers () {
        return answers;
    }

    public void setAnswers (List<AnswerModel> answers) {
        this.answers = answers;
    }

    public Integer getOptional () {
        return optional;
    }

    public void setOptional (Integer optional) {
        this.optional = optional;
    }

    public Long getIdDicQuestion () {
        return idDicQuestion;
    }

    public void setIdDicQuestion (Long idDicQuestion) {
        this.idDicQuestion = idDicQuestion;
    }
}
