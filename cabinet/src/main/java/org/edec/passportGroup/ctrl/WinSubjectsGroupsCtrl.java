package org.edec.passportGroup.ctrl;

import lombok.extern.log4j.Log4j;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.passportGroup.ctrl.renderer.SubjectsListRenderer;
import org.edec.passportGroup.model.GroupModel;
import org.edec.passportGroup.model.SubjectReportModel;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.passportGroup.service.impl.PassportGroupServiceESO;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.*;

@Log4j
public class WinSubjectsGroupsCtrl extends SelectorComposer<Component> {
    PassportGroupService service = new PassportGroupServiceESO();

    @Wire
    private Window winSubjectsGroups;

    @Wire
    private Combobox cmbCtrlForm, cmbStatus;

    @Wire
    private Listbox listSubject;

    @Wire
    private Textbox tbSearch;

    @Wire
    private Vlayout teacherVlayout;

    @Wire
    private Button btnEditSubject, btnDeleteSubject, attach_btn,btnCreateSubject;

    private Map params;
    private boolean isFirstRender;
    private int index;

    private List<SubjectReportModel> subjects = new ArrayList<>();
    private List<GroupModel> groupList = new ArrayList<>();
    private List<SubjectReportModel> listModel = new ArrayList<>();

    private Runnable updateSubjects = this::fillSubjects;

    private Long[] idLesg;
    private Runnable updateTeachers = this::fillTeachers;

    protected TemplatePageCtrl template = new TemplatePageCtrl();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        if (template.getCurrentModule().isReadonly()){
            btnEditSubject.setVisible(false);
            btnDeleteSubject.setVisible(false);
            attach_btn.setVisible(false);
            btnCreateSubject.setVisible(false);
        }

        isFirstRender = true;
        params = Executions.getCurrent().getArg();

