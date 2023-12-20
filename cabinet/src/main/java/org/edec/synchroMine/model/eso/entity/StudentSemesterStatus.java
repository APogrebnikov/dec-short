package org.edec.synchroMine.model.eso.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.edec.synchroMine.model.eso.entity.embedable.OrderStatus;
import org.edec.synchroMine.model.eso.entity.embedable.NotUsedStatus;
import org.edec.synchroMine.model.eso.entity.embedable.Prolongation;
import org.edec.synchroMine.model.eso.entity.embedable.StudyStatus;

import javax.persistence.*;

/**
 * @author Max Dimukhametov
 */
@Data
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "student_semester_status")
public class StudentSemesterStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_student_semester_status")
    private Long id;

    @Embedded
    private Prolongation prolongation;
    @Embedded
    private StudyStatus studyStatus;
    @Embedded
    private OrderStatus orderStatus;
    @Embedded
    private NotUsedStatus notUsedStatus;

    /**
     * Закрыл ли студент сессию
     * <ul>
     * <li><0 - количество долгов
     * <li>0 - пустой запрос
     * <li>1 - закрыл, есть 3
     * <li>2 - закрыл, только на 4
     * <li>3 - закрыл, на 4 и 5
     * <li>4 - закрыл, только на 5.
     * </ul>
     */
    @Column
    private int sessionResult;
    /**
     * Количество долгов(по умолчанию - 0).
     */
    @Column
    private int debtCount;
    /**
     * Форма обучения: 1 - очная, 2 - заочная, 3 - вечерняя(по умолчанию - 1) .
     */
    @Column
    private int formOfStudy;
}
