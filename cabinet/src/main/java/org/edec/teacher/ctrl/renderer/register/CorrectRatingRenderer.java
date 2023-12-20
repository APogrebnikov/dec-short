package org.edec.teacher.ctrl.renderer.register;

import org.edec.teacher.ctrl.listener.CorrectRatingClickListener;
import org.edec.teacher.ctrl.listener.CorrectRequestClickListener;
import org.edec.teacher.ctrl.listener.RatingClickListener;
import org.edec.teacher.ctrl.listener.ThemeEditListener;
import org.edec.teacher.model.register.RegisterRowModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.constants.RegisterConst;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.List;

/**
 * Created by apogrebnikov on  08.11.18.
 */
public class CorrectRatingRenderer implements ListitemRenderer<RegisterRowModel> {
    private List<RatingConst> ratingConstList;
    private FormOfControlConst formOfCtrl;

    public CorrectRatingRenderer(List<RatingConst> ratingConstList, FormOfControlConst formOfCtrl) {
        this.ratingConstList = ratingConstList;
        this.formOfCtrl = formOfCtrl;
    }

    @Override
    public void render(Listitem item, RegisterRowModel data, int index) throws Exception {
        item.setValue(data);

        // Рендерим фио
        Listcell lc = new Listcell();
        Label lb = new Label();
        Integer ind = index + 1;
        if (data.getDeducted()) {
            lb.setValue(ind + ") " + data.getStudentFullName() + " (отчислен)");
            lb.setStyle("color: red;");
        } else if (data.getAcademicLeave()) {
            lb.setValue(ind + ") " + data.getStudentFullName() + " (академ. отпуск)");
            lb.setStyle("color: red;");
        } else {
            lb.setValue(ind + ") " + data.getStudentFullName());
            lb.setStyle("color: #000000;");
        }

        lc.appendChild(lb);
        lc.setStyle("border-bottom: solid 1px #bcbcbc; border-right: solid 1px #bcbcbc; background: #ffffff;");
        item.appendChild(lc);

        // Рендерим оценку
        lc = new Listcell();
        lb = new Label();

        if (formOfCtrl == FormOfControlConst.PASS) {

            if (data.getMark() != null && data.getMark() != 0) {
                if (data.getMark() == RatingConst.FAILED_TO_APPEAR.getRating()) {
                    lb.setValue("Н.Я.");
                    lb.setStyle("color: red;");
                } else if (data.getMark() == RatingConst.NOT_PASS.getRating()) {
                    lb.setValue("Не зачтено");
                    lb.setStyle("color: red;");
                } else if (data.getMark() == RatingConst.PASS.getRating()) {
                    lb.setValue("Зачтено");
                    lb.setStyle("color: #000000;");
                }

                lc.appendChild(lb);
            } else {
                lb.setValue("");
                lc.appendChild(lb);
            }
        } else {
            if (data.getMark() != null && data.getMark() != 0) {
                if (data.getMark() == RatingConst.FAILED_TO_APPEAR.getRating()) {
                    lb.setValue("Н.Я.");
                    lb.setStyle("color: red;");
                } else if (data.getMark() == RatingConst.UNSATISFACTORILY.getRating()) {
                    lb.setValue("2");
                    lb.setStyle("color: red;");
                } else if (data.getMark() > RatingConst.UNSATISFACTORILY.getRating()) {
                    lb.setValue(String.valueOf(data.getMark()));
                    lb.setStyle("color: #000000;");
                } else {
                    lb.setValue("Ошибка! Обратитесь к администратору.");
                }

                lc.appendChild(lb);
            } else {
                lb.setValue("");
                lc.appendChild(lb);
            }
        }
        lc.setStyle("text-align: center; border-bottom: solid 1px #bcbcbc; background: #B0FFAD;");
        item.appendChild(lc);

        // Поле для новой оценки
        lc = new Listcell();
        lc.setStyle("text-align: center; border-bottom: solid 1px #bcbcbc;");
        lb = new Label();

        lc.setTooltiptext("Для ввода оценки кликнете по данному полю.");
        lc.addEventListener(
                Events.ON_CLICK, new CorrectRatingClickListener(lc, data, formOfCtrl, RegisterConst.TYPE_MAIN_NOT_SIGNED, ratingConstList));
        item.appendChild(lc);

        // Поле для кнопки генерации служебной записки
        lc = new Listcell();
        lc.setStyle("text-align: center; border-bottom: solid 1px #bcbcbc;");
        // TODO: добавить условие - если уже была сформирована и подписана
        Button button = new Button("Сформировать");
        button.addEventListener(Events.ON_CLICK, new CorrectRequestClickListener(data));
        lc.appendChild(button);

        item.appendChild(lc);
    }

    private void appendComboitem(Combobox cmb, String label, String style, RatingConst rating) {
        Comboitem item = new Comboitem();
        item.setValue(rating);
        item.setLabel(label);
        item.setStyle(style);
        cmb.appendChild(item);
    }
}
