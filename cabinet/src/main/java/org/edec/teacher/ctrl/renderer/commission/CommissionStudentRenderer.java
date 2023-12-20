package org.edec.teacher.ctrl.renderer.commission;

import org.edec.teacher.model.commission.StudentModel;
import org.edec.teacher.service.CompletionService;
import org.edec.teacher.service.impl.CompletionServiceImpl;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.List;


public class CommissionStudentRenderer implements ListitemRenderer<StudentModel> {
    private CompletionService completionService = new CompletionServiceImpl();
    private ComponentService componentService = new ComponentServiceESOimpl();

    private Boolean isSigned;
    private Integer type;

    public CommissionStudentRenderer (Boolean isSigned, Integer type) {
        this.isSigned = isSigned;
        this.type = type;
    }

    @Override
    public void render (Listitem li, final StudentModel data, int index) throws Exception {
        li.setValue(data);
        componentService.createListcell(li, String.valueOf(index + 1), "width: 30px;", "", "");
        componentService.createListcell(li, data.getFio(), "", "", "");
        componentService.createListcell(li, data.getGroupname(), "text-align: center;", "", "");
        Listcell lcRating = new Listcell();
        lcRating.setParent(li);
        if (!isSigned) {
            final Combobox cmbRating = new Combobox();
            cmbRating.setParent(lcRating);
            cmbRating.setReadonly(true);
            cmbRating.setWidth("160px");
            new Comboitem("Введите оценку").setParent(cmbRating);
            List<RatingConst> ratingConsts = RatingConst.getRatingCommissionByType(type);
            for (RatingConst ratingConst : ratingConsts) {
                Comboitem ci = new Comboitem(ratingConst.getShortname());
                ci.setValue(ratingConst);
                ci.setParent(cmbRating);
            }
            if (data.getRating() == 0) {
                cmbRating.setSelectedIndex(0);
            } else {
                RatingConst ratingConst = RatingConst.getDataByRating(data.getRating());
                for (Comboitem ci : cmbRating.getItems()) {
                    if (ci.getLabel().equals(ratingConst.getShortname())) {
                        cmbRating.setSelectedItem(ci);
                        break;
                    }
                }
            }
            cmbRating.addEventListener(Events.ON_CLICK, event -> saveRating(cmbRating, data));
            cmbRating.addEventListener(Events.ON_BLUR, event -> saveRating(cmbRating, data));
        } else {
            new Label(data.getRatingStr()).setParent(lcRating);
        }
    }

    private void saveRating (Combobox cmbRating, StudentModel data) {
        Integer value = 0;
        RatingConst ratingConst = null;
        if (cmbRating.getSelectedItem() != null && cmbRating.getSelectedItem().getValue() != null) {
            ratingConst = cmbRating.getSelectedItem().getValue();
        }
        if (ratingConst != null) {
            value = ratingConst.getRating();
        }
        if (value != 0) {
            if (completionService.updateRating(value, data.getIdSRH())) {
                data.setRating(value);
            } else {
                PopupUtil.showError("Не удалось обновить оценку.");
            }
        }
    }
}
