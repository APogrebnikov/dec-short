package org.edec.order.ctrl.visualCreate;

import org.edec.order.ctrl.AddFilesToOrderAttachCtrl;
import org.edec.order.ctrl.WinCreateOrderCtrl;
import org.edec.order.ctrl.delegate.IndexPageCtrlDelegate;
import org.edec.order.model.OrderModel;
import org.edec.order.model.OrderRuleModel;
import org.edec.order.model.StudentToAddModel;
import org.edec.order.service.CreateOrderService;
import org.edec.order.service.impl.CreateOrderServiceESO;
import org.edec.order.service.orderCreator.OrderCreator;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.OrderTypeConst;
import org.edec.utility.fileManager.FileModel;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.edec.order.ctrl.WinCreateOrderCtrl.*;


public class VisualDeduction extends VisualAbstract {
    private CreateOrderService service = new CreateOrderServiceESO();
    private List<StudentToAddModel> listStudentsToAdd = new ArrayList<>();
    private List<Media> listMedia = new ArrayList<>();
    public WinCreateOrderCtrl ctrl;

    public VisualDeduction(Window winCreateOrder, IndexPageCtrlDelegate delegate, OrderRuleModel rule, Long currentSem,
                           InstituteModel institute, FormOfStudy formOfStudy) {
        super(winCreateOrder, delegate, rule, currentSem, institute, formOfStudy);
        this.ctrl = ctrl;
    }

    @Override
    public void draw(Component component) {
        while (component.getFirstChild() != null) {
            component.removeChild(component.getFirstChild());
        }

        final Hlayout hlBtnAddStudent = new Hlayout();
        final Listbox lbForStudent = new Listbox();
        lbForStudent.setWidth("600px");
        lbForStudent.appendChild(new Listhead());
        Listheader lh = new Listheader();
        Label lb = new Label("ФИО");
        lb.setSclass("cwf-listheader-label");
        lb.setParent(lh);
        lbForStudent.getListhead().appendChild(lh);
        lh = new Listheader();
        lb = new Label("Дата отчисления");
        lb.setSclass("cwf-listheader-label");
        lb.setParent(lh);
        lbForStudent.getListhead().appendChild(lh);
        if (formOfStudy.equals(FormOfStudy.FULL_TIME)) {
            lh = new Listheader();
            lb = new Label("Прекращение выплат");
            lb.setSclass("cwf-listheader-label");
            lb.setParent(lh);
            lbForStudent.getListhead().appendChild(lh);
        }
        Listheader delete = new Listheader("");
        delete.setWidth("30px");
        lbForStudent.getListhead().appendChild(delete);

        final Button addStudent = new Button("Добавить студентов");
        addStudent.setStyle("margin-left: 10px");
        addStudent.setStyle("width: 300px");
        addStudent.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Map arg = new HashMap();
                arg.put("listbox", lbForStudent);
                arg.put(INSTITUTE_MODEL, institute);
                arg.put(FORM_OF_STUDY, formOfStudy);
                arg.put(ORDER_TYPE, OrderTypeConst.DEDUCTION);
                arg.put("students", listStudentsToAdd);
                ComponentHelper.createWindow("winAddStudentManualy.zul", "winAddStudentManualy", arg).doModal();
            }
        });
        lbForStudent.setVisible(true);
        hlBtnAddStudent.appendChild(addStudent);

        final Hlayout hlBtnAddDocuments = new Hlayout();
        final Button addDocuments = new Button("Прикрепленные документы:");
        final Label countDocuments = new Label("0");
        countDocuments.setStyle("color:red");
        addDocuments.setStyle("margin-left: 10px");
        addDocuments.setStyle("width: 300px");
        hlBtnAddDocuments.appendChild(addDocuments);
        hlBtnAddDocuments.appendChild(countDocuments);
        addDocuments.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Map arg = new HashMap();
                arg.put(AddFilesToOrderAttachCtrl.LABEL_COUNT_DOCUMENT, countDocuments);
                arg.put(AddFilesToOrderAttachCtrl.MEDIAS, listMedia);

                ComponentHelper.createWindow("winAddFilesToOrderAttach.zul", "winAttachFiles", arg).doModal();
            }
        });

        component.appendChild(lbForStudent);
        component.appendChild(hlBtnAddStudent);
        component.appendChild(hlBtnAddDocuments);

        Button beginCreate = new Button("Создать");
        beginCreate.setStyle("margin-top: 10px;");
        beginCreate.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (listStudentsToAdd.size() == 0) {
                    PopupUtil.showWarning("Выберите студентов для приказа");
                    return;
                }

                if (listMedia.size() == 0) {
                    PopupUtil.showWarning("Не прикреплен ни один файл");
                    return;
                }

                HashMap<String, Object> args = new HashMap<>();
                args.put(OrderCreator.ORDER_RULE, rule);
                args.put(OrderCreator.LIST_STUDENTS_TO_ADD, listStudentsToAdd);
                args.put(OrderCreator.ID_SEMESTER, sem);

                try {
                    Long id = new OrderCreator().executeCreate(args);

                    if (id != null) {
                        winCreateOrder.detach();

                        String pathCreatedFile = service
                                .createOrderByParams(institute, FileModel.SubTypeDocument.DEDUCTION, sem, String.valueOf(id), listMedia);

                        if (pathCreatedFile == null || pathCreatedFile.equals("")) {
                            PopupUtil.showError("Не удалось создать папку для приказа");
                            return;
                        }

                        OrderModel order = service.getCreatedOrderById(id);
                        order.setUrl(pathCreatedFile);
                        order = service.updateOrder(order);

                        if (order == null) {
                            PopupUtil.showError("Не удалось создать приказ");
                            return;
                        }

                        Map arg = new HashMap();
                        arg.put(ORDER_MODEL, order);

                        ComponentHelper.createWindow("winEditOrder.zul", "winEditOrder", arg).doModal();

                        delegate.updateUI();
                    } else {
                        PopupUtil.showError("Не удалось создать приказ");
                    }
                } catch (UnsupportedOperationException e) {
                    PopupUtil.showError("Невозможно создать приказ данного типа, обратитесь к администратору");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        component.appendChild(beginCreate);
    }
}
