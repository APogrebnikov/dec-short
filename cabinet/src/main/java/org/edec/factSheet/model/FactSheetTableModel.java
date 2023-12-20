package org.edec.factSheet.model;

import org.edec.model.HumanfaceModel;

import java.util.Date;

public class FactSheetTableModel extends HumanfaceModel {

    private String groupname;
    private String title;
    private String registerNumber;
    private int idFactSheetStatus;
    private Long idFactSheet;
    private Long idHumanface;
    private Boolean officialSeal;
    private Boolean createdByStudent;
    private Boolean isReceipt;
    private Boolean getNotification;
    private Date dateCreate;
    private Date dateCompletion;
    private Date dateReceipt;
    private Boolean deleted = false;

    public Boolean getDeleted () {
        return deleted;
    }

    public void setDeleted (Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getCreatedByStudent () {
        return createdByStudent;
    }

    public void setCreatedByStudent (Boolean createdByStudent) {
        this.createdByStudent = createdByStudent;
    }

    @Override
    public boolean equals (Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FactSheetTableModel)) {
            return false;
        }
        FactSheetTableModel other = (FactSheetTableModel) o;
        return this.getIdFactSheet().equals(other.getIdFactSheet());
    }

    public Date getDateReceipt () {
        return dateReceipt;
    }

    public void setDateReceipt (Date dateReceipt) {
        this.dateReceipt = dateReceipt;
    }

    public Boolean getReceipt () {
        return isReceipt;
    }

    public void setReceipt (Boolean receipt) {
        isReceipt = receipt;
    }

    public Date getDateCreate () {
        return dateCreate;
    }

    public void setDateCreate (Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    public Date getDateCompletion () {
        return dateCompletion;
    }

    public void setDateCompletion (Date dateCompletion) {
        this.dateCompletion = dateCompletion;
    }

    public Boolean getOfficialSeal () {
        return officialSeal;
    }

    public void setOfficialSeal (Boolean officialSeal) {
        this.officialSeal = officialSeal;
    }

    public Long getIdHumanface () {
        return idHumanface;
    }

    public void setIdHumanface (Long idHumanface) {
        this.idHumanface = idHumanface;
    }

    public Long getIdFactSheet () {
        return idFactSheet;
    }

    public void setIdFactSheet (Long idFactSheet) {
        this.idFactSheet = idFactSheet;
    }

    public int getIdFactSheetStatus () {
        return idFactSheetStatus;
    }

    public void setIdFactSheetStatus (int idFactSheetStatus) {
        this.idFactSheetStatus = idFactSheetStatus;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public String getRegisterNumber () {
        return registerNumber;
    }

    public void setRegisterNumber (String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public Boolean getGetNotification () {
        return getNotification;
    }

    public void setGetNotification (Boolean getNotification) {
        this.getNotification = getNotification;
    }
}
