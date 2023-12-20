package org.edec.notification.service.impl;

import org.apache.log4j.Logger;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.notification.model.NotificationModel;
import org.edec.notification.service.NotificationService;
import org.edec.utility.fileManager.FilePath;
import org.edec.utility.httpclient.manager.HttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * @author Max Dimukhametov
 */
public class NotificationServiceImpl implements NotificationService {
    private static final Logger log = Logger.getLogger(NotificationServiceImpl.class.getName());

    private static final String URL_POST_MESSAGE = "notification.postMessage";

    private Properties properties;
    private UserModel currentUser;

    public NotificationServiceImpl () {
        currentUser = new TemplatePageCtrl().getCurrentUser();
        try {
            properties = new FilePath().getDataServiceProp();
        } catch (IOException e) {
            e.printStackTrace();
            log.error(currentUser.getFio(), e);
        }
    }

    @Override
    public Long sendNotification (NotificationModel notification) {
        JSONObject jObject = new JSONObject();
        jObject.put("Subject", notification.getSubject());
        jObject.put("Text", notification.getMessage());
        jObject.put("SenderID", notification.getSenderId());
        jObject.put("Type", notification.getType());
        jObject.put("Date", notification.getDate());
        jObject.put("Priority", notification.getPriority());
        jObject.put("Recipients", notification.getRecipients());

        JSONObject answer = new JSONObject(
                HttpClient.makeHttpRequest(properties.getProperty(URL_POST_MESSAGE), HttpClient.POST, new ArrayList<>(),
                                           jObject.toString()
                ));

        return Long.valueOf(answer.getInt("id"));
    }
}
