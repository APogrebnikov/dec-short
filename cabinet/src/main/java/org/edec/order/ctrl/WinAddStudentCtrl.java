package org.edec.order.ctrl;

import org.edec.order.ctrl.delegate.WinAddStudentCtrlDelegate;
import org.edec.order.ctrl.delegate.WinEditOrderCtrlDelegate;
import org.edec.order.ctrl.renderer.StudentSearchRenderer;
import org.edec.order.model.OrderModel;
import org.edec.order.model.SearchGroupModel;
import org.edec.order.model.SearchStudentModel;
import org.edec.order.model.SectionModel;
import org.edec.order.service.AddStudentService;
import org.edec.order.service.impl.AddStudentServiceESO;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.*;


public class WinAddStudentCtrl extends SelectorComposer<Component> {

    @Wire
    Combobox cmbGroup, cmbListSections;

    @Wire
    Listbox lbSearchStudents;

    @Wire
    Textbox tbFamily, tbName, tbPatronymic;

    OrderModel orderModel;

    List<SearchGroupModel> groupModel;
    List<SectionModel> sectionModel;
    List<SearchStudentModel> studentModel;

    AddStudentService service = new AddStudentServiceESO();

    WinEditOrderCtrlDelegate delegate;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        delegate = (WinEditOrderCtrlDelegate) Executions.getCurrent().getArg().get("delegate");
        orderModel = (OrderModel) Executions.getCurrent().getArg().get("order");

        sectionModel = service.getSectionsForSearch(orderModel.getIdOrder());
        groupModel = service.getGroupBySemester(orderModel.getIdSemester());
        groupModel.add(0, new SearchGroupModel());

        cmbGroup.setModel(new ListModelArray<SearchGroupModel>(groupModel));
        cmbGroup.setItemRenderer(new ComboitemRenderer<SearchGroupModel>() {
            @Override
            public void render(Comboitem ci, SearchGroupModel data, int index) throws Exception {
                if (index == 0) {
                    ci.setLabel("Все группы");
                } else {
                    ci.setValue(data);
                    ci.setLabel(data.getName());
                }
            }
        });

        cmbListSections.setModel(new ListModelArray<SectionModel>(sectionModel));
        cmbListSections.setItemRenderer(new ComboitemRenderer<SectionModel>() {
            @Override
            public void render(Comboitem comboitem, SectionModel sectionModel, int i) throws Exception {
                comboitem.setLabel(sectionModel.getName());
                comboitem.setValue(sectionModel);
            }
        });

        lbSearchStudents.setItemRenderer(new StudentSearchRenderer());
    }

    @Listen("onClick = #btnSearchStudents")
    public void searchStudents() {
        studentModel = service.getStudentsForSearch(tbFamily.getText(), tbName.getText(), tbPatronymic.getText(),
                                                    cmbGroup.getSelectedIndex() == -1 || cmbGroup.getSelectedIndex() == 0
                                                    ? ""
                                                    : cmbGroup.getSelectedItem().getLabel(), orderModel.getIdSemester(),
                                                    orderModel.getIdOrder()
        );

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
        if (cmbListSections.getSelectedIndex() == -1) {
            PopupUtil.showWarning("Не выбран пункт приказа");
            return;
        }

        if (lbSearchStudents.getSelectedIndex() == -1) {
            PopupUtil.showWarning("Не выбраны студенты для добавления");
            return;
        }

        List<SearchStudentModel> listToAdd = new ArrayList<>();

        Set<Listitem> listSelected = lbSearchStudents.getSelectedItems();
        for (Listitem li : listSelected) {
            listToAdd.add((SearchStudentModel) li.getValue());
        }

        if (Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winApproveDateStudents") != null) {
            Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winApproveDateStudents").detach();
        }
        Map arg = new HashMap();
        arg.put("order", orderModel);
        arg.put("students", listToAdd);
        arg.put("section", cmbListSections.getSelectedItem().getValue());
        arg.put("delegateAddStudent", new WinAddStudentCtrlDelegate(this));
        arg.put("delegateEditOrder", delegate);
        ComponentHelper.createWindow("/order/winApproveDateStudents.zul", "winApproveDateStudents", arg).doModal();

        PopupUtil.showInfo("Студенты успешно добавлены");
    }

    public void updateUI() {
        while (lbSearchStudents.getSelectedIndex() != -1) {
            lbSearchStudents.removeItemAt(lbSearchStudents.getSelectedIndex());
        }
    }
}
