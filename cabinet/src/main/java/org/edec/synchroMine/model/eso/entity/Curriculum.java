/*
 *
 */
package org.edec.synchroMine.model.eso.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * Класс Учебный план.
 */
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "curriculum")
public class Curriculum {

    @Id
    @Column(name = "id_curriculum")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Период обучения, может быть дробным числом.
     */
    @Column(name = "periodofstudy")
    private Float periodOfStudy;

    /**
     * Заочная форма обучения: 1 - заочно-очная, 2 - заочная.(по умолчанию 2)
     */
    @Column(name = "distancetype")
    private int distanceType;
    /**
     * Форма обучения: 1 - очная, 2 - заочная(по умолчанию - 1).
     */
    @Column(name = "formofstudy")
    private int formOfStudy;
    /**
     * Поколение учебного плана.
     */
    @Column
    private int generation;
    /**
     * Квалификация: 1 - инженер, 2 - бакалавр, 3 - магистр.(по умолчанию - 1)
     */
    @Column
    private int qualification;

    /**
     * Ссылка на кафедру, за которой закреплён учебный план.
     */
    @Column(name = "id_chair")
    private Long chairId;

    /**
     * Код направления.
     */
    @Column
    private String directionCode;
    /**
     * Название файла с учебным планом для привязки к планам в системе.
     */
    @Column
    private String planFileName;
    /**
     * Код программы.
     */
    @Column
    private String programCode;
    /**
     * Наименование специальности.
     */
    @Column
    private String specialityTitle;
    /**
     * Код квалификации.
     */
    @Column
    private String qualificationCode;

    /**
     * Ссылка на направление обучения.
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "id_direction")
    private Direction direction;

    @Column(name = "created_school_year")
    private Long createdSchoolYear;
}
