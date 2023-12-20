package org.edec.synchroMine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor

public class SchoolYearModel {
    private Long idSY, idOtherDB;
    private Date beginYear, endYear;
    private Boolean isCurrentYear;
}
