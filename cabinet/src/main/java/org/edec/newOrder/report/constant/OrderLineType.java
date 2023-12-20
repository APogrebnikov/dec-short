package org.edec.newOrder.report.constant;

import lombok.Getter;

@Getter
public enum OrderLineType {
    NO_INDENT(0), RED_LINE_INDENT(1), CENTERED(3), EMPTY(4), CENTERED_LIST(5), BREAK(6);

    private Integer value;

    OrderLineType(Integer value) {
        this.value = value;
    }

    public static OrderLineType getLineTypeByValue(Integer value) {
        for(OrderLineType type : OrderLineType.values()) {
            if(type.value.equals(value)) {
                return type;
            }
        }

        return null;
    }
}
