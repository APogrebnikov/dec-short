package org.edec.utility.report.model.order.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
public class TransferEsoModel {
    private Integer course;

    private String description;
    private String descriptionOneDates;
    private String descriptionTwoDates;
    private String fio;
    private String foundation;
    private String groupname;
    private String recordbook;
    private String additional;
    private String prevOrderNum;
    private String prevOrderDate;

    private Date firstDateStudent;
}
