package org.edec.commons.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.model.StudentModel;


@Getter
@Setter
public class StudentGroupModel extends StudentModel {
    private Long idDG;
    private String directionCode, directionName;
    private Integer qualification;

    private String groupname;
}
