package org.edec.newOrder.model.report;


public class OrderReportStudentModel {
    /**
     * ФИО
     */
    private String fio;
    /**
     * Номер зачетной книжки;
     */
    private String recordbook;
    private String scholarship;
    private String INN;

    public OrderReportStudentModel (String fio, String recordbook) {
        this.fio = fio;
        this.recordbook = recordbook;
    }

    public OrderReportStudentModel () {
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getRecordbook () {
        return recordbook;
    }

    public void setRecordbook (String recordbook) {
        this.recordbook = recordbook;
    }

    public String getScholarship() {
        return scholarship;
    }

    public void setScholarship(String scholarship) {
        this.scholarship = scholarship;
    }

    public String getINN() {
        return INN;
    }

    public void setINN(String INN) {
        this.INN = INN;
    }

}
