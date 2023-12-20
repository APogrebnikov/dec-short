package org.edec.teacher.ctrl;

import lombok.extern.log4j.Log4j;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.teacher.ctrl.renderer.registerRequest.RegisterRequestHistoryRenderer;
import org.edec.teacher.ctrl.renderer.registerRequest.StudentRenderer;
import org.edec.teacher.model.GroupModel;
import org.edec.teacher.model.registerRequest.RegisterRequestModel;
import org.edec.teacher.model.registerRequest.StudentModel;
import org.edec.teacher.service.RegisterRequestService;
import org.edec.teacher.service.impl.RegisterRequestServiceImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.zkoss.zk.ui.Executions.getCurrent;

@Log4j
public class WinRegisterRequestCtrl extends SelectorComposer<Component> {

    @Wire
    private Window winRegisterRequest;

    @Wire
    private Button btnCreateRequest;

    @Wire
    private Listbox listboxStudents, listboxRegisterRequestHistory;

    @Wire
    private Checkbox filterUnderConsideration;

    private GroupModel curGroup;

    private int formOfControl;

    private List<StudentModel> listStudent;
    private List<RegisterRequestModel> listRegisterRequestHistory, filteredListRegisterRequestHistory;

    private RegisterRequestService service = new RegisterRequestServiceImpl();

    private TemplatePageCtrl template = new TemplatePageCtrl();

    private ListModelList<StudentModel> studentModels;
    private ListModelList<RegisterRequestModel> registerRequestModels;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        template.checkModuleByRole(getCurrent().getDesktop().getRequestPath(), getPage());

        curGroup = (GroupModel) Executions.getCurrent().getArg().get(WinRegisterCtrl.SELECTED_GROUP);
        formOfControl = (Integer) Executions.getCurrent().getArg().get(WinRegisterCtrl.FORM_CONTROL);

        init();
    }

    public void init() {
        listRegisterRequestHistory = service.getRegisterRequestHistory(template.getCurrentUser().getIdHum(), curGroup.getIdLGSS(),formOfControl);

        filterRequestHistory();
        fillStudents();
    }

    @Listen("onClick = #btnCreateRequest")
    public void sendRequest() {

        List<StudentModel> selectedStudents = new ArrayList<>();

        for (Listitem item : listboxStudents.getSelectedItems()) {
            selectedStudents.add(item.getValue());
        }

        if(selectedStudents.size()==0){
            PopupUtil.showWarning("Выберите хотя бы одного студента!");
            return;
        }

        for (StudentModel student : selectedStudents) {
            RegisterRequestModel requestModel = new RegisterRequestModel();

            requestModel.setIdHumanface(template.getCurrentUser().getIdHum());
            requestModel.setIdLgss(curGroup.getIdLGSS());
            requestModel.setStudent(student);
            requestModel.setApplyingDate(new Date());
            requestModel.setFoc(formOfControl);

            if (!service.sendRequest(requestModel)) {
                PopupUtil.showError("Отправить заявку не удалось!");
                return;
            }

            log.info("Преподаватель " + template.getCurrentUser().getFio() +
                     " отправил запрос на открытие индивидуальной пересдачи по предмету \"" + curGroup.getSubject().getSubjectname() +
                     "\" студенту " + student.getFio() + " из группы " + curGroup.getGroupname());
        }
        PopupUtil.showInfo("Заявка была успешно отправлена!");

        listRegisterRequestHistory = service.getRegisterRequestHistory(template.getCurrentUser().getIdHum(), curGroup.getIdLGSS(),formOfControl);
        filterRequestHistory();
        fillStudents();
    }

    @Listen("onClick = #filterUnderConsideration")
    public void filterRequestHistory() {
        if (filterUnderConsideration.isChecked()) {
            filteredListRegisterRequestHistory = service.filterRequestHistoryByStatus(listRegisterRequestHistory);
            fillRequestHistory(filteredListRegisterRequestHistory);
        } else {
            fillRequestHistory(listRegisterRequestHistory);
        }
    }

    public void fillStudents() {
        listStudent = service.getRequestAvailableStudents(curGroup.getIdLGSS(), formOfControl);
        listStudent = service.filterStudentsByRequests(listStudent, listRegisterRequestHistory);

        studentModels = new ListModelList<>(listStudent);
        studentModels.setMultiple(true);
        listboxStudents.setModel(studentModels);
        listboxStudents.setItemRenderer(new StudentRenderer());
        listboxStudents.renderAll();
    }

    public void fillRequestHistory(List<RegisterRequestModel> requestHistory) {
        registerRequestModels = new ListModelList<>(requestHistory);
        listboxRegisterRequestHistory.setModel(registerRequestModels);
        listboxRegisterRequestHistory.setItemRenderer(new RegisterRequestHistoryRenderer());
        listboxRegisterRequestHistory.renderAll();
    }

}
