package org.edec.commons.entity.dec.aim;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Max Dimukhametov
 */
@Getter
@Setter
@NoArgsConstructor
/*@Entity
@Table(name = "achievement")*/ public class Achievement {
    @Id
    @Column(name = "id_achievement")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "publication_date")
    private Date publicationDate;

    @Column(name = "id_studentcard")
    private Long idStudentCard;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "id_aim_task_type")
    private AimTaskType aimTaskType;
    @ManyToOne
    @JoinColumn(name = "id_aim_level_involvement")
    private AimLevelInvolvement levelInvolvement;
}
