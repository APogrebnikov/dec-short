package org.edec.student.recordBook.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class GroupSemesterEsoModel {

    private Boolean attendance;
    private Boolean eok;
    private Boolean performance;
    private Boolean physcul;

    private Integer maxRedLevel;
    private Integer minGreenLevel;

    private Long idSSS;
    private Long idSem;

    private String groupname;
    private String semesterNumber;
}
