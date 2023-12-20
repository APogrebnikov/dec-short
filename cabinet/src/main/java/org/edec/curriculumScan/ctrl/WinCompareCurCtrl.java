package org.edec.curriculumScan.ctrl;

import com.lowagie.text.ListItem;
import org.apache.log4j.Logger;
import org.edec.curriculumScan.ctrl.renderer.SubjectCompLbRenderer;
import org.edec.curriculumScan.ctrl.renderer.SubjectLbRenderer;
import org.edec.curriculumScan.model.Block;
import org.edec.curriculumScan.model.CurrCompare;
import org.edec.curriculumScan.model.Curriculum;
import org.edec.curriculumScan.model.Subject;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.FormOfStudyConst;
import org.edec.utility.constants.QualificationConst;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;

public class WinCompareCurCtrl extends SelectorComposer<Component> {

    public static final String OLD_CURRICULUM = "old_curriculum";
    public static final String NEW_CURRICULUM = "new_curriculum";
    public static final String MAIN_PAGE = "main_page";

    public final Logger log = Logger.getLogger(WinCompareCurCtrl.class.getName());

    @Wire
    Listbox lbCompareCur;

    @Wire
    Textbox tbFileNameNew, tbCreateDateNew, tbEnterDateNew, tbFormOfStudyNew, tbQualificationNew;

    @Wire
    Textbox tbFileNameOld, tbCreateDateOld, tbEnterDateOld, tbFormOfStudyOld, tbQualificationOld;

