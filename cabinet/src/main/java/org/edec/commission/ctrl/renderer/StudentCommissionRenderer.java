package org.edec.commission.ctrl.renderer;

import org.edec.commission.model.StudentDebtModel;
import org.edec.commission.service.CommissionService;
import org.edec.commission.service.impl.CommissionServiceESOimpl;
import org.edec.utility.constants.RatingConst;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * Created by dmmax
 */
public class StudentCommissionRenderer implements ListitemRenderer<StudentDebtModel> {
    private CommissionService commService = new CommissionServiceESOimpl();

    @Override
    public void render (final Listitem li, final StudentDebtModel data, int index) throws Exception {
        li.setValue(data);

        new Listcell(data.getFio()).setParent(li);
        new Listcell(data.getGroupname()).setParent(li);
        if (data.getRating() != null) {
            new Listcell(RatingConst.getNameByRating(data.getRating())).setParent(li);
        } else {
            li.setStyle("background: #" + (data.isOpenComm() ? "99ff99" : "fff"));
        }
        final Listcell lcCheckKuts = new Listcell();
        lcCheckKuts.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent (Event event) throws Exception {
                if (data.isCheckKuts()) {
                    if (commService.setCheckKutsSrh(data.getIdSrh(), false)) {
                        data.setCheckKuts(false);
                        lcCheckKuts.setImage(null);
                    }
                } else {
                    if (commService.setCheckKutsSrh(data.getIdSrh(), true)) {
                        data.setCheckKuts(true);
                        lcCheckKuts.setImage("/imgs/okCLR.png");
                    }
                }
            }
        });
        lcCheckKuts.setImage(data.isCheckKuts() ? "/imgs/okCLR.png" : null);
        lcCheckKuts.setParent(li);
    }
}
