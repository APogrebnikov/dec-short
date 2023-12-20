package org.edec.student.schedule.ctrl.renderer;

import org.edec.student.schedule.model.ScheduleModel;
import org.edec.student.schedule.model.ScheduleSubjectModel;
import org.zkoss.zul.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleStudentsRenderer implements ListitemRenderer<ScheduleModel> {

    private Vbox fillListCell(List<ScheduleSubjectModel> dayOfWeek) {
        Vbox vbDay = new Vbox();
        Label lSubjectname = new Label();
        Label lTeacher = new Label();
        Label lRoom = new Label();
        for (ScheduleSubjectModel subject : dayOfWeek){

            lSubjectname.setValue(subject.getSubjectname());

            if(subject.getLesson()){
                lTeacher.setValue(subject.getTeacher() + " (Лек.)") ;
            } else {
                lTeacher.setValue(subject.getTeacher() + " (Прак.)") ;
            }
            lSubjectname.setStyle("font-weight: bold; ");
            vbDay.appendChild(lSubjectname);
            vbDay.appendChild(lTeacher);
            if(subject.getRoom()!=null){
                lRoom.setValue(subject.getRoom());
                vbDay.appendChild(lRoom);
            }
            vbDay.setTooltiptext(lSubjectname.getValue() +", \n" + lTeacher.getValue());

            vbDay.setVflex("100%");
            vbDay.setHflex("100%");
            vbDay.setPack("center");
            vbDay.setAlign("center");
            //vbDay.setStyle("position: absolute; top: 2px;");
        }
        return vbDay;
    }

    @Override
    public void render(Listitem li, ScheduleModel data, int i) throws Exception {
        Listcell lcTime = new Listcell();
        Listcell lcMonday = new Listcell();
        Listcell lcTuesday = new Listcell();
        Listcell lcWednesday = new Listcell();
        Listcell lcThursday = new Listcell();
        Listcell lcFriday = new Listcell();
        Listcell lcSaturday = new Listcell();

        lcTime.setLabel(data.getTimeName());

        Calendar cal = Calendar.getInstance();
        cal.setTime(data.getSelectDate());
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                lcMonday.setStyle("background: #73ff91;");
                break;
            case Calendar.TUESDAY:
                lcTuesday.setStyle("background: #73ff91;");
                break;
            case Calendar.WEDNESDAY:
                lcWednesday.setStyle("background: #73ff91;");
                break;
            case Calendar.THURSDAY:
                lcThursday.setStyle("background: #73ff91;");
                break;
            case Calendar.FRIDAY:
                lcFriday.setStyle("background: #73ff91;");
                break;
            case Calendar.SATURDAY:
                lcSaturday.setStyle("background: #73ff91;");
                break;
        }

        Vbox monday = fillListCell(data.getMonday());
        Vbox tuesday = fillListCell(data.getTuesday());
        Vbox wednesday = fillListCell(data.getWednesday());
        Vbox thursday = fillListCell(data.getThursday());
        Vbox friday = fillListCell(data.getFriday());
        Vbox saturday = fillListCell(data.getSaturday());

        lcMonday.appendChild(monday);
        lcTuesday.appendChild(tuesday);
        lcWednesday.appendChild(wednesday);
        lcThursday.appendChild(thursday);
        lcFriday.appendChild(friday);
        lcSaturday.appendChild(saturday);

        li.appendChild(lcTime);
        li.appendChild(lcMonday);
        li.appendChild(lcTuesday);
        li.appendChild(lcWednesday);
        li.appendChild(lcThursday);
        li.appendChild(lcFriday);
        li.appendChild(lcSaturday);
        li.setStyle("height: 60px");
    }

}
