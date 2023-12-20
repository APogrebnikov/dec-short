/*
 * 
 */
package org.edec.synchroMine.model.eso.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * Класс Ведомость.
 */
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "register")
public class Register {

    @Id
    @Column(name = "id_register")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "is_canceled")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isCanceled;

    @Column
    private Date signdate;

    @Column(name = "id_semester")
    private Long idSemester;

    @Column(name = "register_number")
    private String registerNumber;
    @Column(name = "register_url")
    private String registerURL;
    @Column
    private String signatoryTutor;
    @Column(name = "certnumber")
    private String certnumber;
    @Column
    private String thumbprint;
}
