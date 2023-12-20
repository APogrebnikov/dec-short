package org.edec.newOrder.ctrl;

import org.edec.newOrder.ctrl.renderer.FilterStudentForOrderRenderer;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.newOrder.service.orderCreator.concept.OrderServiceFactory;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.*;
import java.util.stream.Collectors;

public class WinFilterStudentsForOrder extends SelectorComposer<Component> {
    public static final String ORDER_FOR_FILTER = "order";

    private OrderEditModel order;
    private List<OrderCreateStudentModel> studentList = new ArrayList<>();
    private List<OrderCreateStudentModel> filteredStudent = new ArrayList<>();

    private OrderService orderService;

    @Wire
    private Checkbox chNotBudget, chBudget, chForeigner, chNotForeigner;

    @Wire
    private Listbox lbStudents;

    @Wire
    private Button btnCreate;
    @Wire
    private Window winFilterStudentsForOrder;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        order = (OrderEditModel) Executions.getCurrent().getArg().get(ORDER_FOR_FILTER);
        orderService = OrderServiceFactory.getServiceByRule(order.getIdOrderRule(), FormOfStudy.FULL_TIME, order.getIdInstitute());
        studentList = orderService.getOrderCreateService().getStudentsForFilterDeductionByComission(order.getIdOrder());

        lbStudents.setItemRenderer(new FilterStudentForOrderRenderer());

        ListModelList<OrderCreateStudentModel> listModelList = new ListModelList<>(studentList);
        listModelList.setMultiple(true);

        lbStudents.setModel(listModelList);
        lbStudents.selectAll();
        lbStudents.renderAll();

    }

    @Listen("onCheck = #chNotBudget, #chBudget, #chForeigner, #chNotForeigner; ")
    public void selectStudentsByFilter() {


        if (chNotForeigner.isChecked() && chForeigner.isChecked() && chNotBudget.isChecked() && chBudget.isChecked()) {
            filteredStudent = studentList;
        }

        if (!chNotBudget.isChecked()) {
            filteredStudent = studentList.stream().filter(student -> (student.getGovernmentFinanced() || student.getForeigner() || student.getForeigner())).collect(Collectors.toList());
        }

        if (!chBudget.isChecked()) {
            filteredStudent = studentList.stream().filter(student -> (!student.getGovernmentFinanced() || student.getForeigner() || !student.getForeigner())).collect(Collectors.toList());
        }

        if (!chForeigner.isChecked() ) {
            filteredStudent = studentList.stream().filter(student -> (!student.getForeigner()) && (student.getGovernmentFinanced() || !student.getGovernmentFinanced())).collect(Collectors.toList());
        }
        if (!chNotForeigner.isChecked() ) {
            filteredStudent = studentList.stream().filter(student -> (student.getForeigner()) && (student.getGovernmentFinanced() || !student.getGovernmentFinanced())).collect(Collectors.toList());
        }

        if (chNotForeigner.isChecked() && !chForeigner.isChecked() && !chNotBudget.isChecked() && chBudget.isChecked()) {
            filteredStudent = studentList.stream().filter(student -> (!student.getForeigner() && student.getGovernmentFinanced())).collect(Collectors.toList());
        }

        if (!chNotForeigner.isChecked() && chForeigner.isChecked() && chNotBudget.isChecked() && !chBudget.isChecked()) {
            filteredStudent = studentList.stream().filter(student -> (student.getForeigner() && !student.getGovernmentFinanced())).collect(Collectors.toList());
        }
        if (chNotForeigner.isChecked() && chForeigner.isChecked() && chNotBudget.isChecked() && !chBudget.isChecked()) {
            filteredStudent = studentList.stream().filter(student -> ((student.getForeigner() || !student.getForeigner()) && !student.getGovernmentFinanced())).collect(Collectors.toList());
        }
        if ((!chNotForeigner.isChecked() && !chForeigner.isChecked()) || (!chNotBudget.isChecked() && !chBudget.isChecked())) {
            filteredStudent.clear();
        }

        for (Listitem li : lbStudents.getItems()) {
            for (OrderCreateStudentModel student : studentList) {
                if (li.getValue().equals(student) && li.isSelected()) {
                    student.setIsChecked(true);
                }
                if (li.getValue().equals(student) && !li.isSelected()) {
                    student.setIsChecked(false);
                }
            }
        }

        ListModelList<OrderCreateStudentModel> listModelList = new ListModelList<>(filteredStudent);
        listModelList.setMultiple(true);

        List<OrderCreateStudentModel> select = new ArrayList<>();
        for (OrderCreateStudentModel studentModel : studentList) {
            if (studentModel.getIsChecked() != null && studentModel.getIsChecked()) {
              select.add(studentModel);
            }
        }
        listModelList.setSelection(select);

        lbStudents.setModel(listModelList);
        lbStudents.renderAll();
    }

    @Listen("onClick = #btnCreate")
    public void createOrder() {

        List<OrderCreateStudentModel> selectedStudents = new ArrayList<>();

        if (lbStudents.getSelectedCount() == 0) {
            PopupUtil.showInfo("Список студентов пуст.");
        } else {
            for (Listitem li : lbStudents.getSelectedItems()) {
                selectedStudents.add(li.getValue());
            }
        }


           studentList.removeAll(selectedStudents);
        for (OrderCreateStudentModel student : studentList){
            orderService.getOrderCreateService().deleteStudentByFilter(student.getId());
        }

        if (order == null) {
            Messagebox.show("Не удалось создать приказ");
            return;
        }

        Map<String, Object> arg = new HashMap<>();
        arg.put(WinEditOrderCtrl.ORDER_MODEL, order);
        ComponentHelper.createWindow("winEditOrder.zul", "winEditOrder", arg).doModal();

        winFilterStudentsForOrder.detach();

    }
}
