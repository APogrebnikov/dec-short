package org.edec.contingentMovement.ctrl;

import org.edec.contingentMovement.model.ResitRatingModel;
import org.edec.contingentMovement.service.ResitService;
import org.edec.contingentMovement.service.impl.ResitImpl;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.synchroMine.model.mine.Student;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class WinResitCtrl extends SelectorComposer<Window> {
    public static final String SELECTED_STUDENT = "selectedStudent";

    @Wire
    private Label lResitStudent;
    @Wire
    private Listbox lbResitOldGroup, lbResitCurrentGroup;

    private ResitService resitService = new ResitImpl();
    private TemplatePageCtrl template = new TemplatePageCtrl();

    private StudentStatusModel selectedStudent;
    private List<ResitRatingModel> listRatingOldGroup, listRatingCurrentGroup;

    @Override
    public void doAfterCompose (Window comp) throws Exception {
        super.doAfterCompose(comp);
        selectedStudent = (StudentStatusModel) ComponentHelper.findArg(SELECTED_STUDENT);

        resitService.fillDirectionCodeAndPreviousGroup(selectedStudent);

        fill();
    }

    private void fill () {

        lResitStudent.setValue("Перезачет оценок у студента " + selectedStudent.getFio() + " (" + selectedStudent.getGroupname() + ")");
        lbResitOldGroup.setItemRenderer((ListitemRenderer<ResitRatingModel>) (listitem, data, index) -> {
            listitem.setValue(data);
            new Listcell(data.getSubjectname()).setParent(listitem);
            new Listcell(data.getGroupname()).setParent(listitem);
            new Listcell(data.getSemesternumber().toString()).setParent(listitem);
            new Listcell(data.getFoc()).setParent(listitem);
            new Listcell(String.valueOf(data.getHoursCount())).setParent(listitem);
            if(RatingConst.getDataByRating(data.getRating()) == RatingConst.PASS) {
                Listcell listcell = new Listcell();
                listcell.appendChild(new Image("/imgs/okCLR.png"));
                listitem.appendChild(listcell);
            } else if(data.getRating() > 2) {
                new Listcell(RatingConst.getDataByRating(data.getRating()).getShortname()).setParent(listitem);
            } else {
                new Listcell().setParent(listitem);
            }
            if (data.getResitRating() != null) {
                listitem.setStyle("background: #bde0ff;");
            }
        });
        lbResitCurrentGroup.setItemRenderer((ListitemRenderer<ResitRatingModel>) (listitem, data, index) -> {
            listitem.setValue(data);
            new Listcell(data.getSubjectname()).setParent(listitem);
            new Listcell(String.valueOf(data.getSemesternumber())).setParent(listitem);
            new Listcell(data.getFoc()).setParent(listitem);
            new Listcell(String.valueOf(data.getHoursCount())).setParent(listitem);
            if(RatingConst.getDataByRating(data.getRating()) == RatingConst.PASS) {
                Listcell listcell = new Listcell();
                listcell.appendChild(new Image("/imgs/okCLR.png"));
                listitem.appendChild(listcell);
            } else if(data.getRating() > 2) {
                new Listcell(RatingConst.getDataByRating(data.getRating()).getShortname()).setParent(listitem);
            } else {
                new Listcell().setParent(listitem);
            }
            if (data.getResitRating() != null) {
                listitem.setStyle("background: #bde0ff;");
            }
        });

        fillListboxOldGroup();
        fillListboxCurrentGroup();
    }

    private void fillListboxOldGroup () {
        listRatingOldGroup = resitService.getListResitRatingModel(selectedStudent.getIdStudentCard(), selectedStudent.getIdDG(), false);
        refreshListboxOldGroup(null);
    }

    private void refreshListboxOldGroup (Integer index) {
        lbResitOldGroup.setModel(new ListModelList<>(listRatingOldGroup));
        lbResitOldGroup.renderAll();

        if (index != null) {
            selectListitem(lbResitOldGroup, index);
        }
    }

    private void selectListitem (Listbox listbox, Integer index) {
        listbox.setSelectedIndex(index);
        listbox.getSelectedItem().focus();
    }

    private void fillListboxCurrentGroup () {
        listRatingCurrentGroup = resitService.getListResitRatingModel(selectedStudent.getIdStudentCard(), selectedStudent.getIdDG(), true);
        refreshListboxCurrentGroup(null);
    }

    private void refreshListboxCurrentGroup (Integer index) {
        lbResitCurrentGroup.setModel(new ListModelList<>(listRatingCurrentGroup));
        lbResitCurrentGroup.renderAll();
        if (index != null) {
            selectListitem(lbResitCurrentGroup, index);
        }
    }

    @Listen("onClick = #btnAutoResit")
    public void autoResit () {
        resitService.autoResit(listRatingOldGroup, listRatingCurrentGroup);
        refreshListboxCurrentGroup(null);
        refreshListboxOldGroup(null);
    }

    @Listen("onClick = #btnManualResit")
    public void manualResit () {
        if (lbResitOldGroup.getSelectedItem() == null || lbResitCurrentGroup.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите по одному предмету из каждого списка!");
            return;
        }
        if (resitService.manualResit(lbResitOldGroup.getSelectedItem().getValue(), lbResitCurrentGroup.getSelectedItem().getValue())) {
            PopupUtil.showInfo("Предметы успешно сопоставились");
            refreshListboxOldGroup(lbResitOldGroup.getSelectedIndex());
            refreshListboxCurrentGroup(lbResitCurrentGroup.getSelectedIndex());
        } else {
            PopupUtil.showInfo("Невозможно сопоставить предметы");
        }
    }

    @Listen("onClick = #btnDeleteSingleResit")
    public void cancelResitForSelectedRow () {
        if (lbResitCurrentGroup.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите предмет для отмены!");
            return;
        }

        ResitRatingModel ratingModel = lbResitCurrentGroup.getSelectedItem().getValue();
        ResitRatingModel oldRating = ratingModel.getResitRating();
        if(oldRating != null) {
            oldRating.setResitRating(null);
            ratingModel.setResitRating(null);
            ratingModel.setRating(0);
        }

        refreshListboxCurrentGroup(lbResitCurrentGroup.getSelectedIndex());
    }

    @Listen("onClick = #btnDeleteAllResit")
    public void deleteResit () {
        DialogUtil.questionWithYesNoButtons("Вы точно хотите удалить все перезачеты?", "Удалить перезачеты?", event -> {
            if (resitService.deleteAllResit(selectedStudent)) {
                PopupUtil.showInfo("Перезачеты успешно удалены");
                fillListboxOldGroup();
                fillListboxCurrentGroup();
            } else {
                DialogUtil.error("Не удалось удалить перезачеты. Обратитесь к администратору.");
            }
        });
    }

    @Listen("onClick = #btnShowResitReport")
    public void showResitReport () {
        JasperReportService service = new JasperReportService();
        JasperReport jasperReport = service.getResitMark(selectedStudent);
        jasperReport.showPdf();
    }

    @Listen("onClick = #btnDownloadResitReport")
    public void downloadResitReport () {
        JasperReportService service = new JasperReportService();
        JasperReport jasperReport = service.getResitMark(selectedStudent);
        jasperReport.downloadDocx();
    }

    @Listen("onClick = #btnSaveResit")
    public void saveResit () {
        List<ResitRatingModel> listOfResitModel = new ArrayList<>();
        for (ResitRatingModel resitModel : listRatingCurrentGroup) {
            if (resitModel.getResitRating() != null) {
                listOfResitModel.add(resitModel);
            }
        }
        if (listOfResitModel.size() == 0) {
            PopupUtil.showWarning("Нет перезачтеных дисциплин");
        }
        if (resitService.saveResit(listOfResitModel, template.getCurrentUser().getIdHum())) {
            PopupUtil.showInfo("Перезачеты успешно сохранены");
            fillListboxCurrentGroup();
            fillListboxOldGroup();
        } else {
            DialogUtil.error("Произошла ошибка в момент сохранения перезачетов. Обратитесь к администратору!");
        }
    }

    @Listen("onClick = #btnWinResitExit")
    public void detachWin () {
        getSelf().detach();
    }
}
