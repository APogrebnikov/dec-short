package org.edec.newOrder.model.editOrder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by apple on 29.11.16.
 */
public class SectionModel {
    private String foundationLos;
    private String foundation;
    private String name;

    private Date firstDate;
    private Date secondDate;
    private Date thirdDate;

    private long id;
    private long idOS;

    private List<GroupModel> groups = new ArrayList<>();
    private List<StudentModel> studentModels = new ArrayList<>();

    public long getId () {
        return id;
    }

    public void setId (long id) {
        this.id = id;
    }

    public String getFoundation () {
        return foundation;
    }

    public void setFoundation (String foundation) {
        this.foundation = foundation;
    }

    public String getFoundationLos () {
        return foundationLos;
    }

    public void setFoundationLos (String foundationLos) {
        this.foundationLos = foundationLos;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public Date getFirstDate () {
        return firstDate;
    }

    public void setFirstDate (Date firstDate) {
        this.firstDate = firstDate;
    }

    public Date getSecondDate () {
        return secondDate;
    }

    public void setSecondDate (Date secondDate) {
        this.secondDate = secondDate;
    }

    public Date getThirdDate () {
        return thirdDate;
    }

    public void setThirdDate (Date thirdDate) {
        this.thirdDate = thirdDate;
    }

    public List<GroupModel> getGroups () {
        return groups;
    }

    public void setGroups (List<GroupModel> groups) {
        this.groups = groups;
    }

    public List<StudentModel> getStudentModels () {
        return studentModels;
    }

    public void setStudentModels (List<StudentModel> studentModels) {
        this.studentModels = studentModels;
    }

    public long getIdOS () {
        return idOS;
    }

    public void setIdOS (long idOS) {
        this.idOS = idOS;
    }
}
