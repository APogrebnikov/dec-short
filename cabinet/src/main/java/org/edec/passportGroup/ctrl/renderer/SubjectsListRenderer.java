package org.edec.passportGroup.ctrl.renderer;

import lombok.extern.log4j.Log4j;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.passportGroup.model.SubjectReportModel;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.passportGroup.service.impl.PassportGroupServiceESO;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.Date;
import java.util.List;


@Log4j
public class SubjectsListRenderer implements ListitemRenderer<SubjectReportModel> {
    private List<SubjectReportModel> subjectModelList;

    private String userFio;
    protected TemplatePageCtrl template = new TemplatePageCtrl();
    private PassportGroupService service = new PassportGroupServiceESO();

    public SubjectsListRenderer (List<SubjectReportModel> subjectModelList, String userFio) {
        this.subjectModelList = subjectModelList;
        this.userFio = userFio;
    }

    // Заполнение строк
    private void fillCells (Listitem listitem, SubjectReportModel subjectModel, int i) {
        Listcell checkCell = new Listcell();

        // Колонка "Дисциплина"
        Listcell subjectNameCell = new Listcell();
        Label lSubjectName = new Label(" " + (++i) + ". " + subjectModel.getSubjectName());
        subjectNameCell.appendChild(lSubjectName);

        // Колонка "Форма контроля"
        Listcell ctrlFormCell = new Listcell();
        Label lCtrlForm = new Label(subjectModel.getFoc().getName());
        ctrlFormCell.appendChild(lCtrlForm);

        // Колонка "Группа"
        Listcell groupCell = new Listcell();
        Label lGroup = new Label(subjectModel.getGroupName());
        groupCell.appendChild(lGroup);

        // Колонка "Дата сдачи"
        Listcell passDateCell = new Listcell();
        Label lPassDate;
        Datebox dbPassDate = new Datebox();
        if (subjectModel.getCheckDate() != null) {
            lPassDate = new Label(DateConverter.convertDateToString(subjectModel.getCheckDate()));
        } else {
            lPassDate = new Label("");
        }

        dbPassDate.setWidth("90%");
        if (!template.getCurrentModule().isReadonly()){
            dbPassDate.addEventListener(Events.ON_CHANGE, event -> {

                if (!service.updateCheckDate(subjectModel.getIdLgss(), dbPassDate.getValue(), subjectModel.getFoc())) {
                    PopupUtil.showError("Обновить дату сдачи не удалось!");

                    log.info("Пользователь " + userFio + " не смог изменить дату сдачи" + " по предмету " + subjectModel.getSubjectName() +
                            " в группе " + subjectModel.getGroupName() + ";");
                } else {
                    ((Label) passDateCell.getFirstChild()).setValue(DateConverter.convertDateToString(dbPassDate.getValue()));

                    service.createSRH(subjectModel.getIdLgss(), subjectModel.getFoc(), subjectModel.getType());
                    log.info("Пользователь " + userFio + " изменил дату сдачи на " + DateConverter.convertDateToString(dbPassDate.getValue()) +
                            " по предмету " + subjectModel.getSubjectName() + " в группе " + subjectModel.getGroupName() + ";");
                }

                passDateCell.getFirstChild().setVisible(true);
                passDateCell.getLastChild().setVisible(false);
            });
            passDateCell.appendChild(lPassDate);
            passDateCell.appendChild(dbPassDate);

            passDateCell.getLastChild().setVisible(false);

            passDateCell.addEventListener(Events.ON_DOUBLE_CLICK, event -> {
                passDateCell.getFirstChild().setVisible(false);

                if (!((Label) passDateCell.getFirstChild()).getValue().equals("")) {
                    Date date = DateConverter.convertStringToDate(((Label) passDateCell.getFirstChild()).getValue(), "dd.MM.yyyy");
                    ((Datebox) passDateCell.getLastChild()).setValue(date);
                }

                passDateCell.getLastChild().setVisible(true);

                ((Datebox) passDateCell.getLastChild()).focus();
                ((Datebox) passDateCell.getLastChild()).open();
            });

            dbPassDate.addEventListener(Events.ON_BLUR, event -> {
                passDateCell.getFirstChild().setVisible(true);
                dbPassDate.setVisible(false);
            });
        }
        // Колонка "Дата консультации"
        Listcell consultDateCell = new Listcell();
        Label lConsultDate;
        Datebox dbConsultDate = new Datebox();

        if (subjectModel.getConsultDate() != null) {
            lConsultDate = new Label(DateConverter.convertDateToString(subjectModel.getConsultDate()));
        } else {
            lConsultDate = new Label("");
        }
        dbConsultDate.setWidth("90%");

        if (!template.getCurrentModule().isReadonly()){
            dbConsultDate.addEventListener(Events.ON_CHANGE, event -> {

                if (!service.updateConsultDate(subjectModel.getIdLgss(), dbConsultDate.getValue())) {
                    PopupUtil.showError("Обновить дату консультации не удалось!");

                    log.info("Пользователь " + userFio + " не смог изменить дату консультации " + " по предмету " +
                            subjectModel.getSubjectName() + " в группе " + subjectModel.getGroupName() + ";");
                } else {
                    ((Label) consultDateCell.getFirstChild()).setValue(DateConverter.convertDateToString(dbConsultDate.getValue()));

                    log.info("Пользователь " + userFio + " изменил дату консультации на " +
                            DateConverter.convertDateToString(dbConsultDate.getValue()) + " по предмету " + subjectModel.getSubjectName() +
                            " в группе " + subjectModel.getGroupName() + ";");
                }

                consultDateCell.getFirstChild().setVisible(true);
                consultDateCell.getLastChild().setVisible(false);
            });

            consultDateCell.appendChild(lConsultDate);
            consultDateCell.appendChild(dbConsultDate);

            consultDateCell.getLastChild().setVisible(false);

            consultDateCell.addEventListener(Events.ON_DOUBLE_CLICK, event -> {
                consultDateCell.getFirstChild().setVisible(false);

                if (!((Label) consultDateCell.getFirstChild()).getValue().equals("")) {
                    Date date = DateConverter.convertStringToDate(((Label) consultDateCell.getFirstChild()).getValue(), "dd.MM.yyyy");
                    ((Datebox) consultDateCell.getLastChild()).setValue(date);
                }

                consultDateCell.getLastChild().setVisible(true);

                ((Datebox) consultDateCell.getLastChild()).focus();
                ((Datebox) consultDateCell.getLastChild()).open();
            });

            dbConsultDate.addEventListener(Events.ON_BLUR, event -> {
                consultDateCell.getFirstChild().setVisible(true);
                dbConsultDate.setVisible(false);
            });
        }

        // Колонка "Статус"
        Listcell statusCell = new Listcell();
        Label lStatus = new Label(subjectModel.getStatus());
        statusCell.appendChild(lStatus);

        // Добавление
        listitem.appendChild(checkCell);
        listitem.appendChild(subjectNameCell);
        listitem.appendChild(ctrlFormCell);
        listitem.appendChild(groupCell);
        listitem.appendChild(passDateCell);
        listitem.appendChild(consultDateCell);
        listitem.appendChild(statusCell);
    }

    //рэндеринг listitem
    @Override
    public void render (Listitem listitem, SubjectReportModel subjectModel, int i) throws Exception {
        listitem.setValue(subjectModel);

        fillCells(listitem, subjectModel, i);
    }
}