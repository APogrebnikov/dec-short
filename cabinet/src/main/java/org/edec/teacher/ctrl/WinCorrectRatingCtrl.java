package org.edec.teacher.ctrl;

import org.edec.teacher.ctrl.renderer.register.CorrectRatingRenderer;
import org.edec.teacher.ctrl.renderer.register.MainRegisterRenderer;
import org.edec.teacher.model.GroupModel;
import org.edec.teacher.model.register.RegisterModel;
import org.edec.teacher.model.register.RegisterRowModel;
import org.edec.teacher.model.registerRequest.StudentModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;

public class WinCorrectRatingCtrl extends SelectorComposer<Component> {
    public static final String SELECTED_RATINGS = "selectedRatings";
    public static final String TYPE = "type";
    public static final String RATING_CONSTS = "ratingConsts";
    public static final String FOC = "formOfControlConst";

    @Wire
    private Window winCorrectRating;

    @Wire
    private Button btnCreateRequest;

    @Wire
    private Listbox listboxStudents;

    private GroupModel curGroup;

    private int formOfControl;

    private List<StudentModel> listStudent;
    private List<RegisterRowModel> selectedRatingRow;
    private List<RatingConst> ratingConsts;
    private FormOfControlConst foc;
    private int type;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        selectedRatingRow = (List<RegisterRowModel>) Executions.getCurrent().getArg().get(SELECTED_RATINGS);
        type = (Integer) Executions.getCurrent().getArg().get(TYPE);
        ratingConsts = (List<RatingConst>) Executions.getCurrent().getArg().get(RATING_CONSTS);
        foc = (FormOfControlConst) Executions.getCurrent().getArg().get(FOC);

        listboxStudents.setItemRenderer(new CorrectRatingRenderer(ratingConsts,foc));
        listboxStudents.setModel(new ListModelList<>(selectedRatingRow));
        listboxStudents.renderAll();
    }

    @Listen("onClick = #btnCreateRequest")
    public void startRequestGenerator() {
        // TODO: Вызвать окно формирования служебной записки
    }

}
