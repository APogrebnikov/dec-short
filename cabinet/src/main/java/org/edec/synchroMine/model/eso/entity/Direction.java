/*
 * 
 */
package org.edec.synchroMine.model.eso.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Класс Направление обучения.
 */
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "direction")
public class Direction {

    /**
     * Идентификатор.
     */
    @Id
    @Column(name = "id_direction")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Код направления
     */
    @Column
    private String code;
    /**
     * Наименование направления обучения.
     */
    @Column
    private String title;

    /**
     * Ссылка на список учебных планов для направления обучения.
     */
    @OneToMany(mappedBy = "direction")
    private Set<Curriculum> listOfCurriculums = new HashSet<>();
}
