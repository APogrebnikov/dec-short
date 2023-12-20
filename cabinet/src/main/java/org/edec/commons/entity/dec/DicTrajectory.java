package org.edec.commons.entity.dec;

import lombok.*;

import javax.persistence.*;

/**
 * @author Max Dimukhametov
 */
@Getter
@Setter
@Entity
@Table(name = "curriculum_dic_trajectory")
public class DicTrajectory {
    @Id
    @Column(name = "id_dic_trajectory")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Override
    public String toString () {
        return this.name;
    }
}
