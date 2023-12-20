package org.edec.secretaryChair.ctrl;

import org.apache.commons.lang.time.DateUtils;

import org.edec.secretaryChair.model.CommissionDayModel;
import org.edec.secretaryChair.model.CommissionModel;
import org.edec.secretaryChair.service.ChooseCommissionDateService;
import org.edec.secretaryChair.service.SecretaryChairService;
import org.edec.secretaryChair.service.impl.ChooseCommissionDateImpl;
import org.edec.secretaryChair.service.impl.SecretaryChairImpl;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.converter.DateConverter;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class WinChoseCommissionTime extends SelectorComposer<Window> {

    static final String COMMISSION = "commission";

    //Компоненты
    @Wire
    private Label lSubjectname, lPeriodCommission;
    @Wire
    private Vbox vbCalendar, vbDayTime;

    //сервисы
    private ChooseCommissionDateService chooseDateService = new ChooseCommissionDateImpl();
    private ComponentService componentService = new ComponentServiceESOimpl();
    private SecretaryChairService chairService = new SecretaryChairImpl();

    private CommissionModel commission;
    private Date today = new Date();

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        commission = (CommissionModel) Executions.getCurrent().getArg().get(COMMISSION);
        fill();
    }

    private void fill() {

        lSubjectname.setValue(commission.getSubjectName());
        lPeriodCommission.setValue(DateConverter.convertDateToString(commission.getDateBegin()) + " - "
                + DateConverter.convertDateToString(commission.getDateEnd()));

        List<CommissionDayModel> weeksIncludePeriodCommission = chooseDateService.getWeeksIncludePeriodCommission(commission);

        componentService.createHboxForFillWeekDay().setParent(vbCalendar);

        int count = 0;
        for (CommissionDayModel day : weeksIncludePeriodCommission) {
            if (count == 0) {
                componentService.createHboxWithFlex().setParent(vbCalendar);
            }
            ++count;
            createHboxDay(day).setParent(vbCalendar.getChildren().get(vbCalendar.getChildren().size() - 1));
            if (count == 7) {
                count = 0;
            }
        }
    }

    private Hbox createHboxDay(CommissionDayModel day) {
        Hbox hboxDay;
        if (DateUtils.isSameDay(day.getDay(), today)) {
            hboxDay = componentService.createHboxWithLabel(DateConverter.convertDateToDayString(day.getDay()), "day today");
        } else {
            hboxDay = componentService.createHboxWithLabel(DateConverter.convertDateToDayString(day.getDay()), "day");
        }

        switch (day.getType()) {
            case NONE_IN_PERIOD:
                hboxDay.setStyle("background: #ccc;");
                break;
            case CLOSE_DAY:
                hboxDay.setStyle("background: #ff9494;");
                break;
            case FREE_DAY:
                hboxDay.setStyle("background: #95FF82;");
                break;
            case FREE_TIME:
                hboxDay.setStyle("background: #FFEF70");
                break;
        }

        hboxDay.addEventListener(Events.ON_CLICK, event -> {
            selectDay(day);
        });

        return hboxDay;
    }

    private void selectDay(CommissionDayModel day) {
        vbDayTime.getChildren().clear();

        new Label("Был выбран день: " + DateConverter.convertDateToString(day.getDay())).setParent(vbDayTime);
        switch (day.getType()) {
            case NONE_IN_PERIOD:
                new Label("Нельзя проводить комиссии в этот день").setParent(vbDayTime);
                break;
            case CLOSE_DAY:
                new Label("Нет свободного времени").setParent(vbDayTime);
                break;
            case FREE_DAY:
            case FREE_TIME:
                new Label("Выберите время для комиссии:").setParent(vbDayTime);
                createItemsByFreePeriods(day);
                break;
        }
    }

    private void createItemsByFreePeriods(CommissionDayModel day) {

        Map<Integer, List<Date>> freeIntervalsByDate = chairService.getFreeIntervalByCommissionDay(day);
        for (List<Date> dates : freeIntervalsByDate.values()) {
            Button btnFreeInterval = new Button(
                    DateConverter.convertTimeToString(dates.get(0)) + " - " + DateConverter.convertTimeToString(dates.get(1)));
            btnFreeInterval.setParent(vbDayTime);
            btnFreeInterval.addEventListener(Events.ON_CLICK, event -> Events.echoEvent("onSelectDate", getSelf(), dates.get(0)));
        }
    }

}
