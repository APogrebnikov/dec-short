package org.edec.commons.entity.dec.aim;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
@Getter
@Setter
@NoArgsConstructor
/*@Entity
@Table(name = "aim")*/ public class Aim {
    @Id
    @Column(name = "id_aim")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "begin_date")
    private Date beginDate;
    @Column(name = "end_date")
    private Date endDate;

    @Column
    private String name;

    @Column(name = "min_point")
    private Integer minPoint;

    @Transient
    private Aim parent;

    @OneToMany(mappedBy = "aim")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<AimCondition> conditions;
    @OneToMany(mappedBy = "aim")
    private List<AimHistory> histories;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "aim")
    @OrderBy("point")
    private List<AimTask> tasks;

    @Override
    public String toString () {
        return name + ", min_point: " + minPoint + ", begin_date: " + beginDate + ", end_date: " + endDate;
    }

    public Aim (String name, Date beginDate, Date finishDate, Integer minPoint) {
        this.name = name;
        this.beginDate = beginDate;
        this.endDate = finishDate;
        this.minPoint = minPoint;
    }
}
