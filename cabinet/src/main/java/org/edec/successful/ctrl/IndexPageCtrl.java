package org.edec.successful.ctrl;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.edec.passportGroup.service.impl.ReportsServiceESO;
import org.edec.successful.ctrl.renderer.MeasureRenderer;
import org.edec.successful.ctrl.renderer.SuccessfulReportRender;
import org.edec.successful.model.CourseModel;
import org.edec.successful.model.DepartmentModel;
import org.edec.successful.model.Filter;
import org.edec.successful.model.Measure;
import org.edec.successful.model.RatingModel;
import org.edec.successful.model.SuccessfulTreeModel;
import org.edec.successful.service.SuccessfulService;
import org.edec.successful.service.impl.SuccessfulReportDataService;
import org.edec.successful.service.xls.SuccessfulExcelReportMainService;
import org.edec.successful.service.impl.SuccessfulServiceImpl;
import org.edec.successful.service.xls.SuccessfulExcelReportTeacherService;
import org.edec.utility.component.model.ChairModel;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.LevelConst;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import java.util.Date;
import java.util.List;

public class IndexPageCtrl extends CabinetSelector {
    @Wire
    private Combobox cmbInst, cmbFormOfStudy, cmbGovFinance, cmbSemester, cmbChair, cmbLvl, cmbGroup, cmbContingent;

    @Wire
    private Checkbox cbAcadem, cbFiz, cbPract, cbDateMarks, cbPassWeek, chSpec, chBach, chMaster, chCourse1, chCourse2, chCourse3, chCourse4, chCourse5, chCourse6;

    @Wire
    private Listbox lbStudent;

    @Wire
    private Groupbox gbFilter;

    @Wire
    private Button btnApply;

    @Wire
    private Vbox vbColumn1;

    @Wire
    private Datebox dbDate;

    @Wire
    private Radio rbCourse, rbChair;

    @Wire
    private Listbox lbMeasure;

    @Wire
    private Popup popupFilter, popupMeasure;

    @Wire
    private Label lblFilter, lblMeasure;

    private ComponentService componentService = new ComponentServiceESOimpl();

    private org.edec.main.model.DepartmentModel currentDep;
    private InstituteModel currentInst;
    private SemesterModel currentSemester;

    private SuccessfulService successfulService = new SuccessfulServiceImpl();
    private SuccessfulReportDataService successfulReportDataService = new SuccessfulReportDataService();
    private SuccessfulExcelReportMainService successfulExcelReportMainService = new SuccessfulExcelReportMainService();
    private SuccessfulExcelReportTeacherService successfulExcelReportTeacherService = new SuccessfulExcelReportTeacherService();

    protected void fill () {
        List<Measure> measures = Arrays.asList(Measure.values());
        lbMeasure.setItemRenderer(new MeasureRenderer());
        lbMeasure.setModel(new ListModelList<>(measures));
        lbMeasure.renderAll();
        onCheckMeasure();
        currentInst = componentService.fillCmbInst(cmbInst, vbColumn1, currentModule.getDepartments());
        //special for Sanchez
        vbColumn1.setVisible(true);

        FormOfStudy currentFOS = componentService.fillCmbFormOfStudy(cmbFormOfStudy, vbColumn1, currentModule.getFormofstudy(), false);
        //TODO: заполнить Кафедры в зависимости от института
        componentService.fillCmbSem(cmbSemester, currentInst.getIdInst(), currentFOS.getType(), null);
        //TODO: выбрать текущий семестр
        if(cmbSemester.getChildren().size()>0) {
            cmbSemester.setSelectedIndex(1);
        }
        componentService.fillCmbGovFinanced(cmbGovFinance);
        componentService.fillCmbLevel(cmbLvl);
        componentService.fillCmbChair(cmbChair);
        //componentService.fillCmbSem(cmbSemester, ((Integer)(1)).longValue(), 1, null);
        dbDate.setValue(new Date());
    }

    @Listen("onClick = #lblFilter")
    public void openFilters() {
        popupFilter.open(lblFilter, "after_start");
    }

    @Listen("onClick = #lblMeasure")
    public void openMeasure() {
        popupMeasure.open(lblMeasure, "after_start");
    }

