package org.edec.synchroMine.model.eso.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Max Dimukhametov
 */
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "order_head")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order_head")
    private Long id;

    @Column
    private Date dateOfBegin;
    @Column
    private Date dateOfEnd;
    @Column
    private Date dateOfFinish;

    /**
     * Человек, кто сейчас подписывает приказ
     */
    @Column(name = "current_hum")
    private Long currentHum;
    @Column(name = "semester")
    private Long idSem;
    /**
     * Идентификатор приказа из шахт
     */
    @Column
    private Long otherdbid;

    @Column(name = "attr1")
    private String attr1;
    @Column
    private String certFio;
    @Column
    private String certNumber;
    @Column(name = "descriptionspec")
    private String description;
    @Column(name = "lotus_id")
    private String lotusID;
    @Column
    private String note;
    @Column(name = "ordernumber")
    private String number;
    @Column
    private String operation;
    @Column(name = "order_url")
    private String url;

    @ManyToOne
    @JoinColumn(name = "id_humanface")
    private HumanFace humanFaceCreated;
    @ManyToOne
    @JoinColumn(name = "id_order_rule")
    private OrderRule orderRule;
    @ManyToOne
    @JoinColumn(name = "id_order_status_type")
    private OrderStatusType orderStatusType;
}