package org.edec.reference.service.impl;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.edec.reference.manager.ReferenceManager;
import org.edec.reference.model.ExcelReportModel;
import org.edec.reference.model.ReferenceModel;
import org.edec.reference.model.StudentModel;
import org.edec.reference.service.ReferenceService;
import org.edec.utility.constants.ReferenceType;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.fileManager.FileModel;
import org.zkoss.util.media.Media;
import org.zkoss.zul.Filedownload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ReferenceServiceImpl implements ReferenceService {

    ReferenceManager manager = new ReferenceManager();

    @Override
    public List<StudentModel> getStudents (String studentFio, Long idInst) {
        return manager.getStudents(studentFio, idInst);
    }

    @Override
    public List<ReferenceModel> getReferences (long idStudentcard) {
        return manager.getReferences(idStudentcard);
    }

    @Override
    public boolean updateStudentStatus (boolean isInvalid, boolean isIndigent, boolean isOrphan, boolean isVeteran, int typeInvalid, long idStudentcard) {
        return manager.updateStudentStatus(idStudentcard, isOrphan, isInvalid, isVeteran, typeInvalid, isIndigent);
    }

    @Override
    public boolean updateStudentDateOfBirth (long idHumanface, Date dateOfBirth) {
        return manager.updateStudentDateOfBirth(idHumanface, dateOfBirth);
    }

    @Override
    public Long createReference (ReferenceModel reference) {
        return manager.createReference(reference);
    }

    @Override
    public boolean updateReference (ReferenceModel reference) {
        return manager.updateReference(reference);
    }

    @Override
    public boolean deleteReference (long idRef) {
        return manager.deleteReference(idRef);
    }

    @Override
    public String createFiles (ReferenceModel reference, long idInst, Media mediaFileRefScan, Media mediaFileApplicationScan) {
        FileManager fileManager = new FileManager();
        FileModel.SubTypeDocument subFolder;

        if (reference.getRefType() == ReferenceType.INDIGENT.getValue()) {
            subFolder = FileModel.SubTypeDocument.INDIGENT;
        } else if (reference.getRefType() == ReferenceType.VETERAN.getValue()) {
            subFolder = FileModel.SubTypeDocument.VETERAN;
        } else {
            subFolder = FileModel.SubTypeDocument.INVALID;
        }

        FileModel fileModel = new FileModel(FileModel.Inst.getInstById(idInst), FileModel.TypeDocument.SOCIAL_REFERENCE,
                subFolder, reference.getIdSemester(), String.valueOf(reference.getIdRef())
        );

        fileModel.setFormat("pdf");

        if (mediaFileRefScan != null) {
            fileManager.createFile(fileModel, mediaFileRefScan.getByteData(), "reference");
        }

        if (mediaFileApplicationScan != null) {
            fileManager.createFile(fileModel, mediaFileApplicationScan.getByteData(), "application");
        }

        return fileManager.getRelativePath(fileModel);
    }

    @Override
    public void deleteFiles (ReferenceModel reference) {
        if (reference.getUrl() != null && !reference.getUrl().equals("")) {
            FileManager fileManager = new FileManager();
            fileManager.deleteFolderWithFiles(new File(fileManager.getFullPath(reference.getUrl())));
        }
    }

    private static HSSFCellStyle createStyleForTitle (HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    @Override
    public void writeIntoExcel () throws IOException {
        HSSFWorkbook report = new HSSFWorkbook();
        HSSFSheet sheet = report.createSheet("Справки");
        List<ExcelReportModel> list = manager.getExcelReport();

        int rowNum = 0;
        Cell cell;
        Row row;

        CellStyle cellStyle = report.createCellStyle();
        CreationHelper createHelper = report.getCreationHelper();

        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));
        HSSFCellStyle style = createStyleForTitle(report);
        row = sheet.createRow(rowNum);

        //fio
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("ФИО");
        cell.setCellStyle(style);
        //groupname
        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Группа");
        cell.setCellStyle(style);
        //regNumber
        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Номер справки");
        cell.setCellStyle(style);
        //typeOfReference
        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Тип справки");
        cell.setCellStyle(style);
        //dateStart
        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Дата начала действия");
        cell.setCellStyle(style);
        //dateFinish
        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Дата окончания действия");
        cell.setCellStyle(style);
        //social
        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Социальная стипендия");
        cell.setCellStyle(style);
        //additional social
        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("Социальная ДПС");
        cell.setCellStyle(style);

        //DATA

        Collections.sort(list, Comparator.comparing(ExcelReportModel::getFio));

        for (ExcelReportModel excRep : list) {
            rowNum++;
            row = sheet.createRow(rowNum);

            //fio
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(excRep.getFio());
            //groupname
            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(excRep.getGroupname());
            //regNumber
            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(excRep.getRegNumber());
            //typeOfReference
            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(excRep.getIdReferenceSubtype());
            //dateStart
            cell = row.createCell(4, CellType.STRING);
            if (excRep.getDateStart() == null) {
                cell.setCellValue("");
            } else {
                cell.setCellValue(DateConverter.convertDateToString(excRep.getDateStart()));
                cell.setCellStyle(cellStyle);
            }
            //dateFinish
            cell = row.createCell(5, CellType.STRING);
            if (excRep.getDateFinish() == null) {
                cell.setCellValue("");
            } else {
                cell.setCellValue(DateConverter.convertDateToString(excRep.getDateFinish()));
                cell.setCellStyle(cellStyle);
            }
            //social and additional social
            if (excRep.getOrderType() != null) {
                if (excRep.getOrderType().equals("socialScholarship")) {
                    cell = row.createCell(6, CellType.STRING);
                    cell.setCellValue(DateConverter.convertDateToString(excRep.getFirstDate()) + " - " +
                                      DateConverter.convertDateToString(excRep.getSecondDate()));
                } else if (excRep.getOrderType().equals("socialIncreasedScholarship")) {
                    cell = row.createCell(7, CellType.STRING);
                    cell.setCellValue(DateConverter.convertDateToString(excRep.getFirstDate()) + " - " +
                                      DateConverter.convertDateToString(excRep.getSecondDate()));
                }
            }
        }
        report.close();

        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }

        String fn = "StudentsExcelReport.xls";
        File file = new File(fn);
        FileOutputStream outFile = new FileOutputStream(file);
        report.write(outFile);
        Filedownload.save(file, null);
    }
}
