package org.edec.reference.model;

import java.util.Date;

public class ReferenceModel {

    private long idRef = -1;
    private long idStudentcard;
    private long refType;
    private long idSemester;

    private String booknumber;
    private String url;

    private Date dateStart;
    private Date dateFinish;
    private Date dateGet;

    public long getIdRef () {
        return idRef;
    }

    public void setIdRef (long idRef) {
        this.idRef = idRef;
    }

    public long getIdStudentcard () {
        return idStudentcard;
    }

    public void setIdStudentcard (long idStudentcard) {
        this.idStudentcard = idStudentcard;
    }

    public long getRefType () {
        return refType;
    }

    public void setRefType (long refType) {
        this.refType = refType;
    }

    public String getBooknumber () {
        return booknumber;
    }

    public void setBooknumber (String booknumber) {
        this.booknumber = booknumber;
    }

    public String getUrl () {
        return url;
    }

    public void setUrl (String url) {
        this.url = url;
    }

    public Date getDateStart () {
        return dateStart;
    }

    public void setDateStart (Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateFinish () {
        return dateFinish;
    }

    public void setDateFinish (Date dateFinish) {
        this.dateFinish = dateFinish;
    }

    public Date getDateGet () {
        return dateGet;
    }

    public void setDateGet (Date dateGet) {
        this.dateGet = dateGet;
    }

    public long getIdSemester () {
        return idSemester;
    }

    public void setIdSemester (long idSemester) {
        this.idSemester = idSemester;
    }
}
