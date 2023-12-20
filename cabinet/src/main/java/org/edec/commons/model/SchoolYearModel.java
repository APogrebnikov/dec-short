package org.edec.commons.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.utility.converter.DateConverter;

import java.util.Date;

/**
 * @author Max Dimukhametov
 */
@Getter
@Setter
public class SchoolYearModel {
    private Date dateOfBegin;
    private Date dateOfEnd;

    private Long idSchoolYear;
    private Long otherdbid;

    @Override
    public String toString () {
        return DateConverter.convert2dateToString(dateOfBegin, dateOfEnd);
    }
}
