package org.edec.contingentMovement.ctrl.renderer;

import org.edec.contingentMovement.model.ResitRatingModel;
import org.edec.utility.constants.CabinetStyleConst;
import org.edec.utility.constants.RatingConst;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import static org.edec.utility.constants.CabinetStyleConst.BACKGROUND_ORANGE;

public class RatingRenderer implements ListitemRenderer<ResitRatingModel> {

    private boolean changedMark;

    public RatingRenderer (boolean changedMark) {
        this.changedMark = changedMark;
    }

    @Override
    public void render (Listitem li, ResitRatingModel data, int index) throws Exception {
        li.setValue(data);

        new Listcell(data.getSubjectname()).setParent(li);
        new Listcell(String.valueOf(data.getSemesternumber())).setParent(li);
        new Listcell(data.getHoursCount() == null ? "-" : data.getHoursCount().toString()).setParent(li);
        new Listcell(data.getFoc() + (data.getType() == 1 ? " (диф.)" : "")).setParent(li);

        Listcell lcRating = new Listcell();
        Label lRating = new Label();
        Combobox cmbRating = new Combobox();
        cmbRating.setVisible(false);
        cmbRating.setReadonly(true);
        cmbRating.setHflex("1");

        for (RatingConst ratingConst : RatingConst.values()) {
            Comboitem ci = new Comboitem(ratingConst.getShortname());
            ci.setValue(ratingConst);
            ci.setParent(cmbRating);
        }
        if (data.getResitRating() != null) {
            li.setStyle(CabinetStyleConst.BACKGROUND_LIGHT_GRAY);
        }else {
            li.addEventListener(Events.ON_RIGHT_CLICK, event -> {
                data.setSelectedAsSecondTable(!data.getSelectedAsSecondTable());
                if(data.getSelectedAsSecondTable()) li.setStyle(BACKGROUND_ORANGE);
                else li.setStyle("background-color: white");
            });
        }
        if (data.getRating() != null) {
            lRating.setValue(RatingConst.getDataByRating(data.getRating()).getShortname());
        }
        lRating.setParent(lcRating);
        cmbRating.setParent(lcRating);
        lcRating.setParent(li);
        lcRating.addEventListener(Events.ON_CLICK, event -> {
            if (!changedMark) {
                return;
            }
            lRating.setVisible(false);
            cmbRating.setVisible(true);
            if (data.getRating() != null) {
                for (Comboitem ci : cmbRating.getItems()) {
                    RatingConst ratingConst = ci.getValue();
                    if (ratingConst.getRating() == data.getRating()) {
                        cmbRating.setSelectedItem(ci);
                        break;
                    }
                }
            }
        });
        cmbRating.addEventListener(Events.ON_SELECT, event -> chooseCombobox(cmbRating, lRating, data));
        cmbRating.addEventListener(Events.ON_BLUR, event -> chooseCombobox(cmbRating, lRating, data));

    }

    private void chooseCombobox (Combobox cmb, Label label, ResitRatingModel rating) {
        if (cmb.getSelectedItem() == null) {
            return;
        }
        RatingConst ratingConst = cmb.getSelectedItem().getValue();
        if (ratingConst == null) {
            label.setValue("");
            rating.setRating(null);
        } else {
            label.setValue(ratingConst.getShortname());
            rating.setRating(ratingConst.getRating());
        }
        label.setVisible(true);
        cmb.setVisible(false);
    }
}
