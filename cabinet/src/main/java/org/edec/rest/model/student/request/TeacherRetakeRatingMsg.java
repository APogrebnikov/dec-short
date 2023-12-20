package org.edec.rest.model.student.request;

import lombok.Data;
import org.edec.rest.model.BaseUserMsg;

@Data
public class TeacherRetakeRatingMsg extends BaseUserMsg {
    private Long idReg;
}
