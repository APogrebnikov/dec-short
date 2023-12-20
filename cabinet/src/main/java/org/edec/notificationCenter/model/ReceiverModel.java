package org.edec.notificationCenter.model;

import lombok.Data;
import org.edec.utility.constants.ReceiverStatusConst;
import org.edec.utility.constants.ReceiverTypeConst;

@Data
public abstract class ReceiverModel {

    private ReceiverTypeConst receiverType;

    private Integer status;

    public ReceiverModel(){
        status = ReceiverStatusConst.NOT_ADDED;
    }
}
