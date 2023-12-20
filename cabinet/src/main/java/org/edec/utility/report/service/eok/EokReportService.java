package org.edec.utility.report.service.eok;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.report.manager.EokReportDAO;
import org.edec.utility.report.model.eok.SubjectModel;
import org.zkoss.util.media.AMedia;

import java.io.*;
import java.util.List;


public class EokReportService {
    private EokReportDAO eokReportDAO = new EokReportDAO();

    public AMedia getEokXlsBySem (SemesterModel semester) {
        List<SubjectModel> subjects = eokReportDAO.getEokModels(semester.getIdSem());

        Workbook book = new HSSFWorkbook();

        for (SubjectModel subject : subjects) {
            boolean addSheet = true;
            if (book.getSheetIndex(subject.getCourseName()) != -1) {
                Sheet sheet = book.getSheet(subject.getCourseName());
                setRowForSheet(subject, sheet);
                addSheet = false;
            }
            if (addSheet) {
                Sheet sheet = book.createSheet(subject.getCourseName());
                setHeaderForSheet(sheet);
                setRowForSheet(subject, sheet);
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

    private void setHeaderForSheet (Sheet sheet) {
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Кафедра");
        row.createCell(1).setCellValue("Дисциплина");
        row.createCell(2).setCellValue("Группа");
        row.createCell(3).setCellValue("Количество человек в группе");
        row.createCell(4).setCellValue("Специальность");
        row.createCell(5).setCellValue("Наименование ЭОК");
        row.createCell(6).setCellValue("URL");
        row.createCell(7).setCellValue("Форма котнроля");
        row.createCell(8).setCellValue("Преподаватель");
    }

    private void setRowForSheet (SubjectModel subject, Sheet sheet) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        row.createCell(0).setCellValue(subject.getDepartment());
        row.createCell(1).setCellValue(subject.getSubjectname());
        row.createCell(2).setCellValue(subject.getGroupname());
        row.createCell(3).setCellValue(subject.getCountStudent());
        row.createCell(4).setCellValue(subject.getSpeciallity());
        row.createCell(5).setCellValue(subject.getEokName());
        row.createCell(6)
           .setCellValue(subject.getIdEsoCourse() != null ? "https://e.sfu-kras.ru/course/view.php?id=" + subject.getIdEsoCourse() : "");
        row.createCell(7).setCellValue(subject.getFormOfControl());
        row.createCell(8).setCellValue(subject.getTeacher());
    }
}
