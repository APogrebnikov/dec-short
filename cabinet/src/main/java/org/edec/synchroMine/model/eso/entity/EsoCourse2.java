package org.edec.synchroMine.model.eso.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Max Dimukhametov
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "esocourse2")
public class EsoCourse2 {
    @Id
    @Column(name = "id_esocourse2", nullable = false, updatable = false)
    private Long id;

    @Column(name = "id_category")
    private Long idCategory;

    @Column
    private String fullname;
    private String shortname;
}
