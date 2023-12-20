package org.edec.synchroMine.model.eso.entity.embedable;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
public class NotUsedStatus {
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
     * Флаг того, что студент является участником боевых действий(по умолчанию - 0).
     */
    @Column(name = "is_combatants")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean combatants;
    /**
     * Флаг второго высшего образования (для заочников)(по умолчанию - 0).
     */
    @Column(name = "is_seconddegree")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean secondDegree;
}