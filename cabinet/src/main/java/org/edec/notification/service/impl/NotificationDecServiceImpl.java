package org.edec.notification.service.impl;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.edec.chat.model.Message;
import org.edec.notification.model.Incoming;
import org.edec.notification.service.NotificationDecService;
import org.edec.utility.httpclient.manager.HttpClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


public class NotificationDecServiceImpl implements NotificationDecService {

    private static final String URL = "http://localhost:57772/chat/";
    private static final String URL_GET_ALL_INCOMING = URL + "dec/incoming"; //Получить все входящие
    private static final String URL_POST_SEND_MESSAGE = URL + "message"; //Сделать рассылку
    private static final String URL_UPDATE_LAST_MESSAGE = URL + "dec/message/"; //Сделать рассылку
    private static final String URL_GET_NEW_MESSAGES =
            URL + "dec/conversation/"; //Получить все непрочитанные сообщения в конкретном диалоге

    @Override
    public List<Incoming> getAllIncoming () {
        List<Incoming> incomingList = new ArrayList<Incoming>();
        /*Incoming message = new Incoming();
        message.setIdHuman(new Long(1));
        message.setNumberUnreadMessages(new Long(5));
        message.setUserDialog(new Long(4));
        incomingList.add(message);*/
        //Запрос на сервер
        List<NameValuePair> params = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(HttpClient.makeHttpRequest(URL_GET_ALL_INCOMING, HttpClient.GET, params, null));

        if (!jsonObject.has("children")) {
            return null;
        } else {

            JSONArray jArray = jsonObject.getJSONArray("children");
            for (int i = 0; i < jArray.length(); i++) {
                Incoming message = new Incoming();
                JSONObject jObj = jArray.getJSONObject(i);
                message.setIdHuman(Long.parseLong(jObj.optString("HumanfaceID")));
                message.setNumberUnreadMessages(Long.parseLong(jObj.optString("Count")));
                message.setUserDialog(Long.parseLong(jObj.optString("UserConversation")));
                incomingList.add(message);
            }
        }
        return incomingList;
    }

    @Override
    public Boolean sendNotification (Message message) {
        List<NameValuePair> params = new ArrayList<>();
        JSONObject params2 = new JSONObject();
        JSONArray array = new JSONArray();
        params2.put("Information", message.getInformation());
        params2.put("HumanfaceID", message.getIdHuman());
        params2.put("CountOfAttachments", "");
        params2.put("Attachment", "");
        params2.put("Humanfaces", message.getRecipients());

        params.add(new BasicNameValuePair("Humanfaces", message.getRecipients().toString()));

        params.add(new BasicNameValuePair("Information", message.getInformation()));
        params.add(new BasicNameValuePair("HumanfaceID", message.getIdHuman().toString()));
        params.add(new BasicNameValuePair("Attachment", ""));
        params.add(new BasicNameValuePair("CountOfAttachments", ""));

        params.add(new BasicNameValuePair("Humanfaces", message.getRecipients().toString()));
        JSONObject jsonObject = new JSONObject(
                HttpClient.makeHttpRequest(URL_POST_SEND_MESSAGE, HttpClient.POST, new ArrayList<>(), params2.toString()));
        if (!jsonObject.has("Status")) {
            return false;
        } else {
            if (jsonObject.get("Status").equals("1")) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public Boolean updateLastMessage (Long userDialog, Long messageId) {
        List<NameValuePair> params = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(
                HttpClient.makeHttpRequest(URL_UPDATE_LAST_MESSAGE + userDialog + "/" + messageId, HttpClient.POST, params, null));
        if (!jsonObject.has("Status")) {
            return false;
        } else {
            if (jsonObject.get("Status").equals("1")) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public List<Message> getUnreadMessages (Long userDialog) //Получить непрочитанные сообщения
    {
        List<Message> messageList = new ArrayList<Message>();
        //Запрос на сервер
        List<NameValuePair> params = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(HttpClient.makeHttpRequest(URL_GET_NEW_MESSAGES + userDialog, HttpClient.GET, params, null));

        if (!jsonObject.has("children")) {
            return null;
        } else {
            JSONArray jArray = jsonObject.getJSONArray("children");
            for (int i = 0; i < jArray.length(); i++) {
                Message message = new Message();
                JSONObject jObj = jArray.getJSONObject(i);
                message.setId(Long.parseLong(jObj.optString("ID")));
                message.setInformation(jObj.optString("Information"));
                message.setIdHuman(Long.parseLong(jObj.optString("HumanfaceID")));
                message.setCreatedAt(LocalDateTime.parse(jObj.optString("CreatedAt"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                String replyTo = jObj.optString("ReplyTo");
                //message.setReply(replyTo.equals("") ? null : Long.parseLong(replyTo));
                message.setImportant(jObj.optString("Important").equals("1") ? true : false);
                messageList.add(message);
            }
        /*
        for (Message m: messageList) {

        }*/
            return messageList;
        }
    }
}
