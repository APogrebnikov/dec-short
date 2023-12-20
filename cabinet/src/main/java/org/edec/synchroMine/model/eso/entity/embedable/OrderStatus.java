package org.edec.synchroMine.model.eso.entity.embedable;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Date;

@Embeddable
@Data
public class OrderStatus {
    /**
     * The ord scholar ship.
     */
    @Column(name = "is_ord_scholarship")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean ordScholarShip;
    /**
     * The ord transfer.
     */
    @Column(name = "is_ord_transfer")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean ordTransfer;
    /**
     * Флаг переводника
     */
    @Column(name = "is_transfer_student")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean transferStudent;
    /**
     * Флаг досрочного закрытия сессии
     */
    @Column(name = "is_early_session_complited")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean earlySessionCompleted;
    /**
     * Флаг подачи заявления на социальную стипендию(по умолчанию - 0).
     */
    @Column(name = "is_put_app_for_social_grant")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean putAppForSocialGrant;
    /**
     * Флаг начисления (получения) социальной стипендии(по умолчанию - 0).
     */
    @Column(name = "is_get_social_grant")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean getSocialGrant;
    /**
     * Дата начала назначения стипендии.
     */
    @Column
    private Date dateOfScholarShipBegin;
    /**
     * Дата окончания назначения стипендии.
     */
    @Column
    private Date dateOfScholarShipEnd;
}