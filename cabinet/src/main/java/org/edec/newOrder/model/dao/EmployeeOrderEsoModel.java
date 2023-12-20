package org.edec.newOrder.model.dao;

/**
 * Created by dmmax
 */
public class EmployeeOrderEsoModel {
    private String fio;
    private String shortFio;
    private String post;
    private String subquery;
    private String certnum;

    private Integer actionrule;

    private Integer formofstudy;

    public EmployeeOrderEsoModel () {
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getShortFio() {
        String[] subStr  = fio.split(" ");
        return subStr[1].charAt(0) + ". " + subStr[2].charAt(0) + "." + ' ' + subStr[0];
    }

    public String getPost () {
        return post;
    }

    public void setPost (String post) {
        this.post = post;
    }

    public Integer getActionrule () {
        return actionrule;
    }

    public void setActionrule (Integer actionrule) {
        this.actionrule = actionrule;
    }

    public String getSubquery () {
        return subquery;
    }

    public void setSubquery (String subquery) {
        this.subquery = subquery;
    }

    public Integer getFormofstudy () {
        return formofstudy;
    }

    public void setFormofstudy (Integer formofstudy) {
        this.formofstudy = formofstudy;
    }

    public String getCertnum() {
        return certnum;
    }

    public void setCertnum(String certnum) {
        this.certnum = certnum;
    }
}
