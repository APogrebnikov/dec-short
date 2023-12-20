package org.edec.newMine.registers.ctrl.renderer;

import org.edec.newMine.registers.model.StudentsRatingModel;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.converter.DateConverter;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class StudentsRatingsRenderer implements ListitemRenderer<StudentsRatingModel> {
    @Override
    public void render(Listitem li, StudentsRatingModel data, int i) throws Exception {
        li.setValue(data);
        new Listcell(i+1 + ". " +data.getFioStudent()).setParent(li);
        new Listcell(data.getMainRating() != 0 ? RatingConst.getByMineRating(data.getMainRating()).toString() : "-").setParent(li);
        new Listcell(data.getMainDateOfPass() != null ? DateConverter.convertDateToStringByFormat(data.getMainDateOfPass(), "dd.MM.yyyy") : "-").setParent(li);
        new Listcell(data.getRatingRetake1() != 0 ? RatingConst.getByMineRating(data.getRatingRetake1()).toString() : "-").setParent(li);
        new Listcell(data.getDateRetake1() != null ? DateConverter.convertDateToStringByFormat(data.getDateRetake1(), "dd.MM.yyyy") : "-").setParent(li);
        new Listcell(data.getRatingRetake2() != 0 ? RatingConst.getByMineRating(data.getRatingRetake2()).toString() : "-").setParent(li);
        new Listcell(data.getDateRetake2() != null ? DateConverter.convertDateToStringByFormat(data.getDateRetake2(), "dd.MM.yyyy") : "-").setParent(li);

    }
}
