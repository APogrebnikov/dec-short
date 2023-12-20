package org.edec.newOrder.model.addStudent;

public class LinkOrderSectionEditModel {
    private Long idLOS;
    private Long idOS;
    private String name;

    public LinkOrderSectionEditModel (Long idLOS, Long idOS, String name) {
        this.idLOS = idLOS;
        this.name = name;
        this.idOS = idOS;
    }

    public Long getIdLOS () {
        return idLOS;
    }

    public void setIdLOS (Long idLOS) {
        this.idLOS = idLOS;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public Long getIdOS () {
        return idOS;
    }

    public void setIdOS (Long idOS) {
        this.idOS = idOS;
    }
}
