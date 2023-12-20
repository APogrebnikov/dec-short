package org.edec.scholarship.model;

import lombok.Data;
import org.edec.utility.constants.OrderActionTypeConst;

import java.util.Date;

@Data
public class ScholarshipHistoryModel {
    private OrderActionTypeConst name;
    private Date dateFrom;
    private Date dateTo;
    private String orderNumber;
    private Date dateCancel;
    private String cancelOrderNumber;
    private Integer size;

    public ScholarshipHistoryModel(ScholarshipHistoryDTO scholarshipHistoryDTO){
        this.name = OrderActionTypeConst.getByType(scholarshipHistoryDTO.getScholarshipType());
        this.dateFrom = scholarshipHistoryDTO.getDateFrom();
        this.dateTo = scholarshipHistoryDTO.getDateTo();
        this.orderNumber = scholarshipHistoryDTO.getOrderNumber();
        this.dateCancel = scholarshipHistoryDTO.getDateCancel();
        this.cancelOrderNumber = scholarshipHistoryDTO.getCancelOrderNumber();
        this.size = scholarshipHistoryDTO.getSize();
    }
}
