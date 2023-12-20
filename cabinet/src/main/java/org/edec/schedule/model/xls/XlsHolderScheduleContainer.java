package org.edec.schedule.model.xls;

import org.edec.schedule.model.HolderScheduleContainer;

import java.util.ArrayList;
import java.util.List;

public class XlsHolderScheduleContainer implements HolderScheduleContainer {

    private String holderName;

    private List<XlsScheduleContainer> schedules;

    public XlsHolderScheduleContainer(String holderName) {
        this.holderName = holderName;
        this.schedules = new ArrayList<>();
    }

    @Override
    public String holderName() {
        return this.holderName;
    }

    @Override
    public List<XlsScheduleContainer> schedules() {
        return this.schedules;
    }

    @Override
    public String toString() {
        return holderName;
    }
}
