package org.edec.rest.model.student.request;

import lombok.Data;
import org.edec.rest.model.BaseUserMsg;

@Data
public class ReportMsg extends BaseUserMsg {
    String text;
    String userId;
    String os;
    String osVersion;
    String appVersion;
    String data;
}
