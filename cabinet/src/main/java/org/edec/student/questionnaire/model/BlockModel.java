package org.edec.student.questionnaire.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Опросник->Блок опроса
 * Created by dmmax
 */
public class BlockModel {
    /**
     * Идентификатор
     */
    private Long id;

    /**
     * Название
     */
    private String name;

    /**
     * Описание
     */
    private String description = "";

    private List<QuestionModel> questions;

    private int countChoosen;

    public BlockModel (JSONObject jsonObject) {
        if (jsonObject.has("id")) {
            this.id = jsonObject.getLong("id");
        }

        if (jsonObject.has("name")) {
            this.name = jsonObject.getString("name");
        }

        if (jsonObject.has("description")) {
            this.description = jsonObject.getString("description");
        }

        if (jsonObject.has("questions")) {
            this.questions = new ArrayList<>();
            JSONArray questionsJSON = jsonObject.getJSONArray("questions");
            for (int i = 0; i < questionsJSON.length(); ++i) {
                JSONObject questionJSON = questionsJSON.getJSONObject(i);
                QuestionModel questionModel = new QuestionModel(questionJSON);
                this.questions.add(questionModel);
            }
        }
        this.countChoosen = 0;
    }

    public BlockModel () {
        questions = new ArrayList<>();
    }

    public BlockModel (Long id, String name, String description, List<QuestionModel> questionModels) {
        this.id = id;
        this.name = name;
        this.questions = questionModels;
        this.description = description;
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

    public List<QuestionModel> getQuestions () {
        return questions;
    }

    public void setQuestions (List<QuestionModel> questions) {
        this.questions = questions;
    }

    public int getCountChoosen () {
        return countChoosen;
    }

    public void setCountChoosen (int countChoosen) {
        this.countChoosen = countChoosen;
    }
}
