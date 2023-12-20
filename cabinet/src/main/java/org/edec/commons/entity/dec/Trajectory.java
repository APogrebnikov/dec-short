package org.edec.commons.entity.dec;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.edec.synchroMine.model.eso.entity.Curriculum;

import javax.persistence.*;

/**
 * @author Max Dimukhametov
 */
@Data
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "curriculum_trajectory")
public class Trajectory {
    @Id
    @Column(name = "id_trajectory")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "current_year")
    private Boolean currentYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_direction")
    public Curriculum curriculum;

    @ManyToOne
    @JoinColumn(name = "id_dic_trajectory")
    private DicTrajectory dicTrajectory;
}
