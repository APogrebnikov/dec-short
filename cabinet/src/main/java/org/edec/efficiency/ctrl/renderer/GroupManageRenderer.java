package org.edec.efficiency.ctrl.renderer;

import org.edec.efficiency.model.ProblemGroup;
import org.edec.efficiency.model.ProblemStudent;
import org.edec.efficiency.model.StatusEfficiency;
import org.edec.efficiency.service.EfficiencyService;
import org.edec.efficiency.service.impl.EfficiencyImpl;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;


public class GroupManageRenderer implements ListitemRenderer<ProblemGroup> {
    private ComponentService componentService = new ComponentServiceESOimpl();
    private EfficiencyService efficiencyService = new EfficiencyImpl();

    private Long idEmp;

    public GroupManageRenderer (Long idEmp) {
        this.idEmp = idEmp;
    }

    @Override
    public void render (final Listitem li, final ProblemGroup data, int index) throws Exception {
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

        final Button btnConfirm = new Button("Подтвердить работу с группой");
        btnConfirm.setParent(gb);

        new Space().setParent(gb);

        final Listbox lbStudent = new Listbox();
        lbStudent.setParent(gb);
        Listhead lh = new Listhead();
        lh.setParent(lbStudent);

        componentService.getListheader("ФИО", "", "1", "").setParent(lh);
        componentService.getListheader("Актив. ЭОК", "110px", "", "center").setParent(lh);
        componentService.getListheader("Посещаемость", "120px", "", "center").setParent(lh);
        componentService.getListheader("Успеваемость", "120ph", "", "center").setParent(lh);
        componentService.getListheader("Создан", "80px", "", "center").setParent(lh);
        componentService.getListheader("Взят в работу", "110px", "", "center").setParent(lh);
        componentService.getListheader("Комментарий", "", "1", "").setParent(lh);
        componentService.getListheader("Статус", "140px", "", "center").setParent(lh);
        componentService.getListheader("Действие", "200px", "", "center").setParent(lh);

        gb.addEventListener(Events.ON_OPEN, event -> {
            if (lbStudent.getItems().size() != 0) {
                return;
            }
            fillListbox(data, lbStudent, li, btnConfirm, false);
        });
        btnConfirm.addEventListener(Events.ON_CLICK, event -> {
            if (efficiencyService.updateStatusConfirmForGroup(idEmp, data.getIdLGS())) {
                fillListbox(data, lbStudent, li, btnConfirm, true);
                btnConfirm.setVisible(false);
                btnConfirm.setDisabled(true);
                PopupUtil.showInfo("Группа " + data.getGroupname() + " принята в работу");
            }
        });
    }

    private void fillListbox (final ProblemGroup data, final Listbox lbStudent, final Listitem liGroup, final Button btnConfirmGroup,
                              final boolean update) {
        if (update) {
            lbStudent.getItems().clear();
            data.getStudents().clear();
            data.getStudents().addAll(efficiencyService.getProblemStudents(data.getIdLGS(), idEmp));
            if (data.getStudents().size() == 0) {
                liGroup.detach();
                return;
            }
        }
        boolean allConfirm = true;
        for (final ProblemStudent student : data.getStudents()) {
            if (student.getStatus() == StatusEfficiency.CREATED) {
                allConfirm = false;
            }
            Listitem liStudent = new Listitem();
            liStudent.setParent(lbStudent);
            if (student.getGroupLeader()) {
                liStudent.setStyle("background: #4DE2F7;");
            }

            componentService.createListcell(liStudent, student.getFio(), "", "", "");
            componentService.createListcell(
                    liStudent, student.getEokActivity() == -1 ? "Не учитывалось" : (student.getEokActivity() + "%"), "", "", "");
            componentService.createListcell(
                    liStudent, student.getAttend() == -1 ? "Не учитывалось" : (student.getAttend() + "%"), "", "", "");
            componentService.createListcell(
                    liStudent, student.getPerformance() == -1 ? "Не учитывалось" : (student.getPerformance() + "%"), "", "", "");
            componentService.createListcell(liStudent, DateConverter.convertDateToString(student.getDateCreated()), "", "", "");
            componentService.createListcell(liStudent, DateConverter.convertDateToString(student.getDateConfirm()), "", "", "");

            Listcell lcComment = new Listcell();
            lcComment.setParent(liStudent);
            final Textbox tbComment = new Textbox(student.getComment());
            tbComment.setParent(lcComment);
            tbComment.setInplace(true);
            tbComment.setPlaceholder("Комментарий..");
            tbComment.setHflex("1");
            tbComment.addEventListener(Events.ON_OK, event -> {
                student.setComment(tbComment.getValue());
                tbComment.setTooltiptext(tbComment.getValue());
                efficiencyService.updateEfficiencyStudent(student);
            });
            tbComment.addEventListener(Events.ON_CHANGE, event -> {
                student.setComment(tbComment.getValue());
                tbComment.setTooltiptext(tbComment.getValue());
                efficiencyService.updateEfficiencyStudent(student);
            });

            componentService.createListcell(liStudent, student.getStatus().getName(), "", "", "");

            Listcell lcActionStudent = new Listcell();
            lcActionStudent.setParent(liStudent);
            if (student.getStatus() == StatusEfficiency.CREATED) {
                Button btnConfirm = new Button("Принять");
                btnConfirm.setParent(lcActionStudent);
                btnConfirm.addEventListener(Events.ON_CLICK, event -> {
                    if (efficiencyService.updateStatusConfirmForStudent(idEmp, student.getIdSSS())) {
                        fillListbox(data, lbStudent, liGroup, btnConfirmGroup, true);
                    }
                });
            } else if (student.getStatus() == StatusEfficiency.CONFIRM) {
                Button btnCompleted = new Button("Завершить");
                btnCompleted.setParent(lcActionStudent);
                btnCompleted.addEventListener(Events.ON_CLICK, event -> {
                    if (efficiencyService.updateStatusCompletedForStudent(idEmp, student.getIdSSS())) {
                        fillListbox(data, lbStudent, liGroup, btnConfirmGroup, true);
                    }
                });
            }
        }
        if (allConfirm) {
            btnConfirmGroup.setVisible(false);
            btnConfirmGroup.setDisabled(true);
        }
        lbStudent.renderAll();
    }
}