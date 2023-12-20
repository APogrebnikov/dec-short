package org.edec.successful.service.xls;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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
import org.edec.successful.service.impl.SuccessfulReportDataService;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SuccessfulExcelReportMainService {
    private SuccessfulReportDataService service = new SuccessfulReportDataService();
    private DecimalFormat formatter = new DecimalFormat("#0.0");

    public HSSFWorkbook getSuccessfulMainReport(List<RatingModel> ratings) throws IOException {
        ratings.sort(Comparator.comparing(RatingModel::getCourse).thenComparing(RatingModel::getGroupname));

        HSSFWorkbook report = new HSSFWorkbook();
        HSSFSheet sheet = report.createSheet("Отчет");

        int rowNum = buildHeader(sheet, report, 0);

        List<Integer> courses = ratings.stream().map(RatingModel::getCourse).distinct().collect(Collectors.toList());

        Row row;
        Cell cell;

        HSSFCellStyle cellStyle = getStyleForCell(report);
        ReportStatModel allStatModel = new ReportStatModel();

        for(Integer course : courses) {
            ReportStatModel courseStatModel = new ReportStatModel();

            List<String> groups = ratings.stream().filter(el -> el.getCourse().equals(course))
                                         .map(RatingModel::getGroupname).distinct().collect(Collectors.toList());

            int i = 1;
            int startMerge = rowNum;

            for(String group : groups) {
                ReportStatModel groupStatModel = service.getStatData(
                        Arrays.asList(Measure.COURSE, Measure.GROUP),
                        ratings,
                        Arrays.asList(course.toString(), group),
                        null,
                        null
                );

                row = sheet.createRow(rowNum++);

                String specialityCode = ratings.stream()
                                               .filter(el -> el.getCourse().equals(course) && el.getGroupname().equals(group))
                                               .map(RatingModel::getSpecialityCode)
                                               .findFirst().orElse("");

                String chair = ratings.stream()
                                               .filter(el -> el.getCourse().equals(course) && el.getGroupname().equals(group))
                                               .map(RatingModel::getEChairShorttitle)
                                               .findFirst().orElse("");

                buildCell(0, row, Integer.toString(i++), CellType.NUMERIC, cellStyle);
                buildCell(1, row, course.toString(), CellType.NUMERIC, cellStyle);
                buildCell(2, row, Integer.toString(groupStatModel.getAllStudents().size()), CellType.NUMERIC, cellStyle);
                buildCell(3, row, group, CellType.STRING, cellStyle);
                buildCell(4, row, specialityCode, CellType.STRING, cellStyle);
                buildCell(5, row, chair, CellType.STRING, cellStyle);
                buildCell(6, row, Integer.toString(groupStatModel.getSuccessfulStudents().size()), CellType.NUMERIC, cellStyle);
                buildCell(7, row, Integer.toString(groupStatModel.getStudentsWithOnlyFiveMarks().size()), CellType.NUMERIC, cellStyle);
                buildCell(8, row, Integer.toString(groupStatModel.getStudentsWithFiveAndFourMarks().size()), CellType.NUMERIC, cellStyle);
                buildCell(9, row, Integer.toString(groupStatModel.getStudentsWithOnlyFourMarks().size()), CellType.NUMERIC, cellStyle);
                buildCell(10, row, Integer.toString(groupStatModel.getStudentsWithFiveFourThreeMarks().size()), CellType.NUMERIC, cellStyle);
                buildCell(11, row, Integer.toString(groupStatModel.getStudentsWithOnlyThreeMarks().size()), CellType.NUMERIC, cellStyle);
                buildCell(12, row, Integer.toString(groupStatModel.getStudentsWithDebts().size()), CellType.NUMERIC, cellStyle);
                buildCell(13, row, Integer.toString(groupStatModel.getStudentsWithOneDebt().size()), CellType.NUMERIC, cellStyle);
                buildCell(14, row, Integer.toString(groupStatModel.getStudentsWithTwoDebts().size()), CellType.NUMERIC, cellStyle);
                buildCell(15, row, Integer.toString(groupStatModel.getStudentsWithThreeOrFourDebts().size()), CellType.NUMERIC, cellStyle);
                buildCell(16, row, Integer.toString(groupStatModel.getStudentsWithMoreThanFiveDebts().size()), CellType.NUMERIC, cellStyle);
                buildCell(17, row, Integer.toString(groupStatModel.getStudentsWithAllDebts().size()), CellType.NUMERIC, cellStyle);

                groupStatModel.mergeInto(courseStatModel);
            }

            if(startMerge != rowNum - 1) {
                sheet.addMergedRegion(new CellRangeAddress(startMerge, rowNum - 1, 1, 1));
            }

            row = sheet.createRow(rowNum++);
            buildStatCountRow(row, courseStatModel, report, sheet, rowNum);

            row = sheet.createRow(rowNum++);
            buildStatPercentRow(row, courseStatModel, report, sheet, rowNum);

            courseStatModel.mergeInto(allStatModel);
        }

        row = sheet.createRow(rowNum++);
        buildStatCountRow(row, allStatModel, report, sheet, rowNum);

        row = sheet.createRow(rowNum++);
        buildStatPercentRow(row, allStatModel, report, sheet, rowNum);

        sheet.createFreezePane(0, 2);

        return report;
    }

    private void buildStatCountRow(Row row, ReportStatModel courseStatModel, HSSFWorkbook report, HSSFSheet sheet, int rowNum) {
        HSSFCellStyle allStatStyle = getStyleForHeader(report);

        buildCell(0, row, "Итого (∑)", CellType.STRING, allStatStyle);
        buildCell(1, row, "Итого (∑)", CellType.STRING, allStatStyle);
        buildCell(2, row, Integer.toString(courseStatModel.getAllStudents().size()), CellType.NUMERIC, allStatStyle);
        buildCell(3, row, "", CellType.STRING, allStatStyle);
        buildCell(4, row, "", CellType.STRING, allStatStyle);
        buildCell(5, row, "", CellType.STRING, allStatStyle);
        buildCell(6, row, Integer.toString(courseStatModel.getSuccessfulStudents().size()), CellType.NUMERIC, allStatStyle);
        buildCell(7, row, Integer.toString(courseStatModel.getStudentsWithOnlyFiveMarks().size()), CellType.NUMERIC, allStatStyle);
        buildCell(8, row, Integer.toString(courseStatModel.getStudentsWithFiveAndFourMarks().size()), CellType.NUMERIC, allStatStyle);
        buildCell(9, row, Integer.toString(courseStatModel.getStudentsWithOnlyFourMarks().size()), CellType.NUMERIC, allStatStyle);
        buildCell(10, row, Integer.toString(courseStatModel.getStudentsWithFiveFourThreeMarks().size()), CellType.NUMERIC, allStatStyle);
        buildCell(11, row, Integer.toString(courseStatModel.getStudentsWithOnlyThreeMarks().size()), CellType.NUMERIC, allStatStyle);
        buildCell(12, row, Integer.toString(courseStatModel.getStudentsWithDebts().size()), CellType.NUMERIC, allStatStyle);
        buildCell(13, row, Integer.toString(courseStatModel.getStudentsWithOneDebt().size()), CellType.NUMERIC, allStatStyle);
        buildCell(14, row, Integer.toString(courseStatModel.getStudentsWithTwoDebts().size()), CellType.NUMERIC, allStatStyle);
        buildCell(15, row, Integer.toString(courseStatModel.getStudentsWithThreeOrFourDebts().size()), CellType.NUMERIC, allStatStyle);
        buildCell(16, row, Integer.toString(courseStatModel.getStudentsWithMoreThanFiveDebts().size()), CellType.NUMERIC, allStatStyle);
        buildCell(17, row, Integer.toString(courseStatModel.getStudentsWithAllDebts().size()), CellType.NUMERIC, allStatStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 1));
    }

    private void buildStatPercentRow(Row row, ReportStatModel statModel, HSSFWorkbook report, HSSFSheet sheet, int rowNum) {
        HSSFCellStyle allStatStyle = getStyleForHeader(report);

        buildCell(0, row, "Итого (%)", CellType.STRING, allStatStyle);
        buildCell(1, row, "Итого (%)", CellType.STRING, allStatStyle);
        buildCell(2, row, "", CellType.STRING, allStatStyle);
        buildCell(3, row, "", CellType.STRING, allStatStyle);
        buildCell(4, row, "", CellType.STRING, allStatStyle);
        buildCell(5, row, "", CellType.STRING, allStatStyle);
        buildCell(6, row, Double.toString(statModel.getSuccessfulStudents().size() * 100.0 / statModel.getAllStudents().size()), CellType.NUMERIC, allStatStyle);
        buildCell(7, row, Double.toString(statModel.getStudentsWithOnlyFiveMarks().size() * 100.0 / statModel.getAllStudents().size()), CellType.NUMERIC, allStatStyle);
        buildCell(8, row, Double.toString(statModel.getStudentsWithFiveAndFourMarks().size() * 100.0 / statModel.getAllStudents().size()), CellType.NUMERIC, allStatStyle);
        buildCell(9, row, Double.toString(statModel.getStudentsWithOnlyFourMarks().size() * 100.0 / statModel.getAllStudents().size()), CellType.NUMERIC, allStatStyle);
        buildCell(10, row, Double.toString(statModel.getStudentsWithFiveFourThreeMarks().size() * 100.0 / statModel.getAllStudents().size()), CellType.NUMERIC, allStatStyle);
        buildCell(11, row, Double.toString(statModel.getStudentsWithOnlyThreeMarks().size() * 100.0 / statModel.getAllStudents().size()), CellType.NUMERIC, allStatStyle);
        buildCell(12, row, Double.toString(statModel.getStudentsWithDebts().size() * 100.0 / statModel.getAllStudents().size()), CellType.NUMERIC, allStatStyle);
        buildCell(13, row, Double.toString(statModel.getStudentsWithOneDebt().size() * 100.0 / statModel.getAllStudents().size()), CellType.NUMERIC, allStatStyle);
        buildCell(14, row, Double.toString(statModel.getStudentsWithTwoDebts().size() * 100.0 / statModel.getAllStudents().size()), CellType.NUMERIC, allStatStyle);
        buildCell(15, row, Double.toString(statModel.getStudentsWithThreeOrFourDebts().size() * 100.0 / statModel.getAllStudents().size()), CellType.NUMERIC, allStatStyle);
        buildCell(16, row, Double.toString(statModel.getStudentsWithMoreThanFiveDebts().size() * 100.0 / statModel.getAllStudents().size()), CellType.NUMERIC, allStatStyle);
        buildCell(17, row, Double.toString(statModel.getStudentsWithAllDebts().size() * 100.0 / statModel.getAllStudents().size()), CellType.NUMERIC, allStatStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 1));
    }

    private int buildHeader(HSSFSheet sheet, HSSFWorkbook report, int rowNum) {
        HSSFCellStyle headerStyle = getStyleForHeader(report);

        Row row = sheet.createRow(rowNum++);

        buildCell(0, row, "№ п/п", CellType.STRING, headerStyle);
        buildCell(1, row, "КУРС", CellType.STRING, headerStyle);
        buildCell(2, row, "Контингент", CellType.STRING, headerStyle);
        sheet.setColumnWidth(2, PoiPixelUtil.pixel2WidthUnits(100));
        buildCell(3, row, "Группа", CellType.STRING, headerStyle);
        sheet.setColumnWidth(3, PoiPixelUtil.pixel2WidthUnits(100));
        buildCell(4, row, "Специальность", CellType.STRING, headerStyle);
        sheet.setColumnWidth(4, PoiPixelUtil.pixel2WidthUnits(120));
        buildCell(5, row, "Вып. Кафедра", CellType.STRING, headerStyle);
        sheet.setColumnWidth(5, PoiPixelUtil.pixel2WidthUnits(80));
        buildCell(6, row, "Сдали сессию", CellType.STRING, headerStyle);
        buildCell(7, row, "Из них сдали сессию на:", CellType.STRING, headerStyle);
        buildCell(8, row, "Из них сдали сессию на:", CellType.STRING, headerStyle);
        buildCell(9, row, "Из них сдали сессию на:", CellType.STRING, headerStyle);
        buildCell(10, row, "Из них сдали сессию на:", CellType.STRING, headerStyle);
        buildCell(11, row, "Из них сдали сессию на:", CellType.STRING, headerStyle);
        buildCell(12, row, "Не сдали сессию", CellType.STRING, headerStyle);
        buildCell(13, row, "Из них имеют долги:", CellType.STRING, headerStyle);
        buildCell(14, row, "Из них имеют долги:", CellType.STRING, headerStyle);
        buildCell(15, row, "Из них имеют долги:", CellType.STRING, headerStyle);
        buildCell(16, row, "Из них имеют долги:", CellType.STRING, headerStyle);
        buildCell(17, row, "Из них имеют долги:", CellType.STRING, headerStyle);

        row = sheet.createRow(rowNum++);

        buildCell(0, row, "№ п/п", CellType.STRING, headerStyle);
        buildCell(1, row, "КУРС", CellType.STRING, headerStyle);
        buildCell(2, row, "Контингент", CellType.STRING, headerStyle);
        buildCell(3, row, "Группа", CellType.STRING, headerStyle);
        buildCell(4, row, "Специальность", CellType.STRING, headerStyle);
        buildCell(5, row, "Вып. Кафедра", CellType.STRING, headerStyle);
        buildCell(6, row, "Сдали сессию", CellType.STRING, headerStyle);
        buildCell(7, row, "5", CellType.NUMERIC, headerStyle);
        buildCell(8, row, "5 и 4", CellType.STRING, headerStyle);
        buildCell(9, row, "4", CellType.NUMERIC, headerStyle);
        buildCell(10, row, "5,4 и 3", CellType.STRING, headerStyle);
        buildCell(11, row, "3", CellType.NUMERIC, headerStyle);
        buildCell(12, row, "Не сдали сессию", CellType.STRING, headerStyle);
        buildCell(13, row, "1 долг", CellType.STRING, headerStyle);
        buildCell(14, row, "2 долга", CellType.STRING, headerStyle);
        buildCell(15, row, "3-4 долга", CellType.STRING, headerStyle);
        buildCell(16, row, "5 и более", CellType.STRING, headerStyle);
        buildCell(17, row, "вся сессия", CellType.STRING, headerStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum - 2, rowNum - 1, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 2, rowNum - 1, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 2, rowNum - 1, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 2, rowNum - 1, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 2, rowNum - 1, 4, 4));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 2, rowNum - 1, 5, 5));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 2, rowNum - 1, 6, 6));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 2, rowNum - 1, 12, 12));

        sheet.addMergedRegion(new CellRangeAddress(rowNum - 2, rowNum - 2, 7, 11));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 2, rowNum - 2, 13, 17));

        return rowNum;
    }

    private void buildCell(int column, Row row, String value, CellType type, HSSFCellStyle style) {
        Cell cell = row.createCell(column, type);

        if(type == CellType.NUMERIC) {
            try {
                cell.setCellValue(Integer.parseInt(value));
            } catch (Exception e) {
                cell.setCellValue(formatter.format(Double.parseDouble(value)));
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

    private void buildCell(int column, Row row, String value) {
        Cell cell = row.createCell(column, CellType.STRING);
        cell.setCellValue(value);
    }

    private void buildCell(int column, Row row, String value, CellType type) {
        Cell cell = row.createCell(column, type);
        cell.setCellValue(value);
    }
}
