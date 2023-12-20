package org.edec.signEditor.model.dao;

public class EmployeeModelEso {
    private Long idEmp;
    private String family;
    private String name;
    private String patronymic;

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
}
