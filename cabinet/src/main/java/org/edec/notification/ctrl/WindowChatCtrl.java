package org.edec.notification.ctrl;

import org.edec.chat.ctrl.component.Format;
import org.edec.chat.ctrl.component.Icons;
import org.edec.chat.ctrl.component.MessageAboutComponent;
import org.edec.chat.ctrl.component.SendPanelComponent;
import org.edec.chat.model.Dialog;
import org.edec.chat.model.Message;
import org.edec.chat.service.ChatService;
import org.edec.chat.service.impl.ChatServiceImpl;

import org.edec.main.ctrl.TemplatePageCtrl;

import org.edec.notification.service.NotificationDecService;
import org.edec.notification.service.impl.NotificationDecServiceImpl;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class WindowChatCtrl extends SelectorComposer {

    private TemplatePageCtrl template = new TemplatePageCtrl();
    private ChatService chatService = new ChatServiceImpl();
    private NotificationDecService decService = new NotificationDecServiceImpl();
    private Dialog dialog = new Dialog();
    private Long userDialog;
    private Long idRecipient;

    private MessageAboutComponent messagePanel;
    private SendPanelComponent sendPanel = new SendPanelComponent();

    private Message activeMessage;
    private Div activeDiv;

    private Media attachment;

    @Wire
    private Div aboutDec;    // Панель для дополнительных действий
    @Wire
    private Div messagesDec; // Блок сообщений
    @Wire
    private Div sendDec;     // Панель отправления

    @Wire
    private org.zkoss.zul.Timer timer;

    @Override
    public void doAfterCompose(Component window) throws Exception {
        super.doAfterCompose(window);
        fill();
    }

    private void fill() {
        dialog.setTitle((String) Executions.getCurrent().getArg().get("nameWindow"));
        //Messagebox.show((String) Executions.getCurrent().getArg().get("userDialog") + "/" + (String) Executions.getCurrent().getArg().get("humanfaceId") );
        userDialog = (Long) Executions.getCurrent().getArg().get("userDialog");
        idRecipient = (Long) Executions.getCurrent().getArg().get("humanfaceId");
        messagePanel = new MessageAboutComponent(dialog.getTitle());
        messagePanel.liteInit();

        aboutDec.appendChild(messagePanel);
// TODO

        sendDec.appendChild(sendPanel);
        bindPanel(dialog, messagesDec);
        loadMessages(messagesDec, dialog);
        Test test = new Test();
        //test.main();
        startTimer();
    }

    public void startTimer() {
        timer.addEventListener(Events.ON_TIMER, new org.zkoss.zk.ui.event.EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                loadMessages(messagesDec);
            }
        });
    }

    @Listen("onClose = this")
    public void closeWindow() {
        PopupUtil.showInfo("Зашел");
    }

    private void bindPanel(Dialog dialog, Div component) {
        messagePanel.getFiles().addEventListener(Events.ON_CLICK, event -> getFilesGroup());

        sendPanel.getSend().addEventListener(Events.ON_CLICK, event -> sendMessage(dialog, component));
        sendPanel.getClip().addEventListener(Events.ON_UPLOAD, event -> upload(((UploadEvent) event)));
        sendPanel.getAttachment().addEventListener(Events.ON_CLICK, event -> close());
        sendPanel.getMessage().addEventListener(Events.ON_OK, event -> sendMessage(dialog, component));
        sendPanel.getMessage().setFocus(true);
    }

    private void getFilesGroup() {
        ComponentHelper.createWindow("/chat/windowFiles.zul", "modalDialog", null).doModal();
    }

    private void upload(UploadEvent event) {
        String format = event.getMedia().getFormat().toLowerCase();

        if (!Format.isImage(format) && !Format.isText(format)) {
            Messagebox.show("Выбран файл другого типа", "Warning", Messagebox.OK, Messagebox.INFORMATION);
            return;
        }

        attachment = event.getMedia();

        sendPanel.getAttachment().setLabel((Format.isImage(format)) ? Icons.INSERT_PHOTO.toString()
                : Icons.INSERT_FILE.toString());
        sendPanel.swap(sendPanel.getClip(), sendPanel.getAttachment());
    }

    private void deleteAttachment() {
        attachment = null;
        sendPanel.swap(sendPanel.getAttachment(), sendPanel.getClip());
        close();
    }

    private void sendMessage(Dialog dialog, Div currentMessage) {
        send(dialog, currentMessage);
        sendPanel.getMessage().setValue("");
    }

    private void loadMessages(Div currentMessages, Dialog dialog) {

        List<Message> loadMessages = decService.getUnreadMessages(userDialog);
        if (loadMessages.size() != 0) {
            List<Message> upMessages = chatService.getUpMessages(userDialog, loadMessages.get(0).getId());
            for (int i = 0; i <= upMessages.size() - 1; i++) {
                loadMessages.add(0, upMessages.get(i));
            }
            dialog.getMessages().addAll(loadMessages);

            displayButton();
            messagesDec.appendChild(displayDialog(dialog));
            decService.updateLastMessage(userDialog, loadMessages.get(loadMessages.size() - 1).getId());

        }

    }

    private void loadMessages(Div currentMessages) {
        int size = dialog.getMessages().size();
        if (size <= 0) {
            return;
        }

        Message lastMessage = dialog.getMessages().get(size - 1);
        List<Message> loadMessages = new ArrayList<Message>();
        loadMessages = chatService.getDownMessages(userDialog, lastMessage.getId());
        if (loadMessages.size() != 0) {
            dialog.getMessages().addAll(loadMessages);
            for (Message message : loadMessages) {
                messagesDec.appendChild(displayMessage(message));
            }

            decService.updateLastMessage(userDialog, loadMessages.get(loadMessages.size() - 1).getId());

            Clients.evalJavaScript("scrollLoadMessage()");

        }

    }

    private void displayButton() {
        Button button = new Button();
        button.addEventListener(Events.ON_CLICK, new org.zkoss.zk.ui.event.EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                List<Message> upMessages = chatService.getUpMessages(userDialog, dialog.getMessages().get(0).getId());
                if (upMessages.size() != 0) {

                    for (int i = 0; i <= upMessages.size() - 1; i++) {
                        dialog.getMessages().add(0, upMessages.get(i));

                    }
                    Div r = new Div();

                    for (int i = 0; i <= upMessages.size() - 1; i++) {
                        r.appendChild(displayMessage(dialog.getMessages().get(i)));

                    }
                    messagesDec.insertBefore(r, messagesDec.getChildren().get(1));
                } else {
                    messagesDec.removeChild(messagesDec.getFirstChild());
                }
            }
        });
        button.setLabel("add");
        button.setSclass("icons");
        Div div = new Div();
        div.setSclass("div-button");
        div.appendChild(button);
        messagesDec.appendChild(div);
    }

    private void displayMessage(Div side, Message message) {
        Div dataDiv = new Div();
        dataDiv.setSclass("message-data");

        /*if (!message.getIdHuman().equals(currentUser.getIdHum())) {
            Label authorLabel = new Label();
            authorLabel.setSclass("message-data_author");
            for (HumanfaceModel h : humanfaceModels) {
                if(h.getIdHum().equals(message.getIdHuman())) {
                    authorLabel.setValue(h.getFamily() + " " + h.getName());
                    break;
                }
            }
            if (authorLabel.getValue().isEmpty()) {
                authorLabel.setValue("Ошибочный пользователь");
            }
            dataDiv.appendChild(authorLabel);
        }*/

        Label timeLabel = new Label();
        timeLabel.setClass("message-data_time");
        timeLabel.setValue(formatLocalDateTime(message.getCreatedAt()));
        dataDiv.appendChild(timeLabel);

        Div contentDiv = new Div();
        contentDiv.setSclass("message-content");

        if (message.getReply() != null) {
            Div attach = new Div();
            attach.setSclass("message-attach");
            displayMessage(attach, message.getReply());
            attach.setParent(contentDiv);
        }

        Label textLabel = new Label();
        textLabel.setSclass("message-content_text");
        textLabel.setValue(message.getInformation());
        contentDiv.appendChild(textLabel);

        dataDiv.setParent(side);
        contentDiv.setParent(side);
    }

    private Div displayMessage(Message message) {
        Div r = new Div();
        r.setSclass(message.getIdHuman().equals(new Long(-3)) ? "message-right" : "message-left");

        displayMessage(r, message);

        r.getLastChild().addEventListener(Events.ON_CLICK, event -> select((Div) r.getLastChild(), message));
        return r;
    }

    private Div displayDialog(Dialog dialog) {
        Div r = new Div();

        for (Message message : dialog.getMessages()) {
            r.appendChild(displayMessage(message));
        }

        return r;
    }

    private String formatLocalDateTime(LocalDateTime localDateTime) {
        LocalDateTime checkTime = LocalDateTime.now();
        //Производним смещение в текущий часовой пояс
        ZonedDateTime zdt = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));
        ZonedDateTime withLocalZone = zdt.withZoneSameInstant(ZoneId.systemDefault());
        localDateTime = withLocalZone.toLocalDateTime();

        if (localDateTime.getDayOfMonth() == checkTime.getDayOfMonth()
                && localDateTime.getMonth() == checkTime.getMonth()
                && localDateTime.getYear() == checkTime.getYear()) {

            return localDateTime.format(DateTimeFormatter.ofPattern(" HH:mm"));
        }

        checkTime = LocalDateTime.of(checkTime.getYear(), checkTime.getMonth(), checkTime.getDayOfMonth(), 0, 0, 0)
                .minusDays(1);

        if (checkTime.isAfter(localDateTime)) {
            return localDateTime.format(DateTimeFormatter.ofPattern(" d LLL HH:mm"));
        } else {
            return "Вчера " + localDateTime.format(DateTimeFormatter.ofPattern(" HH:mm"));
        }
    }

    /**
     * Возращает чат в исходное состояние
     */
    private void close() {
        messagePanel.initPanel();
        activeDiv.setStyle("");

        activeMessage = null;
        attachment = null;

        sendPanel.swap(sendPanel.getAttachment(), sendPanel.getClip());
    }

    /**
     * Выделить блок и запомнить выбранное сообщение
     *
     * @param content Блок содержащий выбранное сообщение
     * @param message Выбранное сообщение
     */
    private void select(Div content, Message message) {
        if (activeMessage != null && activeMessage.equals(message)) {
            close();
            return;
        }

        content.setStyle("color: #fafafa; background: #009699;");

        if (activeMessage != null) {
            activeDiv.setStyle("");
        }

        activeMessage = message;
        activeDiv = content;

        messagePanel.update();
        messagePanel.getStar().setLabel((activeMessage.getImportant()) ? "star" : "star_border");

        sendPanel.getAttachment().setLabel(Icons.INSERT_TEXT.toString());
        sendPanel.swap(sendPanel.getClip(), sendPanel.getAttachment());
    }

    /**
     * Добавляет сообщение в выбранный диалог и выводит сообщение
     *
     * @param dialog   текущий диалог
     * @param messages выбранный список сообщений
     */
    private void send(Dialog dialog, Div messages) {
        if (sendPanel.getMessage().getValue().isEmpty()) {
            return;
        }

        Message message = new Message();
        message.setInformation(sendPanel.getMessage().getValue());
        message.setIdHuman((long) -3); // dtcanat_id -3
        message.setImportant(false);
        message.getRecipients().add(idRecipient);

        // TODO: нужно что-то сделать (метод на сервисе и подгрузка в чат)

        message.setReply(null);
        if (attachment != null) {
            message.setAttachment(null);
        }      // TODO:

        Boolean result = decService.sendNotification(message);
        if (result.equals(false)) {
            PopupUtil.showError("Ошибка при отправке сообщения!");
            return;
        }

    }
}

