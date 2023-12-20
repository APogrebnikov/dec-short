package org.edec.synchroMine.model.mine;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Max Dimukhametov
 */
@Entity
@Table(name = "[ПриказыДействияСтуденты]")
public class OrderActionStudent {
    @Id
    @Column(name = "Код")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "УслПеревод")
    private Boolean conditional;
    @Column(name = "Проведен")
    private Boolean held;

    @Column(name = "ДатаС")
    private Date dateFrom;
    @Column(name = "датаПо")
    private Date dateTo;

    @Column(name = "ГруппаИз")
    private Long idGroupFrom;
    @Column(name = "ГруппаКуда")
    private Long idGroupTo;
    @Column(name = "ФакультетИз")
    private Long idInstitute;
    @Column(name = "КодПриказа")
    private Long idOrder;
    @Column(name = "КодДействияПриказа")
    private Long idOrderAction;
    @Column(name = "КодСтудента")
    private Long idStudent;
    @Column(name = "Основания")
    private Long idStudyForm;
    @Column(name = "ОснованияДо")
    private Long idStudyFormBefore;
    /**
     * Статус
     * -1 - Академический отпуск
     * 1 - учащийся
     * 3 - отчислен
     * 4 - завершил обучение
     * 5 - призван в армию
     * 6 - абитуриент
     * 7 - бывший абитуриент
     * 9 - бывший абитуриент
     * 10 - Web-Абитуриент
     */
    @Column(name = "Статус")
    private Long idStatus;

    @Column(name = "Атрибут1")
    private String attr1;
    @Column(name = "Атрибут2")
    private String attr2;
    @Column(name = "Атрибут3")
    private String attr3;
    @Column(name = "Атрибут4")
    private String attr4;
    @Column(name = "Атрибут5")
    private String attr5;
    @Column(name = "Атрибут6")
    private String attr6;
    @Column(name = "ФИО")
    private String fio;
    @Column(name = "ТекстОснования")
    private String foundation;
    @Column(name = "НоваяФамилия")
    private String newFamily;
    @Column(name = "НовоеИмя")
    private String newName;
    @Column(name = "НовоеОтчество")
    private String newPatronymic;
    @Column(name = "Номер")
    private String number;
    @Column(name = "ТекстПричина")
    private String reason;
    @Column(name = "Стипендия")
    private String scholarship;
    @Column(name = "Сумма")
    private String summ;
    @Column(name = "ТекстШаблона")
    private String template;
    @Column(name = "УЗ")
    private String us;

    /**
     * ----Не учавствуют в запросе
     **/
    @Transient
    private Integer course;
    @Transient
    private Integer is_trustagreement;
    @Transient
    private Integer sessresult;
    @Transient
    private Integer qualification;

    @Transient
    private Long idGroupNextCourse;

    @Transient
    private String additional;
    @Transient
    private String directioncode;
    @Transient
    private String directionname;
    @Transient
    private String formofstudy;
    @Transient
    private String foundationofstudy;
    @Transient
    private String groupname;
    @Transient
    private String profilecode;
    @Transient
    private String profilename;
    @Transient
    private String recordbook;
    @Transient
    private String sectionName;

    public OrderActionStudent () {
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public Boolean getConditional () {
        return conditional;
    }

    public void setConditional (Boolean conditional) {
        this.conditional = conditional;
    }

    public Boolean getHeld () {
        return held;
    }

    public void setHeld (Boolean held) {
        this.held = held;
    }

    public Date getDateFrom () {
        return dateFrom;
    }

    public void setDateFrom (Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo () {
        return dateTo;
    }

    public void setDateTo (Date dateTo) {
        this.dateTo = dateTo;
    }

    public Long getIdGroupFrom () {
        return idGroupFrom;
    }

    public void setIdGroupFrom (Long idGroupFrom) {
        this.idGroupFrom = idGroupFrom;
    }

    public Long getIdGroupTo () {
        return idGroupTo;
    }

    public void setIdGroupTo (Long idGroupTo) {
        this.idGroupTo = idGroupTo;
    }

    public Long getIdInstitute () {
        return idInstitute;
    }

    public void setIdInstitute (Long idInstitute) {
        this.idInstitute = idInstitute;
    }

    public Long getIdOrder () {
        return idOrder;
    }

    public void setIdOrder (Long idOrder) {
        this.idOrder = idOrder;
    }

    public Long getIdOrderAction () {
        return idOrderAction;
    }

    public void setIdOrderAction (Long idOrderAction) {
        this.idOrderAction = idOrderAction;
    }

    public Long getIdStudent () {
        return idStudent;
    }

    public void setIdStudent (Long idStudent) {
        this.idStudent = idStudent;
    }

    public Long getIdStudyForm () {
        return idStudyForm;
    }

    public void setIdStudyForm (Long idStudyForm) {
        this.idStudyForm = idStudyForm;
    }

    public Long getIdStudyFormBefore () {
        return idStudyFormBefore;
    }

    public void setIdStudyFormBefore (Long idStudyFormBefore) {
        this.idStudyFormBefore = idStudyFormBefore;
    }

    public Long getIdStatus () {
        return idStatus;
    }

    public void setIdStatus (Long idStatus) {
        this.idStatus = idStatus;
    }

    public String getAttr1 () {
        return attr1;
    }

    public void setAttr1 (String attr1) {
        this.attr1 = attr1;
    }

    public String getAttr2 () {
        return attr2;
    }

    public void setAttr2 (String attr2) {
        this.attr2 = attr2;
    }

    public String getAttr3 () {
        return attr3;
    }

    public void setAttr3 (String attr3) {
        this.attr3 = attr3;
    }

    public String getAttr4 () {
        return attr4;
    }

    public void setAttr4 (String attr4) {
        this.attr4 = attr4;
    }

    public String getAttr5 () {
        return attr5;
    }

    public void setAttr5 (String attr5) {
        this.attr5 = attr5;
    }

    public String getAttr6 () {
        return attr6;
    }

    public void setAttr6 (String attr6) {
        this.attr6 = attr6;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getFoundation () {
        return foundation;
    }

    public void setFoundation (String foundation) {
        this.foundation = foundation;
    }

    public String getNewFamily () {
        return newFamily;
    }

    public void setNewFamily (String newFamily) {
        this.newFamily = newFamily;
    }

    public String getNewName () {
        return newName;
    }

    public void setNewName (String newName) {
        this.newName = newName;
    }

    public String getNewPatronymic () {
        return newPatronymic;
    }

    public void setNewPatronymic (String newPatronymic) {
        this.newPatronymic = newPatronymic;
    }

    public String getNumber () {
        return number;
    }

    public void setNumber (String number) {
        this.number = number;
    }

    public String getReason () {
        return reason;
    }

    public void setReason (String reason) {
        this.reason = reason;
    }

    public String getScholarship () {
        return scholarship;
    }

    public void setScholarship (String scholarship) {
        this.scholarship = scholarship;
    }

    public String getSumm () {
        return summ;
    }

    public void setSumm (String summ) {
        this.summ = summ;
    }

    public String getTemplate () {
        return template;
    }

    public void setTemplate (String template) {
        this.template = template;
    }

    public String getUs () {
        return us;
    }

    public void setUs (String us) {
        this.us = us;
    }

    public Integer getCourse () {
        return course;
    }

    public void setCourse (Integer course) {
        this.course = course;
    }

    public Integer getIs_trustagreement () {
        return is_trustagreement;
    }

    public void setIs_trustagreement (Integer is_trustagreement) {
        this.is_trustagreement = is_trustagreement;
    }

    public String getAdditional () {
        return additional;
    }

    public void setAdditional (String additional) {
        this.additional = additional;
    }

    public Integer getSessresult () {
        return sessresult;
    }

    public void setSessresult (Integer sessresult) {
        this.sessresult = sessresult;
    }

    public Integer getQualification () {
        return qualification;
    }

    public void setQualification (Integer qualification) {
        this.qualification = qualification;
    }

    public String getDirectioncode () {
        return directioncode;
    }

    public void setDirectioncode (String directioncode) {
        this.directioncode = directioncode;
    }

    public String getDirectionname () {
        return directionname;
    }

    public void setDirectionname (String directionname) {
        this.directionname = directionname;
    }

    public String getFormofstudy () {
        return formofstudy;
    }

    public void setFormofstudy (String formofstudy) {
        this.formofstudy = formofstudy;
    }

    public String getFoundationofstudy () {
        return foundationofstudy;
    }

    public void setFoundationofstudy (String foundationofstudy) {
        this.foundationofstudy = foundationofstudy;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public String getProfilecode () {
        return profilecode;
    }

    public void setProfilecode (String profilecode) {
        this.profilecode = profilecode;
    }

    public String getProfilename () {
        return profilename;
    }

    public void setProfilename (String profilename) {
        this.profilename = profilename;
    }

    public String getRecordbook () {
        return recordbook;
    }

    public void setRecordbook (String recordbook) {
        this.recordbook = recordbook;
    }

    public String getSectionName () {
        return sectionName;
    }

    public void setSectionName (String sectionName) {
        this.sectionName = sectionName;
    }

    public Long getIdGroupNextCourse () {
        return idGroupNextCourse;
    }

    public void setIdGroupNextCourse (Long idGroupNextCourse) {
        this.idGroupNextCourse = idGroupNextCourse;
    }
}
