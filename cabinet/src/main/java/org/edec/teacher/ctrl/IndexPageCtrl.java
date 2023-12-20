package org.edec.teacher.ctrl;

import org.edec.teacher.ctrl.renderer.RetakeRenderer;
import org.edec.teacher.ctrl.renderer.commission.CommissionRenderer;
import org.edec.teacher.ctrl.renderer.SubjectRenderer;
import org.edec.teacher.model.RetakeModel;
import org.edec.teacher.model.SemesterModel;
import org.edec.teacher.model.commission.CommissionModel;
import org.edec.teacher.service.CompletionCommissionService;
import org.edec.teacher.service.CompletionService;
import org.edec.teacher.service.impl.CompletionCommissionImpl;
import org.edec.teacher.service.impl.CompletionServiceImpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RegisterType;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.*;


public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Checkbox chRegisterOnlyUnsigned;

    /**
     * Для обычных ведомостей
     **/
    @Wire
    private Combobox cmbInst, cmbFormOfStudy, cmbSem;

    /**
     * Для комиссионных ведомостей
     **/
    @Wire
    private Listbox lbCommission, lbSubject, lbRetake;
    @Wire
    private Radio rAll, rOnlySigned, rOnlyUnSigned, rOnlySignedWithoutDigitalKey;
    @Wire
    private Radio rRetakeAll, rRetakeOnlySigned, rRetakeOnlyUnSigned,rRetakeOnlySignWithoutKey;
    @Wire
    private Tabpanel tpSubject, tpCommission, tpRetake;
    @Wire
    private Button btnCourseAutoBinding;
    @Wire
    private Datebox dbRetakeDate, dbCommissionDate;
    @Wire
    private Textbox tbRetakeSubject, tbRetakeGroup, tbRetakeFoc, tbRetakeType;
    @Wire
    private Textbox tbCommissionSubject, tbCommissionClassroom, tbCommissionFoc;

    @Wire
    private Tab tabRetake, tabCommission;

    private CompletionService completionService = new CompletionServiceImpl();
    private CompletionCommissionService commissionService = new CompletionCommissionImpl();

    private List<SemesterModel> semesters;
    private List<SemesterModel> semestersCommission;
    private List<RetakeModel> fullRetakeList;
    private List<CommissionModel> fullComissionList;

    protected void fill () {
        Clients.showBusy(tpSubject, "Загрузка данных");
        lbSubject.setItemRenderer(new SubjectRenderer(completionService.getEsoCourses()));
        lbCommission.setItemRenderer(new CommissionRenderer(this::refreshCommission));
        lbRetake.setItemRenderer(new RetakeRenderer());
        refreshRetakes();
        refreshCommission();
        searchOnlyUnsignedRegister();
    }

    @Listen("onCheck = #chRegisterOnlyUnsigned")
    public void searchOnlyUnsignedRegister () {
        Clients.showBusy(tpSubject, "Загрузка данных");
        semesters = completionService.getSemesterByHumanface(getCurrentUser().getIdHum(), chRegisterOnlyUnsigned.isChecked());
        fillCmbInstAndEchoEvent(semesters, cmbInst);
    }

    @Listen("onChange = #cmbInst")
    public void changeCmbInst () {
        fillCmbFOSandEchoEvent(semesters, cmbInst, cmbFormOfStudy);
    }

    @Listen("onChange = #cmbFormOfStudy")
    public void changeCmbFormOfStudy (Event e) {
        fillCmbSemAndEchoEvent(semesters, cmbInst, cmbFormOfStudy, cmbSem);
    }

    @Listen("onChange = #cmbSem")
    public void changeCmbSem () {
        SemesterModel selectedSemester = cmbSem.getSelectedItem().getValue();
        lbSubject.setModel(new ListModelList<Object>(selectedSemester.getSubjects()));
        lbSubject.renderAll();
        Clients.clearBusy(tpSubject);
    }

    @Listen("onSelect = #tabCommission")
    public void fillCommission () {
        rRetakeOnlyUnSigned.setSelected(true);
        refreshCommission();
    }

    @Listen("onCheck = #rAll; onCheck = #rOnlySigned; onCheck = #rOnlyUnSigned; onCheck = #rOnlySignedWithoutDigitalKey")
    public void checkShowSignComm () {
        refreshCommission();
    }

    @Listen("onCheck = #rRetakeAll; onCheck = #rRetakeOnlySigned; onCheck = #rRetakeOnlyUnSigned; onCheck = #rRetakeOnlySignWithoutKey")
    public void checkShowSignRetake () {
        refreshRetakes();
    }

    private void refreshCommission () {
        Clients.showBusy(tpCommission, "Загрузка данных");
        lbCommission.getItems().clear();
        Integer signedRegisters = 0;
        if(rAll.isSelected()){
            signedRegisters = 0;
        }
        if(rOnlySigned.isSelected()){
            signedRegisters = 1;
        }
        if(rOnlyUnSigned.isSelected()){
            signedRegisters = 2;
        }
        if (rOnlySignedWithoutDigitalKey.isSelected()){
            signedRegisters = 3;
        }
        fullComissionList = commissionService.getListCommByHum(getCurrentUser().getIdHum(), signedRegisters);
        int countWarrning = 0;
        for (CommissionModel commissionModel : fullComissionList) {
            if(!commissionModel.isSigned()) {
                countWarrning++;
            }
        }
        ListModelList lmlComission = new ListModelList<>(fullComissionList);
        lbCommission.setModel(lmlComission);
        if(countWarrning > 0) {
            tabCommission.setLabel("Комиссии ("+countWarrning+")");
        }else{
            tabCommission.setLabel("Комиссии");
        }
        Clients.clearBusy(tpCommission);
    }

    /**
     * Обновление списка пересдач для вкладки пересдач
     */
    private void refreshRetakes () {
        Clients.showBusy(tpRetake, "Загрузка данных");
        lbRetake.getItems().clear();
        Integer filter = 0;
        if(rRetakeAll.isSelected()){
            filter = 0;
        }
        if(rRetakeOnlySigned.isSelected()){
            filter = 1;
        }
        if(rRetakeOnlyUnSigned.isSelected()){
            filter = 2;
        }
        if (rRetakeOnlySignWithoutKey.isSelected()){
            filter = 3;
        }
        fullRetakeList = completionService.getRetakesForHum(template.getCurrentUser().getIdHum(), filter);
        int countWarrning = 0;
        for (RetakeModel retakeModel : fullRetakeList) {
            if(retakeModel.getSignDate() == null) {
                countWarrning++;
            }
        }
        ListModelList lmlRetake = new ListModelList<Object>(fullRetakeList);
        lbRetake.setModel(lmlRetake);
        if(countWarrning > 0) {
            tabRetake.setLabel("Пересдачи ("+countWarrning+")");
        }else{
            tabRetake.setLabel("Пересдачи");
        }
        Clients.clearBusy(tpSubject);
        Clients.clearBusy(tpRetake);
    }

    @Listen("onChanging = #tbRetakeSubject")
    public void changeRetakeSubName(InputEvent event) {
        tbRetakeSubject.setValue(event.getValue());
        filterRetakes();
    }
    @Listen("onChanging = #tbRetakeType")
    public void changeRetakeType(InputEvent event) {
        tbRetakeType.setValue(event.getValue());
        filterRetakes();
    }
    @Listen("onChanging = #tbRetakeGroup")
    public void changeRetakeGroup(InputEvent event) {
        tbRetakeGroup.setValue(event.getValue());
        filterRetakes();
    }
    @Listen("onChanging = #tbRetakeFoc")
    public void changeRetakeFoc(InputEvent event) {
        tbRetakeFoc.setValue(event.getValue());
        filterRetakes();
    }
    @Listen("onChange = #dbRetakeDate")
    public void changeRetakeDate() {
        filterRetakes();
    }

    public void filterRetakes(){
        List<RetakeModel> filtredList = new ArrayList<>();
        for (RetakeModel retakeModel : fullRetakeList) {
            if(retakeModel.getSubject().getSubjectname().toLowerCase().contains(tbRetakeSubject.getValue().toLowerCase())
                    && retakeModel.getGroup().getGroupname().toLowerCase().contains(tbRetakeGroup.getValue().toLowerCase() )
                    && RegisterType.getRegisterTypeByRetakeCount(retakeModel.getRetakeCount()).getName().toLowerCase().contains(tbRetakeType.getValue().toLowerCase())
                    && FormOfControlConst.getName(retakeModel.getSubject().getFormofcontrol()).getName().toLowerCase().contains(tbRetakeFoc.getValue().toLowerCase())
            ) {
                if(dbRetakeDate.getValue()!=null) {
                    if(retakeModel.getDateOfRetake().equals(dbRetakeDate.getValue())){
                        filtredList.add(retakeModel);
                    }
                } else {
                    filtredList.add(retakeModel);
                }
            }
        }
        lbRetake.getItems().clear();
        ListModelList lmlRetake = new ListModelList<Object>(filtredList);
        lbRetake.setModel(lmlRetake);
    }

    @Listen("onChanging = #tbCommissionSubject")
    public void changeCommissionSubName(InputEvent event) {
        tbCommissionSubject.setValue(event.getValue());
        filterCommission();
    }
    @Listen("onChanging = #tbCommissionClassroom")
    public void changeCommissionGroup(InputEvent event) {
        tbCommissionClassroom.setValue(event.getValue());
        filterCommission();
    }
    @Listen("onChanging = #tbCommissionFoc")
    public void changeCommissionFoc(InputEvent event) {
        tbCommissionFoc.setValue(event.getValue());
        filterCommission();
    }
    @Listen("onChange = #dbCommissionDate")
    public void changeCommissionDate() {
        filterCommission();
    }

    public void filterCommission(){
        List<CommissionModel> filtredList = new ArrayList<>();
        for (CommissionModel commissionModel : fullComissionList) {
            if(commissionModel.getSubjectName().toLowerCase().contains(tbCommissionSubject.getValue().toLowerCase())
                    && commissionModel.getClassroom().toLowerCase().contains(tbCommissionClassroom.getValue().toLowerCase() )
                    && commissionModel.getFocStr().toLowerCase().contains(tbCommissionFoc.getValue().toLowerCase())
            ) {
                if(dbCommissionDate.getValue()!=null) {
                    if(commissionModel.getComissionDate().equals(dbCommissionDate.getValue())){
                        filtredList.add(commissionModel);
                    }
                } else {
                    filtredList.add(commissionModel);
                }
            }
        }
        lbCommission.getItems().clear();
        ListModelList lmlCommission = new ListModelList<Object>(filtredList);
        lbCommission.setModel(lmlCommission);
    }
    private void fillCmbInstAndEchoEvent (List<SemesterModel> semesters, Combobox cmbInst) {
        cmbInst.getChildren().clear();
        Set<String> institutes = completionService.getInstitutesByModelSemester(semesters);
        for (String inst : institutes) {
            new Comboitem(inst).setParent(cmbInst);
        }

        if (institutes.size() == 0) {
            Clients.clearBusy(tpSubject);
            cmbInst.getChildren().clear();
            cmbInst.setValue("");
            cmbFormOfStudy.setValue("");
            cmbFormOfStudy.getChildren().clear();
            cmbSem.getChildren().clear();
            cmbSem.setValue("");
            lbSubject.getItems().clear();
            btnCourseAutoBinding.setDisabled(true);

            return;
        }
        cmbInst.setSelectedIndex(0);

        Events.echoEvent("onChange", cmbInst, null);
    }

    private void fillCmbFOSandEchoEvent (List<SemesterModel> semesters, Combobox cmbInst, Combobox cmbFOS) {
        cmbFOS.getChildren().clear();
        Set<String> formOfStudies = completionService.getFormOfStudyByInst(cmbInst.getValue(), semesters);
        for (String fos : formOfStudies) {
            new Comboitem(fos).setParent(cmbFOS);
        }
        cmbFOS.setSelectedIndex(0);
        Events.echoEvent("onChange", cmbFOS, null);
    }

    private void fillCmbSemAndEchoEvent (List<SemesterModel> semesters, Combobox cmbInst, Combobox cmbFOS, Combobox cmbSem) {
        cmbSem.getChildren().clear();
        int indexSem = 0;
        List<SemesterModel> semestersForCmb = completionService.getSemesterByFOSandInst(cmbInst.getValue(), cmbFOS.getValue(), semesters);
        for (SemesterModel semester : semestersForCmb) {
            Comboitem ci = new Comboitem(semester.getSemesterStr());
            ci.setValue(semester);
            ci.setParent(cmbSem);
            if (semester.getCurSem()) {
                indexSem = semestersForCmb.indexOf(semester);
            }
        }

        cmbSem.setSelectedIndex(indexSem);
        Events.echoEvent("onChange", cmbSem, null);
    }

    @Listen("onClick = #btnCourseAutoBinding")
    public void openCourseBindingWindow () {
        Map arg = new HashMap();

        arg.put(WinCourseBindingCtrl.USER, template.getCurrentUser());
        arg.put(WinCourseBindingCtrl.ID_CURRENT_SEMESTER, ((SemesterModel) cmbSem.getSelectedItem().getValue()).getIdSemester());

        ComponentHelper.createWindow("/teacher/winCourseBinding.zul", "winCourseBinding", arg).doModal();
    }
}
