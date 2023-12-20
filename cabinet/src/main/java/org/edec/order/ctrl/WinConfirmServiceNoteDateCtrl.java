package org.edec.order.ctrl;

import org.edec.order.model.OrderModel;
import org.edec.utility.constants.OrderTypeConst;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;


public class WinConfirmServiceNoteDateCtrl extends SelectorComposer<Component> {
    private OrderModel order;

    @Wire
    Datebox dateServiceNote;

    @Wire
    Window winConfirmServiceNoteDate;

    @Wire
    Label lblTitle;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        order = (OrderModel) Executions.getCurrent().getArg().get("order");

        if(order.getOrderType().equals(OrderTypeConst.SET_ELIMINATION_DEBTS.getType())) {
            lblTitle.setValue("Представление установление сроков ЛАЗ");
        } else {
            lblTitle.setValue("Служебная продление ЛАЗ");
        }
    }

    @Listen("onClick = #btnCreateNewServiceNote")
    public void onClickCreateNewServiceNote() {
        if(dateServiceNote.getValue() == null) return;

        try {
            FileManager manager = new FileManager();
            JasperReportService reportService = new JasperReportService();
            JasperReport serviceNote = null;

            if(order.getOrderType().equals(OrderTypeConst.TRANSFER.getType())) {
                //TODO исправить год на не статичный
                serviceNote = reportService.getJasperReportForServiceNote(order, dateServiceNote.getValue(), "2017/2018");
            } else if(order.getOrderType().equals(OrderTypeConst.SET_ELIMINATION_DEBTS.getType())) {
                serviceNote = reportService.getJasperReportForSetEliminationNotion(order, dateServiceNote.getValue());
            }


            if(order.getOrderType().equals(OrderTypeConst.SET_ELIMINATION_DEBTS.getType())) {
                manager.createAttachForOrderUrl(order, serviceNote.getFile(), "Служебная записка ЛАЗ.pdf");
            } else {
                manager.createAttachForOrderUrl(order, serviceNote.getFile(), "Служебная продление ЛАЗ.pdf");
            }

            PopupUtil.showInfo("Документ сформирован успешно");

            winConfirmServiceNoteDate.detach();
        } catch (Exception e) {
            PopupUtil.showError("Не удалось сформировать документ");
            e.printStackTrace();
        }
    }
}
