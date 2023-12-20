package org.edec.synchroMine.model.eso.entity.embedable;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
public class StudyStatus {
    /**
     * Отчислен
     **/
    @Column(name = "is_deducted")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean deducted;
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

    //Основания обучения
    /**
     * Флаг бюджетной формы обучения(по умолчанию - 1).
     */
    @Column(name = "is_government_financed")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean governmentFinanced;
    /**
     * Флаг целевого договора(по умолчанию - 0).
     */
    @Column(name = "is_trustagreement")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean trustAgreement;
}