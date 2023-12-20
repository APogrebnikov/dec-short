package org.edec.report.passportGroup.service;

import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.edec.model.HumanfaceModel;
import org.edec.passportGroup.model.GroupModel;
import org.edec.passportGroup.model.SemesterModel;
import org.edec.report.passportGroup.manager.PassportGroupReportManager;
import org.edec.report.passportGroup.model.SubjectReportModel;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.report.POIUtility;
import org.zkoss.util.media.AMedia;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PassportGroupReportService {

    public AMedia generatePasportGroupXls (SemesterModel selectedSem, List<GroupModel> groups) throws IOException {
        Workbook workbook = new HSSFWorkbook();

        PassportGroupReportManager passportGroupReportManager = new PassportGroupReportManager();

        CellStyle defaultStyle = getDefaultStyle(workbook);

        for (GroupModel group : groups) {
            List<SubjectReportModel> subjects = passportGroupReportManager.getSubjectsByLgs(group.getIdLgs());
            List<HumanfaceModel> humanfaces = passportGroupReportManager.getHumanfacesByIdLGS(group.getIdLgs(), group.getIdDicGroup());
            subjects = divideSubjectByFoc(subjects);

            if (subjects.size() == 0 || humanfaces.size() == 0) {
                continue;
            }

            int sizeOfSubject = subjects.size();
            int indexLastSubject = 2 + (2 * sizeOfSubject);
            int countOfColumnOnSecondPage = 8;

            Sheet sheet = createSheet(workbook, group.getGroupName());
            fillColumnsWidth(sheet, sizeOfSubject);

            for (int i = 0; i <= indexLastSubject + countOfColumnOnSecondPage; i++) {
                sheet.setDefaultColumnStyle(i, defaultStyle);
            }

            /*Заголовок 1 (Группа)*/
            Row row1 = sheet.createRow(0);

            Cell cell11 = row1.createCell(0);
            cell11.setCellValue("Выполнения учебного плана группой " + group.getGroupName());
            cell11.setCellStyle(defaultStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, indexLastSubject));

            Cell cell12 = row1.createCell(indexLastSubject + 1);
            cell12.setCellValue("");
            cell12.setCellStyle(defaultStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, indexLastSubject + 1, indexLastSubject + countOfColumnOnSecondPage));

            /*Заголовок 2 (Семестр)*/
            Row row2 = sheet.createRow(1);

            Cell cell21 = row2.createCell(0);
            cell21.setCellValue("за " + (selectedSem.getSeason() == 0 ? "осенний" : "весенний") + " семестр " +
                                DateConverter.convert2dateToString(selectedSem.getDateOfBegin(), selectedSem.getDateOfEnd()) +
                                " учебного года");
            cell21.setCellStyle(defaultStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, indexLastSubject));

            Cell cell22 = row2.createCell(indexLastSubject + 1);
            cell22.setCellValue("");
            cell22.setCellStyle(defaultStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, indexLastSubject + 1, indexLastSubject + countOfColumnOnSecondPage));

            /*Блок с заголовками и предметами*/
            Row row3 = sheet.createRow(2);

            Cell cell31 = row3.createCell(0);
            cell31.setCellValue("Для заметок");
            cell31.setCellStyle(defaultStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 6, 0, 0));

            Cell cell33 = row3.createCell(1);
            cell33.setCellValue("№ п/п");
            cell33.setCellStyle(defaultStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 6, 1, 1));

            Cell cell34 = row3.createCell(2);
            cell34.setCellValue("Фамилия И.О.");
            cell34.setCellStyle(defaultStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 6, 2, 2));

            Cell cell35 = row3.createCell(3);
            cell35.setCellValue("Успеваемость за семестр");
            cell35.setCellStyle(defaultStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 3, indexLastSubject));

            Cell cell36 = row3.createCell(indexLastSubject + 1);
            cell36.setCellValue("Примечание");
            cell36.setCellStyle(defaultStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 6, indexLastSubject + 1, indexLastSubject + 1));

            Cell cell37 = row3.createCell(indexLastSubject + 2);
            cell37.setCellValue("№ п/п");
            cell37.setCellStyle(defaultStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 6, indexLastSubject + 2, indexLastSubject + 2));

            Cell cell38 = row3.createCell(indexLastSubject + 3);
            cell38.setCellValue("Закрытие сессии");
            cell38.setCellStyle(defaultStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 5, indexLastSubject + 3, indexLastSubject + 4));

            Cell cell39 = row3.createCell(indexLastSubject + 5);
            cell39.setCellValue("Продление сессии");
            cell39.setCellStyle(defaultStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 5, indexLastSubject + 5, indexLastSubject + 6));

            Cell cell310 = row3.createCell(indexLastSubject + 7);
            cell310.setCellValue("Приказы");
            cell310.setCellStyle(defaultStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 5, indexLastSubject + 7, indexLastSubject + 8));

            /*Формы контроля*/
            Row row4 = sheet.createRow(3);

            // Количество предметов у одной формы контроля, нужно для задачи колонок
            int countOfFocSubject = 0;
            // 2 - иднекс последней статической колноки
            int lastIndexOfFoc = 2;
            String currentFoc = "";

            for (int i = 0; i < subjects.size(); i++) {
                SubjectReportModel subject = subjects.get(i);
                SubjectReportModel nextSubject = null;
                if (i + 1 != subjects.size()) {
                    nextSubject = subjects.get(i + 1);
                }
                //Если поменялась форма контроля, то обновляем переменные (ФК + количество предметов)
                if (!currentFoc.equals(subject.getFoc())) {
                    currentFoc = subject.getFoc();
                    countOfFocSubject = 0;
                }
                ++countOfFocSubject;
                //Если это последний предмет или формы контроля у текущего и следующего предмета различаются,
                // то создаем форму контроля
                if (nextSubject == null || !subject.getFoc().equals(nextSubject.getFoc())) {
                    Cell cellFoc = row4.createCell(lastIndexOfFoc + 1);
                    cellFoc.setCellValue(currentFoc);
                    cellFoc.setCellStyle(defaultStyle);
                    sheet.addMergedRegion(new CellRangeAddress(3, 3, lastIndexOfFoc + 1, lastIndexOfFoc + (countOfFocSubject * 2)));
                    lastIndexOfFoc = lastIndexOfFoc + (countOfFocSubject * 2);
                }
                /*if (i + 1 == subjects.size() || (!currentFoc.equals("") && !currentFoc.equals(subject.getFoc()))) {
                    Cell cellFoc = row4.createCell(lastIndexOfFoc + 1);
                    cellFoc.setCellValue(currentFoc);
                    cellFoc.setCellStyle(defaultStyle);
                    if (i + 1 == subjects.size()) {
                        ++countOfFocSubject;
                    }
                    sheet.addMergedRegion(new CellRangeAddress(3, 3, lastIndexOfFoc + 1, lastIndexOfFoc + (countOfFocSubject * 2)));
                    lastIndexOfFoc = lastIndexOfFoc + (countOfFocSubject * 2);
                }
                ++countOfFocSubject;*/
            }

            /*Названия предметов и часы*/
            Row row5 = sheet.createRow(4);
            Row row6 = sheet.createRow(5);
            Row row7 = sheet.createRow(6);

            row5.setHeight((short) ((420 * 2) + 240));

            int lastIndexForSubject = 2; //Последний индекс статического поля

            for (SubjectReportModel subject : subjects) {
                ++lastIndexForSubject;

                Cell cellSubject = row5.createCell(lastIndexForSubject);
                cellSubject.setCellValue(subject.getSubjectname());
                cellSubject.setCellStyle(defaultStyle);
                sheet.addMergedRegion(new CellRangeAddress(4, 4, lastIndexForSubject, lastIndexForSubject + 1));

                Cell cellHourscount = row6.createCell(lastIndexForSubject);
                cellHourscount.setCellValue(String.valueOf(subject.getHourscount()));
                cellHourscount.setCellStyle(defaultStyle);
                sheet.addMergedRegion(new CellRangeAddress(5, 5, lastIndexForSubject, lastIndexForSubject + 1));

                //Зачет и ведомость
                Cell cellZachetka = row7.createCell(lastIndexForSubject);
                cellZachetka.setCellValue("зач");
                cellZachetka.setCellStyle(defaultStyle);

                Cell cellVedomost = row7.createCell(lastIndexForSubject + 1);
                cellVedomost.setCellValue("вед");
                cellVedomost.setCellStyle(defaultStyle);

                ++lastIndexForSubject;
            }

            Cell cell71 = row7.createCell(indexLastSubject + 3);
            cell71.setCellValue("Дата");
            cell71.setCellStyle(defaultStyle);

            Cell cell72 = row7.createCell(indexLastSubject + 4);
            cell72.setCellValue("Подпись");
            cell72.setCellStyle(defaultStyle);

            Cell cell73 = row7.createCell(indexLastSubject + 5);
            cell73.setCellValue("Дата");
            cell73.setCellStyle(defaultStyle);

            Cell cell74 = row7.createCell(indexLastSubject + 6);
            cell74.setCellValue("Подпись");
            cell74.setCellStyle(defaultStyle);

            Cell cell75 = row7.createCell(indexLastSubject + 7);
            cell75.setCellValue("Дата");
            cell75.setCellStyle(defaultStyle);

            Cell cell76 = row7.createCell(indexLastSubject + 8);
            cell76.setCellValue("№");
            cell76.setCellStyle(defaultStyle);

            /*Информация по студентам*/
            for (int i = 0; i < 38; i++) {
                Row row = sheet.createRow(7 + i);
                HumanfaceModel humanface = null;
                if (i < humanfaces.size()) {
                    humanface = humanfaces.get(i);
                }

                for (int j = 0; j < indexLastSubject + 9; j++) {
                    Cell cell = row.createCell(j);
                    if (j == 1 || j == indexLastSubject + 2) {
                        cell.setCellValue(i + 1);
                    }
                    if (j == 2) {
                        if (humanface != null) {
                            cell.setCellValue(humanface.getShortFio());
                        }
                    }
                    cell.setCellStyle(defaultStyle);
                }
            }
        }

        AMedia aMedia = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
            aMedia = new AMedia(
                    "Паспорт групп " + DateConverter.convert2dateToString(selectedSem.getDateOfBegin(), selectedSem.getDateOfEnd()) + " " +
                    (selectedSem.getSeason() == 0 ? "осень" : "весна"), "xls", "application/xls", bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        bos.close();
        workbook.close();
        return aMedia;
    }

    private Sheet createSheet(Workbook workbook, String groupname) {
        groupname = groupname.replaceAll("/", "-");
        Sheet sheet = workbook.createSheet(groupname);
        sheet.setFitToPage(true);
        sheet.setAutobreaks(true);

        sheet.getPrintSetup().setLandscape(true);
        sheet.getPrintSetup().setHeaderMargin(0);
        sheet.getPrintSetup().setFooterMargin(0);
        sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A3_PAPERSIZE);

        return sheet;
    }

    private void fillColumnsWidth(Sheet sheet, int sizeOfSubject) {
        sheet.setColumnWidth(0, POIUtility.pixel2WidthUnits(50));
        sheet.setColumnWidth(1, POIUtility.pixel2WidthUnits(30));
        sheet.setColumnWidth(2, POIUtility.pixel2WidthUnits(100));

        int lastIndexOfSubject = 2 + (sizeOfSubject * 2);
        double widthForSubject = 550D / (double) (sizeOfSubject * 2);
        for (int i = 3; i <= lastIndexOfSubject; i++) {
            sheet.setColumnWidth(i, POIUtility.pixel2WidthUnits((int) widthForSubject));
        }

        sheet.setColumnWidth(lastIndexOfSubject + 1, POIUtility.pixel2WidthUnits(265));
        sheet.setColumnWidth(lastIndexOfSubject + 2, POIUtility.pixel2WidthUnits(30));
        sheet.setColumnWidth(lastIndexOfSubject + 3, POIUtility.pixel2WidthUnits(60));
        sheet.setColumnWidth(lastIndexOfSubject + 4, POIUtility.pixel2WidthUnits(60));
        sheet.setColumnWidth(lastIndexOfSubject + 5, POIUtility.pixel2WidthUnits(60));
        sheet.setColumnWidth(lastIndexOfSubject + 6, POIUtility.pixel2WidthUnits(60));
        sheet.setColumnWidth(lastIndexOfSubject + 7, POIUtility.pixel2WidthUnits(60));
        sheet.setColumnWidth(lastIndexOfSubject + 8, POIUtility.pixel2WidthUnits(150));
    }

    private CellStyle getDefaultStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setWrapText(true);
        return cellStyle;
    }

    private List<SubjectReportModel> divideSubjectByFoc(List<SubjectReportModel> subjects) {
        List<SubjectReportModel> result = new ArrayList<>();

        List<SubjectReportModel> examList = new ArrayList<>();
        List<SubjectReportModel> passList = new ArrayList<>();
        List<SubjectReportModel> cpList = new ArrayList<>();
        List<SubjectReportModel> cwList = new ArrayList<>();
        List<SubjectReportModel> practicList = new ArrayList<>();

        for (SubjectReportModel subject : subjects) {
            if (subject.getExam()) {
                examList.add(new SubjectReportModel(subject.getSubjectname(), "Экзамен", subject.getHourscount()));
            }
            if (subject.getPass()) {
                passList.add(new SubjectReportModel(subject.getSubjectname(), "Зачет", subject.getHourscount()));
            }
            if (subject.getCp()) {
                cpList.add(new SubjectReportModel(subject.getSubjectname(), "КП", subject.getHourscount()));
            }
            if (subject.getCw()) {
                cwList.add(new SubjectReportModel(subject.getSubjectname(), "КР", subject.getHourscount()));
            }
            if (subject.getPractic()) {
                practicList.add(new SubjectReportModel(subject.getSubjectname(), "Практика", subject.getHourscount()));
            }
        }

        result.addAll(examList);
        result.addAll(passList);
        result.addAll(cpList);
        result.addAll(cwList);
        result.addAll(practicList);

        return result;
    }
}
