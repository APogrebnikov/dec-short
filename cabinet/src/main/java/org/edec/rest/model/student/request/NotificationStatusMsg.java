package org.edec.rest.model.student.request;

import lombok.Data;
import org.edec.rest.model.BaseUserMsg;

import java.util.Date;
import java.util.List;

@Data
public class NotificationStatusMsg extends BaseUserMsg {
    private List<Long> notifications;
}
