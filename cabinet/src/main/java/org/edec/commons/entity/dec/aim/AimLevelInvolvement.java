package org.edec.commons.entity.dec.aim;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * степень участия в каком-либо виде деятельности
 *
 * @author Max Dimukhametov
 */
@Getter
@Setter
@NoArgsConstructor
/*@Entity
@Table(name = "aim_level_involvement")*/ public class AimLevelInvolvement {
    @Id
    @Column(name = "id_aim_level_involvement")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;
}
