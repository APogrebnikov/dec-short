package org.edec.synchroMine.register.ctrl;

import org.edec.synchroMine.register.ctrl.renderer.RegisterLinkRenderer;
import org.edec.synchroMine.register.dao.request.LinkRegisterDaoRequest;
import org.edec.synchroMine.register.service.RegisterLinkService;
import org.edec.synchroMine.register.service.impl.RegisterLinkImpl;
import org.edec.utility.zk.IncludeSelector;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

public class RegisterLinksCtrl extends IncludeSelector {

    @Wire
    private Checkbox chOnlyUnlinked;
    @Wire
    private Intbox ibCourse, ibSemesterNumber, ibCourseMine, ibSemesterNumberMine;
    @Wire
    private Listbox lbRegisterLinks;
    @Wire
    private Textbox tbSubjectnameCabinet, tbSubjectnameMine, tbGroupname;

    private RegisterLinkService service = new RegisterLinkImpl();

    @Override
    protected void fill() {
        lbRegisterLinks.setItemRenderer(new RegisterLinkRenderer());
        findLinkRegister();
    }

    @Listen("onOK = #ibCourse; onOK = #ibSemesterNumber; onOK = #tbSubjectnameCabinet;" +
            "onOK = #tbSubjectnameMine; onOK = #tbGroupname; onCheck = #chOnlyUnlinked")
    public void findLinkRegister() {

        LinkRegisterDaoRequest request = new LinkRegisterDaoRequest();
        request.setCourse(ibCourse.getValue());
        request.setSemesterNumber(ibSemesterNumber.getValue());
        request.setCourseMine(ibCourseMine.getValue());
        request.setSemesterNumberMine(ibSemesterNumberMine.getValue());
        request.setGroupname(tbGroupname.getValue());
        request.setSubjectnameCabinet(tbSubjectnameCabinet.getValue());
        request.setSubjectnameMine(tbSubjectnameMine.getValue());
        request.setOnlyUnlinked(chOnlyUnlinked.isChecked());

        callLazyLoading(request);
    }

    private void callLazyLoading(LinkRegisterDaoRequest request) {
        Clients.showBusy(lbRegisterLinks, "Загрузка данных");
        Events.echoEvent("onLater", lbRegisterLinks, request);
    }

    @Listen("onLater = #lbRegisterLinks")
    public void fillListboxRegisterLinks(Event event) {
        lbRegisterLinks.setModel(new ListModelList<>(
                service.findLinkRegisterByRequest(
                        (LinkRegisterDaoRequest) event.getData())));
        lbRegisterLinks.renderAll();
        Clients.clearBusy(lbRegisterLinks);
    }
}
