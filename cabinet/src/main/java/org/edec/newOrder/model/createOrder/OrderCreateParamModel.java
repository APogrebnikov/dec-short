package org.edec.newOrder.model.createOrder;

import org.edec.newOrder.model.enums.ComponentEnum;
import org.zkoss.zul.Span;
import org.zkoss.zul.impl.LabelImageElement;

public class OrderCreateParamModel {
    /**
     * Подпись к параметру приказа
     */
    private String labelParam;

    /**
     * Визуальный элемент
     */
    private ComponentEnum uiElement;

    private boolean isRequired = true;

    public OrderCreateParamModel () {

    }

    public OrderCreateParamModel (String labelParam, ComponentEnum uiElement, boolean isRequired) {
        this.labelParam = labelParam;
        this.uiElement = uiElement;
        this.isRequired = isRequired;
    }

    public String getLabelParam () {
        return labelParam;
    }

    public void setLabelParam (String labelParam) {
        this.labelParam = labelParam;
    }

    public ComponentEnum getUiElement () {
        return uiElement;
    }

    public void setUiElement (ComponentEnum uiElement) {
        this.uiElement = uiElement;
    }
}
