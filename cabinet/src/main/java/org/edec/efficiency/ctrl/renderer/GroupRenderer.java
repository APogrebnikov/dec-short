package org.edec.efficiency.ctrl.renderer;

import org.edec.efficiency.model.ProblemGroup;
import org.edec.efficiency.model.ProblemStudent;
import org.edec.efficiency.model.StatusEfficiency;
import org.edec.efficiency.service.EfficiencyService;
import org.edec.efficiency.service.impl.EfficiencyImpl;
import org.edec.model.EmployeeModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.converter.DateConverter;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.List;


public class GroupRenderer implements ListitemRenderer<ProblemGroup> {
    private ComponentService componentService = new ComponentServiceESOimpl();
    private EfficiencyService efficiencyService = new EfficiencyImpl();

    private List<EmployeeModel> employees;

    public GroupRenderer (List<EmployeeModel> employees) {
        this.employees = employees;
    }

    @Override
    public void render (Listitem li, final ProblemGroup data, int index) throws Exception {
        li.setValue(data);

        final Listcell lc = new Listcell();
        lc.setParent(li);

        final Groupbox gb = new Groupbox();
        gb.setParent(lc);
        gb.setMold("3d");
        gb.setOpen(false);

        Caption caption = new Caption();
        caption.setParent(gb);
        new Label(data.getGroupname()).setParent(caption);

        boolean allConfirm = true;
        for (ProblemStudent student : data.getStudents()) {
            if (student.getStatus() == StatusEfficiency.CREATED) {
                allConfirm = false;
                break;
            }
        }

        Hbox hbEmployee = new Hbox();
        final Combobox cmbAction = new Combobox();
        Button btnAssign = new Button("Назначить");
        if (!allConfirm) {
            hbEmployee.setParent(gb);
            hbEmployee.setHflex("1");
            cmbAction.setParent(hbEmployee);
            cmbAction.setReadonly(true);
            cmbAction.setPlaceholder("Назначить сотрудника..");
            cmbAction.setItemRenderer(new EmployeeRenderer());
            cmbAction.setModel(new ListModelList<>(employees));
            btnAssign.setParent(hbEmployee);

            new Space().setParent(gb);
        }

        final Listbox lbStudent = new Listbox();
        lbStudent.setParent(gb);
        Listhead lh = new Listhead();
        lh.setParent(lbStudent);

        componentService.getListheader("ФИО", "", "1", "").setParent(lh);
        componentService.getListheader("Актив. ЭОК", "110px", "", "center").setParent(lh);
        componentService.getListheader("Посещаемость", "120px", "", "center").setParent(lh);
        componentService.getListheader("Успеваемость", "120px", "", "center").setParent(lh);
        componentService.getListheader("Создан", "80px", "", "center").setParent(lh);
        componentService.getListheader("Взят в работу", "110px", "", "center").setParent(lh);
        componentService.getListheader("Комментарий", "", "1", "").setParent(lh);
        componentService.getListheader("Статус", "140px", "", "center").setParent(lh);
        componentService.getListheader("Действие", "200px", "", "").setParent(lh);

        gb.addEventListener(Events.ON_OPEN, event -> {
            if (lbStudent.getItems().size() != 0) {
                return;
            }
            fillListbox(data, lbStudent, false);
        });
        btnAssign.addEventListener(Events.ON_CLICK, event -> {
            if ((cmbAction.getSelectedItem() == null) || (cmbAction.getSelectedItem().getValue() == null)) {
                return;
            }
            EmployeeModel selectedEmployee = cmbAction.getSelectedItem().getValue();
            if (efficiencyService.updateEmployeeForGroup(selectedEmployee.getIdEmp(), data.getIdLGS())) {
                fillListbox(data, lbStudent, true);
            }
        });
    }

    private void fillListbox (ProblemGroup data, Listbox lbStudent, boolean update) {
        if (update) {
            lbStudent.getItems().clear();
            data.getStudents().clear();
            data.getStudents().addAll(efficiencyService.getProblemStudents(data.getIdLGS(), null));
        }
        for (final ProblemStudent student : data.getStudents()) {
            Listitem liStudent = new Listitem();

            if (student.getGroupLeader()) {
                liStudent.setStyle("background: #4DE2F7;");
            }

            liStudent.setParent(lbStudent);
            componentService.createListcell(liStudent, student.getFio(), "", "", "");
            componentService.createListcell(
                    liStudent, student.getEokActivity() == -1 ? "Не учитывалось" : (student.getEokActivity() + "%"), "", "", "");
            componentService.createListcell(
                    liStudent, student.getAttend() == -1 ? "Не учитывалось" : (student.getAttend() + "%"), "", "", "");
            componentService.createListcell(
                    liStudent, student.getPerformance() == -1 ? "Не учитывалось" : (student.getPerformance() + "%"), "", "", "");
            componentService.createListcell(liStudent, DateConverter.convertDateToString(student.getDateCreated()), "", "", "");
            componentService.createListcell(liStudent, DateConverter.convertDateToString(student.getDateConfirm()), "", "", "");
            componentService.createListcell(liStudent, student.getComment(), "", "", "");
            componentService.createListcell(liStudent, student.getStatus().getName(), "", "", "");

            if (student.getStatus() == StatusEfficiency.CREATED) {
                Listcell lcActionStudent = new Listcell();
                lcActionStudent.setParent(liStudent);
                final Combobox cmbActionStudenet = new Combobox();
                cmbActionStudenet.setParent(lcActionStudent);
                cmbActionStudenet.setReadonly(true);
                cmbActionStudenet.setItemRenderer(new EmployeeRenderer());
                cmbActionStudenet.setReadonly(student.getStatus() == StatusEfficiency.CONFIRM);
                cmbActionStudenet.setModel(new ListModelList<>(employees));
                cmbActionStudenet.addEventListener("onAfterRender", event -> {
                    if (student.getIdEmp() != null && employees != null) {
                        cmbActionStudenet.setSelectedIndex(
                                employees.indexOf(efficiencyService.getEmpByEmployees(student.getIdEmp(), employees)));
                    }
                });
                cmbActionStudenet.addEventListener(Events.ON_CHANGE, updateEmployeeForStudent(student, cmbActionStudenet));
                cmbActionStudenet.addEventListener(Events.ON_BLUR, updateEmployeeForStudent(student, cmbActionStudenet));
            } else if (student.getStatus() == StatusEfficiency.CONFIRM) {
                componentService.createListcell(
                        liStudent, efficiencyService.getEmpByEmployees(student.getIdEmp(), employees).getShortFio(), "", "", "");
            }
        }
        lbStudent.renderAll();
    }

    private EventListener<Event> updateEmployeeForStudent (final ProblemStudent problemStudent, final Combobox cmbAction) {
        return event -> {
            if (cmbAction.getSelectedItem() == null || cmbAction.getSelectedItem().getValue() == null) {
                return;
            }
            EmployeeModel selectedEmp = cmbAction.getSelectedItem().getValue();
            if (problemStudent.getIdEmp() != null && problemStudent.getIdEmp().equals(selectedEmp.getIdEmp())) {
                return;
            }
            if (efficiencyService.updateEmployeeForStudent(selectedEmp.getIdEmp(), problemStudent.getIdEfficiencyStudent())) {
                problemStudent.setIdEmp(selectedEmp.getIdEmp());
            }
        };
    }
}