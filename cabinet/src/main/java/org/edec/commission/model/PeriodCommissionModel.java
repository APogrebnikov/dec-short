package org.edec.commission.model;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PeriodCommissionModel {
    private Date dateOfBegin;
    private Date dateOfEnd;
    private boolean isGraduate = false;
    private Long idSem;

    public PeriodCommissionModel () {
    }

    public PeriodCommissionModel (Date dateOfBegin, Date dateOfEnd) {
        this.dateOfBegin = dateOfBegin;
        this.dateOfEnd = dateOfEnd;
    }

}
