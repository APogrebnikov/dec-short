package org.edec.synchroMine.model.mine;

import javax.persistence.*;


@Entity
@Table(name = "[ПриказыВидыДействий]")
public class OrderActionType {
    @Id
    @Column(name = "Код")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "Шаблон")
    private String template;

    public OrderActionType () {
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public String getTemplate () {
        return template;
    }

    public void setTemplate (String template) {
        this.template = template;
    }
}
