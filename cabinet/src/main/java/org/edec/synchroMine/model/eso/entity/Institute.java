package org.edec.synchroMine.model.eso.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author Max Dimukhametov
 */
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "institute")
public class Institute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_institute")
    private Long id;

    /**
     * Идентификатор из Шахт
     */
    @Column
    private Long otherdbid;

    /**
     * Название LDAP ветки
     */
    @Column(name = "ldap_node")
    private String ldapNode;
    /**
     * Полное название института.
     */
    @Column
    private String fullTitle;
    /**
     * Короткое название института.
     */
    @Column
    private String shortTitle;
}
