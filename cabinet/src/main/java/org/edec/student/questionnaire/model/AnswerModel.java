package org.edec.student.questionnaire.model;

import org.json.JSONObject;

/**
 * Опрос->Блок->Вопрос->Ответ
 * Ответ модели
 * Created by dmmax
 */
public class AnswerModel implements Cloneable {
    /**
     * Идентификатор
     */
    private Long id;

    /**
     * Кастомный ли элемент? Если да, то в случае с комбобокс и радиобаттон добавляется возможность ввода текста
     */
    private int custom;
    private Integer score;

    /**
     * Название ответа
     */
    private String name;

    private String value;

    AnswerModel (JSONObject jsonObject) {
        if (jsonObject.has("id")) {
            this.id = jsonObject.getLong("id");
        }
        if (jsonObject.has("custom")) {
            this.custom = jsonObject.getInt("custom");
        }
        if (jsonObject.has("name")) {
            this.name = String.valueOf(jsonObject.get("name"));
        }
        if (jsonObject.has("score") && !jsonObject.get("score").equals("")) {
            this.score = jsonObject.getInt("score");
        }
    }

    public AnswerModel () {
    }

    public AnswerModel (Long id, String name, int custom) {
        this.id = id;
        this.name = name;
        this.custom = custom;
    }

    public AnswerModel clone () throws CloneNotSupportedException {
        AnswerModel obj = (AnswerModel) super.clone();
        obj.id = id;
        obj.name = name;
        obj.custom = custom;
        obj.score = score;
        return obj;
    }

    public String getValue () {
        return value;
    }

    public void setValue (String value) {
        this.value = value;
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

    public int getCustom () {
        return custom;
    }

    public void setCustom (int custom) {
        this.custom = custom;
    }

    public Integer getScore () {
        return score;
    }

    public void setScore (Integer score) {
        this.score = score;
    }
}