    Curriculum oldCurriculum, newCurriculum;
    IndexPageCtrl mainPage;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);

        mainPage = (IndexPageCtrl) Executions.getCurrent().getArg().get(MAIN_PAGE);
        oldCurriculum = (Curriculum) Executions.getCurrent().getArg().get(OLD_CURRICULUM);
        newCurriculum = (Curriculum) Executions.getCurrent().getArg().get(NEW_CURRICULUM);

        if(oldCurriculum == null || newCurriculum == null)
        {
            PopupUtil.showError("Что-то пошло не так");
            return;
        }

        // TODO Добавить дополнительные поля для сравнения (специальность, направление, сроки обучения)

        tbFileNameNew.setText(newCurriculum.getPlanFilename());
        tbCreateDateNew.setText(newCurriculum.getCreatedSchoolYear().toString());
        tbEnterDateNew.setText(newCurriculum.getEnterSchoolYear().toString());
        tbFormOfStudyNew.setText(newCurriculum.getFormOfStudy().getName());
        tbQualificationNew.setText(newCurriculum.getQualification().getName());

        tbFileNameOld.setText(oldCurriculum.getPlanFilename());
        tbCreateDateOld.setText(oldCurriculum.getCreatedSchoolYear().toString());
        tbEnterDateOld.setText(oldCurriculum.getEnterSchoolYear().toString());
        tbFormOfStudyOld.setText(FormOfStudyConst.getFormOfStudyByType(oldCurriculum.getFormOfStudyType()).getName());
        tbQualificationOld.setText(QualificationConst.getByType(oldCurriculum.getQualificationType()).getName());

        List<Subject> oldSubjects = new ArrayList<>();
        List<Subject> newSubjects = new ArrayList<>();

        for (Block block : newCurriculum.getBlockList()) {
            newSubjects.addAll(block.getSubjectList());
        }

        for (Block block : oldCurriculum.getBlockList()) {
            oldSubjects.addAll(block.getSubjectList());
        }

        lbCompareCur.setModel(new ListModelList<>(createCompareObject(oldCurriculum, newCurriculum)));
        lbCompareCur.setItemRenderer(new SubjectCompLbRenderer());
        lbCompareCur.renderAll();


        lbCompareCur.addEventListener("onSelect", new EventListener<SelectEvent>() {
            @Override
            public void onEvent (SelectEvent event) throws Exception {
                // Открываем выбранный
                if(event.getSelectedItems().size() > 0) {
                    Listitem li = (Listitem) event.getSelectedItems().toArray()[0];

                    Listcell lcNameNew = (Listcell) li.getChildren().get(SubjectCompLbRenderer.NEW_NAME_NUMBER);
                    Listcell lcNameOld = (Listcell) li.getChildren().get(SubjectCompLbRenderer.OLD_NAME_NUMBER);
                    Listcell lcCompetenceNew = (Listcell) li.getChildren().get(SubjectCompLbRenderer.NEW_COMPETENCE_NUMBER);
                    Listcell lcCompetenceOld = (Listcell) li.getChildren().get(SubjectCompLbRenderer.OLD_COMPETENCE_NUMBER);

                    CurrCompare currCompare = li.getValue();
                    if (currCompare.getNewModel() != null) {
                        if (!currCompare.check(currCompare.getNewModel().getName(), currCompare.getOldModel() == null ? null : currCompare.getOldModel().getName())) {
                            lcNameNew.setStyle("background: #ffacac;");
                        } else {
                            lcNameNew.setStyle("");
                        }
                        if (!currCompare.check(currCompare.getNewModel().getCompetenceString(), currCompare.getOldModel() == null ? null : currCompare.getOldModel().getCompetenceString())) {
                            lcCompetenceNew.setStyle("border-right: 3px solid red; background: #ffacac;");
                        } else {
                            lcCompetenceNew.setStyle("border-right: 3px solid red;");
                        }
                    }
                    if (currCompare.getOldModel() != null) {
                        lcNameOld.setStyle("");
                        lcCompetenceOld.setStyle("");
                    }
                }
                // Закрываем развыбранный
                if(event.getPreviousSelectedItems().size() > 0) {
                    Listitem li = (Listitem) event.getPreviousSelectedItems().toArray()[0];

                    Listcell lcNameNew = (Listcell) li.getChildren().get(SubjectCompLbRenderer.NEW_NAME_NUMBER);
                    Listcell lcNameOld = (Listcell) li.getChildren().get(SubjectCompLbRenderer.OLD_NAME_NUMBER);
                    Listcell lcCompetenceNew = (Listcell) li.getChildren().get(SubjectCompLbRenderer.NEW_COMPETENCE_NUMBER);
                    Listcell lcCompetenceOld = (Listcell) li.getChildren().get(SubjectCompLbRenderer.OLD_COMPETENCE_NUMBER);

                    CurrCompare currCompare = li.getValue();
                    if (currCompare.getNewModel() != null) {
                        if (!currCompare.check(currCompare.getNewModel().getName(), currCompare.getOldModel() == null ? null : currCompare.getOldModel().getName())) {
                            lcNameNew.setStyle("white-space: nowrap; overflow: hidden; text-overflow: ellipsis; background: #ffacac;");
                        } else {
                            lcNameNew.setStyle("white-space: nowrap; overflow: hidden; text-overflow: ellipsis;");
                        }
                        if (!currCompare.check(currCompare.getNewModel().getCompetenceString(), currCompare.getOldModel() == null ? null : currCompare.getOldModel().getCompetenceString())) {
                            lcCompetenceNew.setStyle("border-right: 3px solid red; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; background: #ffacac;");
                        } else {
                            lcCompetenceNew.setStyle("border-right: 3px solid red; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;");
                        }
                    }
                    if (currCompare.getOldModel() != null) {
                        lcNameOld.setStyle("white-space: nowrap; overflow: hidden; text-overflow: ellipsis;");
                        lcCompetenceOld.setStyle("white-space: nowrap; overflow: hidden; text-overflow: ellipsis;");
                    }

                }
            }
        });

    }

    /**
     * Создаем объект для отображения сравнения двух учебных планов
     * @param oldCurriculum
     * @param newCurriculum
     * @return
     */
    public List<CurrCompare> createCompareObject(Curriculum oldCurriculum, Curriculum newCurriculum) {
        List<CurrCompare> list = new ArrayList<>();
        List<Subject> oldSubjects = new ArrayList<>();
        List<Subject> newSubjects = new ArrayList<>();

        for (Block block : newCurriculum.getBlockList()) {
            newSubjects.addAll(block.getSubjectList());
        }

        for (Block block : oldCurriculum.getBlockList()) {
            oldSubjects.addAll(block.getSubjectList());
        }

        List<CurrCompare> notFoundNewList = new ArrayList<>();
        // Ищем - была ли в серверном УП дисциплина из нового и заполняем строку
        for (Subject newSubject : newSubjects) {
            CurrCompare newLine = new CurrCompare();
            newLine.setNewModel(newSubject);
            boolean find = false;
            for (Subject oldSubject : oldSubjects) {
                // TODO: придумать более вменяемые условия сравнения
                if (newSubject.getName().equals(oldSubject.getName())) {
                    newLine.setOldModel(oldSubject);
                    list.add(newLine);
                    oldSubjects.remove(oldSubject);
                    find = true;
                    break;
                }
            }
            if (!find){
                notFoundNewList.add(newLine);
            }
        }

        // Здесь, чтобы отображать разницу в конце списка
        list.addAll(notFoundNewList);

        // Остаток старых записываем в новые линии
        for (Subject oldSubject : oldSubjects) {
            CurrCompare newLine = new CurrCompare();
            newLine.setNewModel(null);
            newLine.setOldModel(oldSubject);
            list.add(newLine);
        }

        return list;
    }

    @Listen("onClick = #btAcceptNew;")
    public void saveNewCurriculum () {
        /* TODO: Здесь необходимо не просто сохранить учебный план -
        * а перезаписать старый - но с новыми параметрами, удалив ненужные строки
        * Важно, т.к. на текущий момент создастся дубликат, а это нам не нужно
        */
        // Найти все старые дисциплины и их компетенции и удалить их
        // Сохранить значения старых дисциплин с измененными значениями

        mainPage.storeOneCurriculum(newCurriculum);
    }

    @Listen("onClick = #btAcceptOld;")
    public void saveOldCurriculum () {
        PopupUtil.showWarning("Будет использован старый УП");
    }
}