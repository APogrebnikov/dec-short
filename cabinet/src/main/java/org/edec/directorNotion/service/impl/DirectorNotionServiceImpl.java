package org.edec.directorNotion.service.impl;

import org.edec.commission.report.model.NotionMainModel;
import org.edec.commission.report.model.NotionModel;
import org.edec.directorNotion.manager.DirectorNotionManager;
import org.edec.directorNotion.model.StudentModel;
import org.edec.directorNotion.service.DirectorNotionService;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.service.jasperReport.JasperReportService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DirectorNotionServiceImpl implements DirectorNotionService {
    DirectorNotionManager manager = new DirectorNotionManager();

    @Override
    public List<StudentModel> getStudentsByFilter (String fio, String recordbook, String groupname) {
        return manager.getStudentByFilter(fio, recordbook, groupname);
    }

    @Override
    public void getDirectorNotion (boolean isPdf, List<StudentModel> students, String notionDate) {
        List<NotionMainModel> mainModels = new ArrayList<>();
        NotionMainModel model = new NotionMainModel();

        List<NotionModel> listToPrint = students.stream().map(student -> {
            NotionModel studentNotion = new NotionModel();
            studentNotion.setRecordbook(student.getRecordbook());
            studentNotion.setFio(student.getFio());
            studentNotion.setCourse(Integer.toString(student.getCourse()));
            studentNotion.setGroupname(student.getGroupname());
            studentNotion.setDirectionNumber(student.getDirectionNumber());
            studentNotion.setDirectionName(student.getDirectionName());
            studentNotion.setFormOfStudy(student.getFormOfStudy() == 1 ? "очная" : "очно-заочная");
            studentNotion.setDateNotion(notionDate);

            return studentNotion;
        }).collect(Collectors.toList());

        model.setNotions(listToPrint);
        mainModels.add(model);

        JasperReportService jasperReportService = new JasperReportService();
        JasperReport jasperReport = jasperReportService.printReportBranchOfContractNotion(mainModels);

        if (isPdf) {
            jasperReport.showPdf();
        } else {
            jasperReport.downloadDocx();
        }
    }
}