        cmbCtrlForm.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);

        fillSubjects();
        if (!template.getCurrentModule().isReadonly()){

        }
        listSubject.addEventListener(Events.ON_SELECT, event -> {
            index = listSubject.getSelectedItem().getIndex();
            fillTeachers();
        });
    }

    private void fillSubjects() {

        groupList = (List<GroupModel>) params.get("groupList");

        Collections.sort(groupList, new Comparator<GroupModel>() {
            @Override
            public int compare(GroupModel g1, GroupModel g2) {
                return g1.getGroupName().compareTo(g2.getGroupName());
            }
        });

        subjects = service.getSubjectsReport(groupList, cmbCtrlForm.getSelectedIndex(), cmbStatus.getSelectedIndex(), tbSearch.getValue());

        listModel = service.getSubjectListModel(subjects);

        listModel = service.filterSubjectReportList(listModel, tbSearch.getValue(), cmbCtrlForm.getSelectedIndex(), cmbStatus.getSelectedIndex());

        listSubject.setItemRenderer(new SubjectsListRenderer(listModel, template.getCurrentUser().getFio()));
        listSubject.setModel(new ListModelList<>(listModel));
        listSubject.renderAll();

        try {
            if (!isFirstRender) {
                if (index == listSubject.getItemCount()) index--;
                listSubject.setSelectedIndex(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        isFirstRender = false;
    }

    private void fillTeachers() {

        btnEditSubject.setDisabled(false);
        btnDeleteSubject.setDisabled(false);

        teacherVlayout.getChildren().clear();
        idLesg = new Long[listModel.get(index).getListEmployees().size()];

        if (listModel.get(index).getListEmployees().size() > 0) {
            for (int i = 0; i < listModel.get(index).getListEmployees().size(); i++) {
                idLesg[i] = listModel.get(index).getListEmployees().get(i).getIdLesg();
                Vbox teacherView = new Vbox();
                teacherView.setId("view" + i);
                teacherView.setWidth("100%");
                teacherView.setStyle("background: #AFB9C3;" +
                        "padding: 8px;" +
                        "margin-left: 8px;" +
                        "border-radius: 10px;" +
                        "right: -5px");

                Label lFIO = new Label(listModel.get(index).getListEmployees().get(i).getFullName());
                lFIO.setStyle("font-weight: bold; font-size: 100%");
                teacherView.appendChild(lFIO);

                for (int j = 0; j < listModel.get(index).getListEmployees().get(i).getListDepTitles().size(); j++) {
                    Label lDepList = new Label(listModel.get(index).getListEmployees().get(i).getListDepTitles().get(j));
                    teacherView.appendChild(lDepList);
                    if (j > 0) {
                        teacherView.setHeight(50 + (i * 10) + "px");
                    }
                }

                Button btnDetach = new Button("Открепить");
                if (template.getCurrentModule().isReadonly()) {
                    btnDetach.setVisible(false);
                } else {
                    btnDetach.setStyle("float: right;" +
                            "margin-right: 8px");
                    btnDetach.addEventListener(Events.ON_CLICK, event -> {
                        try {
                            Messagebox.show("Вы уверены?", "Подтверждение", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, event1 -> {
                                if (event1.getName().equals("onOK")) {
                                    for (int i1 = 0; i1 < listModel.get(index).getListEmployees().size(); i1++) {
                                        if ((teacherView.getId().equals("view" + i1))) {
                                            if (service.removeTeacherFromSubject(idLesg[i1])) {

                                                listModel.get(index).getListEmployees().remove(i1);
                                                fillTeachers();
                                                fillSubjects();
                                            } else {
                                                PopupUtil.showError("Ошибка");
                                            }
                                        }
                                    }
                                    PopupUtil.showInfo("Преподаватель откреплен!");
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    });
                }


                teacherView.appendChild(btnDetach);
                teacherVlayout.appendChild(teacherView);
            }
        } else {
            Vbox teacherView = new Vbox();
            Label lNoOne = new Label("Нет прикрепленных преподавателей");
            lNoOne.setStyle("color: #000000;" +
                    "font-size: 16px;" +
                    "font-weight: 700;" +
                    "font-family: opensans, arial, freesans, sans-serif;" +
                    "font-style: normal;" +
                    "line-height: 20px;");
            teacherView.appendChild(lNoOne);
            teacherView.setAlign("center");
            teacherView.setWidth("100%");
            teacherVlayout.appendChild(teacherView);
        }
    }

    @Listen("onOK = #tbSearch")
    public void searchSubject() {
        teacherVlayout.getChildren().clear();
        fillSubjects();
        listSubject.clearSelection();
    }

    @Listen("onChange = #cmbCtrlForm, #cmbStatus")
    public void changeFilterSubject() {
        teacherVlayout.getChildren().clear();
        fillSubjects();
        listSubject.clearSelection();
    }

    @Listen("onClick = #attach_btn")
    public void attachSubject() {
        if (listSubject.getSelectedItem() != null) {
            Map arg = new HashMap();
            arg.put("subject", listSubject.getSelectedItem().getValue());
            arg.put("updateSubjects", updateSubjects);
            arg.put("updateTeachers", updateTeachers);
            ComponentHelper.createWindow("/passportGroup/winTeachers.zul", "winTeachers", arg).doModal();
        } else {
            PopupUtil.showWarning("Сначала выберите предмет");
        }
    }

    @Listen("onClick = #report_close_btn")
    public void close() {
        winSubjectsGroups.detach();
    }

    @Listen("onClick = #btnCreateSubject")
    public void createSubject() {
        Map arg = new HashMap();

        arg.put(WinSubjectEditorCtrl.SUBJECT, null);
        arg.put(WinSubjectEditorCtrl.GROUP, groupList);
        arg.put(WinSubjectEditorCtrl.UPDATE_SUBJECTS, updateSubjects);

        ComponentHelper.createWindow("/passportGroup/winSubjectEditor.zul", "winSubjectEditor", arg).doModal();
    }

    @Listen("onClick = #btnEditSubject")
    public void editSubject() {
        SubjectReportModel selectedSubject = listSubject.getSelectedItem().getValue();
        Map arg = new HashMap();

        arg.put(WinSubjectEditorCtrl.SUBJECT, selectedSubject);
        arg.put(WinSubjectEditorCtrl.GROUP, groupList);
        arg.put(WinSubjectEditorCtrl.UPDATE_SUBJECTS, updateSubjects);

        ComponentHelper.createWindow("/passportGroup/winSubjectEditor.zul", "winSubjectEditor", arg).doModal();
    }

    @Listen("onClick = #btnDeleteSubject")
    public void deleteSubject() {
        SubjectReportModel selectedSubject = listSubject.getSelectedItem().getValue();
        long idLGSS = selectedSubject.getIdLgss();
        boolean hasRating = service.countSR(idLGSS) > 0 ? true : false;
        String winQuestion;

        if (hasRating) {
            winQuestion = "У текущего предмета есть оценки. Вы уверены, что хотите удалить предмет с удалением всех оценок по нему и истории посещаемости?";
        } else {
            winQuestion = "Вы уверены, что хотите удалить предмет?";
        }
        Messagebox.show(winQuestion, "Удаление предмета", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, (EventListener) evt -> {
            if (evt.getName().equals("onYes")) {
                try {
                    boolean hasMultipleFoc = countSubjectFoc(idLGSS) > 1 ? true : false;

                    if (service.deleteSubject(idLGSS, hasRating, hasMultipleFoc, selectedSubject.getFoc())) {
                        PopupUtil.showInfo("Предмет был удален");
                        fillSubjects();

                        log.info("Пользователь " + template.getCurrentUser().getFio()
                                + " удалил предмет " + selectedSubject.getSubjectName()
                                + " у группы " + selectedSubject.getGroupName());
                    } else {
                        PopupUtil.showError("Не удалось удалить предмет");

                        log.error("Пользователь " + template.getCurrentUser().getFio()
                                + " не смог удалить предмет " + selectedSubject.getSubjectName()
                                + " у группы " + selectedSubject.getGroupName());
                    }
                } catch (Exception e) {
                    PopupUtil.showError("Не удалось удалить предмет");
                }
            }
        });
    }

    public int countSubjectFoc(long idLGSS) {
        int count = 0;

        for (SubjectReportModel subject : (List<SubjectReportModel>) listSubject.getListModel()) {
            if (subject.getIdLgss() == idLGSS) {
                count++;
            }
        }

        return count;
    }

}
