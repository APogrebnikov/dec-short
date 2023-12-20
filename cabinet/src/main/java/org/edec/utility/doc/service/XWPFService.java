package org.edec.utility.doc.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.edec.utility.doc.model.ParagraphData;
import org.edec.utility.doc.model.RunData;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * Сервис для создания word документа на основе apache poi
 */
public class XWPFService {

    /**
     * Инициализация начальных параметров документа
     *
     * @return
     */
    public XWPFDocument init() {
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
        pageMar.setTop(BigInteger.valueOf(720L));
        pageMar.setRight(BigInteger.valueOf(720L));
        pageMar.setBottom(BigInteger.valueOf(1440L));

        return document;
    }

    /**
     * Создание параграфа
     *
     * @param document документ для создания
     * @param paragraphData данные для параграфа
     * @param addBreak дополнительный отступ после параграфа
     */
    public void createParagraph(XWPFDocument document, ParagraphData paragraphData, boolean addBreak) {
        XWPFParagraph paragraph = document.createParagraph();

        paragraph.setAlignment(paragraphData.getParagraphAlignment());

        paragraphData.getRunDataList().forEach(runData -> createRun(paragraph, runData));

        switch (paragraphData.getIndentLevel()){
            case 1:
                paragraph.setIndentationFirstLine(720);
                break;
            case 5:
                paragraph.setIndentationFirstLine(2160);
                break;
            default:
                break;
        }

        if (addBreak) {
            paragraph.createRun().addBreak();
        }

        if(!paragraphData.getStyle().isEmpty()){
            paragraph.setStyle(paragraphData.getStyle());
        }
    }

    /**
     * Создание текста
     *
     * @param paragraph параграф для создания
     * @param runData данные для текста
     */
    private void createRun(XWPFParagraph paragraph, RunData runData) {
        XWPFRun run = paragraph.createRun();

        run.setFontFamily(runData.getFontFamily());
        run.setFontSize(runData.getFontSize());
        run.setText(runData.getText());
        run.setBold(runData.getIsBold());
        run.setItalic(runData.getIsItalic());

        if (runData.getIsUnderline()) {
            run.setUnderline(UnderlinePatterns.SINGLE);
        }

        if (runData.getAddTab()) {
            run.addTab();
            run.setText(runData.getAdditionalText());
        }
    }

    public void addPageNumeration(XWPFDocument document) {
        // create header-footer
        XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
        if (headerFooterPolicy == null) {
            headerFooterPolicy = document.createHeaderFooterPolicy();
        }

        // create footer start
        XWPFFooter footer = null;
        footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);

        XWPFParagraph paragraph = footer.getParagraphArray(0);

        if (paragraph == null) {
            paragraph = footer.createParagraph();
        }
        paragraph.setAlignment(ParagraphAlignment.RIGHT);

        paragraph.createRun();

        paragraph.getCTP().addNewFldSimple().setInstr("PAGE \\* MERGEFORMAT");
    }

    public void createDividedParagraph(XWPFDocument document, String leftContent, String rightContent){
        List<String> content = Arrays.asList(leftContent, "", rightContent);
        int[] cols = {6000, 6000, 3000};

        XWPFTable table = document.createTable(1, 3);

        table.getCTTbl().getTblPr().unsetTblBorders();

        for(int i = 0; i < table.getNumberOfRows(); i++){
            XWPFTableRow row = table.getRow(i);

            int numCells = row.getTableCells().size();
            for(int j = 0; j < numCells; j++){
                XWPFTableCell cell = row.getCell(j);
                cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(cols[j]));
                XWPFParagraph paragraph = cell.addParagraph();
                paragraph.setAlignment(ParagraphAlignment.LEFT);
                createRun(paragraph, RunData.builder().text(content.get(j)).build());
            }
        }
    }

    public void createBreak(XWPFDocument document,int breakAmount){
        for(int i = 0; i <breakAmount;i++){
            createBreak(document);
        }
    }

    public void createBreak(XWPFDocument document){
        document.createParagraph().createRun().addBreak();
    }

    public void addWordFooter(XWPFDocument document, CTBody body, String clientDate,
                               String graphName, long TabWidth) throws IOException, InvalidFormatException {

        CTSectPr sectPr = body.getSectPr();
        if(sectPr==null)
        {
            sectPr = body.addNewSectPr();
        }


        CTP footerCtp = CTP.Factory.newInstance();
        CTR footerCtr = footerCtp.addNewR();
        XWPFParagraph footerCopyrightParagraph = new XWPFParagraph(footerCtp, document);
        document.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
        XWPFRun run = footerCopyrightParagraph.getRun(footerCtr);
        run.setText(graphName);
        run.addTab();
        run.setText(clientDate);
        setTabStop(footerCtp, STTabJc.Enum.forString("right"), BigInteger.valueOf(TabWidth));

        XWPFParagraph[] footerParagraphs = { footerCopyrightParagraph };

        XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(document, sectPr);
        headerFooterPolicy.createFooter(STHdrFtr.DEFAULT, footerParagraphs);
    }

    private  void setTabStop(CTP oCTP, STTabJc.Enum oSTTabJc, BigInteger oPos) {
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
}
