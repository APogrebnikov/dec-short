package org.edec.commons.entity.dec.aim;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
@Getter
@Setter
@NoArgsConstructor
/*@Entity
@Table(name = "aim_task_type")*/ public class AimTaskType {
    @Id
    @Column(name = "id_aim_task_type")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @OneToMany(mappedBy = "taskType")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<AimTaskTime> taskTimes = new ArrayList<>();
}
