package org.edec.newOrder.ctrl;

import org.edec.utility.component.model.SemesterModel;
import org.edec.newOrder.ctrl.renderer.StudentSearchRenderer;
import org.edec.newOrder.model.addStudent.LinkOrderSectionEditModel;
import org.edec.newOrder.model.addStudent.SearchGroupModel;
import org.edec.newOrder.model.addStudent.SearchStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.service.esoImpl.EditOrderService;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * Created by antonskripacev on 03.01.17.
 */
public class WinAddStudentCtrl extends SelectorComposer<Component> {
    public static final int UPDATE_UI = 1;
    public static final int ORDER = 2;
    public static final int ORDER_SERVICE = 3;

    @Wire
    private Combobox cmbGroup, cmbListSections;

    @Wire
    private Listbox lbSearchStudents;

    @Wire
    private Textbox tbFamily, tbName, tbPatronymic;

    private OrderEditModel orderModel;

    private List<SearchGroupModel> groupModel;
    private List<LinkOrderSectionEditModel> sectionModel;
    private List<SearchStudentModel> studentModel;

    private EditOrderService service = new EditOrderService();
    private OrderService orderService;

    private Runnable delegate;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        delegate = (Runnable) Executions.getCurrent().getArg().get(UPDATE_UI);
        orderModel = (OrderEditModel) Executions.getCurrent().getArg().get(ORDER);
        orderService = (OrderService) Executions.getCurrent().getArg().get(ORDER_SERVICE);

        sectionModel = service.getSectionsFromOrder(orderModel.getIdOrder());

        cmbGroup.setItemRenderer((ComboitemRenderer<SearchGroupModel>) (ci, data, index) -> {
            if(index == 0) {
                ci.setLabel("Все группы");
            } else {
                ci.setValue(data);
                ci.setLabel(data.getName());
            }
        });

        cmbListSections.setModel(new ListModelArray<>(sectionModel));
        cmbListSections.setItemRenderer((ComboitemRenderer<LinkOrderSectionEditModel>) (comboitem, sectionModel, i) -> {
            comboitem.setLabel(sectionModel.getName());
            comboitem.setValue(sectionModel);
        });

        lbSearchStudents.setItemRenderer(new StudentSearchRenderer());

        groupModel = service.getGroupsForSearch(orderModel.getIdOrder());
        groupModel.add(0, new SearchGroupModel());
        cmbGroup.setModel(new ListModelArray<>(groupModel));
        Events.echoEvent("onLater", cmbGroup, null);
    }

    @Listen("onLater = #cmbGroup")
    public void onCmbGroupRender() {
        cmbGroup.setSelectedIndex(0);
    }

    @Listen("onClick = #btnSearchStudents; onOK = #tbFamily, #tbName, #tbPatronymic")
    public void searchStudents() {
        studentModel = service.getStudentsForSearch(tbFamily.getText(),
                tbName.getText(),
                tbPatronymic.getText(),
                cmbGroup.getSelectedIndex() == -1 || cmbGroup.getSelectedIndex() == 0 ? "" : cmbGroup.getSelectedItem().getLabel(), orderModel.getIdOrder());


        ListModelList<SearchStudentModel> list = new ListModelList<>(studentModel);

        list.setMultiple(true);

        lbSearchStudents.setModel(list);
        lbSearchStudents.renderAll();
    }

    @Listen("onClick = #closeSearch")
    public void closeSearch() {
        getSelf().detach();
    }

    @Listen("onClick = #btnAddStudents")
    public void addStudents() {
        if(cmbListSections.getSelectedIndex() == -1) {
            PopupUtil.showWarning("Не выбран пункт приказа");
            return;
        }

        if(lbSearchStudents.getSelectedIndex() == -1) {
            PopupUtil.showWarning("Не выбраны студенты для добавления");
            return;
        }

        List<SearchStudentModel> listToAdd = lbSearchStudents.getSelectedItems()
                .stream()
                .map(li -> (SearchStudentModel)li.getValue())
                .collect(Collectors.toList());

        Map<Integer, Object> arg = new HashMap<>();
        arg.put(WinSetParamsForStudentCtrl.ORDER, orderModel);
        arg.put(WinSetParamsForStudentCtrl.STUDENTS, listToAdd);
        arg.put(WinSetParamsForStudentCtrl.SECTION, cmbListSections.getSelectedItem().getValue());
        arg.put(WinSetParamsForStudentCtrl.UPDATE_ADD_STUDENT_UI, (Runnable) this::updateUI);
        arg.put(WinSetParamsForStudentCtrl.UPDATE_ADD_EDIT_ORDER, delegate);
        arg.put(WinSetParamsForStudentCtrl.ORDER_SERVICE, orderService);
        ComponentHelper.createWindow("/newOrder/winSetParamsForStudent.zul", "winSetParamsForStudent", arg).doModal();
    }

    public void updateUI() {
        while(lbSearchStudents.getSelectedIndex() != -1) lbSearchStudents.removeItemAt(lbSearchStudents.getSelectedIndex());
    }
}
