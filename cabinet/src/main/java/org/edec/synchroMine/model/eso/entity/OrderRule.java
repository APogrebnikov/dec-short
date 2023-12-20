package org.edec.synchroMine.model.eso.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author Max Dimukhametov
 */
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "order_rule")
public class OrderRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order_rule")
    private Long id;

    @Column(name = "form_of_control")
    private Integer formOfControl;

    @Column
    private String description;
    @Column
    private String description2;
    @Column(name = "head_description")
    private String headDescription;
    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "id_institute")
    private Institute institute;

    @ManyToOne
    @JoinColumn(name = "id_order_type")
    private OrderType orderType;
}