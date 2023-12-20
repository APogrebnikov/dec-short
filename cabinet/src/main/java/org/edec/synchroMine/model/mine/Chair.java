package org.edec.synchroMine.model.mine;

import javax.persistence.*;


@Entity
@Table(name = "Кафедра")
public class Chair {
    @Id
    @Column(name = "Код")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long idInst;

    private String name;
    private String shortname;

    public Chair () {
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public Long getIdInst () {
        return idInst;
    }

    public void setIdInst (Long idInst) {
        this.idInst = idInst;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getShortname () {
        return shortname;
    }

    public void setShortname (String shortname) {
        this.shortname = shortname;
    }
}
