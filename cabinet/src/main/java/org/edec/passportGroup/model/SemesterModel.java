package org.edec.passportGroup.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by antonskripacev on 09.04.17.
 */
public class SemesterModel {
    private Long idSemester;
    private Integer formOfStudy;
    private Integer season;
    private Date dateOfBegin, dateOfEnd;

    public Integer getFormOfStudy () {
        return formOfStudy;
    }

    public void setFormOfStudy (Integer formOfStudy) {
        this.formOfStudy = formOfStudy;
    }

    public Integer getSeason () {
        return season;
    }

    public void setSeason (Integer season) {
        this.season = season;
    }

    public Date getDateOfBegin () {
        return dateOfBegin;
    }

    public void setDateOfBegin (Date dateOfBegin) {
        this.dateOfBegin = dateOfBegin;
    }

    public Date getDateOfEnd () {
        return dateOfEnd;
    }

    public void setDateOfEnd (Date dateOfEnd) {
        this.dateOfEnd = dateOfEnd;
    }

    public Long getIdSemester () {
        return idSemester;
    }

    public void setIdSemester (Long idSemester) {
        this.idSemester = idSemester;
    }

    public String getFullName () {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");

        String schoolYear = simpleDateFormat.format(getDateOfBegin()) + "/" + simpleDateFormat.format(getDateOfEnd());

        String semesterStatusText, formOfStudy;

        if (getSeason() == 0) {
            semesterStatusText = " осенний ";
        } else if (getSeason() == 1) {
            semesterStatusText = " весенний ";
        } else {
            semesterStatusText = "";
        }

        if (getFormOfStudy() == 1) {
            formOfStudy = "(очная)";
        } else if (getFormOfStudy() == 2) {
            formOfStudy = "(заочная)";
        } else {
            formOfStudy = "";
        }

        return schoolYear + semesterStatusText + formOfStudy;
    }
}
