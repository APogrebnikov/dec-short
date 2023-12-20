package org.edec.newOrder.model.report;

import lombok.Data;

/**
 * Created by dmmax
 */
@Data
public class OrderReportEmployeeModel {
    /**
     * ФИО согласующего лица
     */
    private String fio;
    /**
     * Должность согласующего лица
     */
    private String role;

    private String certnum;
    private String certfio;
    /**
     * Конструктор модели согласующего лица
     *
     * @param fio
     * @param role
     */
    public OrderReportEmployeeModel (String fio, String role) {
        super();
        this.fio = fio;
        this.role = role;
    }

}
