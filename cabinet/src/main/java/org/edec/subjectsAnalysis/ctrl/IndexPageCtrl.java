package org.edec.subjectsAnalysis.ctrl;

import org.edec.subjectsAnalysis.ctrl.renderer.ExamRenderer;
import org.edec.subjectsAnalysis.ctrl.renderer.PassRenderer;
import org.edec.subjectsAnalysis.model.SubjectsAnalysisComparator;
import org.edec.subjectsAnalysis.model.SubjectsAnalysisModel;
import org.edec.subjectsAnalysis.service.SubjectsAnalysisService;
import org.edec.subjectsAnalysis.service.impl.SubjectAnalysisServiceImpl;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.report.POIUtility;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер страницы поиска дисциплин из зоны риска
 */
public class IndexPageCtrl extends CabinetSelector {
    @Wire
    private Combobox cmbSemester,cmbCourse, cmbFOS, cmbTotalRiskScore;
    @Wire
    private Radio rPass, rExam;
    @Wire
    private Listbox lbExam, lbPass;
    @Wire
    private Checkbox cbASC;
    @Wire
    private Listheader lhrSubjectExam, lhrAvgRating, lhrModa, lhrOneRetakeExam,lhrMoreThanOneRetakeExam,lhrThree,lhrFour,lhrFive, lhrTwo,
            lhrSubjectPass, lhrOneRetakePass, lhrMoreThamOneRetakePass, lhrPass, lhrNotPass, lhrTotalRiskScore;

    private ComponentService componentService = new ComponentServiceESOimpl();
    private SubjectsAnalysisService service = new SubjectAnalysisServiceImpl();

    private FormOfStudy fos;
    private SemesterModel selectedSem;
    private Integer course = 0;
    private Integer score = null;

    /**
     * Граничные показатели
     */
    // Для средней оценки
    double avgRating = 0;
    // Для одной пересдачи
    double oneRetake = 0;
    // Для троешников
    double three = 0;
    // для отличников в экзаменах
    double five = 0;
    // для отличников в зачетах
    double pass = 0;
    // Для более чем 1 пересдача
    double hqMoreOneRetake = 0;
    // Для неатестаций в экзаменах
    double hqTwo = 0;
    // Для неатестаций в зачетах
    double hqNotPass = 0;

