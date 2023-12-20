package org.edec.commons.entity.dec.aim;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
@Getter
@Setter
@NoArgsConstructor
/*@Entity
@Table(name = "aim_set")*/ public class AimSet {

    @Id
    @Column(name = "id_aim_set")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "begin_date")
    private Date beginDate;
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "increasing_ratio")
    private Float increasingRatio = 1F;
    @Column(name = "start_point")
    private Integer startPoint;
    @Column(name = "id_last_check_achievement")
    private Long idLastAchievement;

    @Enumerated(EnumType.ORDINAL)
    private AimSetStatus status = AimSetStatus.STARTED;
    @ManyToOne
    @JoinColumn(name = "id_aim_student")
    private AimStudent aimStudent;
    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<AimStudentTask> tasks = new ArrayList<>();

    public enum AimSetStatus {
        STARTED, CANCELED, FINISHED
    }
}
