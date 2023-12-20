package org.edec.notificationCenter.ctrl.renderer;

import org.edec.notificationCenter.model.NotificationModel;
import org.edec.utility.converter.DateConverter;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.function.Consumer;

public class NotificationRenderer implements ListitemRenderer<NotificationModel> {

    private Consumer<NotificationModel> openNotificationEditor;
    private Consumer<NotificationModel> openChat;
    private Consumer<NotificationModel> removeNotification;

    public NotificationRenderer(Consumer<NotificationModel> openNotificationEditor,
                                Consumer<NotificationModel> openChat,
                                Consumer<NotificationModel> removeNotification) {
        this.openNotificationEditor = openNotificationEditor;
        this.openChat = openChat;
        this.removeNotification = removeNotification;
    }

    @Override
    public void render(Listitem listitem, NotificationModel notificationModel, int i) throws Exception {
        listitem.setValue(notificationModel);

        new Listcell(DateConverter.convertDateToString(notificationModel.getDatePosted())).setParent(listitem);
        new Listcell(notificationModel.getHeader()).setParent(listitem);
        new Listcell(notificationModel.getReceiverType()).setParent(listitem);

        Listcell lcEdit = new Listcell();
        lcEdit.setParent(listitem);

        Button btnEdit = new Button("", "/imgs/edit.png");
        btnEdit.setParent(lcEdit);

        btnEdit.addEventListener(Events.ON_CLICK, event -> openNotificationEditor.accept(notificationModel));

        Listcell lcRemove = new Listcell();
        lcRemove.setParent(listitem);

        Button btnRemove = new Button("","/imgs/delaltCLR.png");
        btnRemove.setParent(lcRemove);

        btnRemove.addEventListener(Events.ON_CLICK, event -> removeNotification.accept(notificationModel));

        if(notificationModel.getIsSent()) btnRemove.setDisabled(true);

        Listcell lcChat = new Listcell();
        lcChat.setParent(listitem);

        Hbox hbox = new Hbox();
        hbox.setParent(lcChat);

        Button btnChat = new Button("", "/imgs/chat.png");
        btnChat.setParent(hbox);

        btnChat.addEventListener(Events.ON_CLICK, event -> openChat.accept(notificationModel));

        if (notificationModel.getUnreadMessageCounter() != 0) {
            btnChat.setStyle("");
            Label lUnreadMessageCount = new Label(notificationModel.getUnreadMessageCounter().toString());
            lUnreadMessageCount.setStyle("background: red; color: white; border-radius: 10px; padding: 3px;");
            lUnreadMessageCount.setParent(hbox);
        }
    }
}
