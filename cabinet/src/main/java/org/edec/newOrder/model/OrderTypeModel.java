package org.edec.newOrder.model;

/**
 * Created by dmmax
 */
public class OrderTypeModel {
    private Long idType;

    private String name;

    public OrderTypeModel (Long idType, String name) {
        this.idType = idType;
        this.name = name;
    }

    public OrderTypeModel () {
    }

    public Long getIdType () {
        return idType;
    }

    public void setIdType (Long idType) {
        this.idType = idType;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }
}
