package org.edec.successful.service.xls;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.edec.report.xls.PoiPixelUtil;
import org.edec.successful.model.Measure;
import org.edec.successful.model.RatingModel;
import org.edec.successful.model.ReportStatModel;
import org.edec.successful.service.SuccessfulService;
import org.edec.successful.service.impl.SuccessfulReportDataService;
import org.edec.successful.service.impl.SuccessfulServiceImpl;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SuccessfulExcelReportTeacherService {
    private SuccessfulReportDataService service = new SuccessfulReportDataService();
    private SuccessfulService successfulService = new SuccessfulServiceImpl();
    private DecimalFormat formatter = new DecimalFormat("#0.0");

    public HSSFWorkbook getSuccessfulTeacherReport(List<RatingModel> ratings) throws IOException {
        ratings.sort(
                Comparator.comparing(RatingModel::getTChairFulltitle)
                          .thenComparing(RatingModel::getSubjectName)
                          .thenComparing(RatingModel::getFOC)
                          .thenComparing(RatingModel::getGroupname)
        );

        HSSFWorkbook report = new HSSFWorkbook();
        HSSFSheet sheet = report.createSheet("Отчет");

        int rowNum = buildHeader(sheet, report, 0);

        Row row;
        Cell cell;

        HSSFCellStyle cellStyle = getStyleForCell(report);
        HSSFCellStyle cellFloatStyle = getStyleForFloatCell(report);

        for(int i = 1; i < ratings.size(); i++) {
            if(i == ratings.size() - 1 || !isSameSubject(ratings.get(i - 1), ratings.get(i))) {
                int index = i;
                List<RatingModel> filteredRatings = ratings.stream()
                        .filter(el -> el.getGroupname().equals(ratings.get(index).getGroupname()))
                        .filter(el -> el.getTChairFulltitle().equals(ratings.get(index).getTChairFulltitle()))
                        .filter(el -> el.getFOC().equals(ratings.get(index).getFOC()))
                        .filter(el -> el.getSubjectName().equals(ratings.get(index).getSubjectName()))
                        .collect(Collectors.toList());

                ReportStatModel statModel = service.getStatData(
                        new ArrayList<>(),
                        filteredRatings,
                        new ArrayList<>(),
                        null,
                        null
                );

                row = sheet.createRow(rowNum++);

                buildCell(0, row, ratings.get(i).getTChairFulltitle(), CellType.STRING, cellStyle);
                buildCell(1, row, successfulService.getTeachersStrByIdSubject(ratings.get(i).getIdSubject()), CellType.STRING, cellStyle);
                buildCell(2, row, ratings.get(i).getSubjectName(), CellType.STRING, cellStyle);
                buildCell(3, row, ratings.get(i).getFOC().getName(), CellType.STRING, cellStyle);
                buildCell(4, row, ratings.get(i).getGroupname(), CellType.STRING, cellStyle);
                buildCell(5, row, Integer.toString(statModel.getAllStudents().size()), CellType.NUMERIC, cellStyle);
                buildCell(6, row,
                          Double.toString((statModel.getSuccessfulStudents().size() * 100.0 / statModel.getAllStudents().size())),
                          CellType.NUMERIC, cellFloatStyle);
            }
        }

        sheet.createFreezePane(0, 1);

        return report;
    }

    private boolean isSameSubject(RatingModel r1, RatingModel r2) {
        return Objects.equals(r1.getSubjectName(), r2.getSubjectName()) &&
               Objects.equals(r1.getTChairFulltitle(), r2.getTChairFulltitle()) &&
               Objects.equals(r1.getFOC(), r2.getFOC()) &&
               Objects.equals(r1.getGroupname(), r2.getGroupname());
    }

    private int buildHeader(HSSFSheet sheet, HSSFWorkbook report, int rowNum) {
        HSSFCellStyle headerStyle = getStyleForHeader(report);

        Row row = sheet.createRow(rowNum++);

        buildCell(0, row, "Кафедра", CellType.STRING, headerStyle);
        buildCell(1, row, "Преподаватели", CellType.STRING, headerStyle);
        buildCell(2, row, "Предмет", CellType.STRING, headerStyle);
        buildCell(3, row, "ФК", CellType.STRING, headerStyle);
        buildCell(4, row, "Группа", CellType.STRING, headerStyle);
        buildCell(5, row, "Кол-во студ.", CellType.STRING, headerStyle);
        buildCell(6, row, "Успеваемость, %", CellType.STRING, headerStyle);

        sheet.setColumnWidth(0, PoiPixelUtil.pixel2WidthUnits(200));
        sheet.setColumnWidth(1, PoiPixelUtil.pixel2WidthUnits(200));
        sheet.setColumnWidth(2, PoiPixelUtil.pixel2WidthUnits(200));
        sheet.setColumnWidth(4, PoiPixelUtil.pixel2WidthUnits(100));
        sheet.setColumnWidth(6, PoiPixelUtil.pixel2WidthUnits(120));

        return rowNum;
    }

    private void buildCell(int column, Row row, String value, CellType type, HSSFCellStyle style) {
        Cell cell = row.createCell(column, type);

        if(type == CellType.NUMERIC) {
            try {
                cell.setCellValue(Integer.parseInt(value));
            } catch (Exception e) {
                cell.setCellValue(Double.parseDouble(value));
            }
        } else {
            cell.setCellValue(value);
        }
        cell.setCellStyle(style);
    }

    private HSSFCellStyle getStyleForHeader(HSSFWorkbook report) {
        HSSFFont font = report.createFont();
        font.setBold(true);
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short)14);

        HSSFCellStyle style = report.createCellStyle();
        style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);
        style.setWrapText(true);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        return style;
    }

    private HSSFCellStyle getStyleForCell(HSSFWorkbook report) {
        HSSFCellStyle cellStyle = report.createCellStyle();
        Font font = report.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short)12);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);

        return cellStyle;
    }

    private HSSFCellStyle getStyleForFloatCell(HSSFWorkbook report) {
        DataFormat format = report.createDataFormat();

        HSSFCellStyle cellStyle = report.createCellStyle();
        Font font = report.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short)12);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setDataFormat(format.getFormat("##.00"));
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);

        return cellStyle;
    }

    private void buildCell(int column, Row row, String value) {
        Cell cell = row.createCell(column, CellType.STRING);
        cell.setCellValue(value);
    }

    private void buildCell(int column, Row row, String value, CellType type) {
        Cell cell = row.createCell(column, type);
        cell.setCellValue(value);
    }
}
