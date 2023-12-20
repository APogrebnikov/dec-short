package org.edec.newOrder.report.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.newOrder.report.constant.OrderLineType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderLineModel {
    private String lineInfo;
    private Integer lineType, lineNumber;
    private Integer linePage = 0;

    private List<OrderLineModel> subOrderLines = new ArrayList<>();

    public OrderLineModel(String lineInfo, Integer lineType) {
        this.lineType = lineType;

        if(lineType.equals(OrderLineType.EMPTY.getValue())) {
            this.lineInfo = "empty";
        } else {
            this.lineInfo = lineInfo;
        }
    }

    public OrderLineModel(){}

}
