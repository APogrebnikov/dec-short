package org.edec.utility.report.model.order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmmax
 */
public class GroupOrderModel {
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
    private List<StudentOrderModel> students = new ArrayList<StudentOrderModel>();

    /**
     * Список пунктов
     */
    private List<SectionOrderModel> sections = new ArrayList<SectionOrderModel>();

    /**
     * Список подписывающих\согласующих лич
     */
    private List<EmployeeOrderModel> employees = new ArrayList<EmployeeOrderModel>();

    public GroupOrderModel () {
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

    public List<StudentOrderModel> getStudents () {
        return students;
    }

    public void setStudents (List<StudentOrderModel> students) {
        this.students = students;
    }

    public List<SectionOrderModel> getSections () {
        return sections;
    }

    public void setSections (List<SectionOrderModel> sections) {
        this.sections = sections;
    }

    public List<EmployeeOrderModel> getEmployees () {
        return employees;
    }

    public void setEmployees (List<EmployeeOrderModel> employees) {
        this.employees = employees;
    }
}
