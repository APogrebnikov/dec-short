package org.edec.contingentMovement.ctrl.individualCurriculum;

import org.edec.contingentMovement.ctrl.renderer.AddedSubjectRenderer;
import org.edec.contingentMovement.model.ResitRatingModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.util.ArrayList;
import java.util.List;

public class WinAddSubjectsCtrl extends SelectorComposer<Window> {

    static final String ADDED_SUBJECTS = "added_subjects";
    static final String EVENT_ON_FINISH = "onFinish";

    @Wire
    private Checkbox chDiffPass;
    @Wire
    private Combobox cmbFormOfControl, cmbRating;
    @Wire
    private Doublebox doubleboxHours;
    @Wire
    private Intbox ibSemesterNumber;
    @Wire
    private Listbox lbAddedSubjects;
    @Wire
    private Textbox tbSubjectName, tbSubjectCode;

    private List<ResitRatingModel> tmpSubjects, addedSubjects;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        addedSubjects = (List<ResitRatingModel>) Executions.getCurrent().getArg().get(ADDED_SUBJECTS);
        tmpSubjects = new ArrayList<>(addedSubjects);

        cmbFormOfControl.setModel(new ListModelList<>(FormOfControlConst.values()));
        cmbRating.setModel(new ListModelList<>(RatingConst.getPositiveRatings()));

        lbAddedSubjects.setItemRenderer(new AddedSubjectRenderer());
        lbAddedSubjects.setModel(new ListModelList<>(tmpSubjects));
    }

    @Listen("onDelete = #lbAddedSubjects")
    public void deleteSubject(Event event) {

        ResitRatingModel subjectForDelete = (ResitRatingModel) event.getData();
        tmpSubjects.remove(subjectForDelete);
        lbAddedSubjects.setModel(new ListModelList<>(tmpSubjects));
    }

    @Listen("onClick = #btnAddSubject")
    public void addSubject() {

        FormOfControlConst foc = cmbFormOfControl.getSelectedItem().getValue();
        RatingConst rating = cmbRating.getSelectedItem().getValue();

        ResitRatingModel newRating = new ResitRatingModel();

        newRating.setSubjectcode(tbSubjectCode.getValue());
        newRating.setSubjectname(tbSubjectName.getValue());
        newRating.setHoursCount(doubleboxHours.doubleValue());
        newRating.setSemesternumber(ibSemesterNumber.getValue());
        newRating.setFoc(foc.getName());
        newRating.setType(chDiffPass.isChecked() ? 1 : 0);
        newRating.setRating(rating.getRating());

        tmpSubjects.add(newRating);
        lbAddedSubjects.setModel(new ListModelList<>(tmpSubjects));
    }

    @Listen("onClick = #btnCancel")
    public void cancel() {

        getSelf().detach();
    }

    @Listen("onClick = #btnSave")
    public void save() {

        addedSubjects.clear();
        addedSubjects.addAll(tmpSubjects);
        Events.postEvent(EVENT_ON_FINISH, getSelf(), null);
        getSelf().detach();
    }
}
