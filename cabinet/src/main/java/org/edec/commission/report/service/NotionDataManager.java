package org.edec.commission.report.service;

import org.apache.commons.lang3.StringUtils;
import org.edec.commission.report.dao.CommissionReportDAO;
import org.edec.commission.report.model.notion.NotionDto;
import org.edec.commission.report.model.notion.NotionFilter;
import org.edec.commission.report.model.notion.NotionStudentModel;

import java.util.ArrayList;
import java.util.List;

public class NotionDataManager {

    private CommissionReportDAO commissionReportDAO = new CommissionReportDAO();

    public List<NotionStudentModel> getNotionsModelByFilter(NotionFilter notionFilter) {

        List<NotionDto> notionDtos = commissionReportDAO.getNotionDtoModel(notionFilter);

        List<NotionStudentModel> students = new ArrayList<>();

        for (NotionDto dto : notionDtos) {

            NotionStudentModel student = students.stream()
                    .filter(tmpStudent -> StringUtils.equals(dto.getFio(), tmpStudent.getStudentFio())
                            && StringUtils.equals(dto.getGroupName(), tmpStudent.getGroupName()))
                    .findFirst().orElse(null);

            if (student == null) {
                student = dto.toNotionStudentModel();
                students.add(student);
            }

            student.getDebts().add(dto.toNotionStudentDebtModel());
        }

        return students;
    }
}
