package org.edec.successful.ctrl.renderer;

import org.edec.successful.model.Measure;
import org.edec.successful.model.RatingModel;
import org.edec.successful.model.ReportStatModel;
import org.edec.successful.model.StudentReportModel;
import org.edec.successful.model.SuccessfulTreeModel;
import org.edec.successful.service.impl.SuccessfulReportDataService;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfoot;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO вывод статистики о списке студентов
public class SuccessfulReportRender implements ListitemRenderer<SuccessfulTreeModel> {
    private SuccessfulTreeModel rootModel;
    private List<RatingModel> allRatings;

    private List<String> valuesStack = new ArrayList<>();
    private List<Measure> measureStack = new ArrayList<>();
    private List<Measure> allMeasures;

    private SuccessfulReportDataService reportService = new SuccessfulReportDataService();

    public SuccessfulReportRender(SuccessfulTreeModel rootModel, List<Measure> allMeasures, List<RatingModel> ratingModels) {
        this.rootModel = rootModel;
        this.allRatings = ratingModels;
        this.allMeasures = allMeasures;
    }

    @Override
    public void render(Listitem li, SuccessfulTreeModel successfulTreeModel, int i) throws Exception {
        if(rootModel.getParent() != null) {
            Listcell cell = new Listcell("<<");
            cell.addEventListener(Events.ON_CLICK, event -> {
                Listbox lb = li.getListbox();
                lb.setModel(new ListModelList<>(rootModel.getParent().getChilds()));
                rootModel = rootModel.getParent();

                measureStack.remove(measureStack.size() - 1);
                valuesStack.remove(valuesStack.size() - 1);

                lb.renderAll();
                buildFooter(lb.getListfoot());
            });
            cell.setParent(li);
        } else {
            new Listcell(String.valueOf(i + 1)).setParent(li);
        }

        Listcell cellValue = new Listcell(successfulTreeModel.getValue());
        cellValue.setParent(li);
        cellValue.setTooltiptext(successfulTreeModel.getValue());
        cellValue.setStyle("font-size: 11px");

        ((Label)li.getListbox().getListhead().getChildren().get(1).getFirstChild())
                .setValue(reportService.getHeaderValueForLayer(allMeasures.get(measureStack.size())));

        // TODO комментарий к условию successfulTreeModel.getChilds().get(0).getStudentReportModel(
        if(!successfulTreeModel.getChilds().isEmpty() && successfulTreeModel.getChilds().get(0).getStudentReportModel() == null) {
            cellValue.addEventListener(Events.ON_CLICK, event -> {
                Listbox lb = li.getListbox();
                rootModel = successfulTreeModel;

                measureStack.add(allMeasures.get(measureStack.size()));
                valuesStack.add(successfulTreeModel.getValue());

                lb.setModel(new ListModelList<>(rootModel.getChilds()));
                lb.renderAll();
                buildFooter(lb.getListfoot());
            });
        }

        ReportStatModel model = reportService.getStatData(measureStack, allRatings, valuesStack, successfulTreeModel.getValue(), allMeasures.get(measureStack.size()));

        double percentSuccessfulStudents = (double)model.getSuccessfulStudents().size() * 100 / model.getAllStudents().size();
        double percentUnSuccessfulStudents = (double)model.getStudentsWithDebts().size() * 100 / model.getAllStudents().size();
        double percentFullDebtors = (double)model.getStudentsWithAllDebts().size() * 100 / model.getAllStudents().size();

        DecimalFormat formatter = new DecimalFormat("#0.0");

        configureCell(
                Integer.toString(model.getAllStudents().size()),
                li,
                model.getAllStudents().stream().map(StudentReportModel::getFio).collect(Collectors.joining("\n"))
        );

        configureCell(
                Integer.toString(model.getSuccessfulStudents().size()) + " (" + formatter.format(percentSuccessfulStudents) + "%)",
                li,
                model.getSuccessfulStudents().stream().map(StudentReportModel::getFio).collect(Collectors.joining("\n"))
        );

        configureCell(
                Integer.toString(model.getStudentsWithOnlyFiveMarks().size()),
                li,
                model.getStudentsWithOnlyFiveMarks().stream().map(StudentReportModel::getFio).collect(Collectors.joining("\n"))
        );

        configureCell(
                Integer.toString(model.getStudentsWithFiveAndFourMarks().size()),
                li,
                model.getStudentsWithFiveAndFourMarks().stream().map(StudentReportModel::getFio).collect(Collectors.joining("\n"))
        );

        configureCell(
                Integer.toString(model.getStudentsWithOnlyFourMarks().size()),
                li,
                model.getStudentsWithOnlyFourMarks().stream().map(StudentReportModel::getFio).collect(Collectors.joining("\n"))
        );

        configureCell(
                Integer.toString(model.getStudentsWithFiveFourThreeMarks().size()),
                li,
                model.getStudentsWithFiveFourThreeMarks().stream().map(StudentReportModel::getFio).collect(Collectors.joining("\n"))
        );

        configureCell(
                Integer.toString(model.getStudentsWithOnlyThreeMarks().size()),
                li,
                model.getStudentsWithOnlyThreeMarks().stream().map(StudentReportModel::getFio).collect(Collectors.joining("\n"))
        );

        configureCell(
                Integer.toString(model.getStudentsWithTwoThreeMarks().size()),
                li,
                model.getStudentsWithTwoThreeMarks().stream().map(StudentReportModel::getFio).collect(Collectors.joining("\n"))
        );

        configureCell(
                Integer.toString(model.getStudentsWithDebts().size()) + " (" + formatter.format(percentUnSuccessfulStudents) + "%)",
                li,
                model.getStudentsWithDebts().stream().map(StudentReportModel::getFio).collect(Collectors.joining("\n"))
        );

        configureCell(
                Integer.toString(model.getStudentsWithOneDebt().size()),
                li,
                model.getStudentsWithOneDebt().stream().map(StudentReportModel::getFio).collect(Collectors.joining("\n"))
        );

        configureCell(
                Integer.toString(model.getStudentsWithTwoDebts().size()),
                li,
                model.getStudentsWithTwoDebts().stream().map(StudentReportModel::getFio).collect(Collectors.joining("\n"))
        );

        configureCell(
                Integer.toString(model.getStudentsWithThreeOrFourDebts().size()),
                li,
                model.getStudentsWithThreeOrFourDebts().stream().map(StudentReportModel::getFio).collect(Collectors.joining("\n"))
        );

        configureCell(
                Integer.toString(model.getStudentsWithMoreThanFiveDebts().size()),
                li,
                model.getStudentsWithMoreThanFiveDebts().stream().map(StudentReportModel::getFio).collect(Collectors.joining("\n"))
        );

        configureCell(
                Integer.toString(model.getStudentsWithAllDebts().size())+ " (" + formatter.format(percentFullDebtors) + "%)",
                li,
                model.getStudentsWithAllDebts().stream().map(StudentReportModel::getFio).collect(Collectors.joining("\n"))
        );
    }

