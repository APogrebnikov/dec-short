package org.edec.questionnaire.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.questionnaire.model.QuestionnaireModel;
import org.edec.questionnaire.model.TopQuestModel;
import org.edec.questionnaire.service.QuestionnaireServiceEnsemble;
import org.edec.utility.fileManager.FilePath;
import org.edec.utility.httpclient.manager.HttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class QuestionnaireServiceEnsembleImpl implements QuestionnaireServiceEnsemble {

    private static final Logger log = Logger.getLogger(QuestionnaireServiceEnsembleImpl.class.getName());

    private static final String URL_POST_SEND_QUESTIONNAIRE = "questionnaire.postSendQuestionnaire";
    private static final String URL_POST_SEND_TOPQUEST = "questionnaire.postSendTopQuest";

    private Properties properties;
    private UserModel currentUser;

    public QuestionnaireServiceEnsembleImpl() {
        currentUser = new TemplatePageCtrl().getCurrentUser();
        try {
            properties = new FilePath().getDataServiceProp();
        } catch (IOException e) {
            e.printStackTrace();
            log.error(currentUser.getFio(), e);
        }
    }

    @Override
    public boolean sendQuestionnaire(QuestionnaireModel questionnaire) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(questionnaire);
            log.info("Опросник: " + questionnaire.getDescription() + " успешно создан!");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error(currentUser.getFio() + ".Не удалось преобразовать опросник в JSON", e);
            return false;
        }
        HttpClient.makeHttpRequest(properties.getProperty(URL_POST_SEND_QUESTIONNAIRE), HttpClient.POST, new ArrayList<>(), jsonString);
        return true;
    }

    @Override
    public Long sendTopQuest(TopQuestModel topQuest) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(topQuest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error(currentUser.getFio() + ".Не удалось преобразовать TopQuest в JSON", e);
            return 0L;
        }
        JSONObject jsonObject = new JSONObject(
                HttpClient.makeHttpRequest(properties.getProperty(URL_POST_SEND_TOPQUEST), HttpClient.POST, new ArrayList<>(), jsonString));
        if (jsonObject.has("id")) {
            return jsonObject.getLong("id");
        } else {
            return 0L;
        }
    }
}
