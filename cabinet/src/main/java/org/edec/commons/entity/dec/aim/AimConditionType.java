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
@Table(name = "aim_condition_type")*/ public class AimConditionType {
    @Id
    @Column(name = "id_aim_condition_type")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(name = "type_value")
    @Enumerated(EnumType.ORDINAL)
    private TypeValue typeValue;

    public enum TypeValue {
        TEXT, BOOLEAN, INT, FLOAT, DATE
    }
}
