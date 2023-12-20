/*
 * 
 */
package org.edec.contingentMovement.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "student_semester_status")
public class StudentStatusModel {

    /**
     * Идентификатор.
     */
    @Id
    @Column(name = "id_student_semester_status")
    private long id;

    /**
     * Флаг бюджетной формы обучения(по умолчанию - 1).
     */
    @Column(name = "is_government_financed")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean governmentFinanced;

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
     * Флаг того, что студент отчислен в текущем семестре(по умолчанию - 0).
     */
    @Column(name = "is_deducted")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean deducted;

    /**
     * Флаг научной работы(по умолчанию - 0).
     */
    @Column(name = "is_scientificwork")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean scientificWork;

    /**
     * Флаг общественной работы(по умолчанию - 0).
     */
    @Column(name = "is_publicWork")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean publicWork;

    /**
     * Флаг того, что студент является ликвидатором аварии на ЧАЭС(по умолчанию - 0).
     */
    @Column(name = "is_chernobolec")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean chernobolec;

    /**
     * Флаг того, что студент сирота(по умолчанию - 0).
     */
    @Column(name = "is_sirota")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean sirota;

    /**
     * Флаг того, что студент имеет инвалидность(по умолчанию - 0).
     */
    @Column(name = "is_invalid")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean invalid;

    /**
     * Флаг того, что студент имеет продление сессии(по умолчанию - 0).
     */
    @Column(name = "is_sessionprolongation")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean sessionProlongation;

    /**
     * Флаг того, что студент является участником боевых действий(по умолчанию - 0).
     */
    @Column(name = "is_combatants")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean combatants;

    /**
     * Флаг того, что студент в академическом отпуске(по умолчанию - 0).
     */
    @Column(name = "is_academicleave")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean academicLeave;

    /**
     * Слушатель: посещает занятия, но неофициально. Не учитывается в приказах и отчётах(по умолчанию - 0).
     */
    @Column(name = "is_listener")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean listener;

    /**
     * Флаг целевого договора(по умолчанию - 0).
     */
    @Column(name = "is_trustagreement")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean trustAGreement;

    /**
     * Флаг второго высшего образования (для заочников)(по умолчанию - 0).
     */
    @Column(name = "is_seconddegree")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean secondDegree;

    /**
     * Флаг старосты группы(по умолчанию - 0).
     */
    @Column(name = "is_group_leader")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean groupLeader;

    /**
     * Флаг того, что студент завершил обучение(по умолчанию - 0).
     */
    @Column(name = "is_educationcomplete")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean educationComplete;

    /**
     * Дата начала продления сессии(по умолчанию - 0).
     */
    @Column
    private Date prolongationBeginDate;

    /**
     * Дата окончания продления сессии(по умолчанию - 0).
     */
    @Column
    private Date prolongationEndDate;

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
    private Boolean earlySessionComplited;

    @Column(name = "is_specialeducation")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean specialEducaion;

    @Column(name = "is_get_academic")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean getAcademic;

    @Column(name = "is_get_social")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean getSocial;

    @Column(name = "is_get_social_increased")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean getSocialIncreased;

    @Column(name = "is_transfered")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean transfered;

    @Column(name = "is_transfered_conditionally")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean transferedConditionally;
}
