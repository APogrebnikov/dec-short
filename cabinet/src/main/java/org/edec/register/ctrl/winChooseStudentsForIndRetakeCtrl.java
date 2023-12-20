package org.edec.register.ctrl;

import org.edec.register.model.RetakeSubjectModel;
import org.edec.register.model.StudentModel;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class winChooseStudentsForIndRetakeCtrl extends SelectorComposer<Component> {
    public static final String STUDENTS = "student";
    public static final String DATE_BEGIN = "dateOfBegin";
    public static final String DATE_END = "dateOfEnd";
    public static final String SUBJECT = "subject";
    public static final String TYPE_RETAKE = "typeRetake";

    RetakeService service = new RetakeServiceImpl();
    Date dateOfBegin, dateOfEnd;
    Integer typeRetake;
    RetakeSubjectModel subject;

    List<StudentModel> students = new ArrayList<>();

    @Wire
    private Listbox lbStudents;

    @Wire
    private Button btnOpenIndRetake;

    @Wire
    private Window winChooseStudentsForIndRetake;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        students = (List<StudentModel>) Executions.getCurrent().getArg().get(STUDENTS);
        dateOfBegin = (Date) Executions.getCurrent().getArg().get(DATE_BEGIN);
        dateOfEnd = (Date) Executions.getCurrent().getArg().get(DATE_END);
        typeRetake = (Integer) Executions.getCurrent().getArg().get(TYPE_RETAKE);
        subject = (RetakeSubjectModel) Executions.getCurrent().getArg().get(SUBJECT);

        List<String> fio = students.stream().map(el -> el.getFio()).collect(Collectors.toList());

        ListModelList<String> studentFio = new ListModelList<>(fio);
        studentFio.setMultiple(true);
        lbStudents.setCheckmark(true);
        lbStudents.setMultiple(true);
        lbStudents.setModel(studentFio);
        lbStudents.renderAll();
    }

    @Listen("onClick = #btnOpenIndRetake")
    public void openRetakeForChoosenStudent(){
        if (lbStudents.getSelectedItems().isEmpty()){
            PopupUtil.showError("Выберите хотя бы одного студента!");
        } else {
            List<StudentModel> studentsForOpenRetake = new ArrayList<>();

            for (Listitem item : lbStudents.getSelectedItems()){
                for (StudentModel studentModel : students){
                    if (item.getValue().equals(studentModel.getFio())){
                        StudentModel student = new StudentModel();
                        student.setFamily(item.getValue().toString().split(" ")[0]);
                        student.setName(item.getValue().toString().split(" ")[1]);
                        student.setPatronymic(item.getValue().toString().split(" ")[2]);
                        student.setIdSR(studentModel.getIdSR());
                        student.setIdSemester(studentModel.getIdSemester());
                        student.setType(studentModel.getType());
                        student.setIdSSS(studentModel.getIdSSS());

                        studentsForOpenRetake.add(student);
                    }

                }

            }
            students = service.getCheckedStudentsForOpenRetake(subject, studentsForOpenRetake);
            if (!students.isEmpty()){
                if (service.openRetake(subject, subject.getFocInt(), students, dateOfBegin, dateOfEnd, typeRetake)){
                    PopupUtil.showInfo("Ведомости открыты успешно!");
                    winChooseStudentsForIndRetake.detach();
                } else {
                    PopupUtil.showError("Не удалось открыть ведомость.");
                    winChooseStudentsForIndRetake.detach();
                }
            } else {
                PopupUtil.showError("Нет подходящих студентов для открытия ведомости.");
                winChooseStudentsForIndRetake.detach();
            }




        }
    }
}
