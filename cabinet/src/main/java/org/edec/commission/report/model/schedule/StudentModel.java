package org.edec.commission.report.model.schedule;

public class StudentModel {
    private String fio;
    private String groupname;
    private String recordbook;
    private Integer budget;

    public StudentModel () {
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getRecordbook () {
        return recordbook;
    }

    public void setRecordbook (String recordbook) {
        this.recordbook = recordbook;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public Integer getBudget () { return budget;}

    public void setBudget (Integer budget) {this.budget = budget;}

}
