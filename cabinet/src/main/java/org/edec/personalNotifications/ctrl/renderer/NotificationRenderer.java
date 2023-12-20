package org.edec.personalNotifications.ctrl.renderer;

import javafx.util.Pair;
import org.edec.notificationCenter.ctrl.WinChatCtrl;
import org.edec.notificationCenter.model.NotificationModel;
import org.edec.notificationCenter.service.NotificationService;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationRenderer implements ListitemRenderer<NotificationModel> {

    //Идентификатор уведомлений, отправленных системой
    private static Long SYSTEM_ID = 0L;

    private NotificationService service;

    public NotificationRenderer(NotificationService service) {
        this.service = service;
    }

    @Override
    public void render(Listitem item, NotificationModel data, int index) throws Exception {
        item.setValue(data);

        Listcell lcNotification = new Listcell();
        lcNotification.setParent(item);

        Groupbox gbNotification = new Groupbox();
        gbNotification.setParent(lcNotification);
        gbNotification.setMold("3d");

        if (data.getIsRead()) {
            gbNotification.setOpen(false);
        } else {
            gbNotification.setStyle("background-color: white");
            lcNotification.setStyle("background-color: orange");
            gbNotification.setOpen(true);

            service.updatePersonalNotificationStatus(data.getIdLinkNotificationHumanface());
        }

        Caption caption = new Caption();
        caption.setParent(gbNotification);

        Hbox hbCaption = new Hbox();
        hbCaption.setParent(caption);

        Label lHeader = new Label(data.getHeader());
        lHeader.setStyle("font-weight: bold; margin-left: 8px;");
        lHeader.setParent(hbCaption);

        Hbox hbChat = new Hbox();
        hbChat.setParent(hbCaption);
        hbChat.setStyle("position: absolute; right: 0;");

        if (data.getUnreadMessageCounter() != 0) {
            new Label("Непрочитанных сообщений от деканата:").setParent(hbChat);
            Label lUnreadMessagesCount = new Label(data.getUnreadMessageCounter().toString());
            lUnreadMessagesCount.setParent(hbChat);
            lUnreadMessagesCount.setStyle("color: red; font-weight: bold; margin-right: 8px;");
        }

        //Если уведомление создано системой, то оно не предусматривает использование обратной связи
        if (!data.getIdSender().equals(SYSTEM_ID)) {
            Button btnChat = new Button("", "/imgs/chat.png");
            btnChat.setParent(hbChat);
            btnChat.addEventListener(Events.ON_CLICK, event -> openChat(data, service));
        }

        //отображаем текст уведомления
        Label text = new Label(data.getText());
        text.setMultiline(true);
        text.setParent(gbNotification);

        List<Pair<String, String>> linkList = parseHtmlLinks(data.getText());

        //отображаем прикрепленные файлы/внешние ссылки
        if (!data.getAttachedFiles().isEmpty() || !linkList.isEmpty()) {
            Hbox attachedBlock = new Hbox();
            attachedBlock.setStyle("margin-top: 8px;");
            attachedBlock.setParent(gbNotification);

            data.getAttachedFiles().forEach(attachedFile -> attachButton("z-icon-class", "", attachedFile, attachedBlock,
                                                                         event -> service.downloadFile(data.getId(), attachedFile)
            ));

            linkList.forEach(link -> attachButton("z-icon-globe", "background: #ff7e00;color: white", link.getValue(), attachedBlock,
                                                  event -> Executions.getCurrent().sendRedirect(link.getKey(), "_blank")
            ));
        }
    }

    private void attachButton(String iconClass, String style, String text, Hbox parent, EventListener<? extends Event> event) {
        Button btn = new Button(text);

        btn.setIconSclass(iconClass);
        btn.setStyle(style);
        btn.setParent(parent);
        btn.addEventListener(Events.ON_CLICK, event);

        Separator separator = new Separator();
        separator.setWidth("8px");
        separator.setParent(parent);
    }

    private List<Pair<String, String>> parseHtmlLinks(String text) {
        List<Pair<String, String>> linkList = new ArrayList<>();

        String[] textWords = text.split(" ");

        for (String word : textWords) {
            if (word.contains("www.") || word.contains("https://") || word.contains("http://")) {
                linkList.add(new Pair<>(word, "Cсылка"));
            }
        }

        return linkList;
    }

    private void openChat(NotificationModel notification, NotificationService service) {
        Map<String, Object> arg = new HashMap<>();

        arg.put(WinChatCtrl.NOTIFICATION, notification);
        arg.put(WinChatCtrl.SERVICE, service);

        ComponentHelper.createWindow("/notificationCenter/winChat.zul", "winChat", arg).doModal();
    }
}
