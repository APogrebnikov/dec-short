package org.edec.rest.model.student.request;

import lombok.Data;
import org.edec.rest.model.BaseUserMsg;

import java.util.Date;

@Data
public class AttendanceMsg extends BaseUserMsg {
    private Long idLGSS;
    private Integer pos;
    private Date date;
}
