package org.edec.chat.service.impl;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.edec.chat.ChatClient;
import org.edec.chat.manager.ChatEsoManager;
import org.edec.chat.model.Message;
import org.edec.chat.service.ChatService;
import org.edec.utility.httpclient.manager.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lunin on 26.09.2017.
 */

public class ChatServiceImpl implements ChatService {

    private ChatEsoManager chatEsoManager = new ChatEsoManager();

    private static final String URL = "http://localhost:57772/chat/";
    private static final String URL_UPDATE_LAST_MESSAGE = URL + "message/"; //Получить userDialog
    private static final String URL_FIND_MESSAGES = URL + "message/find"; //Получить userDialog
    private static final String URL_GET_USER_DIALOG = URL + "conversations"; //Получить userDialog
    private static final String URL_GET_NEW_MESSAGES = URL + "conversations/"; //Получить все непрочитанные сообщения в конкретном диалоге
    private static final String URL_GET_DOWN_MESSAGES = URL + "messages/new/";  //Подгрузить вниз
    private static final String URL_GET_UP_MESSAGES = URL + "messages/old/";  //Подгрузить вверх
    private static final String URL_GET_IMPORTANT_MESSAGE = URL + "important/"; //Получить важные
    private static final String URL_POST_SEND_MESSAGE = URL + "message"; //Отправить сообщение на сервер
    private static final String URL_POST_IMPORTANT_MESSAGE = URL + "important/"; //Пометить как важное или неважное

    @Override
    public List<Message> findMessageByInformation (Long userDialog, String information) {
        List<Message> messageList = new ArrayList<Message>();
         /*
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("userConv", userDialog));
        params.add(new BasicNameValuePair("str", information));
        JSONObject jsonObject = new JSONObject(httpClient.makeHttpRequest(URL_FIND_MESSAGES, httpClient.GET, params, null));
        if (!jsonObject.has("children")) {
            return null;
        }
        else {
            JSONArray jArray = jsonObject.getJSONArray("children");
            for (int i = 0; i < jArray.length(); i++) {
                Message message = new Message();
                JSONObject jObj = jArray.getJSONObject(i);
                message.setId(Long.parseLong(jObj.optString("ID")));
                message.setInformation(jObj.optString("Information"));
                message.setIdHuman(Long.parseLong(jObj.optString("HumanfaceID")));
                message.setCreatedAt(LocalDateTime.parse(jObj.optString("CreatedAt"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                String replyTo = jObj.optString("ReplyTo");
                message.setReply(replyTo.equals("") ? null : Long.parseLong(replyTo));
                message.setImportant(jObj.optString("Important").equals("1") ? true : false);
                messageList.add(message);
            }/*
        /*
        for (Message m: messageList) {

        }*/

        //}
        Message message = new Message();
        message.setInformation("Найденное сообщение");
        message.setIdHuman(212484L);
        message.setCreatedAt(LocalDateTime.now());
        messageList.add(message);
        return messageList;
    }

    @Override
    public Long getUserDialog (Long humanfaceId, String groupName) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("HumanfaceID", humanfaceId.toString()));
        params.add(new BasicNameValuePair("GroupName", groupName));
        JSONObject jsonObject = new JSONObject();// = new JSONObject(HttpClient.makeHttpRequest(URL_GET_USER_DIALOG, HttpClient.POST, params, null));
        System.out.println("JSON: " + jsonObject);
        if (!jsonObject.has("ucID")) {
            return null;
        } else {
            return Long.parseLong(jsonObject.get("ucID").toString());
        }
    }

    @Override
    public List<Message> getUnreadMessages (Long userDialog) //Получить непрочитанные сообщения
    {
        List<Message> messageList = new ArrayList<Message>();
        //Запрос на сервер
        List<NameValuePair> params = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(); //new JSONObject(HttpClient.makeHttpRequest(URL_GET_NEW_MESSAGES + userDialog, HttpClient.GET, params, null));

        if (!jsonObject.has("children")) {
            return new ArrayList<>();
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

    @Override
    public List<Message> getDownMessages (Long userDialog, Long messageId) //Подгрузить вниз - получить новые сообщения
    {

        List<Message> messageList = new ArrayList<Message>();
        //Запрос на сервер
        List<NameValuePair> params = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(); //new JSONObject(HttpClient.makeHttpRequest(URL_GET_DOWN_MESSAGES + userDialog + "/" + messageId,
        //HttpClient.GET, params, null));

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
        }
        /*
        for (Message m: messageList) {

        }*/
        return messageList;
    }

    @Override
    public List<Message> getUpMessages (Long userDialog, Long messageId) //Подгрузить вверх
    {
        List<Message> messageList = new ArrayList<Message>();
        //Запрос на сервер
        List<NameValuePair> params = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(
                HttpClient.makeHttpRequest(URL_GET_UP_MESSAGES + userDialog + "/" + messageId, HttpClient.GET, params, null));

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
        }
        /*
        for (Message m: messageList) {

        }*/
        return messageList;
    }

    @Override
    public List<Message> getImportantMessages (Long userDialog) //Получить все важные сообщений
    {
        List<Message> messageList = new ArrayList<Message>();
        List<NameValuePair> params = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(
                HttpClient.makeHttpRequest(URL_GET_IMPORTANT_MESSAGE + userDialog, HttpClient.GET, params, null));

        return messageList;
    }

    @Override
    public Long sendMessage (Message message, Long userDialog) //Отправить сообщение на сервер, возвращает id сообщения или ошибку
    {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("Information", message.getInformation()));
        params.add(new BasicNameValuePair("HumanfaceID", message.getIdHuman().toString()));
        params.add(new BasicNameValuePair("UserConversation", "1"));    //   NEW
        //        params.add(new BasicNameValuePair("UserConversation", userDialog.toString()));
        params.add(new BasicNameValuePair("ReplyTo", ""));
        params.add(new BasicNameValuePair("Important", "0"));
        //JSONObject jsonObject = new JSONObject(HttpClient.makeHttpRequest(URL_POST_SEND_MESSAGE, HttpClient.POST, params, null));
        //(message.getImportant()) ? "1" : "0" )
        //(message.getReply() == null) ? "" : message.getReply().toString()
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("params", params);

        // if (!jsonObject.has("Status"))
        //      return null;
        //  else {
        //if (jsonObject.get("Status").equals("1")) {
        return 1L;
        //} else return null;
        //   }
    }

    @Override
    public Boolean noteImportantMessage (Long messageId, int status) //Отметить сообщениe как важное на сервере и наоброт
    {
        List<NameValuePair> params = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(
                HttpClient.makeHttpRequest(URL_POST_IMPORTANT_MESSAGE + messageId + "/" + status, HttpClient.POST, params, null));
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
    public Boolean addMessage (Long idHum, String message, Long idChat, int userType, Long replyTo, List<String> paths) {
        return chatEsoManager.addMessage(idHum, message, idChat, userType, replyTo, paths);
    }

    @Override
    public List<Message> downloadMessages (Long last) {
        return chatEsoManager.downloadMessages(last);
    }
}
