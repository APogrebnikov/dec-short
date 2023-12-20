package org.edec.utility.pdfViewer.ctrl;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.apache.log4j.Logger;
import org.edec.newOrder.report.OrderLineService;
import org.edec.newOrder.report.constant.OrderLineType;
import org.edec.newOrder.report.model.OrderLineModel;
import org.edec.newOrder.report.service.OrderReportFillService;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PdfLineEditorCtrl extends SelectorComposer<Component> {

    public static final String ARG = "arg";
    public static final String BEAN_DATA = "bean_data";
    public static final String FILE_NAME = "file_name";
    public static final String JASPER_FILE = "jasper_file";

    public static final Logger log = Logger.getLogger(PdfLineEditorCtrl.class.getName());

    @Wire
    private Iframe iframeReport;

    @Wire
    private Textbox tbEditor;

    @Wire
    private Window winReportEditor;

    private byte[] buffer = null;
    private String jasperFile;
    private String fileName;
    private Map arg;
    private Object beanData;

    private OrderReportFillService orderReportFillService = new OrderReportFillService();
    private OrderLineService orderLineService = new OrderLineService();

    private List<OrderLineModel> lines;
    private Long idOrder;
    private Long idOrderRule;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        jasperFile = (String) Executions.getCurrent().getArg().get(JASPER_FILE);
        fileName = (String) Executions.getCurrent().getArg().get(FILE_NAME);
        arg = (Map) Executions.getCurrent().getArg().get(ARG);
        beanData = Executions.getCurrent().getArg().get(BEAN_DATA);

        idOrder = Long.parseLong(arg.get("idOrder").toString());
        idOrderRule = Long.parseLong(arg.get("idOrderRule").toString());

        generateJasper(jasperFile, fileName, arg, beanData, "pdf");

        lines = orderLineService.getOrderLines(idOrder);

        tbEditor.setValue(getTextFromLines(lines));
    }

    private void generateJasper(String jasperFile, String fileName, Map arg, Object beanData, String type) {
        try {
            if (beanData != null) {
                if (beanData instanceof JRBeanCollectionDataSource) {
                    buffer = JasperRunManager.runReportToPdf(jasperFile, arg, (JRBeanCollectionDataSource) beanData);
                } else if (beanData instanceof JRMapCollectionDataSource) {
                    buffer = JasperRunManager.runReportToPdf(jasperFile, arg, (JRMapCollectionDataSource) beanData);
                }
            } else {
                buffer = JasperRunManager.runReportToPdf(jasperFile, arg, new JREmptyDataSource());
            }

            ByteArrayInputStream is = new ByteArrayInputStream(buffer);
            AMedia amedia = new AMedia(fileName + "." + type, type, "application/" + type, is);
            iframeReport.setContent(amedia);
            iframeReport.setHflex("1");
            iframeReport.setVflex("1");
        } catch (JRException e) {
            e.printStackTrace();
            PopupUtil.showError("Проблемы с отображением документа, обратитесь к администратору!");
            winReportEditor.detach();
        }
    }

    //Получаем строку из списка линий для отображения в текстбоксе
    private String getTextFromLines(List<OrderLineModel> lines) {

        StringBuilder orderTextBuilder = new StringBuilder();

        for (OrderLineModel line : lines) {

            orderTextBuilder.append((line.getLineType().equals(OrderLineType.EMPTY.getValue()) ? "" : line.getLineInfo()));
            orderTextBuilder.append("\n");
        }

        return orderTextBuilder.toString();
    }

    //Получаем список линий из строки текстбокса
    private List<OrderLineModel> getLinesFromText(String text) {
        List<OrderLineModel> lines = new ArrayList<>();

        String[] arrLines = text.split("\n", -1);

        int lineNumber = 1;
        for(String arrLine : arrLines){
            OrderLineModel newLine = new OrderLineModel();

            newLine.setLineInfo(arrLine);
            newLine.setLineNumber(lineNumber);

            lines.add(newLine);

            lineNumber++;
        }

        return lines;
    }

    @Listen("onClick = #btnRefreshView")
    public void updatePdfView() {

        lines = mergeLines(lines, getLinesFromText(tbEditor.getText()));

        JRBeanCollectionDataSource updatedBeanData = orderReportFillService.getBeanData(idOrder, idOrderRule, lines);

        generateJasper(jasperFile, fileName, arg, updatedBeanData, "pdf");
    }

    @Listen("onClick = #btnSaveLines")
    public void saveLines() {
        updatePdfView();

        int lineNumber = 1;
        for(OrderLineModel line : lines){

            line.setLineNumber(lineNumber);
            lineNumber++;
        }

        orderLineService.deleteOrderLines(idOrder);
        orderLineService.saveOrderLines(idOrder, lines);
    }

    private List<OrderLineModel> mergeLines(List<OrderLineModel> oldLines, List<OrderLineModel> newLines) {

        //Удаляем пустые строки из pdf
        oldLines = oldLines.stream().filter(orderLineModel ->
                                                    !orderLineModel.getLineType().equals(OrderLineType.EMPTY.getValue()))
                           .collect(Collectors.toList());

        //Забираем номера пустых строк из текстового поля
        List<Integer> numbers = new ArrayList<>();

        for (OrderLineModel line : newLines) {
            if (line.getLineInfo().equals("")) {
                numbers.add(new Integer(line.getLineNumber()));
            }
        }

        //вставляем в pdf строки по номерам и сохраняем

        for (Integer number : numbers) {
            oldLines.add(number - 1, new OrderLineModel("", OrderLineType.EMPTY.getValue()));
        }

        return oldLines;
    }
}
