package org.edec.chairsRegisters.docReport.service;

import org.apache.poi.xwpf.usermodel.*;
import org.edec.chairsRegisters.model.ChairsRegisterModel;
import org.edec.chairsRegisters.model.GroupBySemRegisterModel;
import org.edec.utility.doc.model.ParagraphData;
import org.edec.utility.doc.model.table.RowData;
import org.edec.utility.doc.model.RunData;
import org.edec.utility.doc.model.table.TableData;
import org.edec.utility.converter.DateConverter;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.math.BigInteger;
import java.util.*;

public class RetakesScheduleService {

    public XWPFDocument createScheduleRetakes(List<GroupBySemRegisterModel> retakesRegisterForReport, String dateFrom, String dateTo) {
        XWPFDocument scheduleRetakes = init();
        createParagraph(scheduleRetakes, new ParagraphData(ParagraphAlignment.LEFT, Collections
                .singletonList(new RunData("Расписание пересдач в период с " + dateFrom + " по " + dateTo ))), false );
        for (GroupBySemRegisterModel model : retakesRegisterForReport) {
            if (retakesRegisterForReport.get(0) != model) {
                createBreak(scheduleRetakes);
            }
            createParagraph(scheduleRetakes, new ParagraphData(ParagraphAlignment.LEFT, Collections
                    .singletonList(new RunData(model.getSem(), true, false))), false );
            createTable(scheduleRetakes, transformToTableData(Arrays.asList("Дисциплина", "Группа", "Преподаватель", "Дата"), model.getListRegister()));
        }



        return scheduleRetakes;
    }
    private XWPFDocument init() {
        XWPFDocument document = new XWPFDocument();

        //Задаем стили
        XWPFStyles styles = document.createStyles();

        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        CTPageSz pageSz = sectPr.addNewPgSz();
        //Задаем размеры А4
        pageSz.setW(BigInteger.valueOf(11900));
        pageSz.setH(BigInteger.valueOf(16840));
        CTPageMar pageMar = sectPr.addNewPgMar();
        //Задаем отступы от края документа
        pageMar.setLeft(BigInteger.valueOf(720L));
        pageMar.setTop(BigInteger.valueOf(800L));
        pageMar.setRight(BigInteger.valueOf(720L));
        pageMar.setBottom(BigInteger.valueOf(1440L));

        return document;
    }

    private void createParagraph(XWPFTableRow row, ParagraphData paragraphData, boolean isFirstCell, boolean isFirstRow, int cellNumber) {
        XWPFParagraph paragraph;

        if (isFirstCell) {
            paragraph = row.getCell(0).addParagraph();
        } else {
            if(isFirstRow) {
                paragraph = row.addNewTableCell().addParagraph();
            }else {
                paragraph = row.getCell(cellNumber).addParagraph();
            }
        }

        paragraph.setAlignment(paragraphData.getParagraphAlignment());

        paragraphData.getRunDataList().forEach(runData -> createRun(paragraph, runData));
    }
    private void createParagraph(XWPFDocument document, ParagraphData paragraphData, boolean addBreak) {
        XWPFParagraph paragraph = document.createParagraph();

        paragraph.setAlignment(paragraphData.getParagraphAlignment());

        paragraphData.getRunDataList().forEach(runData -> createRun(paragraph, runData));

        if(addBreak) paragraph.createRun().addBreak();
    }

    private void createRun(XWPFParagraph paragraph, RunData runData) {
        XWPFRun run = paragraph.createRun();

        run.setFontFamily("Times New Roman");
        run.setFontSize(10);
        run.setText(runData.getText());

        if (runData.getIsBold()) {
            run.setBold(true);
        }
    }


    private void createTable(XWPFDocument document, TableData tableData) {
        XWPFTable table = document.createTable();

       table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(4000));
        CTTblLayoutType type = table.getCTTbl().getTblPr().addNewTblLayout();
        type.setType(STTblLayoutType.FIXED);

        tableData.getColumnSize().forEach(columnSize -> table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(columnSize)));

        tableData.getContent().forEach(content -> createRow(table, content));
    }

    private void createRow(XWPFTable table, RowData rowData) {
        XWPFTableRow row;

        if (rowData.isHeader()) {
            row = table.getRow(0);
            row.setRepeatHeader(true);

            for (int i = 0; i < rowData.getContent().size(); i++) {
                createParagraph(row, rowData.getContent().get(i), i == 0, true, i);
                row.getCell(i).removeParagraph(0);

            }
        } else {
            row = table.createRow();
            row.setCantSplitRow(true);
            for (int i = 0; i < rowData.getContent().size(); i++) {
                createParagraph(row, rowData.getContent().get(i), i == 0, false, i);
                row.getCell(i).removeParagraph(0);
            }
        }

    }
    private TableData transformToTableData(List<String> headers, List<ChairsRegisterModel> modelList){

        List<RowData> rowDataList = new ArrayList<>();

        rowDataList.add(transformHeader(headers));

        for (ChairsRegisterModel model : modelList) {
            rowDataList.add(transformRowData(model));
        }

        //return new TableData(rowDataList, Arrays.asList(1440, 1440, 1440, 1440, 1440));
        return new TableData(rowDataList, Arrays.asList(2000, 3000, 1200));

    }
    private RowData transformRowData(ChairsRegisterModel model){
        List<ParagraphData> paragraphDataList = new ArrayList<>();
        String foc = (model.getRetakeCount() == 2 || model.getRetakeCount() == -2 ? "общ.": "индив.");
        paragraphDataList.add(new ParagraphData(ParagraphAlignment.LEFT, Collections.singletonList(new RunData(model.getSubjectname() + " (" + model.getFoc() +", " +foc+")" ))));
        paragraphDataList.add(new ParagraphData(ParagraphAlignment.CENTER, Collections.singletonList(new RunData(model.getGroupname()))));
        paragraphDataList.add(new ParagraphData(ParagraphAlignment.CENTER, Collections.singletonList(new RunData(model.getFioTeachers()))));
        paragraphDataList.add(new ParagraphData(ParagraphAlignment.LEFT, Collections.singletonList(new RunData(DateConverter.convertDateToStringByFormat(model.getBeginDate(), "dd.MM.yyyy")
                + " - " +DateConverter.convertDateToStringByFormat(model.getEndDate(), "dd.MM.yyyy") ))));

        return new RowData(paragraphDataList, false);
    }
    private RowData transformHeader(List<String> headers){
        List<ParagraphData> paragraphDataList = new ArrayList<>();

        headers.forEach(header->paragraphDataList.add(new ParagraphData(ParagraphAlignment.CENTER, Collections.singletonList(new RunData(header)))));

        return new RowData(paragraphDataList, true);
    }

    private void createBreak(XWPFDocument document){
        document.createParagraph().createRun().addBreak();
    }

}
