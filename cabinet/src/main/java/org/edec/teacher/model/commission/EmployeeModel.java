package org.edec.teacher.model.commission;


public class EmployeeModel {
    private Boolean chairman;

    private String fio;
    private String shortfio;

    public EmployeeModel () {
    }

    public String getShortfio () {
        return fio.split(" ")[0] + " " + fio.split(" ")[1].substring(0, 1) + ". " + fio.split(" ")[2].substring(0, 1) + ".";
    }

    public Boolean getChairman () {
        return chairman;
    }

    public void setChairman (Boolean chairman) {
        this.chairman = chairman;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }
}
