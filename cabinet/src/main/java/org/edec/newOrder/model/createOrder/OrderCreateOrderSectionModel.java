package org.edec.newOrder.model.createOrder;

public class OrderCreateOrderSectionModel {
    private Long id;
    private String name;
    private String foundation;

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getFoundation () {
        return foundation;
    }

    public void setFoundation (String foundation) {
        this.foundation = foundation;
    }
}
