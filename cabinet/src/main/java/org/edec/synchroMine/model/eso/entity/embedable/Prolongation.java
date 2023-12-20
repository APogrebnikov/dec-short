package org.edec.synchroMine.model.eso.entity.embedable;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Date;

@Embeddable
@Data
public class Prolongation {
    /**
     * Флаг того, что студент имеет продление сессии(по умолчанию - 0).
     */
    @Column(name = "is_sessionprolongation")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean sessionProlongation;
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
}