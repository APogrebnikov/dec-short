package org.edec.secretaryChair.service;

import org.edec.secretaryChair.model.CommissionDayModel;
import org.edec.secretaryChair.model.CommissionModel;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ChooseCommissionDateService {

    List<CommissionDayModel> getWeeksIncludePeriodCommission(CommissionModel commission);

    public Map<Integer, List<Date>> getFreeIntervalByCommissionDay(CommissionDayModel commissionDay);
}