    @Listen("onClick = #btnStatistic")
    public void openStatistic() {
        ComponentHelper.createWindow("/successful/winSuccessfulStatistic.zul", "winSuccessfulStatistic", new HashMap()).doModal();
    }

    @Listen("onChange = #cmbSemester; onClick=#chSpec, #chBach, #chMaster, #chCourse1, #chCourse2, #chCourse3, #chCourse4, #chCourse5, #chCourse6")
    public void onChangeSemOpen () {
        if(cmbSemester.getSelectedIndex() != -1) {
            currentSemester = cmbSemester.getSelectedItem().getValue();
        }

        String courses = concatCourses();
        String levels = concatLevels();
        //TODO: Проверка семестра
        if (cmbSemester.getSelectedItem() != null) {
            componentService.fillCmbGroupsWithEmpty(cmbGroup, ((SemesterModel) cmbSemester.getSelectedItem().getValue()).getIdSem(), courses,
                    levels);
        }else{
            Messagebox.show("Выберите семестр");
        }

        cmbGroup.setText("");
    }

    private String concatCourses() {
        String res = "";
        int i = 0;
        if (chCourse1.isChecked()) {
            res = "1";
            i++;
        }
        if (chCourse2.isChecked()) {
            if (i > 0) {
                res = res + ",2";
            } else {
                res = "2";
            }
            i++;
        }
        if (chCourse3.isChecked()) {
            if (i > 0) {
                res = res + ",3";
            } else {
                res = "3";
            }
            i++;
        }
        if (chCourse4.isChecked()) {
            if (i > 0) {
                res = res + ",4";
            } else {
                res = "4";
            }
            i++;
        }
        if (chCourse5.isChecked()) {
            if (i > 0) {
                res = res + ",5";
            } else {
                res = "5";
            }
            i++;
        }
        if (chCourse6.isChecked()) {
            if (i > 0) {
                res = res + ",6";
            } else {
                res = "6";
            }
            i++;
        }
        return res;
    }

    private String concatLevels() {
        String res = "";
        int i = 0;

        if (chBach.isChecked()) {
            res = LevelConst.BACH.getType().toString();
            i++;
        }
        if (chMaster.isChecked()) {
            if (i > 0) {
                res = res + "," + LevelConst.MAGISTR.getType().toString();
            } else {
                res = LevelConst.MAGISTR.getType().toString();
            }
            i++;
        }
        if (chSpec.isChecked()) {
            if (i > 0) {
                res = res + "," + LevelConst.SPEC.getType().toString();
            } else {
                res = LevelConst.SPEC.getType().toString();
            }
        }
        return res;
    }

    @Listen("onOK=#btnApply; onClick = #btnApply")
    public void buildReport () {
        if(currentSemester == null || currentInst == null) {
            DialogUtil.info("Не заполнены все обязательные параметры");
            return;
        }

        Clients.showBusy(lbStudent, "Загрузка данных");

        List<RatingModel> ratings = successfulService.getRatingByFilter(collectFilter());

        SuccessfulTreeModel successfulTreeModel = successfulReportDataService.createTreeModel(ratings, onCheckMeasure());

        lbStudent.setModel(new ListModelList<>(successfulTreeModel.getChilds()));

        SuccessfulReportRender render = new SuccessfulReportRender(successfulTreeModel, onCheckMeasure(), ratings);
        lbStudent.setItemRenderer(render);
        lbStudent.renderAll();
        render.buildFooter(lbStudent.getListfoot());

        Clients.clearBusy(lbStudent);
    }

