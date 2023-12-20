package org.edec.teacher.ctrl.renderer;

import org.edec.teacher.ctrl.WinRegisterCtrl;
import org.edec.teacher.ctrl.WinReportGroupCtrl;
import org.edec.teacher.model.EsoCourseModel;
import org.edec.teacher.model.GroupModel;
import org.edec.teacher.model.SubjectModel;
import org.edec.teacher.service.CompletionService;
import org.edec.teacher.service.impl.CompletionServiceImpl;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SubjectRenderer implements ListitemRenderer<SubjectModel> {
    private ComponentService componentService = new ComponentServiceESOimpl();
    private CompletionService completionService = new CompletionServiceImpl();

    private List<EsoCourseModel> esoCourses;


    public SubjectRenderer(List<EsoCourseModel> esoCourses) {
        this.esoCourses = esoCourses;
    }

    @Override
    public void render(final Listitem li, final SubjectModel data, int index) throws Exception {
        li.setValue(data);
        componentService.createListcell(li, String.valueOf(index + 1), "color: #000;", "", "");
        Listcell lcSubject = new Listcell(data.getSubjectname());
        lcSubject.setParent(li);
        lcSubject.setStyle("color: #000;");
        lcSubject.setTooltiptext(data.getSubjectname());
        componentService.createListcell(li, FormOfControlConst.getName(data.getFormofcontrol()).getName(), "color: #000;", "", "");

        Listcell lcGroup = new Listcell();
        final Combobox cmbGroup = new Combobox();
        for (GroupModel group : data.getGroups()) {
            Comboitem ci = new Comboitem();
            if(group.getCompletionDate() != null){
                ci.setLabel(group.getGroupname() + " (" + new SimpleDateFormat("dd.MM.yyyy").format(group.getCompletionDate()) + " г.)");
            }
            else {
                ci.setLabel(group.getGroupname());
            }
            ci.setValue(group);
            ci.setParent(cmbGroup);
        }
        cmbGroup.setStyle("width: 200px;");
        cmbGroup.setReadonly(true);
        cmbGroup.setSelectedIndex(0);
        cmbGroup.setParent(lcGroup);
        lcGroup.setParent(li);

        Listcell lcBtnRegister = new Listcell();
        Button btnRegister = new Button("Ведомость");
        btnRegister.setTooltiptext("Просмотреть Ведомость");
        btnRegister.setHoverImage("/imgs/docsCLR.png");
        btnRegister.setParent(lcBtnRegister);
        lcBtnRegister.setParent(li);

        Listcell lcBtnAttendanceProgress = new Listcell();
        lcBtnAttendanceProgress.setParent(li);
        Button btnAttendanceProgress = new Button("Отчет");
        btnAttendanceProgress.setParent(lcBtnAttendanceProgress);
        btnAttendanceProgress.setTooltiptext("Просмотреть посещаемость");

        Listcell lcESOcourse = new Listcell();
        lcESOcourse.setParent(li);

        Bandbox bdEsoCourse = new Bandbox();
        bdEsoCourse.setParent(lcESOcourse);
        bdEsoCourse.setMold("rounded");
        bdEsoCourse.setAutodrop(true);
        bdEsoCourse.setWidth("120px");
        bdEsoCourse.setStyle("margin-right: 10px;");
        bdEsoCourse.setReadonly(true);
        if (((GroupModel) cmbGroup.getSelectedItem().getValue()).getIdESOcourse() != null) {
            bdEsoCourse.setValue(String.valueOf(completionService.getFilteredEsoCourses(null, ((GroupModel) cmbGroup.getSelectedItem().getValue())
                    .getIdESOcourse(), esoCourses).get(0).getIdEsoCourse()));
        }
        Bandpopup bpop = new Bandpopup();
        bpop.setParent(bdEsoCourse);


        Hbox hbEsoCourse = new Hbox();
        hbEsoCourse.setParent(bpop);

        Button btnDelEsoCourse = new Button("", "/imgs/crossCLR.png");
        btnDelEsoCourse.setParent(hbEsoCourse);
        btnDelEsoCourse.setTooltiptext("Удалить привязку курса");

        Label lEsoCourse = new Label();
        lEsoCourse.setParent(hbEsoCourse);

        Listbox lbEsoCourse = new Listbox();
        lbEsoCourse.setParent(bpop);
        lbEsoCourse.setWidth("500px;");
        lbEsoCourse.setVflex("1");
        lbEsoCourse.setMold("paging");
        lbEsoCourse.setItemRenderer(new EsoCourseRenderer());
        lbEsoCourse.setPageSize(5);
        Listhead lhEsoCourse = new Listhead();
        lhEsoCourse.setParent(lbEsoCourse);

        Listheader lhrIdCourse = new Listheader();
        lhrIdCourse.setParent(lhEsoCourse);
        lhrIdCourse.setWidth("80px");
        Intbox intIdCourse = new Intbox();
        intIdCourse.setParent(lhrIdCourse);
        intIdCourse.setPlaceholder("Введите код");
        intIdCourse.setValue(((GroupModel) cmbGroup.getSelectedItem().getValue()).getIdESOcourse() != null
                ? ((GroupModel) cmbGroup.getSelectedItem().getValue()).getIdESOcourse().intValue()
                : null);

        Listheader lhrNameCourse = new Listheader();
        lhrNameCourse.setParent(lhEsoCourse);
        lhrNameCourse.setWidth("400px");
        Textbox tbCourseName = new Textbox();
        tbCourseName.setPlaceholder("Введите название");
        tbCourseName.setParent(lhrNameCourse);

        A aEsoCourse = new A();
        aEsoCourse.setParent(lcESOcourse);
        aEsoCourse.setIconSclass("z-icon-hand-o-up");
        aEsoCourse.setTarget("_blank");
        aEsoCourse.setTooltiptext("Перейти на электронные курсы");

        btnRegister.addEventListener(Events.ON_CLICK, showRegister(cmbGroup, data));
        btnAttendanceProgress.addEventListener(Events.ON_CLICK, showReport(cmbGroup));
        cmbGroup.addEventListener(Events.ON_CHANGE, changeGroup(cmbGroup, bdEsoCourse));
        bdEsoCourse.addEventListener(Events.ON_OPEN, onOpenBandbox(cmbGroup, intIdCourse, tbCourseName, lbEsoCourse, lEsoCourse));
        btnDelEsoCourse.addEventListener(Events.ON_CLICK, onClickDelEsoBtn(cmbGroup, bdEsoCourse));
        tbCourseName.addEventListener(Events.ON_OK, onOKtextboxes(intIdCourse, tbCourseName, lbEsoCourse));
        intIdCourse.addEventListener(Events.ON_OK, onOKtextboxes(intIdCourse, tbCourseName, lbEsoCourse));
        lbEsoCourse.addEventListener(Events.ON_SELECT, onSelectEsoCourse(lbEsoCourse, cmbGroup, bdEsoCourse));
        aEsoCourse.addEventListener(Events.ON_CLICK, showESOcourse(cmbGroup, aEsoCourse));
    }

    private EventListener<Event> onClickDelEsoBtn(final Combobox cmbGroup, final Bandbox bandbox) {
        return event -> {
            GroupModel selectedGroup = cmbGroup.getSelectedItem().getValue();
            completionService.updateESOcourse(selectedGroup.getIdLGSS(), null);
            selectedGroup.setIdESOcourse(null);
            bandbox.setValue("");
            bandbox.close();
        };
    }

    private EventListener<Event> changeGroup(final Combobox cmbGroup, final Bandbox bdEsoCourse) {
        return event -> {
            GroupModel groupModel = cmbGroup.getSelectedItem().getValue();
            if (groupModel.getIdESOcourse() != null) {
                bdEsoCourse.setValue(
                        completionService.getFilteredEsoCourses(null, groupModel.getIdESOcourse(), esoCourses).get(0).getIdEsoCourse().toString());
            } else {
                bdEsoCourse.setValue("");
            }
        };
    }

    private EventListener<Event> showRegister(final Combobox cmbGroup, final SubjectModel data) {
        return event -> {
            Map arg = new HashMap();
            arg.put(WinRegisterCtrl.SELECTED_GROUP, cmbGroup.getSelectedItem().getValue());
            arg.put(WinRegisterCtrl.FORM_CONTROL, data.getFormofcontrol());

            ComponentHelper.createWindow("/teacher/winRegister.zul", "winRegister", arg).doModal();
        };
    }

    private EventListener<Event> showReport(final Combobox cmbGroup) {
        return event -> {
            Map arg = new HashMap();
            arg.put(WinReportGroupCtrl.SELECTED_GROUP, cmbGroup.getSelectedItem().getValue());

            ComponentHelper.createWindow("/teacher/winReportGroup.zul", "winReportGroup", arg).doModal();
        };
    }

    private EventListener<Event> showESOcourse(final Combobox cmbGroup, final A a) {
        return  event-> {
                GroupModel selectedGroup = cmbGroup.getSelectedItem().getValue();
                if (selectedGroup.getIdESOcourse() != null)
                    Executions.getCurrent().sendRedirect("https://e.sfu-kras.ru/course/view.php?id=" + selectedGroup.getIdESOcourse(), "_blank");
                else
                    PopupUtil.showWarning("Сначала заполните идентификатор своего курса!");

        };
    }

    private EventListener<Event> onOpenBandbox(final Combobox cmbGroup, final Intbox intIdCourse, final Textbox tbNameCourse, final Listbox lbEsoCourse, final Label lEsoCourse) {
        return event -> {
            GroupModel selectedGroup = cmbGroup.getSelectedItem().getValue();
            intIdCourse.setValue(selectedGroup.getIdESOcourse() == null ? null : selectedGroup.getIdESOcourse().intValue());
            tbNameCourse.setValue("");
            lEsoCourse.setValue(selectedGroup.getIdESOcourse() == null
                    ? "Курс не выбран"
                    : "Выбранный курс: " + completionService.getFilteredEsoCourses(null, selectedGroup.getIdESOcourse(), esoCourses).get(0).getFullname());
            lbEsoCourse.setModel(new ListModelList<>(
                    completionService.getFilteredEsoCourses(null, intIdCourse.getValue() != null ? intIdCourse.getValue().longValue() : null, esoCourses)));
            lbEsoCourse.renderAll();
        };
    }

    private EventListener<Event> onOKtextboxes(final Intbox intIdCourse, final Textbox tbNameCourse, final Listbox lbEsoCourse) {
        return event -> {
            lbEsoCourse.setModel(new ListModelList<>(
                    completionService.getFilteredEsoCourses(tbNameCourse.getValue().equals("") ? null : tbNameCourse.getValue(),
                            intIdCourse.getValue() != null ? intIdCourse.getValue().longValue() : null,
                            esoCourses)));
            lbEsoCourse.renderAll();
        };
    }

    private EventListener<Event> onSelectEsoCourse(final Listbox lbEsoCourse, final Combobox cmbGroup, final Bandbox bdEsoCourse) {
        return  event-> {
                GroupModel selectedGroup = cmbGroup.getSelectedItem().getValue();
                EsoCourseModel selectedCourse = lbEsoCourse.getSelectedItem().getValue();
                if (cmbGroup.getItemCount() > 1) {
                    Messagebox.show("Вы хотите подписать на электронный курс все группы?", "Подписка других групп", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, (EventListener) evt -> {
                        if (evt.getName().equals("onOK")) {
                            PopupUtil.showInfo("Группы успешно подписаны!");
                            for (GroupModel groupModel : selectedGroup.getSubject().getGroups()) {
                                completionService.updateESOcourse(groupModel.getIdLGSS(), selectedCourse.getIdEsoCourse());
                                groupModel.setIdESOcourse(selectedCourse.getIdEsoCourse());
                            }
                        } else {
                            completionService.updateESOcourse(selectedGroup.getIdLGSS(), selectedCourse.getIdEsoCourse());
                            selectedGroup.setIdESOcourse(selectedCourse.getIdEsoCourse());
                        }
                    });
                } else {
                    completionService.updateESOcourse(selectedGroup.getIdLGSS(), selectedCourse.getIdEsoCourse());
                    selectedGroup.setIdESOcourse(selectedCourse.getIdEsoCourse());
                }
                bdEsoCourse.setValue(selectedCourse.getFullname());
                bdEsoCourse.close();

        };
    }
}
