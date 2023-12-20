package org.edec.questionnaire.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Опросник->Блок опроса
 *
 * @author Max Dimukhametov
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BlockModel {
    /**
     * Идентификатор
     */
    @JsonIgnore
    private Long id;
    /**
     * Ссылка на словарь блоков
     **/
    private Long idDicBlock;

    /**
     * Дополнительное поле для привязки поля к сущности
     */
    private String additional;
    /**
     * Описание
     */
    private String description;
    /**
     * Название
     */
    private String name;

    private List<QuestionModel> questions;

    public BlockModel (Long idDicBlock, String additional, String description, String name, List<QuestionModel> questions) {
        this.idDicBlock = idDicBlock;
        this.additional = additional;
        this.description = description;
        this.name = name;
        this.questions = questions;
    }

    public String getAdditional () {
        return additional;
    }

    public void setAdditional (String additional) {
        this.additional = additional;
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

    public List<QuestionModel> getQuestions () {
        return questions;
    }

    public void setQuestions (List<QuestionModel> questions) {
        this.questions = questions;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public Long getIdDicBlock () {
        return idDicBlock;
    }

    public void setIdDicBlock (Long idDicBlock) {
        this.idDicBlock = idDicBlock;
    }
}
