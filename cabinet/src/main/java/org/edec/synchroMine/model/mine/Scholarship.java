package org.edec.synchroMine.model.mine;

import javax.persistence.*;


@Entity
@Table(name = "[Стипендия]")
public class Scholarship {
    @Id
    @Column(name = "Код")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "Тип")
    private Integer type;

    @Column(name = "КодБ")
    private String codeB;
    @Column(name = "Название")
    private String name;
    @Column(name = "Сумма")
    private String summ;
    @Column(name = "ТекстДляПриказа")
    private String textForOrder;

    public Scholarship () {
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public Integer getType () {
        return type;
    }

    public void setType (Integer type) {
        this.type = type;
    }

    public String getCodeB () {
        return codeB;
    }

    public void setCodeB (String codeB) {
        this.codeB = codeB;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getSumm () {
        return summ;
    }

    public void setSumm (String summ) {
        this.summ = summ;
    }

    public String getTextForOrder () {
        return textForOrder;
    }

    public void setTextForOrder (String textForOrder) {
        this.textForOrder = textForOrder;
    }
}
