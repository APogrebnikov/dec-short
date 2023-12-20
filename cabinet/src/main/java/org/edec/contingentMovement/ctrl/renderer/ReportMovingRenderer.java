package org.edec.contingentMovement.ctrl.renderer;

import org.edec.contingentMovement.model.ReportModel;
import org.edec.contingentMovement.model.StudentMoveModel;
import org.edec.contingentMovement.service.impl.ReportService;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.*;

import java.util.List;

public class ReportMovingRenderer implements ListitemRenderer<ReportModel> {
    public Listfoot currentFooter;

    public ReportService reportService;

    public ReportMovingRenderer(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public void render (Listitem li, ReportModel data, int index) throws Exception {
        li.setValue(data);

        Listcell lcGroup = new Listcell();
        lcGroup.setLabel(data.getGroupName());
        lcGroup.setParent(li);

        Listcell lcB = new Listcell();
        //String budLabel = "";
        //budLabel += "("+(data.getBudgetStudents().size()-data.getBudgetWithMove().size())+")";
        lcB.setLabel(String.valueOf(data.getBudgetStudents().size()));
        lcB.setParent(li);
        final List<StudentMoveModel> budgetStudents = data.getBudgetStudents();
        lcB.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(budgetStudents);
        });
        this.reportService.totalBudgetOld += data.getBudgetWithMove().size();
        this.reportService.totalBudget += data.getBudgetStudents().size();

