package org.edec.commission.service.impl;

import javafx.scene.layout.Border;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.edec.commission.manager.EntityManagerReportCommission;
import org.edec.commission.model.CommissionStudentReportModel;
import org.edec.commission.service.CommissionReportService;
import org.edec.utility.converter.DateConverter;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class CommissionReportImpl implements CommissionReportService {
    private EntityManagerReportCommission emReportCommission = new EntityManagerReportCommission();

    @Override
    public byte[] getXlsxForStudentCommission(int formofstudy, Date dateOfBegin, Date dateOfEnd) {

        List<CommissionStudentReportModel> students = emReportCommission.getStudentForReport(formofstudy, dateOfBegin, dateOfEnd);
        Workbook book = new HSSFWorkbook();

        Sheet sheet = book.createSheet("Студенты в комиссиях");
        sheet.getPrintSetup().setHeaderMargin(0);
        sheet.getPrintSetup().setFooterMargin(0);
        sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
        sheet.getPrintSetup().setLandscape(true);
        setHeadersForSheet(sheet);

        CellStyle styleBorderBottom = borderBottom(book);
        CellStyle styleDefault = defaultStyle(book);

        int currentStudentStart = 1;
        int currentStudentEnd = 1;
        int fioCulumn = 0;

        for (int i = 0; i < students.size(); i++) {
            CommissionStudentReportModel student = students.get(i);
            CommissionStudentReportModel nextStudent = null;
            if ((i + 1) != students.size()) {
                nextStudent = students.get(i + 1);
            }

            if (nextStudent == null || !student.getFio().equals(nextStudent.getFio())) {
                // Заканчиваем отсчет студента
                currentStudentEnd = i + 1;
                if (currentStudentStart != currentStudentEnd) {
                    sheet.addMergedRegion(new CellRangeAddress(currentStudentStart, currentStudentEnd, fioCulumn, fioCulumn));
                    // Для выравнивания первой ячейки по верху
                    Cell currentCell = sheet.getRow(currentStudentStart).getCell(fioCulumn);
                    CellStyle currentStyle = currentCell.getCellStyle();
                    Cell cell = CellUtil.createCell(sheet.getRow(currentStudentStart), 0, student.getFio());
                    currentStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
                    cell.setCellStyle(currentStyle);
                }
                // Начинаем отсчет нового студента
                currentStudentStart = i + 2;
            }

            if (nextStudent == null || !student.getFio().equals(nextStudent.getFio())) {
                setContent(sheet, student, styleBorderBottom);
            } else {
                setContent(sheet, student, styleDefault);
            }
        }
      /*  for (int i = 0; i <= 9; i++) {
            sheet.autoSizeColumn(i);
        }*/

        for (int i = 0; i < 9; i++) {
            sheet.autoSizeColumn(i);
        }

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            book.write(bos);
            bos.close();
            return bos.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private void setHeadersForSheet(Sheet sheet) {
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("ФИО");
        row.createCell(1).setCellValue("Платник");
        row.createCell(2).setCellValue("Группа");
        row.createCell(3).setCellValue("Дисциплина");
        row.createCell(4).setCellValue("ФК");
        row.createCell(5).setCellValue("Оценка");
        row.createCell(6).setCellValue("Семестр");
        row.createCell(7).setCellValue("Кафедра");
        row.createCell(8).setCellValue("Дата комиссии");
        row.createCell(9).setCellValue("Аудитория");
    }

    private void setContent(Sheet sheet, CommissionStudentReportModel commissionStudent, CellStyle cellStyle) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        row.createCell(0).setCellValue(commissionStudent.getFio());
        row.createCell(1).setCellValue(!commissionStudent.getGovernmentFinanced() ? "Да" : "Нет");
        row.createCell(2).setCellValue(commissionStudent.getGroupname());
        row.createCell(3).setCellValue(commissionStudent.getSubjectname());
        row.createCell(4).setCellValue(commissionStudent.getFocStr());
        row.createCell(5).setCellValue(commissionStudent.getRating());
        row.createCell(6).setCellValue(commissionStudent.getSemesterStr());
        row.createCell(7).setCellValue(commissionStudent.getChair());
        row.createCell(8)
                .setCellValue(commissionStudent.getCommissionDate() != null ? DateConverter.convertTimestampToString(
                        commissionStudent.getCommissionDate()) : "");
        row.createCell(9).setCellValue(commissionStudent.getClassroom());
        if (cellStyle != null) {
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                cell.setCellStyle(cellStyle);
            }
        }
    }

    private CellStyle borderBottom(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(CellStyle.BORDER_MEDIUM);
        cellStyle.setWrapText(true);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        return cellStyle;
    }

    private CellStyle defaultStyle(Workbook book) {
        CellStyle defaultStyle = book.createCellStyle();
        defaultStyle.setWrapText(true);
        defaultStyle.setBorderBottom(CellStyle.BORDER_THIN);
        defaultStyle.setBorderLeft(CellStyle.BORDER_THIN);
        defaultStyle.setBorderRight(CellStyle.BORDER_THIN);
        defaultStyle.setBorderTop(CellStyle.BORDER_THIN);
        return defaultStyle;
    }
}
