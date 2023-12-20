package org.edec.fcmNotifications.service.impl;

import org.edec.fcmNotifications.model.NotificationModel;
import org.edec.fcmNotifications.model.RecipientModel;
import org.edec.fcmNotifications.manager.fcmNotificationsManager;
import org.edec.fcmNotifications.service.fcmNotificationService;
import org.json.JSONObject;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class fcmNotificationServiceImpl implements fcmNotificationService {
    fcmNotificationsManager manager = new fcmNotificationsManager();
    // Вынести в константы
    String fcmServerkey = "AAAAz6FNbX0:APA91bF84H1vtVX9i5Z-ety_vsll4FwmjVbW_B9ditOyTJQAZa_DriWsgHZOPsRKUuRQLd1xfLwUjeuOhaTKonnuCNkMYkpLlgKbQWeOddZohvqIyKkcDpz_iDSF_sRVHHvXenyTnUoe";

    @Override
    public boolean createTokenByidHumanface (long idHumanface, String token) {
        return manager.createTokenByIdHumanface(idHumanface, token) != null;
    }

    @Override
    public List<String> getTokenByIdHumanface(long idHumanface) {
        return manager.getTokenByIdHumanface(idHumanface);
    }

    private HttpURLConnection openConnection (String url, JSONObject request)  {
        URL urlObject = null;
        try {
            urlObject = new URL(url);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) urlObject.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        connection.setRequestProperty("Authorization", "key="+fcmServerkey);
        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        OutputStream os;
        try {
            os = connection.getOutputStream();
            os.write(request.toString().getBytes("UTF-8"));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return connection;
    }

    private void readResponse (HttpURLConnection connection) {
        InputStream is;
        String response = null;
        try {
            is = new BufferedInputStream(connection.getInputStream());
            response = org.apache.commons.io.IOUtils.toString(is, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null){
            JSONObject jsonResponse = new JSONObject(response);
            System.out.println(jsonResponse);
        }

    }

    @Override
    public boolean sendMessageToPerson (long idHumanface, NotificationModel notificationModel) {
//        String token;
//        if (getTokenByIdHumanface(idHumanface).equals("")) {
//            return false;
//        } else {
//            token = getTokenByIdHumanface(idHumanface);
//        }
        if (idHumanface != 0) {
            List<String> listToken = getTokenByIdHumanface(idHumanface);

            if (listToken.size() == 0){
                return false;
            } else {
                for (String token : listToken){
                    JSONObject notification = new JSONObject();
                    JSONObject request = new JSONObject();

                    notification.put("title", notificationModel.getTitle());
                    notification.put("body", notificationModel.getBody());
                    notification.put("icon", notificationModel.getIcon());
                    notification.put("click_action", notificationModel.getClickAction());
                    request.put("notification", notification);
                    request.put("to", token);

                    String url = "http://fcm.googleapis.com/fcm/send";
                    HttpURLConnection connection = openConnection(url, request);

                    readResponse(connection);
                }
                return true;
            }
        }else {
            return false;
        }

    }

    @Override
    public boolean sendMessageToGroup (List<Long> listIdHumanface, NotificationModel notificationModel) {

        // Без объекта получателя
        /*for (long idHumanface: lstIdHumanface) {
            if (!getTokenByIdHumanface(idHumanface).equals("")) lstTokens.add(getTokenByIdHumanface(idHumanface));
        }
        */
        // С объектом получателя

        for (long idHumanface : listIdHumanface) {
            List<String> listTokens = getTokenByIdHumanface(idHumanface);
            if (listTokens.size() == 0) {
                System.out.println("В базе нет токенов для данного пользователя, idHum = " + idHumanface);
            }  else {
//                List<RecipientModel> recipients = new ArrayList<>();
//                for (String token : listTokens){
//                    if (!getTokenByIdHumanface(idHumanface).equals("")) {
//                        RecipientModel recipient = new RecipientModel(idHumanface, token);
//                        recipients.add(recipient);
//                        listTokens.add(recipient.getToken());
//                    }
//                }
                JSONObject notification = new JSONObject();
                JSONObject request = new JSONObject();

                notification.put("title", notificationModel.getTitle());
                notification.put("body", notificationModel.getBody());
                notification.put("icon", notificationModel.getIcon());
                notification.put("click_action", notificationModel.getClickAction());
                request.put("notification", notification);
                request.put("registration_ids", listTokens);

                String url = "http://fcm.googleapis.com/fcm/send";
                HttpURLConnection connection = openConnection(url, request);

                readResponse(connection);

            }
        }
        return true;
    }
}
