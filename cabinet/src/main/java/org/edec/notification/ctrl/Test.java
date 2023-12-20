package org.edec.notification.ctrl;

import org.edec.chat.model.Message;
import org.edec.chat.service.ChatService;
import org.edec.chat.service.impl.ChatServiceImpl;

import java.util.ArrayList;
import java.util.List;


public class Test {

    static public void main () {
        for (int i = 0; i < 10000; i++) {
            ChatService chatService = new ChatServiceImpl();
            List<Message> loadMessages = new ArrayList<Message>();
            loadMessages = chatService.getDownMessages(new Long(7), new Long(286));
            //Pause for 4 seconds
            long f = 1000;
            try {
                // thread to sleep for 1000 milliseconds
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println(e);
            }
            //print a message
            System.out.println(i);
        }
    }
}
