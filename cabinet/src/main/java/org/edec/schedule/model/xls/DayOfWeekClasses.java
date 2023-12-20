package org.edec.schedule.model.xls;

import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;

import java.util.ArrayList;
import java.util.List;

@Data
public class DayOfWeekClasses {

    private Cell dayOfWeek;

    private List<TimeClasses> timeClasses = new ArrayList<>();
}
