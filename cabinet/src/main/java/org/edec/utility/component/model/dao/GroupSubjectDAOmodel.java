package org.edec.utility.component.model.dao;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GroupSubjectDAOmodel {
    private Boolean exam, pass, cp, cw, practic;

    private Double hoursCount;

    private Integer course;
    private Integer semester;
    private Integer type;

    private Long idDG;
    private Long idLGS;
    private Long idSubj;

    private String groupname;
    private String subjectname;

    public GroupSubjectDAOmodel () {
    }
}