        Listcell lcD = new Listcell();
        //String unbudLabel = "";
        //unbudLabel += "("+(data.getUnBudgetStudents().size()-data.getUnBudgetWithMove().size())+")";
        lcD.setLabel(String.valueOf(data.getUnBudgetStudents().size()));
        lcD.setParent(li);
        final List<StudentMoveModel> unBudgetStudents = data.getUnBudgetStudents();
        lcD.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(unBudgetStudents);
        });
        this.reportService.totalUnBudgetOld += data.getUnBudgetWithMove().size();
        this.reportService.totalUnBudget += data.getUnBudgetStudents().size();

        Listcell lcBA = new Listcell();
        lcBA.setLabel(String.valueOf(data.getBudgetAcademicStudents().size()));
        lcBA.setParent(li);
        final List<StudentMoveModel> budgetAcademicStudents = data.getBudgetAcademicStudents();
        lcBA.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(budgetAcademicStudents);
        });
        this.reportService.totalBudgetAcademic += data.getBudgetAcademicStudents().size();

        Listcell lcDA = new Listcell();
        lcDA.setLabel(String.valueOf(data.getUnBudgetAcademicStudents().size()));
        lcDA.setParent(li);
        final List<StudentMoveModel> unBudgetAcademicStudents = data.getUnBudgetAcademicStudents();
        lcDA.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(unBudgetAcademicStudents);
        });
        this.reportService.totalUnBudgetAcademic += data.getUnBudgetAcademicStudents().size();

        Listcell lcBP = new Listcell();
        lcBP.setLabel(String.valueOf(data.getBudgetMoveInStudents().size()));
        lcBP.setParent(li);
        final List<StudentMoveModel> budgetMoveInStudents = data.getBudgetMoveInStudents();
        lcBP.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(budgetMoveInStudents);
        });
        this.reportService.totalBudgetMoveIn += data.getBudgetMoveInStudents().size();

        Listcell lcDP = new Listcell();
        lcDP.setLabel(String.valueOf(data.getUnBudgetMoveInStudents().size()));
        lcDP.setParent(li);
        final List<StudentMoveModel> unBudgetMoveInStudents = data.getUnBudgetMoveInStudents();
        lcDP.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudents(unBudgetMoveInStudents);
        });
        this.reportService.totalUnBudgetMoveIn += data.getUnBudgetMoveInStudents().size();

        Listcell lcBU = new Listcell();
        lcBU.setLabel(String.valueOf(data.getBudgetMoveOutStudents().size()));
        lcBU.setParent(li);
        final List<StudentMoveModel> budgetMoveOutStudents = data.getBudgetMoveOutStudents();
        lcBU.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudentsWithReason(budgetMoveOutStudents);
        });
        this.reportService.totalBudgetMoveOut += data.getBudgetMoveOutStudents().size();

        Listcell lcDU = new Listcell();
        lcDU.setLabel(String.valueOf(data.getUnBudgetMoveOutStudents().size()));
        lcDU.setParent(li);
        final List<StudentMoveModel> unBudgetMoveOutStudents = data.getUnBudgetMoveOutStudents();
        lcDU.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudentsWithReason(unBudgetMoveOutStudents);
        });
        this.reportService.totalUnBudgetMoveOut += data.getUnBudgetMoveOutStudents().size();

        Listcell lcBAU = new Listcell();
        lcBAU.setLabel(String.valueOf(data.getBudgetAcademicMoveOutStudents().size()));
        lcBAU.setParent(li);
        final List<StudentMoveModel> budgetAcademicMoveOutStudents = data.getBudgetAcademicMoveOutStudents();
        lcBAU.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudentsWithReason(budgetAcademicMoveOutStudents);
        });
        this.reportService.totalBudgetAcademicMoveOut += data.getBudgetAcademicMoveOutStudents().size();

        Listcell lcDAU = new Listcell();
        lcDAU.setLabel(String.valueOf(data.getUnBudgetAcademicMoveOutStudents().size()));
        lcDAU.setParent(li);
        final List<StudentMoveModel> unBudgetAcademicMoveOutStudents = data.getUnBudgetAcademicMoveOutStudents();
        lcDAU.addEventListener(Events.ON_CLICK, (EventListener<MouseEvent>) event -> {
            showStudentsWithReason(unBudgetAcademicMoveOutStudents);
        });
        this.reportService.totalUnBudgetAcademicMoveOut += data.getUnBudgetAcademicMoveOutStudents().size();
    }

    public void showStudents (List<StudentMoveModel> students) {
        DialogUtil.info(concat(students), "Студенты");
    }

    public void showStudentsWithReason (List<StudentMoveModel> students) {
        DialogUtil.info(concatReason(students), "Студенты");
    }

    public String concat (List<StudentMoveModel> students) {
        String res = "";
        for (int i = 0; i < students.size(); i++) {
            res = res + (i + 1) + ". " + students.get(i).getFio() + "\n";
        }
        return res;
    }

    public String concatReason (List<StudentMoveModel> students) {
        String res = "";
        for (int i = 0; i < students.size(); i++) {
            res = res + (i + 1) + ". " + students.get(i).getFio() + "\n";
            res = res + "(\"" + students.get(i).getReason() + "\")\n\n";
        }
        return res;
    }

    public void calcFooter (Listfoot lfStudent) {
        Listfooter lfEmpty = new Listfooter("");
        lfEmpty.setParent(lfStudent);

        Listfooter lfBudget = new Listfooter(this.reportService.totalBudget.toString());
        lfBudget.setParent(lfStudent);
        lfBudget.setTooltiptext("Кол-во студентов");
        lfBudget.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfUnBudget = new Listfooter(this.reportService.totalUnBudget.toString());
        lfUnBudget.setParent(lfStudent);
        lfUnBudget.setTooltiptext("Кол-во студентов");
        lfUnBudget.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfBudgetAcademic = new Listfooter(this.reportService.totalBudgetAcademic.toString());
        lfBudgetAcademic.setParent(lfStudent);
        lfBudgetAcademic.setTooltiptext("Кол-во студентов");
        lfBudgetAcademic.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfUnBudgetAcademic = new Listfooter(this.reportService.totalUnBudgetAcademic.toString());
        lfUnBudgetAcademic.setParent(lfStudent);
        lfUnBudgetAcademic.setTooltiptext("Кол-во студентов");
        lfUnBudgetAcademic.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfBudgetMoveIn = new Listfooter(this.reportService.totalBudgetMoveIn.toString());
        lfBudgetMoveIn.setParent(lfStudent);
        lfBudgetMoveIn.setTooltiptext("Кол-во студентов");
        lfBudgetMoveIn.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfUnBudgetMoveIn = new Listfooter(this.reportService.totalUnBudgetMoveIn.toString());
        lfUnBudgetMoveIn.setParent(lfStudent);
        lfUnBudgetMoveIn.setTooltiptext("Кол-во студентов");
        lfUnBudgetMoveIn.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfBudgetMoveOut = new Listfooter(this.reportService.totalBudgetMoveOut.toString());
        lfBudgetMoveOut.setParent(lfStudent);
        lfBudgetMoveOut.setTooltiptext("Кол-во студентов");
        lfBudgetMoveOut.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfUnBudgetMoveOut = new Listfooter(this.reportService.totalUnBudgetMoveOut.toString());
        lfUnBudgetMoveOut.setParent(lfStudent);
        lfUnBudgetMoveOut.setTooltiptext("Кол-во студентов");
        lfUnBudgetMoveOut.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfBudgetAcademicMoveOut = new Listfooter(this.reportService.totalBudgetAcademicMoveOut.toString());
        lfBudgetAcademicMoveOut.setParent(lfStudent);
        lfBudgetAcademicMoveOut.setTooltiptext("Кол-во студентов");
        lfBudgetAcademicMoveOut.setStyle("border: 1px solid #bfbfbf;");

        Listfooter lfUnBudgetAcademicMoveOut = new Listfooter(this.reportService.totalUnBudgetAcademicMoveOut.toString());
        lfUnBudgetAcademicMoveOut.setParent(lfStudent);
        lfUnBudgetAcademicMoveOut.setTooltiptext("Кол-во студентов");
        lfUnBudgetAcademicMoveOut.setStyle("border: 1px solid #bfbfbf;");

    }
}
