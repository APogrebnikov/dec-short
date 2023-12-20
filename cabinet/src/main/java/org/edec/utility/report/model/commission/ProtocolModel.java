package org.edec.utility.report.model.commission;

import org.edec.teacher.model.commission.EmployeeModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ProtocolModel {
    private Date commissionDate;
    /**
     * Номер протокола
     */
    private String numberOfProtocol;
    /**
     * Форма контроля
     */
    private String formOfControl;
    /**
     * Предмет
     */
    private String subject;
    /**
     * Глава комиссии
     */
    private String chairman;

    /**
     * Название группы
     */
    private String groupname;
    /**
     * ФИО студента
     */
    private String fio;
    private String ratingStr;

    private String protocolNumber;

    private String semesterStr;
    private String hoursCountStr;

    private List<EmployeeModel> commission = new ArrayList<>();
    private List<EmployeeModel> fullCommission = new ArrayList<>();

    public ProtocolModel () { }

    public String getNumberOfProtocol () {
        return numberOfProtocol;
    }

    public void setNumberOfProtocol (String numberOfProtocol) {
        this.numberOfProtocol = numberOfProtocol;
    }

    public String getFormOfControl () {
        return formOfControl;
    }

    public void setFormOfControl (String formOfControl) {
        this.formOfControl = formOfControl;
    }

    public String getSubject () {
        return subject;
    }

    public void setSubject (String subject) {
        this.subject = subject;
    }

    public String getChairman () {
        return chairman;
    }

    public void setChairman (String chairman) {
        this.chairman = chairman;
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

    public List<EmployeeModel> getCommission () {
        return commission;
    }

    public void setCommission (List<EmployeeModel> commission) {
        this.commission = commission;
    }

    public List<EmployeeModel> getFullCommission () {
        return fullCommission;
    }

    public void setFullCommission (List<EmployeeModel> fullCommission) {
        this.fullCommission = fullCommission;
    }

    public String getRatingStr () {
        return ratingStr;
    }

    public void setRatingStr (String ratingStr) {
        this.ratingStr = ratingStr;
    }

    public Date getCommissionDate () {
        return commissionDate;
    }

    public void setCommissionDate (Date commissionDate) {
        this.commissionDate = commissionDate;
    }

    public String getProtocolNumber() {
        return protocolNumber;
    }

    public void setProtocolNumber(String protocolNumber) {
        this.protocolNumber = protocolNumber;
    }

    public String getSemesterStr() {
        return semesterStr;
    }

    public void setSemesterStr(String semesterStr) {
        this.semesterStr = semesterStr;
    }

    public String getHoursCountStr() {
        return hoursCountStr;
    }

    public void setHoursCountStr(String hoursCountStr) {
        this.hoursCountStr = hoursCountStr;
    }
}
