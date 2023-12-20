package org.edec.report.protocolResit.service;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.edec.contingentMovement.model.ResitRatingModel;
import org.edec.contingentMovement.report.model.ProtocolCommissionModel;
import org.edec.utility.doc.model.ParagraphData;
import org.edec.utility.doc.model.table.RowData;
import org.edec.utility.doc.model.RunData;
import org.edec.utility.doc.model.table.TableData;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProtocolResitService {

    public XWPFDocument createProtocol(ProtocolCommissionModel protocolCommissionModel) {
        XWPFDocument protocolResit = init();

        createParagraph(protocolResit, new ParagraphData(ParagraphAlignment.CENTER, Collections
                .singletonList(RunData.builder().text("Протокол № " + (protocolCommissionModel.getNumberProtocol() != null ? protocolCommissionModel.getNumberProtocol() : "")).build())), true);

        createParagraph(protocolResit, new ParagraphData(ParagraphAlignment.CENTER, Collections
                .singletonList(RunData.builder().text("заседания аттестационной комиссии от " + (protocolCommissionModel.getDateCommission() != null ? new SimpleDateFormat("dd.MM.yyyy").format(protocolCommissionModel.getDateCommission()):"")).build())),true);

        createParagraph(protocolResit, new ParagraphData(ParagraphAlignment.CENTER, Collections
                .singletonList(RunData.builder()
                                      .text("Повестка дня:")
                                      .isBold(true)
                                      .build())), true);

        createParagraph(protocolResit, new ParagraphData(ParagraphAlignment.BOTH, Collections
                .singletonList(RunData.builder().text(protocolCommissionModel.getAgenda()).build())), true);

        createBreak(protocolResit);

        createParagraph(protocolResit, new ParagraphData(ParagraphAlignment.LEFT, Collections
                .singletonList(RunData.builder().text("Постановили:").build())), true);

        if(!protocolCommissionModel.getResitSubjects().isEmpty()){
            createParagraph(protocolResit, new ParagraphData(ParagraphAlignment.LEFT,
                                                             Collections.singletonList(RunData.builder().text("1. Перезачесть " + protocolCommissionModel.getFioStudent() + " следующие учебные дисциплины, практики:").build())), true);

            createTable(protocolResit, transformToTableData(protocolCommissionModel.getResitSubjects(), Arrays.asList(
                    "№ п/п",
                    "Блок",
                    "Наименование учебных предметов, курсов, дисциплин (модулей), практики",
                    "Семестр",
                    "Трудоемкость (ЗЕ)",
                    "Форма контроля"
            )));
        }

        createBreak(protocolResit);

        if(protocolCommissionModel.getResitSubjectSecondParagraph()!=null && !protocolCommissionModel.getResitSubjectSecondParagraph().isEmpty()){
            //для второго и третьего пунктов
            createParagraph(protocolResit, new ParagraphData(ParagraphAlignment.LEFT,
                                                             Collections.singletonList(RunData.builder().text("2. На основании анализа представленных документов, определить следующий перечень и объем\n" +
                                                                                                                            "учебных дисциплин, практик, подлежащих аттестации:").build()))
                                                                     , true);

            createTable(protocolResit, transformToTableData(protocolCommissionModel.getResitSubjectSecondParagraph()
                    , Arrays.asList(
                            "№ п/п",
                            "Блок",
                            "Наименование учебных предметов, курсов, дисциплин (модулей), практики",
                            "Семестр",
                            "Трудоемкость (ЗЕ)",
                            "Форма аттестации")));

            createBreak(protocolResit);

            createParagraph(protocolResit, new ParagraphData(ParagraphAlignment.LEFT,
                                                             Collections.singletonList(RunData.builder().text("3. Установить срок прохождения аттестации до " + (protocolCommissionModel.getDateResit()!= null ? new SimpleDateFormat("dd.MM.yyyy").format(protocolCommissionModel.getDateResit()): "")).build())), true);

        }

        createBreak(protocolResit);

        createCompositeParagraphChairMan(protocolResit, protocolCommissionModel.getChairman());

        createBreak(protocolResit);

        if(protocolCommissionModel.getСommissionMembers() != null) {
            for (int i = 0;i<protocolCommissionModel.getСommissionMembers().size();i++){
                createCompositeParagraphComissionMember(protocolResit, protocolCommissionModel.getСommissionMembers().get(i), i);
                createBreak(protocolResit);
            }
        }

        return protocolResit;
    }

    private TableData transformToTableData(List<ResitRatingModel> modelList, List<String> headers){

        List<RowData> rowDataList = new ArrayList<>();

        rowDataList.add(transformHeader(headers));

        int counter = 1;

        for (ResitRatingModel model : modelList) {
            model.setNumber(counter++);
            rowDataList.add(transformRowData(model));
        }

        //return new TableData(rowDataList, Arrays.asList(1440, 1440, 1440, 1440, 1440));
        return new TableData(rowDataList, Arrays.asList(1190, 4165, 1190, 1785, 1785));

    }

    private RowData transformHeader(List<String> headers){
        List<ParagraphData> paragraphDataList = new ArrayList<>();

        headers.forEach(header->paragraphDataList.add(new ParagraphData(ParagraphAlignment.CENTER, Collections.singletonList(RunData.builder().text(header).build()))));

        return new RowData(paragraphDataList, true);
    }

    private RowData transformRowData(ResitRatingModel model){
        List<ParagraphData> paragraphDataList = new ArrayList<>();

        paragraphDataList.add(new ParagraphData(ParagraphAlignment.CENTER, Collections.singletonList(RunData.builder().text(model.getNumber().toString()).build())));
        paragraphDataList.add(new ParagraphData(ParagraphAlignment.LEFT, Collections.singletonList(RunData.builder().text(model.getSubjectcode() !=null ? model.getSubjectcode() : "" ).build())));
        paragraphDataList.add(new ParagraphData(ParagraphAlignment.LEFT, Collections
                .singletonList(RunData.builder().text(model.getSubjectname()).build())));
        paragraphDataList.add(new ParagraphData(ParagraphAlignment.CENTER, Collections.singletonList(RunData.builder().text(model.getSemesternumber().toString()).build())));
        paragraphDataList.add(new ParagraphData(ParagraphAlignment.CENTER, Collections.singletonList(RunData.builder().text((model.getFoc().equals("КП") || model.getFoc().equals("КР") ? (model.getResitRating()!=null ? model.getFoc() :"-") : convertHours(model.getSubjectname(), model.getHoursCount()))).build())));
        paragraphDataList.add(new ParagraphData(ParagraphAlignment.CENTER, Collections.singletonList(RunData.builder().text(model.getRating()==null ? model.getFoc() : model.getStrRatingFull()).build())));

        return new RowData(paragraphDataList, false);
    }

    private String convertHours(String subjectname, Double hoursCount){
        return subjectname.toLowerCase().contains("прикладн")
                        && subjectname.toLowerCase().contains("физическ")
                                           && subjectname.toLowerCase().contains("культур")
                                                              ? "-"
                                                              : (hoursCount % 36.0 == 0.0 ? Integer.toString((int)(hoursCount / 36.0)) : String.format("%.1f", hoursCount / 36.0));
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
        pageMar.setTop(BigInteger.valueOf(1440L));
        pageMar.setRight(BigInteger.valueOf(720L));
        pageMar.setBottom(BigInteger.valueOf(1440L));

        return document;
    }

    private void createParagraph(XWPFDocument document, ParagraphData paragraphData, boolean addBreak) {
        XWPFParagraph paragraph = document.createParagraph();

        paragraph.setAlignment(paragraphData.getParagraphAlignment());

        paragraphData.getRunDataList().forEach(runData -> createRun(paragraph, runData));

        if(addBreak) paragraph.createRun().addBreak();
    }

    private void createParagraph(XWPFDocument document, ParagraphData paragraphData, boolean addBreak, int position) {
        XWPFParagraph paragraph = document.createParagraph();

        paragraph.setAlignment(paragraphData.getParagraphAlignment());

        paragraphData.getRunDataList().forEach(runData -> createRun(paragraph, runData));

        if(addBreak) paragraph.createRun().addBreak();

        if(position == 1){
            BigInteger pos1 = BigInteger.valueOf(4500);
            setTabStop(paragraph, STTabJc.Enum.forString("center"), pos1);
        }else if(position == 2){
            BigInteger pos2 = BigInteger.valueOf(9000);
            setTabStop(paragraph, STTabJc.Enum.forString("right"), pos2);
        }
    }

    private void createCompositeParagraphChairMan(XWPFDocument document, String fio){
        XWPFParagraph paragraph = document.createParagraph();

        XWPFRun tmpRun = paragraph.createRun();
        tmpRun.setFontFamily("Times New Roman");
        tmpRun.setFontSize(12);
        tmpRun.setText("Председатель комиссии:");
        tmpRun.addTab();
        tmpRun.setText("_________    " + fio);

        BigInteger pos2 = BigInteger.valueOf(10500);
        setTabStop(paragraph, STTabJc.Enum.forString("right"), pos2);
    }

    private void createCompositeParagraphComissionMember(XWPFDocument document, String fio, int pos){
        XWPFParagraph paragraph = document.createParagraph();

        XWPFRun tmpRun = paragraph.createRun();
        tmpRun.setFontFamily("Times New Roman");
        tmpRun.setFontSize(12);
        if(pos==0) tmpRun.setText("Члены комиссии:");
        tmpRun.addTab();
        tmpRun.setText("_________    " + fio);

        BigInteger pos2 = BigInteger.valueOf(10500);
        setTabStop(paragraph, STTabJc.Enum.forString("right"), pos2);
    }

    public static void setTabStop(XWPFParagraph oParagraph, STTabJc.Enum oSTTabJc, BigInteger oPos) {
        CTP oCTP = oParagraph.getCTP();
        CTPPr oPPr = oCTP.getPPr();
        if (oPPr == null) {
            oPPr = oCTP.addNewPPr();
        }

        CTTabs oTabs = oPPr.getTabs();
        if (oTabs == null) {
            oTabs = oPPr.addNewTabs();
        }

        CTTabStop oTabStop = oTabs.addNewTab();
        oTabStop.setVal(oSTTabJc);
        oTabStop.setPos(oPos);
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

    private void createRun(XWPFParagraph paragraph, RunData runData) {
        XWPFRun run = paragraph.createRun();

        run.setFontFamily("Times New Roman");
        run.setFontSize(12);
        run.setText(runData.getText());

        if (runData.getIsBold()) {
            run.setBold(true);
        }
    }

    private void createRun(XWPFParagraph paragraph, RunData runData, int position) {
        XWPFRun run = paragraph.createRun();

        run.setTextPosition(position);

        run.setFontFamily("Times New Roman");
        run.setFontSize(12);
        run.setText(runData.getText());

        if (runData.getIsBold()) {
            run.setBold(true);
        }
    }

    private void createTable(XWPFDocument document, TableData tableData) {
        XWPFTable table = document.createTable();

        table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(595));

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

            for (int i = 0; i < rowData.getContent().size(); i++) {
                createParagraph(row, rowData.getContent().get(i), i == 0, false, i);
                row.getCell(i).removeParagraph(0);
            }
        }

    }

    private void createBreak(XWPFDocument document,int breakAmount){
        for(int i = 0; i <breakAmount;i++){
            createBreak(document);
        }
    }

    private void createBreak(XWPFDocument document){
        document.createParagraph().createRun().addBreak();
    }
}
