package org.edec.newMine.orders.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class OrderStudentsModel {
    private String fio, groupname, recordbook, summ;
    private int course;
    private Date dateFrom, dateTo;
    private Long idStudent;
}
