package org.edec.curriculumSchedule.ctrl;

import lombok.extern.log4j.Log4j;
import org.edec.curriculumSchedule.ctrl.renderer.GroupListRenderer;
import org.edec.curriculumSchedule.model.GroupModel;
import org.edec.curriculumSchedule.service.CurriculumScheduleService;
import org.edec.curriculumSchedule.service.iml.CurriculumScheduleServiceESO;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Vbox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j
public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Combobox cmbFormOfStudy, cmbSemester, cmbInst, cmbCourse;

    @Wire
    private Datebox dateOfBeginStudy, dateOfEndStudy, dateOfBeginSemester, dateOfEndSemester, dateOfBeginPassWeek,
            dateOfEndPassWeek, dateOfBeginSession, dateOfEndSession, dateOfBeginVacation, dateOfEndVacation,
            dateOfCertificationFrom, dateOfCertificationTo, dateOfBeginPractice, dateOfEndPractice;

    @Wire
    private Listbox lbGroup;

    @Wire
    private Button btnSaveSemesterDates, btnSaveDatesOfStudy;

    @Wire
    private Vbox vbInst, vbFormOfStudy, vbSemester, vbCourse, vbQualification;

    @Wire
    private Checkbox engineer, bachelor, master;

    private List<GroupModel> groups;
    private CurriculumScheduleService service = new CurriculumScheduleServiceESO();
    private ComponentService componentService = new ComponentServiceESOimpl();

    private InstituteModel currentInstituteShow;

    protected void fill () {
        lbGroup.setItemRenderer(new GroupListRenderer());
        currentInstituteShow = componentService.fillCmbInst(cmbInst, vbInst, currentModule.getDepartments());
        componentService.fillCmbFormOfStudy(cmbFormOfStudy, vbFormOfStudy, currentModule.getFormofstudy());
        if (cmbFormOfStudy.getItemCount() == 1) {
            componentService.fillCmbSem(cmbSemester, currentInstituteShow.getIdInst(),
                                        ((FormOfStudy) cmbFormOfStudy.getSelectedItem().getValue()).getType(), null
            );
        }
        engineer.setChecked(true);
        bachelor.setChecked(true);
        master.setChecked(true);
    }

    @Listen("onChange = #cmbInst; onChange = #cmbFormOfStudy;")
    public void changeInstAndFOS () {
        if (cmbInst.getSelectedItem().getValue() == null || ((FormOfStudy) cmbFormOfStudy.getSelectedItem().getValue()).getType() == 3) {
            cmbSemester.setSelectedItem(null);
            cmbSemester.setValue("");
            cmbCourse.setValue("");
            vbSemester.setVisible(false);
            vbCourse.setVisible(false);
            vbQualification.setVisible(false);
            lbGroup.setModel(new ListModelList<>());
            return;
        }

        vbSemester.setVisible(true);
        vbQualification.setVisible(true);
        componentService.fillCmbSem(cmbSemester, ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInst(),
                                    ((FormOfStudy) cmbFormOfStudy.getSelectedItem().getValue()).getType(), null
        );
        lbGroup.setModel(new ListModelList<>());
    }

    @Listen("onChange = #cmbSemester")
    public void selectedSemester () {
        cmbCourse.setSelectedIndex(0);
        fillGroups();
        fillGroupDates();
        lbGroup.addEventListener(Events.ON_SELECT, event -> fillGroupDates());
        cmbCourse.setDisabled(false);
    }

    @Listen("onChange = #cmbCourse")
    public void changeFilterGroup () {
        fillGroups();
    }

    @Listen("onClick = #engineer, #bachelor, #master;")
    public void changeQualification () {
        fillGroups();
    }

    private void fillGroups () {
        if (cmbSemester.getSelectedIndex() != -1) {
            lbGroup.setModel(new ListModelList<>());
            SemesterModel model = cmbSemester.getSelectedItem().getValue();
            groups = service.getGroupsByFilter(model.getIdSem(), cmbCourse.getSelectedIndex(), bachelor.isChecked(), master.isChecked(),
                                               engineer.isChecked()
            );
            ListModelList<GroupModel> lmGroup = new ListModelList<>(groups);
            lmGroup.setMultiple(true);
            lbGroup.setItemRenderer(new GroupListRenderer());
            lbGroup.setMultiple(true);
            lbGroup.setCheckmark(true);
            lbGroup.setAttribute("data", groups);
            lbGroup.setModel(lmGroup);
            lbGroup.renderAll();
            Clients.showBusy(lbGroup, "Загрузка данных");
            lbGroup.addEventListener("onFill", event -> Clients.clearBusy(lbGroup));
            Events.echoEvent("onFill", lbGroup, null);
        }
    }

    private void fillGroupDates () {

        if (lbGroup.getSelectedItems().size() != 0) {
            groupDatesComparison();
        } else {
            dateOfBeginStudy.setValue(null);
            dateOfEndStudy.setValue(null);
            dateOfBeginSemester.setValue(null);
            dateOfEndSemester.setValue(null);
            dateOfBeginPassWeek.setValue(null);
            dateOfEndPassWeek.setValue(null);
            dateOfBeginSession.setValue(null);
            dateOfEndSession.setValue(null);
            dateOfBeginVacation.setValue(null);
            dateOfEndVacation.setValue(null);
            dateOfCertificationFrom.setValue(null);
            dateOfCertificationTo.setValue(null);
            dateOfBeginPractice.setValue(null);
            dateOfEndPractice.setValue(null);
        }
    }

    /* Если выбранных групп больше одной, то сравниваем выбранные группы по каждому полю: в случае совпадения значений
     * одного поля для каждой из выбранных групп - вставляем значение поля  */
    private void groupDatesComparison () {

        List<GroupModel> selectedGroups = lbGroup.getSelectedItems()
                                                 .stream()
                                                 .map(s -> (GroupModel) s.getValue())
                                                 .collect(Collectors.toList());

        GroupModel groupModel = selectedGroups.get(0);

        dateOfBeginStudy.setValue(groupModel.getDateOfBeginStudy());
        dateOfEndStudy.setValue(groupModel.getDateOfEndStudy());
        dateOfBeginSemester.setValue(groupModel.getDateOfBeginSemester());
        dateOfEndSemester.setValue(groupModel.getDateOfEndSemester());
        dateOfBeginPassWeek.setValue(groupModel.getDateOfBeginPassWeek());
        dateOfEndPassWeek.setValue(groupModel.getDateOfEndPassWeek());
        dateOfBeginSession.setValue(groupModel.getDateOfBeginSession());
        dateOfEndSession.setValue(groupModel.getDateOfEndSession());
        dateOfBeginVacation.setValue(groupModel.getDateOfBeginVacation());
        dateOfEndVacation.setValue(groupModel.getDateOfEndVacation());
        dateOfCertificationFrom.setValue(groupModel.getDateOfCertificationFrom());
        dateOfCertificationTo.setValue(groupModel.getDateOfCertificationTo());
        dateOfBeginPractice.setValue(groupModel.getDateOfBeginPractice());
        dateOfEndPractice.setValue(groupModel.getDateOfEndPractice());

        selectedGroups.forEach(group -> {
            if (dateOfBeginStudy.getValue() != null && !dateOfBeginStudy.getValue().equals(group.getDateOfBeginStudy())) {
                dateOfBeginStudy.setValue(null);
            }
            if (dateOfEndStudy.getValue() != null && !dateOfEndStudy.getValue().equals(group.getDateOfEndStudy())) {
                dateOfEndStudy.setValue(null);
            }
            if (dateOfBeginSemester.getValue() != null && !dateOfBeginSemester.getValue().equals(group.getDateOfBeginSemester())) {
                dateOfBeginSemester.setValue(null);
            }
            if (dateOfEndSemester.getValue() != null && !dateOfEndSemester.getValue().equals(group.getDateOfEndSemester())) {
                dateOfEndSemester.setValue(null);
            }
            if (dateOfBeginPassWeek.getValue() != null && !dateOfBeginPassWeek.getValue().equals(group.getDateOfBeginPassWeek())) {
                dateOfBeginPassWeek.setValue(null);
            }
            if (dateOfEndPassWeek.getValue() != null && !dateOfEndPassWeek.getValue().equals(group.getDateOfEndPassWeek())) {
                dateOfEndPassWeek.setValue(null);
            }
            if (dateOfBeginSession.getValue() != null && !dateOfBeginSession.getValue().equals(group.getDateOfBeginSession())) {
                dateOfBeginSession.setValue(null);
            }
            if (dateOfEndSession.getValue() != null && !dateOfEndSession.getValue().equals(group.getDateOfEndSession())) {
                dateOfEndSession.setValue(null);
            }
            if (dateOfBeginVacation.getValue() != null && !dateOfBeginVacation.getValue().equals(group.getDateOfBeginVacation())) {
                dateOfBeginVacation.setValue(null);
            }
            if (dateOfEndVacation.getValue() != null && !dateOfEndVacation.getValue().equals(group.getDateOfEndVacation())) {
                dateOfEndVacation.setValue(null);
            }
            if (dateOfCertificationFrom.getValue() != null && !dateOfCertificationFrom.getValue().equals(group.getDateOfCertificationFrom())) {
                dateOfCertificationFrom.setValue(null);
            }
            if (dateOfCertificationTo.getValue() != null && !dateOfCertificationTo.getValue().equals(group.getDateOfCertificationTo())) {
                dateOfCertificationTo.setValue(null);
            }

            if (dateOfBeginPractice.getValue() != null && !dateOfBeginPractice.getValue().equals(group.getDateOfBeginPractice())) {
                dateOfBeginPractice.setValue(null);
            }

            if (dateOfEndPractice.getValue() != null && !dateOfEndPractice.getValue().equals(group.getDateOfEndPractice())) {
                dateOfEndPractice.setValue(null);
            }
        });
    }

    @Listen("onClick = #btnSaveDatesOfStudy;")
    public void setDatesOfStudy () {
        if (lbGroup.getSelectedCount() != 0) {
            List<Listitem> groupItems = new ArrayList<>();

            groupItems.addAll(lbGroup.getSelectedItems());
            for (Listitem group : groupItems) {
                GroupModel groupModel = group.getValue();

                if (dateOfBeginStudy.getValue() != null) {
                    groupModel.setDateOfBeginStudy(dateOfBeginStudy.getValue());
                }
                if (dateOfEndStudy.getValue() != null) {
                    groupModel.setDateOfEndStudy(dateOfEndStudy.getValue());
                }
                if (dateOfCertificationFrom.getValue() != null) {
                    groupModel.setDateOfCertificationFrom(dateOfCertificationFrom.getValue());
                }
                if (dateOfCertificationTo.getValue() != null) {
                    groupModel.setDateOfCertificationTo(dateOfCertificationTo.getValue());
                }
                if(dateOfBeginPractice.getValue() != null){
                    groupModel.setDateOfBeginPractice(dateOfBeginPractice.getValue());
                }
                if(dateOfEndPractice.getValue() != null){
                    groupModel.setDateOfBeginPractice(dateOfEndPractice.getValue());
                }
                if (!service.updateGroupInformation(groupModel)) {
                    PopupUtil.showError("Сроки обучения обновить не удалось!");
                    return;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                log.info("Пользователь " + template.getCurrentUser().getFio() + " изменил сроки обучения у группы " +
                         groupModel.getGroupName() + ((dateOfBeginStudy.getValue() != null)
                                                      ? " начало обучения: " + dateFormat.format(dateOfBeginStudy.getValue())
                                                      : "") +
                         ((dateOfEndStudy.getValue() != null) ? " конец обучения: " + dateFormat.format(dateOfEndStudy.getValue()) : ""));
            }
            PopupUtil.showInfo("Сроки обучения успешно обновлены!");
        } else {
            PopupUtil.showWarning("Сначала выберите группу");
        }
    }

    @Listen("onClick = #btnSaveSemesterDates;")
    public void saveSemesterDates () {
        if (lbGroup.getSelectedCount() != 0) {
            List<Listitem> groupItems = new ArrayList<>();
            groupItems.addAll(lbGroup.getSelectedItems());
            for (Listitem group : groupItems) {
                GroupModel groupModel = group.getValue();
                if (dateOfBeginSemester.getValue() != null) {
                    groupModel.setDateOfBeginSemester(dateOfBeginSemester.getValue());
                }
                if (dateOfEndSemester.getValue() != null) {
                    groupModel.setDateOfEndSemester(dateOfEndSemester.getValue());
                }
                if (dateOfBeginSession.getValue() != null) {
                    groupModel.setDateOfBeginSession(dateOfBeginSession.getValue());
                }
                if (dateOfEndSession.getValue() != null) {
                    groupModel.setDateOfEndSession(dateOfEndSession.getValue());
                }
                if (dateOfBeginPassWeek.getValue() != null) {
                    groupModel.setDateOfBeginPassWeek(dateOfBeginPassWeek.getValue());
                }
                if (dateOfEndPassWeek.getValue() != null) {
                    groupModel.setDateOfEndPassWeek(dateOfEndPassWeek.getValue());
                }
                if (dateOfBeginVacation.getValue() != null) {
                    groupModel.setDateOfBeginVacation(dateOfBeginVacation.getValue());
                }
                if (dateOfEndVacation.getValue() != null) {
                    groupModel.setDateOfEndVacation(dateOfEndVacation.getValue());
                }
                if(dateOfBeginPractice.getValue()!=null){
                    groupModel.setDateOfBeginPractice(dateOfBeginPractice.getValue());
                }
                if(dateOfEndPractice.getValue()!=null){
                    groupModel.setDateOfEndPractice(dateOfEndPractice.getValue());
                }

                if (!service.updateSemesterGroupInformation(groupModel)) {
                    PopupUtil.showError("График УП обновить не удалось!");
                    return;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                log.info("Пользователь " + template.getCurrentUser().getFio() + " изменил график УП у группы " + groupModel.getGroupName() +
                         ((dateOfBeginSemester.getValue() != null) ? " начало семестра: " +
                                                                     dateFormat.format(dateOfBeginSemester.getValue()) : "") +
                         ((dateOfEndSemester.getValue() != null)
                          ? " конец семестра: " + dateFormat.format(dateOfEndSemester.getValue())
                          : "") + ((dateOfBeginSession.getValue() != null)
                                   ? " начало сессии: " + dateFormat.format(dateOfBeginSession.getValue())
                                   : "") +
                         ((dateOfEndSession.getValue() != null) ? " конец сессии: " + dateFormat.format(dateOfEndSession.getValue()) : "") +
                         ((dateOfBeginPassWeek.getValue() != null) ? " начало зачетной недели " +
                                                                     dateFormat.format(dateOfBeginPassWeek.getValue()) : "") +
                         ((dateOfEndPassWeek.getValue() != null) ? " конец зачетной недели " +
                                                                   dateFormat.format(dateOfEndPassWeek.getValue()) : "") +
                         ((dateOfBeginVacation.getValue() != null)
                          ? " начало каникул " + dateFormat.format(dateOfBeginVacation.getValue())
                          : "") + ((dateOfEndVacation.getValue() != null)
                                   ? " конец каникул " + dateFormat.format(dateOfEndVacation.getValue())
                                   : ""));
            }
            PopupUtil.showInfo("График УП успешно обновлен!");
        } else {
            PopupUtil.showWarning("Сначала выберите группу");
        }
    }
}