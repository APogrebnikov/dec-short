package org.edec.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by lunin on 17.10.2017.
 */
@Getter
@Setter
@NoArgsConstructor
public class Attachment {
    private Long uid;
    private Long messageId;
    private String title;
    private byte[] file;
    private String url;

    public Attachment (Long id, Long messageId, String title, byte[] file, String url) {
        this.uid = id;
        this.messageId = messageId;
        this.title = title;
        this.file = file;
        this.url = url;
    }
}
