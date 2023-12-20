package org.edec.commission.ctrl;

import org.edec.commission.report.model.notion.NotionStudentModel;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class WinSelectStudentForNotion extends SelectorComposer<Component> {

    public static final String STUDENTS = "students";
    public static final String IS_DOCX = "idDocx";

    @Wire
    private Datebox dbDateNotion, dbExamination;
    @Wire
    private Listbox lbStudents;
    @Wire
    private Textbox tbExecutorFio, tbExecutorTel;

    private JasperReportService jasperReportService = new JasperReportService();

    private Boolean isDocx;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        dbDateNotion.setValue(new Date());

        List<NotionStudentModel> notionStudents = (List<NotionStudentModel>) Executions.getCurrent().getArg().get(STUDENTS);
        isDocx = (Boolean) Executions.getCurrent().getArg().get(IS_DOCX);
        lbStudents.setItemRenderer((Listitem listitem, NotionStudentModel notionModel, int i) -> {
            new Listcell(notionModel.getStudentFio()).setParent(listitem);
            new Listcell(notionModel.getGroupName()).setParent(listitem);
            listitem.setValue(notionModel);
        });

        ListModelList<NotionStudentModel> lmNotion = new ListModelList<>(notionStudents);
        lmNotion.setMultiple(true);
        lbStudents.setModel(lmNotion);
    }

    @Listen("onClick = #btnPrint")
    public void onClickPrint() {

        if (lbStudents.getSelectedCount() == 0) {
            PopupUtil.showWarning("Выберите студентов для создания представления");
            return;
        }
        List<NotionStudentModel> selectedNotionStudents = lbStudents.getSelectedItems().stream()
                .map(li -> ((NotionStudentModel) li.getValue()))
                .collect(Collectors.toList());
        printReport(selectedNotionStudents);
    }

    private void printReport(List<NotionStudentModel> listToPrint) {

        JasperReport jasperReport = jasperReportService.printReportNewNotionDirector(
                dbDateNotion.getValue(), dbExamination.getValue(),
                tbExecutorFio.getValue(), tbExecutorTel.getValue(), listToPrint);

        if (isDocx) {
            jasperReport.downloadDocx();
        } else {
            jasperReport.showPdf();
        }
    }
}
