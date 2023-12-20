package org.edec.newOrder.model;

/**
 * Created by dmmax
 */
public class OrderStatusModel {
    private Long idStatus;
    private String name;

    public OrderStatusModel (Long idStatus, String name) {
        this.idStatus = idStatus;
        this.name = name;
    }

    public OrderStatusModel () {
    }

    public Long getIdStatus () {
        return idStatus;
    }

    public void setIdStatus (Long idStatus) {
        this.idStatus = idStatus;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }
}
