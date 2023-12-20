package org.edec.commission.report.model;

/**
 * Created by dmmax
 */
public class EmployeeCommissionModel {
    private Boolean chairman;

    private String employeeRole;
    private String FIO;

    public EmployeeCommissionModel () {
    }

    public Boolean isChairman () {
        return chairman;
    }

    public void setChairman (Boolean chairman) {
        this.chairman = chairman;
    }

    public String getEmployeeRole () {
        return employeeRole;
    }

    public void setEmployeeRole (String employeeRole) {
        this.employeeRole = employeeRole;
    }

    public String getFIO () {
        return FIO;
    }

    public void setFIO (String FIO) {
        this.FIO = FIO;
    }
}
