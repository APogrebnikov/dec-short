package org.edec.student.questionnaire.service.impl;

import org.edec.student.questionnaire.model.AnswerModel;
import org.edec.student.questionnaire.model.QuestionnaireModel;
import org.edec.student.questionnaire.service.QuestionnaireEnsembleService;
import org.edec.utility.fileManager.FilePath;
import org.edec.utility.httpclient.manager.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;


public class QuestionnaireEnsembleImpl implements QuestionnaireEnsembleService {
    private static final String URL_POST_HUM_ANSWER = "questionnaire.postHumanAnswer";
    private static final String URL_GET_QUESTIONNAIRES_BY_HUM = "questionnaire.getQuestionnairesByHum";
    private static final String URL_GET_QUESTIONNAIRE = "questionnaire.getQuestionnaire";

    private Properties properties;

    public QuestionnaireEnsembleImpl () {
        try {
            properties = new FilePath().getDataServiceProp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public QuestionnaireEnsembleImpl (ServletContext servletContext) {
        try {
            properties = new FilePath(servletContext).getDataServiceProp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean sendHumAnswer (Long id_humanface, Long id_questionnaire, List<AnswerModel> answers) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("humanface", id_humanface);
        jsonObject.put("questionnaire", id_questionnaire);

        JSONArray jsonAnswers = new JSONArray();
        for (AnswerModel answer : answers) {
            JSONObject answerJSON = new JSONObject();
            answerJSON.put("value", answer.getValue());
            answerJSON.put("answer", answer.getId());
            answerJSON.put("score", answer.getScore());
            jsonAnswers.put(answerJSON);
        }
        jsonObject.put("answers", jsonAnswers);

        HttpClient.makeHttpRequest(properties.getProperty(URL_POST_HUM_ANSWER), HttpClient.POST, new ArrayList<>(), jsonObject.toString());
        return true;
    }

    @Override
    public List<QuestionnaireModel> getQuestionnairesByIdHum (Long idHum) {
        List<QuestionnaireModel> questionnaires = new ArrayList<>();
        String jsonString = HttpClient.makeHttpRequest(properties.getProperty(URL_GET_QUESTIONNAIRES_BY_HUM) + idHum + "/questionnaire",
                                                       HttpClient.GET, new ArrayList<>(), ""
        );

        if (jsonString == null) {
            return new ArrayList<>();
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has("children")) {
                JSONArray jsonArray = jsonObject.getJSONArray("children");
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject questionnaireJSON = jsonArray.getJSONObject(i);
                    questionnaires.add(new QuestionnaireModel(questionnaireJSON));
                }
            }
            return questionnaires;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public List<QuestionnaireModel> getAllQuestionnairesByIdHum (Long idHum) {
        List<QuestionnaireModel> questionnaires = new ArrayList<>();
        String jsonString = HttpClient.makeHttpRequest(properties.getProperty(URL_GET_QUESTIONNAIRES_BY_HUM) + idHum + "/questionnaire",
                HttpClient.GET, new ArrayList<>(), ""
        );

        if (jsonString == null || jsonString.equals("")) {
            return new ArrayList<>();
        }

        JSONObject jsonObject = new JSONObject(jsonString);
        if (jsonObject.has("children")) {
            JSONArray jsonArray = jsonObject.getJSONArray("children");
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject questionnaireJSON = jsonArray.getJSONObject(i);
                questionnaires.add(new QuestionnaireModel(questionnaireJSON));
            }
        }
        return questionnaires;
    }

    @Override
    public QuestionnaireModel getQuestionnaire (Long id) {
        String jsonString = HttpClient.makeHttpRequest(properties.getProperty(URL_GET_QUESTIONNAIRE) + id, HttpClient.GET,
                                                       new ArrayList<>(), ""
        );
        JSONObject jsonObject = new JSONObject(jsonString);
        return new QuestionnaireModel(jsonObject);
    }
}