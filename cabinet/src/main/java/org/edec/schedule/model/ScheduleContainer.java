package org.edec.schedule.model;

import org.edec.utility.constants.QualificationConst;

public interface ScheduleContainer<OUTPUT> {

    Integer course();
    QualificationConst qualification();

    OUTPUT scheduleData();
}
