package org.edec.commission.component;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.edec.report.debtorsReport.service.DebtorsReportService;
import org.edec.utility.component.manager.ComponentManager;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class WinDebtorReport extends Window {
    private Integer formOfStudy;
    private Long idInst;

    public WinDebtorReport(Integer formOfStudy, Long idInst) {
        super();

        this.formOfStudy = formOfStudy;
        this.idInst = idInst;

        setWidth("450px");
        setHeight("520px");
        setClosable(true);
        setTitle("Отчеты");

        Vbox vbox = new Vbox();
        appendChild(vbox);

        Listbox listboxSem = new Listbox();
        listboxSem.setHeight("400px");
        listboxSem.appendChild(new Listhead());

        listboxSem.getListhead().appendChild(new Listheader("Название семестра"));
        ((Listheader) listboxSem.getListhead().getFirstChild()).setStyle("color: white !important");

        // TODO move to service!
        List<SemesterModel> semesterModels = new ComponentManager().getSemester(idInst, formOfStudy, null);

        listboxSem.setItemRenderer((ListitemRenderer<SemesterModel>) (listitem, semesterModel, i) -> {
            listitem.setValue(semesterModel);
            new Listcell(DateConverter.convert2dateToString(semesterModel.getDateOfBegin(), semesterModel.getDateOfEnd()) + " " +
                         (semesterModel.getSeason() == 0 ? "осень" : "весна")).setParent(listitem);
        });
        listboxSem.setModel(new ListModelList<>(semesterModels));
        listboxSem.renderAll();

        listboxSem.setMultiple(true);
        listboxSem.setCheckmark(true);

        vbox.appendChild(listboxSem);

        vbox.appendChild(generateHboxWithReportsByStudents(listboxSem));
        vbox.appendChild(generateHboxWithReportsByChairs(listboxSem));
    }

    private Hbox generateHboxWithReportsByStudents(Listbox listboxSem) {
        Hbox hbox = new Hbox();

        Button btnDownloadFirstReport = new Button("Отчет(по студентам)");
        btnDownloadFirstReport.setImage("/imgs/excel.png");

        btnDownloadFirstReport.addEventListener(Events.ON_CLICK, event -> {
            List<SemesterModel> selectedSemesters = listboxSem.getSelectedItems().stream().map(Listitem::<SemesterModel>getValue).collect(Collectors.toList());

            if (selectedSemesters.size() == 0) {
                PopupUtil.showInfo("Выберите семестры");
                return;
            }

            HSSFWorkbook report = new DebtorsReportService().getReportDebtorsBySemestersOrderByFio(
                    selectedSemesters.stream().map(SemesterModel::getIdSem).collect(Collectors.toList()));
            String fn = "Отчет по должникам(по фио).xls";
            File file = new File(fn);
            FileOutputStream outFile = new FileOutputStream(file);
            report.write(outFile);
            Filedownload.save(file, null);
        });

        Button btnDownloadSecondReport = new Button("Отчет(по студентам, с неподп. ведом.)");
        btnDownloadSecondReport.setImage("/imgs/excel.png");

        btnDownloadSecondReport.addEventListener(Events.ON_CLICK, event -> {
            List<SemesterModel> selectedSemesters = listboxSem.getSelectedItems().stream().map(Listitem::<SemesterModel>getValue).collect(Collectors.toList());

            if (selectedSemesters.size() == 0) {
                PopupUtil.showInfo("Выберите семестры");
                return;
            }

            HSSFWorkbook report = new DebtorsReportService().getReportDebtorsBySemestersOrderByFioWithUnsignedRegisters(
                    selectedSemesters.stream().map(SemesterModel::getIdSem).collect(Collectors.toList()));
            String fn = "Отчет по должникам(по фио).xls";
            File file = new File(fn);
            FileOutputStream outFile = new FileOutputStream(file);
            report.write(outFile);
            Filedownload.save(file, null);
        });

        hbox.appendChild(btnDownloadFirstReport);
        hbox.appendChild(btnDownloadSecondReport);

        return hbox;
    }

    private Hbox generateHboxWithReportsByChairs(Listbox listboxSem) {
        Hbox hbox = new Hbox();

        Button btnDownloadFirstReport = new Button("Отчет(по кафедрам)");
        btnDownloadFirstReport.setImage("/imgs/excel.png");

        btnDownloadFirstReport.addEventListener(Events.ON_CLICK, event -> {
            List<SemesterModel> selectedSemesters = listboxSem.getSelectedItems().stream().map(Listitem::<SemesterModel>getValue).collect(Collectors.toList());

            if (selectedSemesters.size() == 0) {
                PopupUtil.showInfo("Выберите семестры");
                return;
            }

            HSSFWorkbook report = new DebtorsReportService().getReportDebtorsBySemestersOrderByDepartment(
                    selectedSemesters.stream().map(SemesterModel::getIdSem).collect(Collectors.toList()));
            String fn = "Отчет по должникам(по кафедре).xls";
            File file = new File(fn);
            FileOutputStream outFile = new FileOutputStream(file);
            report.write(outFile);
            Filedownload.save(file, null);
        });

        Button btnDownloadSecondReport = new Button("Отчет(по кафедрам, с неподп. ведом.)");
        btnDownloadSecondReport.setImage("/imgs/excel.png");

        btnDownloadSecondReport.addEventListener(Events.ON_CLICK, event -> {
            List<SemesterModel> selectedSemesters = listboxSem.getSelectedItems().stream().map(Listitem::<SemesterModel>getValue).collect(Collectors.toList());

            if (selectedSemesters.size() == 0) {
                PopupUtil.showInfo("Выберите семестры");
                return;
            }

            HSSFWorkbook report = new DebtorsReportService().getReportDebtorsBySemestersOrderByDepartmentWithUnsignedRegisters(
                    selectedSemesters.stream().map(SemesterModel::getIdSem).collect(Collectors.toList()));
            String fn = "Отчет по должникам(по кафедре).xls";
            File file = new File(fn);
            FileOutputStream outFile = new FileOutputStream(file);
            report.write(outFile);
            Filedownload.save(file, null);
        });

        hbox.appendChild(btnDownloadFirstReport);
        hbox.appendChild(btnDownloadSecondReport);

        return hbox;
    }
}
