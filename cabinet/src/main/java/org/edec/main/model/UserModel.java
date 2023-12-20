package org.edec.main.model;

import org.edec.utility.constants.LevelConst;

import java.util.ArrayList;
import java.util.List;


public class UserModel {
    private boolean groupLeader;
    private boolean parent;
    private boolean student;
    private boolean teacher;

    private Integer formofstudystudent;

    private Long idHum;
    private Long idParent;

    private String fio;
    private String groupname;
    private String startPage;
    private LevelConst qualification;

    private List<RoleModel> roles = new ArrayList<>();

    public UserModel () {
    }

    public boolean isGroupLeader () {
        return groupLeader;
    }

    public void setGroupLeader (boolean groupLeader) {
        this.groupLeader = groupLeader;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public Integer getFormofstudystudent () {
        return formofstudystudent;
    }

    public void setFormofstudystudent (Integer formofstudystudent) {
        this.formofstudystudent = formofstudystudent;
    }

    public Long getIdHum () {
        return idHum;
    }

    public void setIdHum (Long idHum) {
        this.idHum = idHum;
    }

    public Long getIdParent () {
        return idParent;
    }

    public void setIdParent (Long idParent) {
        this.idParent = idParent;
    }

    public List<RoleModel> getRoles () {
        return roles;
    }

    public void setRoles (List<RoleModel> roles) {
        this.roles = roles;
    }

    public boolean isParent () {
        return parent;
    }

    public void setParent (boolean parent) {
        this.parent = parent;
    }

    public boolean isStudent () {
        return student;
    }

    public void setStudent (boolean student) {
        this.student = student;
    }

    public String getStartPage () {
        return startPage;
    }

    public void setStartPage (String startPage) {
        this.startPage = startPage;
    }

    public String getShortFIO () {
        String[] fio = getFio().split(" ");
        return fio[0] + " " + fio[1].substring(0, 1) + "." + (fio.length == 3 ? " " + fio[2].substring(0, 1) + "." : "") ;
    }

    public boolean isTeacher () {
        return teacher;
    }

    public void setTeacher (boolean teacher) {
        this.teacher = teacher;
    }

    public LevelConst getQualification () {
        return qualification;
    }

    public void setQualification (LevelConst qualification) {
        this.qualification = qualification;
    }
}
