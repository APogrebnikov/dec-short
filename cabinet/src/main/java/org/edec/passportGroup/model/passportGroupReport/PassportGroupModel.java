package org.edec.passportGroup.model.passportGroupReport;

/**
 * Created by dmmax
 */
public class PassportGroupModel
{
    private String fio;
    private String government;
    private String prolongation;
    private String subjectname;
    private Double hoursCount;

    private Long id_sss;
    private Integer is_active = 1;
    /**
     * Флаги обучения
     */
    private Integer is_exam, is_pass, is_courseproject, is_coursework, is_practic, type;

    private Integer examrating, passrating, courseprojectrating, courseworkrating, practicrating;

    private Integer esoexamrating, esopassrating, esocourseprojectrating, esocourseworkrating;

    public Double getHoursCount(){
        return  hoursCount;
    }

    public void setHoursCount(Double hoursCount) {
        this.hoursCount = hoursCount;
    }
    public Long getId_sss()
    {
        return id_sss;
    }

    public void setId_sss(Long id_sss)
    {
        this.id_sss = id_sss;
    }

    public String getFio()
    {
        return fio;
    }

    public void setFio(String fio)
    {
        this.fio = fio;
    }

    public String getGovernment()
    {
        return government;
    }

    public void setGovernment(String government)
    {
        this.government = government;
    }

    public String getProlongation()
    {
        return prolongation;
    }

    public void setProlongation(String prolongation)
    {
        this.prolongation = prolongation;
    }

    public Integer getIs_exam()
    {
        return is_exam;
    }

    public void setIs_exam(Integer is_exam)
    {
        this.is_exam = is_exam;
    }

    public Integer getIs_pass()
    {
        return is_pass;
    }

    public void setIs_pass(Integer is_pass)
    {
        this.is_pass = is_pass;
    }

    public Integer getIs_courseproject()
    {
        return is_courseproject;
    }

    public void setIs_courseproject(Integer is_courseproject)
    {
        this.is_courseproject = is_courseproject;
    }

    public Integer getIs_coursework()
    {
        return is_coursework;
    }

    public void setIs_coursework(Integer is_coursework)
    {
        this.is_coursework = is_coursework;
    }

    public Integer getIs_practic()
    {
        return is_practic;
    }

    public void setIs_practic(Integer is_practic)
    {
        this.is_practic = is_practic;
    }

    public Integer getExamrating()
    {
        return examrating;
    }

    public void setExamrating(Integer examrating)
    {
        this.examrating = examrating;
    }

    public Integer getPassrating()
    {
        return passrating;
    }

    public void setPassrating(Integer passrating)
    {
        this.passrating = passrating;
    }

    public Integer getCourseprojectrating()
    {
        return courseprojectrating;
    }

    public void setCourseprojectrating(Integer courseprojectrating)
    {
        this.courseprojectrating = courseprojectrating;
    }

    public Integer getCourseworkrating()
    {
        return courseworkrating;
    }

    public void setCourseworkrating(Integer courseworkrating)
    {
        this.courseworkrating = courseworkrating;
    }

    public Integer getPracticrating()
    {
        return practicrating;
    }

    public void setPracticrating(Integer practicrating)
    {
        this.practicrating = practicrating;
    }

    public Integer getEsoexamrating()
    {
        return esoexamrating;
    }

    public void setEsoexamrating(Integer esoexamrating)
    {
        this.esoexamrating = esoexamrating;
    }

    public Integer getEsopassrating()
    {
        return esopassrating;
    }

    public void setEsopassrating(Integer esopassrating)
    {
        this.esopassrating = esopassrating;
    }

    public Integer getEsocourseprojectrating()
    {
        return esocourseprojectrating;
    }

    public void setEsocourseprojectrating(Integer esocourseprojectrating)
    {
        this.esocourseprojectrating = esocourseprojectrating;
    }

    public Integer getEsocourseworkrating()
    {
        return esocourseworkrating;
    }

    public void setEsocourseworkrating(Integer esocourseworkrating)
    {
        this.esocourseworkrating = esocourseworkrating;
    }

    public String getSubjectname()
    {
        return subjectname;
    }

    public void setSubjectname(String subjectname)
    {
        this.subjectname = subjectname;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIs_active() {
        return is_active;
    }

    public void setIs_active(Integer is_active) {
        this.is_active = is_active;
    }
}
