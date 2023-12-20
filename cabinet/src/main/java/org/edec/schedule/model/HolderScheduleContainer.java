package org.edec.schedule.model;

import java.util.List;

public interface HolderScheduleContainer {

    String holderName();

    List<? extends ScheduleContainer> schedules();
}
