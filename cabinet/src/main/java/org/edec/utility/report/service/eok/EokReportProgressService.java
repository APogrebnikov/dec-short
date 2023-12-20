package org.edec.utility.report.service.eok;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.report.manager.EokReportDAO;
import org.edec.utility.report.model.eok.SubjectModel;
import org.zkoss.util.media.AMedia;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class EokReportProgressService {
    private EokReportDAO eokReportDAO = new EokReportDAO();

    public AMedia getEokProgressXlsBySem (SemesterModel semester) {
        List<SubjectModel> subjects = eokReportDAO.getEokModels(semester.getIdSem());
        List<SubjectModel>  progress = eokReportDAO.getProgressByWeek(semester.getIdSem());

        Workbook book = new HSSFWorkbook();
        CellStyle style = book.createCellStyle();
        style.setDataFormat(book.createDataFormat().getFormat("0.00%"));

        for (SubjectModel subject : subjects) {
            List<SubjectModel> progressByGroup = progress.stream().filter(
                    el-> el.getGroupname().equals(subject.getGroupname()) && el.getSubjectname().equals(subject.getSubjectname())).collect(Collectors.toList());
            boolean addSheet = true;
            if (book.getSheetIndex(subject.getCourseName()) != -1) {
                Sheet sheet = book.getSheet(subject.getCourseName());
                setRowForSheet(subject, sheet, progressByGroup, style);
                addSheet = false;
            }
            if (addSheet) {
                Sheet sheet = book.createSheet(subject.getCourseName());
                setHeaderForSheet(sheet, progressByGroup);
                setRowForSheet(subject, sheet, progressByGroup, style);
            }
        }
        AMedia aMedia = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            book.write(bos);
            aMedia = new AMedia(
                    "Журнал ЭОК " + DateConverter.convert2dateToString(semester.getDateOfBegin(), semester.getDateOfEnd()) + " " +
                            (semester.getSeason() == 0 ? "осень" : "весна"), "xls", "application/xls", bos.toByteArray());
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aMedia;
    }

    private void setHeaderForSheet (Sheet sheet,  List<SubjectModel> progress) {
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Кафедра");
        row.createCell(1).setCellValue("Дисциплина");
        row.createCell(2).setCellValue("Группа");
        row.createCell(3).setCellValue("Наименование ЭОК");
        row.createCell(4).setCellValue("URL");
        row.createCell(5).setCellValue("Преподаватель");

        sheet.setColumnWidth(0,  (int) (35 * 1.14388) * 256); //кафедрa
        sheet.setColumnWidth(1,  (int) (35 * 1.14388) * 256);//дисциплина
        sheet.setColumnWidth(2,  (int) (10 * 1.14388) * 256);//группа
        sheet.setColumnWidth(3,  (int) (35 * 1.14388) * 256);//наименование эок
        sheet.setColumnWidth(4,  (int) (10 * 1.14388) * 256);//url
        sheet.setColumnWidth(5,  (int) (30 * 1.14388) * 256);//преподаватель

        int j = 0;
        for (int i = 6; i <= (progress.size() + 6); i++ ) {
            if (j<progress.size()) {
                SubjectModel progressModel = progress.get(j);
                row.createCell(i).setCellValue(DateConverter.convertDateToStringByFormat(progressModel.getSynchProgressDate(), "dd.MM.yyyy"));
                j++;
            }
            sheet.autoSizeColumn(i);
        }

    }

    private void setRowForSheet (SubjectModel subject, Sheet sheet, List<SubjectModel> progress, CellStyle style) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        row.createCell(0).setCellValue(subject.getDepartment());
        row.createCell(1).setCellValue(subject.getSubjectname());
        row.createCell(2).setCellValue(subject.getGroupname());
        row.createCell(3).setCellValue(subject.getEokName());
        row.createCell(4)
                .setCellValue(subject.getIdEsoCourse() != null ? "https://e.sfu-kras.ru/course/view.php?id=" + subject.getIdEsoCourse() : "");
        row.createCell(5).setCellValue(subject.getTeacher());
        int j = 0;
        for (int i = 6; i <= (progress.size() + 6); i++ ) {
            if (j<progress.size()) {
                SubjectModel progresModel = progress.get(j);
                Cell progressCell =  row.createCell(i);
                progressCell.setCellValue(progresModel.getProgress());
                progressCell.setCellStyle(style);
                j++;
            } else {
                row.createCell(i).setCellValue(" ");
            }
        }
    }
}
