package org.edec.utility.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum ReportCommissionConst {
    SCHEDULE("Расписание", true, false, false),
    NOTION("Представление директора", true, true, false),
    LIST_OF_STUDENT("Список студентов с дисциплинами (xls)", false, true, false),
    LIST_OF_STUDENT_BY_FOS("Общий список студентов", true, true, false),
    LIST_OF_STUDENT_BY_CHAIR("Список студентов по кафедрам", true, true, false);

    String typeOfReport;
    boolean pdf, docx, excel;

    @Override
    public String toString() {
        return this.typeOfReport;
    }

    public static List<ReportCommissionConst> getPdfReports() {
        return getReports(0);
    }

    public static List<ReportCommissionConst> getDocxReports() {
        return getReports(1);
    }

    public static List<ReportCommissionConst> getExcelReports() {
        return getReports(2);
    }

    /**
     * Получаем отчеты
     *
     * @param type 0 - pdf, 1 - docx, 3 - excel
     *
     * @return
     */
    private static List<ReportCommissionConst> getReports(int type) {
        List<ReportCommissionConst> reports = new ArrayList<>();
        for (ReportCommissionConst report : ReportCommissionConst.values()) {
            if (((type == 0) && report.isPdf()) || ((type == 1) && report.isDocx()) || ((type == 2) && report.isExcel())) {
                reports.add(report);
            }
        }
        return reports;
    }
}
