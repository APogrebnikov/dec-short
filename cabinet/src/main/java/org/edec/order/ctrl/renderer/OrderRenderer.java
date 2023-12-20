package org.edec.order.ctrl.renderer;

import org.edec.order.ctrl.WinEditOrderCtrl;
import org.edec.order.model.OrderModel;
import org.edec.order.service.impl.OrderServiceESOimpl;
import org.edec.utility.constants.OrderStatusConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.pdfViewer.model.PdfViewer;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;


public class OrderRenderer implements ListitemRenderer<OrderModel> {
    private Boolean isReadOnly;

    public OrderRenderer(Boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    @Override
    public void render(final Listitem li, final OrderModel data, int index) throws Exception {
        li.setValue(data);

        new Listcell(String.valueOf(li.getListbox().getListModel().getSize() - index)).setParent(li);
        new Listcell(data.getNumber()).setParent(li);
        if (isReadOnly) {
            new Listcell().setParent(li);
            new Listcell().setParent(li);
            new Listcell(data.getDatefinish() == null
                    ? ""
                    : DateConverter.convertDateToString(data.getDatefinish())).setParent(li);
        } else {
            new Listcell(data.getDatesign() == null
                    ? ""
                    : DateConverter.convertDateToString(data.getDatesign())).setParent(li);
            new Listcell(data.getDatecreated() == null
                    ? ""
                    : DateConverter.convertDateToString(data.getDatecreated())).setParent(li);
            new Listcell().setParent(li);
        }
        Listcell lcType = new Listcell(data.getType());
        lcType.setTooltiptext(data.getType());
        lcType.setParent(li);


        Listcell lcDescription = new Listcell(data.getDescription());
        lcDescription.setTooltiptext(data.getDescription());
        //TODO: перенести в стили
        lcDescription.setStyle(lcDescription.getStyle()+"; line-height: 18px !important; font-size: 12px !important;");
        lcDescription.setParent(li);
        String currentStatus = data.getCurrenthumanface() == null
                    ? data.getOperation()
                    : data.getCurrenthumanface();
        new Listcell(data.getStatus().equals("На согласовании")
                ? currentStatus
                : data.getStatus()).setParent(li);


        Listcell lcCountStudents = new Listcell(Long.toString(data.getCountStudents()));
        lcCountStudents.setParent(li);

        Listcell lcBtn = new Listcell();
        lcBtn.setParent(li);
        Button btnPdf = new Button("", "/imgs/pdf.png");
        btnPdf.setParent(lcBtn);
        btnPdf.addEventListener(Events.ON_CLICK, event -> {
            PdfViewer pdfViewer = new PdfViewer(data.getUrl());
            pdfViewer.showDirectory();
        });

        lcBtn = new Listcell();
        lcBtn.setParent(li);
        Button btnEdit = new Button("", "/imgs/edit.png");
        if (!isReadOnly) btnEdit.setParent(lcBtn);
        btnEdit.addEventListener(Events.ON_CLICK, event -> new WinEditOrderCtrl().showWinEditCtrl(data));
        lcBtn = new Listcell();
        lcBtn.setParent(li);

        Button btnDelete = new Button("", "/imgs/del.png");
        if (!isReadOnly) btnDelete.setParent(lcBtn);
        if (data.getStatus().equals(OrderStatusConst.CREATED.getName())) {
            btnDelete.addEventListener(Events.ON_CLICK, event ->
                    Messagebox.show("Вы действительно хотите удалить приказ?", "Удаление приказа",
                            Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, (EventListener) evt -> {
                if (evt.getName().equals("onOK")) {
                    try {
                        new OrderServiceESOimpl().deleteOrder(data);
                        li.getListbox().removeChild(li);
                        PopupUtil.showInfo("Приказ успешно удален");
                    } catch (Exception e) {
                        PopupUtil.showError("Не удалось удалить приказ");
                        System.out.println("Ошибка удаления приказа с id = " + data.getIdOrder());
                    }
                }
            }));
        } else {
            btnDelete.setDisabled(true);
        }
    }
}
