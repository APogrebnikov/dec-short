package org.edec.commission.ctrl;


import org.edec.commission.report.model.schedule.ScheduleChairModel;
import org.edec.commission.report.model.schedule.ScheduleSubjectModel;
import org.edec.commission.report.model.schedule.StudentModel;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WinFilterStudentsByChairCtrl extends SelectorComposer<Component> {

    public static final String CHAIRS = "listChairs";
    public static final String DOCX = "is_docx";

    private JasperReportService jasperReportService = new JasperReportService();
    private List<ScheduleChairModel> listChairs;
    private Boolean is_docx;

    @Wire
    Checkbox chNoBudget, chBudget;
    @Wire
    Button btnShowStudentsByChair;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        listChairs = (List<ScheduleChairModel>) Executions.getCurrent().getArg().get(CHAIRS);
        is_docx = (Boolean) Executions.getCurrent().getArg().get(DOCX);

    }

    @Listen("onClick = #btnShowStudentsByChair")
    public void showStudentsByChair() {
        List<ScheduleChairModel> filteredStudentsByChair = new ArrayList<>();

        for (ScheduleChairModel chairModel : listChairs) {
            List<ScheduleSubjectModel> listSubjects = chairModel.getSubjects();
            ScheduleChairModel filteredChair = chairModel.clone();

            for (ScheduleSubjectModel subjectModel : listSubjects) {
                List<StudentModel> listStudent = subjectModel.getStudents();
                ScheduleSubjectModel filteredSubject = subjectModel.clone();

                if (!chBudget.isChecked() && chNoBudget.isChecked()) {
                   listStudent = listStudent.stream().filter(s -> s.getBudget() == 0).collect(Collectors.toList());
                } else if (chBudget.isChecked() && !chNoBudget.isChecked()) {
                    listStudent = listStudent.stream().filter(s -> s.getBudget() == 1).collect(Collectors.toList());
                }

                filteredSubject.setStudents(listStudent);
                if(!filteredSubject.getStudents().isEmpty()) {
                    filteredChair.getSubjects().add(filteredSubject);
                }
            }

            if(!filteredChair.getSubjects().isEmpty()) {
                filteredStudentsByChair.add(filteredChair);
            }
        }

        if (chBudget.isChecked() && chNoBudget.isChecked()) {
            filteredStudentsByChair = listChairs;
        }

        JasperReport report = jasperReportService.getListOfStudentByChair(filteredStudentsByChair);
        if (filteredStudentsByChair == null || filteredStudentsByChair.isEmpty()) {
            PopupUtil.showError("Студенты не найдены.");
        } else if (!chBudget.isChecked() && !chNoBudget.isChecked()) {
            PopupUtil.showError("Выберите основание обучения!");
        } else if (is_docx) {
            report.downloadDocx();
        } else {
            report.showPdf();
        }


    }
}
