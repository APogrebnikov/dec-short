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
@Table(name = "aim_student")*/ public class AimStudent {
    @Id
    @Column(name = "id_aim_student")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "begin_date")
    private Date beginDate;

    @Column(name = "id_studentcard")
    private Long idStudentCard;

    @ManyToOne
    @JoinColumn(name = "id_aim")
    private Aim aim;
    @OneToMany(mappedBy = "aimStudent", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<AimSet> sets = new ArrayList<>();
}
