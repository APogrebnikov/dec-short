package org.edec.student.recordBook.model;

import org.edec.efficiency.model.ConfigurationEfficiency;
import org.edec.utility.component.model.RatingModel;

import java.util.Date;


public class GradeBookModel extends RatingModel {
    private Date consultationDate;
    private Date dateOfPass;
    private Date cpDate;
    private Date statusBeginDate;
    private Date statusFinishDate;

    private Double esoGradeCurrent;
    private Double esoGradeMax;
    private Double hoursCount;

    private Long idEsoCourse;
    private Long lessonCount;
    private Long visitCount;

    private String status;
    private String style;
    private String teacher;

    private ConfigurationEfficiency configuration;

    public GradeBookModel () {
    }

    public Double getEsoGradeCurrent () {
        return esoGradeCurrent;
    }

    public void setEsoGradeCurrent (Double esoGradeCurrent) {
        this.esoGradeCurrent = esoGradeCurrent;
    }

    public Double getEsoGradeMax () {
        return esoGradeMax;
    }

    public void setEsoGradeMax (Double esoGradeMax) {
        this.esoGradeMax = esoGradeMax;
    }

    public Double getHoursCount () {
        return hoursCount;
    }

    public void setHoursCount (Double hoursCount) {
        this.hoursCount = hoursCount;
    }

    public Long getIdEsoCourse () {
        return idEsoCourse;
    }

    public void setIdEsoCourse (Long idEsoCourse) {
        this.idEsoCourse = idEsoCourse;
    }

    public String getTeacher () {
        return teacher;
    }

    public void setTeacher (String teacher) {
        this.teacher = teacher;
    }

    public String getStyle (boolean choosen) {
        String value = "color: #000;";
        if (getFoc().equals("Экзамен")) {
            value += choosen ? "background: #FF4242; border: 1px solid #FF4242;" : "background: #FF7373; border: 1px solid #FF7373;";
        } else if (getFoc().equals("Зачет")) {
            value += choosen ? "background: #30d5c8; border: 1px solid #30d5c8;" : "background: #4DE2F7; border: 1px solid #4DE2F7;";
        } else if (getFoc().equals("КП")) {
            value += choosen ? "background: #68f768; border: 1px solid #68f768;" : "background: #95FF82; border: 1px solid #95FF82;";
        } else if (getFoc().equals("КР")) {
            value += choosen ? "background: #ffff33; border: 1px solid #ffff33;" : "background: #FFFE7E; border: 1px solid #FFFE7E;";
        } else if (getFoc().equals("Практика")) {
            value += choosen ? "background: #bababa; border: 1px solid #bababa;" : "background: #EDEDED; border: 1px solid #EDEDED;";
        }
        return value;
    }

    public Date getConsultationDate () {
        return consultationDate;
    }

    public void setConsultationDate (Date consultationDate) {
        this.consultationDate = consultationDate;
    }

    public Date getDateOfPass () {
        return dateOfPass;
    }

    public void setDateOfPass (Date dateOfPass) {
        this.dateOfPass = dateOfPass;
    }

    public Date getCpDate () {
        return cpDate;
    }

    public void setCpDate (Date cpDate) {
        this.cpDate = cpDate;
    }

    public String getStyle () {
        return style;
    }

    public void setStyle (String style) {
        this.style = style;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    public Long getLessonCount () {
        return lessonCount;
    }

    public void setLessonCount (Long lessonCount) {
        this.lessonCount = lessonCount;
    }

    public Long getVisitCount () {
        return visitCount;
    }

    public void setVisitCount (Long visitCount) {
        this.visitCount = visitCount;
    }

    public Date getStatusBeginDate () {
        return statusBeginDate;
    }

    public void setStatusBeginDate (Date statusBeginDate) {
        this.statusBeginDate = statusBeginDate;
    }

    public Date getStatusFinishDate () {
        return statusFinishDate;
    }

    public void setStatusFinishDate (Date statusFinishDate) {
        this.statusFinishDate = statusFinishDate;
    }

    public ConfigurationEfficiency getConfiguration () {
        return configuration;
    }

    public void setConfiguration (ConfigurationEfficiency configuration) {
        this.configuration = configuration;
    }
}
