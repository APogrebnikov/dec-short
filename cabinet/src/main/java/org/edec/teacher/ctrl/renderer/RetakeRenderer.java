package org.edec.teacher.ctrl.renderer;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.edec.teacher.ctrl.WinRegisterCtrl;
import org.edec.teacher.model.RetakeModel;
import org.edec.utility.DateUtility;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RegisterType;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RetakeRenderer implements ListitemRenderer<RetakeModel> {

    private ComponentService componentService = new ComponentServiceESOimpl();

    @Override
    public void render(Listitem li, RetakeModel data, int index) throws Exception {
        li.setValue(data);
        componentService.createListcell(li, String.valueOf(index + 1), "color: #000;", "", "");

        Listcell lcDate = new Listcell(data.getDateOfRetake()!=null? DateConverter.convertDateToStringByFormat(data.getDateOfRetake(), "dd.MM.yyyy"):"");
        lcDate.setParent(li);

        Listcell lcSubject = new Listcell(data.getSubject().getSubjectname());
        if (data.getFioStudent() != null) {
            lcSubject.setLabel(data.getSubject().getSubjectname() + " (" + data.getFioStudent()+")");
        } else {
            lcSubject.setLabel(data.getSubject().getSubjectname());
        }
        lcSubject.setParent(li);

        Listcell lcGroup = new Listcell(data.getGroup().getGroupname());
        lcGroup.setParent(li);

        componentService.createListcell(li, FormOfControlConst.getName(data.getSubject().getFormofcontrol()).getName(), "color: #000;", "", "");

        Listcell lcType = new Listcell(RegisterType.getRegisterTypeByRetakeCount(data.getRetakeCount()).getName());
        lcType.setParent(li);

        Listcell lcBtnRegister = new Listcell();
        Button btnRegister = new Button("Ведомость");
        btnRegister.setTooltiptext("Просмотреть Ведомость");
        btnRegister.setHoverImage("/imgs/docsCLR.png");
        btnRegister.setParent(lcBtnRegister);
        lcBtnRegister.setParent(li);


        // Подписана
        if (data.getSignDate() != null && data.getCertnumber() != null) {
            li.setStyle("background: #99FF99");
        }
        // Подписана без ЭЦП
        if (data.getSignDate() != null && data.getCertnumber() != null && data.getCertnumber().equals("  ")) {
            li.setStyle("background: #bff0b6");
        }
        if (data.getSignDate() == null || data.getCertnumber() == null ) {
            // продленные даты пересдачи
            if (data.getSecondDateBegin() != null && data.getSecondDateEnd() != null) {
                if (DateUtility.isBetweenDate(new Date(), data.getSecondDateBegin(), data.getSecondDateEnd())){
                    li.setStyle("background: #eedc82");// Не подписана
                }
                if (DateUtility.isAfterDate(new Date(), data.getSecondDateEnd())) {
                    li.setStyle("background: #FF7373");// Просрочена
                }
            } else {
                // основные даты пересдачи
                if (DateUtility.isBetweenDate(new Date(), data.getBegindate(), DateUtils.addDays(data.getEnddate(), 3))) {
                    li.setStyle("background: #eedc82");// Не подписана, но время есть (Дата+3 дня - это сейчас или позже)
                }
                if ( DateUtility.isAfterDate(new Date(), DateUtils.addDays(data.getEnddate(), 3))){
                    li.setStyle("background: #FF7373");// Просрочена
                }
            }
        }

        btnRegister.addEventListener(Events.ON_CLICK, event -> {
            Map arg = new HashMap();
            arg.put(WinRegisterCtrl.SELECTED_GROUP, data.getGroup());
            arg.put(WinRegisterCtrl.FORM_CONTROL, data.getFormofcontrol());
            if(data.getRetakeCount() == 2 || data.getRetakeCount() == -2)
            {
                arg.put(WinRegisterCtrl.TAB, 1);
            }

            if(data.getRetakeCount() == 4 || data.getRetakeCount() == -4)
            {
                arg.put(WinRegisterCtrl.TAB, 2);
            }

            ComponentHelper.createWindow("/teacher/winRegister.zul", "winRegister", arg).doModal();
        });
    }
}
