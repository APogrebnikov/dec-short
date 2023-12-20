package org.edec.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lunin on 26.09.2017.
 */
@Getter
@Setter
@NoArgsConstructor
public class Dialog {

    private Long id;
    private String title;
    /*private LocalDateTime createdAt;*/
    private List<Message> messages = new ArrayList<>();

    /**
     * Конструктор диалога
     *
     * @param id
     * @param title
     */
    public Dialog (Long id, String title/*, LocalDateTime createdAt, List<Message> messages*/) {
        this.id = id;
        this.title = title;
    }

    public void addMessage (Message message) {
        messages.add(message);
    }
}
