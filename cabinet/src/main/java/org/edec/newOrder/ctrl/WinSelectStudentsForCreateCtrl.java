package org.edec.newOrder.ctrl;

import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.service.CreateOrderService;
import org.edec.newOrder.service.esoImpl.CreateOrderServiceESO;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/*
    TODO глобальная переработка компоненты
    1) введение фильтров?
    2) Отображение доп информации?
*/

public class WinSelectStudentsForCreateCtrl extends CabinetSelector {
    public static final int INSTITUTE = 0;
    public static final int FOS = 1;
    public static final int ADD_ACTION = 2;

    @Wire
    private Combobox cmbSemester;

    @Wire
    private Window winSelectStudentsForCreate;

    @Wire
    private Textbox tbFamily;

    @Wire
    private Button btnSearch;

    @Wire
    private Listbox lbSearchResults;

    private FormOfStudy currentFOS;
    private InstituteModel currentInstitute;
    private SemesterModel selectedSemester;

    private ComponentService componentService = new ComponentServiceESOimpl();
    private CreateOrderService service = new CreateOrderServiceESO();
    private Consumer<List<OrderCreateStudentModel>> addAction;

    @Override
    protected void fill () {
        currentInstitute = (InstituteModel) Executions.getCurrent().getArg().get(INSTITUTE);
        currentFOS = (FormOfStudy) Executions.getCurrent().getArg().get(FOS);
        addAction = (Consumer<List<OrderCreateStudentModel>>) Executions.getCurrent().getArg().get(ADD_ACTION);

        componentService.fillCmbSem(cmbSemester, currentInstitute.getIdInst(), currentFOS.getType(), null);
        winSelectStudentsForCreate.setPosition("center, center");
    }

    @Listen("onClick = #btnSearch")
    public void searchStudents () {
        if (cmbSemester.getSelectedIndex() == -1) {
            return;
        }

        Long idSem = ((SemesterModel) cmbSemester.getSelectedItem().getValue()).getIdSem();
        String fio = tbFamily.getValue();

        fillLbSearchStudents(service.searchStudentsForOrderCreation(idSem, 0L, fio));
    }

    private void fillLbSearchStudents (List<OrderCreateStudentModel> students) {
        lbSearchResults.getItems().removeAll(lbSearchResults.getItems());

        students.forEach(this::addRowInLbSearchStudents);
    }

    private void addRowInLbSearchStudents (OrderCreateStudentModel studentModel) {
        Listitem li = new Listitem();
        li.setValue(studentModel);

        Listcell cellChoose = new Listcell();
        li.appendChild(cellChoose);

        Listcell cellFio = new Listcell(studentModel.getFio());
        li.appendChild(cellFio);

        Listcell cellGroup = new Listcell(studentModel.getGroupname());
        li.appendChild(cellGroup);

        lbSearchResults.appendChild(li);
    }

    @Listen("onClick = #btnAdd")
    public void addStudentsToOrder () {
        addAction.accept(lbSearchResults.getSelectedItems()
                                        .stream()
                                        .map(item -> (OrderCreateStudentModel) item.getValue())
                                        .collect(Collectors.toList()));

        lbSearchResults.getItems().removeAll(lbSearchResults.getItems());
    }

    @Listen("onClick = #btnReturnToOrder")
    public void closeWindow () {
        winSelectStudentsForCreate.detach();
    }
}
