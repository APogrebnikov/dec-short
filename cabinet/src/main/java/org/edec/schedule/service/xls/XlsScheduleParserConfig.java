package org.edec.schedule.service.xls;

import lombok.NonNull;
import org.edec.schedule.model.xls.XlsHolderScheduleContainer;
import org.edec.schedule.service.ScheduleLoader;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class XlsScheduleParserConfig {

    //Сервис
    private ScheduleLoader<String, List<XlsHolderScheduleContainer>> loader = new HtmlXlsScheduleLoader();

    //Переменные
    private List<XlsHolderScheduleContainer> holders;
    private XlsHolderScheduleContainer selectedHolder;

    public XlsScheduleParserConfig(String institute) {
        holders = getHoldersByInstitute(institute);
        if (holders.isEmpty()) {
            throw new IllegalArgumentException("Не удалось найти расписание");
        }
        selectedHolder = holders.get(0);
    }

    public void setHolders(@NonNull @NotEmpty List<XlsHolderScheduleContainer> holders) {
        this.holders = holders;
    }

    public List<XlsHolderScheduleContainer> getHoldersByInstitute(@NonNull String institute) {
        return loader.findScheduleByParam(institute);
    }

    public List<XlsHolderScheduleContainer> getHolders() {
        return holders;
    }

    public void setSelectedHolder(@NonNull XlsHolderScheduleContainer selectedHolder) {
        this.selectedHolder = selectedHolder;
    }

    public XlsHolderScheduleContainer getSelectedHolder() {
        return selectedHolder;
    }
}
