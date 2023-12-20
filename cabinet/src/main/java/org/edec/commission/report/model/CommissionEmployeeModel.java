package org.edec.commission.report.model;

/**
 * Created by dmmax
 */
public class CommissionEmployeeModel {
    private Integer leader;

    private String fio;
    private String shortfio;

    public CommissionEmployeeModel () {
    }

    public String getShortfio () {
        return fio.split(" ")[0] + " " + fio.split(" ")[1].substring(0, 1) + ". " + fio.split(" ")[2].substring(0, 1) + ".";
    }

    public void setShortfio (String shortfio) {
        this.shortfio = shortfio;
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
}
