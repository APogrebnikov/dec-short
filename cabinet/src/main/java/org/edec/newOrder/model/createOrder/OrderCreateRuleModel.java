package org.edec.newOrder.model.createOrder;

/**
 * Created by antonskripacev on 04.01.17.
 */
public class OrderCreateRuleModel {
    private Long id;
    private Long idOrderType;
    private String name;
    private Boolean isAutomatic;

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public Long getIdOrderType () {
        return idOrderType;
    }

    public void setIdOrderType (Long idOrderType) {
        this.idOrderType = idOrderType;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public Boolean getAutomatic () {
        return isAutomatic;
    }

    public void setAutomatic (Boolean automatic) {
        isAutomatic = automatic;
    }
}
