package org.edec.newOrder.ctrl;

import org.edec.newOrder.ctrl.renderer.OrderEditStudentRenderer;
import org.edec.newOrder.model.editOrder.GroupModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.SectionModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.report.ReportService;
import org.edec.newOrder.service.OrderEnsembleService;
import org.edec.newOrder.service.esoImpl.EditOrderService;
import org.edec.newOrder.service.esoImpl.OrderEnsembleServiceImpl;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.newOrder.service.orderCreator.concept.OrderServiceFactory;
import org.edec.rest.ctrl.OrderRestCtrl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.constants.OrderStatusConst;
import org.edec.utility.httpclient.manager.HttpClient;
import org.edec.utility.pdfViewer.model.PdfViewer;
import org.edec.utility.report.service.poi.WordService;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.json.JSONObject;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WinEditOrderCtrl extends CabinetSelector {
    public static final String ORDER_MODEL = "order_model";

    @Wire
    private Button btnSendToServer, btnSaveDesc, btnAddStudents, btnAttachNewFiles, btnRegenerateDocuments,
            btnDeleteSelectedStudents, btnPdfEditor, btnSyncOrder, btnDownloadDoc, btnExportOrder, btnUpdateOrderInfo;

    @Wire
    private Groupbox gbContent;

    @Wire
    private Textbox tbDescOrder;

    private OrderService orderService;
    private OrderEnsembleService orderEnsembleService = new OrderEnsembleServiceImpl();
    private EditOrderService editOrderService = new EditOrderService();

    private OrderEditModel order;
    private boolean readOnly = true;

    @Override
    protected void fill() {
        order = (OrderEditModel) Executions.getCurrent().getArg().get(ORDER_MODEL);
        orderService = OrderServiceFactory.getServiceByRule(order.getIdOrderRule(), FormOfStudy.getFormOfStudyByType(order.getFormOfStudy()), order.getIdInstitute());

        if (orderService.getOrderDocuments().size() > 0) {
            btnRegenerateDocuments.setVisible(true);
        }

        switch (OrderStatusConst.getOrderStatusConstByName(order.getStatus())) {
            case CREATED:
            case REVISION:
                readOnly = false;
                break;
            case EXPORTED:
                btnExportOrder.setVisible(false);
                btnUpdateOrderInfo.setVisible(true);
                enableReadOnlyMode();
                break;
            case SYNCHED:
                enableReadOnlyMode();
                break;
            case AGREED:
                btnSendToServer.setVisible(false);
                btnSyncOrder.setVisible(true);
                enableReadOnlyMode();
                break;
        }

        //Редактор строк и конвертер в док формат
        //в текущем виде не подходит для академической стипендии
        if(order.getIdOrderRule().equals(OrderRuleConst.ACADEMIC_IN_SESSION.getId())
           || order.getIdOrderRule().equals(OrderRuleConst.ACADEMIC_NOT_IN_SESSION.getId())){
            btnPdfEditor.setDisabled(true);
            btnDownloadDoc.setDisabled(true);
        }

        tbDescOrder.setText(order.getDescription());

        fillContent();
    }

    private void enableReadOnlyMode(){
        tbDescOrder.setDisabled(true);
        btnAttachNewFiles.setDisabled(true);
        btnSendToServer.setDisabled(true);
        btnSaveDesc.setDisabled(true);
        btnAddStudents.setDisabled(true);
        btnDeleteSelectedStudents.setDisabled(true);
        btnPdfEditor.setDisabled(true);
        btnExportOrder.setDisabled(true);
        readOnly = true;
    }

    private void fillContent() {
        while (gbContent.getFirstChild() != null) gbContent.removeChild(gbContent.getFirstChild());
        editOrderService.fillOrderModel(order);

        switch (orderService.getGroupingInEditEnum()) {
            case BY_GROUP:
                fillAndGroupByGroup();
                break;
            case BY_SECTION:
                fillAndGroupBySection();
                break;
            case BY_SECTION_WITH_ORDER_FOUNDATION:
                fillAndGroupBySectionWithFoundationForOrder();
                break;
            case BY_SECTION_WITH_STUDENT_FOUNDATION:
                fillAndGroupBySectionWithFoundationForStudent();
                break;
        }
    }

    private void fillAndGroupBySection() {
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
                lbStudent.setHeight("100%");
                lbStudent.setParent(gbSection);
                lbStudent.setModel(new ListModelList<>(group.getStudentModels()));
                lbStudent.setItemRenderer(new OrderEditStudentRenderer(order, this::fillContent, readOnly, section));
                lbStudent.renderAll();
            }
        }
    }

    private void fillAndGroupByGroup() {
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
                lbStudent.setHeight("100%");
                lbStudent.setParent(gbGroup);
                lbStudent.setModel(new ListModelList<>(section.getStudentModels()));
                lbStudent.setItemRenderer(new OrderEditStudentRenderer(order, this::fillContent, readOnly, section));
                lbStudent.renderAll();
            }
        }
    }

    private void fillAndGroupBySectionWithFoundationForOrder() {
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
                lbStudent.setHeight("100%");
                lbStudent.setParent(gbSection);
                lbStudent.setModel(new ListModelList<>(group.getStudentModels()));
                lbStudent.setItemRenderer(new OrderEditStudentRenderer(order, this::fillContent, readOnly, section));
                lbStudent.renderAll();
            }

            if (section.getName().endsWith("(Бюджет)")) {
                Hbox hbox = new Hbox();
                Label label = new Label("Основание");
                Textbox tb = new Textbox();
                Button btn = new Button("Сохранить");

                if (readOnly) {
                    tb.setDisabled(true);
                    btn.setDisabled(true);
                }

                hbox.appendChild(label);
                hbox.appendChild(tb);
                hbox.appendChild(btn);

                hbox.setStyle("margin-top: 10px");

                tb.setText((section.getFoundationLos() == null || section.getFoundationLos().equals("") ? section.getFoundation() : section.getFoundationLos()));
                tb.setWidth("500px");

                btn.addEventListener(Events.ON_CLICK, event -> {
                    editOrderService.saveFoundation(section.getId(), tb.getText());

                    PopupUtil.showInfo("Изменения сохранены");
                });

                gbSection.appendChild(hbox);
            }
        }
    }

    private void fillAndGroupBySectionWithFoundationForStudent() {
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
                    lbStudent.setHeight("100%");
                    lbStudent.setParent(gbSection);
                    ArrayList<StudentModel> student = new ArrayList<>();
                    student.add(studentModel);
                    lbStudent.setModel(new ListModelList<>(student));
                    lbStudent.setItemRenderer(new OrderEditStudentRenderer(order, this::fillContent, readOnly, section));
                    lbStudent.renderAll();

                    Hbox hbox = new Hbox();
                    Label label = new Label("Основание");
                    Textbox tb = new Textbox();
                    Button btn = new Button("Сохранить");

                    if (readOnly) {
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
                        tb.setText((section.getFoundationLos() == null || section.getFoundationLos().equals("") ? section.getFoundation() : section.getFoundationLos()));
                    }

                    tb.setWidth("500px");

                    btn.addEventListener(Events.ON_CLICK, event -> {
                        editOrderService.saveFoundationStudent(studentModel, tb.getText());

                        PopupUtil.showInfo("Изменения сохранены");
                    });

                    gbSection.appendChild(hbox);
                }
            }
        }
    }

    @Listen("onClick = #btnDeleteSelectedStudents")
    public void deleteSelectedStudents() {
        if (order.getGroups() == null || order.getGroups().size() == 0) {
            for (SectionModel sectionModel : order.getSections()) {
                for(GroupModel groupModel : sectionModel.getGroups()) {
                    for (StudentModel studentModel : groupModel.getStudentModels()) {
                        if (studentModel.isSelected()) {
                            this.editOrderService.deleteStudents(studentModel);
                        }
                    }
                }
            }
        } else {
            for (GroupModel groupModel : order.getGroups()) {
                for(SectionModel sectionModel : groupModel.getSections()) {
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


    public void showWinEditCtrl(OrderEditModel tempOrder) {
        Map arg = new HashMap();
        arg.put(ORDER_MODEL, tempOrder);
        ComponentHelper.createWindow("/newOrder/winEditOrder.zul", "winEditOrder", arg).doModal();
    }

    @Listen("onClick = #btnAttachNewFiles")
    public void attachNewFiles() {
        Map arg = new HashMap();
        arg.put(ORDER_MODEL, order);
        ComponentHelper.createWindow("/newOrder/winAttachNewFiles.zul", "winAttachNewFiles", arg).doModal();
    }

    @Listen("onClick = #btnShowPreviewPdf")
    public void showOrder() {
        JasperReport jasperReport = new ReportService().getJasperForOrder(order.getIdOrderRule(), order.getIdOrder());
        jasperReport.showPdf();
    }

    @Listen("onClick = #btnPdfEditor")
    public void openPdfEditor(){
        JasperReport jasperReport = new ReportService().getJasperForOrder(order.getIdOrderRule(), order.getIdOrder());
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
            //Костыль для приказа на ПГАС чтобы можно было одновременно сформировать и приказ на ГАС
            if(order.getIdOrderRule() == OrderRuleConst.ACADEMIC_INCREASED.getId()) {
                editOrderService.updateStudentsForAcademicIncreased(order.getIdOrder());
            }
            PopupUtil.showInfo("Бизнес-процесс успешно запущен!");
            tbDescOrder.setDisabled(true);
            btnSendToServer.setDisabled(true);
            btnSaveDesc.setDisabled(true);
            btnAddStudents.setDisabled(true);
            btnRegenerateDocuments.setDisabled(true);
            readOnly = true;
        } else {
            PopupUtil.showError("Не удалось отправить на сервер, обратитесь к администратору!");
        }
        Clients.clearBusy();
    }

    @Listen("onClick = #btnSaveDesc")
    public void onClickSaveDescOrder() {
        orderService.getEditCreatedOrderService().updateOrderDesc(tbDescOrder.getText(), order.getIdOrder().longValue());
        order.setDescription(tbDescOrder.getText());
        PopupUtil.showInfo("Описание сохранено");
    }

    @Listen("onClick = #btnRegenerateDocuments")
    public void onClickBtnRegenerateDocuments() {
        Map arg = new HashMap();
        arg.put(WinRegenerateDocumentsCtrl.ORDER_MODEL, order);

        ComponentHelper.createWindow("/newOrder/winRegenerateDocuments.zul", "winRegenerateDocuments", arg).doModal();
    }

    @Listen("onClick = #btnAddStudents")
    public void onClickBtnAddStudents() {
        Map arg = new HashMap();
        arg.put(WinAddStudentCtrl.ORDER, order);
        arg.put(WinAddStudentCtrl.UPDATE_UI, (Runnable) this::fillContent);
        arg.put(WinAddStudentCtrl.ORDER_SERVICE, orderService);
        ComponentHelper.createWindow("/newOrder/winAddStudent.zul","winAddStudents", arg).doModal();
    }

    @Listen("onClick = #btnSyncOrder")
    public void onClickBtnSyncOrder(){
        Map arg = new HashMap();
        arg.put(WinFillOrderInfoCtrl.FILL_ORDER_INFO, (BiConsumer<String,Date>) this::updateAndSyncOrder);
        ComponentHelper.createWindow("/newOrder/winFillOrderInfo.zul","winFillOrderInfo", arg).doModal();
    }

    private void updateAndSyncOrder(String number, Date date){
        order.setNumber(number);
        order.setDatesign(date);

        editOrderService.updateOrderInfo(order);

        editOrderService.syncOrder(order.getIdOrder());
    }

    @Listen("onClick =#btnDownloadDoc")
    public void downloadOrderDoc(){
        new WordService().downloadDocx(order.getIdOrderRule(), order.getIdOrder());
    }

    @Listen("onClick = #btnExportOrder")
    public void onClickExportOrder(){
        Clients.showBusy("Экспорт проекта приказа в шахты...");
        Events.echoEvent("onLater", btnExportOrder, null);
    }

    @Listen("onLater = #btnExportOrder")
    public void exportOrder(){
        editOrderService.exportProject(order.getIdOrder());

        Clients.clearBusy();
    }

    @Listen("onClick = #btnUpdateOrderInfo")
    public void onCluckUpdateOrderInfo(){
        Map arg = new HashMap();
        arg.put(WinFillOrderInfoCtrl.FILL_ORDER_INFO, (BiConsumer<String,Date>) this::updateOrderInfo);
        arg.put(WinFillOrderInfoCtrl.ORDER_NUMBER, order.getNumber());
        arg.put(WinFillOrderInfoCtrl.ORDER_DATE, order.getDatesign());
        ComponentHelper.createWindow("/newOrder/winFillOrderInfo.zul","winFillOrderInfo", arg).doModal();
    }

    private void updateOrderInfo(String number, Date date){
        order.setNumber(number);
        order.setDatesign(date);

        editOrderService.updateOrderInfo(order);
        PopupUtil.showInfo("Данные о приказе успешно обновлены!");
    }

}
