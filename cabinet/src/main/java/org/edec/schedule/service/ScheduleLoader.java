package org.edec.schedule.service;

public interface ScheduleLoader<INPUT, OUTPUT> {

    OUTPUT findScheduleByParam(INPUT param);
}
