package org.edec.report.сomissionReport;

import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.edec.commission.report.model.schedule.CourseModel;
import org.edec.commission.report.model.schedule.FormOfStudyModel;
import org.edec.commission.report.model.schedule.GroupModel;
import org.edec.commission.report.model.schedule.StudentModel;
import org.edec.utility.doc.model.ParagraphData;
import org.edec.utility.doc.model.RunData;
import org.edec.utility.converter.DateConverter;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StudentListByFocService {

    private XWPFDocument init() {
        XWPFDocument document = new XWPFDocument();

        //Задаем стили
        XWPFStyles styles = document.createStyles();
        CTFonts fonts = CTFonts.Factory.newInstance();
        fonts.setHAnsi("Times New Roman");

        styles.setDefaultFonts(fonts);
        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();

        CTPageSz pageSz = sectPr.addNewPgSz();
        //Задаем размеры А4
        pageSz.setW(BigInteger.valueOf(11900));
        pageSz.setH(BigInteger.valueOf(16840));
        CTPageMar pageMar = sectPr.addNewPgMar();
        //Задаем отступы от края документа
        pageMar.setLeft(BigInteger.valueOf(400L));
        pageMar.setTop(BigInteger.valueOf(440L));
        pageMar.setRight(BigInteger.valueOf(720L));
        pageMar.setBottom(BigInteger.valueOf(1440L));

        // задаем футер с нумерацией страниц
        CTP ctp = CTP.Factory.newInstance();
        ctp.addNewR().addNewPgNum();

        XWPFParagraph codePara = new XWPFParagraph(ctp, document);
        XWPFParagraph[] paragraphs = new XWPFParagraph[1];
        paragraphs[0] = codePara;

        codePara.setAlignment(ParagraphAlignment.RIGHT);

        XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(document, sectPr);
        headerFooterPolicy.createFooter(STHdrFtr.DEFAULT, paragraphs);

        return document;
    }

    public XWPFDocument createDocReport(List<FormOfStudyModel> listModel, Date dateOfBegin, Date dateOfEnd) {
        XWPFDocument document = init();

        createParagraph(document, new ParagraphData(ParagraphAlignment.LEFT, Collections
                .singletonList(new RunData("Cписок студентов, отправленных на комиссию c "
                        + DateConverter.convertDateToStringByFormat(dateOfBegin, "dd.MM.yyyy") + "г. по "
                        + DateConverter.convertDateToStringByFormat(dateOfEnd, "dd.MM.yyyy") + "г."))), true);

        for (FormOfStudyModel formOfStudyModel : listModel) {
            createParagraph(document, new ParagraphData(ParagraphAlignment.LEFT, Collections
                    .singletonList(new RunData(formOfStudyModel.getFormOfStudy(), true, true))), false);
            for (CourseModel courseModel : formOfStudyModel.getCourses()) {
                createParagraph(document, new ParagraphData(ParagraphAlignment.LEFT, Collections
                        .singletonList(new RunData(courseModel.getCourse().toString() + " курс", true, false))), false);
                for (GroupModel groupModel : courseModel.getGroups()) {
                    createParagraph(document, new ParagraphData(ParagraphAlignment.LEFT, Collections
                            .singletonList(new RunData("Группа " + groupModel.getGroupname()))), false);
                    int i = 1;
                    for (StudentModel studentModel : groupModel.getStudents()) {
                        createParagraph(document, new ParagraphData(ParagraphAlignment.LEFT, Collections
                                .singletonList(new RunData("       " + i++ + ". (#" + studentModel.getRecordbook() + ") " + studentModel.getFio()))), false);
                    }
                }
            }
            createBreak(document);
            createLastParagraph(document, new ParagraphData(ParagraphAlignment.CENTER, Collections
                    .singletonList(new RunData("В случае получения неудовлетворительной оценки или неявки, вы будете отчислены!", true, false))), true);
        }

        return document;
    }

    private void createParagraph(XWPFDocument document, ParagraphData paragraphData, boolean addBreak) {
        XWPFParagraph paragraph = document.createParagraph();

        paragraph.setAlignment(paragraphData.getParagraphAlignment());

        paragraphData.getRunDataList().forEach(runData -> createRun(paragraph, runData));
        paragraph.setSpacingAfter(50);

        if (addBreak) paragraph.createRun().addBreak();
    }

    private void createRun(XWPFParagraph paragraph, RunData runData) {
        XWPFRun run = paragraph.createRun();

        run.setFontFamily("Times New Roman");
        run.setFontSize(10);
        run.setText(runData.getText());

        if (runData.getIsBold()) {
            run.setBold(true);
        }
        if (runData.getIsUnderline()) {
            run.setUnderline(UnderlinePatterns.SINGLE);
        }
    }

    private void createLastParagraph(XWPFDocument document, ParagraphData paragraphData, boolean addBreak) {
        XWPFParagraph paragraph = document.createParagraph();

        paragraph.setAlignment(paragraphData.getParagraphAlignment());

        paragraphData.getRunDataList().forEach(runData -> createLastRun(paragraph, runData));
        paragraph.setSpacingAfter(50);

        if (addBreak) paragraph.createRun().addBreak();
    }

    private void createLastRun(XWPFParagraph paragraph, RunData runData) {
        XWPFRun run = paragraph.createRun();

        run.setFontFamily("Times New Roman");
        run.setFontSize(12);
        run.setText(runData.getText());

        if (runData.getIsBold()) {
            run.setBold(true);
        }
        if (runData.getIsUnderline()) {
            run.setUnderline(UnderlinePatterns.SINGLE);
        }
    }

    private void createBreak(XWPFDocument document) {
        document.createParagraph().createRun().addBreak();
    }

}
