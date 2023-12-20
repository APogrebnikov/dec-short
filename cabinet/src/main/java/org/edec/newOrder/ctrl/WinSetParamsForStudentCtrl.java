package org.edec.newOrder.ctrl;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.addStudent.LinkOrderSectionEditModel;
import org.edec.newOrder.model.addStudent.SearchStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.service.ComponentProvider;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;
import org.zkoss.zul.impl.XulElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WinSetParamsForStudentCtrl extends SelectorComposer<Component> {
    public static final int ORDER = 1;
    public static final int SECTION = 2;
    public static final int STUDENTS = 3;
    public static final int UPDATE_ADD_STUDENT_UI = 4;
    public static final int UPDATE_ADD_EDIT_ORDER = 5;
    public static final int ORDER_SERVICE = 6;

    @Wire
    private Window winSetParamsForStudent;

    @Wire
    private Listbox lbStudents;

    @Wire
    private Button btnApprove;

    @Wire
    private Button btnCancel;

    private OrderService service;
    private ComponentProvider componentProvider = new ComponentProvider();

    private OrderEditModel order;
    private LinkOrderSectionEditModel sectionModel;
    private List<SearchStudentModel> listStudentsToAdd = new ArrayList<>();
    private List<XulElement> componentParams = new ArrayList<>();

    private Runnable updateAddStudentWindow, updateEditOrderWindow;
    private String studentsAlreadyInOrderMsg = "";
    private boolean isGoingToClose = false;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);

        order = (OrderEditModel) Executions.getCurrent().getArg().get(ORDER);
        sectionModel = (LinkOrderSectionEditModel) Executions.getCurrent().getArg().get(SECTION);
        listStudentsToAdd = (List<SearchStudentModel>) Executions.getCurrent().getArg().get(STUDENTS);
        updateAddStudentWindow = (Runnable) Executions.getCurrent().getArg().get(UPDATE_ADD_STUDENT_UI);
        updateEditOrderWindow = (Runnable) Executions.getCurrent().getArg().get(UPDATE_ADD_EDIT_ORDER);
        service = (OrderService) Executions.getCurrent().getArg().get(ORDER_SERVICE);

        List<StudentModel> studentsInsection = order.getStudentsInSection(sectionModel.getIdOS());
        listStudentsToAdd.stream()
                .filter(student -> studentsInsection.stream().anyMatch(studentInSection -> studentInSection.getFio().equals(student.getFio()) && studentInSection.getGroupname().equals(student.getGroupname())))
                .map(SearchStudentModel::getFio).forEach(el -> studentsAlreadyInOrderMsg += el + "\n");

        if(!studentsAlreadyInOrderMsg.equals("")) {
            listStudentsToAdd.removeAll(listStudentsToAdd.stream()
                    .filter(student -> order.getStudentsInSection(sectionModel.getIdOS())
                            .stream()
                            .anyMatch(studentInSection -> studentInSection.getFio().equals(student.getFio()) && studentInSection.getGroupname().equals(student.getGroupname()))
                    ).collect(Collectors.toList())
            );

            studentsAlreadyInOrderMsg = "Следующие студенты уже есть в этой секции в приказе и не будут добавлены: \n" + studentsAlreadyInOrderMsg;

            if(listStudentsToAdd.isEmpty()) {
                isGoingToClose = true;
                Events.echoEvent("onLater", winSetParamsForStudent, null);
                return;
            }
        }

        componentProvider.createListheader(lbStudents.getListhead(), "Студент", "cwf-listheader-label", "width: 200px");
        componentProvider.createListheader(lbStudents.getListhead(), "Группа", "cwf-listheader-label", "width: 100px");

        List<OrderVisualParamModel> visualParamModels = service.getEditCreatedOrderService().getVisualParamsByIdSection(sectionModel.getIdOS());
        for (OrderVisualParamModel param : visualParamModels) {
            componentProvider.createListheader(lbStudents.getListhead(), param.getName(), "cwf-listheader-label", "width: " + param.getListHeaderWidth());
        }

        for (SearchStudentModel studentModel : listStudentsToAdd) {
            Listitem li = new Listitem();

            componentProvider.createListcell(
                    li, studentModel.getSurname() + " " + studentModel.getName() + " " + studentModel.getPatronymic(), "width:  200px");
            componentProvider.createListcell(li, studentModel.getGroupname(), "width: 100px");

            for (OrderVisualParamModel param : visualParamModels) {
                XulElement component = componentProvider.provideComponent(param.getEditComponent());
                componentProvider.createListcell(li, component, "width: " + param.getListHeaderWidth());
                componentParams.add(component);
            }

            lbStudents.appendChild(li);
        }

        Events.echoEvent("onLater", winSetParamsForStudent, null);
    }

    @Listen("onLater = #winSetParamsForStudent")
    public void onAfterRender() {
        if(isGoingToClose) {
            DialogUtil.info("Нет студентов для добавления");
            winSetParamsForStudent.detach();
        }

        if(!"".equals(studentsAlreadyInOrderMsg)) {
            DialogUtil.info(studentsAlreadyInOrderMsg);
        }
    }

    @Listen("onClick = #btnCancel")
    public void cancel () {
        winSetParamsForStudent.detach();
    }

    @Listen("onClick = #btnApprove")
    public void approveList () {
        int i = 0;

        for (SearchStudentModel studentModel : listStudentsToAdd) {
            for (int j = 0; j < service.getEditCreatedOrderService().getVisualParamsByIdSection(sectionModel.getIdOS()).size(); j++) {
                studentModel.getStudentParams().add(componentProvider.getValueComponent(
                                    componentParams.get(i * service.getEditCreatedOrderService().getVisualParamsByIdSection(sectionModel.getIdOS()).size() + j)));
            }

            service.getEditCreatedOrderService().addStudentToOrder(studentModel, order, sectionModel);
            i++;
        }

        updateAddStudentWindow.run();
        updateEditOrderWindow.run();

        winSetParamsForStudent.detach();
    }
}
