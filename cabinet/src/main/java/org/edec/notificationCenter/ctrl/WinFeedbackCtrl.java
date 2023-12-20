package org.edec.notificationCenter.ctrl;

import org.edec.notificationCenter.model.CommentaryModel;
import org.edec.notificationCenter.model.FeedbackModel;
import org.edec.notificationCenter.model.NotificationModel;
import org.edec.notificationCenter.service.NotificationService;
import org.edec.notificationCenter.service.impl.NotificationServiceImpl;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WinFeedbackCtrl extends CabinetSelector {

    //Используем данный id для отправки сообщений со стороны деканата
    public static final Long ID_DECANAT = 0L;

    @Wire
    Listbox lbReceiversFeedback;

    @Wire
    Button btnSendMessage;

    @Wire
    Textbox tbMessage, tbSearchReceiver;

    @Wire
    Div divChat;

    NotificationModel notification;

    List<FeedbackModel> feedbackList;

    NotificationService service;

    List<CommentaryModel> selectedChatHistory = new ArrayList<>();
    List<CommentaryModel> unreadMessages = new ArrayList<>();

    @Override
    protected void fill() {

        notification = (NotificationModel) Executions.getCurrent().getArg().get(IndexPageCtrl.NOTIFICATION);
        service = (NotificationServiceImpl) Executions.getCurrent().getArg().get(IndexPageCtrl.NOTIFICATION_SERVICE);

        feedbackList = service.getFeedback(notification.getId());

        lbReceiversFeedback.setItemRenderer((ListitemRenderer<FeedbackModel>) (item, data, index) -> {
            item.setValue(data);

            item.addEventListener(Events.ON_CLICK, event -> {
                selectedChatHistory = service.getComments(ID_DECANAT, data.getIdLinkNotificationHumanface());

                unreadMessages = selectedChatHistory.stream().filter(comment -> !comment.getIsRead() && comment.getIdSender() != ID_DECANAT)
                                                    .collect(Collectors.toList());

                service.updateCommentStatus(unreadMessages);

                service.fillDivChat(divChat, selectedChatHistory, ID_DECANAT);

                enableChatComponents();
            });

            new Listcell().setParent(item);

            new Listcell(data.getFio()).setParent(item);

            Listcell lcUnreadMessageCount = new Listcell();
            lcUnreadMessageCount.setParent(item);

            if (data.getUnreadMessageCount() != 0) {
                Label lUnreadMessageCount = new Label(data.getUnreadMessageCount().toString());
                lUnreadMessageCount.setStyle("background: red; color: white; border-radius: 10px; padding: 3px;");
                lUnreadMessageCount.setParent(lcUnreadMessageCount);
            }
        });

        fillFeedbackListbox(feedbackList);
    }

    private void enableChatComponents() {
        btnSendMessage.setDisabled(false);
        tbMessage.setDisabled(false);
    }

    public void fillFeedbackListbox(List<FeedbackModel> feedbackList) {

        lbReceiversFeedback.setModel(new ListModelList<>(feedbackList));
        lbReceiversFeedback.renderAll();
    }

    @Listen("onOK = #tbSearchReceiver")
    public void filterReceivers(){
        String filterFio = tbSearchReceiver.getValue();

        List<FeedbackModel> filteredFeedbackList = feedbackList.stream().filter(feedbackModel -> feedbackModel.getFio().contains(filterFio)).collect(Collectors.toList());

        fillFeedbackListbox(filteredFeedbackList);
    }

    @Listen("onClick = #btnUpdateChat")
    public void updateSelectedChatHistory() {
        FeedbackModel selectedChat = lbReceiversFeedback.getSelectedItem().getValue();

        selectedChatHistory = service.getComments(ID_DECANAT, selectedChat.getIdLinkNotificationHumanface());
    }

    @Listen("onClick = #btnSendMessage")
    public void sendMessage() {
        if (tbMessage.getValue().equals("")) {
            return;
        }

        if (selectedChatHistory.size() == 0) {
            divChat.getChildren().clear();
        }

        FeedbackModel selectedChat = lbReceiversFeedback.getSelectedItem().getValue();

        CommentaryModel newCommentary = new CommentaryModel();
        newCommentary.setIdSender(ID_DECANAT);
        newCommentary.setMessage(tbMessage.getText());
        newCommentary.setNameSender("Вы");

        service.sendComment(selectedChat.getIdLinkNotificationHumanface(), newCommentary);

        selectedChatHistory = service.getComments(ID_DECANAT, selectedChat.getIdLinkNotificationHumanface());

        tbMessage.setText("");

        service.constructDivMessage(divChat, newCommentary,
                                    selectedChatHistory.isEmpty() ? null : selectedChatHistory.get(selectedChatHistory.size() - 1).getDatePosted(),
                                    ID_DECANAT
        );

    }
}
