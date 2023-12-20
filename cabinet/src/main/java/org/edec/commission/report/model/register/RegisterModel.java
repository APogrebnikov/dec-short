package org.edec.commission.report.model.register;

import org.edec.commission.report.model.CommissionEmployeeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmmax
 */
public class RegisterModel {
    /**
     * Председатель
     **/
    private String chairman;
    /**
     * Члены комиссии разделенные \n
     **/
    private String commiteeman;
    /**
     * Курс
     **/
    private String course;
    /**
     * Дата сдачи
     **/
    private String dateOfPass;
    /**
     * Форма контроля
     **/
    private String formOfControl;
    /**
     * Институт
     **/
    private String institute;
    /**
     * Номер ведомости
     **/
    private String numberOfReg;
    /**
     * Семестр
     **/
    private String semester;
    /**
     * Название предмета
     **/
    private String subject;

    private List<CommissionEmployeeModel> employees = new ArrayList<>();
    private List<HumanfaceModel> students = new ArrayList<>();

    public RegisterModel () {
    }

    public String getFormOfControl () {
        return formOfControl;
    }

    public void setFormOfControl (String formOfControl) {
        this.formOfControl = formOfControl;
    }

    public String getChairman () {
        return chairman;
    }

    public void setChairman (String chairman) {
        this.chairman = chairman;
    }

    public String getCommiteeman () {
        return commiteeman;
    }

    public void setCommiteeman (String commiteeman) {
        this.commiteeman = commiteeman;
    }

    public String getCourse () {
        return course;
    }

    public void setCourse (String course) {
        this.course = course;
    }

    public String getDateOfPass () {
        return dateOfPass;
    }

    public void setDateOfPass (String dateOfPass) {
        this.dateOfPass = dateOfPass;
    }

    public String getInstitute () {
        return institute;
    }

    public void setInstitute (String institute) {
        this.institute = institute;
    }

    public String getNumberOfReg () {
        return numberOfReg;
    }

    public void setNumberOfReg (String numberOfReg) {
        this.numberOfReg = numberOfReg;
    }

    public String getSemester () {
        return semester;
    }

    public void setSemester (String semester) {
        this.semester = semester;
    }

    public String getSubject () {
        return subject;
    }

    public void setSubject (String subject) {
        this.subject = subject;
    }

    public List<HumanfaceModel> getStudents () {
        return students;
    }

    public void setStudents (List<HumanfaceModel> students) {
        this.students = students;
    }

    public List<CommissionEmployeeModel> getEmployees () {
        return employees;
    }

    public void setEmployees (List<CommissionEmployeeModel> employees) {
        this.employees = employees;
    }
}
