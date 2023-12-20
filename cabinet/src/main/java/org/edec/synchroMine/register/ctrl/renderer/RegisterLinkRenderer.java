package org.edec.synchroMine.register.ctrl.renderer;

import org.edec.commons.entity.dec.mine.LinkRegister;
import org.edec.synchroMine.register.service.RegisterLinkService;
import org.edec.synchroMine.register.service.impl.RegisterLinkImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

public class    RegisterLinkRenderer implements ListitemRenderer<LinkRegister> {

    private RegisterLinkService registerLinkService = new RegisterLinkImpl();

    @Override
    public void render(Listitem li, LinkRegister data, int index) throws Exception {

        li.setValue(data);

        new Listcell(data.getSubjectnameCabinet()).setParent(li);

        Listcell lcSubjectMine = new Listcell();
        lcSubjectMine.setParent(li);

        Textbox tbSubjectMine = new Textbox(data.getSubjectnameMine());
        tbSubjectMine.setParent(lcSubjectMine);
        tbSubjectMine.setHflex("1");
        tbSubjectMine.addEventListener(Events.ON_OK, event -> {
            data.setSubjectnameMine(tbSubjectMine.getValue());
            updateRegisterLink(data);
        });

        new Listcell(data.getGroupname()).setParent(li);
        new Listcell(String.valueOf(data.getCourse())).setParent(li);
        new Listcell(String.valueOf(data.getSemesterNumber())).setParent(li);

        Listcell lcCourseMine = new Listcell();
        lcCourseMine.setParent(li);

        Intbox ibCourseMine = new Intbox();
        if (data.getCourseMine() != null) {
            ibCourseMine.setValue(data.getCourseMine());
        }
        ibCourseMine.setParent(lcCourseMine);
        ibCourseMine.setHflex("1");
        ibCourseMine.addEventListener(Events.ON_CLICK, event -> {
            data.setCourseMine(ibCourseMine.getValue());
            updateRegisterLink(data);
        });

        Listcell lcSemesterNumberMine = new Listcell();
        lcSemesterNumberMine.setParent(li);

        Intbox ibSemesterNumberMine = new Intbox();
        if (data.getSemesterNumberMine() != null) {
            ibSemesterNumberMine.setValue(data.getSemesterNumberMine());
        }
        ibSemesterNumberMine.setParent(lcSemesterNumberMine);
        ibSemesterNumberMine.setHflex("1");
        ibSemesterNumberMine.addEventListener(Events.ON_CLICK, event -> {
           data.setSemesterNumberMine(ibSemesterNumberMine.getValue());
           updateRegisterLink(data);
        });
    }

    private void updateRegisterLink(LinkRegister data) {
        registerLinkService.saveSubjectnameMineForLinkRegister(data);
        PopupUtil.showInfo("Название обновлено");
    }
}
