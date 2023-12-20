package org.edec.contingentMovement.service.impl;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.edec.contingentMovement.manager.ReportManagerDAO;
import org.edec.contingentMovement.manager.ReportManagerMineDAO;
import org.edec.contingentMovement.model.GroupModel;
import org.edec.contingentMovement.model.ReportModel;
import org.edec.contingentMovement.model.StudentMoveModel;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReportService {

    public ReportManagerDAO reportManagerDAO = new ReportManagerDAO();
    public ReportManagerMineDAO reportManagerMineDAO = new ReportManagerMineDAO();
    public List<GroupModel> groups = new ArrayList<>();

    /**
     * Переменные для подсчета итоговых сумм
     */
    public Integer totalBudget = 0;
    public Integer totalBudgetOld = 0;
    public Integer totalUnBudget = 0;
    public Integer totalUnBudgetOld = 0;
    public Integer totalBudgetAcademic = 0;
    public Integer totalBudgetAcademicOld = 0;
    public Integer totalUnBudgetAcademic = 0;
    public Integer totalUnBudgetAcademicOld = 0;
    public Integer totalBudgetMoveIn = 0;
    public Integer totalUnBudgetMoveIn = 0;
    public Integer totalBudgetMoveOut = 0;
    public Integer totalUnBudgetMoveOut = 0;
    public Integer totalBudgetAcademicMoveOut = 0;
    public Integer totalUnBudgetAcademicMoveOut = 0;

    public List<ReportModel> initReport(String fos, Date fromDate, Date toDate) {
        //groups = reportManagerDAO.getGroupList(1);
        groups = reportManagerMineDAO.getAllGroups(fos,27);
        //groups = generateGroupsTemp();

        List<ReportModel> reportModels = new ArrayList<>();
        for (GroupModel group : groups) {
            ReportModel reportModel = new ReportModel();
            reportModel.setGroupName(group.getGroupname());
            reportModel.setGroupId(group.getIdDG());
            reportModel.setGroup(group);
            reportModel.setBudgetStudents(reportManagerMineDAO.getCountBudget(group.getGroupname()));
            reportModel.setUnBudgetStudents(reportManagerMineDAO.getCountUnBudget(group.getGroupname()));
            reportModel.setBudgetAcademicStudents(reportManagerMineDAO.getCountBudgetAcademic(group.getGroupname()));
            reportModel.setUnBudgetAcademicStudents(reportManagerMineDAO.getCountUnBudgetAcademic(group.getGroupname()));
            reportModel.setBudgetMoveInStudents(reportManagerMineDAO.getCountBudgetMoveIn(group.getGroupname(), fromDate, toDate));
            reportModel.setUnBudgetMoveInStudents(reportManagerMineDAO.getCountUnBudgetMoveIn(group.getGroupname(), fromDate, toDate));
            reportModel.setBudgetMoveOutStudents(reportManagerMineDAO.getCountBudgetMoveOut(group.getGroupname(), fromDate, toDate));
            reportModel.setUnBudgetMoveOutStudents(reportManagerMineDAO.getCountUnBudgetMoveOut(group.getGroupname(), fromDate, toDate));
            reportModel.setBudgetAcademicMoveOutStudents(reportManagerMineDAO.getCountBudgetAcademicMoveOut(group.getGroupname(), fromDate, toDate));
            reportModel.setUnBudgetAcademicMoveOutStudents(reportManagerMineDAO.getCountUnBudgetAcademicMoveOut(group.getGroupname(), fromDate, toDate));

            // Два дополнительных запроса, которых не хватало для отчета
            reportModel.setBudgetAcademicMoveInStudents(reportManagerMineDAO.getCountBudgetAcademicMoveIn(group.getGroupname(), fromDate, toDate));
            reportModel.setUnBudgetAcademicMoveInStudents(reportManagerMineDAO.getCountUnBudgetAcademicMoveIn(group.getGroupname(), fromDate, toDate));

            reportModels.add(reportModel);
        }
        return reportModels;
    }

    public String printReport(List<ReportModel> reportModels) {
        String report = "";
        for (ReportModel reportModel : reportModels) {
            report += "\n" + reportModel.getGroupName() +
                    "\t" + reportModel.getBudgetStudents().size() +
                    "\t" + reportModel.getUnBudgetStudents().size() +
                    "\t" + reportModel.getBudgetAcademicStudents().size() +
                    "\t" + reportModel.getUnBudgetAcademicStudents().size() +
                    "\t" + reportModel.getBudgetMoveInStudents().size() +
                    "\t" + reportModel.getUnBudgetMoveInStudents().size() +
                    "\t" + reportModel.getBudgetMoveOutStudents().size() +
                    "\t" + reportModel.getUnBudgetMoveOutStudents().size() +
                    "\t" + reportModel.getBudgetAcademicMoveOutStudents().size() +
                    "\t" + reportModel.getUnBudgetAcademicMoveOutStudents().size();
        }
        return report;
    }

    /**
     * Загружаем все измерения за конкретную дату в БД
     * @param reportModels
     * @param createdDate
     */
    public void insertAllMeasures(List<ReportModel> reportModels, Date createdDate) {
        for (ReportModel reportModel : reportModels) {
            reportManagerDAO.insertOneMeasure(reportModel, createdDate);
        }
    }

    public List<String> generateGroupsTemp() {
        List<String> tempGroupList = new ArrayList<>();
        tempGroupList.add("КИ16-16Б");
        tempGroupList.add("КИ16-17Б");
        tempGroupList.add("КИ17-16Б");
        tempGroupList.add("КИ17-17Б");
        tempGroupList.add("КИ17-03Б");
        tempGroupList.add("КИ16-08Б");
        tempGroupList.add("КИ15-11Б");
        tempGroupList.add("КИ15-17Б");
        tempGroupList.add("КИ15-04Б");
        return tempGroupList;
    }

    public void generateReportCSV (List<ReportModel> reportModels) throws FileNotFoundException {
        Writer out;
        out = new OutputStreamWriter(new FileOutputStream("students_contingent.csv"), Charset.forName("Cp1251"));
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.EXCEL.withDelimiter(';'))) {
            for (ReportModel reportModel : reportModels) {
                List studentsRecord = new ArrayList();
                studentsRecord.add(reportModel.getGroupName());
                studentsRecord.add(concatStudents(reportModel.getBudgetStudents()));
                studentsRecord.add(concatStudents(reportModel.getUnBudgetStudents()));
                studentsRecord.add(concatStudents(reportModel.getBudgetAcademicStudents()));
                studentsRecord.add(concatStudents(reportModel.getUnBudgetAcademicStudents()));
                studentsRecord.add(concatStudents(reportModel.getBudgetMoveInStudents()));
                studentsRecord.add(concatStudents(reportModel.getUnBudgetMoveInStudents()));
                studentsRecord.add(concatStudents(reportModel.getBudgetMoveOutStudents()));
                studentsRecord.add(concatStudents(reportModel.getUnBudgetMoveOutStudents()));
                studentsRecord.add(concatStudents(reportModel.getBudgetAcademicMoveOutStudents()));
                studentsRecord.add(concatStudents(reportModel.getUnBudgetAcademicMoveOutStudents()));
                studentsRecord.add(concatStudents(reportModel.getBudgetAcademicMoveInStudents()));
                studentsRecord.add(concatStudents(reportModel.getUnBudgetAcademicMoveInStudents()));
                printer.printRecord(studentsRecord);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String concatStudents (List<StudentMoveModel> studentMoveModels) {
        String res = "";
        for (StudentMoveModel studentMoveModel : studentMoveModels) {
            res += "|" + studentMoveModel.getFio();
        }
        return res;
    }


    public byte[] generateReportXlsx(List<ReportModel> reportModels) {
        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("Движение контингента");
        sheet.getPrintSetup().setHeaderMargin(0);
        sheet.getPrintSetup().setFooterMargin(0);
        sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
        sheet.getPrintSetup().setLandscape(true);
        setHeadersForSheet(sheet);

        int index = 2;
        // Сбрасываем счетчики
        resetTotal();
        // Генерируем контент для отчета
        for (ReportModel reportModel : reportModels) {
            generateContentRow(sheet, reportModel, index);
            index++;
        }

        generateSumRow(sheet, index);

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            book.write(bos);
            bos.close();
            return bos.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private void setHeadersForSheet (Sheet sheet) {

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
        preRow.createCell(0).setCellValue("Шифр специальности, направление, программа");
        preRow.getCell(0).setCellStyle(style);
        sheet.setColumnWidth(0,4000);
        preRow.createCell(1).setCellValue("Шифр группы");
        preRow.getCell(1).setCellStyle(style);
        sheet.setColumnWidth(1,4000);
        preRow.createCell(2).setCellValue("Кафедра");
        preRow.getCell(2).setCellStyle(style);
        sheet.setColumnWidth(2,5000);
        preRow.createCell(3).setCellValue("На 1 число прошлого месяца");
        preRow.getCell(3).setCellStyle(style);
        preRow.createCell(4).setCellValue("");
        preRow.createCell(5).setCellValue("");
        preRow.createCell(6).setCellValue("");
        preRow.createCell(7).setCellValue("Прибыло");
        preRow.createCell(8).setCellValue("");
        preRow.createCell(9).setCellValue("Выбыло");
        preRow.createCell(10).setCellValue("");
        preRow.createCell(11).setCellValue("На 1 число текущего месяца");
        preRow.createCell(12).setCellValue("");
        preRow.createCell(13).setCellValue("");
        preRow.createCell(14).setCellValue("");

        Row row = sheet.createRow(1);
        row.setHeightInPoints(75f);
        row.createCell(0).setCellValue("");
        row.createCell(1).setCellValue("");
        row.createCell(2).setCellValue("");
        row.createCell(3).setCellValue("Обучающиеся на бюджет. основе");
        row.getCell(3).setCellStyle(style);
        row.createCell(4).setCellValue("Обучающиеся на полной компенсационной основе");
        row.getCell(4).setCellStyle(style);
        row.createCell(5).setCellValue("В академическом отпуске, бюджетная основа");
        row.getCell(5).setCellStyle(style);
        row.createCell(6).setCellValue("В академическом отпуске, компенсационная основа");
        row.getCell(6).setCellStyle(style);
        row.createCell(7).setCellValue("Обучающиеся на бюджет. основе");
        row.getCell(7).setCellStyle(style);
        row.createCell(8).setCellValue("Обучающиеся на полной компенсационной основе");
        row.getCell(8).setCellStyle(style);
        row.createCell(9).setCellValue("Обучающиеся на бюджет. основе");
        row.getCell(9).setCellStyle(style);
        row.createCell(10).setCellValue("Обучающиеся на полной компенсационной основе");
        row.getCell(10).setCellStyle(style);
        row.createCell(11).setCellValue("Обучающиеся на бюджет. основе");
        row.getCell(11).setCellStyle(style);
        row.createCell(12).setCellValue("Обучающиеся на полной компенсационной основе");
        row.getCell(12).setCellStyle(style);
        row.createCell(13).setCellValue("В академическом отпуске, бюджетная основа");
        row.getCell(13).setCellStyle(style);
        row.createCell(14).setCellValue("В академическом отпуске, компенсационная основа");
        row.getCell(14).setCellStyle(style);
        // Фиксирование заголовка на экране
        sheet.createFreezePane(0, 2);

        // Объединение заданных строк и колонок заголовка
        sheet.addMergedRegion(new CellRangeAddress(0,1,0,0));
        setBordersToMergedCells(sheet, new CellRangeAddress(0,1,0,0));
        sheet.addMergedRegion(new CellRangeAddress(0,1,1,1));
        setBordersToMergedCells(sheet, new CellRangeAddress(0,1,1,1));
        sheet.addMergedRegion(new CellRangeAddress(0,1,2,2));
        setBordersToMergedCells(sheet, new CellRangeAddress(0,1,2,2));
        sheet.addMergedRegion(new CellRangeAddress(0,0,3,6));
        setBordersToMergedCells(sheet, new CellRangeAddress(0,0,3,6));
        sheet.addMergedRegion(new CellRangeAddress(0,0,7,8));
        setBordersToMergedCells(sheet, new CellRangeAddress(0,0,7,8));
        sheet.addMergedRegion(new CellRangeAddress(0,0,9,10));
        setBordersToMergedCells(sheet, new CellRangeAddress(0,0,9,10));
        sheet.addMergedRegion(new CellRangeAddress(0,0,11,14));
        setBordersToMergedCells(sheet, new CellRangeAddress(0,0,11,14));
    }

    protected void setBordersToMergedCells(Sheet sheet, CellRangeAddress rangeAddress) {
        RegionUtil.setBorderTop(2, rangeAddress, sheet);
        RegionUtil.setBorderLeft(2, rangeAddress, sheet);
        RegionUtil.setBorderRight(2, rangeAddress, sheet);
        RegionUtil.setBorderBottom(2, rangeAddress, sheet);
    }

    /**
     * Заполняем конкретную строку данных информацией из модели движения контингента для группы
     * @param sheet
     * @param reportModel
     * @param index
     */
    public void generateContentRow (Sheet sheet, ReportModel reportModel, int index) {
        Row rowContent = sheet.createRow(index);
        // Шифр
        if (reportModel.getGroup()!=null){
            rowContent.createCell(0).setCellValue(reportModel.getGroup().getSpeciality()+" "+reportModel.getGroup().getSpecname());
        }else{
            rowContent.createCell(0).setCellValue("-");
        }

        // Группа
        rowContent.createCell(1).setCellValue(reportModel.getGroupName());

        // Кафедра
        if (reportModel.getGroup()!=null){
            rowContent.createCell(2).setCellValue(reportModel.getGroup().getShortDepartment());
        }else{
            rowContent.createCell(2).setCellValue("-");
        }

        // Прошлое - бюджет
        rowContent.createCell(3).setCellValue(reportModel.getCountOfBudgetOld());
        totalBudgetOld += reportModel.getCountOfBudgetOld();

        // Прошлое - договор
        rowContent.createCell(4).setCellValue(reportModel.getCountOfUnBudgetOld());
        totalUnBudgetOld += reportModel.getCountOfUnBudgetOld();

        // Прошлое - академ бюджет
        rowContent.createCell(5).setCellValue(reportModel.getCountOfBudgetAcademicOld());
        totalBudgetAcademicOld += reportModel.getCountOfBudgetAcademicOld();

        // Прошлое - академ договор
        rowContent.createCell(6).setCellValue(reportModel.getCountOfUnBudgetAcademicOld());
        totalUnBudgetAcademicOld += reportModel.getCountOfUnBudgetAcademicOld();

        // Прибыло - бюджет
        String moveInBudget = "";
        moveInBudget = String.valueOf(reportModel.getCountOfBudgetMoveIn());
        rowContent.createCell(7).setCellValue(moveInBudget);
        totalBudgetMoveIn += reportModel.getCountOfBudgetMoveIn();

        // Прибыло - договор
        String moveInUnBudget = "";
        moveInUnBudget = String.valueOf(reportModel.getCountOfUnBudgetMoveIn());
        rowContent.createCell(8).setCellValue(moveInUnBudget);
        totalUnBudgetMoveIn += reportModel.getCountOfUnBudgetMoveIn();

        // Убыло - бюджет
        String moveOutBudget = "";
        moveOutBudget = String.valueOf(reportModel.getCountOfBudgetMoveOut());
        rowContent.createCell(9).setCellValue(moveOutBudget);
        totalBudgetMoveOut += reportModel.getCountOfBudgetMoveOut();

        // Убыло - договор
        String moveOutUnBudget = "";
        moveOutUnBudget = String.valueOf(reportModel.getCountOfUnBudgetMoveOut());
        rowContent.createCell(10).setCellValue(moveOutUnBudget);
        totalUnBudgetMoveOut += reportModel.getCountOfUnBudgetMoveOut();

        // Настоящее - бюджет
        rowContent.createCell(11).setCellValue(reportModel.getCountOfBudget());
        totalBudget += reportModel.getCountOfBudget();

        // Настоящее - договор
        rowContent.createCell(12).setCellValue(reportModel.getCountOfUnBudget());
        totalUnBudget += reportModel.getCountOfUnBudget();

        // Настоящее - академ бюджет
        rowContent.createCell(13).setCellValue(reportModel.getCountOfBudgetAcademic());
        totalBudgetAcademic += reportModel.getCountOfBudgetAcademic();

        // Настоящее - академ договор
        rowContent.createCell(14).setCellValue(reportModel.getCountOfUnBudgetAcademic());
        totalUnBudgetAcademic += reportModel.getCountOfUnBudgetAcademic();
    }

    /**
     * Генерация строки Итого
     * @param sheet
     */
    public void generateSumRow (Sheet sheet, int index) {
        CellStyle style = sheet.getWorkbook().createCellStyle();
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

        Row sumRow = sheet.createRow(index);
        sumRow.createCell(0).setCellValue("ИТОГО");
        sumRow.getCell(0).setCellStyle(style);
        sumRow.createCell(1).setCellValue("");
        sumRow.getCell(1).setCellStyle(style);
        sumRow.createCell(2).setCellValue("");
        sumRow.getCell(2).setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(index,index,0,2));
        setBordersToMergedCells(sheet, new CellRangeAddress(index,index,0,2));

        sumRow.createCell(3).setCellValue(totalBudgetOld);
        sumRow.getCell(3).setCellStyle(style);
        sumRow.createCell(4).setCellValue(totalUnBudgetOld);
        sumRow.getCell(4).setCellStyle(style);
        sumRow.createCell(5).setCellValue(totalBudgetAcademicOld);
        sumRow.getCell(5).setCellStyle(style);
        sumRow.createCell(6).setCellValue(totalUnBudgetAcademicOld);
        sumRow.getCell(6).setCellStyle(style);

        sumRow.createCell(7).setCellValue(totalBudgetMoveIn);
        sumRow.getCell(7).setCellStyle(style);
        sumRow.createCell(8).setCellValue(totalUnBudgetMoveIn);
        sumRow.getCell(8).setCellStyle(style);
        sumRow.createCell(9).setCellValue(totalBudgetMoveOut);
        sumRow.getCell(9).setCellStyle(style);
        sumRow.createCell(10).setCellValue(totalUnBudgetMoveOut);
        sumRow.getCell(10).setCellStyle(style);
        sumRow.createCell(11).setCellValue(totalBudget);
        sumRow.getCell(11).setCellStyle(style);
        sumRow.createCell(12).setCellValue(totalUnBudget);
        sumRow.getCell(12).setCellStyle(style);
        sumRow.createCell(13).setCellValue(totalBudgetAcademic);
        sumRow.getCell(13).setCellStyle(style);
        sumRow.createCell(14).setCellValue(totalUnBudgetAcademic);
        sumRow.getCell(14).setCellStyle(style);

    }

    /**
     * Геренация данных для отчета из статических строк, записанных в нашей БД
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<ReportModel> prepareReportModelForXlsxDates(Date fromDate, Date toDate) {

        groups = reportManagerMineDAO.getAllGroups("1",27);

        List<ReportModel> reportFrom = reportManagerDAO.getMoveReportForDate(fromDate);
        List<ReportModel> reportTo = reportManagerDAO.getMoveReportForDate(toDate);

        // TODO: Вставить обработку того, что данные есть за эти даты
        List<ReportModel> reportSum = new ArrayList<>();

        for (ReportModel modelFrom : reportFrom) {
            for (ReportModel modelTo : reportTo) {
                if (modelFrom.getGroupName().equals(modelTo.getGroupName())) {
                    ReportModel sumItem = new ReportModel();

                    sumItem.setGroupName(modelFrom.getGroupName());

                    for (GroupModel group : groups) {
                        if(group.getGroupname().equals(modelFrom.getGroupName()))
                        {
                            sumItem.setGroup(group);
                            break;
                        }
                    }

                    sumItem.setCountOfBudgetOld(modelFrom.getCountOfBudget());
                    sumItem.setCountOfUnBudgetOld(modelFrom.getCountOfUnBudget());
                    sumItem.setCountOfBudgetAcademicOld(modelFrom.getCountOfBudgetAcademic());
                    sumItem.setCountOfUnBudgetAcademicOld(modelFrom.getCountOfUnBudgetAcademic());

                    sumItem.setCountOfBudget(modelTo.getCountOfBudget());
                    sumItem.setCountOfUnBudget(modelTo.getCountOfUnBudget());
                    sumItem.setCountOfBudgetAcademic(modelTo.getCountOfBudgetAcademic());
                    sumItem.setCountOfUnBudgetAcademic(modelTo.getCountOfUnBudgetAcademic());

                    //Берем фактические значения движения из статических данных второй даты
                    sumItem.setCountOfBudgetMoveIn(modelTo.getCountOfBudgetMoveIn());
                    sumItem.setCountOfBudgetMoveOut(modelTo.getCountOfBudgetMoveOut());
                    sumItem.setCountOfUnBudgetMoveIn(modelTo.getCountOfUnBudgetMoveIn());
                    sumItem.setCountOfUnBudgetMoveOut(modelTo.getCountOfUnBudgetMoveOut());

                    //TODO: добавить разделение академиков

                    if (modelTo.getCountOfBudget()>modelFrom.getCountOfBudget()) {
                        sumItem.setCountOfBudgetMoveIn(modelTo.getCountOfBudget()-modelFrom.getCountOfBudget());
                    }else{
                        sumItem.setCountOfBudgetMoveOut(modelFrom.getCountOfBudget()-modelTo.getCountOfBudget());
                    }

                    if (modelTo.getCountOfUnBudget()>modelFrom.getCountOfUnBudget()) {
                        sumItem.setCountOfUnBudgetMoveIn(modelTo.getCountOfUnBudget()-modelFrom.getCountOfUnBudget());
                    }else{
                        sumItem.setCountOfUnBudgetMoveOut(modelFrom.getCountOfUnBudget()-modelTo.getCountOfUnBudget());
                    }

                    reportSum.add(sumItem);
                }
            }
        }
        return reportSum;
    }

    /**
     * Генерация данных для динамических данных полученных из Шахт
     * @param reportModels
     * @return
     */
    public List<ReportModel> prepareReportModelForXlsx(List<ReportModel> reportModels) {
        for (ReportModel reportModel : reportModels) {
            //TODO: вставить реальные формулы расчета предыдущих значений
            Integer budgetOld = reportModel.getCountOfBudget();
            budgetOld -= reportModel.getCountOfBudgetMoveIn();
            budgetOld += reportModel.getCountOfBudgetMoveOut();
            reportModel.setCountOfBudgetOld(budgetOld);

            Integer unBudgetOld = reportModel.getCountOfUnBudget();
            unBudgetOld -= reportModel.getCountOfUnBudgetMoveIn();
            unBudgetOld += reportModel.getCountOfUnBudgetMoveOut();
            reportModel.setCountOfUnBudgetOld(unBudgetOld);

            Integer budgetAcademicOld = reportModel.getCountOfBudgetAcademic();
            budgetAcademicOld -= reportModel.getCountOfBudgetAcademicMoveIn();
            budgetAcademicOld += reportModel.getCountOfBudgetAcademicMoveOut();
            reportModel.setCountOfBudgetAcademicOld(budgetAcademicOld);

            Integer unBudgetAcademicOld = reportModel.getCountOfUnBudgetAcademic();
            unBudgetAcademicOld -= reportModel.getCountOfUnBudgetAcademicMoveIn();
            unBudgetAcademicOld += reportModel.getCountOfUnBudgetAcademicMoveOut();
            reportModel.setCountOfUnBudgetAcademicOld(unBudgetAcademicOld);
        }
        return reportModels;
    }

    public void resetTotal() {
        this.totalBudget = 0;
        this.totalBudgetOld = 0;
        this.totalUnBudget = 0;
        this.totalUnBudgetOld = 0;
        this.totalBudgetAcademic = 0;
        this.totalBudgetAcademicOld = 0;
        this.totalUnBudgetAcademic = 0;
        this.totalUnBudgetAcademicOld = 0;
        this.totalBudgetMoveIn = 0;
        this.totalUnBudgetMoveIn = 0;
        this.totalBudgetMoveOut = 0;
        this.totalUnBudgetMoveOut = 0;
        this.totalBudgetAcademicMoveOut = 0;
        this.totalUnBudgetAcademicMoveOut = 0;
    }
}
