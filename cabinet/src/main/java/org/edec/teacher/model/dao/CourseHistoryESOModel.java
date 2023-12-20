package org.edec.teacher.model.dao;

public class CourseHistoryESOModel {

    private Long idEmp;
    private Long idLGSS;
    private String subjectname;
    private Long idEsoCourse2;
    private Long idSem;
    private String fullname;
    private String groupName;

    public String getSubjectname () {
        return subjectname;
    }

    public void setSubjectname (String subjectname) {
        this.subjectname = subjectname;
    }

    public Long getIdEsoCourse2 () {
        return idEsoCourse2;
    }

    public void setIdEsoCourse2 (Long idEsoCourse2) {
        this.idEsoCourse2 = idEsoCourse2;
    }

    public Long getIdSem () {
        return idSem;
    }

    public void setIdSem (Long idSem) {
        this.idSem = idSem;
    }

    public String getFullname () {
        return fullname;
    }

    public void setFullname (String fullname) {
        this.fullname = fullname;
    }

    public Long getIdEmp () {
        return idEmp;
    }

    public void setIdEmp (Long idEmp) {
        this.idEmp = idEmp;
    }

    public Long getIdLGSS () {
        return idLGSS;
    }

    public void setIdLGSS (Long idLGSS) {
        this.idLGSS = idLGSS;
    }

    public String getGroupName () {
        return groupName;
    }

    public void setGroupName (String groupName) {
        this.groupName = groupName;
    }
}
