package org.edec.commission.report.model.register;


public class HumanfaceModel {
    /**
     * Фамилия И.О. студента
     */
    private String fio;
    /**
     * Название группы
     */
    private String groupname;
    /**
     * Оценка по предмету
     */
    private String rating;

    public HumanfaceModel (String fio, String groupname, String rating) {
        super();
        this.fio = fio;
        this.groupname = groupname;
        this.rating = rating;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public String getRating () {
        return rating;
    }

    public void setRating (String rating) {
        this.rating = rating;
    }
}
