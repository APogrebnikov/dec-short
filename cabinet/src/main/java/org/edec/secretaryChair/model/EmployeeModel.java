package org.edec.secretaryChair.model;


public class EmployeeModel {
    private Integer leader = 0;
    private Integer pos;

    private Long idEmployee;
    private Long idLED;

    private String role;
    private String fio;

    public EmployeeModel () {
    }

    public Long getIdEmployee () {
        return idEmployee;
    }

    public void setIdEmployee (Long idEmployee) {
        this.idEmployee = idEmployee;
    }

    public Long getIdLED () {
        return idLED;
    }

    public void setIdLED (Long idLED) {
        this.idLED = idLED;
    }

    public String getRole () {
        return role;
    }

    public void setRole (String role) {
        this.role = role;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public Integer getLeader () {
        return leader;
    }

    public void setLeader (Integer leader) {
        this.leader = leader;
    }

    public Integer getPos () {
        return pos;
    }

    public void setPos (Integer pos) {
        this.pos = pos;
    }
}
