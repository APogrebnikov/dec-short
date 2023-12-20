package org.edec.efficiency.ctrl.renderer;

import org.edec.efficiency.model.ProblemGroup;
import org.edec.efficiency.model.ProblemStudent;
import org.edec.efficiency.service.EfficiencyService;
import org.edec.efficiency.service.impl.EfficiencyImpl;
import org.edec.model.EmployeeModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.converter.DateConverter;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.List;

public class GroupAllStudentRenderer implements ListitemRenderer<ProblemGroup> {
    private ComponentService componentService = new ComponentServiceESOimpl();
    private EfficiencyService efficiencyService = new EfficiencyImpl();

    private List<EmployeeModel> employees;

    public GroupAllStudentRenderer (List<EmployeeModel> employees) {
        this.employees = employees;
    }

    @Override
    public void render (Listitem li, ProblemGroup data, int index) throws Exception {
        li.setValue(data);

        Listcell lc = new Listcell();
        lc.setParent(li);

        Groupbox gb = new Groupbox();
        gb.setParent(lc);
        gb.setMold("3d");
        gb.setOpen(false);

        Caption caption = new Caption();
        caption.setParent(gb);
        new Label(data.getGroupname()).setParent(caption);

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
        componentService.getListheader("Сотрудник", "150px", "", "").setParent(lh);

        gb.addEventListener(Events.ON_OPEN, event -> {
            if (lbStudent.getItems().size() != 0) {
                return;
            }
            fillListbox(data, lbStudent, false);
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
            EmployeeModel empByEmployees = efficiencyService.getEmpByEmployees(student.getIdEmp(), employees);
            componentService.createListcell(liStudent, empByEmployees == null ? "" : empByEmployees.getShortFio(), "", "", "");
        }
        lbStudent.renderAll();
    }
}
