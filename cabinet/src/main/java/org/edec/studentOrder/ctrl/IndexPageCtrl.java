package org.edec.studentOrder.ctrl;

import org.edec.studentOrder.ctrl.renderer.OrderRenderer;
import org.edec.studentOrder.ctrl.renderer.StudentOrderRenderer;
import org.edec.studentOrder.manager.StudentOrderManager;
import org.edec.studentOrder.model.OrderModel;
import org.edec.studentOrder.model.StudentOrderModel;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Textbox tbLastName, tbFirstName, tbSemester, tbSectionFilter, tbTypeFilter;
    @Wire
    private Button btnSearch;
    @Wire
    private Listbox lbStudent, lbOrder;

    private List<OrderModel> orders;

    @Override
    protected void fill() throws InterruptedException {

    }

    @Listen("onClick = #btnSearch")
    public void search () {
        String name = tbFirstName.getValue();
        String family = tbLastName.getValue();
        int sem = Integer.parseInt(tbSemester.getValue());
        StudentOrderManager manager = new StudentOrderManager();
        List<StudentOrderModel> students = manager.searchStudent(name, family, sem);
        ListModelList<StudentOrderModel> listmodel = new ListModelList<>(students);
        lbStudent.setModel(listmodel);
        Consumer<StudentOrderModel> action = student -> {
            orders = manager.getOrder(student.getIdStudentSemesterStatus());
            ListModelList<OrderModel> listOrder = new ListModelList<>(orders);
            lbOrder.setModel(listOrder);
            lbOrder.setItemRenderer(new OrderRenderer());
            lbOrder.renderAll();
        };
        lbStudent.setItemRenderer(new StudentOrderRenderer(action));
        lbStudent.renderAll();
    }

    @Listen("onOK = #tbTypeFilter; onOK = #tbSectionFilter")
    public void filterOrderType () {

        //        for (int i=0; i<orders.size(); i++){
        //            if(orders.get(i).getTypeOrder().contains(tbTypeFilter.getValue()) && ) {
        //                orderType.add(orders.get(i));
        //            }
        //        }

        lbOrder.setModel(new ListModelList<>(orders.stream()
                                                   .filter(order_model -> order_model.getTypeOrder()
                                                                                     .toLowerCase()
                                                                                     .contains(tbTypeFilter.getValue().toLowerCase()))
                                                   .filter(order_model -> order_model.getSection()
                                                                                     .toLowerCase()
                                                                                     .contains(tbSectionFilter.getValue().toLowerCase()))
                                                   .collect(Collectors.toList())));

        lbOrder.renderAll();
    }
}
