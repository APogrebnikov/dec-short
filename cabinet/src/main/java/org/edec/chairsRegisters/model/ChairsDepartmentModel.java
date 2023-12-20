package org.edec.chairsRegisters.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@NoArgsConstructor
public class ChairsDepartmentModel {
    private String fulltitle;
    private Long idDepartment, idChair, idInst;
}
