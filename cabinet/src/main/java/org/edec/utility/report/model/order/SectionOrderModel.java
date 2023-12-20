package org.edec.utility.report.model.order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmmax
 */
public class SectionOrderModel {
    private Boolean prolongation;
    /**
     * Описание пунтка
     */
    private String description;
    /**
     * Основание пункта
     */
    private String foundation;

    /**
     * Список студентов
     */
    private List<StudentOrderModel> students = new ArrayList<StudentOrderModel>();

    /**
     * Список групп
     */
    private List<GroupOrderModel> groups = new ArrayList<GroupOrderModel>();

    /**
     * Список курсов
     */
    private List<CourseOrderModel> courses = new ArrayList<CourseOrderModel>();

    /**
     * Cписок подпунктов
     */
    private List<SectionOrderModel> subsections = new ArrayList<SectionOrderModel>();

    /**
     * Список подписывающих\согласующих лич
     */
    private List<EmployeeOrderModel> employees = new ArrayList<EmployeeOrderModel>();

    public SectionOrderModel () {
    }

    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public String getFoundation () {
        return foundation;
    }

    public void setFoundation (String foundation) {
        this.foundation = foundation;
    }

    public List<StudentOrderModel> getStudents () {
        return students;
    }

    public void setStudents (List<StudentOrderModel> students) {
        this.students = students;
    }

    public List<GroupOrderModel> getGroups () {
        return groups;
    }

    public void setGroups (List<GroupOrderModel> groups) {
        this.groups = groups;
    }

    public List<CourseOrderModel> getCourses () {
        return courses;
    }

    public void setCourses (List<CourseOrderModel> courses) {
        this.courses = courses;
    }

    public List<SectionOrderModel> getSubsections () {
        return subsections;
    }

    public void setSubsections (List<SectionOrderModel> subsections) {
        this.subsections = subsections;
    }

    public List<EmployeeOrderModel> getEmployees () {
        return employees;
    }

    public void setEmployees (List<EmployeeOrderModel> employees) {
        this.employees = employees;
    }

    public Boolean getProlongation () {
        return prolongation;
    }

    public void setProlongation (Boolean prolongation) {
        this.prolongation = prolongation;
    }
}
