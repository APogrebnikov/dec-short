package org.edec.student.journalOrder.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor

public class JournalOrderModel {
    private Date dateSignOrder;

    private String groupname;
    private String orderNumber;
    private String orderType;
    private String semesterStr;
    private String headDescription;
    private String sectionDescription;
    private Date firstDate, secondDate;
    private String summScolarship;
    private Long idOrderType;


}