package org.edec.order.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class StudentModel {
    private String fio;
    private String recordnumber;
    private String numberPrevOrder;
    private String foundation;
    private String additional;

    private Date firstDate;
    private Date secondDate;
    private Date thirdDate;
    private Date datePrevOrder;

    private long id;

    /**
     * используется при создании переводного
     **/
    private Boolean prolongatedManualy;
    private Date dateProlongation;
    private String groupname;

    // для мультиудаления
    private boolean selected;

    private Boolean isGovernmentFinanced;
}
