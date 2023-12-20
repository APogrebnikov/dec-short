package org.edec.utility.report.model.jasperReport;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import org.edec.utility.fileManager.FileModel;
import org.edec.utility.report.ctrl.ReportPageCtrl;
import org.edec.utility.sign.service.SignService;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Window;

import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;


public class JasperReport {
    private ServletContext servletContext;

    /**
     * Путь того, где сохранять файл
     */
    private FileModel fileModel;
    /**
     * Название файла
     */
    private String name;
    /**
     * Путь до jasper-файла
     */
    private String pathJasper;
    /**
     * Параметры
     */
    private Map params;
    /**
     * Пользовательские данные
     */
    private JRDataSource dataSource;
    /**
     * Сервис для обновления данных и UI
     **/
    private SignService signService;

    public JasperReport (String name, String pathJasper, Map params, JRDataSource dataSource, FileModel fileModel,
                         SelectorComposer<Component> controller, ServletContext servletContext) {
        this.name = name;
        this.pathJasper = pathJasper;
        this.params = params;
        this.dataSource = dataSource;
        this.fileModel = fileModel;
        this.servletContext = servletContext;
    }

    public JasperReport (String name, String pathJasper, Map params, JRDataSource dataSource, SignService signService) {
        this.name = name;
        this.pathJasper = pathJasper;
        this.params = params;
        this.dataSource = dataSource;
        this.signService = signService;
    }

    public void showPdf () {
        Map arg = new HashMap();
        arg.put(ReportPageCtrl.ARG, params);
        arg.put(ReportPageCtrl.BEAN_DATA, dataSource);
        arg.put(ReportPageCtrl.FILE_NAME, name);
        arg.put(ReportPageCtrl.JASPER_FILE, pathJasper);
        arg.put(ReportPageCtrl.SIGN_SERVICE, signService);

        ComponentHelper.createWindow("/utility/report/report.zul", "winReport", arg).doModal();
    }

    public void showPdfEditor(){
        Map arg = new HashMap();
        arg.put(ReportPageCtrl.ARG, params);
        arg.put(ReportPageCtrl.BEAN_DATA, dataSource);
        arg.put(ReportPageCtrl.FILE_NAME, name);
        arg.put(ReportPageCtrl.JASPER_FILE, pathJasper);

        ComponentHelper.createWindow("/utility/report/report_editor.zul", "winReport", arg).doModal();
    }

    public void downloadDocx () {
        JRDocxExporter docxExporter = new JRDocxExporter();
        JasperPrint jp;

        try {
            if (dataSource != null) {
                jp = JasperFillManager.fillReport(pathJasper, params, dataSource);
            } else {
                jp = JasperFillManager.fillReport(pathJasper, params, new JREmptyDataSource());
            }

            docxExporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
            docxExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, name);
            docxExporter.exportReport();
            File f = new File(name);
            f.deleteOnExit();
            byte[] buffer = new byte[(int) f.length()];
            FileInputStream fs;
            fs = new FileInputStream(f);
            fs.read(buffer);
            fs.close();
            ByteArrayInputStream is = new ByteArrayInputStream(buffer);
            AMedia amedia = new AMedia(name + ".docx", "docx", "application/docx", is);
            Filedownload.save(amedia);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getFile () {
        byte[] bytes = null;
        if (dataSource == null) {
            dataSource = new JREmptyDataSource();
        }
        try {
            bytes = JasperRunManager.runReportToPdf(pathJasper, params, dataSource);
        } catch (JRException e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
