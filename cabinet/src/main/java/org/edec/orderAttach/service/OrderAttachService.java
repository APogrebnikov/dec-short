package org.edec.orderAttach.service;

import org.edec.orderAttach.model.OrderWithAttachModel;
import org.edec.utility.pdfViewer.SimpleSignModel;

import java.util.List;

public interface OrderAttachService {
    List<OrderWithAttachModel> getOrderList();
    List<SimpleSignModel> generateSignModelsByAttaches(OrderWithAttachModel order);
}
