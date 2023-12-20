package org.edec.order.ctrl;

import org.edec.newOrder.ctrl.WinFillOrderInfoCtrl;
import org.edec.newOrder.report.OrderLineService;
import org.edec.newOrder.report.ReportService;
import org.edec.newOrder.report.service.OrderReportFillService;
import org.edec.order.ctrl.delegate.WinEditOrderCtrlDelegate;
import org.edec.order.ctrl.renderer.StudentOrderRenderer;
import org.edec.order.model.GroupModel;
import org.edec.order.model.OrderModel;
import org.edec.order.model.SectionModel;
import org.edec.order.model.StudentModel;
import org.edec.order.service.EditOrderService;
import org.edec.order.service.OrderEnsembleService;
import org.edec.order.service.impl.EditOrderServiceESO;
import org.edec.order.service.impl.OrderEnsembleServiceImpl;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.constants.OrderStatusConst;
import org.edec.utility.constants.OrderTypeConst;
import org.edec.utility.pdfViewer.model.PdfViewer;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WinEditOrderCtrl extends SelectorComposer<Window> {
    public static final String ORDER_MODEL = "order_model";

    @Wire
    private Button btnSendToServer, btnSaveDesc, btnAddStudents, btnAttachNewFiles, btnCreateNewServiceNote, btnDeleteSelectedStudents, btnPdfEditor, btnSyncOrder;

    @Wire
    private Groupbox gbContent;

    @Wire
    private Textbox tbDescOrder;

    private EditOrderService editOrderService = new EditOrderServiceESO();
    private OrderEnsembleService orderEnsembleService = new OrderEnsembleServiceImpl();
    private OrderLineService orderLineService = new OrderLineService();
    private OrderReportFillService orderReportFillService = new OrderReportFillService();

    private OrderModel order;

    private boolean readOnly = true;

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);
        order = (OrderModel) Executions.getCurrent().getArg().get(ORDER_MODEL);

        switch (OrderStatusConst.getOrderStatusConstByName(order.getStatus())) {
            case CREATED:
            case REVISION:
                readOnly = false;
                break;
            case AGREED:
                btnSendToServer.setVisible(false);
                btnSyncOrder.setVisible(true);
            default:
                tbDescOrder.setDisabled(true);
                btnAttachNewFiles.setDisabled(true);
                btnSendToServer.setDisabled(true);
                btnSaveDesc.setDisabled(true);
                btnAddStudents.setDisabled(true);
                btnPdfEditor.setDisabled(true);
                btnCreateNewServiceNote.setDisabled(true);
                btnDeleteSelectedStudents.setDisabled(true);

                readOnly = true;
                break;
        }

        if (order.getIdOrderRule().equals(OrderRuleConst.TRANSFER_PROLONGATION.getId()) ||
            order.getIdOrderRule().equals(OrderRuleConst.SET_ELIMINATION_NOT_RESPECTFUL.getId())) {
            btnCreateNewServiceNote.setVisible(true);

            if (order.getIdOrderRule().equals(OrderRuleConst.SET_ELIMINATION_NOT_RESPECTFUL.getId())) {
                btnCreateNewServiceNote.setLabel("Предст. директора");
            }
        }

        //Редактор строк в текущем виде не подходит для академической стипендии
        if(order.getIdOrderRule().equals(OrderRuleConst.ACADEMIC_IN_SESSION.getId())
           || order.getIdOrderRule().equals(OrderRuleConst.ACADEMIC_NOT_IN_SESSION.getId())){
            btnPdfEditor.setDisabled(true);
        }

        tbDescOrder.setText(order.getDescription());

        fillContent();
    }

    public void fillContent() {
        while (gbContent.getFirstChild() != null) {
            gbContent.removeChild(gbContent.getFirstChild());
        }
        editOrderService.fillOrderModel(order);
        switch (OrderTypeConst.getByType(order.getOrderType())) {
            case ACADEMIC:
                fillForAcademic();
                break;
            case DEDUCTION:
                fillForDeduction();
                break;
            case SOCIAL:
                fillForSocial();
                break;
            case SOCIAL_INCREASED:
                fillForSocialIncreased();
                break;
            case TRANSFER:
                fillForTransfer();
                break;
            case SET_ELIMINATION_DEBTS:
                fillForSetEliminationDebts();
                break;
            default:
                return;
        }
    }

    private void fillForTransfer() {
        for (SectionModel section : order.getSections()) {
            Groupbox gbSection = new Groupbox();
            gbSection.setMold("3d");
            gbSection.setParent(gbContent);

            Caption captionSection = new Caption(section.getName());
            captionSection.setParent(gbSection);

            for (GroupModel group : section.getGroups()) {
                Label lSection = new Label(group.getName());
                lSection.setParent(gbSection);
                Listbox lbStudent = new Listbox();
                lbStudent.setParent(gbSection);
                lbStudent.setModel(new ListModelList<>(group.getStudentModels()));
                lbStudent.setItemRenderer(new StudentOrderRenderer(order, new WinEditOrderCtrlDelegate(this)));
                lbStudent.renderAll();
            }

            if (section.getName().endsWith("Бюджет, ув. причина)")) {
                Hbox hbox = new Hbox();
                Label label = new Label("Основание");
                Textbox tb = new Textbox();
                Button btn = new Button("Сохранить");

                if (isReadOnly()) {
                    tb.setDisabled(true);
                    btn.setDisabled(true);
                }

                hbox.appendChild(label);
                hbox.appendChild(tb);
                hbox.appendChild(btn);

                hbox.setStyle("margin-top: 10px");

                tb.setText((section.getFoundationLos() == null || section.getFoundationLos().equals("")
                            ? section.getFoundation()
                            : section.getFoundationLos()));
                tb.setWidth("500px");

                btn.addEventListener(Events.ON_CLICK, event -> {
                    editOrderService.saveFoundation(section.getId(), tb.getText());
                    DialogUtil.info("Изменения сохранены");
                });

                gbSection.appendChild(hbox);
            }
        }
    }

    private void fillForSetEliminationDebts() {
        for (SectionModel section : order.getSections()) {
            Groupbox gbSection = new Groupbox();
            gbSection.setMold("3d");
            gbSection.setParent(gbContent);

            Caption captionSection = new Caption(section.getName());
            captionSection.setParent(gbSection);

            for (GroupModel group : section.getGroups()) {
                Label lSection = new Label(group.getName());
                lSection.setParent(gbSection);
                Listbox lbStudent = new Listbox();
                lbStudent.setParent(gbSection);
                lbStudent.setModel(new ListModelList<>(group.getStudentModels()));
                lbStudent.setItemRenderer(new StudentOrderRenderer(order, new WinEditOrderCtrlDelegate(this)));
                lbStudent.renderAll();
            }

            if (section.getName().endsWith("(Бюджет)")) {
                Hbox hbox = new Hbox();
                Label label = new Label("Основание");
                Textbox tb = new Textbox();
                Button btn = new Button("Сохранить");

                if (isReadOnly()) {
                    tb.setDisabled(true);
                    btn.setDisabled(true);
                }

                hbox.appendChild(label);
                hbox.appendChild(tb);
                hbox.appendChild(btn);

                hbox.setStyle("margin-top: 10px");

                tb.setText((section.getFoundationLos() == null || section.getFoundationLos().equals("")
                            ? section.getFoundation()
                            : section.getFoundationLos()));
                tb.setWidth("500px");

                btn.addEventListener(Events.ON_CLICK, event -> {
                    editOrderService.saveFoundation(section.getId(), tb.getText());
                    DialogUtil.info("Изменения сохранены");
                });

                gbSection.appendChild(hbox);
            }
        }
    }

    private void fillForAcademic() {
        for (GroupModel group : order.getGroups()) {
            Groupbox gbGroup = new Groupbox();
            gbGroup.setMold("3d");
            gbGroup.setParent(gbContent);

            Caption captionGroup = new Caption(group.getName());
            captionGroup.setParent(gbGroup);

            for (SectionModel section : group.getSections()) {
                Label lSection = new Label(section.getName());
                lSection.setParent(gbGroup);
                Listbox lbStudent = new Listbox();
                lbStudent.setParent(gbGroup);
                lbStudent.setModel(new ListModelList<>(section.getStudentModels()));
                lbStudent.setItemRenderer(new StudentOrderRenderer(order, new WinEditOrderCtrlDelegate(this)));
                lbStudent.renderAll();
            }
        }
    }

    private void fillForSocial() {
        for (SectionModel section : order.getSections()) {
            Groupbox gbSection = new Groupbox();
            gbSection.setMold("3d");
            gbSection.setParent(gbContent);

            Caption captionGroup = new Caption(section.getName());
            captionGroup.setParent(gbSection);

            for (GroupModel group : section.getGroups()) {
                Label lGroup = new Label(group.getName());
                lGroup.setParent(gbSection);
                Listbox lbStudent = new Listbox();
                lbStudent.setParent(gbSection);
                lbStudent.setModel(new ListModelList<>(group.getStudentModels()));
                lbStudent.setItemRenderer(new StudentOrderRenderer(order, new WinEditOrderCtrlDelegate(this)));
                lbStudent.renderAll();
            }
        }
    }

    private void fillForSocialIncreased() {
        for (SectionModel section : order.getSections()) {
            Groupbox gbSection = new Groupbox();
            gbSection.setMold("3d");
            gbSection.setParent(gbContent);

            Caption captionSection = new Caption(section.getName());
            captionSection.setParent(gbSection);

            for (GroupModel group : section.getGroups()) {
                Label lSection = new Label(group.getName());
                lSection.setParent(gbSection);
                Listbox lbStudent = new Listbox();
                lbStudent.setParent(gbSection);
                lbStudent.setModel(new ListModelList<>(group.getStudentModels()));
                lbStudent.setItemRenderer(new StudentOrderRenderer(order, new WinEditOrderCtrlDelegate(this), section));
                lbStudent.renderAll();
            }
        }
    }

    private void fillForDeduction() {
        for (SectionModel section : order.getSections()) {
            Groupbox gbSection = new Groupbox();
            gbSection.setMold("3d");
            gbSection.setParent(gbContent);

            Caption captionSection = new Caption(section.getName());
            captionSection.setParent(gbSection);

            for (GroupModel group : section.getGroups()) {
                Label lGroup = new Label(group.getName());
                lGroup.setParent(gbSection);

                for (StudentModel studentModel : group.getStudentModels()) {
                    Listbox lbStudent = new Listbox();
                    lbStudent.setParent(gbSection);
                    ArrayList<StudentModel> student = new ArrayList<>();
                    student.add(studentModel);
                    lbStudent.setModel(new ListModelList<>(student));
                    lbStudent.setItemRenderer(new StudentOrderRenderer(order, new WinEditOrderCtrlDelegate(this)));
                    lbStudent.renderAll();

                    Hbox hbox = new Hbox();
                    Label label = new Label("Основание");
                    Textbox tb = new Textbox();
                    Button btn = new Button("Сохранить");

                    if (isReadOnly()) {
                        tb.setDisabled(true);
                        btn.setDisabled(true);
                    }

                    hbox.appendChild(label);
                    hbox.appendChild(tb);
                    hbox.appendChild(btn);

                    hbox.setStyle("margin-top: 10px");

                    if (studentModel.getFoundation() != null && !studentModel.getFoundation().equals("")) {
                        tb.setText(studentModel.getFoundation());
                    } else {
                        tb.setText((section.getFoundationLos() == null || section.getFoundationLos().equals("")
                                    ? (studentModel.getIsGovernmentFinanced()
                                       ? section.getFoundation()
                                       : section.getFoundation().replace(".", "; расчет фактически израсходованных средств."))
                                    : section.getFoundationLos()));
                    }

                    tb.setWidth("500px");

                    btn.addEventListener(Events.ON_CLICK, event -> {
                        editOrderService.saveFoundationStudent(studentModel, tb.getText());
                        orderLineService.updateOrderLines(order.getIdOrder(), orderReportFillService.getLines(order.getIdOrder()));

                        PopupUtil.showInfo("Изменения сохранены");
                    });

                    gbSection.appendChild(hbox);
                }
            }
        }
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    @Listen("onClick = #btnDeleteSelectedStudents")
    public void deleteSelectedStudents() {
        if (order.getGroups() == null || order.getGroups().size() == 0) {
            for (SectionModel sectionModel : order.getSections()) {
                for (GroupModel groupModel : sectionModel.getGroups()) {
                    for (StudentModel studentModel : groupModel.getStudentModels()) {
                        if (studentModel.isSelected()) {
                            this.editOrderService.deleteStudents(studentModel);
                        }
                    }
                }
            }
        } else {
            for (GroupModel groupModel : order.getGroups()) {
                for (SectionModel sectionModel : groupModel.getSections()) {
                    for (StudentModel studentModel : sectionModel.getStudentModels()) {
                        if (studentModel.isSelected()) {
                            this.editOrderService.deleteStudents(studentModel);
                        }
                    }
                }
            }
        }

        fillContent();
    }

    @Listen("onClick = #btnAttachedFiles")
    public void showAttachedFile() {
        PdfViewer pdfViewer = new PdfViewer(order.getUrl());
        pdfViewer.showDirectory();
    }

    @Listen("onClick = #btnAttachNewFiles")
    public void attachNewFiles() {
        Map arg = new HashMap();
        arg.put(ORDER_MODEL, order);
        ComponentHelper.createWindow("/order/winAttachNewFiles.zul", "winAttachNewFiles", arg).doModal();
    }

    @Listen("onClick = #btnShowPreviewPdf")
    public void showOrder() {
        JasperReport jasperReport = new ReportService().getJasperForOrder(order.getIdOrder());
        jasperReport.showPdf();
    }

    @Listen("onClick = #btnPdfEditor")
    public void showPdfEditor(){
        JasperReport jasperReport = new ReportService().getJasperForOrder(order.getIdOrder());
        jasperReport.showPdfEditor();
    }

    @Listen("onClick = #btnSendToServer")
    public void startEnsembleProcess() {
        if (order.getDescription().equals("")) {
            PopupUtil.showWarning("Заполните описание!");
            return;
        }

        if (order.getDescription().replace('\\','/').matches("[/*?\"<>|!]")){
            PopupUtil.showWarning("Описание не должно содержать запрещенные символы /\\:*?\"<>|!");
            return;
        }

        Clients.showBusy("Ожидайте запуска бизнес-процесса...");
        Events.echoEvent("onLater", btnSendToServer, null);
    }

    @Listen("onLater = #btnSendToServer")
    public void onLaterBtnSendToServer() {
        if (orderEnsembleService.sendOrderToEnsemble(order)) {
            PopupUtil.showInfo("Бизнес-процесс успешно запущен!");
            tbDescOrder.setDisabled(true);
            btnSendToServer.setDisabled(true);
            btnSaveDesc.setDisabled(true);
            btnAddStudents.setDisabled(true);
            readOnly = true;
        } else {
            PopupUtil.showError("Не удалось отправить на сервер, обратитесь к администратору!");
        }
        Clients.clearBusy();
    }

    public void showWinEditCtrl(OrderModel tempOrder) {
        if (Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winEditOrder") != null) {
            Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winEditOrder").detach();
        }
        Map arg = new HashMap();
        arg.put(ORDER_MODEL, tempOrder);
        ComponentHelper.createWindow("/order/winEditOrder.zul", "winEditOrder", arg).doModal();
    }

    @Listen("onClick = #btnAddStudents")
    public void onClickBtnAddStudents() {
        if (Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winAddStudents") != null) {
            Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winAddStudents").detach();
        }
        Map arg = new HashMap();
        arg.put("order", order);
        arg.put("delegate", new WinEditOrderCtrlDelegate(this));
        ComponentHelper.createWindow("/order/winAddStudent.zul", "winAddStudents", arg).doModal();
    }

    @Listen("onClick = #btnSaveDesc")
    public void onClickSaveDescOrder() {
        editOrderService.updateOrderDesc(tbDescOrder.getText(), order.getIdOrder().longValue());
        order.setDescription(tbDescOrder.getText());
        PopupUtil.showInfo("Описание сохранено");
    }

    @Listen("onClick = #btnCreateNewServiceNote")
    public void onClickCreateNewServiceNote() {
        Map arg = new HashMap();
        arg.put("order", order);
        ComponentHelper.createWindow("/order/winConfirmServiceNoteDate.zul", "winConfirmServiceNoteDate", arg).doModal();
    }

    @Listen("onClick = #btnSyncOrder")
    public void onClickBtnSyncOrder(){
        Map arg = new HashMap();
        arg.put(WinFillOrderInfoCtrl.FILL_ORDER_INFO, (BiConsumer<String, Date>) this::updateAndSyncOrder);
        ComponentHelper.createWindow("/newOrder/winFillOrderInfo.zul","winFillOrderInfo", arg).doModal();
    }

    private void updateAndSyncOrder(String number, Date date){
        order.setNumber(number);
        order.setDatesign(date);

        editOrderService.updateOrderInfo(order);

        editOrderService.syncOrder(order.getIdOrder());

    }
}

