package org.edec.passportGroup.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.passportGroup.model.GroupModel;
import org.edec.passportGroup.model.StudentModel;
import org.edec.passportGroup.model.SubjectModel;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.passportGroup.service.impl.PassportGroupServiceESO;
import org.edec.register.model.RetakeSubjectModel;
import org.edec.register.service.RetakeService;
import org.edec.register.service.impl.RetakeServiceImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.zkoss.zk.ui.Executions.getCurrent;

public class WinOpenPersonalRetakeCtrl extends SelectorComposer<Component> {

    public static final String SUBJECT = "subject";
    public static final String STUDENTS = "students";
    public static final String GROUP = "group";
    public static final String RUNNABLE_UPDATE = "runnable_update";
    public static final String TYPE_OF_RETAKE = "type_of_retake";

    @Wire
    private Datebox dateOfBeginRetake, dateOfEndRetake;

    @Wire
    private Button openRetake;

    @Wire
    private Window winOpenPersonalRetake;

    @Wire
    private  Label lStudents;
    @Wire
    private  Label cStudents;

    private TemplatePageCtrl template = new TemplatePageCtrl();

    private SubjectModel subject;
    private List<StudentModel> studentsList = new ArrayList<>();
    private GroupModel group;
    private int typeOfRetake;
    private List<org.edec.register.model.StudentModel> studentModelList = new ArrayList<>();
    private RetakeService retakeService = new RetakeServiceImpl();

    private Runnable updateGroupReport;

    private PassportGroupService service = new PassportGroupServiceESO();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        template.checkModuleByRole(getCurrent().getDesktop().getRequestPath(), getPage());

        subject = (SubjectModel) Executions.getCurrent().getArg().get(SUBJECT);
        studentsList = (List<StudentModel>) Executions.getCurrent().getArg().get(STUDENTS);
        group = (GroupModel) Executions.getCurrent().getArg().get(GROUP);
        updateGroupReport = (Runnable) Executions.getCurrent().getArg().get(RUNNABLE_UPDATE);

        typeOfRetake = (Integer) Executions.getCurrent().getArg().get(TYPE_OF_RETAKE);
        if (typeOfRetake == -2){
            winOpenPersonalRetake.setTitle("Открыть ведомость общей пересдачи");
        } else {
            winOpenPersonalRetake.setTitle("Открыть ведомость индивидуальной пересдачи");
        }

        RetakeSubjectModel retakeSubjectModel = new RetakeSubjectModel();
        retakeSubjectModel.setIdLGSS(subject.getIdLgss());
        retakeSubjectModel.setFocInt(subject.getFoc().getValue());
        retakeSubjectModel.setTypeRetake(typeOfRetake);

        for (StudentModel student : studentsList){
            org.edec.register.model.StudentModel studentModel = new org.edec.register.model.StudentModel();
            studentModel.setIdSSS(student.getIdSSS());
            studentModel.setIdSR(student.getIdSR());
            studentModel.setIdSemester(student.getIdSemester());
            studentModel.setType(student.getType());
            studentModel.setFamily(student.getFullName().split(" ")[0]);
            studentModel.setName(student.getFullName().split(" ")[1]);
            studentModel.setPatronymic(student.getFullName().split(" ").length > 2 ?
                                       student.getFullName().split(" ")[2]
                                       : "");

            studentModelList.add(studentModel);
        }
        List<org.edec.register.model.StudentModel> canceledStudents = new ArrayList<>();
        studentModelList = retakeService.getCheckedStudentsForOpenRetake(retakeSubjectModel, studentModelList, canceledStudents);

        if (!studentModelList.isEmpty()){
            String studListStr = studentModelList.stream().map(student -> student.getFio()).collect(Collectors.joining("\n"));
            lStudents.setValue("Для студентов: \n" + studListStr);
        }
        if (!canceledStudents.isEmpty()){
            String canceledListStr = canceledStudents.stream().map(student -> student.getFio()+" - "+student.getReason()).collect(Collectors.joining("\n"));
            cStudents.setValue("\nНе попадут: \n" + canceledListStr);
        }
        setCalendarDate();
    }

    public void setCalendarDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 3);

        dateOfBeginRetake.setValue(new Date());
        dateOfBeginRetake.setValue(new Date());
        dateOfEndRetake.setValue(cal.getTime());
        dateOfEndRetake.setValue(cal.getTime());
    }

    @Listen("onClick = #btnOpenRetake")
    public void openRetake() {
        if (studentModelList.isEmpty()){
            PopupUtil.showError("Нет подходящих для открытия пересдачи студентов!");
            winOpenPersonalRetake.detach();
            return;
        }
        if (dateOfBeginRetake.getValue() == null) {
            PopupUtil.showWarning("Не заполнены даты пересдач!");
            return;
        }

        if (dateOfBeginRetake.getValue().after(dateOfEndRetake.getValue())) {
            PopupUtil.showWarning("Дата начала комиссии не может назначаться позже даты конца комиссии!");
            return;
        }

        if (dateOfEndRetake.getValue().before(new Date())) {
            Messagebox.show("Открытые ведомости будут просроченными. Вы уверены?", "Внимание!", Messagebox.YES | Messagebox.NO,
                            Messagebox.QUESTION, event -> {
                        if (event.getName().equals(Messagebox.ON_YES)) {
                            if (!service.openRetake(subject, studentModelList, group, dateOfBeginRetake.getValue(), dateOfEndRetake.getValue(),
                                                    template.getCurrentUser().getFio(), typeOfRetake)) {
                                PopupUtil.showError("Открыть ведомость не удалось!");
                            } else {
                                PopupUtil.showInfo("Открытие ведомости прошло успешно!");
                            }
                            updateGroupReport.run();
                            winOpenPersonalRetake.detach();
                        }
                    }
            );

            return;
        }

        if (!service.openRetake(subject, studentModelList, group, dateOfBeginRetake.getValue(), dateOfEndRetake.getValue(),
                                template.getCurrentUser().getFio(), typeOfRetake
        )) {
            PopupUtil.showError("Открыть ведомость не удалось!");
        } else {
            PopupUtil.showInfo("Открытие ведомости прошло успешно!");
        }

        updateGroupReport.run();

        winOpenPersonalRetake.detach();
    }
}

