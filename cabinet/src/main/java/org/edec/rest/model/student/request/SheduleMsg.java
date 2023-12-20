package org.edec.rest.model.student.request;

import lombok.Data;
import org.edec.rest.model.BaseUserMsg;

import java.util.Date;

@Data
public class SheduleMsg extends BaseUserMsg {
    private Long idDicGroup;
    private Integer week;
    private Date date;
}
