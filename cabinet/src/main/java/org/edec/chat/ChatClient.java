package org.edec.chat;

import org.edec.chat.ctrl.IndexPageCtrl;
import org.edec.chat.service.ChatService;
import org.edec.chat.service.impl.ChatServiceImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Date;
import javax.websocket.*;

@ClientEndpoint
public class ChatClient {
    Session userSession = null;
    private MessageHandler messageHandler;

    public ChatClient (URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen (Session userSession) {
        System.out.println("opening webSocket");
        this.userSession = userSession;
    }

    @OnClose
    public void onClose (Session userSession, CloseReason reason) {
        System.out.println("closing webSocket");
        this.userSession = null;
    }

    @OnMessage
    public void onMessage (String message) {
        if (message.equals("SendMessage")) {
            this.messageHandler.handleMessage(message);
        }
    }

    public void addMessageHandler (MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public void sendMessage (String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    public static interface MessageHandler {
        public void handleMessage (String message);
    }
}