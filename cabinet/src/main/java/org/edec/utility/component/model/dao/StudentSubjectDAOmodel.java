package org.edec.utility.component.model.dao;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StudentSubjectDAOmodel {
    private Boolean exam, pass, cp, cw, practic;

    private Double hoursCount;

    private Integer semester;
    private Integer type;

    private Long idSSS;
    private Long idSubj;

    private String subjectname;

    public StudentSubjectDAOmodel () {
    }
}
