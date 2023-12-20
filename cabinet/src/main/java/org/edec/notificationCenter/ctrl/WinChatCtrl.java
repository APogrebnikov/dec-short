package org.edec.notificationCenter.ctrl;

import org.edec.notificationCenter.model.CommentaryModel;
import org.edec.notificationCenter.model.NotificationModel;
import org.edec.notificationCenter.service.NotificationService;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WinChatCtrl extends CabinetSelector {

    public static final String NOTIFICATION = "notification";
    public static final String SERVICE = "service";

    @Wire
    Timer timerUnreadMessages;

    @Wire
    Div divChat;

    @Wire
    Textbox tbMessage;

    List<CommentaryModel> chatMessages = new ArrayList<>();
    List<CommentaryModel> unreadMessages = new ArrayList<>();

    NotificationModel notification;
    NotificationService service;

    @Override
    protected void fill() {
        notification = (NotificationModel) Executions.getCurrent().getArg().get(NOTIFICATION);
        service = (NotificationService) Executions.getCurrent().getArg().get(SERVICE);

        chatMessages = service.getComments(getCurrentUser().getIdHum(), notification.getIdLinkNotificationHumanface());

        service.fillDivChat(divChat, chatMessages, getCurrentUser().getIdHum());

        unreadMessages = chatMessages.stream()
                                     .filter(comment -> !comment.getIsRead() && comment.getIdSender() != getCurrentUser().getIdHum())
                                     .collect(Collectors.toList());

        timerUnreadMessages.start();

        service.updateCommentStatus(unreadMessages);
    }

    @Listen("onClick = #btnSendMessage")
    public void sendMessage() {
        if (tbMessage.getText() == "") {
            return;
        }

        if (chatMessages.size() == 0) {
            divChat.getChildren().clear();
        }

        CommentaryModel newCommentary = new CommentaryModel();
        newCommentary.setIdSender(getCurrentUser().getIdHum());
        newCommentary.setMessage(tbMessage.getText());
        newCommentary.setNameSender("Вы");

        service.sendComment(notification.getIdLinkNotificationHumanface(), newCommentary);

        tbMessage.setText("");
        service.constructDivMessage(divChat, newCommentary,
                                    chatMessages.isEmpty() ? null : chatMessages.get(chatMessages.size() - 1).getDatePosted(),
                                    getCurrentUser().getIdHum()
        );

        chatMessages.add(newCommentary);
    }

    @Listen("onTimer = #timerUnreadMessages")
    public void changeUnreadMessageColor() {
        // divChat.getChildren().get(0)
    }
}
