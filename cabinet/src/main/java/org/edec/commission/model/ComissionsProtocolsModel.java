package org.edec.commission.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor

public class ComissionsProtocolsModel {
    private String  timeComission, protocolNumber, groupname, chairsName, fioStudent, subjectname, regNumber, certnumber, comDateStr, semester;
    private Date dateComission;
    private int retakeCount;
    private Long idRegister, idRegCom;

}
