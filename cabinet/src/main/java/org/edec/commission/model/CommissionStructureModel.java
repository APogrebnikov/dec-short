package org.edec.commission.model;

/**
 * Created by dmmax
 */
public class CommissionStructureModel {
    private boolean leader;

    private String fio;
    private String rolename;

    public CommissionStructureModel () {
    }

    public boolean isLeader () {
        return leader;
    }

    public void setLeader (boolean leader) {
        this.leader = leader;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getRolename () {
        return rolename;
    }

    public void setRolename (String rolename) {
        this.rolename = rolename;
    }
}
