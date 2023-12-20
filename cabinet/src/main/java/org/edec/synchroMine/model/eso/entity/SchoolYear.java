package org.edec.synchroMine.model.eso.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Max Dimukhametov
 */
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "schoolyear")
public class SchoolYear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_schoolyear")
    private Long id;

    @Column
    private Date dateOfBegin;
    @Column
    private Date dateOfEnd;

    /**
     * Идентификатор шахт
     */
    @Column
    private Long otherDbId;
}
