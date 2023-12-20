package org.edec.student.journalOrder.ctrl;

import org.edec.student.journalOrder.model.JournalOrderModel;
import org.edec.student.journalOrder.service.JournalOrderService;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.ListitemRenderer;


public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Listbox lbJournal;

    private JournalOrderService journalOrderService = new JournalOrderService();

    protected void fill () {
        template.setVisitedModuleByHum(template.getCurrentUser().getIdHum(), currentModule.getIdModule());
        lbJournal.setItemRenderer((ListitemRenderer<JournalOrderModel>) (li, data, index) -> {
            li.setValue(data);
            new Listcell(String.valueOf(index + 1)).setParent(li);
            new Listcell(data.getSemesterStr()).setParent(li);
            new Listcell(data.getGroupname()).setParent(li);
            new Listcell(data.getOrderNumber()).setParent(li);
            new Listcell(DateConverter.convertDateToString(data.getDateSignOrder())).setParent(li);
            new Listcell(data.getOrderType()).setParent(li);
            Listcell headDescription = new Listcell("Дата: с "+ DateConverter.convertDateToString(data.getFirstDate())
                    + (DateConverter.convertDateToString(data.getSecondDate()) != "" ? " по " + DateConverter.convertDateToString(data.getSecondDate()) : "")
                    + "; "+"\n" + data.getHeadDescription());
            headDescription.setTooltiptext("Дата: с "+ DateConverter.convertDateToString(data.getFirstDate()) + (DateConverter.convertDateToString(data.getSecondDate()) != "" ? " по " + DateConverter.convertDateToString(data.getSecondDate()) : "")  + "; \n" + data.getHeadDescription());
            headDescription.setParent(li);
            Listcell sectionOrder = new Listcell(( data.getSummScolarship() != null ? " Сумма: " + data.getSummScolarship() +" (руб.);   " : " ") +" \n "+data.getSectionDescription() +". ");
            sectionOrder.setTooltiptext(( data.getSummScolarship() != null ? " Сумма: " + data.getSummScolarship()+" (руб.);" : "") +" \n "+data.getSectionDescription() +". ");
            sectionOrder.setParent(li);
        });
        lbJournal.setModel(new ListModelList<>(journalOrderService.getJournalByHum(template.getCurrentUser().getIdHum())));
        lbJournal.renderAll();
    }
}
