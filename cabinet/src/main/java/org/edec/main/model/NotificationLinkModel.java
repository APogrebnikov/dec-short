package org.edec.main.model;

import lombok.Data;

@Data
public class NotificationLinkModel {
    int count;
    String name;
    String uri;

    public NotificationLinkModel(int count,String name, String uri){
        this.count = count;
        this.name = name;
        this.uri = uri;
    }
}