    @Override
    protected void fill()  {
        hideListboxPass();
        componentService.fillCmbFormOfStudy(cmbFOS, cmbFOS, FormOfStudy.ALL.getType(), false);
        fillCmbSem();

        lhrSubjectExam.setSortAscending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_SUBJECTNAME));
        lhrSubjectExam.setSortDescending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_SUBJECTNAME_REV));
        lhrAvgRating.setSortAscending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_AVG_RATING));
        lhrAvgRating.setSortDescending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_AVG_RATING_REV));
        lhrModa.setSortAscending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_MODA));
        lhrModa.setSortDescending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_MODA_REV));
        lhrOneRetakeExam.setSortAscending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_ONE_RETAKE));
        lhrOneRetakeExam.setSortDescending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_ONE_RETAKE_REV));
        lhrMoreThanOneRetakeExam.setSortAscending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_MORE_THAN_ONE_RETAKE));
        lhrMoreThanOneRetakeExam.setSortDescending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_MORE_THAN_ONE_RETAKE_REV));
        lhrThree.setSortAscending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_THREE));
        lhrThree.setSortDescending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_THREE_REV));
        lhrFour.setSortAscending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_FOUR));
        lhrFour.setSortDescending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_FOUR_REV));
        lhrFive.setSortAscending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_FIVE));
        lhrFive.setSortDescending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_FIVE_REV));
        lhrTwo.setSortAscending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_NOT_ATTEST));
        lhrTwo.setSortDescending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_NOT_ATTEST_REV));
        lhrSubjectPass.setSortAscending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_SUBJECTNAME));
        lhrSubjectPass.setSortDescending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_SUBJECTNAME_REV));
        lhrOneRetakePass.setSortAscending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_ONE_RETAKE));
        lhrOneRetakePass.setSortDescending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_ONE_RETAKE_REV));
        lhrMoreThamOneRetakePass.setSortAscending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_MORE_THAN_ONE_RETAKE));
        lhrMoreThamOneRetakePass.setSortDescending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_MORE_THAN_ONE_RETAKE_REV));
        lhrPass.setSortAscending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_PASS));
        lhrPass.setSortDescending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_PASS_REV));
        lhrNotPass.setSortAscending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_NOT_PASS));
        lhrNotPass.setSortDescending(new SubjectsAnalysisComparator(SubjectsAnalysisComparator.CompareMethods.BY_NOT_PASS_REV));

    }

    @Listen("onChange = #cmbFOS")
    public void fillCmbSem() {
        fos = cmbFOS.getSelectedItem().getValue();
        lbPass.getItems().clear();
        lbExam.getItems().clear();
        cmbSemester.setSelectedIndex(-1);
        cmbCourse.setSelectedIndex(-1);
        componentService.fillCmbSem(cmbSemester, 1L, fos.getType(), null);
    }

    @Listen("onCheck = #rPass")
    public void hideListboxExam() {
        lbPass.setVisible(true);
        lbExam.setVisible(false);
    }

    @Listen("onCheck = #rExam")
    public void hideListboxPass() {
        lbPass.setVisible(false);
        lbExam.setVisible(true);
    }

    @Listen("onChange = #cmbSemester")
    public void fillListboxes(){
        selectedSem = cmbSemester.getSelectedItem().getValue();
        fillListboxPass(0, null);
        fillListboxExam(0, null);
    }

    @Listen("onClick = #btnBorder")
    public void showBorders() {
        if (rExam.isSelected()) {
            Messagebox.show("Средняя оценка (меньше):\n" + avgRating + "\n\n" +
                            "Одна пересдача (больше):\n" + oneRetake + "\n\n" +
                            ">1 пересдачи (больше):\n" + hqMoreOneRetake + "\n\n" +
                            "% Тройки (больше):\n" + three + "\n\n" +
                            "% Отлично (больше):\n" + five + "\n\n" +
                            "% Н.А. (больше):\n" + hqTwo,
                    "Границы",
                    org.zkoss.zul.Messagebox.OK,
                    org.zkoss.zul.Messagebox.INFORMATION);
        }
        if (rPass.isSelected()) {
            Messagebox.show("Одна пересдача (больше):\n" + oneRetake + "\n\n" +
                            ">1 пересдачи (больше):\n" + hqMoreOneRetake + "\n\n" +
                            "% Зачет (больше):\n" + pass + "\n\n" +
                            "% Н.А. (больше):\n" + hqNotPass,
                    "Границы",
                    org.zkoss.zul.Messagebox.OK,
                    org.zkoss.zul.Messagebox.INFORMATION);
        }
    }

    private void fillListboxExam(Integer course, Integer score) {
        if (selectedSem != null) {
            List<SubjectsAnalysisModel> listExams = service.getExamsSubjectsList(selectedSem.getIdSem(), course);

            if (!listExams.isEmpty()) {
//                double totalAvgRating = service.getAvg(listExams, 0);
//                double stDevAvgRating = service.getStDev(listExams, 0);
//                avgRating = totalAvgRating - stDevAvgRating;
                avgRating = service.getAvg(listExams, 0);

//                double modaMedian = service.getAvg(listExams, 1);
//                double stDevOneRetake = service.getStDev(listExams, 1);
//                oneRetake = totalOneRetake + stDevOneRetake;

                double totalOneRetake = service.getAvg(listExams, 1);
                double stDevOneRetake = service.getStDev(listExams, 1);
                oneRetake = totalOneRetake + stDevOneRetake;

                double totalThree = service.getAvg(listExams, 2);
                double stDevThree = service.getStDev(listExams, 2);
                three = totalThree + stDevThree;

                double totalFive = service.getAvg(listExams, 3);
                double stDevFive = service.getStDev(listExams, 3);
                five = totalFive + stDevFive;

                hqMoreOneRetake = service.getHQ(listExams, 0);
                hqTwo = service.getHQ(listExams, 1);

                for (SubjectsAnalysisModel model: listExams) {
                    int totalRiskScore = 0;
                    if (model.getAvgRating() < this.avgRating) {
                        totalRiskScore++;
                    }
                    if (model.getOneRetake() != null && model.getOneRetake() > this.oneRetake) {
                        totalRiskScore++;
                    }
                    if (model.getMoreThenOneRetake() != null && model.getMoreThenOneRetake() > this.hqMoreOneRetake) {
                        totalRiskScore++;
                    }
                    if (model.getPartThree() > this.three) {
                        totalRiskScore++;
                    }
                    if (model.getPartFive() > this.five) {
                        totalRiskScore++;
                    }
                    if (model.getPartTwo() > this.hqTwo) {
                        totalRiskScore++;
                    }
                    model.setTotalRiskScore(totalRiskScore);
                }

                if (score != null) {
                    listExams = listExams.stream().filter(x -> x.getTotalRiskScore().equals(score)).collect(Collectors.toList());
                }

                lbExam.setModel(new ListModelList<>(listExams));
                lbExam.setItemRenderer(new ExamRenderer(course, 1, selectedSem.getIdSem(),
                        avgRating,
                        oneRetake,
                        three,
                        five,
                        hqMoreOneRetake,
                        hqTwo));
            } else {
                PopupUtil.showWarning("Не нашлось предметов по заданным параметрам!");
                lbExam.getItems().clear();
                lbPass.getItems().clear();
            }
        }
    }

    private void fillListboxPass(Integer course, Integer score) {
        if (selectedSem != null) {
            List<SubjectsAnalysisModel> listPass = service.getPassSubjectsList(selectedSem.getIdSem(), course);
            if (!listPass.isEmpty()) {

                double totalOneRetake = service.getAvg(listPass, 1);
                double stDevOneRetake = service.getStDev(listPass, 1);
                oneRetake = totalOneRetake + stDevOneRetake;

                hqMoreOneRetake = service.getHQ(listPass, 0);
                pass = service.getHQ(listPass, 2);
                hqNotPass = service.getHQ(listPass, 3);

                for (SubjectsAnalysisModel model: listPass) {
                    int totalRiskScore = 0;
                    if (model.getOneRetake() != null && model.getOneRetake() > this.oneRetake) {
                        totalRiskScore++;
                    }
                    if (model.getMoreThenOneRetake() != null && model.getMoreThenOneRetake() > this.hqMoreOneRetake) {
                        totalRiskScore++;
                    }
                    if (model.getPartPass() > this.pass) {
                        totalRiskScore++;
                    }
                    if (model.getPartNoPass() > this.hqNotPass) {
                        totalRiskScore++;
                    }
                    model.setTotalRiskScore(totalRiskScore);
                }

                if (score != null) {
                    listPass = listPass.stream().filter(x -> x.getTotalRiskScore().equals(score)).collect(Collectors.toList());
                }

                lbPass.setModel(new ListModelList<>(listPass));
                lbPass.setItemRenderer(new PassRenderer(course, 2, selectedSem.getIdSem(),
                        oneRetake,
                        hqMoreOneRetake,
                        pass,
                        hqNotPass));

            } else {
                PopupUtil.showWarning("Не нашлось предметов по заданным параметрам!");
                lbExam.getItems().clear();
                lbPass.getItems().clear();
            }
        }
    }


    @Listen("onChange = #cmbCourse; ")
    public void fillListboxesByCourse() {
        switch(cmbCourse.getSelectedIndex()){
            case 0: course = 0; break;
            case 1: course = 1; break;
            case 2: course = 2; break;
            case 3: course = 3; break;
            case 4: course = 4; break;
            case 5: course = 5; break;
            case 6: course = 6; break;
        }
        fillListboxPass(course, score);
        fillListboxExam(course, score);
    }

    @Listen("onChange = #cmbTotalRiskScore")
    public void fillListboxesByScore() {
        switch(cmbTotalRiskScore.getSelectedIndex()){
            case 0: score = null; break;
            case 1: score = 0; break;
            case 2: score = 1; break;
            case 3: score = 2; break;
            case 4: score = 3; break;
            case 5: score = 4; break;
            case 6: score = 5; break;
        }
        fillListboxPass(course, score);
        fillListboxExam(course, score);
    }

}
