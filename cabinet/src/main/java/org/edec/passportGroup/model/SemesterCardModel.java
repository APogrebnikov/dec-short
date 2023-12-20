package org.edec.passportGroup.model;

public class SemesterCardModel extends SemesterModel {
    private Long idLgs, idHf;
    private String groupName;

    public Long getIdLgs () {
        return idLgs;
    }

    public void setIdLgs (Long idLgs) {
        this.idLgs = idLgs;
    }

    public Long getIdHf () {
        return idHf;
    }

    public void setIdHf (Long idHf) {
        this.idHf = idHf;
    }

    public String getGroupName () {
        return groupName;
    }

    public void setGroupName (String groupName) {
        this.groupName = groupName;
    }
}
