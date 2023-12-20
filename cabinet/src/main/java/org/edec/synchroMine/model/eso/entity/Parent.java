/*
 * 
 */
package org.edec.synchroMine.model.eso.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Класс Учебный план.
 */
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "parent")
public class Parent {

    @Id
    @Column(name = "id_parent")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String family;

    @Column
    private String patronymic;

    @Column
    private String username;

    @Column
    private String password;

    @Column(name = "id_studentcard")
    private Long idStudentCard;

    @Column(name = "is_active")
    private Boolean active;

    @Column
    private String email;

    @Column(name = "start_page")
    private String startPage;
}
