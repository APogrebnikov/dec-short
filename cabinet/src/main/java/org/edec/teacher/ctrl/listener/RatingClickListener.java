package org.edec.teacher.ctrl.listener;

import lombok.extern.log4j.Log4j;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.teacher.model.register.RegisterRowModel;
import org.edec.teacher.service.RegisterService;
import org.edec.teacher.service.impl.RegisterServiceImpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.constants.RegisterConst;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.Date;
import java.util.List;

/**
 * Created by antonskripacev on 24.02.17.
 */
    @Log4j
public class RatingClickListener implements EventListener<Event> {
    private Listcell lc;

    private RegisterRowModel data;

    private FormOfControlConst formOfCtrl;
    private int registerType;
    private int type;

    private long idSystemUser;

    private RegisterService service = new RegisterServiceImpl();

    private List<RatingConst> ratings;
    private UserModel currentUser = (UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER);

    Combobox comb;

    public RatingClickListener(Listcell lc, RegisterRowModel data, FormOfControlConst formOfCtrl, int registerType, int type, List<RatingConst> ratings) {
        this.lc = lc;
        this.data = data;
        this.formOfCtrl = formOfCtrl;
        this.registerType = registerType;
        this.idSystemUser = 17L;
        this.type = type;
        this.ratings = ratings;
    }

    @Override
    public void onEvent(Event event) throws Exception {
        lc.getChildren().clear();
        comb = new Combobox();

        for (RatingConst ratingConst : ratings) {
            String style = "color: black;";

            if (ratingConst.equals(RatingConst.NOT_PASS) || ratingConst.equals(RatingConst.FAILED_TO_APPEAR) || ratingConst.equals(RatingConst.UNSATISFACTORILY)) {
                style = "color: red;";
            }

            appendComboitem(comb, ratingConst.getShortname(), style, ratingConst);
        }

        comb.setStyle("width: 80px; margin-left: 5px;");
        comb.addEventListener(Events.ON_CHANGE, event1 -> onChangeCtrl());

        lc.appendChild(comb);

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
     * Метод сохранения оценки.
     */
    public void onChangeCtrl() {
        try {
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

            // Сохраняем в базе измененную оценку
            if (registerType == RegisterConst.TYPE_MAIN_NOT_SIGNED && ratingValue != 0) {
                if (null != data.getIdSRH() && data.getIdSRH() != 0) {
                    data.setMark(ratingValue);
                    data.setChangeDateTime(new Date());
                    service.updateSRHDateAndRating(data.getIdSRH(), ratingValue);
                } else {
                    long idSRH = service.createSRH(
                            formOfCtrl == FormOfControlConst.EXAM,
                            formOfCtrl == FormOfControlConst.PASS,
                            formOfCtrl == FormOfControlConst.CP,
                            formOfCtrl == FormOfControlConst.CW,
                            formOfCtrl == FormOfControlConst.PRACTIC,
                            type,
                            "0.0.0",
                            ratingValue,
                            data.getIdSR(),
                            idSystemUser,
                            registerType
                    );
                    data.setChangeDateTime(new Date());
                    data.setMark(ratingValue);
                    data.setIdSRH(idSRH);
                    data.setRetakeCount(registerType);
                }
            } else if (ratingValue != 0) {
                data.setMark(ratingValue);
                data.setChangeDateTime(new Date());
                service.updateSRHDateAndRating(data.getIdSRH(), ratingValue);
            }
            log.info("Оценка "+data.getMark()+" для студента "+ data.getStudentFullName()+" преподавателем " +
                    currentUser.getShortFIO() + " внесена успешно.");
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Ошибка сохранения в базе данных. Обратитесь к администратору.", "Ошибка!", Messagebox.OK, Messagebox.ERROR);
            log.error("Ошибка! Оценка "+data.getMark()+" для студента "+ data.getStudentFullName()+" преподавателем " +currentUser.getShortFIO()+ " не записана");
        }
    }
}
