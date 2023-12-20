package org.edec.report.debtorsReport.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.edec.report.debtorsReport.manager.DebtorReportManager;
import org.edec.report.debtorsReport.manager.MineDebtorManager;
import org.edec.report.debtorsReport.model.DebtorContactModel;
import org.edec.report.debtorsReport.model.DebtorReportModel;
import org.edec.report.xls.XlsReport;
import org.edec.report.xls.XlsReportColumn;

import java.util.*;
import java.util.stream.Collectors;

public class DebtorsReportService {
    public HSSFWorkbook getReportDebtorsBySemestersOrderByFio(List<Long> semesters) {
        DebtorReportManager manager = new DebtorReportManager();
        List<DebtorReportModel> debtors = manager.getDebtorsBySemesters(semesters);
        setContactInfo(debtors);
        List<DebtorReportModel> dividedDebtorsByFoc = getDividedModelByFoc(debtors, false);


        dividedDebtorsByFoc.sort(
                Comparator.comparing(DebtorReportModel::getGroupname)
                          .thenComparing(DebtorReportModel::getFio)
                          .thenComparing(DebtorReportModel::getIdSemester)
                          .thenComparing(DebtorReportModel::getSubjectname)
        );

        XlsReport<DebtorReportModel> xlsReport = new XlsReport<>(dividedDebtorsByFoc);
        xlsReport
                .addColumn("ФИО", "fio", 315, true)
                .addColumn("Группа", "groupname", 114)
                .addColumn("Предмет", "subjectname", 383)
                .addColumn("ФК", "foc", 73)
                .addColumn("ЗЕ", "ZE", 73)
                .addColumn("Кафедра", "subjectDepartment", 333)
                .addColumn("Семестр", "semester", 122)
                .addColumn("Преподаватель", "teachers", 314)
                .addColumn("Платник", "notBudget", 73)
                .addColumn("Долги", "countDebts", 52)
                .addColumn("Телефон", "phone", 150)
                .addColumn("email", "email", 150)
                .setColumnType("Долги", XlsReportColumn.TYPE_NUMBER);

        return xlsReport.generateReport();
    }

    public HSSFWorkbook getReportDebtorsBySemestersOrderByFioWithUnsignedRegisters(List<Long> semesters) {
        DebtorReportManager manager = new DebtorReportManager();
        List<DebtorReportModel> debtors = manager.getDebtorsBySemestersWithUnsignedRegisters(semesters);
        setContactInfo(debtors);
        List<DebtorReportModel> dividedDebtorsByFoc = getDividedModelByFoc(debtors, false);


        dividedDebtorsByFoc.sort(
                Comparator.comparing(DebtorReportModel::getGroupname)
                          .thenComparing(DebtorReportModel::getFio)
                          .thenComparing(DebtorReportModel::getIdSemester)
                          .thenComparing(DebtorReportModel::getSubjectname)
        );

        XlsReport<DebtorReportModel> xlsReport = new XlsReport<>(dividedDebtorsByFoc);
        xlsReport
                .addColumn("ФИО", "fio", 315, true)
                .addColumn("Группа", "groupname", 114)
                .addColumn("Предмет", "subjectname", 383)
                .addColumn("ФК", "foc", 73)
                .addColumn("ЗЕ", "ZE", 73)
                .addColumn("Кафедра", "subjectDepartment", 333)
                .addColumn("Семестр", "semester", 122)
                .addColumn("Преподаватель", "teachers", 314)
                .addColumn("Платник", "notBudget", 73)
                .addColumn("Долги", "countDebts", 52)
                .addColumn("Телефон", "phone", 150)
                .addColumn("email", "email", 150)
                .setColumnType("Долги", XlsReportColumn.TYPE_NUMBER);

        return xlsReport.generateReport();
    }

    public HSSFWorkbook getReportDebtorsBySemestersOrderByDepartment(List<Long> semesters) {
        DebtorReportManager manager = new DebtorReportManager();
        List<DebtorReportModel> debtors = manager.getDebtorsBySemesters(semesters);
        setContactInfo(debtors);
        List<DebtorReportModel> dividedDebtorsByFoc = getDividedModelByFoc(debtors, false);


        dividedDebtorsByFoc.sort(
                Comparator.comparing(DebtorReportModel::getSubjectDepartment)
                          .thenComparing(DebtorReportModel::getCountDebts)
                          .thenComparing(DebtorReportModel::getFio)
        );

        XlsReport<DebtorReportModel> xlsReport = new XlsReport<>(dividedDebtorsByFoc);
        xlsReport
                .addColumn("Кафедра", "subjectDepartment", 333)
                .addColumn("Предмет", "subjectname", 383)
                .addColumn("ФК", "foc", 73)
                .addColumn("ЗЕ", "ZE", 73)
                .addColumn("Семестр", "semester", 122)
                .addColumn("Преподаватель", "teachers", 314)
                .addColumn("Группа", "groupname", 114)
                .addColumn("Студент", "fio", 315)
                .addColumn("Платник", "notBudget", 73)
                .addColumn("Долги", "countDebts", 52)
                .addColumn("Телефон", "phone", 150)
                .addColumn("email", "email", 150)
                .setColumnType("Долги", XlsReportColumn.TYPE_NUMBER);

        return xlsReport.generateReport();
    }

