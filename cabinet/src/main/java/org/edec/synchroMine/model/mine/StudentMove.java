package org.edec.synchroMine.model.mine;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Max Dimukhametov
 */
@Entity
@Table(name = "[Перемещения]")
public class StudentMove {
    @Id
    @Column(name = "Код_Перемещения")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "ДатаС")
    private Date dateFrom;
    @Column(name = "Дата_Перемещения")
    private Date dateMove;
    @Column(name = "ДатаПо")
    private Date dateTo;
    @Column(name = "ДатаЗаписи")
    private Date dateWrite;

    @Column(name = "Год_Поступления")
    private Integer comeDate;
    @Column(name = "Долг")
    private Integer dept;
    @Column(name = "Скрыть")
    private Integer hide;
    @Column(name = "КодТипаПеремещения")
    private Integer moveTypeId;
    @Column(name = "КодПриказа")
    private Integer orderType;
    @Column(name = "ПрошлыйСтатус")
    private Integer statusOld;
    @Column(name = "КодТипа")
    private Integer typeCode;

    @Column(name = "КодГруппыИз")
    private Long idGroupFrom;
    @Column(name = "КодГруппыВ")
    private Long idGroupTo;
    @Column(name = "КодФакультетаИз")
    private Long idInstituteFrom;
    @Column(name = "КодФакультетаВ")
    private Long idInstituteTo;

    @Column(name = "Код_Студента")
    private Long idStudent;

    @Column(name = "Примечание")
    private String comment;
    @Column(name = "Компьютер")
    private String computerName;
    @Column(name = "Из_Группы")
    private String groupFrom;
    @Column(name = "В_Группу")
    private String groupTo;
    /**
     * Тип_Перемещения
     * <p>
     * Отчислить по собственному желанию
     * Академический отпуск
     * Отчислен
     */
    @Column(name = "Тип_Перемещения")
    private String moveType;
    @Column(name = "Документ")
    private String orderNumber;
    /**
     * Основания_Обучения
     * УсловияОбучения	Сокращение
     * ---------------------------
     * Общие основания		ОО
     * Целевой прием		ЦН
     * Сверхплановый набор	СН
     * Особые правила		ОП
     * Направление Мин. Обр	МИН.ОБР
     */
    @Column(name = "Основания_Обучения")
    private String reason;
    @Column(name = "Учебный_год")
    private String schoolyear;
    @Column(name = "Специальность")
    private String speciality;
    @Column(name = "Пользователь")
    private String userName;

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public Date getDateFrom () {
        return dateFrom;
    }

    public void setDateFrom (Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateMove () {
        return dateMove;
    }

    public void setDateMove (Date dateMove) {
        this.dateMove = dateMove;
    }

    public Date getDateTo () {
        return dateTo;
    }

    public void setDateTo (Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateWrite () {
        return dateWrite;
    }

    public void setDateWrite (Date dateWrite) {
        this.dateWrite = dateWrite;
    }

    public Integer getComeDate () {
        return comeDate;
    }

    public void setComeDate (Integer comeDate) {
        this.comeDate = comeDate;
    }

    public Integer getDept () {
        return dept;
    }

    public void setDept (Integer dept) {
        this.dept = dept;
    }

    public Integer getHide () {
        return hide;
    }

    public void setHide (Integer hide) {
        this.hide = hide;
    }

    public Integer getMoveTypeId () {
        return moveTypeId;
    }

    public void setMoveTypeId (Integer moveTypeId) {
        this.moveTypeId = moveTypeId;
    }

    public Integer getOrderType () {
        return orderType;
    }

    public void setOrderType (Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getStatusOld () {
        return statusOld;
    }

    public void setStatusOld (Integer statusOld) {
        this.statusOld = statusOld;
    }

    public Integer getTypeCode () {
        return typeCode;
    }

    public void setTypeCode (Integer typeCode) {
        this.typeCode = typeCode;
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

    public Long getIdInstituteFrom () {
        return idInstituteFrom;
    }

    public void setIdInstituteFrom (Long idInstituteFrom) {
        this.idInstituteFrom = idInstituteFrom;
    }

    public Long getIdInstituteTo () {
        return idInstituteTo;
    }

    public void setIdInstituteTo (Long idInstituteTo) {
        this.idInstituteTo = idInstituteTo;
    }

    public Long getIdStudent () {
        return idStudent;
    }

    public void setIdStudent (Long idStudent) {
        this.idStudent = idStudent;
    }

    public String getComment () {
        return comment;
    }

    public void setComment (String comment) {
        this.comment = comment;
    }

    public String getComputerName () {
        return computerName;
    }

    public void setComputerName (String computerName) {
        this.computerName = computerName;
    }

    public String getGroupFrom () {
        return groupFrom;
    }

    public void setGroupFrom (String groupFrom) {
        this.groupFrom = groupFrom;
    }

    public String getGroupTo () {
        return groupTo;
    }

    public void setGroupTo (String groupTo) {
        this.groupTo = groupTo;
    }

    public String getMoveType () {
        return moveType;
    }

    public void setMoveType (String moveType) {
        this.moveType = moveType;
    }

    public String getOrderNumber () {
        return orderNumber;
    }

    public void setOrderNumber (String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getReason () {
        return reason;
    }

    public void setReason (String reason) {
        this.reason = reason;
    }

    public String getSchoolyear () {
        return schoolyear;
    }

    public void setSchoolyear (String schoolyear) {
        this.schoolyear = schoolyear;
    }

    public String getSpeciality () {
        return speciality;
    }

    public void setSpeciality (String speciality) {
        this.speciality = speciality;
    }

    public String getUserName () {
        return userName;
    }

    public void setUserName (String userName) {
        this.userName = userName;
    }
}
