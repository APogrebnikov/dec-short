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
@Table(name = "humanface")
public class HumanFace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_humanface")
    private Long id;

    @Column
    private String email;
    @Column
    private String family;
    @Column
    private String name;
    @Column
    private String patronymic;
}
