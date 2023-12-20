package org.edec.newOrder.model;


public class EmployeeOrderModel {
    private Boolean sign;
    private Integer actionrule;
    private Long idHum;
    private String email;
    private String fio;
    /**
     * Доп запрос для динамических подписывающих лиц (иностранный отдел)
     **/
    private String subquery;

    public EmployeeOrderModel () {
    }

    public Boolean getSign () {
        return sign;
    }

    public void setSign (Boolean sign) {
        this.sign = sign;
    }

    public Long getIdHum () {
        return idHum;
    }

    public void setIdHum (Long idHum) {
        this.idHum = idHum;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public Integer getActionrule () {
        return actionrule;
    }

    public void setActionrule (Integer actionrule) {
        this.actionrule = actionrule;
    }

    public String getEmail () {
        return email;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public String getSubquery () {
        return subquery;
    }

    public void setSubquery (String subquery) {
        this.subquery = subquery;
    }
}
