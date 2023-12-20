package org.edec.commission.model;

/**
 * Created by dmmax
 */
public class StudentCountDebtModel {
    private Integer debt;

    private String listSSS;

    private Long idDG;
    private Long idSC;

    private String fio;
    private String groupname;

    public StudentCountDebtModel () {
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public Long getIdDG () {
        return idDG;
    }

    public void setIdDG (Long idDG) {
        this.idDG = idDG;
    }

    public Long getIdSC () {
        return idSC;
    }

    public void setIdSC (Long idSC) {
        this.idSC = idSC;
    }

    public Integer getDebt () {
        return debt;
    }

    public void setDebt (Integer debt) {
        this.debt = debt;
    }

    public String getListSSS () {
        return listSSS;
    }

    public void setListSSS (String listSSS) {
        this.listSSS = listSSS;
    }
}
