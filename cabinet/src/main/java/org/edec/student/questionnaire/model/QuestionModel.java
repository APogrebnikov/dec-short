package org.edec.student.questionnaire.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Опрос->Блок->Вопрос
 * модель вопроса
 * Created by dmmax
 */
public class QuestionModel {
    /**
     * Идентификатор
     */
    private Long id;

    private boolean isOptional;

    private Integer maxVal;
    private Integer optional;
    private Integer required;
    /**
     * Тип ответа на вопрос:
     * 0 - радио
     * 1 - чекбокс
     * 2 - текст
     * 3 - шкала
     */
    private int type;

    /**
     * Название
     */
    private String name;

    /**
     * Выбранные ответы
     */
    private List<AnswerModel> choosenAnswer;

    /**
     * Ссылка на ответы
     */
    private List<AnswerModel> answers;

    QuestionModel (JSONObject jsonObject) {
        if (jsonObject.has("id")) {
            this.id = jsonObject.getLong("id");
        }

        if (jsonObject.has("name")) {
            this.name = jsonObject.getString("name");
        }

        if (jsonObject.has("optional")) {
            if (!jsonObject.get("optional").equals("")) {
                this.optional = jsonObject.getInt("optional");
            } else {
                this.optional = 0;
            }
        }

        if (jsonObject.has("required")) {
            if (!jsonObject.get("required").equals("")) {
                this.required = jsonObject.getInt("required");
            } else {
                this.required = 1;
            }
        }

        if (jsonObject.has("type")) {
            this.type = jsonObject.getInt("type");
        }

        if (jsonObject.has("maxVal")) {
            if (!jsonObject.get("maxVal").equals("")) {
                this.maxVal = jsonObject.getInt("maxVal");
            } else {
                this.maxVal = 0;
            }
        }

        if (jsonObject.has("answers")) {
            this.answers = new ArrayList<>();
            JSONArray answersJSON = jsonObject.getJSONArray("answers");
            for (int i = 0; i < answersJSON.length(); ++i) {
                JSONObject answerJSON = answersJSON.getJSONObject(i);
                AnswerModel answerModel = new AnswerModel(answerJSON);
                this.answers.add(answerModel);
            }
        }
        this.choosenAnswer = new ArrayList<>();
    }

    public QuestionModel (Long id, String name, int type, Integer maxVal, List<AnswerModel> answerModels) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.answers = answerModels;
        this.maxVal = maxVal;
        this.choosenAnswer = new ArrayList<>();
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

    public List<AnswerModel> getAnswers () {
        return answers;
    }

    public void setAnswers (List<AnswerModel> answers) {
        this.answers = answers;
    }

    public int getType () {
        return type;
    }

    public void setType (int type) {
        this.type = type;
    }

    public List<AnswerModel> getChoosenAnswer () {
        return choosenAnswer;
    }

    public void setChoosenAnswer (List<AnswerModel> choosenAnswer) {
        this.choosenAnswer = choosenAnswer;
    }

    public Integer getMaxVal () {
        return maxVal;
    }

    public void setMaxVal (Integer maxVal) {
        this.maxVal = maxVal;
    }

    public Integer getRequired () {
        return required;
    }

    public void setRequired (Integer required) {
        this.required = required;
    }

    public Integer getOptional () {
        return optional;
    }

    public void setOptional (Integer optional) {
        this.optional = optional;
    }

    public boolean isOptional () {
        return isOptional;
    }

    public void setOptional (boolean optional) {
        isOptional = optional;
    }
}
