package org.edec.passportGroup.model.eso;

/**
 * Created by ilyabaikalow on 20.11.17.
 */
public class TeacherModelESO {
    private Long idLesg;
    private Long idTeacher;
    private String fullName;
    private String instTitle;
    private String depTitle;

    public Long getIdLesg () {
        return idLesg;
    }

    public void setIdLesg (Long idLesg) {
        this.idLesg = idLesg;
    }

    public Long getIdTeacher () {
        return idTeacher;
    }

    public void setIdTeacher (Long idTeacher) {
        this.idTeacher = idTeacher;
    }

    public String getFullName () {
        return fullName;
    }

    public void setFullName (String fullName) {
        this.fullName = fullName;
    }

    public String getInstTitle () {
        return instTitle;
    }

    public void setInstTitle (String instTitle) {
        this.instTitle = instTitle;
    }

    public String getDepTitle () {
        return depTitle;
    }

    public void setDepTitle (String depTitle) {
        this.depTitle = depTitle;
    }
}
