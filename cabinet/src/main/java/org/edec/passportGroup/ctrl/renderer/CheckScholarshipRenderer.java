package org.edec.passportGroup.ctrl.renderer;

import org.edec.passportGroup.model.GroupModel;
import org.edec.passportGroup.model.ScholarshipInfo;
import org.edec.passportGroup.model.StudentModel;
import org.edec.passportGroup.model.SubjectModel;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by apple on 28.11.2017.
 */
public class CheckScholarshipRenderer extends GroupReportListRenderer {
    private String fioUser;
    private Long idHumanface;
    private Runnable update;

    public CheckScholarshipRenderer(List<SubjectModel> subjectModelList, GroupModel groupModel, String fioUser, Long idHumanface,
                                    Runnable update) {
        super(subjectModelList, groupModel, true, null,false);
        this.fioUser = fioUser;
        this.idHumanface = idHumanface;
        this.update = update;
    }

    @Override
    public void render (Listitem listitem, StudentModel studentModel, int i) throws Exception {
        super.render(listitem, studentModel, i);

        int countFilledCells = 0;

        if (i == 0) {
            createListheader("Статус", "pg_header_label_new pg_header_fon_pr pg_header_label_new_scholarship_lineheight", "60px", listitem);
            createListheader("Дата", "pg_header_label_new_scholarship pg_header_fon_pr", "100px", listitem);
            createListheader("Вид", "pg_header_label_new_scholarship pg_header_fon_pr", "100px", listitem);
            createListheader("Отмена", "pg_header_label_new_scholarship pg_header_fon_pr", "100px", listitem);
            createListheader("Дата", "pg_header_label_new_scholarship pg_header_fon_pr", "100px", listitem);
            createListheader("Вид", "pg_header_label_new_scholarship pg_header_fon_pr", "100px", listitem);
            createListheader("", "pg_header_label_new pg_header_fon_pr pg_header_label_new_scholarship_lineheight", "60px", listitem);

            for (int j = 0; j < 6; j++) {
                Listcell emptyCell = new Listcell("");
                emptyCell.setStyle("align: center; border-right: 1px solid #777777; ");
                listitem.appendChild(emptyCell);
            }
        } else if (i > 1) {
            /*
                Заполнение первой ячейки(должен ли студент получать стипендию)
             */
            Listcell listcell = new Listcell();

            if (studentModel.getListScholarshipInfo().size() == 1) {
                // если никакой информации нет - проверяем, нужна ли стипендия
                if (studentModel.getListScholarshipInfo().get(0).getIdDicAction() == null) {
                    listcell.setStyle(
                            "background: " + getColorNameByScholarshipInfoForCurSem(studentModel.getListScholarshipInfo().get(0)));
                    listcell.setTooltiptext(studentModel.getListScholarshipInfo().get(0).getReason());
                } else {
                    // если есть информация - проверяем, не истек ли срок стипендии(актуально для продления)
                    if (studentModel.getListScholarshipInfo().get(0).getDateScholarshipEnd() != null &&
                        studentModel.getListScholarshipInfo().get(0).getDateScholarshipEnd().before(new Date())) {
                        listcell.setStyle(
                                "background: " + getColorNameByScholarshipInfoForCurSem(studentModel.getListScholarshipInfo().get(0)));
                        listcell.setTooltiptext(studentModel.getListScholarshipInfo().get(0).getReason());
                    } else {
                        listcell.setStyle("background: #95FF82");
                    }
                }
            } else {
                //берем последнюю информацию по студенту
                ScholarshipInfo scholarshipInfo = studentModel.getListScholarshipInfo()
                                                              .get(studentModel.getListScholarshipInfo().size() - 1);

                if (scholarshipInfo.getIdDicAction() == 1) {
                    listcell.setStyle("background: #95FF82");
                } else {
                    listcell.setStyle(
                            "background: " + getColorNameByScholarshipInfoForCurSem(studentModel.getListScholarshipInfo().get(0)));
                    listcell.setTooltiptext(studentModel.getListScholarshipInfo().get(0).getReason());
                }
            }

            listitem.appendChild(listcell);
            countFilledCells++;

            List<ScholarshipInfo> listSch = studentModel.getListScholarshipInfo();
            if (listSch.size() == 1 && listSch.get(0).getIdDicAction() != null) {
                constructCellsForSetScholarship(listSch.get(0), listitem);
                countFilledCells += 2;
            } else if (listSch.size() > 1) {
                constructCellsForSetScholarship(listSch.get(0), listitem);
                countFilledCells += 2;

                if (listSch.get(1).getIdDicAction() == 1) {
                    constructEmptyCell(listitem);
                    countFilledCells++;

                    constructCellsForSetScholarship(listSch.get(1), listitem);
                    countFilledCells += 2;
                } else {
                    constructCellsForCanceledScholarship(listSch.get(1), listitem);
                    countFilledCells++;
                }

                if (listSch.size() == 3) {
                    constructCellsForSetScholarship(listSch.get(2), listitem);
                    countFilledCells += 2;
                }
            }

            for (; countFilledCells < 6; countFilledCells++) {
                constructEmptyCell(listitem);
            }

            if (studentModel.getListScholarshipInfo().size() == 3) {
                // если есть три этапа по стипендии - ничего не делаем
                Listcell cell = new Listcell();
                listitem.appendChild(cell);
                return;
            } else if (studentModel.getListScholarshipInfo().size() == 1 &&
                       studentModel.getListScholarshipInfo().get(0).getIdDicAction() == null) {
                // если нет никакой информации - назначаем
                createLcWithButtonSetScholarship(studentModel, listitem);
            } else if (studentModel.getListScholarshipInfo().get(studentModel.getListScholarshipInfo().size() - 1).getIdDicAction() == 2) {
                // если последняя была отмена - можем назначить
                createLcWithButtonSetScholarship(studentModel, listitem);
            } else {
                createLcWithButtonCancelScholarship(studentModel, listitem);
            }
        }
    }