    private void configureCell(String cellValue, Listitem li, String cellInfo) {
        Listcell cell = new Listcell(cellValue);
        cell.setParent(li);
        cell.addEventListener(Events.ON_CLICK, event -> DialogUtil.info(cellInfo));
    }

    public void buildFooter (Listfoot lfStudent) {
        lfStudent.getChildren().clear();

        Listfooter lfStudentCourse = new Listfooter("Сумма:");
        lfStudentCourse.setParent(lfStudent);

        ReportStatModel model = reportService.getStatData(measureStack, allRatings, valuesStack, null, null);

        double percentSuccessfulStudents = (double)model.getSuccessfulStudents().size() * 100 / model.getAllStudents().size();
        double percentUnSuccessfulStudents = (double)model.getStudentsWithDebts().size() * 100 / model.getAllStudents().size();
        double percentFullDebtors = (double)model.getStudentsWithAllDebts().size() * 100 / model.getAllStudents().size();

        DecimalFormat formatter = new DecimalFormat("#0.0");

        buildListfooter("", "").setParent(lfStudent);
        buildListfooter(Integer.toString(model.getAllStudents().size()), "Кол-во студентов").setParent(lfStudent);
        buildListfooter(Integer.toString(model.getSuccessfulStudents().size()) + " (" + formatter.format(percentSuccessfulStudents) + "%)", "Сдали").setParent(lfStudent);
        buildListfooter(Integer.toString(model.getStudentsWithOnlyFiveMarks().size()), "На 5").setParent(lfStudent);
        buildListfooter(Integer.toString(model.getStudentsWithFiveAndFourMarks().size()), "На 4 и 5").setParent(lfStudent);
        buildListfooter(Integer.toString(model.getStudentsWithOnlyFourMarks().size()), "На 4 и 5").setParent(lfStudent);
        buildListfooter(Integer.toString(model.getStudentsWithFiveFourThreeMarks().size()), "На 3 и 4-5").setParent(lfStudent);
        buildListfooter(Integer.toString(model.getStudentsWithOnlyThreeMarks().size()), "Все 3").setParent(lfStudent);
        buildListfooter(Integer.toString(model.getStudentsWithTwoThreeMarks().size()), "На 2 и 3").setParent(lfStudent);
        buildListfooter(Integer.toString(model.getStudentsWithDebts().size()) + " (" + formatter.format(percentUnSuccessfulStudents) + "%)", "Долги").setParent(lfStudent);
        buildListfooter(Integer.toString(model.getStudentsWithOneDebt().size()), "1 Долг").setParent(lfStudent);
        buildListfooter(Integer.toString(model.getStudentsWithTwoDebts().size()), "2 Долга").setParent(lfStudent);
        buildListfooter(Integer.toString(model.getStudentsWithThreeOrFourDebts().size()), "3-4 Долга").setParent(lfStudent);
        buildListfooter(Integer.toString(model.getStudentsWithMoreThanFiveDebts().size()), "5 и более").setParent(lfStudent);
        buildListfooter(Integer.toString(model.getStudentsWithAllDebts().size()) + " (" + formatter.format(percentFullDebtors) + "%)", "Не сдал полностью").setParent(lfStudent);
    }

    private Listfooter buildListfooter(String value, String tooltip) {
        Listfooter lf = new Listfooter(value);
        lf.setTooltiptext(tooltip);
        lf.setStyle("border: 1px solid #bfbfbf;");
        return lf;
    }
}
