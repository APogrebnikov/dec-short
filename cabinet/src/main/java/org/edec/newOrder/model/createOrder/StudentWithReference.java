package org.edec.newOrder.model.createOrder;


public class StudentWithReference {
    private Long idStudentcard;
    private Long idReference;

    private String family;
    private String name;
    private String patronymic;
    private String url;

    public Long getIdStudentcard () {
        return idStudentcard;
    }

    public void setIdStudentcard (Long idStudentcard) {
        this.idStudentcard = idStudentcard;
    }

    public Long getIdReference () {
        return idReference;
    }

    public void setIdReference (Long idReference) {
        this.idReference = idReference;
    }

    public String getFamily () {
        return family;
    }

    public void setFamily (String family) {
        this.family = family;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getPatronymic () {
        return patronymic;
    }

    public void setPatronymic (String patronymic) {
        this.patronymic = patronymic;
    }

    public String getUrl () {
        return url;
    }

    public void setUrl (String url) {
        this.url = url;
    }
}
