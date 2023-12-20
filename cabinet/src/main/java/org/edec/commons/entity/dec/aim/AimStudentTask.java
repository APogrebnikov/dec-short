package org.edec.commons.entity.dec.aim;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
@Getter
@Setter
@NoArgsConstructor
/*@Entity
@Table(name = "aim_student_task")*/ public class AimStudentTask {
    @Id
    @Column(name = "id_aim_student_task")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Сколько раз нужно выполнить задание
     */
    @Column
    private Integer amount;
    /**
     * Количество дней, необходимое на завершение задачи
     */
    @Column(name = "execution_days")
    private Integer executionDays;

    /**
     * если size = 0, задачи еще не выполнены
     */
    @ManyToMany
    @JoinTable(name = "aim_student_complete_task",
               joinColumns = {@JoinColumn(name = "id_aim_student_task", referencedColumnName = "id_aim_student_task")},
               inverseJoinColumns = {@JoinColumn(name = "id_achievement", referencedColumnName = "id_achievement")})
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Achievement> achievements;
    @ManyToOne
    @JoinColumn(name = "id_aim_set")
    private AimSet set;
    @ManyToOne
    @JoinColumn(name = "id_aim_task")
    private AimTask task;

    //Не хранимые переменные
    @Transient
    private Boolean completed = false;
}
