package org.edec.order.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class StudentToAddModel {
    private String fio;
    private String group;

    private Long id;

    private Date firstDate;
    private Date secondDate;

    private Boolean governmentFinanced;
}
