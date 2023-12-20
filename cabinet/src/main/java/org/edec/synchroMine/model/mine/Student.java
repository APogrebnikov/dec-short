package org.edec.synchroMine.model.mine;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "[Все_Студенты]")
public class Student {
    @Id
    @Column(name = "Код")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "АкадемСтипендияС")
    private Date dateAcademicScholarshipFrom;
    @Column(name = "АкадемСтипендияПо")
    private Date dateAcademicScholarshipTo;

    @Column(name = "АкадемСтипендия")
    private Integer academicScholarship;

    @Column(name = "Код_Группы")
    private Long idGroup;
    @Column(name = "КодФакультета")
    private Long idInst;
    @Column(name = "Статус")
    private Long idStatus;

    @Column(name = "Фамилия")
    private String family;
    @Column(name = "Имя")
    private String name;
    @Column(name = "Отчество")
    private String patronymic;
    @Column(name = "Номер_Зачетной_Книжки")
    private String recordbook;
    @Column(name = "Основания")
    private String reasonStudy;

    public Student () {
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public Date getDateAcademicScholarshipFrom () {
        return dateAcademicScholarshipFrom;
    }

    public void setDateAcademicScholarshipFrom (Date dateAcademicScholarshipFrom) {
        this.dateAcademicScholarshipFrom = dateAcademicScholarshipFrom;
    }

    public Date getDateAcademicScholarshipTo () {
        return dateAcademicScholarshipTo;
    }

    public void setDateAcademicScholarshipTo (Date dateAcademicScholarshipTo) {
        this.dateAcademicScholarshipTo = dateAcademicScholarshipTo;
    }

    public Integer getAcademicScholarship () {
        return academicScholarship;
    }

    public void setAcademicScholarship (Integer academicScholarship) {
        this.academicScholarship = academicScholarship;
    }

    public Long getIdStatus () {
        return idStatus;
    }

    public void setIdStatus (Long idStatus) {
        this.idStatus = idStatus;
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

    public String getRecordbook () {
        return recordbook;
    }

    public void setRecordbook (String recordbook) {
        this.recordbook = recordbook;
    }

    public Long getIdGroup () {
        return idGroup;
    }

    public void setIdGroup (Long idGroup) {
        this.idGroup = idGroup;
    }

    public Long getIdInst () {
        return idInst;
    }

    public void setIdInst (Long idInst) {
        this.idInst = idInst;
    }

    public String getReasonStudy () {
        return reasonStudy;
    }

    public void setReasonStudy (String reasonStudy) {
        this.reasonStudy = reasonStudy;
    }
}
