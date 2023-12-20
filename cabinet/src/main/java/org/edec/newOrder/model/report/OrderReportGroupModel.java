package org.edec.newOrder.model.report;

import java.util.ArrayList;
import java.util.List;


public class OrderReportGroupModel {
    /**
     * Название группы
     */
    private String groupname;
    /**
     * ФИО утверждающего лица
     */
    private String predicatingfio;
    /**
     * ДОЛЖНОСТЬ утверждающего лица
     */
    private String predicatingpost;

    /**
     * Список студентов
     */
    private List<OrderReportStudentModel> students = new ArrayList<>();

    /**
     * Список пунктов
     */
    private List<OrderReportSectionModel> sections = new ArrayList<>();

    /**
     * Список подписывающих\согласующих лич
     */
    private List<OrderReportEmployeeModel> employees = new ArrayList<>();

    public OrderReportGroupModel () {
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public String getPredicatingfio () {
        return predicatingfio;
    }

    public void setPredicatingfio (String predicatingfio) {
        this.predicatingfio = predicatingfio;
    }

    public String getPredicatingpost () {
        return predicatingpost;
    }

    public void setPredicatingpost (String predicatingpost) {
        this.predicatingpost = predicatingpost;
    }

    public List<OrderReportStudentModel> getStudents () {
        return students;
    }

    public void setStudents (List<OrderReportStudentModel> students) {
        this.students = students;
    }

    public List<OrderReportSectionModel> getSections () {
        return sections;
    }

    public void setSections (List<OrderReportSectionModel> sections) {
        this.sections = sections;
    }

    public List<OrderReportEmployeeModel> getEmployees () {
        return employees;
    }

    public void setEmployees (List<OrderReportEmployeeModel> employees) {
        this.employees = employees;
    }
}
