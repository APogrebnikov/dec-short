package org.edec.newOrder.model.report;

import org.edec.newOrder.model.EmployeeOrderModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmmax
 */
public class OrderReportSectionModel {
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
    private List<OrderReportStudentModel> students = new ArrayList<>();

    /**
     * Список групп
     */
    private List<OrderReportGroupModel> groups = new ArrayList<>();

    /**
     * Список курсов
     */
    private List<OrderReportCourseModel> courses = new ArrayList<>();

    /**
     * Cписок подпунктов
     */
    private List<OrderReportSectionModel> subsections = new ArrayList<OrderReportSectionModel>();

    /**
     * Список подписывающих\согласующих лич
     */
    private List<EmployeeOrderModel> employees = new ArrayList<EmployeeOrderModel>();

    public OrderReportSectionModel () {
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

    public List<OrderReportStudentModel> getStudents () {
        return students;
    }

    public void setStudents (List<OrderReportStudentModel> students) {
        this.students = students;
    }

    public List<OrderReportGroupModel> getGroups () {
        return groups;
    }

    public void setGroups (List<OrderReportGroupModel> groups) {
        this.groups = groups;
    }

    public List<OrderReportCourseModel> getCourses () {
        return courses;
    }

    public void setCourses (List<OrderReportCourseModel> courses) {
        this.courses = courses;
    }

    public List<OrderReportSectionModel> getSubsections () {
        return subsections;
    }

    public void setSubsections (List<OrderReportSectionModel> subsections) {
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
