package org.edec.utility.report.model.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dmmax
 */
public class MainOrderModel {
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
    private List<EmployeeOrderModel> employees = new ArrayList<EmployeeOrderModel>();
    /**
     * Список групп
     */
    private List<GroupOrderModel> groups = new ArrayList<GroupOrderModel>();
    /**
     * Список пунктов
     */
    private List<SectionOrderModel> sections = new ArrayList<SectionOrderModel>();
    /**
     * Список курсов
     */
    private List<CourseOrderModel> courses = new ArrayList<CourseOrderModel>();
    private List<IndividualOrderModel> individualsStudents = new ArrayList<>();

    /**
     * Для служебки ЛАЗ
     **/
    private String dateNote;
    private String semesters;

    public MainOrderModel () {
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

    public List<EmployeeOrderModel> getEmployees () {
        return employees;
    }

    public void setEmployees (List<EmployeeOrderModel> employees) {
        this.employees = employees;
    }

    public List<GroupOrderModel> getGroups () {
        return groups;
    }

    public void setGroups (List<GroupOrderModel> groups) {
        this.groups = groups;
    }

    public List<SectionOrderModel> getSections () {
        return sections;
    }

    public void setSections (List<SectionOrderModel> sections) {
        this.sections = sections;
    }

    public List<CourseOrderModel> getCourses () {
        return courses;
    }

    public void setCourses (List<CourseOrderModel> courses) {
        this.courses = courses;
    }

    public List<IndividualOrderModel> getIndividualsStudents () {
        return individualsStudents;
    }

    public void setIndividualsStudents (List<IndividualOrderModel> individualsStudents) {
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
}
