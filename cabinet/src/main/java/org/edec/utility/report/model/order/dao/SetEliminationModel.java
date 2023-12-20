package org.edec.utility.report.model.order.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SetEliminationModel {
    private Integer course;

    private String description;
    private String fio;
    private String foundation;
    private String groupname;
    private String recordbook;

    private Date beginYear, endYear, firstDateStudent;

    private Boolean governmentFinanced;
}
