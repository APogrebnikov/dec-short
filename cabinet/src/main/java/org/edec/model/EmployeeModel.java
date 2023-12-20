package org.edec.model;


public class EmployeeModel extends HumanfaceModel {
    private Long idEmp;
    private Long idLED;

    private String roleName;

    public EmployeeModel () {
    }

    public Long getIdEmp () {
        return idEmp;
    }

    public void setIdEmp (Long idEmp) {
        this.idEmp = idEmp;
    }

    public Long getIdLED () {
        return idLED;
    }

    public void setIdLED (Long idLED) {
        this.idLED = idLED;
    }

    public String getRoleName () {
        return roleName;
    }

    public void setRoleName (String roleName) {
        this.roleName = roleName;
    }
}
