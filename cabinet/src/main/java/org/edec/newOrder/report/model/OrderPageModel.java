package org.edec.newOrder.report.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.newOrder.model.report.OrderReportEmployeeModel;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderPageModel {

    List<OrderLineModel> orderLines = new ArrayList<>();
    List<OrderReportEmployeeModel> employees;
    String predicatingfio;
    String predicatingpost;

    public OrderPageModel(){
        orderLines.add(new OrderLineModel());
    }

}
