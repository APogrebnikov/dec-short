package org.edec.student.questionnaire.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Модель опросника
 * Created by dmmax.
 */
public class QuestionnaireModel {
    /**
     * Идентификатор
     */
    private Long id;

    /**
     * Тема
     */
    private String subject;

    /**
     * Дата отправления
     */
    private Date posted;

    /**
     * Id отправителя
     */
    private Long senderId;

    /**
     * Описание
     */
    private String description;

    private List<BlockModel> blocks;
    private Integer complete = 0;

    /**
     * Преобразование JSON в объект
     *
     * @param jsonObject
     */
    public QuestionnaireModel (JSONObject jsonObject) {
        if (jsonObject.has("id")) {
            this.id = jsonObject.getLong("id");
        }

        if (jsonObject.has("Subject")) {
            this.subject = jsonObject.getString("Subject");
        }

        if (jsonObject.has("description")) {
            this.description = jsonObject.getString("description");
        }

        if (jsonObject.has("Posted")) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                this.posted = format.parse(jsonObject.getString("Posted"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (jsonObject.has("SenderID")) {
            this.senderId = jsonObject.getLong("SenderID");
        }

        if (jsonObject.has("blocks")) {
            blocks = new ArrayList<>();
            JSONArray jsonBlocks = jsonObject.getJSONArray("blocks");
            for (int i = 0; i < jsonBlocks.length(); ++i) {
                JSONObject jsonBlock = jsonBlocks.getJSONObject(i);
                BlockModel blockModel = new BlockModel(jsonBlock);
                this.blocks.add(blockModel);
            }
        }

        if (jsonObject.has("Complete")) {
            this.complete = jsonObject.getInt("Complete");
        }
    }

    public QuestionnaireModel (Long id, String subject, String description, Date posted, Long senderId, List<BlockModel> blockModels) {
        this.id = id;
        this.subject = subject;
        this.posted = posted;
        this.senderId = senderId;
        this.blocks = blockModels;
        this.description = description;
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

    public Date getPosted () {
        return posted;
    }

    public void setPosted (Date posted) {
        this.posted = posted;
    }

    public Long getSenderId () {
        return senderId;
    }

    public void setSenderId (Long senderId) {
        this.senderId = senderId;
    }

    public List<BlockModel> getBlocks () {
        return blocks;
    }

    public void setBlocks (List<BlockModel> blocks) {
        this.blocks = blocks;
    }

    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }
}
