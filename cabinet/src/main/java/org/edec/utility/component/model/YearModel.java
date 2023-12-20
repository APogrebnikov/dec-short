package org.edec.utility.component.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @author Alex
 */
@Getter
@Setter
@NoArgsConstructor
public class YearModel {
    private Long idSchoolYear;
    private Date dateOfBegin;
    private Date dateOfEnd;
    private Long otherDbId;
}
