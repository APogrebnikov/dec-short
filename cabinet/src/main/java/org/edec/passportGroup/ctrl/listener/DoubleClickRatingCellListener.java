package org.edec.passportGroup.ctrl.listener;

import org.edec.passportGroup.model.RatingModel;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.passportGroup.service.impl.PassportGroupServiceESO;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listcell;

public class DoubleClickRatingCellListener implements EventListener<Event> {

    //Порядковые номера элементов, содержащихся в Listcell
    private static final int CMB_ELEMENT = 0;
    private static final int LABEL_ELEMENT = 1;
    private static final int IMG_ELEMENT = 2;

    private Listcell clickedListcell;
    private RatingModel ratingModel;
    private PassportGroupService service = new PassportGroupServiceESO();

    public DoubleClickRatingCellListener(Listcell clickedListcell, RatingModel ratingModel) {
        this.clickedListcell = clickedListcell;
        this.ratingModel = ratingModel;
    }

    @Override
    public void onEvent(Event event) throws Exception {
        //Проверяем можно ли студенту отредактировать оценку
        if (!service.checkIfOpenRetakeExist(ratingModel.getIdSR(), ratingModel.getFoc()) || service.notExistEmployeeSubjectGroup(ratingModel.getIdSubject())
        ) {

            //Если retakeCount = 5, значит оценка была проставлена вручную из паспорта группы
            //Присваиваем cmb значение из ratingModel
            if (ratingModel.getRating() != RatingConst.ZERO.getRating() //Проверяем - стоит ли оценка по предмету
                    && ratingModel.getRating() != RatingConst.FAILED_TO_APPEAR.getRating()) {

                if (ratingModel.getRating() == RatingConst.NOT_LEARNED.getRating()) {
                    ((Combobox) clickedListcell.getChildren().get(CMB_ELEMENT)).setSelectedIndex(1);
                } else {
                    boolean isNotDifPass = ((Combobox) clickedListcell.getChildren().get(CMB_ELEMENT)).getItemCount() == 4;

                    if (ratingModel.getFoc() == FormOfControlConst.PASS && isNotDifPass) {
                        if (ratingModel.getRating() == RatingConst.PASS.getRating()) {
                            ((Combobox) clickedListcell.getChildren().get(CMB_ELEMENT)).setSelectedIndex(2); //выставляем зачтено
                        } else {
                            ((Combobox) clickedListcell.getChildren().get(CMB_ELEMENT)).setSelectedIndex(3); //выставляем не зачтено
                        }
                    } else {
                        // +1 т.к. в списке есть н/и
                        ((Combobox) clickedListcell.getChildren().get(CMB_ELEMENT)).setSelectedIndex(ratingModel.getRating() + 1);
                    }
                }
            }

            clickedListcell.getChildren().get(CMB_ELEMENT).setVisible(true);
            clickedListcell.getChildren().get(LABEL_ELEMENT).setVisible(false);
            clickedListcell.getChildren().get(IMG_ELEMENT).setVisible(false);

            ((Combobox) clickedListcell.getChildren().get(CMB_ELEMENT)).setFocus(true);
        } else {
            PopupUtil.showWarning("Нельзя редактировать оценку, пока у студента открыта ведомость!");
        }
    }
}