    private void createListheader (String label, String sclass, String width, Listitem listitem) {
        Listheader listheader = new Listheader();

        Div div = new Div();
        Label lb = new Label(label);
        lb.setSclass("pg_scholarship_label");
        div.appendChild(lb);
        listheader.appendChild(div);
        div.setSclass(sclass);
        listheader.setWidth(width);
        listitem.getListbox().getListhead().appendChild(listheader);
    }

    private void createLcWithButtonSetScholarship (StudentModel studentModel, Listitem listitem) {
        Listcell listcell = new Listcell();
        Button btn = new Button("", "/imgs/ok.png");
        btn.setHoverImage("/imgs/okCLR.png");
        listcell.appendChild(btn);

        btn.addEventListener(Events.ON_CLICK, event -> {
            HashMap args = new HashMap();
            //TODO сделать константы
            args.put("scholarshipInfo", studentModel.getListScholarshipInfo().get(studentModel.getListScholarshipInfo().size() - 1));
            args.put("studentModel", studentModel);
            args.put("fioUser", fioUser);
            args.put("update", update);
            args.put("idHumanface", idHumanface);
            ComponentHelper.createWindow("/passportGroup/winAddScholarshipForStudent.zul", "winAddScholarshipForStudent", args).doModal();
        });

        listitem.appendChild(listcell);
    }

    private void createLcWithButtonCancelScholarship (StudentModel studentModel, Listitem listitem) {
        Listcell listcell = new Listcell();
        Button btn = new Button("", "/imgs/cross.png");
        btn.setHoverImage("/imgs/crossCLR.png");
        listcell.appendChild(btn);

        btn.addEventListener(Events.ON_CLICK, event -> {
            //TODO сделать заполнение номера приказа;
            if (service.cancelScholarship(
                    studentModel, studentModel.getListScholarshipInfo().get(studentModel.getListScholarshipInfo().size() - 1), new Date(),
                    "", fioUser, idHumanface
            )) {
                PopupUtil.showInfo("Стипендия была успешно отменена");
                update.run();
            } else {
                PopupUtil.showError("Не удалось отменить стипендию, обратитесь к администратору");
            }
        });

        listitem.appendChild(listcell);
    }