    /**
     * Собираем модель фильтров
     * @return
     */
    private Filter collectFilter(){
        Filter filter = new Filter();

        filter.setFos(cmbFormOfStudy.getSelectedItem().getValue());
        filter.setCourses(concatCourses());
        filter.setLevels(concatLevels());
        if (cmbChair.getSelectedItem() != null) {
            filter.setIdChair(((ChairModel) cmbChair.getSelectedItem().getValue()).getIdChair());
        } else {
            filter.setIdChair(null);
        }
        filter.setIdSemester(((SemesterModel) cmbSemester.getSelectedItem().getValue()).getIdSem());
        filter.setGovFin(cmbGovFinance.getSelectedItem().getValue());
        filter.setGroupName(cmbGroup.getValue());
        if (currentDep != null){
            filter.setIdDepartment(currentDep.getIdDepartment());
        } else {
            filter.setIdDepartment(null);
        }

        if(cbDateMarks.isChecked()) {
            filter.setLastDate(dbDate.getValue());
        } else {
            filter.setLastDate(new Date());
        }

        filter.setIdInstitute(currentInst.getIdInst());
        filter.setUseAcadem(cbAcadem.isChecked());
        filter.setUseFiz(cbFiz.isChecked());
        filter.setUsePractic(cbPract.isChecked());
        filter.setOnlyPassWeek(cbPassWeek.isChecked());


        return filter;
    }

    @Listen("onClick = #btnXlsReport")
    public void buildXlsReport() throws IOException {
        if(currentSemester == null || currentInst == null) {
            DialogUtil.info("Не заполнены все обязательные параметры");
            return;
        }

        List<RatingModel> ratings = successfulService.getRatingByFilter(collectFilter());

        HSSFWorkbook report = successfulExcelReportMainService.getSuccessfulMainReport(ratings);
        String fn = "Отчет об успешности.xls";
        File file = new File(fn);
        FileOutputStream outFile = new FileOutputStream(file);
        report.write(outFile);
        Filedownload.save(file, null);
    }

    @Listen("onClick = #btnXlsReportTeacher")
    public void buildXlsReportTeacher() throws IOException {
        if(currentSemester == null || currentInst == null) {
            DialogUtil.info("Не заполнены все обязательные параметры");
            return;
        }

        List<RatingModel> ratings = successfulService.getRatingByFilter(collectFilter());

        HSSFWorkbook report = successfulExcelReportTeacherService.getSuccessfulTeacherReport(ratings);
        String fn = "Отчет об успешности(по преподавателям).xls";
        File file = new File(fn);
        FileOutputStream outFile = new FileOutputStream(file);
        report.write(outFile);
        Filedownload.save(file, null);
    }

    @Listen("onDrop = #userListbox")
    public void onDragDropUserListbox(DropEvent dropEvent) {
        lbMeasure.appendChild(dropEvent.getTarget());
    }

    @Listen("onDrop = listitem")
    public void onDragDropUserListitem(DropEvent dropEvent) {
        Listitem droppedListitem = (Listitem) dropEvent.getTarget();
        Listitem draggedListitem = (Listitem) dropEvent.getDragged();

        Component nextDra = getNextElement(draggedListitem);
        Component nextDro = getNextElement(droppedListitem);

        lbMeasure.insertBefore(droppedListitem, nextDra);
        lbMeasure.insertBefore(draggedListitem, nextDro);
        onCheckMeasure();
    }

    /**
     * Собираем коллекцию измерений в правильном порядке, перерисовываем компонент
     * @return
     */
    @Listen("onClick = .measurecb")
    public List<Measure> onCheckMeasure() {
        List<Measure> measures = new ArrayList<>();
        int i = 0;
        for (Component child : lbMeasure.getChildren()) {
            Listitem item = (Listitem) child;
            Listcell cell = (Listcell) item.getChildren().get(0);
            Checkbox checkbox = (Checkbox) cell.getChildren().get(0);
            String name = ((Measure)item.getValue()).getName();
            if(checkbox.isChecked()) {
                i++;
                checkbox.setLabel(i+". "+name);
                measures.add((Measure)item.getValue());
            }else{
                checkbox.setLabel(name);
            }
        }
        return measures;
    }

    private int checkPosition(Listitem item){
        // Проверяем, а раньше он или позже
        int pos = 0;
        for (Component child : lbMeasure.getChildren()) {
            if(child == item){
                return pos;
            }
            pos++;
        }
        return -1;
    }

    private Component getNextElement(Listitem item){
        boolean find = false;
        for (Component child : lbMeasure.getChildren()) {
            if(find){
                return child;
            }
            if(child == item) {
                find = true;
            }
        }
        return null;
    }
}
