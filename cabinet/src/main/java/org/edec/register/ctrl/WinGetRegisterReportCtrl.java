package org.edec.register.ctrl;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.edec.commission.model.ComissionsProtocolsModel;
import org.edec.register.model.report.RegisterDateModel;
import org.edec.register.model.report.RegisterModel;
import org.edec.register.service.dao.RegisterReportServiceImpl;
import org.edec.report.xls.XlsReport;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.model.jasperReport.ReportSrc;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class WinGetRegisterReportCtrl extends SelectorComposer<Component> {
    @Wire
    private Datebox dateOfBeginRegisterReport, dateOfEndRegisterReport;
    @Wire
    private Button btnGetRegisterReport;

    private SemesterModel sem = new SemesterModel();
    // Тип документа 0 - pdf, 1 - xls
    private Integer doctype;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        sem = (SemesterModel) Executions.getCurrent().getArg().get("Semester");
        doctype = (Integer) Executions.getCurrent().getArg().get("DocType");
    }

    @Listen("onClick = #btnGetRegisterReport")
    public void getRegisterReport() throws IOException {
        Date dateOfBegin = dateOfBeginRegisterReport.getValue();
        Date dateOfEnd = dateOfEndRegisterReport.getValue();

        if (doctype == 0) {
            JasperReportService service = new JasperReportService();
            JasperReport jasperReport = service.getReportForRegisters(dateOfBegin, dateOfEnd, sem);

            if (jasperReport == null) {
                PopupUtil.showInfo("Нет ведомостей за выбранный период");
            } else {
                jasperReport.showPdf();
            }
        }
        if (doctype == 1) {
            List<RegisterDateModel> registerDateModels = new RegisterReportServiceImpl()
                    .getRegistersByPeriod(dateOfBegin, dateOfEnd, sem.getIdSem());
            List<RegisterModel> list = new ArrayList<>();

            for (RegisterDateModel registerDateModel : registerDateModels) {
                for (RegisterModel register : registerDateModel.getRegisters()) {
                    list.add(register);
                }
            }

            XlsReport<RegisterModel> xlsReport = new XlsReport<>(list);
            xlsReport
                    .addColumn("Дата подписания", "signDate", 151)
                    .addColumn("№ регистра", "registerNumber", 115)
                    .addColumn("Предмет", "subject", 383)
                    .addColumn("Группа", "group", 115)
                    .addColumn("Форма контроля", "formOfCtrl", 115)
                    .addColumn("Преподаватель", "tutor", 383)
                    .addColumn("Кафедра", "chair", 333)
                    .addColumn("Дата проведения", "examinationDate", 151);

            HSSFWorkbook report = xlsReport.generateReport();
            String fn = "Реестр ведомостей ("+sem.toString()+").xls";
            File file = new File(fn);
            FileOutputStream outFile = new FileOutputStream(file);
            report.write(outFile);
            Filedownload.save(file, null);
        }
    }
}
