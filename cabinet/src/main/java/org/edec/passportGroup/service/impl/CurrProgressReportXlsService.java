package org.edec.passportGroup.service.impl;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.edec.passportGroup.manager.CurProgressReportXlsManager;
import org.edec.passportGroup.model.passportGroupReport.curProgressXls.CurProgressGroupModel;
import org.edec.passportGroup.model.passportGroupReport.curProgressXls.CurProgressStudentModel;
import org.edec.passportGroup.model.passportGroupReport.curProgressXls.CurProgressSubjectModel;
import org.zkoss.util.media.AMedia;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class CurrProgressReportXlsService {

    private CurProgressReportXlsManager manager = new CurProgressReportXlsManager();
    private List<CurProgressGroupModel> groups = new ArrayList<>();

    public AMedia getReportCurProgressXls(List<Long> idLgsList) {
        fillGroups(idLgsList);

        Workbook book = new HSSFWorkbook();

        for (CurProgressGroupModel group : groups) {
            CurProgressStudentModel student = group.getStudents().get(0);
            Sheet sheet = book.createSheet(group.getGroupname().contains("/") ? group.getGroupname().replace("/", "|") : group.getGroupname());
            setHeaderForSheet(sheet, student.getSubjects());
            setRowForSheet(sheet, group.getStudents());
            setWidthColumn(sheet, student.getSubjects());
        }

        AMedia aMedia = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            book.write(bos);
            aMedia = new AMedia("Текущая успеваемость", "xls", "application/xls", bos.toByteArray());
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aMedia;
    }

    private void setHeaderForSheet(Sheet sheet, List<CurProgressSubjectModel> subjects) {

        CellRangeAddress mergeFioCell = new CellRangeAddress(0, 1, 0, 0);
        sheet.addMergedRegion(mergeFioCell);

        Row subjectRow = sheet.createRow(0);
        subjectRow.createCell(0).setCellValue("ФИО");
        int j = 0;
        for (int i = 1; i <= subjects.size() * 2; ) {
            CellRangeAddress mergeSubjectRow = new CellRangeAddress(0, 0, i, i + 1);
            sheet.addMergedRegion(mergeSubjectRow);
            if (j < subjects.size()) {
                CurProgressSubjectModel subject = subjects.get(j);
                subjectRow.createCell(i).setCellValue(subject.getSubjectname());
            }
            j++;
            i += 2;
        }

        Row attendanceAndProgress = sheet.createRow(1);
        for (int i = 1; i <= subjects.size() * 2; ) {
            attendanceAndProgress.createCell(i).setCellValue("Проп.");
            attendanceAndProgress.createCell(i + 1).setCellValue("Успев.");
            i += 2;
        }
        subjectRow.createCell(subjects.size() * 2 + 1).setCellValue("Всего: ");
        attendanceAndProgress.createCell(subjects.size() * 2 + 1).setCellValue("Проп.");
        attendanceAndProgress.createCell(subjects.size() * 2 + 2).setCellValue("Успев.");
        CellRangeAddress mergeTotalCell = new CellRangeAddress(0, 0, subjects.size() * 2 + 1, subjects.size() * 2 + 2);
        sheet.addMergedRegion(mergeTotalCell);
    }

    private void setRowForSheet(Sheet sheet, List<CurProgressStudentModel> students) {
        HSSFCellStyle cellStyle = (HSSFCellStyle) sheet.getWorkbook().createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());

        int sumTotalAttendByStudent = 0;
        int avgTotalProgressByStudent = 0;
        for (CurProgressStudentModel student : students) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            row.createCell(0).setCellValue(student.getFioStudent());
            int i = 1;
            for (CurProgressSubjectModel subject : student.getSubjects()) {
                row.createCell(i).setCellValue(subject.getAttend());
                row.createCell(i + 1).setCellValue(subject.getProgress() + " %");
                i += 2;
            }
            int totalAttendByStudent = student.getSubjects().stream().mapToInt(subject -> (subject.getAttend().intValue())).sum();
            Double totalProgressByStudent = student.getSubjects().stream().mapToInt(subject -> (subject.getProgress())).average().getAsDouble();

            Cell totalAttendByStudentCell = row.createCell(student.getSubjects().size() * 2 + 1);
            totalAttendByStudentCell.setCellValue(totalAttendByStudent);
            totalAttendByStudentCell.setCellStyle(cellStyle);

            Cell totalProgressByStudentCell = row.createCell(student.getSubjects().size() * 2 + 2);
            totalProgressByStudentCell.setCellValue(totalProgressByStudent.intValue() + " %");
            totalProgressByStudentCell.setCellStyle(cellStyle);
            sumTotalAttendByStudent = sumTotalAttendByStudent + totalAttendByStudent;
            avgTotalProgressByStudent = avgTotalProgressByStudent + totalProgressByStudent.intValue();
        }

        Row totalRow = sheet.createRow(sheet.getLastRowNum() + 1);
        totalRow.createCell(0).setCellValue("Итого: ");

        CurProgressStudentModel studentModel = students.get(0);
        int j = 0;
        for (int i = 0; i < studentModel.getSubjects().size(); i++) {

            int totalAttendBySybject = 0;
            int totalProgressBySubject = 0;
            for (CurProgressStudentModel student : students) {
                CurProgressSubjectModel subject = student.getSubjects().get(i);
                totalAttendBySybject = totalAttendBySybject + subject.getAttend().intValue();
                totalProgressBySubject = totalProgressBySubject + subject.getProgress();
            }

            Cell totalAttendBySybjectCell = totalRow.createCell(j + 1);
            totalAttendBySybjectCell.setCellValue(totalAttendBySybject);
            totalAttendBySybjectCell.setCellStyle(cellStyle);

            Cell totalProgressBySubjectCell = totalRow.createCell(j + 2);
            totalProgressBySubjectCell.setCellStyle(cellStyle);
            totalProgressBySubjectCell.setCellValue((totalProgressBySubject / students.size()) + " %");
            j += 2;
        }
        Cell sumTotalAttendByStudentCell = totalRow.createCell(j + 1);
        sumTotalAttendByStudentCell.setCellValue(sumTotalAttendByStudent);
        sumTotalAttendByStudentCell.setCellStyle(cellStyle);

        Cell avgTotalProgressByStudentCell = totalRow.createCell(j + 2);
        avgTotalProgressByStudentCell.setCellValue(avgTotalProgressByStudent / students.size() + " %");
        avgTotalProgressByStudentCell.setCellStyle(cellStyle);
    }

    private void setWidthColumn(Sheet sheet, List<CurProgressSubjectModel> subjects) {
        sheet.setColumnWidth(0, (int) (30 * 1.14388) * 256);
        for (int i = 1; i <= subjects.size(); i++) {
            sheet.setColumnWidth(i, (int) (13 * 1.14388) * 256);

        }

    }

    private void fillGroups(List<Long> listIdLgs) {
        try {
            groups = manager.getGroups(listIdLgs);
            groups.stream().forEach(groupModel -> groupModel.getStudents().addAll(manager.getStudentsByGroup(groupModel.getIdLgs())));
            for (CurProgressGroupModel group : groups) {
                group.getStudents().stream().forEach(studentModel -> studentModel.getSubjects().addAll(manager.getSubjects(group.getIdLgs(), studentModel.getIdSSS())));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
