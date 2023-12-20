package org.edec.synchroMine.model.eso.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * @author Max Dimukhametov
 */
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "semester")
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_semester")
    private Long id;

    @Column(name = "is_current_sem")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean currentSem;

    /**
     * Указываем какая первая неделя 1 - нечетная, 2 - четная
     */
    @Column
    private Integer firstWeek;
    /**
     * Форма обучения. 1 - очная, 2 - заочная
     */
    @Column(name = "formofstudy")
    private Integer formOfStudy;

    /**
     * Сезон. 0 - осень, 1 - весна
     */
    @Column
    private Integer season;

    @ManyToOne
    @JoinColumn(name = "id_institute")
    private Institute institute;
    @ManyToOne
    @JoinColumn(name = "id_schoolyear")
    private SchoolYear schoolYear;
}
