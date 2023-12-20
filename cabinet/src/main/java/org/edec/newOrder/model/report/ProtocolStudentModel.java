package org.edec.newOrder.model.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolStudentModel {
    private String fio, groupname, type, amount, dateOfBegin, dateOfEnd;
}
