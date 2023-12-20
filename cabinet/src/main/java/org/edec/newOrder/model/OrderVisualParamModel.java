package org.edec.newOrder.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.newOrder.model.enums.ComponentEnum;

@Getter
@Setter
public class OrderVisualParamModel {
    private String name;
    private String listHeaderWidth;
    private ComponentEnum editComponent;

    public OrderVisualParamModel() { }

    public OrderVisualParamModel(String name, ComponentEnum editComponent) {
        this.name = name;
        this.editComponent = editComponent;
        this.listHeaderWidth = "200px";
    }

    public OrderVisualParamModel(String name, ComponentEnum editComponent, String listHeaderWidth) {
        this.name = name;
        this.editComponent = editComponent;
        this.listHeaderWidth = listHeaderWidth;
    }
}
