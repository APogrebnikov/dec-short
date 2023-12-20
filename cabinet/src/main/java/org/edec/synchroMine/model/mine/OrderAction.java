package org.edec.synchroMine.model.mine;

import javax.persistence.*;


@Entity
@Table(name = "[ПриказыДействия]")
public class OrderAction {
    @Id
    @Column(name = "Код")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "Массовое")
    private boolean mass;

    @Column(name = "Номер")
    private Integer number;

    @Column(name = "КодДействия")
    private Long idAction;
    @Column(name = "КодПриказа")
    private Long idOrder;

    @Column(name = "Текст")
    private String text;

    public OrderAction () {
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public boolean isMass () {
        return mass;
    }

    public void setMass (boolean mass) {
        this.mass = mass;
    }

    public Integer getNumber () {
        return number;
    }

    public void setNumber (Integer number) {
        this.number = number;
    }

    public Long getIdAction () {
        return idAction;
    }

    public void setIdAction (Long idAction) {
        this.idAction = idAction;
    }

    public Long getIdOrder () {
        return idOrder;
    }

    public void setIdOrder (Long idOrder) {
        this.idOrder = idOrder;
    }

    public String getText () {
        return text;
    }

    public void setText (String text) {
        this.text = text;
    }
}