    private void setContactInfo(List<DebtorReportModel> debtors) {
        List<Long> ids = debtors.stream().map(DebtorReportModel::getIdStudentMine).collect(Collectors.toList());
        List<DebtorContactModel> debtorsInfo = new MineDebtorManager().getContactInfoByListId(ids);

        for(DebtorContactModel contactModel : debtorsInfo) {
            debtors.stream().filter(
                    el -> Objects.equals(contactModel.getFio(), el.getFio())
                          && Objects.equals(contactModel.getIdStudentMine(), el.getIdStudentMine())
            ).forEach(el -> {
                el.setPhone(contactModel.getPhone());
                el.setEmail(contactModel.getEmail());
            });

        }
    }

    public HSSFWorkbook getReportDebtorsBySemestersOrderByDepartmentWithUnsignedRegisters(List<Long> semesters) {
        DebtorReportManager manager = new DebtorReportManager();
        List<DebtorReportModel> debtors = manager.getDebtorsBySemestersWithUnsignedRegisters(semesters);
        setContactInfo(debtors);
        List<DebtorReportModel> dividedDebtorsByFoc = getDividedModelByFoc(debtors, false);

        dividedDebtorsByFoc.sort(
                Comparator.comparing(DebtorReportModel::getSubjectDepartment)
                          .thenComparing(DebtorReportModel::getCountDebts)
                          .thenComparing(DebtorReportModel::getFio)
        );

        XlsReport<DebtorReportModel> xlsReport = new XlsReport<>(dividedDebtorsByFoc);
        xlsReport
                .addColumn("Кафедра", "subjectDepartment", 333)
                .addColumn("Предмет", "subjectname", 383)
                .addColumn("ФК", "foc", 73)
                .addColumn("ЗЕ", "ZE", 73)
                .addColumn("Семестр", "semester", 122)
                .addColumn("Преподаватель", "teachers", 314)
                .addColumn("Группа", "groupname", 114)
                .addColumn("Студент", "fio", 315)
                .addColumn("Платник", "notBudget", 73)
                .addColumn("Долги", "countDebts", 52)
                .addColumn("Телефон", "phone", 150)
                .addColumn("email", "email", 150)
                .setColumnType("Долги", XlsReportColumn.TYPE_NUMBER);

        return xlsReport.generateReport();
    }

    public List<DebtorReportModel> getDividedModelByFoc(List<DebtorReportModel> tempListStudentDebt, boolean openComm) {
        List<DebtorReportModel> result = new ArrayList<>();

        List<DebtorReportModel> examList = new ArrayList<>();
        List<DebtorReportModel> passList = new ArrayList<>();
        List<DebtorReportModel> cpList = new ArrayList<>();
        List<DebtorReportModel> cwList = new ArrayList<>();
        List<DebtorReportModel> practicList = new ArrayList<>();

        for (DebtorReportModel model : tempListStudentDebt) {
            if (model.getIs_exam() == 1 && (model.getExamrating()==0) && (!openComm || !model.getExamComm())) {
                DebtorReportModel newModel = createFOCmodel(model);
                newModel.setOpenComm(model.getExamComm());
                newModel.setFoc("Экзамен");
                examList.add(newModel);

            }
            if (model.getIs_pass() == 1 && (model.getPassrating()==0) && (!openComm || !model.getPassComm())) {
                DebtorReportModel newModel = createFOCmodel(model);
                newModel.setOpenComm(model.getPassComm());
                newModel.setFoc("Зачет");
                passList.add(newModel);
            }
            if (model.getIs_cp() == 1 && (model.getCourseprojectrating()==0) && (!openComm || !model.getCpComm())) {
                DebtorReportModel newModel = createFOCmodel(model);
                newModel.setOpenComm(model.getCpComm());
                newModel.setFoc("КП");
                if (model.getIs_exam() != 0 || model.getIs_pass() != 0){
                    newModel.setZE("0.0");
                }
                cpList.add(newModel);
            }
            if (model.getIs_cw() == 1 && (model.getCourseworkrating()==0) && (!openComm || !model.getCwComm())) {
                DebtorReportModel newModel = createFOCmodel(model);
                newModel.setOpenComm(model.getCwComm());
                newModel.setFoc("КР");
                if (model.getIs_exam() != 0 ||  model.getIs_pass() != 0){
                    newModel.setZE("0.0");
                }
                cwList.add(newModel);
            }
            if (model.getIs_practice() == 1 && model.getIs_pass() == 0 && (model.getPracticrating()==0) && (!openComm || !model.getPracticComm())) {
                DebtorReportModel newModel = createFOCmodel(model);
                newModel.setOpenComm(model.getPracticComm());
                newModel.setFoc("Практика");
                if (model.getIs_exam() != 0 || model.getIs_pass() != null){
                    newModel.setZE("0.0");
                }
                practicList.add(newModel);
            }
        }

        result.addAll(examList);
        result.addAll(passList);
        result.addAll(cpList);
        result.addAll(cwList);
        result.addAll(practicList);

        Collections.sort(result);

        return result;
    }

    private DebtorReportModel createFOCmodel (DebtorReportModel tempModel) {
        DebtorReportModel newModel = new DebtorReportModel();

        newModel.setSubjectDepartment(tempModel.getSubjectDepartment());
        newModel.setFoc(tempModel.getFoc());
        newModel.setTeachers(tempModel.getTeachers());
        newModel.setCountDebts(tempModel.getCountDebts());
        newModel.setSemester(tempModel.getSemester());
        newModel.setFio(tempModel.getFio());
        newModel.setIdSemester(tempModel.getIdSemester());
        newModel.setGroupname(tempModel.getGroupname());
        newModel.setSubjectname(tempModel.getSubjectname());
        newModel.setPhone(tempModel.getPhone());
        newModel.setEmail(tempModel.getEmail());
        newModel.setZE(tempModel.getZE());
        newModel.setNotBudget(tempModel.getNotBudget());

        return newModel;
    }

}
