package org.edec.utility.report.model.order;

/**
 * Created by dmmax
 */
public class EmployeeOrderModel {
    /**
     * ФИО согласующего лица
     */
    private String fio;
    /**
     * Должность согласующего лица
     */
    private String role;

    /**
     * Конструктор модели согласующего лица
     *
     * @param fio
     * @param role
     */
    public EmployeeOrderModel (String fio, String role) {
        super();
        this.fio = fio;
        this.role = role;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getRole () {
        return role;
    }

    public void setRole (String role) {
        this.role = role;
    }
}
