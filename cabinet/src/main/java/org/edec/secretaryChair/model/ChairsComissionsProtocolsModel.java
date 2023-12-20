package org.edec.secretaryChair.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ChairsComissionsProtocolsModel {
    private String  timeComission, protocolNumber, chairsName, fioStudent, subjectname, regNumber, certnumber, comDateStr, groupname, semester;
    private Date dateComission;
    private int retakeCount;
    private Long idRegister, idRegCom;
}
