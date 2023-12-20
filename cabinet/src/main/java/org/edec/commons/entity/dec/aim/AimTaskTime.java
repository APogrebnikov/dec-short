package org.edec.commons.entity.dec.aim;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author Max Dimukhametov
 */
@Getter
@Setter
@NoArgsConstructor
/*@Entity
@Table(name = "aim_task_time")*/ public class AimTaskTime {
    @Id
    @Column(name = "id_aim_task_time")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "execution_days")
    private Integer executionDays;

    //По каким полям уникальное значение искалось
    @Column
    private Integer course;

    @Column(name = "id_institute")
    private Long idInst;
    @Column(name = "id_direction")
    private Long idDirection;

    @ManyToOne
    @JoinColumn(name = "id_aim_task_type")
    private AimTaskType taskType;
}
