package org.edec.chat.ctrl;

import org.edec.chat.ctrl.component.Format;
import org.edec.chat.ctrl.component.Icons;
import org.edec.chat.ctrl.component.MessageAboutComponent;
import org.edec.chat.ctrl.component.SendPanelComponent;
import org.edec.chat.manager.ChatEsoManager;
import org.edec.chat.model.Dialog;
import org.edec.chat.model.Message;
import org.edec.chat.service.ChatService;
import org.edec.chat.service.impl.ChatServiceImpl;
import org.edec.main.model.UserModel;
import org.edec.model.HumanfaceModel;

import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import javax.websocket.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by lunin on 02.10.2017.
 */

@ClientEndpoint
public class IndexPageCtrl extends CabinetSelector {

    private static Long last = (long) 64;

    Session userSession = null;

    private MessageHandler messageHandler;

    private List<Message> listMessages;

    private UserModel currentUser = template.getCurrentUser();

    private ChatService chatService = new ChatServiceImpl();
    private ChatEsoManager chatEsoManager = new ChatEsoManager();
    private List<HumanfaceModel> humanfaceModels = chatEsoManager.getHumanfaceModel(currentUser.getGroupname());

    private Dialog dialogGroup = new Dialog();
    private Long userDialog;


    private MessageAboutComponent messagePanel;
    private SendPanelComponent sendPanel = new SendPanelComponent();

    private Message activeMessage;
    private Div activeDiv;

    private Media attachment;

    private Desktop desktop;

    @Wire
    private Div aboutGroup;    // Панель для дополнительных действий
    @Wire
    private Div messagesGroup; // Блок сообщений
    @Wire
    private Div sendGroup;     // Панель отправления

    @Wire
    private Textbox textMessageGroup;
    @Wire
    private Label loadFile;
    @Wire
    private Div messageBox;
    @Wire
    private Button sendMessageGroup;
    @Wire
    private Button chooseFileGroup;

    public IndexPageCtrl() {
    }

