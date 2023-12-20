package org.edec.rest.model.student.request;

import lombok.Data;
import org.edec.rest.model.BaseUserMsg;

import java.util.Date;

@Data
public class DateMsg {
    private Date date;
    private Integer fos;
}
