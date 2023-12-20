package org.edec.commons.component;

import org.edec.commons.model.StudentGroupModel;
import org.edec.commons.service.SearchStudentService;
import org.edec.commons.service.impl.SearchStudentImpl;
import org.zkoss.composite.Composite;
import org.zkoss.composite.CompositeCtrls;
import org.zkoss.composite.Composites;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.Set;


@Composite(name = "lbsearchstudent", macroURI = "/WEB-INF/component/lbsearchstudent.zul")
public class LbSearchStudent extends Div implements IdSpace, ListitemRenderer<StudentGroupModel> {

    @Wire
    private Textbox tbSearchStudentFilter;
    @Wire
    private Listbox lbSearchStudent;

    private SearchStudentService searchStudentService = new SearchStudentImpl();

    private Long idInst;
    private Integer fos;

    public LbSearchStudent() {
        try {
            CompositeCtrls.register(LbSearchStudent.class, Executions.getCurrent().getDesktop().getWebApp());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        Composites.doCompose(this, null);
        lbSearchStudent.setItemRenderer(this);
    }

    @Listen("onOK = #tbSearchStudentFilter")
    public void searchStudents() {
        if (tbSearchStudentFilter.getValue().equals("")) {
            lbSearchStudent.getItems().clear();
        } else {
            Clients.showBusy(lbSearchStudent, "Загрузка данных");
            Events.echoEvent("onLater", lbSearchStudent, null);
        }
    }

    @Listen("onLater = #lbSearchStudent")
    public void laterSearchStudent() {
        lbSearchStudent.setModel(
                new ListModelList<>(searchStudentService.getStudentByFilter(tbSearchStudentFilter.getValue(), idInst, fos)));
        lbSearchStudent.renderAll();
        Clients.clearBusy(lbSearchStudent);
    }

    public void addListenersForSearchTextbox(String eventName, EventListener<Event> event) {
        tbSearchStudentFilter.addEventListener(eventName, event);
    }

    public void setImplementationOfService(SearchStudentService searchStudentService) {
        this.searchStudentService = searchStudentService;
    }

    public void setCheckmark(boolean checkmark) {
        lbSearchStudent.setCheckmark(checkmark);
    }

    public String getValueByFilter() {
        return tbSearchStudentFilter.getValue();
    }

    public Listitem getSelectedItem() {
        return lbSearchStudent.getSelectedItem();
    }

    public Set<Listitem> getSelectedItems() {
        return lbSearchStudent.getSelectedItems();
    }

    @Override
    public void render(Listitem li, StudentGroupModel data, int index) throws Exception {
        li.setValue(data);

        new Listcell(data.getFio()).setParent(li);
        new Listcell(data.getGroupname()).setParent(li);
    }

    public void setIdInst(Long idInst) {
        this.idInst = idInst;
    }

    public void setFos(Integer fos) {
        this.fos = fos;
    }
}
