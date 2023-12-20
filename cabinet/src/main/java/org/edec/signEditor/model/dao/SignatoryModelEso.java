package org.edec.signEditor.model.dao;

public class SignatoryModelEso {
    private Long idEmp;
    private String family;
    private String name;
    private String patronymic;
    private Long idLre;
    private String rule;
    private Integer role;
    private Integer pos;
    private Boolean isPrint;
    private String post;
    private String subquery;
    private Boolean sign;
    private Long idRule;
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

    public Integer getPos () {
        return pos;
    }

    public void setPos (Integer pos) {
        this.pos = pos;
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

    public Long getIdEmp () {
        return idEmp;
    }

    public void setIdEmp (Long idEmp) {
        this.idEmp = idEmp;
    }

    public String getFamily () {
        return family;
    }

    public void setFamily (String family) {
        this.family = family;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getPatronymic () {
        return patronymic;
    }

    public void setPatronymic (String patronymic) {
        this.patronymic = patronymic;
    }

    public Boolean getSign () {
        return sign;
    }

    public void setSign (Boolean sign) {
        this.sign = sign;
    }

    public Long getIdRule () {
        return idRule;
    }

    public void setIdRule (Long idRule) {
        this.idRule = idRule;
    }

    public Integer getFos () {
        return fos;
    }

    public void setFos (Integer fos) {
        this.fos = fos;
    }
}
