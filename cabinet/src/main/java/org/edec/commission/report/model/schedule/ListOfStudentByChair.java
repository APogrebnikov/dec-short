package org.edec.commission.report.model.schedule;

public class ListOfStudentByChair {
    private String fio;
    private String fulltitle;
    private String groupname;
    private String subjectname;
    private Integer budget;

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getFulltitle () {
        return fulltitle;
    }

    public void setFulltitle (String fulltitle) {
        this.fulltitle = fulltitle;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public String getSubjectname () {
        return subjectname;
    }

    public void setSubjectname (String subjectname) {
        this.subjectname = subjectname;
    }

    public Integer getBudget () {
        return budget;
    }

    public void setBudget (Integer budget) {
        this.budget = budget;
    }
}
