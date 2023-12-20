package org.edec.commission.report.model.protocol;

import org.edec.commission.report.model.CommissionEmployeeModel;

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

    private List<CommissionEmployeeModel> comission = new ArrayList<>();
    private List<CommissionEmployeeModel> fullComission = new ArrayList<>();

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

    public List<CommissionEmployeeModel> getComission () {
        return comission;
    }

    public void setComission (List<CommissionEmployeeModel> comission) {
        this.comission = comission;
    }

    public List<CommissionEmployeeModel> getFullComission () {
        return fullComission;
    }

    public void setFullComission (List<CommissionEmployeeModel> fullComission) {
        this.fullComission = fullComission;
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
}
