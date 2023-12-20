package org.edec.synchroMine.model.dao;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Max Dimukhametov
 */
@Getter
@Setter
public class WorkloadModel {
    private Integer course;
    private Integer semester;

    private String family;
    private String name;
    private String patronymic;
    private String subjectname;
}
