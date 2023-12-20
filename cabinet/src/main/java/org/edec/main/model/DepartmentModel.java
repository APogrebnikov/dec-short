package org.edec.main.model;

import lombok.Data;


@Data
public class DepartmentModel {
    private Long idChair;
    private Long idDepartment;
    private Long idDepartmentMine;
    private Long idInstitute;
    private Long idInstituteMine;

    private String fulltitle;
    private String institute;
    private String shorttitle;
}
