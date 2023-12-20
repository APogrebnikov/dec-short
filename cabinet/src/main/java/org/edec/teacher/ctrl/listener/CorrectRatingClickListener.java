package org.edec.teacher.ctrl.listener;

import lombok.extern.log4j.Log4j;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.teacher.model.correctRequest.CorrectRequestModel;
import org.edec.teacher.model.register.RegisterRowModel;
import org.edec.teacher.service.RegisterRequestService;
import org.edec.teacher.service.impl.RegisterRequestServiceImpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.constants.RegisterRequestStatusConst;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.Date;
import java.util.List;

/**
 * Created by apogrebnikov on 08.11.18.
 */
@Log4j
public class CorrectRatingClickListener implements EventListener<Event> {
    private Listcell lc;

    private RegisterRowModel data;

    private FormOfControlConst formOfCtrl;
    private int registerType;

    private long idSystemUser;

    private RegisterRequestService service = new RegisterRequestServiceImpl();

    private List<RatingConst> ratings;
    private UserModel currentUser = (UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER);

    Combobox comb;

    public CorrectRatingClickListener(Listcell lc, RegisterRowModel data, FormOfControlConst formOfCtrl, int registerType, List<RatingConst> ratings) {
        this.lc = lc;
        this.data = data;
        this.formOfCtrl = formOfCtrl;
        this.registerType = registerType;
        this.idSystemUser = 17L;
        this.ratings = ratings;
    }

    @Override
    public void onEvent(Event event) throws Exception {
        lc.getChildren().clear();
        comb = new Combobox();

        for (RatingConst ratingConst : ratings) {

            if(!data.getMark().equals(ratingConst.getRating())) {
                String style = "color: black;";

                if (ratingConst.equals(RatingConst.NOT_PASS) || ratingConst.equals(RatingConst.FAILED_TO_APPEAR) || ratingConst.equals(RatingConst.UNSATISFACTORILY)) {
                    style = "color: red;";
                }

                appendComboitem(comb, ratingConst.getShortname(), style, ratingConst);
            }
        }

        comb.setStyle("width: 100px; margin-left: 5px;");


        lc.getChildren().clear();
        lc.appendChild(comb);

        comb.addEventListener(Events.ON_CHANGE, event1 -> onChangeCtrl());
        comb.focus();
        comb.open();
    }

    private void appendComboitem(Combobox cmb, String label, String style, RatingConst rating) {
        Comboitem item = new Comboitem();
        item.setValue(rating);
        item.setLabel(label);
        item.setStyle(style);
        cmb.appendChild(item);
    }

    /**
     * Метод изменения оценки.
     */
    public void onChangeCtrl() {
        Integer ratingValue = 0;
        if (comb.getSelectedItem() != null)
            ratingValue = ((RatingConst) comb.getSelectedItem().getValue()).getRating();
        lc.getChildren().clear();
        Label lb = new Label();
        if (ratingValue == 0) {
            ratingValue = data.getMark();
        }
        if (ratingValue == RatingConst.FAILED_TO_APPEAR.getRating()) {
            lb.setValue("Н.Я.");
            lb.setStyle("color: red;");
        } else if (ratingValue == RatingConst.NOT_PASS.getRating()) {
            lb.setValue("Не зачтено");
            lb.setStyle("color: red;");
        } else if (ratingValue == RatingConst.PASS.getRating()) {
            lb.setValue("Зачтено");
            lb.setStyle("color: #000000;");
        } else if (ratingValue == RatingConst.UNSATISFACTORILY.getRating()) {
            lb.setValue("2");
            lb.setStyle("color: red;");
        } else if (ratingValue > RatingConst.UNSATISFACTORILY.getRating()) {
            lb.setValue(String.valueOf(ratingValue));
            lb.setStyle("color: #000000;");
        } else lb.setValue("Введите оценку.");
        lc.appendChild(lb);
        data.setCurCorrectRequest(saveCorrectRequest(ratingValue, data.getCurCorrectRequest()));
    }


    public CorrectRequestModel saveCorrectRequest(int newRating, CorrectRequestModel correctRequest) {
        try {
            if (correctRequest == null ) {
                correctRequest = new CorrectRequestModel();
            }
            correctRequest.setIdSRH(data.getIdSRH());
            correctRequest.setFoc(formOfCtrl.getValue());
            correctRequest.setOldRating(data.getMark());
            correctRequest.setNewRating(newRating);
            correctRequest.setStatus(RegisterRequestStatusConst.CREATED);
            correctRequest.setIdHumanface(currentUser.getIdHum());
            correctRequest.setDateOfApplying(new Date());
            service.sendCorrectRequest(correctRequest);
            return correctRequest;
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Ошибка сохранения в базе данных. Обратитесь к администратору.", "Ошибка!", Messagebox.OK, Messagebox.ERROR);
            log.error("Ошибка! Корректировка оценки "+data.getMark()+" для студента "+ data.getStudentFullName()+" преподавателем " +currentUser.getShortFIO()+ " не записана");
            return null;
        }
    }
}
