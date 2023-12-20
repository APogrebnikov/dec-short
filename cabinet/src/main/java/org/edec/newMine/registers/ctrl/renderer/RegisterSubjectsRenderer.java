package org.edec.newMine.registers.ctrl.renderer;

import org.edec.newMine.registers.model.RegistersSubjectsModel;
import org.edec.newMine.registers.model.StudentsRatingModel;
import org.edec.newMine.registers.service.RegisterService;
import org.edec.newMine.registers.service.impl.RegisterServiceImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.List;

public class RegisterSubjectsRenderer implements ListitemRenderer<RegistersSubjectsModel> {
    Listbox lbRatings;

    private RegisterService service = new RegisterServiceImpl();


    public RegisterSubjectsRenderer (Listbox lbRating){
        this.lbRatings = lbRating;
    }

    @Override
    public void render(Listitem li, RegistersSubjectsModel data, int i) throws Exception {
        li.setValue(data);
        new Listcell(i+1 + ". "+data.getSubjectname()).setParent(li);
        new Listcell(data.getFocByRegisterType()).setParent(li);
        li.addEventListener(Events.ON_RIGHT_CLICK, event -> {
            PopupUtil.showError("id ведмости " +data.getIdMineRegister() +
                    " id предмета " +data.getOtherIdSubject() +
                    " тип ведомости " + data.getTypeMineRegister());
        });

        li.addEventListener(Events.ON_CLICK, event -> {
            if (data.getIdMineRegister() != null && data.getOtherIdSubject() != null && data.getTypeMineRegister() != 0) {
                List<StudentsRatingModel> studentsRatingList = service.getStudentsAndMarks(data.getOtherIdSubject(), data.getTypeMineRegister(), data.getIdMineRegister());
                //PopupUtil.showError(String.valueOf(studentsRatingList.size()));
                if (studentsRatingList.size() != 0) {
                    lbRatings.setModel(new ListModelList<>(studentsRatingList));
                    lbRatings.setItemRenderer(new StudentsRatingsRenderer());
                    lbRatings.renderAll();
                } else {
                    lbRatings.getItems().clear();
                    lbRatings.setEmptyMessage("Не найдены оценки по этому предмету. Возможно день сдачи еще не наступил!");
                }
            } else {
                lbRatings.setEmptyMessage("Ведомость не найдена");
            }
        });
    }
}
