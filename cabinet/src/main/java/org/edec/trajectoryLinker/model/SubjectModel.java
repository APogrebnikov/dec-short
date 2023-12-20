package org.edec.trajectoryLinker.model;

import lombok.Data;

@Data
public class SubjectModel {
    private Long idSubject;
    private Long idTrajectory;
    private Long idCurSubjectTrajectory;
    private String subjectName;
    private String subjectCurBlock;
    private Integer semesterNumber;
    private Boolean linked = false;
}
