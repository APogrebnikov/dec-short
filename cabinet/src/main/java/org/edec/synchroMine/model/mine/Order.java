package org.edec.synchroMine.model.mine;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "[Приказы]")
public class Order {
    @Id
    @Column(name = "Код_приказа")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "Утвержден")
    private Boolean confirm;
    @Column(name = "Удален")
    private Boolean deleted;
    @Column(name = "Проведен")
    private Boolean held;

    @Column(name = "ДатаСоздания")
    private Date dateCreated;
    @Column(name = "Дата")
    private Date dateSigned;

    @Column(name = "Архив")
    private Integer archive;
    @Column(name = "Отменил")
    private Integer cancelerId;
    @Column(name = "Утвердил")
    private Integer confirmerId;
    @Column(name = "Провел")
    private Integer helperId;
    @Column(name = "УчебныйГод")
    private Integer schoolYearId;
    @Column(name = "ПрогНомер")
    private Integer progNumber;

    @Column(name = "Создал")
    private Long creatorEmpId;
    @Column(name = "КодФакультета")
    private Long instituteId;

    @Column(name = "Приказ")
    private String description;
    @Column(name = "Номер")
    private String number;
    @Column(name = "ТипПриказа")
    private String orderType;
    @Column(name = "Подзаголовок")
    private String subtitle;

    public Order () {
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public Boolean getConfirm () {
        return confirm;
    }

    public void setConfirm (Boolean confirm) {
        this.confirm = confirm;
    }

    public Boolean getDeleted () {
        return deleted;
    }

    public void setDeleted (Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getHeld () {
        return held;
    }

    public void setHeld (Boolean held) {
        this.held = held;
    }

    public Date getDateCreated () {
        return dateCreated;
    }

    public void setDateCreated (Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateSigned () {
        return dateSigned;
    }

    public void setDateSigned (Date dateSigned) {
        this.dateSigned = dateSigned;
    }

    public Integer getArchive () {
        return archive;
    }

    public void setArchive (Integer archive) {
        this.archive = archive;
    }

    public Integer getCancelerId () {
        return cancelerId;
    }

    public void setCancelerId (Integer cancelerId) {
        this.cancelerId = cancelerId;
    }

    public Integer getConfirmerId () {
        return confirmerId;
    }

    public void setConfirmerId (Integer confirmerId) {
        this.confirmerId = confirmerId;
    }

    public Integer getHelperId () {
        return helperId;
    }

    public void setHelperId (Integer helperId) {
        this.helperId = helperId;
    }

    public Integer getSchoolYearId () {
        return schoolYearId;
    }

    public void setSchoolYearId (Integer schoolYearId) {
        this.schoolYearId = schoolYearId;
    }

    public Integer getProgNumber () {
        return progNumber;
    }

    public void setProgNumber (Integer progNumber) {
        this.progNumber = progNumber;
    }

    public Long getCreatorEmpId () {
        return creatorEmpId;
    }

    public void setCreatorEmpId (Long creatorEmpId) {
        this.creatorEmpId = creatorEmpId;
    }

    public Long getInstituteId () {
        return instituteId;
    }

    public void setInstituteId (Long instituteId) {
        this.instituteId = instituteId;
    }

    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public String getNumber () {
        return number;
    }

    public void setNumber (String number) {
        this.number = number;
    }

    public String getOrderType () {
        return orderType;
    }

    public void setOrderType (String orderType) {
        this.orderType = orderType;
    }

    public String getSubtitle () {
        return subtitle;
    }

    public void setSubtitle (String subtitle) {
        this.subtitle = subtitle;
    }
}
