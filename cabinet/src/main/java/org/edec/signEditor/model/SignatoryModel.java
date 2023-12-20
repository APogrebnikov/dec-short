package org.edec.signEditor.model;

public class SignatoryModel {
    private Long idLre;
    private String rule;
    private Integer role;
    private Integer position;
    private Boolean isPrint;
    private String post;
    private EmployeeModel employee;
    private String subquery;
    private Long idRule;
    private Boolean sign;
    private Integer fos;

    public String getRule () {
        return rule;
    }

    public void setRule (String rule) {
        this.rule = rule;
    }

    public Integer getRole () {
        return role;
    }

    public void setRole (Integer role) {
        this.role = role;
    }

    public Integer getPosition () {
        return position;
    }

    public void setPosition (Integer position) {
        this.position = position;
    }

    public Boolean getPrint () {
        return isPrint;
    }

    public void setPrint (Boolean print) {
        isPrint = print;
    }

    public String getPost () {
        return post;
    }

    public void setPost (String post) {
        this.post = post;
    }

    public String getSubquery () {
        return subquery;
    }

    public void setSubquery (String subquery) {
        this.subquery = subquery;
    }

    public Long getIdLre () {
        return idLre;
    }

    public void setIdLre (Long idLre) {
        this.idLre = idLre;
    }

    public EmployeeModel getEmployee () {
        return employee;
    }

    public void setEmployee (EmployeeModel employee) {
        this.employee = employee;
    }

    public Long getIdRule () {
        return idRule;
    }

    public void setIdRule (Long idRule) {
        this.idRule = idRule;
    }

    public Boolean getSign () {
        return sign;
    }

    public void setSign (Boolean sign) {
        this.sign = sign;
    }

    public Integer getFos () {
        return fos;
    }

    public void setFos (Integer fos) {
        this.fos = fos;
    }
}
