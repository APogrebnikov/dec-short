package org.edec.kcp.service.impl;

import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.edec.kcp.model.KCPFullModel;
import org.edec.utility.constants.FormOfStudyConst;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class PrintService {
    public byte[] generateReportXlsx(List<KCPFullModel> reportModelList) {
        Workbook book = new HSSFWorkbook();
        // Подготовка заголовков
        Sheet sheet = book.createSheet("Контрольные цифры приема");
        sheet.getPrintSetup().setHeaderMargin(0);
        sheet.getPrintSetup().setFooterMargin(0);
        sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
        sheet.getPrintSetup().setLandscape(true);
        setHeadersForSheet(sheet);

        // Генерация строк
        int index = 2;
        // Генерируем контент для отчета
        for (KCPFullModel reportModel : reportModelList) {
            generateContentRow(sheet, reportModel, index);
            index++;
        }

        // Генерация файла
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            book.write(bos);
            bos.close();
            return bos.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    public void generateContentRow(Sheet sheet, KCPFullModel data, int index) {
        Row rowContent = sheet.createRow(index);
        rowContent.createCell(0).setCellValue(data.getDirectioncode() + " "
                + data.getDirection()
                + " (" + FormOfStudyConst.getNameByType(data.getFos()) + ")");
        rowContent.createCell(1).setCellValue(Integer.valueOf(data.getStartyear()));

        Integer kcpBudget = data.getKcpBudget() == null ? 0 : data.getKcpBudget();
        Integer kcpDogovor = data.getKcpDogovor() == null ? 0 : data.getKcpDogovor();
        Integer kcpTotal = kcpBudget + kcpDogovor;
        rowContent.createCell(2).setCellValue(kcpTotal);
        rowContent.createCell(3).setCellValue(kcpBudget);
        rowContent.createCell(4).setCellValue(kcpDogovor);

        Integer contingetTotal = data.getContingentTotal() == null ? 0 : data.getContingentTotal();
        rowContent.createCell(5).setCellValue(contingetTotal);
        Double contingentTotalPerc = calculatePercentage(contingetTotal, kcpTotal);
        rowContent.createCell(6).setCellValue(contingentTotalPerc);
        Integer contingetBudget = data.getContingentBudget() == null ? 0 : data.getContingentBudget();
        rowContent.createCell(7).setCellValue(contingetBudget);
        Double contingentBudgetPerc = calculatePercentage(contingetBudget, kcpBudget);
        rowContent.createCell(8).setCellValue(contingentBudgetPerc);
        Integer contingetDogovor = data.getContingentDogovor() == null ? 0 : data.getContingentDogovor();
        rowContent.createCell(9).setCellValue(contingetDogovor);
        Double contingentDogovorPerc = calculatePercentage(contingetDogovor, kcpDogovor);
        rowContent.createCell(10).setCellValue(contingentDogovorPerc);

        Integer contingetAfterTotal = data.getAfterCommTotal() == null ? 0 : data.getAfterCommTotal();
        rowContent.createCell(11).setCellValue(contingetAfterTotal);
        Double contingentTotalAfterPerc = calculatePercentage(contingetAfterTotal, kcpTotal);
        rowContent.createCell(12).setCellValue(contingentTotalAfterPerc);
        Integer contingetAfterBudget = data.getAfterCommBudget() == null ? 0 : data.getAfterCommBudget();
        rowContent.createCell(13).setCellValue(contingetAfterBudget);
        Double contingentBudgetAfterPerc = calculatePercentage(contingetAfterBudget, kcpBudget);
        rowContent.createCell(14).setCellValue(contingentBudgetAfterPerc);
        Integer contingetAfterDogovor = data.getAfterCommDogovor() == null ? 0 : data.getAfterCommDogovor();
        rowContent.createCell(15).setCellValue(contingetAfterDogovor);
        Double contingentDogovorAfterPerc = calculatePercentage(contingetAfterDogovor, kcpDogovor);
        rowContent.createCell(16).setCellValue(contingentDogovorAfterPerc);
    }

    private void setHeadersForSheet(Sheet sheet) {

        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setWrapText(true);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Row preRow = sheet.createRow(0);
        preRow.createCell(0).setCellValue("");
        sheet.setColumnWidth(0, 14000);
        preRow.createCell(1).setCellValue("");
        preRow.createCell(2).setCellValue("КЦП");
        preRow.createCell(3).setCellValue("");
        preRow.createCell(4).setCellValue("");
        preRow.createCell(5).setCellValue("Контингент");
        preRow.createCell(6).setCellValue("");
        preRow.createCell(7).setCellValue("");
        preRow.createCell(8).setCellValue("");
        preRow.createCell(9).setCellValue("");
        preRow.createCell(10).setCellValue("");
        preRow.createCell(11).setCellValue("После отчисления");
        preRow.createCell(12).setCellValue("");
        preRow.createCell(13).setCellValue("");
        preRow.createCell(14).setCellValue("");
        preRow.createCell(15).setCellValue("");
        preRow.createCell(16).setCellValue("");

        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue("Направление");
        row.createCell(1).setCellValue("Год набора");
        row.createCell(2).setCellValue("Всего");
        row.createCell(3).setCellValue("Бюджет");
        row.createCell(4).setCellValue("Договор");
        //row.getCell(4).setCellStyle(style);
        row.createCell(5).setCellValue("Всего");
        row.createCell(6).setCellValue("%");
        row.createCell(7).setCellValue("Бюджет");
        row.createCell(8).setCellValue("%");
        row.createCell(9).setCellValue("Договор");
        row.createCell(10).setCellValue("%");

        row.createCell(11).setCellValue("Всего");
        row.createCell(12).setCellValue("%");
        row.createCell(13).setCellValue("Бюджет");
        row.createCell(14).setCellValue("%");
        row.createCell(15).setCellValue("Договор");
        row.createCell(16).setCellValue("%");
        // Фиксирование заголовка на экране
        sheet.createFreezePane(0, 2);

        // Объединение заданных строк и колонок заголовка
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        setBordersToMergedCells(sheet, new CellRangeAddress(0, 1, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 4));
        setBordersToMergedCells(sheet, new CellRangeAddress(0, 1, 2, 4));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 10));
        setBordersToMergedCells(sheet, new CellRangeAddress(0, 1, 5, 10));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 11, 16));
        setBordersToMergedCells(sheet, new CellRangeAddress(0, 1, 11, 16));
    }

    protected void setBordersToMergedCells(Sheet sheet, CellRangeAddress rangeAddress) {
        RegionUtil.setBorderTop(2, rangeAddress, sheet);
        RegionUtil.setBorderLeft(2, rangeAddress, sheet);
        RegionUtil.setBorderRight(2, rangeAddress, sheet);
        RegionUtil.setBorderBottom(2, rangeAddress, sheet);
    }

    public Double calculatePercentage(int obtained, int total) {
        if (total == 0) {
            return Double.valueOf(0);
        }
        return Double.valueOf(obtained * 100 / total);
    }
}
