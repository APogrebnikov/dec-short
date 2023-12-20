package org.edec.schedule.model.xls;

import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;

import java.util.ArrayList;
import java.util.List;

@Data
public class TimeClasses {

    private Cell timeClasses;

    private List<WeekClasses> weekClasses = new ArrayList<>();
}