    private void constructEmptyCell (Listitem item) {
        Listcell listcell = new Listcell();
        item.appendChild(listcell);
    }

    private void constructCellsForCanceledScholarship (ScholarshipInfo scholarshipInfo, Listitem li) {
        Listcell listcell = new Listcell();
        Label label = new Label(DateConverter.convertDateToString(scholarshipInfo.getDateScholarshipBegin()));
        listcell.appendChild(label);
        li.appendChild(listcell);
    }

    private void constructCellsForSetScholarship (ScholarshipInfo scholarshipInfo, Listitem li) {
        Listcell listcell = new Listcell();
        Label label = new Label(DateConverter.convertDateToString(scholarshipInfo.getDateScholarshipBegin()) + " - " +
                                DateConverter.convertDateToString(scholarshipInfo.getDateScholarshipEnd()));
        listcell.appendChild(label);

        li.appendChild(listcell);

        listcell = new Listcell();

        if (scholarshipInfo.getDateScholarshipBegin() != null && scholarshipInfo.getSectionName() != null &&
            !scholarshipInfo.getSectionName().equals("")) {
            // если найден пункт по стипендии
            label = new Label(scholarshipInfo.getSectionName());
            listcell.appendChild(label);
        } else if (scholarshipInfo.getDateScholarshipBegin() != null) {
            // если не найдем пункт по стипендии - значит стипендия была назначена вручную
            label = new Label("Вручную");
            listcell.appendChild(label);
        }

        li.appendChild(listcell);
    }

    private String getColorNameByScholarshipInfoForCurSem (ScholarshipInfo scholarshipInfo) {
        if (scholarshipInfo.getIdCurDicGroup() == null) {
            scholarshipInfo.setReason("Не заполнена текущая группа в базе данных, обратитесь к администратору\n");
            return "#4DE2F7";
        }

        if (scholarshipInfo.getDeducted() != null && scholarshipInfo.getDeducted()) {
            scholarshipInfo.setReason("Отчислен\n");
            return "#FF7373";
        }

        if (scholarshipInfo.getDeductedCurSem() != null && scholarshipInfo.getDeductedCurSem()) {
            scholarshipInfo.setReason("Отчислен\n");
            return "#FF7373";
        }

        if (scholarshipInfo.getAcademicLeave() != null && scholarshipInfo.getAcademicLeave()) {
            scholarshipInfo.setReason("Академ. отпуск\n");
            return "#FF7373";
        }

        if (scholarshipInfo.getAcademicLeaveCurSem() != null && scholarshipInfo.getAcademicLeaveCurSem()) {
            scholarshipInfo.setReason("Академ. отпуск\n");
            return "#FF7373";
        }

        if (scholarshipInfo.getGovernmentFinanced() != null && !scholarshipInfo.getGovernmentFinanced()) {
            scholarshipInfo.setReason("Платник\n");
            return "#FF7373";
        }

        if (scholarshipInfo.getNextGovernmentFinanced() != null && !scholarshipInfo.getNextGovernmentFinanced()) {
            scholarshipInfo.setReason("Платник\n");
            return "#FF7373";
        }

        if (scholarshipInfo.getSessionResult() != null && scholarshipInfo.getSessionResult() <= 0) {
            scholarshipInfo.setReason("Долги\n");
            return "#FF7373";
        }

        if (scholarshipInfo.getSessionResult() != null && scholarshipInfo.getSessionResult() == 1) {
            scholarshipInfo.setReason("Есть тройки\n");
            return "#FF7373";
        }

        if (!scholarshipInfo.getIdCurDicGroup().equals(scholarshipInfo.getIdDicGroup())) {
            scholarshipInfo.setReason("Не текущая группа\n");
            return "#FF7373";
        }

        scholarshipInfo.setReason(
                "Дата сдачи последнего долга: " + DateConverter.convertDateToString(scholarshipInfo.getDateCompleteSession()));
        return "#FFFE7E";
    }
}
