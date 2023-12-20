package org.edec.passportGroup.ctrl.listener;

import org.edec.main.model.UserModel;
import org.edec.passportGroup.model.GroupModel;
import org.edec.passportGroup.model.RatingModel;
import org.edec.passportGroup.model.StudentModel;
import org.edec.passportGroup.model.SubjectModel;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.passportGroup.service.impl.PassportGroupServiceESO;
import org.edec.register.model.RetakeModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;

import java.util.List;


public class ChangeCmbMarkListener implements EventListener<Event> {
    private RatingModel ratingModel;
    private UserModel currentUser;
    private StudentModel studentModel;
    private Image imgPass;
    private List<SubjectModel> subjectModelList;
    private GroupModel groupModel;

    private Label lblMark;
    private Listcell lcMark;

    private PassportGroupService service = new PassportGroupServiceESO();

    public ChangeCmbMarkListener(RatingModel ratingModel, UserModel currentUser, StudentModel studentModel, Image imgPass,
                                 List<SubjectModel> subjectModelList, GroupModel groupModel, Label lblMark, Listcell lcMark) {
        this.ratingModel = ratingModel;
        this.currentUser = currentUser;
        this.studentModel = studentModel;
        this.imgPass = imgPass;
        this.subjectModelList = subjectModelList;
        this.groupModel = groupModel;
        this.lblMark = lblMark;
        this.lcMark = lcMark;
    }

    @Override
    public void onEvent(Event event) throws Exception {
        Combobox cmbMark = (Combobox) event.getTarget();

        cmbMark.setVisible(false);
        if (cmbMark.getSelectedIndex() != -1) { // если какой то элемент в комбобоксе был выбран
            int oldRating = ratingModel.getRating();

            // Сохраняем рейтинг из комбокса
            if (cmbMark.getSelectedIndex() == RatingConst.ZERO.getRating()) {
                ratingModel.setRating(RatingConst.ZERO.getRating());
            } else {
                ratingModel.setRating(RatingConst.getRatingByShortname(cmbMark.getSelectedItem().getValue().toString()));
            }

            boolean isDifPass = (ratingModel.getFoc() == FormOfControlConst.PASS) && cmbMark.getItemCount() == 7;

            // Устанавливаем новое значение в ячейке
            if (isDifPass ||
                ratingModel.getFoc() == FormOfControlConst.EXAM ||
                ratingModel.getFoc() == FormOfControlConst.PRACTIC ||
                ratingModel.getFoc() == FormOfControlConst.CW ||
                ratingModel.getFoc() == FormOfControlConst.CP) {

                switch (RatingConst.getDataByRating(ratingModel.getRating())) {
                    case ZERO:
                        lblMark.setValue("");
                        break;
                    case NOT_LEARNED:
                        lblMark.setValue(RatingConst.NOT_LEARNED.getShortname());
                        break;
                    default:
                        lblMark.setValue(cmbMark.getSelectedItem().getValue().toString());
                }

                lblMark.setVisible(true);
            } else {
                switch (RatingConst.getDataByRating(ratingModel.getRating())) {
                    case ZERO:
                        lblMark.setValue("");
                        lblMark.setVisible(true);
                        break;
                    case NOT_LEARNED:
                        ratingModel.setRating(RatingConst.NOT_LEARNED.getRating());
                        lblMark.setValue(RatingConst.NOT_LEARNED.getShortname());
                        lblMark.setVisible(true);
                        break;
                    case PASS:
                        ratingModel.setRating(RatingConst.PASS.getRating());
                        imgPass.setSrc("/imgs/okCLR.png");
                        imgPass.setHeight("15px");
                        imgPass.setVisible(true);
                        break;
                    case NOT_PASS:
                        ratingModel.setRating(RatingConst.NOT_PASS.getRating());
                        lblMark.setValue("");
                        lblMark.setVisible(true);
                        break;
                    case FAILED_TO_APPEAR:
                        ratingModel.setRating(RatingConst.FAILED_TO_APPEAR.getRating());
                        lblMark.setValue(RatingConst.FAILED_TO_APPEAR.getShortname());
                        lblMark.setVisible(true);
                }
            }

            int newRating = ratingModel.getRating();

            if (oldRating != newRating) {
                RetakeModel retakeModel = new RetakeModel();
                retakeModel.setIdSR(ratingModel.getIdSR());
                retakeModel.setType(ratingModel.getType());

                String subjectName = "";
                for (SubjectModel subject : subjectModelList) {
                    if (subject.getIdSubject().equals(ratingModel.getIdSubject())) {
                        subjectName = subject.getSubjectName();
                        break;
                    }
                }

                if (service.changeRating(retakeModel, FormOfControlConst.getName(ratingModel.getFoc().getValue()), newRating, oldRating,
                                         currentUser.getFio(), currentUser.getIdHum(),
                                         groupModel.getGroupName(), subjectName, studentModel.getFullName()
                )) {
                    PopupUtil.showInfo("Оценка успешно изменена!", "end_after");
                } else {
                    PopupUtil.showError("Изменить оценку не удалось!", "end_after");
                }
            }
        } else {
            lcMark.getChildren().get(0).setVisible(true);
        }
    }
}
