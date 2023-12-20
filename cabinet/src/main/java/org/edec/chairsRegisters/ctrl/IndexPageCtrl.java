package org.edec.chairsRegisters.ctrl;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.edec.chairsRegisters.ctrl.renderer.ChairsComissionRegisterRenderer;
import org.edec.chairsRegisters.ctrl.renderer.ChairsRegisterRenderer;
import org.edec.chairsRegisters.ctrl.renderer.ChairsRetakeRegisterRenderer;
import org.edec.chairsRegisters.docReport.service.RetakesScheduleService;
import org.edec.chairsRegisters.model.ChairsDepartmentModel;
import org.edec.chairsRegisters.model.ChairsRegisterModel;
import org.edec.chairsRegisters.model.GroupBySemRegisterModel;
import org.edec.chairsRegisters.service.ChairsRegisterService;
import org.edec.chairsRegisters.service.impl.ChairsRegisterServiceImpl;
import org.edec.main.model.UserModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Combobox cmbSemester;
    @Wire
    private Checkbox chbSign, chbUnsign, chbOverdue, chbWithNumber, chbBachelors, chbMaster, chbSpec;
    @Wire
    private Radio rFullTime, rExtramural;
    @Wire
    private Datebox dbFrom, dbTo;
    @Wire
    private Button btnSearch;
    // ------------ основные ведомости ------------------------------
    @Wire
    private Listbox lbMain;
    @Wire
    private Textbox tbMainFilterSubjectname, tbMainFilterGroupname, tbMainFilterTeacher, tbMainFilterNumberRegister;
    @Wire
    private Combobox cmbMainFilterFoc;
    @Wire
    private Tab tabMain, tabRetake, tabComission;
    // ------------- комиссии -----------------------------------------
    @Wire
    private Listbox lbComission;
    @Wire
    private Textbox tbComissionFilterSubjectname, tbComissionFilterGroupname, tbComissionFilterTeacher, tbComissionFilterRegisterNumber;
    @Wire
    private Combobox cmbComissionFilterFoc;
    // ------------ пересдачи --------------------------------------------
    @Wire
    private Listbox lbRetake;
    @Wire
    private Textbox tbRetakeFilterSubjectname, tbRetakeFilterGroupname, tbRetakeFilterTeacher, tbRetakeFilterRegisterNumber;
    @Wire
    private Combobox cmbRetakeFilterFoc;
    @Wire
    private Checkbox chbIndivRetake, chbCommonRetake;
    @Wire
    private Button btnPrintScheduleRetake;
    @Wire
    private Datebox dbRetakeFrom, dbRetakeTo;

    private ComponentService componentService = new ComponentServiceESOimpl();

    private ChairsRegisterService service = new ChairsRegisterServiceImpl();
    private List<ChairsRegisterModel> listMainRegisters = new ArrayList<>();
    private List<ChairsRegisterModel> listRetakeRegisters = new ArrayList<>();
    private List<ChairsRegisterModel> listComissionRegisters = new ArrayList<>();


    private List<ChairsRegisterModel> tmpListMainRegisters = new ArrayList<>();
    private List<ChairsRegisterModel> tmpListRetakeRegisters = new ArrayList<>();
    private List<ChairsRegisterModel> tmpListComissionRegisters = new ArrayList<>();
    private List<ChairsRegisterModel> tmpListRegister;
    private UserModel currentUser;

    private int fos;
    private SemesterModel selestedSem;
    private ChairsDepartmentModel departmen;


    @Override
    protected void fill() throws InterruptedException {
        rFullTime.setChecked(true);
        currentUser = template.getCurrentUser();
        departmen = service.getIdDepartment(currentUser.getIdHum());
        setCheckboxTrue();
        if (departmen==null) {
            PopupUtil.showWarning("Не удается определить кафедру, обратитесь к администратору.");
            return;
        } else {
            getSemesterByFos();
        }
    }

    @Listen("onClick = #rFullTime; onClick = #rExtramural;")
    public void getSemesterByFos() {
        if (departmen==null) {
            //PopupUtil.showWarning("Не удается определить кафедру, обратитесь к администратору.");
        } else {
            if (rFullTime.isChecked()) {
                fos = 1;
            } else if (rExtramural.isChecked()) {
                fos = 2;
            }
            componentService.fillCmbSem(cmbSemester, 1L, fos, null);
        }
    }

    @Listen("onClick = #btnPrintScheduleRetake")
    public void printScheduleRetake() throws IOException {
        if (dbRetakeFrom.getValue() != null && dbRetakeTo.getValue()!= null) {
            List<GroupBySemRegisterModel> listMainRegistersByDate = service.getRetakeRegiseterByPeriod(departmen.getIdDepartment(), selestedSem.getIdSem(), dbRetakeFrom.getValue() , dbRetakeTo.getValue());
            if (!listMainRegistersByDate.isEmpty()){
                String dateFromStr = DateConverter.convertDateToStringByFormat(dbRetakeFrom.getValue(), "dd.MM.yyyy");
                String dateToStr = DateConverter.convertDateToStringByFormat(dbRetakeTo.getValue(), "dd.MM.yyyy");
                XWPFDocument reportdoc = new RetakesScheduleService().createScheduleRetakes(listMainRegistersByDate, dateFromStr, dateToStr);
                File file = new File("Расписание пересдач с "+ dateFromStr + " по "+dateToStr +".docx");
                FileOutputStream outFile = new FileOutputStream(file);
                reportdoc.write(outFile);
                Filedownload.save(file, null);
            } else {
                PopupUtil.showInfo("Нет пересдач за выбранный период.");
            }

        } else {
            PopupUtil.showWarning("Заполните обе даты!");
            return;
        }

    }

    @Listen("onClick = #btnPrintScheduleRetakeXls")
    public void printScheduleRetakeXls () throws IOException {
        if (dbRetakeFrom.getValue() != null && dbRetakeTo.getValue()!= null) {
            List<GroupBySemRegisterModel> listMainRegistersByDate = service.getRetakeRegiseterByPeriod(departmen.getIdDepartment(), selestedSem.getIdSem(), dbRetakeFrom.getValue() , dbRetakeTo.getValue());
            if (!listMainRegistersByDate.isEmpty()){
                HSSFWorkbook report =  service.getRetakeRegisterReportXLS(listMainRegistersByDate);
                String dateFromStr = DateConverter.convertDateToStringByFormat(dbRetakeFrom.getValue(), "dd.MM.yyyy");
                String dateToStr = DateConverter.convertDateToStringByFormat(dbRetakeTo.getValue(), "dd.MM.yyyy");
                String fn = "Расписание пересдач с "+ dateFromStr + " по "+dateToStr +".xls";
                File file = new File(fn);
                FileOutputStream outFile = new FileOutputStream(file);
                report.write(outFile);
                Filedownload.save(file, null);

            } else {
                PopupUtil.showInfo("Нет пересдач за выбранный период.");
            }
        } else {
            PopupUtil.showWarning("Заполните обе даты!");
            return;
        }
    }


    @Listen("onAfterRender = #cmbSemester")
    public void setCurrentSemInCmb(){
        if (rFullTime.isChecked()) {
            fos = 1;
        } else if (rExtramural.isChecked()) {
            fos = 2;
        }
        for (int i = 0; i < cmbSemester.getItems().size(); i++) {
            if (((SemesterModel) cmbSemester.getItems().get(i).getValue()).isCurSem()) {
                cmbSemester.setSelectedIndex(i);
                selestedSem = cmbSemester.getSelectedItem().getValue();
            }
        }

        fillMainListbox();
        fillRetakeListbox();
        fillComissionListbox();
    }

    private void setCheckboxTrue(){
        chbSpec.setChecked(true);
        chbBachelors.setChecked(true);
        chbMaster.setChecked(true);

        chbWithNumber.setChecked(true);
        chbOverdue.setChecked(true);
        chbUnsign.setChecked(true);
        chbSign.setChecked(true);

        chbIndivRetake.setChecked(true);
        chbCommonRetake.setChecked(true);
    }


    @Listen("onClick = #btnSearch")
    public void fillRegistersListbox() {
        if (cmbSemester.getSelectedIndex() != -1) {
            selestedSem = cmbSemester.getSelectedItem().getValue();
            fillMainListbox();
            fillRetakeListbox();
            fillComissionListbox();
            setCheckboxTrue();
        } else {
            PopupUtil.showWarning("Выберите семестр!");
        }
    }

    private void fillMainListbox() {
        listMainRegisters = service.getListMainRegisters(departmen.getIdDepartment(), selestedSem.getIdSem());
        lbMain.setModel(new ListModelList(listMainRegisters));
        lbMain.setItemRenderer(new ChairsRegisterRenderer());
        lbMain.renderAll();
        int i = service.countUnsignRegister(listMainRegisters);
        if (i != 0) {
            tabMain.setLabel("Основная сдача (" + i + ")");
        } else {
            tabMain.setLabel("Основная сдача");
        }
    }

    private void fillRetakeListbox() {
        listRetakeRegisters = service.getListRetakeRegisters(departmen.getIdDepartment(), selestedSem.getIdSem());
        lbRetake.setModel(new ListModelList(listRetakeRegisters));
        lbRetake.setItemRenderer(new ChairsRetakeRegisterRenderer());
        lbRetake.renderAll();
        int i = service.countUnsignRegister(listRetakeRegisters);
        if (i != 0) {
            tabRetake.setLabel("Пересдачи (" + i + ")");
        } else {
            tabRetake.setLabel("Пересдачи");
        }
    }

    private void fillComissionListbox() {
        listComissionRegisters = service.getListComissionRegisters(departmen.getIdDepartment(), selestedSem.getIdSem());
        lbComission.setModel(new ListModelList(listComissionRegisters));
        lbComission.setItemRenderer(new ChairsComissionRegisterRenderer());
        lbComission.renderAll();
        int i = service.countUnsignRegister(listComissionRegisters);
        if (i != 0) {
            tabComission.setLabel("Комиссии (" + i + ")");
        } else {
            tabComission.setLabel("Комиссии");
        }
    }

    @Listen("onSelect = #tabMain; onSelect = #tabRetake; onSelect = #tabComission")
    public void clearFilters() {
        filterRegisters();
    }

    @Listen("onClick = #chbSign;  onClick = #chbUnsign; onClick = #chbOverdue; onClick = #chbWithNumber; " +
            "onClick = #chbBachelors; onClick = #chbMaster; onClick = #chbSpec; onChange = #dbFrom; onChange = #dbTo;")
    public void filterRegisters() {
        if (tabMain.isSelected()) {
            tmpListRegister = new ArrayList<>(listMainRegisters);
        } else if (tabRetake.isSelected()) {
            tmpListRegister = new ArrayList<>(listRetakeRegisters);
        } else if (tabComission.isSelected()) {
            tmpListRegister = new ArrayList<>(listComissionRegisters);
        }
        List<ChairsRegisterModel> filteredByDate = new ArrayList<>();
        List<ChairsRegisterModel> filteredByStatus = new ArrayList<>();
        List<ChairsRegisterModel> filteredByQualification = new ArrayList<>();
        // -------------------------- проверка статусов -------------------------
        if (chbSign.isChecked()) {
            filteredByStatus.addAll(tmpListRegister.stream().filter(register -> register.getCertnumber() != null).collect(Collectors.toList()));
        }
        if (chbUnsign.isChecked()) {
            filteredByStatus.addAll(tmpListRegister.stream().filter(register -> register.getCertnumber() == null).collect(Collectors.toList()));
        }
        if (chbOverdue.isChecked()) {
            filteredByStatus.addAll(tmpListRegister.stream().filter(register -> ((register.getComissionDate() != null && new Date().after(service.getOverdueMainDate(register.getComissionDate())))
                    || (register.getSecondEndDate() != null && new Date().after(service.getOverdueRetakeDate(register.getSecondEndDate())))
                    || (register.getEndDate() != null && new Date().after(service.getOverdueRetakeDate(register.getEndDate())))
                    || (register.getPassdate() != null && new Date().after(service.getOverdueMainDate(register.getPassdate())))
                    || register.getExamdate() != null && new Date().after(service.getOverdueRetakeDate(register.getExamdate()))) && (register.getCertnumber() == null || register.getCertnumber().equals(""))).collect(Collectors.toList()));
        }
        if (chbWithNumber.isChecked()) {
            filteredByStatus.addAll(tmpListRegister.stream().filter(register -> register.getRegisterNumber() != null).collect(Collectors.toList()));

        }

        // --------------------------- проверка квалификации ------------------------------------
        if (chbBachelors.isChecked()) {
            filteredByQualification.addAll(tmpListRegister.stream().filter(register -> register.getQualification() == 2).collect(Collectors.toList()));
        }
        if (chbMaster.isChecked()) {
            filteredByQualification.addAll(tmpListRegister.stream().filter(register -> register.getQualification() == 3).collect(Collectors.toList()));
        }
        if (chbSpec.isChecked()) {
            filteredByQualification.addAll(tmpListRegister.stream().filter(register -> register.getQualification() == 1).collect(Collectors.toList()));
        }
        filteredByStatus.retainAll(filteredByQualification);
        // ---------------------------------- проверка дат -------------------------------------

        if (dbTo.getValue() != null && dbFrom.getValue() != null) {
            filteredByDate.addAll(tmpListRegister.stream().filter(register -> ((register.getSigndate() != null && (register.getSigndate().before(dbTo.getValue()) || register.getSigndate().equals(dbTo.getValue()))
            && (register.getSigndate().after(dbFrom.getValue()) || register.getSigndate().equals(dbFrom.getValue()))))).collect(Collectors.toList()));
        } else if (dbFrom.getValue() != null) {
            filteredByDate.addAll(tmpListRegister.stream().filter(register -> ((register.getSigndate() != null && (register.getSigndate().after(dbFrom.getValue()) || register.getSigndate().equals(dbFrom.getValue()))))).collect(Collectors.toList()));
        } else  if (dbTo.getValue() != null) {
            filteredByDate.addAll(tmpListRegister.stream().filter(register -> ((register.getSigndate() != null && (register.getSigndate().before(dbTo.getValue()) || register.getSigndate().equals(dbFrom.getValue()))))).collect(Collectors.toList()));
        }

        if (!filteredByDate.isEmpty() || (filteredByDate.isEmpty() && (dbFrom.getValue() != null || dbTo.getValue() != null)) ) {
            filteredByStatus.retainAll(filteredByDate);
        }
        if (tabMain.isSelected()) {
            tmpListMainRegisters = filteredByStatus.stream().distinct().collect(Collectors.toList());
            lbMain.setModel(new ListModelList(tmpListMainRegisters));
            lbMain.setItemRenderer(new ChairsRegisterRenderer());
            lbMain.renderAll();
        } else {
            tmpListMainRegisters = listMainRegisters;
        }
        if (tabRetake.isSelected()) {
            tmpListRetakeRegisters = filteredByStatus.stream().distinct().collect(Collectors.toList());
            lbRetake.setModel(new ListModelList(tmpListRetakeRegisters));
            lbRetake.setItemRenderer(new ChairsRetakeRegisterRenderer());
            lbRetake.renderAll();
        } else {
            tmpListRetakeRegisters = listRetakeRegisters;
        }
        if (tabComission.isSelected()) {
            tmpListComissionRegisters = filteredByStatus.stream().distinct().collect(Collectors.toList());
            lbComission.setModel(new ListModelList(tmpListComissionRegisters));
            lbComission.setItemRenderer(new ChairsComissionRegisterRenderer());
            lbComission.renderAll();
        } else {
            tmpListComissionRegisters = listComissionRegisters;
        }
    }

    @Listen("onOK = #tbMainFilterSubjectname; onOK = #tbMainFilterGroupname; onOK = #tbMainFilterTeacher; onOK = #tbMainFilterNumberRegister; onChange = #cmbMainFilterFoc")
    public void filterByAuxheaderMainRegister() {

        List<ChairsRegisterModel> filteredRegister;
        if (tmpListMainRegisters.isEmpty()) {
            filteredRegister = listMainRegisters;
        } else {
            filteredRegister = tmpListMainRegisters;
        }

        if (tbMainFilterSubjectname.getValue() != null && !tbMainFilterSubjectname.getValue().equals("")) {
            filteredRegister = filteredRegister.stream().filter(reg -> reg.getSubjectname().toLowerCase().contains(tbMainFilterSubjectname.getValue().toLowerCase())).collect(Collectors.toList());
        }
        if (tbMainFilterGroupname.getValue() != null) {
            filteredRegister = filteredRegister.stream().filter(reg -> reg.getGroupname().toLowerCase().contains(tbMainFilterGroupname.getValue().toLowerCase())).collect(Collectors.toList());
        }
        if (tbMainFilterTeacher.getValue() != null) {
            filteredRegister = filteredRegister.stream().filter(reg -> {
                if (reg.getFioTeachers() != null) {
                    return reg.getFioTeachers().toLowerCase().contains(tbMainFilterTeacher.getValue().toLowerCase());
                }
                return false;
            }).collect(Collectors.toList());
        }
        if (tbMainFilterNumberRegister.getValue() != null && !tbMainFilterNumberRegister.getValue().equals("")) {
            filteredRegister = filteredRegister.stream().filter(reg -> reg.getRegisterNumber() != null && reg.getRegisterNumber().toLowerCase().contains(tbMainFilterNumberRegister.getValue().toLowerCase())).collect(Collectors.toList());
        }
        if (cmbMainFilterFoc.getSelectedIndex() != -1) {
            filteredRegister = filteredRegister.stream().filter(reg -> reg.getFoc().equals(cmbMainFilterFoc.getValue())).collect(Collectors.toList());
        }
        lbMain.setModel(new ListModelList(filteredRegister));
        lbMain.setItemRenderer(new ChairsRegisterRenderer());
        lbMain.renderAll();
    }

    @Listen("onOK = #tbRetakeFilterSubjectname, #tbRetakeFilterGroupname, #tbRetakeFilterTeacher, #tbRetakeFilterRegisterNumber; onClick = #chbCommonRetake, #chbIndivRetake; onChange =#cmbRetakeFilterFoc")
    public void filterByAuxheaderRetakeRegister() {
        filterRegisters();
        List<ChairsRegisterModel> filteredRegister = new ArrayList<>();
        if (tmpListRetakeRegisters.isEmpty()) {
            filteredRegister.addAll(listRetakeRegisters);
        } else {
            filteredRegister.addAll(tmpListRetakeRegisters);
        }
        if (chbIndivRetake.isChecked()) {
            filteredRegister = filteredRegister.stream().filter(reg -> reg.getRetakeCount() == 4 || reg.getRetakeCount() == -4).collect(Collectors.toList());
        }
        if (chbCommonRetake.isChecked()) {
            filteredRegister = filteredRegister.stream().filter(reg -> reg.getRetakeCount() == 2 || reg.getRetakeCount() == -2).collect(Collectors.toList());
        }
        if (chbIndivRetake.isChecked() && chbCommonRetake.isChecked()) {
            filteredRegister = tmpListRetakeRegisters;
        } else if (!chbIndivRetake.isChecked() && !chbCommonRetake.isChecked()){
            filteredRegister.clear();
        }
        if (tbRetakeFilterSubjectname.getValue() != null && !tbRetakeFilterSubjectname.getValue().equals("")) {
            filteredRegister = filteredRegister.stream().filter(reg -> reg.getSubjectname().toLowerCase().contains(tbRetakeFilterSubjectname.getValue().toLowerCase())).collect(Collectors.toList());
        }
        if (tbRetakeFilterGroupname.getValue() != null && !tbRetakeFilterGroupname.getValue().equals("")) {
            filteredRegister = filteredRegister.stream().filter(reg -> reg.getGroupname().toLowerCase().contains(tbRetakeFilterGroupname.getValue().toLowerCase())).collect(Collectors.toList());
        }
        if (tbRetakeFilterTeacher.getValue() != null && !tbRetakeFilterTeacher.getValue().equals("")) {
            filteredRegister = filteredRegister.stream().filter(reg -> reg.getFioTeachers().toLowerCase().contains(tbRetakeFilterTeacher.getValue().toLowerCase())).collect(Collectors.toList());
        }
        if (tbRetakeFilterRegisterNumber.getValue() != null && !tbRetakeFilterRegisterNumber.getValue().equals("")) {
            filteredRegister = filteredRegister.stream().filter(reg -> reg.getRegisterNumber() != null && reg.getRegisterNumber().toLowerCase().contains(tbRetakeFilterRegisterNumber.getValue().toLowerCase())).collect(Collectors.toList());
        }
        if (cmbRetakeFilterFoc.getSelectedIndex() != -1) {
            filteredRegister = filteredRegister.stream().filter(reg -> reg.getFoc().equals(cmbRetakeFilterFoc.getValue())).collect(Collectors.toList());
        }
        lbRetake.setModel(new ListModelList(filteredRegister));
        lbRetake.setItemRenderer(new ChairsRetakeRegisterRenderer());
        lbRetake.renderAll();
    }

    @Listen("onOK = #tbComissionFilterSubjectname; onOK =#tbComissionFilterGroupname; onOK = #tbComissionFilterTeacher; onOK = #tbComissionFilterRegisterNumber; onChange = #cmbComissionFilterFoc")
    public void filterByAuxheaderComissionRegister() {

        List<ChairsRegisterModel> filteredRegister;
        if (tmpListComissionRegisters.isEmpty()) {
            filteredRegister = listComissionRegisters;
        } else {
            filteredRegister = tmpListComissionRegisters;
        }

        if (tbComissionFilterSubjectname.getValue() != null && !tbComissionFilterSubjectname.getValue().equals("")) {
            filteredRegister = filteredRegister.stream().filter(reg -> reg.getSubjectname().toLowerCase().contains(tbComissionFilterSubjectname.getValue().toLowerCase())).collect(Collectors.toList());
        }
        if (tbComissionFilterGroupname.getValue() != null && !tbComissionFilterGroupname.getValue().equals("")) {
            filteredRegister = filteredRegister.stream().filter(reg -> reg.getGroupname().toLowerCase().contains(tbComissionFilterGroupname.getValue().toLowerCase())).collect(Collectors.toList());
        }
        if (tbComissionFilterTeacher.getValue() != null && !tbComissionFilterTeacher.getValue().equals("")) {
            filteredRegister = filteredRegister.stream().filter(reg -> reg.getFioTeachers().toLowerCase().contains(tbComissionFilterTeacher.getValue().toLowerCase())).collect(Collectors.toList());
        }
        if (tbComissionFilterRegisterNumber.getValue() != null && !tbComissionFilterRegisterNumber.getValue().equals("")) {
            filteredRegister = filteredRegister.stream().filter(reg -> reg.getRegisterNumber() != null && reg.getRegisterNumber().toLowerCase().contains(tbComissionFilterRegisterNumber.getValue().toLowerCase())).collect(Collectors.toList());
        }
        if (cmbComissionFilterFoc.getSelectedIndex() != -1) {
            filteredRegister = filteredRegister.stream().filter(reg -> reg.getFoc().equals(cmbComissionFilterFoc.getValue())).collect(Collectors.toList());

        }
        lbComission.setModel(new ListModelList(filteredRegister));
        lbComission.setItemRenderer(new ChairsComissionRegisterRenderer());
        lbComission.renderAll();
    }
}
