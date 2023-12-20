package org.edec.register.model.dao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectModelEso {
    private Long idSubject;
    private Long idGroup;
    private Long idLgss;
    private String subjectName, groupName;
    private String familyTeacher;
    private String nameTeacher;
    private String patronymicTeacher;
    private Boolean exam, pass, cp, cw, practic;
    private Integer type;
    private Integer course;
    private int qualification;
}
