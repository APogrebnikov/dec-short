package org.edec.utility.doc.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.edec.newOrder.manager.EditOrderManagerESO;
import org.edec.newOrder.model.report.OrderReportEmployeeModel;
import org.edec.newOrder.report.constant.OrderLineType;
import org.edec.newOrder.report.model.OrderLineModel;
import org.edec.utility.doc.model.ParagraphData;
import org.edec.utility.doc.model.RunData;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocService {

    private final XWPFService xwpfService = new XWPFService();

    private EditOrderManagerESO manager = new EditOrderManagerESO();

    /**
     * Заполнение шаблона docx документа данными
     *
     * @param template шаблон документа
     * @param data данные
     *
     * @return
     */
    public XWPFDocument getDoc(XWPFDocument template, Map<String, Object> data) {
        XWPFDocument doc = xwpfService.init();

        int position = 0;

        for (XWPFParagraph p : template.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);

                    if (text != null) {
                        //Вставляем тело приказа
                        if (text.contains("${content}")) {
                            List<ParagraphData> contentList = (ArrayList) data.get("content");
                            for (ParagraphData paragraph : contentList) {
                                xwpfService.createParagraph(doc, paragraph, false);
                                position++;
                            }
                            r.setText("",0);
                        } else { //Заполняем переменные значениями
                            text = fillVariable(text, data);
                            r.setText(text, 0);
                        }
                    }
                }
            }

            doc.createParagraph();
            doc.setParagraph(p, position++);
        }

        xwpfService.createDividedParagraph(doc, (String) data.get("predicate_post"), (String) data.get("predicate_fio"));
        xwpfService.createBreak(doc, 1);
        xwpfService.createParagraph(doc, new ParagraphData(ParagraphAlignment.LEFT, Arrays.asList(RunData.builder().text("СОГЛАСОВАНО:").build())), true);

        List<OrderReportEmployeeModel> employees = (List<OrderReportEmployeeModel>) data.get("employees");

        employees.forEach(employee-> xwpfService.createDividedParagraph(doc, employee.getRole(), employee.getFio()));

        xwpfService.createBreak(doc, 1);

        xwpfService.createParagraph(doc, new ParagraphData(ParagraphAlignment.LEFT, Arrays.asList(RunData.builder()
                                                                                                         .text((String) data.get("executor_fio"))
                                                                                                         .fontSize(10).build())), false);

        xwpfService.createParagraph(doc, new ParagraphData(ParagraphAlignment.LEFT, Arrays.asList(RunData.builder()
                                                                                                         .text((String) data.get("executor_phone")).fontSize(10).build())), false);

        xwpfService.addPageNumeration(doc);

        return doc;
    }

    public XWPFDocument getNotion(XWPFDocument template, Map<String, Object> data){
        XWPFDocument doc = xwpfService.init();

        int position = 0;

        for (XWPFParagraph p : template.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);

                    if (text != null) {
                        //Вставляем тело приказа
                        if (text.contains("${content}")) {
                            List<ParagraphData> contentList = (ArrayList) data.get("content");
                            for (ParagraphData paragraph : contentList) {
                                xwpfService.createParagraph(doc, paragraph, false);
                                position++;
                            }
                            r.setText("",0);
                        } else { //Заполняем переменные значениями
                            text = fillVariable(text, data);
                            r.setText(text, 0);
                        }
                    }
                }
            }

            doc.createParagraph();
            doc.setParagraph(p, position++);
        }

        xwpfService.createDividedParagraph(doc, "Директор института", "Д. В. Капулин");

        xwpfService.createBreak(doc);

        xwpfService.createParagraph(doc, new ParagraphData(ParagraphAlignment.LEFT, Collections.singletonList(
                RunData.builder()
                       .text("Сомова Марина Валериевна")
                       .fontSize(10).build()), OrderLineType.NO_INDENT.getValue()), false);

        xwpfService.createParagraph(doc, new ParagraphData(ParagraphAlignment.LEFT, Collections.singletonList(
                RunData.builder()
                       .text("т. 2-912-237")
                       .fontSize(10).build()), OrderLineType.NO_INDENT.getValue()), false);

        return doc;
    }

    /**
     * Заменяет найденные в тексте переменные значениями из Map
     * @param text анализируемый текст
     * @param data значения переменных
     * @return обработанный текст
     */
    private String fillVariable(String text, Map<String, Object> data) {

        String regex = ".*\\$\\{.*}.*";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()){
            String replacement = (String) data.get(text.substring(text.indexOf("${") + 2, text.indexOf("}")));
            text = text.replaceFirst("\\$\\{.*}", replacement);

            matcher = pattern.matcher(text);
        }

        return text;
    }

    /**
     * Создание шаблона для приказов
     *
     * @return
     */
    public XWPFDocument getOrderTemplate(Long idOrderRule) {
        //TODO:добавить разделение когда перенесу шаблон для академического приказа
        XWPFDocument template = xwpfService.init();

        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.CENTER, Collections.singletonList(
                RunData.builder()
                       .text("Министерство науки и высшего образования РФ")
                       .isItalic(true).build())), false);

        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.CENTER, Collections.singletonList(
                RunData.builder()
                       .text("Федеральное государственное автономное образовательное учреждение")
                       .isItalic(true).build())), false);

        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.CENTER, Collections
                .singletonList(RunData.builder()
                                      .text("высшего образования")
                                      .isItalic(true).build())), false);

        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.CENTER, Collections
                .singletonList(RunData.builder()
                                      .text("«СИБИРСКИЙ ФЕДЕРАЛЬНЫЙ УНИВЕРСИТЕТ»")
                                      .isItalic(true)
                                      .isBold(true).build())), true);

        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.CENTER, Collections
                .singletonList(RunData.builder()
                                      .text("ПРИКАЗ")
                                      .isItalic(true).build())), true);

        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.LEFT,
                                                                Collections.singletonList(RunData.builder().text("${institute}").build())
        ), false);

        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.LEFT,
                                                                Arrays.asList(RunData.builder().text("${formofstudy}").isUnderline(true).build(),
                                                                              RunData.builder().text("                                       " +
                                                                                                     "                                       ").build(),
                                                                              RunData.builder().text("№________").isUnderline(true).build())),
                                    false);

        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.LEFT,
                                                                Collections.singletonList(RunData.builder().text("${type_order}").build())),
                                    true);

        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.LEFT,
                                                                Collections.singletonList(RunData.builder().text("${content}").build())),
                                    true);

        try {
            xwpfService.addWordFooter(template, CTBody.Factory.newInstance(), "KEK", "MEM", 50);
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }

        return template;
    }

    public XWPFDocument getNotionRectorTemplate(){
        XWPFDocument template = xwpfService.init();

        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.LEFT, Collections.singletonList(
                RunData.builder()
                       .text("ФГАОУ ВО СФУ")
                       .isBold(true)
                       .build())), false);
        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.LEFT, Collections.singletonList(
                RunData.builder()
                       .text("ИНСТИТУТ КОСМИЧЕСКИХ И")
                       .build())), false);
        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.LEFT, Collections.singletonList(
                RunData.builder()
                       .text("ИНФОРМАЦИОННЫХ ТЕХНОЛОГИЙ")
                       .build())), true);
        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.LEFT, Collections.singletonList(
                RunData.builder()
                       .text("СЛУЖЕБНАЯ ЗАПИСКА")
                       .isBold(true)
                       .build())), false);
        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.LEFT, Collections.singletonList(
                RunData.builder()
                       .text("${date} г.")
                       .isBold(true)
                       .build())), true);
        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.LEFT, Collections.singletonList(
                RunData.builder()
                       .text("Об установлении сроков прохождения")
                       .fontSize(11)
                       .build())), false);
        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.LEFT, Collections.singletonList(
                RunData.builder()
                       .text("первой повторной промежуточной аттестации\n")
                       .fontSize(11)
                       .build())), true);
        xwpfService.createBreak(template, 1);
        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.BOTH, Collections.singletonList(
                RunData.builder()
                       .text("Прошу установить срок прохождения первой повторной промежуточной аттестации для ликвидации академических задолженностей, " +
                             "образовавшихся по итогам промежуточной аттестации ${sem} учебного года, до ${first_date} обучающимся, " +
                             "не прошедшим промежуточную аттестацию по неуважительной причине " +
                             "и получившим неудовлетворительные результаты:")
                       .build()), OrderLineType.RED_LINE_INDENT.getValue()), false);

        xwpfService.createParagraph(template, new ParagraphData(ParagraphAlignment.LEFT,
                                                                Collections.singletonList(RunData.builder().text("${content}").build())),
                                    true);

        return template;
    }

    public List<ParagraphData> parseOrderLine(Long idOrder){

        List<OrderLineModel> list = manager.getOrderLines(idOrder);
        List<ParagraphData> content = new ArrayList<>();

        list.forEach(line->{
            ParagraphData paragraphData = new ParagraphData();

            switch (OrderLineType.getLineTypeByValue(line.getLineType())){
                case RED_LINE_INDENT:
                    paragraphData.setIndentLevel(OrderLineType.RED_LINE_INDENT.getValue());
                    break;
                case EMPTY:
                    line.setLineInfo("");
                case CENTERED:
                    paragraphData.setParagraphAlignment(ParagraphAlignment.CENTER);
                    break;
                case CENTERED_LIST:
                    paragraphData.setIndentLevel(OrderLineType.CENTERED_LIST.getValue());
                    break;
            }

            List<RunData> runDataList = checkBoldTags(line.getLineInfo());

            paragraphData.setRunDataList(runDataList);

            content.add(paragraphData);
        });

        return content;
    }

    private List<RunData> checkBoldTags(String lineInfo){
        List<RunData> result = new ArrayList<>();
        while(lineInfo.contains("<b>") && lineInfo.contains("</b>")){
            result.add(RunData.builder().text(lineInfo.substring(0, lineInfo.indexOf("<b>"))).build());
            result.add(RunData.builder()
                    .text(lineInfo.substring(lineInfo.indexOf("<b>") + 3,lineInfo.indexOf("</b>")))
                    .isBold(true).build());
            lineInfo = lineInfo.substring(lineInfo.indexOf("</b>") + 4);
        }

        result.add(RunData.builder().text(lineInfo).build());

        return result;
    }
}
