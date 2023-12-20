package org.edec.orderAttach.ctrl;

import org.edec.commons.model.OrderAttachModel;
import org.edec.orderAttach.model.OrderWithAttachModel;
import org.edec.orderAttach.service.OrderAttachService;
import org.edec.orderAttach.service.impl.OrderAttachImpl;
import org.edec.orderAttach.service.impl.SignOrderAttachImpl;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.httpclient.manager.ReportHttpClient;
import org.edec.utility.httpclient.manager.ReportHttpService;
import org.edec.utility.pdfViewer.ctrl.WinSignFilesCtrl;
import org.edec.utility.pdfViewer.model.PdfViewer;
import org.edec.utility.zk.CabinetSelector;
import org.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.File;

public class IndexPageCtrl extends CabinetSelector {
    //Компоненты
    @Wire
    private Vlayout vlOrder;

    //Сервисы
    private OrderAttachService orderAttachService = new OrderAttachImpl();
    private ReportHttpService reportHttpService = new ReportHttpService();

    //Переменные
    private String currentCertNumber, currentSerialNumber;

    @Override
    protected void fill() throws InterruptedException {
        callLazyLoadingVlOrder();
    }

    private void callLazyLoadingVlOrder() {
        Clients.showBusy(vlOrder, "Загрузка данных");
        Events.echoEvent("onLater", vlOrder, null);
    }

    @Listen("onLater = #vlOrder")
    public void lazyLoadingOnVlOrder() {
        for (OrderWithAttachModel order : orderAttachService.getOrderList()) {
            createOrderGroupbox(order);
        }
        Clients.clearBusy(vlOrder);
    }

    private void createOrderGroupbox(OrderWithAttachModel order) {
        Groupbox gbOrder = new Groupbox();
        gbOrder.setParent(vlOrder);
        gbOrder.setMold("3d");

        Caption captionOrder = new Caption("Приказ (" + order.getDescription() + ") от " + DateConverter.convertDateToString(order.getDateCreated()) +
                (order.getNumber() == null ? "" : " номер " + order.getNumber()));
        captionOrder.setParent(gbOrder);
        changeStyleGroupbox(captionOrder, order);

        Vbox vbContent = new Vbox();
        vbContent.setParent(gbOrder);

        Button btnSignAll = new Button("Подписать все");
        ;
        if (order.getSignedStatus() != OrderWithAttachModel.SignedStatus.ALL_SIGNED) {
            btnSignAll.setParent(vbContent);
            btnSignAll.addEventListener(Events.ON_CLICK, event -> {
                WinSignFilesCtrl.showWindow(orderAttachService.generateSignModelsByAttaches(order));
            });
        }

        createListboxForAttaches(vbContent, order);

        order.clearListeners();
        order.addListener(() -> {
            changeStyleGroupbox(captionOrder, order);
            Component foundComponent = vbContent.getChildren().stream()
                    .filter(component -> component instanceof Listbox)
                    .findFirst().orElse(null);
            vbContent.getChildren().remove(foundComponent);
            if (order.getSignedStatus() == OrderWithAttachModel.SignedStatus.ALL_SIGNED) {
                vbContent.getChildren().remove(btnSignAll);
            }
            createListboxForAttaches(vbContent, order);
        });

    }

    private void createListboxForAttaches(Vbox vbContent, OrderWithAttachModel order) {
        Listbox lbDocument = new Listbox();
        lbDocument.setParent(vbContent);
        Listhead lhDocument = new Listhead();
        lhDocument.setParent(lbDocument);

        Listheader lhrDocument = new Listheader("Документ");
        lhrDocument.setParent(lhDocument);
        lhrDocument.setHflex("1");

        Listheader lhrAction = new Listheader("");
        lhrAction.setParent(lhDocument);
        lhrAction.setWidth("150px");

        lbDocument.setItemRenderer((ListitemRenderer<OrderAttachModel>) (item, data, index) -> {
            item.setValue(data);

            new Listcell(data.getFileName() != null ? data.getFileName() : data.getJSONData().getString(ReportHttpClient.FILE_NAME)).setParent(item);

            Listcell lcAction = new Listcell();
            lcAction.setParent(item);

            Button btnAction = new Button();
            btnAction.setParent(lcAction);
            if (data.getCertNumber() == null && data.getFileName() == null) {
                btnAction.setLabel("Подписать");
                btnAction.setStyle("background: #FFFE7E;");
                btnAction.addEventListener(Events.ON_CLICK, event -> {
                    PdfViewer pdfViewer = new PdfViewer(
                            new SignOrderAttachImpl(data, order),
                            reportHttpService.getReport(
                                    data.getJSONData().getString(ReportHttpClient.FILE_NAME),
                                    data.getTypeReport(),
                                    new JSONObject(data.getJSONData().getString(ReportHttpClient.DATA))),
                            data.getJSONData().getString(ReportHttpClient.FILE_NAME));
                    pdfViewer.showPdf();
                });
            } else {
                btnAction.setLabel("Показать PDF");
                btnAction.addEventListener(Events.ON_CLICK, event -> {
                    PdfViewer pdfViewer = new PdfViewer(order.getUrl() + File.separator + "attach" + File.separator + data.getFileName());
                    pdfViewer.showPdf();
                });
            }
        });
        lbDocument.setModel(new ListModelList<>(order.getOrderAttaches()));
        lbDocument.renderAll();

    }

    private void changeStyleGroupbox(Caption caption, OrderWithAttachModel order) {
        if (order.getSignedStatus() == OrderWithAttachModel.SignedStatus.SOME_SIGNED) {
            caption.setStyle("background: #FFFE7E;");
        } else if (order.getSignedStatus() == OrderWithAttachModel.SignedStatus.NONE_SIGNED) {
            caption.setStyle("background: #FF7373;");
        } else {
            caption.setStyle("background: 0;");
        }
    }
}
