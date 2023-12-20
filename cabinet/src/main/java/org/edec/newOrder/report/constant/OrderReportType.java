package org.edec.newOrder.report.constant;

import lombok.Getter;

@Getter
public enum OrderReportType {
    SEPARATE_BY_PAGES(1), CONTINUOUS(2);

    private Integer value;

    OrderReportType(Integer value) {
        this.value = value;
    }

    public static OrderReportType getLineTypeByValue(Integer value) {
        for(OrderReportType type : OrderReportType.values()) {
            if(type.value.equals(value)) {
                return type;
            }
        }

        return null;
    }
}
