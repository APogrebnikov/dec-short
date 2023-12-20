package org.edec.newOrder.model.dao;

import java.util.Date;

/**
 * Created by apple on 29.11.2017.
 * Модель для переноса приказа в таблицу order_action
 */
public class OrderImportModel {
    private Long idLOSS, idStudentcard, idDicGroupFromOrder, idDicGroup, idInstitute, idSemester;
    private Date dateAction, dateStart, dateFinish;
    private String orderNumber;

    public Long getIdLOSS () {
        return idLOSS;
    }

    public void setIdLOSS (Long idLOSS) {
        this.idLOSS = idLOSS;
    }

    public Long getIdStudentcard () {
        return idStudentcard;
    }

    public void setIdStudentcard (Long idStudentcard) {
        this.idStudentcard = idStudentcard;
    }

    public Long getIdDicGroupFromOrder () {
        return idDicGroupFromOrder;
    }

    public void setIdDicGroupFromOrder (Long idDicGroupFromOrder) {
        this.idDicGroupFromOrder = idDicGroupFromOrder;
    }

    public Long getIdDicGroup () {
        return idDicGroup;
    }

    public void setIdDicGroup (Long idDicGroup) {
        this.idDicGroup = idDicGroup;
    }

    public Long getIdInstitute () {
        return idInstitute;
    }

    public void setIdInstitute (Long idInstitute) {
        this.idInstitute = idInstitute;
    }

    public Long getIdSemester () {
        return idSemester;
    }

    public void setIdSemester (Long idSemester) {
        this.idSemester = idSemester;
    }

    public Date getDateAction () {
        return dateAction;
    }

    public void setDateAction (Date dateAction) {
        this.dateAction = dateAction;
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

    public String getOrderNumber () {
        return orderNumber;
    }

    public void setOrderNumber (String orderNumber) {
        this.orderNumber = orderNumber;
    }
}
