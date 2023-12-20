package org.edec.schedule.model.xls;

import org.apache.poi.ss.usermodel.Workbook;
import org.edec.schedule.model.ScheduleContainer;
import org.edec.utility.constants.QualificationConst;

public class XlsScheduleContainer implements ScheduleContainer<Workbook> {

    private Integer course;
    private String url;

    private QualificationConst qualification;
    private Workbook workbook;

    public XlsScheduleContainer(Integer course, String url, QualificationConst qualification, Workbook workbook) {
        this.course = course;
        this.url = url;
        this.qualification = qualification;
        this.workbook = workbook;
    }

    @Override
    public Integer course() {
        return this.course;
    }

    public String url() {
        return url;
    }

    @Override
    public QualificationConst qualification() {
        return this.qualification;
    }

    @Override
    public Workbook scheduleData() {
        return this.workbook;
    }
}
