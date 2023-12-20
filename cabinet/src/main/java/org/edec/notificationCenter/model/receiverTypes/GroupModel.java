package org.edec.notificationCenter.model.receiverTypes;

import lombok.Data;
import org.edec.notificationCenter.model.ReceiverModel;

@Data
public class GroupModel extends ReceiverModel {

    private Long idGroup;
    private String groupName;
}
