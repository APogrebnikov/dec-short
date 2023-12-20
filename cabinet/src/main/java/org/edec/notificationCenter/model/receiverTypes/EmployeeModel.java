package org.edec.notificationCenter.model.receiverTypes;

import lombok.Data;
import org.edec.notificationCenter.model.ReceiverModel;

@Data
public class EmployeeModel extends ReceiverModel {

    private Long idHumanface;
    private String fio;
}
