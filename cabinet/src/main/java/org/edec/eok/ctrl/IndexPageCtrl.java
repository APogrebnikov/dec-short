package org.edec.eok.ctrl;

import org.edec.eok.ctrl.renderer.SubjectRenderer;
import org.edec.eok.model.SubjectModel;
import org.edec.eok.service.EokService;
import org.edec.eok.service.impl.EokESOimpl;
import org.edec.teacher.ctrl.renderer.EsoCourseRenderer;
import org.edec.teacher.model.EsoCourseModel;
import org.edec.teacher.service.CompletionService;
import org.edec.teacher.service.impl.CompletionServiceImpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.component.model.GroupModel;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.report.service.eok.EokReportProgressService;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.report.service.eok.EokReportService;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.List;

public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Bandbox bdSetEok;
    @Wire
    private Checkbox chSpec, chBach, chMaster, chEOK, chTeacher;
    @Wire
    private Combobox cmbCourse, cmbInst, cmbFormOfStudy, cmbSemester;
    @Wire
    private Groupbox gbFilter;
    @Wire
    private Listbox lbGroup, lbSubject, lbSetEok;
    @Wire
    private Longbox longSetEokId;
    @Wire
    private Vbox vbInst, vbFormOfStudy, vbSemester, vbQualification;
    @Wire
    private Textbox tbGroup, tbDepartment, tbSubject, tbSetEokName;

    private ComponentService componentService = new ComponentServiceESOimpl();
    private CompletionService completionService = new CompletionServiceImpl();
    private EokService eokService = new EokESOimpl();

    private List<EsoCourseModel> esoCourses;
    private List<SubjectModel> subjects;

    protected void fill () {
        lbSetEok.setItemRenderer(new EsoCourseRenderer());
        esoCourses = completionService.getEsoCourses();
        componentService.fillCmbInst(cmbInst, vbInst, currentModule.getDepartments());
        componentService.fillCmbFormOfStudy(cmbFormOfStudy, vbFormOfStudy, currentModule.getFormofstudy());
        //Если правами задан уже институт и форма обучения, то загружаем семестры по этим данным
        if (!vbInst.isVisible() && !vbFormOfStudy.isVisible()) {
            Events.echoEvent(Events.ON_CHANGE, cmbFormOfStudy, null);
        }
    }

    @Listen("onChange = #cmbInst; onChange = #cmbFormOfStudy;")
    public void changeInstAndFOS () {
        if (cmbInst.getSelectedItem().getValue() == null || ((FormOfStudy) cmbFormOfStudy.getSelectedItem().getValue()).getType() == 3) {
            cmbSemester.setSelectedItem(null);
            cmbSemester.setValue("");
            vbSemester.setVisible(false);
            lbGroup.setModel(new ListModelList<>());
            lbSubject.setModel(new ListModelList<>());
            return;
        }
        vbSemester.setVisible(true);
        componentService.fillCmbSem(cmbSemester, ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInst(),
                                    ((FormOfStudy) cmbFormOfStudy.getSelectedItem().getValue()).getType(), null
        );
        lbGroup.setModel(new ListModelList<>());
        lbSubject.setModel(new ListModelList<>());
    }

    @Listen("onChange = #cmbSemester")
    public void selectedSem () {
        if (!chSpec.isChecked() && !chBach.isChecked() && !chMaster.isChecked()) {
            PopupUtil.showWarning("Выберите хотя бы одну квалификацию");
            return;
        }
        tbGroup.setValue("");
        cmbCourse.setSelectedIndex(0);
        tbDepartment.setValue("");
        tbSubject.setValue("");
        lbGroup.setModel(new ListModelList<>());
        lbSubject.setModel(new ListModelList<>());
        String qualification = "";
        if (chSpec.isChecked()) {
            qualification += "1,";
        }
        if (chBach.isChecked()) {
            qualification += "2,";
        }
        if (chMaster.isChecked()) {
            qualification += "3,";
        }
        qualification = qualification.substring(0, qualification.length() - 1);
        componentService.fillListboxGroup(
                lbGroup, true, ((SemesterModel) cmbSemester.getSelectedItem().getValue()).getIdSem(), qualification);
    }

    @Listen("onOK=#tbGroup;onChange=#cmbCourse;")
    public void searchGroup () {
        componentService.searchInListbox(lbGroup, tbGroup.getValue().equals("") ? null : tbGroup.getValue(),
                                         cmbCourse.getSelectedIndex() == 0 ? null : cmbCourse.getSelectedIndex(), true
        );
    }

    @Listen("onClick = #btnSearch")
    public void searchSubject () {
        if (lbGroup.getSelectedCount() == 0) {
            PopupUtil.showWarning("Выберите хотя бы одну группу.");
            return;
        }
        subjects = null;
        String idLGS = "";
        for (Listitem li : lbGroup.getSelectedItems()) {
            GroupModel groupModel = li.getValue();
            idLGS += groupModel.getIdLGS() + ",";
        }
        idLGS = idLGS.substring(0, idLGS.length() - 1);
        lbSubject.setItemRenderer(new SubjectRenderer(esoCourses));
        Clients.showBusy(lbSubject, "Загрузка данных");
        Events.echoEvent("onFill", lbSubject, idLGS);
    }

    @Listen("onOK = #tbDepartment; onOK = #tbSubject; onCheck = #chEOK; onCheck = #chTeacher;")
    public void searchSubjectsByFilter () {
        Clients.showBusy(lbSubject, "Загрузка данных");
        Events.echoEvent("onFill", lbSubject, null);
    }

    @Listen("onFill = #lbSubject")
    public void fillSubject (Event e) {
        String idLGS = (String) e.getData();
        List<SubjectModel> result;
        if (subjects == null) {
            subjects = eokService.getSubjects(idLGS);
            result = subjects;
        } else {
            result = eokService.getSubjectsByFilter(
                    subjects, tbDepartment.getValue(), tbSubject.getValue(), chEOK.isChecked(), chTeacher.isChecked());
        }
        ListModelList<SubjectModel> lmSubject = new ListModelList<>(result);
        lmSubject.setMultiple(true);
        lbSubject.setCheckmark(true);
        lbSubject.setMultiple(true);
        lbSubject.setModel(lmSubject);
        lbSubject.renderAll();
        Clients.clearBusy(lbSubject);
    }

    @Listen("onOpen = #bdSetEok")
    public void setEok () {
        tbSetEokName.setValue("");
        longSetEokId.setValue(null);
        lbSetEok.setModel(new ListModelList<>(completionService.getFilteredEsoCourses(null, null, esoCourses)));
        lbSetEok.renderAll();
    }

    @Listen("onOK = #tbSetEokName; onOK = #longSetEokId;")
    public void onOKsetEOK () {
        lbSetEok.setModel(new ListModelList<>(
                completionService.getFilteredEsoCourses(tbSetEokName.getValue().equals("") ? null : tbSetEokName.getValue(),
                                                        longSetEokId.getValue(), esoCourses
                )));
        lbSetEok.renderAll();
    }

    @Listen("onSelect = #lbSetEok")
    public void selectedListboxSetEok () {
        if (lbSubject.getSelectedCount() == 0) {
            PopupUtil.showWarning("Выберите хотя бы один предмет из списка");
            return;
        }
        EsoCourseModel esoCourse = lbSetEok.getSelectedItem().getValue();
        for (Listitem li : lbSubject.getSelectedItems()) {
            SubjectModel subject = li.getValue();
            subject.setIdEsoCourse(esoCourse.getIdEsoCourse());
            completionService.updateESOcourse(subject.getIdLGSS(), esoCourse.getIdEsoCourse());
        }
        bdSetEok.close();
        Clients.showBusy(lbSubject, "Загрузка данных");
        Events.echoEvent("onFill", lbSubject, null);
    }

    @Listen("onClick = #btnReport")
    public void showReport () {
        if (cmbSemester.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите семестр");
            return;
        }
        EokReportService eokReportService = new EokReportService();
        Filedownload.save(eokReportService.getEokXlsBySem(cmbSemester.getSelectedItem().getValue()));
    }

    @Listen("onClick = #btnReportProgress")
    public void showProgressReport(){
        if (cmbSemester.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите семестр");
            return;
        }
        EokReportProgressService eokReportService = new EokReportProgressService();
        Filedownload.save(eokReportService.getEokProgressXlsBySem(cmbSemester.getSelectedItem().getValue()));
    }
}