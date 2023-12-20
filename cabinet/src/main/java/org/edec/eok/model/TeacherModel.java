package org.edec.eok.model;


public class TeacherModel {
    private String fio;

    public TeacherModel () {
    }

    public TeacherModel (String fio) {
        this.fio = fio;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getShortFio () {
        String[] fio = getFio().split(" ");
        return fio[0] + " " + fio[1].substring(0, 1) + ". " + fio[2].substring(0, 1) + ".";
    }
}
