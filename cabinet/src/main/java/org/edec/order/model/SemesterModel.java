package org.edec.order.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class SemesterModel {
    private Long id;
    private Integer season;

    private Date dateOfBeginYear;
    private Date dateOfEndYear;

    private Boolean currentSemester;
}
