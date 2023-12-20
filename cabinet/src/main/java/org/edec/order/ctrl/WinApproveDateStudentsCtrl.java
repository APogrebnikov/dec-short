package org.edec.order.ctrl;

import org.edec.order.ctrl.delegate.WinAddStudentCtrlDelegate;
import org.edec.order.ctrl.delegate.WinEditOrderCtrlDelegate;
import org.edec.order.ctrl.renderer.ApproveDateStudentsRenderer;
import org.edec.order.model.OrderModel;
import org.edec.order.model.SearchStudentModel;
import org.edec.order.model.SectionModel;
import org.edec.order.model.StudentToAddModel;
import org.edec.order.service.AddStudentService;
import org.edec.order.service.impl.AddStudentServiceESO;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import java.util.ArrayList;
import java.util.List;


public class WinApproveDateStudentsCtrl extends SelectorComposer<Component> {
    @Wire
    Window winApproveDateStudents;

    @Wire
    Listbox lbStudents;

    @Wire
    Button btnApprove;

    @Wire
    Button btnCancel;

    AddStudentService service = new AddStudentServiceESO();

    private OrderModel order;
    private SectionModel sectionModel;
    private List<SearchStudentModel> listStudentsToApprove;
    private List<StudentToAddModel> listStudentsToAdd = new ArrayList<>();

    private WinAddStudentCtrlDelegate delegateAddStudent;
    private WinEditOrderCtrlDelegate delegateEditOrder;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);

        order = (OrderModel) Executions.getCurrent().getArg().get("order");
        sectionModel = (SectionModel) Executions.getCurrent().getArg().get("section");
        listStudentsToApprove = (List<SearchStudentModel>) Executions.getCurrent().getArg().get("students");
        delegateAddStudent = (WinAddStudentCtrlDelegate) Executions.getCurrent().getArg().get("delegateAddStudent");
        delegateEditOrder = (WinEditOrderCtrlDelegate) Executions.getCurrent().getArg().get("delegateEditOrder");

        constructModel();

        lbStudents.setModel(new ListModelList<>(listStudentsToAdd));
        lbStudents.setItemRenderer(new ApproveDateStudentsRenderer(order));
        lbStudents.renderAll();
    }

    @Listen("onClick = #btnCancel")
    public void cancel () {
        winApproveDateStudents.detach();
    }

    @Listen("onClick = #btnApprove")
    public void approveList () {
        service.addStudentInOrder(order, sectionModel, listStudentsToAdd);

        delegateEditOrder.updateUI();
        delegateAddStudent.updateUI();

        winApproveDateStudents.detach();
    }

    public void constructModel () {
        for (SearchStudentModel item : listStudentsToApprove) {
            StudentToAddModel newItem = new StudentToAddModel();
            newItem.setFio(item.getSurname() + " " + item.getName() + " " + item.getPatronymic());
            newItem.setFirstDate(sectionModel.getFirstDate());
            newItem.setSecondDate(sectionModel.getSecondDate());
            newItem.setId(item.getId());

            listStudentsToAdd.add(newItem);
        }
    }
}
