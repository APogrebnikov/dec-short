package org.edec.rest.model.teacher.request;

import lombok.Data;
import org.edec.rest.model.BaseUserMsg;

@Data
public class SubjectMarkMsg extends BaseUserMsg {
    Boolean exam;
    Boolean pass;
    Boolean cp;
    Boolean cw;
    Boolean practic;
    Integer passType;
    Integer newRating;
    Long idSR;
    Integer retakeCount;
    Long idSRH;

    public Boolean isExam() {
        if (exam == null) {
            return false;
        }
        return exam;
    }

    public Boolean isPass() {
        if (pass == null) {
            return false;
        }
        return pass;
    }

    public Boolean isCp() {
        if (cp == null) {
            return false;
        }
        return cp;
    }

    public Boolean isCw() {
        if (cw == null) {
            return false;
        }
        return cw;
    }

    public Boolean isPractic() {
        if (practic == null) {
            return false;
        }
        return practic;
    }
}
