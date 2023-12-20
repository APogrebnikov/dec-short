package org.edec.utility.component.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SubjectModel {
    private Boolean exam, pass, cp, cw, practic;

    private Double hoursCount;

    private Integer formOfControl;
    private Integer type;

    private Long idLGSS;
    private Long idSubj;

    private String subjectname;

    public SubjectModel () {
    }
}
