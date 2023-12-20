package org.edec.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.utility.converter.DateConverter;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
public class SemesterModel {
    private boolean curSem;

    private Integer firstWeek;
    private Integer season;

    private Long idSem;
    private Long idSchoolYear;

    private Date dateOfBegin;
    private Date dateOfEnd;

    @Override
    public String toString () {
        return DateConverter.convertDateToYearString(getDateOfBegin()) + "-" + DateConverter.convertDateToYearString(getDateOfEnd()) +
               " (" + (season == 0 ? "осень" : "весна") + ")";
    }
}
