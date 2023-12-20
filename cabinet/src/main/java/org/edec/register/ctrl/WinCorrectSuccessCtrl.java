package org.edec.register.ctrl;

import org.edec.register.model.SimpleRatingModel;
import org.edec.register.service.RegisterRequestService;
import org.edec.register.service.impl.RegisterRequestServiceImpl;
import org.edec.teacher.model.correctRequest.CorrectRequestModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.constants.RegisterType;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;

public class WinCorrectSuccessCtrl extends SelectorComposer<Component> {
    public static final String CORRECT_REQUEST = "register_request";
    public static final String UPDATE_CORRECT_REQUEST = "update_correct_request";

    @Wire
    private Button btnDeny, btnSuccess;

    @Wire
    private Window winCorrectSuccess;

    @Wire
    private Textbox tbCorrectNumber, tbTicherFIO, tbStudentFIO, tbNewRating;

    @Wire
    private Textbox tbMainRaiting, tbReRaiting, tbComRaiting;

    @Wire
    private Combobox cbMainRaiting;

    private RegisterRequestService service = new RegisterRequestServiceImpl();

    Runnable updateCorrectRequest;
    CorrectRequestModel correctRequest;
    RegisterRequestService registerRequestService = new RegisterRequestServiceImpl();
    private List<RatingConst> ratingConstList;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        updateCorrectRequest = (Runnable) Executions.getCurrent().getArg().get(UPDATE_CORRECT_REQUEST);
        correctRequest = (CorrectRequestModel) Executions.getCurrent().getArg().get(CORRECT_REQUEST);
        fillWindow();
    }

    public void fillWindow(){
        if (ratingConstList == null) {
            initCmbMarks();
        }

        tbCorrectNumber.setValue(correctRequest.getId().toString());
        tbTicherFIO.setValue(correctRequest.getTeacherFIO());
        tbStudentFIO.setValue(correctRequest.getStudentFIO());
        tbNewRating.setValue(RatingConst.getNameByRating(correctRequest.getNewRating()));

        // Получаем историю оценок
        List<SimpleRatingModel> list = registerRequestService.getRatingHistoryForStudent(correctRequest.getIdSRH());
        if (list != null) {
            for (SimpleRatingModel simpleRatingModel : list) {
                if (simpleRatingModel.getRetakeCount() == RegisterType.MAIN.getRetakeCount()) {
                    //cbMainRaiting.setValue(RatingConst.getNameByRating(simpleRatingModel.getNewRating()));
                    selectItemByRating(cbMainRaiting, simpleRatingModel.getNewRating());
                }

                if (simpleRatingModel.getRetakeCount() == RegisterType.MAIN_RETAKE.getRetakeCount()
                        || simpleRatingModel.getRetakeCount() == RegisterType.INDIVIDUAL_RETAKE.getRetakeCount()) {
                    tbReRaiting.setValue(RatingConst.getNameByRating(simpleRatingModel.getNewRating()));
                    //selectItemByRating(tbReRaiting, simpleRatingModel.getNewRating());
                }

                if (simpleRatingModel.getRetakeCount() == RegisterType.COMMISSION.getRetakeCount()) {
                    tbComRaiting.setValue(RatingConst.getNameByRating(simpleRatingModel.getNewRating()));
                    //selectItemByRating(tbComRaiting, simpleRatingModel.getNewRating());
                }
            }
        }
    }

    private void initCmbMarks() {

        int type = 1;
        boolean isNotDifPass = FormOfControlConst.getName(this.correctRequest.getFC()) == FormOfControlConst.PASS;
        boolean practicIsPass = FormOfControlConst.getName(this.correctRequest.getFC()) == FormOfControlConst.PRACTIC;

        if (isNotDifPass || practicIsPass) {
            type = 2;
        }

        ratingConstList = RatingConst.getRatingsByType(type);

        for (RatingConst ratingConst : ratingConstList) {
            String style = "color: black;";

            if (ratingConst.equals(RatingConst.NOT_PASS) || ratingConst.equals(RatingConst.FAILED_TO_APPEAR) || ratingConst.equals(RatingConst.UNSATISFACTORILY)) {
                style = "color: red;";
            }

            appendComboitem(cbMainRaiting, ratingConst.getShortname(), style, ratingConst);
        }
    }

    private void appendComboitem(Combobox cmb, String label, String style, RatingConst rating) {
        Comboitem item = new Comboitem();
        item.setValue(rating);
        item.setLabel(label);
        item.setStyle(style);
        cmb.appendChild(item);
    }


    @Listen("onClick = #btnSuccess")
    public void successBtnClick() {
        // TODO: добавить защиту от дурака
        // Проверяем, что все заполнено, и оценки совпадают
        if (((RatingConst)cbMainRaiting.getSelectedItem().getValue()).getRating() - correctRequest.getNewRating() != 0){
            Messagebox.show("Введенная оценка не соответствует оценке из служебной.\nВы действительно хотите сохранить новую оценку?", "Корректировка оценки",
                    Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, (EventListener) evt -> {
                        if (evt.getName().equals("onOK")) {
                            correctRequest.setNewRating(((RatingConst)cbMainRaiting.getSelectedItem().getValue()).getRating());
                            this.saveCorrectRequest();
                        }
                    });
        } else {
            this.saveCorrectRequest();
        }

    }

    public void saveCorrectRequest(){

        if (!registerRequestService.acceptCorrectRequest(correctRequest)){
            PopupUtil.showError("Не удалось обновить ведомость, обратитесь к администратору");
            btnSuccess.setDisabled(true);
        } else {
            PopupUtil.showInfo("Ведомость успешно обновлена");
            updateCorrectRequest.run();
            winCorrectSuccess.detach();
        }
    }

    @Listen("onClick = #btnCancel")
    public void cancelBtnClick() {
        winCorrectSuccess.detach();
    }

    public void selectItemByRating(Combobox cmb, Integer newRating)
    {
        for (Comboitem item : cmb.getItems()) {
            if(((RatingConst)item.getValue()).getRating() == newRating) {
                cmb.setSelectedItem(item);
            }
        }

    }
}