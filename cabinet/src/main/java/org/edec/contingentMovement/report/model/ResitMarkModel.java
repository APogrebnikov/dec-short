package org.edec.contingentMovement.report.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResitMarkModel {
    private String infoTo, infoFrom, foc, rating, focOldGroup;
    private Integer  passRating, examRating, courseProjectRating, courseWorkRating, practicRating, newRating;
    private Long idStudentCard, idDicGroup;
}
