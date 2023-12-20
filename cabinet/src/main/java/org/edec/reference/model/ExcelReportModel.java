package org.edec.reference.model;

import java.util.Date;

public class ExcelReportModel {

    private String regNumber;

    private String fio;
    private String groupname;

    private Date dateStart;
    private Date dateFinish;

    private Date firstDate;
    private Date secondDate;

    private String orderType;

    private String typeOfReference;

    public ExcelReportModel () {
    }
    public String getRegNumber () {
        return regNumber;
    }

    public void setRegNumber (String regNumber) {
        this.regNumber = regNumber;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public Date getDateStart () {
        return dateStart;
    }

    public void setDateStart (Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateFinish () {
        return dateFinish;
    }

    public void setDateFinish (Date dateFinish) {
        this.dateFinish = dateFinish;
    }

    public String getIdReferenceSubtype () {
        return typeOfReference;
    }

    public void setIdReferenceSubtype (String typeOfReference) { this.typeOfReference = typeOfReference; }

    public Date getFirstDate () { return firstDate; }

    public void setFirstDate () {this.firstDate = firstDate;}

    public Date getSecondDate () {return secondDate;}

    public void setSecondDate () {this.secondDate = secondDate;}

    public String getOrderType () { return orderType; }

    public void setOrderType (String orderType) {this.orderType = orderType;}
}
