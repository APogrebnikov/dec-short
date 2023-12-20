package org.edec.scholarship.model;

import lombok.Data;

import java.util.Date;

@Data
public class ScholarshipHistoryDTO {
    private Long idScholarshipHistory;
    private Long idMine;
    private Integer scholarshipType;
    private Date dateFrom;
    private Date dateTo;
    private Date dateCancel;
    private Long idMineCancel;
    private Integer size;
    private String orderNumber;
    private String cancelOrderNumber;
}
