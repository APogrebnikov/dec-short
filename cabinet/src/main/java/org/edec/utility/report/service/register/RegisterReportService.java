package org.edec.utility.report.service.register;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.newOrder.model.report.ProtocolComissionerModel;
import org.edec.register.manager.RegisterManager;
import org.edec.register.service.RegisterService;
import org.edec.register.service.impl.RegisterServiceImpl;
import org.edec.teacher.model.GroupModel;
import org.edec.teacher.model.register.RegisterModel;
import org.edec.teacher.model.register.RegisterRowModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.report.manager.RegisterReportDAO;
import org.edec.utility.report.model.register.RegisterJasperModel;
import org.edec.utility.report.model.register.StudentModel;

import java.util.ArrayList;
import java.util.List;


public class RegisterReportService {
    private RegisterReportDAO registerReportDAO = new RegisterReportDAO();

    public JRBeanCollectionDataSource getBeanData(Long idRegister, Long idHum, Long idInst, Long idSem) {
        return getBeanData(idRegister, idHum, idInst, idSem, null);
    }

    public JRBeanCollectionDataSource getBeanData(Long idRegister, Long idHum, Long idInst, Long idSem, Boolean isDeducted) {
        RegisterJasperModel register = registerReportDAO.getRegisterReportModel(idRegister, idHum);
        FormOfControlConst foc = FormOfControlConst.getName(register.getFormOfControl());

        if (idInst != null && idSem != null) {
            RegisterService registerService = new RegisterServiceImpl();
            String allTeacherForRegister = registerService.getAllTeacherForRegister(idRegister);
            if (!allTeacherForRegister.equals("")) {
                register.setTeacher(allTeacherForRegister);
            }
        }
        String registerNumber = register.getRegisterNumber() == null ? "" : register.getRegisterNumber();
        if (register.getRetakeCount() == 2 || register.getRetakeCount() == 4
                || register.getRetakeCount() == -2 || register.getRetakeCount() == -4) {
            register.setTypeOfRegister("ВЕДОМОСТЬ ПЕРЕСДАЧИ № " + registerNumber);
        } else {
            if (foc == FormOfControlConst.EXAM) {
                register.setTypeOfRegister("ЭКЗАМЕНАЦИОННАЯ ВЕДОМОСТЬ № " + registerNumber);
            } else {
                register.setTypeOfRegister("ЗАЧЕТНАЯ ВЕДОМОСТЬ № " + registerNumber);
            }
        }

        if (foc == FormOfControlConst.CP) {
            register.setCoWorkOrProj("КУРСОВОЙ ПРОЕКТ");
            register.setRegtype("курсовой проект");
        } else if (foc == FormOfControlConst.CW) {
            register.setCoWorkOrProj("КУРСОВАЯ РАБОТА");
            register.setRegtype("курсовая работа");
        } else if (foc == FormOfControlConst.EXAM) {
            register.setCoWorkOrProj("");
            register.setRegtype("экзамен");
        } else if (foc == FormOfControlConst.PASS) {
            register.setCoWorkOrProj("");
            register.setRegtype("зачет");
        } else if (foc == FormOfControlConst.PRACTIC) {
            register.setCoWorkOrProj("");
            register.setRegtype("практика");
        }

        if (foc == FormOfControlConst.EXAM || foc == FormOfControlConst.PRACTIC) {
            register.setDateOfExaminationTitle("ДАТА СДАЧИ ЭКЗАМЕНА");
        } else {
            register.setDateOfExaminationTitle("ДАТА СДАЧИ ЗАЧЕТА");
        }

        List<StudentModel> students = null;
        if (isDeducted != null && isDeducted.equals(true)) {
            students = registerReportDAO.getStudentByRegisterWithDeducted(idRegister);
        } else {
            students = registerReportDAO.getStudentByRegister(idRegister);
        }
        register.setStudents(students);

        //studentA - отличник, B - ударник, C - УДОВЛ. , D - неУДОВЛ., E - не явка
        int studentA = 0, studentB = 0, studentC = 0, studentD = 0, studentE = 0;
        for (StudentModel student : students) {
            int rating = student.getRating();
            if (rating == 1 || rating == 5) {
                studentA++;
            } else if (rating == 4) {
                studentB++;
            } else if (rating == 3) {
                studentC++;
            } else if (rating == 2 || rating == -2) {
                studentD++;
            } else if (rating == -3) {
                studentE++;
            }
        }

        String marksCount = "";
        if (foc == FormOfControlConst.PASS && register.getType() == 0) {
            if (studentA + studentD + studentE == students.size()) {
                marksCount = "ЗАЧТЕНО: " + String.valueOf(studentA) + " НЕ АТТЕСТОВАНО: " + String.valueOf(studentE + studentD);
            } else {
                marksCount = "ЗАЧТЕНО:\tНЕ АТТЕСТОВАНО:  ";
            }
        } else {
            if (studentA + studentB + studentC + studentD + studentE == students.size()) {
                marksCount = "ОТЛИЧНО: " + studentA + " ХОРОШО: " + studentB + " УДОВЛ.: " + studentC + " НЕУД.: " + studentD +
                        " НЕ АТТЕСТОВАНО: " + studentE;
            } else {
                marksCount = "ОТЛИЧНО:    ХОРОШО:    УДОВЛ.:    НЕУД.:    НЕ АТТЕСТОВАНО: ";
            }
        }
        register.setMarksCount(marksCount);

        // Правки для нового формата ведомости от 30.10.2022
        register.setRegisterNumber(registerNumber);
        String shortTeacher = "";
        String[] subStr = register.getTeacher().split(" ");
        if (subStr.length > 2) {
            shortTeacher = subStr[1].substring(0, 1) + "." + subStr[2].substring(0, 1) + ". " + subStr[0];
        } else {
            shortTeacher = subStr[1].substring(0, 1) + ". " + subStr[0];
        }
        register.setTeacher(shortTeacher);

        // Изменение проверяющего в зависимости от формы обучения
        // Зашиваем ФИО
        if (register.getFormofstudy().equals(1)){
            register.setReceiver("Т.В. Липунова");
        } else {
            register.setReceiver("О.Г. Фатеева");
        }

        // Заглушка, пока не определится формат
        register.setDirection(register.getSpeciality());

        // Заглушка для множественного представления учителей
        List<ProtocolComissionerModel> comissionerList = new ArrayList<>();
        List<ProtocolComissionerModel> teachers = registerReportDAO.getTeachersByRegister(idRegister);
        for (ProtocolComissionerModel protocolComissionerModel : teachers) {
            String shortFio = "";
            String[] subStrFio = protocolComissionerModel.getFio().split(" ");
            if (subStrFio.length > 2) {
                shortFio = subStrFio[1].substring(0, 1) + "." + subStrFio[2].substring(0, 1) + ". " + subStrFio[0];
            } else {
                shortFio = subStrFio[1].substring(0, 1) + ". " + subStrFio[0];
            }
            protocolComissionerModel.setShortFio(shortFio);
            comissionerList.add(protocolComissionerModel);
        }
        register.setComissionerList(comissionerList);

        List<RegisterJasperModel> data = new ArrayList<>();
        data.add(register);
        return new JRBeanCollectionDataSource(data);
    }

    public JRBeanCollectionDataSource getBeanDataWithoutMarks(int formOfControl, GroupModel curGroup, RegisterModel mainRegister) {
        RegisterJasperModel register = new RegisterJasperModel();
        register.setFormOfControl(formOfControl);
        register.setSubject(curGroup.getSubject().getSubjectname());
        register.setGroupname(curGroup.getGroupname());
        register.setCourse(curGroup.getCourse().toString());
        register.setTotalHours(curGroup.getHoursCount() + "(" + curGroup.getHoursCount() / 36 + "зач.ед.)");

        register.setSemester(curGroup.getSemesterNumber().toString());

        for (RegisterRowModel ratingModel : mainRegister.getListRegisterRow()) {
            StudentModel studentModel = new StudentModel();

            studentModel.setFio(ratingModel.getStudentFullName());
            studentModel.setRecordBook(ratingModel.getRecordbookNumber());

            register.getStudents().add(studentModel);
        }

        ArrayList<RegisterJasperModel> list = new ArrayList<>();
        list.add(register);

        return new JRBeanCollectionDataSource(list);
    }
}
