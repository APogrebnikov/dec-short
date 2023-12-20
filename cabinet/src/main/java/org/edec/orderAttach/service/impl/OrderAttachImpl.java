package org.edec.orderAttach.service.impl;

import org.edec.commons.model.OrderAttachModel;
import org.edec.orderAttach.manager.OrderAttachDAO;
import org.edec.orderAttach.model.OrderWithAttachModel;
import org.edec.orderAttach.model.dao.OrderAttachDAOModel;
import org.edec.orderAttach.service.OrderAttachService;
import org.edec.utility.httpclient.manager.ReportHttpClient;
import org.edec.utility.pdfViewer.SimpleSignModel;
import org.edec.utility.pdfViewer.model.PdfSignRESTFile;
import org.edec.utility.sign.service.SignService;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderAttachImpl implements OrderAttachService {
    private OrderAttachDAO orderAttachDAO = new OrderAttachDAO();

    @Override
    public List<OrderWithAttachModel> getOrderList() {
        return divideModel(orderAttachDAO.getOrderAttaches());
    }

    @Override
    public List<SimpleSignModel> generateSignModelsByAttaches(OrderWithAttachModel order) {
        //Преобразуем лист прикрепленных файлов в модель для подписи
        return order.getOrderAttaches().stream()
                //Нас интересуют только не подписанные файлы
                .filter(orderAttach -> orderAttach.getCertNumber() == null)
                .map(orderAttach -> new SimpleSignModel(
                        //Создаем файл, который будет сгенерирован
                        new PdfSignRESTFile(
                                orderAttach.getJSONData().getString(ReportHttpClient.FILE_NAME),
                                orderAttach.getTypeReport(),
                                new JSONObject(orderAttach.getJSONData().getString(ReportHttpClient.DATA))),
                        //Добавляем сервис, который будет вызван
                        new SignOrderAttachImpl(
                                orderAttach,
                                order)))
                .collect(Collectors.toList());
    }

    private List<OrderWithAttachModel> divideModel(List<OrderAttachDAOModel> orderAttaches) {
        List<OrderWithAttachModel> result = new ArrayList<>();
        for (OrderAttachDAOModel orderAttach : orderAttaches) {
            OrderWithAttachModel order = result.stream()
                    .filter(orderTmp -> orderAttach.getIdOrder().equals(orderTmp.getIdOrder().intValue()))
                    .findFirst()
                    .orElse(null);
            if (order == null) {
                order = new OrderWithAttachModel();
                order.setDateCreated(orderAttach.getDateCreated());
                order.setDescription(orderAttach.getDescription());
                order.setIdOrder(orderAttach.getIdOrder().longValue());
                order.setNumber(orderAttach.getNumber());
                order.setType(orderAttach.getOrderType());
                order.setUrl(orderAttach.getUrl());
                result.add(order);
            }

            order.addAttach(
                    new OrderAttachModel(orderAttach.getIdOrderAttach().longValue(), orderAttach.getCertNumber(), orderAttach.getCertFioAttach(),
                            orderAttach.getFileName(), orderAttach.getParams(), orderAttach.getReportType(),
                            order)
            );
        }
        result.forEach(OrderWithAttachModel::checkStatus);
        return result;
    }
}
