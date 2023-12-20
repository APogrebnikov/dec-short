package org.edec.teacher.ctrl;

import org.edec.main.model.UserModel;
import org.edec.teacher.ctrl.renderer.CourseBindingRenderer;
import org.edec.teacher.model.CourseHistoryModel;
import org.edec.teacher.model.EsoCourseModel;
import org.edec.teacher.model.GroupModel;
import org.edec.teacher.service.CompletionService;
import org.edec.teacher.service.impl.CompletionServiceImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import java.util.ArrayList;
import java.util.List;

public class WinCourseBindingCtrl extends SelectorComposer<Component> {

    public static final String USER = "user";
    public static final String ID_CURRENT_SEMESTER = "idCurSem";

    private UserModel currentUser;
    private Long idCurrentSemester;
    private List<CourseHistoryModel> listHistoryCourses;

    private CompletionService service = new CompletionServiceImpl();

    @Wire
    Window winCourseBinding;

    @Wire
    Listbox listCourses;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);

        currentUser = (UserModel) Executions.getCurrent().getArg().get(WinCourseBindingCtrl.USER);
        idCurrentSemester = (Long) Executions.getCurrent().getArg().get(WinCourseBindingCtrl.ID_CURRENT_SEMESTER);

        init();
    }

    private void init () {
        listCourses.getItems().clear();
        listCourses.clearSelection();

        listHistoryCourses = service.getAvailableCourses(currentUser.getIdHum(), idCurrentSemester);

        if (!listHistoryCourses.isEmpty()) {
            ListModelList coursesModels = new ListModelList<>(listHistoryCourses);
            coursesModels.setMultiple(true);
            listCourses.setModel(coursesModels);
            listCourses.setItemRenderer(new CourseBindingRenderer());
            listCourses.renderAll();
            listCourses.selectAll();
        }
    }

    @Listen("onClick = #btnAttach")
    public void attachCourses () {
        List<CourseHistoryModel> selectedCourses = new ArrayList<>();

        for (Listitem item : listCourses.getSelectedItems()) {
            selectedCourses.add(item.getValue());
        }

        if (selectedCourses.isEmpty()) {
            PopupUtil.showWarning("Выберите хотя бы один предмет!");
        } else {
            //Выбираем сначала группы, у которых необходимо сделать привязку, а затем и ЭОК
            for (CourseHistoryModel courseHistoryModel : selectedCourses) {
                for (GroupModel group : courseHistoryModel.getGroupList()) {
                    if (group.getSelected()) {
                        for (EsoCourseModel course : courseHistoryModel.getUsedCourses()) {
                            if (course.getChecked()) {
                                if (service.updateESOcourse(group.getIdLGSS(), course.getIdEsoCourse())) {
                                    PopupUtil.showInfo("Курсы были успешно привязаны, обновите страницу!");
                                    init();
                                } else {
                                    PopupUtil.showError("Привязать курсы повторно не удалось!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Listen("onClick = #btnClose")
    public void close () {
        winCourseBinding.detach();
    }
}



