package org.edec.successful.model;

import java.util.ArrayList;
import java.util.List;


public class DepartmentModel {
    private List<GroupModel> groups = new ArrayList<>();
    private String fulltitle;
    private Long idDepartment;
    private Integer count;

    public List<GroupModel> getGroups () {
        return groups;
    }

    public void setGroups (List<GroupModel> groups) {
        this.groups = groups;
    }

    public String getFulltitle () {
        return fulltitle;
    }

    public void setFulltitle (String fulltitle) {
        this.fulltitle = fulltitle;
    }

    public Long getIdDepartment () {
        return idDepartment;
    }

    public void setIdDepartment (Long idDepartment) {
        this.idDepartment = idDepartment;
    }

    public Integer getCount () {
        return count;
    }

    public void setCount (Integer count) {
        this.count = count;
    }
}
