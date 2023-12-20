package org.edec.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class StudentModel extends HumanfaceModel {
    private Long idStudentCard, idSSS;
    private Integer isGovernmentFinanced;

    private String recordBook;
}
