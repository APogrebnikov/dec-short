package org.edec.chairsRegisters.service.impl;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.edec.chairsRegisters.manager.ChairsRegisterManager;
import org.edec.chairsRegisters.model.ChairsDepartmentModel;
import org.edec.chairsRegisters.model.ChairsRegisterModel;
import org.edec.chairsRegisters.model.GroupBySemRegisterModel;
import org.edec.chairsRegisters.service.ChairsRegisterService;
import org.edec.utility.converter.DateConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChairsRegisterServiceImpl implements ChairsRegisterService {

    private ChairsRegisterManager manager = new ChairsRegisterManager();
    @Override
    public List<ChairsRegisterModel> getListMainRegisters(Long idDepartment, Long idSemester) {
        List<ChairsRegisterModel> listMainRegister = manager.getMainRegisters(idDepartment, idSemester);
        for (ChairsRegisterModel mainRegister : listMainRegister){
                mainRegister.setFioTeachers(manager.getTeachersByIdRegister(mainRegister.getIdLgss()));
                if (mainRegister.getFioTeachers() == null) {
                    mainRegister.setFioTeachers(mainRegister.getSignatorytutor());
                }
        }
        return listMainRegister;
    }

    @Override
    public List<ChairsRegisterModel> getListRetakeRegisters(Long idDepartment, Long idSemester) {
        List<ChairsRegisterModel> listRetakeRegister = manager.getRetakeRegister(idDepartment, idSemester);
        for (ChairsRegisterModel register : listRetakeRegister){
            register.setFioTeachers(manager.getTeachersByIdRegister(register.getIdLgss()));
        }
        return listRetakeRegister;
    }

    @Override
    public List<GroupBySemRegisterModel> getRetakeRegiseterByPeriod(Long idDepartment, Long idSemester, Date dateFrom, Date dateTo) {
        List<ChairsRegisterModel> modelList = manager.getRetakeRegisterForReport(idDepartment, idSemester, dateFrom, dateTo);
        for (ChairsRegisterModel register : modelList) {
            register.setFioTeachers(manager.getTeachersByIdRegister(register.getIdLgss()));
        }
        return groupBySemRegister(modelList);
    }

    public List<GroupBySemRegisterModel> groupBySemRegister (List<ChairsRegisterModel> listRegister) {
        List<GroupBySemRegisterModel> grouppedRegister =  new ArrayList<>();

        for (ChairsRegisterModel register : listRegister) {
            boolean addRegisterBySem = true;
            for (GroupBySemRegisterModel groupSemModel : grouppedRegister) {
                if (groupSemModel.getSem().equals(register.getSemesterStr())) {
                    groupSemModel.getListRegister().add(register);
                    addRegisterBySem = false;
                    break;
                }
            }
            if (addRegisterBySem) {
                GroupBySemRegisterModel model = new GroupBySemRegisterModel();
                model.setSem(register.getSemesterStr());
                model.getListRegister().add(register);
                grouppedRegister.add(model);
            }

        }
        return  grouppedRegister;
    }

    @Override
    public List<ChairsRegisterModel> getListComissionRegisters(Long idDepartment, Long idSemester) {
        List<ChairsRegisterModel> listComissionRegister = manager.getComissionRegisterList(idDepartment, idSemester);
        for (ChairsRegisterModel register : listComissionRegister){
            register.setFioTeachers(manager.getTeachersForComission(register.getIdComissionRegister()));
        }
        return listComissionRegister;
    }

    @Override
    public ChairsDepartmentModel getIdDepartment(Long idHumanface) {
        return manager.getIdDepartmentByIdHumanface(idHumanface);
    }

    @Override
    public int countUnsignRegister(List<ChairsRegisterModel> listRegister){
        int count = 0;
        for (ChairsRegisterModel register : listRegister){
            if (register.getCertnumber() == null){
                count ++;
            }
        }
        return  count;
    }

    @Override
    public Date getOverdueRetakeDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, 3);
        return  cal.getTime();
    }

    @Override
    public HSSFWorkbook getRetakeRegisterReportXLS(List<GroupBySemRegisterModel> listRetakeRegister) {
        HSSFWorkbook book = new HSSFWorkbook();

        for (GroupBySemRegisterModel semModel : listRetakeRegister) {
            HSSFSheet sheet = book.createSheet(semModel.getSem());
            HSSFRow rowTitle = sheet.createRow(0);

            HSSFCellStyle style = book.createCellStyle();
            style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);

            Cell subjectname = rowTitle.createCell(0);
            subjectname.setCellValue("Дисциплина");
            subjectname.setCellStyle(style);
            sheet.setColumnWidth(0, 10000);
            Cell groupname  = rowTitle.createCell(1);
            groupname.setCellValue("Группа");
            groupname.setCellStyle(style);
            sheet.setColumnWidth(1, 3500);
            Cell teachers  = rowTitle.createCell(2);
            teachers.setCellValue("Преподаватели");
            teachers.setCellStyle(style);
            sheet.setColumnWidth(2, 10000);
            Cell dateTo  = rowTitle.createCell(3);
            dateTo.setCellValue("Дата проведения");
            dateTo.setCellStyle(style);
            sheet.setColumnWidth(3, 6000);

            HSSFCellStyle styleCell = book.createCellStyle();
            styleCell.setBorderBottom(BorderStyle.THIN);
            styleCell.setBorderTop(BorderStyle.THIN);
            styleCell.setBorderRight(BorderStyle.THIN);
            styleCell.setBorderLeft(BorderStyle.THIN);
            styleCell.setWrapText(true);
            styleCell.setAlignment(HorizontalAlignment.LEFT);
            styleCell.setVerticalAlignment(VerticalAlignment.CENTER);

            int i = 1;
            for (ChairsRegisterModel regModel : semModel.getListRegister()) {
                HSSFRow row = sheet.createRow(i++);
                Cell subjectnameCell = row.createCell(0);
                subjectnameCell.setCellValue(regModel.getSubjectname());
                subjectnameCell.setCellStyle(styleCell);

                Cell groupnameCell  = row.createCell(1);
                groupnameCell.setCellValue(regModel.getGroupname());
                groupnameCell.setCellStyle(styleCell);

                Cell teachersCell  = row.createCell(2);
                teachersCell.setCellValue(regModel.getFioTeachers());
                teachersCell.setCellStyle(styleCell);

                Cell dateCell  = row.createCell(3);
                if (regModel.getSecondBeginDate() != null && regModel.getSecondEndDate() != null) {
                    dateCell.setCellValue(DateConverter.convertDateToStringByFormat(regModel.getSecondBeginDate(), "dd.MM.yyyy")
                            + " - " + DateConverter.convertDateToStringByFormat(regModel.getSecondEndDate(), "dd.MM.yyyy"));
                } else {
                    dateCell.setCellValue(DateConverter.convertDateToStringByFormat(regModel.getBeginDate(), "dd.MM.yyyy")
                            + " - " + DateConverter.convertDateToStringByFormat(regModel.getEndDate(), "dd.MM.yyyy"));
                }
                dateCell.setCellStyle(styleCell);



            }
        }


        return book;
    }

    @Override
    public Long getCurrentSem(int fos, Long idInst) {
        return manager.getCurrentSem(fos, idInst);
    }

    @Override
    public Date getOverdueMainDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return  cal.getTime();
    }


}
