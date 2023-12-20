package org.edec.secretaryChair.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.edec.secretaryChair.manager.EntityManagerSecretaryChair;
import org.edec.secretaryChair.model.CommissionDayModel;
import org.edec.secretaryChair.model.CommissionModel;
import org.edec.secretaryChair.service.ChooseCommissionDateService;
import org.edec.utility.converter.DateConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChooseCommissionDateImpl implements ChooseCommissionDateService {

    private EntityManagerSecretaryChair emSecretary = new EntityManagerSecretaryChair();

    @Override
    public List<CommissionDayModel> getWeeksIncludePeriodCommission(CommissionModel commission) {

        List<Date> weeksIncludeCommissionPeriod = DateConverter.getDateRangeByTwoDates(
                DateConverter.getMondayOfWeekByDate(commission.getDateBegin()),
                DateConverter.getSundayOfWeekByDate(commission.getDateEnd())
        );

        List<CommissionModel> commissionByStudent = emSecretary.getCommissionByDate(commission.getId());

        List<CommissionDayModel> commissionDays = new ArrayList<>();

        for (Date day : weeksIncludeCommissionPeriod) {

            CommissionDayModel dayModel = new CommissionDayModel(day);
            commissionDays.add(dayModel);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(day);

            if (day.before(commission.getDateBegin()) || day.after(commission.getDateEnd())
                    || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                continue;
            }

            List<CommissionModel> findCommissionsInDay = commissionByStudent.stream()
                    //Ищем комиссии на текущий день
                    .filter(commissionModel -> DateUtils.isSameDay(day, commissionModel.getCommissionDate()))
                    .collect(Collectors.toList());

            if (findCommissionsInDay.size() == 0) {
                dayModel.setType(CommissionDayModel.DayType.FREE_DAY);
                continue;
            }else {
                dayModel.setType(CommissionDayModel.DayType.CLOSE_DAY);
                continue;
            }
//            dayModel.setBusyTimes(findCommissionsInDay.stream()
//                    //Если текущая комиссия и найденаая комиссия физ. кул-ры, то их добавлять не нужно
//                    .filter(commissionModel -> !(commissionModel.isPhysicalCulture() && commission.isPhysicalCulture()))
//                    .collect(Collectors.groupingBy(
//                            CommissionModel::getCommissionDate)) //Вытаскиваем уникальные commissionDate
//                    .keySet()
//                    .stream()
//                    .sorted()
//                    .collect(Collectors.toList()));
//
//            if (getFreeIntervalByCommissionDay(dayModel).size() == 0) {
//                dayModel.setType(CommissionDayModel.DayType.CLOSE_DAY);
//            }
//            dayModel.setType(CommissionDayModel.DayType.FREE_TIME);
        }

        return commissionDays;
    }

    @Override
    public Map<Integer, List<Date>> getFreeIntervalByCommissionDay(CommissionDayModel commissionDay) {

        //Ограничения по рабочему дню dateMin = 8:30, dateMax = 21:00
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(commissionDay.getDay());

        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);
        Date dateMin = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 0);
        Date dateMax = calendar.getTime();

        int count = 0;
        Map<Integer, List<Date>> mapDates = new HashMap<>();

        if (CollectionUtils.isEmpty(commissionDay.getBusyTimes())) {
            mapDates.put(++count, Arrays.asList(dateMin, dateMax));
        }

        Iterator<Date> iterator = commissionDay.getBusyTimes().iterator();

        while (iterator.hasNext()) {
            Date date = iterator.next();
            if (!DateUtils.isSameDay(date, dateMin)) {
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, 8);
                calendar.set(Calendar.MINUTE, 30);
                dateMin = calendar.getTime();
            }

            //Интервал, когда нельзя назначать комиссию (+-2 часа от комиссии)
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, -2);
            Date curDateMin = calendar.getTime();

            calendar.add(Calendar.HOUR_OF_DAY, 4);
            Date curDateMax = calendar.getTime();

            if (DateConverter.getMinuteOfDay(dateMin) <= DateConverter.getMinuteOfDay(curDateMin)) {
                mapDates.put(++count, Arrays.asList(dateMin, curDateMin));
            }
            dateMin = curDateMax;
            if (!iterator.hasNext() && DateConverter.getMinuteOfDay(curDateMax) <= DateConverter.getMinuteOfDay(dateMax)) {
                mapDates.put(++count, Arrays.asList(curDateMax, dateMax));
            }
        }
        return mapDates;
    }
}