    protected void fill() throws InterruptedException {
        dialogGroup.setTitle(currentUser.getGroupname());
        messagePanel = new MessageAboutComponent(dialogGroup.getTitle());

        aboutGroup.appendChild(messagePanel);
        userDialog = chatService.getUserDialog(currentUser.getIdHum(), dialogGroup.getTitle());
        loadMessages(messagesGroup, dialogGroup);
        sendGroup.appendChild(sendPanel);

        bindPanel(dialogGroup, messagesGroup);
        desktop = Executions.getCurrent().getDesktop();
        desktop.enableServerPush(true);

        try {
            connect(new URI("ws://localhost:9090/ChatServer_war_exploded/ws"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        addMessageHandler(new MessageHandler() {
            public void handleMessage(String message) {
                System.out.println(message);
            }
        });

        //Подгрузка
        downloadMessages(null, dialogGroup, messagesGroup);
    }

    private void bindPanel(Dialog dialog, Div component) {
        messagePanel.getSearch().addEventListener(Events.ON_MOUSE_OVER, event -> searchFocus());
        messagePanel.getSearch().addEventListener(Events.ON_CLICK, event -> search(dialog));
        messagePanel.getKeyword().addEventListener(Events.ON_OK, event -> search(dialog));
        messagePanel.getFiles().addEventListener(Events.ON_CLICK, event -> getFilesGroup());
        messagePanel.getImportant().addEventListener(Events.ON_CLICK, event -> getImportantMessages());
        messagePanel.getReply().addEventListener(Events.ON_CLICK, event -> {
        });
        messagePanel.getClose().addEventListener(Events.ON_CLICK, event -> close());

        if (currentUser.isGroupLeader()) {
            messagePanel.getStar().addEventListener(Events.ON_CLICK, event -> important(activeMessage, messagePanel.getStar()));
        } else {
            messagePanel.getStar().addEventListener(Events.ON_CLICK, event -> getImportantMessages());
        }

        sendPanel.getSend().addEventListener(Events.ON_CLICK, event -> sendMessage(dialog, component));
        sendPanel.getClip().addEventListener(Events.ON_UPLOAD, event -> upload(((UploadEvent) event)));
        sendPanel.getAttachment().addEventListener(Events.ON_CLICK, event -> close());
        sendPanel.getMessage().addEventListener(Events.ON_OK, event -> sendMessage(dialog, component));
        sendPanel.getMessage().setFocus(true);
    }

    private void getFilesGroup() {
        // TODO почему у 2-ух разных окон одинаковые идентификаторы? Необходимо исправить.
        ComponentHelper.createWindow("/chat/windowFiles.zul", "modalDialog", null).doModal();
    }

    private void getImportantMessages() {
        ComponentHelper.createWindow("/chat/windowMessage.zul", "modalDialog", null).doModal();
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

    private void sendMessage(Dialog dialog, Div messages) {
        send(dialog, messages);
        sendPanel.getMessage().setValue("");
    }

    private void search(Dialog dialog) {
        Executions.getCurrent().getSession().getAttribute("some_param");
        Executions.getCurrent().getSession().setAttribute("some_param", null);

        if (!messagePanel.getKeyword().getValue().isEmpty()) {
            String keyword = messagePanel.getKeyword().getValue();
            List<Message> messages = chatService.findMessageByInformation(userDialog, keyword);
            dialog.getMessages().addAll(messages);
            messagePanel.getKeyword().setValue("");
            // TODO: update list of message;
        }
        messagePanel.getKeyword().setFocus(false);
    }

    private void searchFocus() {
        messagePanel.getKeyword().setFocus(true);
    }

    private void loadMessages(Div currentMessages, Dialog dialog) {
        List<Message> loadMessages = chatService.getUnreadMessages(userDialog);
        if (loadMessages.size() != 0) {
            currentMessages.appendChild(displayDialog(dialog));
            dialog.getMessages().addAll(loadMessages);
            chatService.updateLastMessage(userDialog, loadMessages.get(loadMessages.size() - 1).getId());
        }
        Clients.evalJavaScript("scroll()");
    }

    private void loadMessages(Div currentMessages) {
        int size = dialogGroup.getMessages().size();
        if (size <= 0) {
            return;
        }

        Message lastMessage = dialogGroup.getMessages().get(size - 1);
        List<Message> loadMessages = chatService.getDownMessages(userDialog, lastMessage.getId());

        if (loadMessages != null) {
            dialogGroup.getMessages().addAll(loadMessages);
            for (Message message : loadMessages) {
                currentMessages.appendChild(displayMessage(message));
            }

            if (loadMessages.size() != 0) {
                chatService.updateLastMessage(userDialog, loadMessages.get(loadMessages.size() - 1).getId());
            }

            Clients.evalJavaScript("scrollLoadMessage()");
            Clients.scrollIntoView(messagesGroup.getLastChild());
        }

    }

    private void displayMessage(Div side, Message message) {
        Div dataDiv = new Div();
        dataDiv.setSclass("message-data");

        if (!message.getIdHuman().equals(currentUser.getIdHum())) {
            Label authorLabel = new Label();
            authorLabel.setSclass("message-data_author");
            for (HumanfaceModel h : humanfaceModels) {
                if (h.getIdHum().equals(message.getIdHuman())) {
                    authorLabel.setValue(h.getFamily() + " " + h.getName());
                    break;
                }
            }
            if (authorLabel.getValue().isEmpty()) {
                authorLabel.setValue("Ошибочный пользователь");
            }
            dataDiv.appendChild(authorLabel);
        }

        Label timeLabel = new Label();
        timeLabel.setClass("message-data_time");
        timeLabel.setValue(formatLocalDateTime(convertToLocalDateTimeViaMilisecond(message.getPosted())));
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

    //Отображение

    private Div displayMessage(Message message) {
        Div r = new Div();
        r.setSclass(message.getIdHuman().equals(currentUser.getIdHum()) ? "message-right" : "message-left");

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
     * Изменение важности сообщения
     *
     * @param message Выбранное сообщение
     * @param btn     Нажатая кнопка
     * @return Статус выполнения запроса
     */

    private void important(Message message, Button btn) { // TODO: tutudum
        message.setImportant(!message.getImportant());

        int status = (message.getImportant()) ? 0 : 1;

        if (false/*chatService.noteImportantMessage(message.getId(), status)*/) {
            btn.setLabel("star_border");
            PopupUtil.showError("Ошибка");
        }

        btn.setLabel((activeMessage.getImportant()) ? "star" : "star_border");
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

    private void send(Dialog dialog, Div messages) {
        if (sendPanel.getMessage().getValue().isEmpty()) {
            return;
        }

        boolean result = chatService.addMessage(template.getCurrentUser().getIdHum(), sendPanel.getMessage().getValue(), (long) 1,
                1, (long) 5, new ArrayList<>());

        if (!result) {
            PopupUtil.showError("Ошибка при отправке сообщения!");
            return;
        } else {
            sendMessage("SendMessage");
        }
    }

    private void downloadMessages(Long last, Dialog dialog, Div messages) {
        listMessages = chatService.downloadMessages(last);
        for (Message i : listMessages) {
            messages.appendChild(displayMessage(i));
            dialog.addMessage(i);
        }
        loadMessages(messages, dialog);
    }

    public LocalDateTime convertToLocalDateTimeViaMilisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    // WebSocket

    public void connect(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session userSession) throws InterruptedException {
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("closing webSocket");
        this.userSession = null;
    }

    @OnMessage
    public void onMessage(String message) throws InterruptedException {
        if (message.equals("SendMessage")) {
            Executions.activate(desktop);
            downloadMessages(last++, dialogGroup, messagesGroup);
            messageHandler.handleMessage(message);
            Executions.deactivate(desktop);
        }
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        messageHandler = msgHandler;
    }

    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    public static interface MessageHandler {
        public void handleMessage(String message);
    }
}