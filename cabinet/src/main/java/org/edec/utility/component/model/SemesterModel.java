package org.edec.utility.component.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.utility.converter.DateConverter;

import java.util.Date;


@Getter @Setter @NoArgsConstructor
public class SemesterModel {
    private boolean curSem;

    private Integer season;
    private Integer formofstudy;

    private Long idSem;

    private Date dateOfBegin;
    private Date dateOfEnd;

    @Override
    public String toString() {
        return DateConverter.convertDateToYearString(dateOfBegin) + "-" + DateConverter.convertDateToYearString(dateOfEnd) +
                " (" + (season == 0 ? "осень" : "весна") + ")";
    }
}
