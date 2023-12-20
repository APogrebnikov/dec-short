package org.edec.newOrder.model.report;

import org.edec.newOrder.report.model.OrderLineModel;
import org.edec.newOrder.report.model.OrderPageModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dmmax
 */
public class OrderReportMainModel {
    /**
     * Печатать фразу "ПРИКАЗЫВАЮ:" или нет
     */
    private Boolean printorder = true;

    /**
     * Дата приказа
     */
    private Date datesign;

    private Integer formOfStudyId;

    /**
     * ФИО с сертификата утверждающего лица
     */
    private String certfio;
    /**
     * НОМЕР сертификата утверждающего лица
     */
    private String certnumber;
    /**
     * Описание приказа
     */
    private String descriptiontitle;
    /**
     * Описание приказа 2
     */
    private String descriptiontitle2;
    /**
     * ФИО исполнителя
     */
    private String executorfio;
    /**
     * ТЕЛ исполнителя
     */
    private String executortel;
    /**
     * Форма обучения
     **/
    private String formofstudy;
    /**
     * Название института
     */
    private String institute;
    /**
     * Номер приказа
     */
    private String ordernumber;
    /**
     * ФИО утверждающего лица
     */
    private String predicatingfio;
    /**
     * ТЕЛ утверждающего лица
     */
    private String predicatingpost;
    /**
     * Тип приказа
     */
    private String typeorder;

    /**
     * Правило приказа
     */
    private Long idOrderRule;

    /*** Список согласующих лиц*/
    private List<OrderReportEmployeeModel> employees = new ArrayList<>();
    /**
     * Список групп
     */
    private List<OrderReportGroupModel> groups = new ArrayList<>();
    /**
     * Список пунктов
     */
    private List<OrderReportSectionModel> sections = new ArrayList<>();
    /**
     * Список курсов
     */
    private List<OrderReportCourseModel> courses = new ArrayList<>();
    private List<OrderReportModel> individualsStudents = new ArrayList<>();
    private List<OrderLineModel> orderLines = new ArrayList<>();
    private List<OrderPageModel> orderPages = new ArrayList<>();

    /**
     * Для служебки ЛАЗ
     **/
    private String dateNote;
    private String semesters;

    public OrderReportMainModel () {
    }

    public Boolean getPrintorder () {
        return printorder;
    }

    public void setPrintorder (Boolean printorder) {
        this.printorder = printorder;
    }

    public Date getDatesign () {
        return datesign;
    }

    public void setDatesign (Date datesign) {
        this.datesign = datesign;
    }

    public String getCertfio () {
        return certfio;
    }

    public void setCertfio (String certfio) {
        this.certfio = certfio;
    }

    public String getCertnumber () {
        return certnumber;
    }

    public void setCertnumber (String certnumber) {
        this.certnumber = certnumber;
    }

    public String getDescriptiontitle () {
        return descriptiontitle;
    }

    public void setDescriptiontitle (String descriptiontitle) {
        this.descriptiontitle = descriptiontitle;
    }

    public String getExecutorfio () {
        return executorfio;
    }

    public void setExecutorfio (String executorfio) {
        this.executorfio = executorfio;
    }

    public String getExecutortel () {
        return executortel;
    }

    public void setExecutortel (String executortel) {
        this.executortel = executortel;
    }

    public String getFormofstudy () {
        return formofstudy;
    }

    public void setFormofstudy (String formofstudy) {
        this.formofstudy = formofstudy;
    }

    public String getInstitute () {
        return institute;
    }

    public void setInstitute (String institute) {
        this.institute = institute;
    }

    public String getOrdernumber () {
        return ordernumber;
    }

    public void setOrdernumber (String ordernumber) {
        this.ordernumber = ordernumber;
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

    public String getTypeorder () {
        return typeorder;
    }

    public void setTypeorder (String typeorder) {
        this.typeorder = typeorder;
    }

    public List<OrderReportEmployeeModel> getEmployees () {
        return employees;
    }

    public void setEmployees (List<OrderReportEmployeeModel> employees) {
        this.employees = employees;
    }

    public List<OrderReportGroupModel> getGroups () {
        return groups;
    }

    public void setGroups (List<OrderReportGroupModel> groups) {
        this.groups = groups;
    }

    public List<OrderReportSectionModel> getSections () {
        return sections;
    }

    public void setSections (List<OrderReportSectionModel> sections) {
        this.sections = sections;
    }

    public List<OrderReportCourseModel> getCourses () {
        return courses;
    }

    public void setCourses (List<OrderReportCourseModel> courses) {
        this.courses = courses;
    }

    public List<OrderReportModel> getIndividualsStudents () {
        return individualsStudents;
    }

    public void setIndividualsStudents (List<OrderReportModel> individualsStudents) {
        this.individualsStudents = individualsStudents;
    }

    public String getDescriptiontitle2 () {
        return descriptiontitle2;
    }

    public void setDescriptiontitle2 (String descriptiontitle2) {
        this.descriptiontitle2 = descriptiontitle2;
    }

    public Long getIdOrderRule () {
        return idOrderRule;
    }

    public void setIdOrderRule (Long idOrderRule) {
        this.idOrderRule = idOrderRule;
    }

    public String getDateNote () {
        return dateNote;
    }

    public void setDateNote (String dateNote) {
        this.dateNote = dateNote;
    }

    public String getSemesters () {
        return semesters;
    }

    public void setSemesters (String semesters) {
        this.semesters = semesters;
    }

    public Integer getFormOfStudyId () {
        return formOfStudyId;
    }

    public void setFormOfStudyId (Integer formOfStudyId) {
        this.formOfStudyId = formOfStudyId;
    }

    public List<OrderPageModel> getOrderPages() {
        return orderPages;
    }

    public void setOrderPages(List<OrderPageModel> orderPages) {
        this.orderPages = orderPages;
    }

        public List<OrderLineModel> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<OrderLineModel> orderLines) {
        this.orderLines = orderLines;
    }
}
