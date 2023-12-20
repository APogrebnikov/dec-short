package org.edec.utility.component.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class StudentModel {
    private Date birthday;

    private Long idHum;
    private Long idStudCard;
    private Long idStudCardMine;
    private Long idSSS;

    /**
     * Условия обучения
     */
    private Integer condOfEducation;
    private Integer semester;
    private Integer sex;
    private Integer status;

    private String family;
    private String groupname;
    private String name;
    private String patronymic;
    private String recordbook;

    private StudentModel otherStudentModel;
    private List<SubjectModel> subjects = new ArrayList<>();

    public StudentModel () {
    }

    public Date getBirthday () {
        return birthday;
    }

    public void setBirthday (Date birthday) {
        this.birthday = birthday;
    }

    public String getFio () {
        return family + " " + name + " " + patronymic;
    }

    public String getShortFio () {
        return family + " " + name.substring(0, 1) + ". " + (patronymic != null ? patronymic.substring(0, 1) + "." : "");
    }

    public Integer getCondOfEducation () {
        return condOfEducation;
    }

    public void setCondOfEducation (Integer condOfEducation) {
        this.condOfEducation = condOfEducation;
    }

    public Long getIdHum () {
        return idHum;
    }

    public void setIdHum (Long idHum) {
        this.idHum = idHum;
    }

    public Long getIdStudCard () {
        return idStudCard;
    }

    public void setIdStudCard (Long idStudCard) {
        this.idStudCard = idStudCard;
    }

    public Long getIdStudCardMine () {
        return idStudCardMine;
    }

    public void setIdStudCardMine (Long idStudCardMine) {
        this.idStudCardMine = idStudCardMine;
    }

    public Long getIdSSS () {
        return idSSS;
    }

    public void setIdSSS (Long idSSS) {
        this.idSSS = idSSS;
    }

    public String getFamily () {
        return family;
    }

    public void setFamily (String family) {
        this.family = family;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
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

    public Integer getStatus () {
        return status;
    }

    public void setStatus (Integer status) {
        this.status = status;
    }

    public StudentModel getOtherStudentModel () {
        return otherStudentModel;
    }

    public void setOtherStudentModel (StudentModel otherStudentModel) {
        this.otherStudentModel = otherStudentModel;
    }

    public List<SubjectModel> getSubjects () {
        return subjects;
    }

    public void setSubjects (List<SubjectModel> subjects) {
        this.subjects = subjects;
    }

    public Integer getSemester () {
        return semester;
    }

    public void setSemester (Integer semester) {
        this.semester = semester;
    }

    public Integer getSex () {
        return sex;
    }

    public void setSex (Integer sex) {
        this.sex = sex;
    }
}
