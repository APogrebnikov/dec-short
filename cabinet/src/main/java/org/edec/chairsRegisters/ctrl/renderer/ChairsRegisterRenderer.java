package org.edec.chairsRegisters.ctrl.renderer;

import org.edec.chairsRegisters.model.ChairsRegisterModel;
import org.edec.utility.DateUtility;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class ChairsRegisterRenderer implements ListitemRenderer<ChairsRegisterModel> {
    @Override
    public void render(Listitem li, ChairsRegisterModel data, int i) throws Exception {
        li.appendChild(new Listcell(data.getSubjectname()));
        li.appendChild(new Listcell(data.getGroupname()));

        Listcell lcFioTeachers = new Listcell(data.getFioTeachers());
        lcFioTeachers.setTooltiptext(data.getFioTeachers());
        li.appendChild(lcFioTeachers);

        li.appendChild(new Listcell(data.getFoc()));

        Listcell lcSignDate = new Listcell();
        if  (data.getSigndate() == null){
            lcSignDate.setLabel("-");
        } else {
            lcSignDate.setLabel(DateConverter.convertDateToStringByFormat(data.getSigndate(), "dd.MM.yyyy"));
        }
        li.appendChild(lcSignDate);
        li.appendChild(new Listcell(data.getRegisterNumber()));

        Listcell lcPdf = new Listcell();
        Button btnPdf = new Button();
        btnPdf.setImage("/imgs/pdf.png");
        btnPdf.addEventListener(Events.ON_CLICK, event -> {
            if (data.getIdRegister() == null || data.getIdSemester() == null
                    || data.getIdInstitute() == null){
                PopupUtil.showError("Невозможно просмотреть ведомость!");
            } else {
                JasperReportService jasperReport = new JasperReportService();
                jasperReport.getJasperReportRegister(data.getIdRegister(), data.getIdSemester(), data.getIdInstitute(), data.getFos())
                        .showPdf();
            }

        });
        lcPdf.appendChild(btnPdf);
        li.appendChild(lcPdf);

        Listcell lcExamPassDate = new Listcell();
        if (data.getExamdate() != null) {
            lcExamPassDate.setLabel(DateConverter.convertDateToStringByFormat(data.getExamdate(), "dd.MM.yyyy"));
        } else if (data.getPassdate() != null){
            lcExamPassDate.setLabel(DateConverter.convertDateToStringByFormat(data.getPassdate(), "dd.MM.yyyy"));
        } else {
            lcExamPassDate.setLabel("-");
        }
        li.appendChild(lcExamPassDate);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);

        if (data.getCertnumber() == null && data.getEndDate() != null
            && !DateUtility.isDayBelongsToPeriod(new Date(), data.getEndDate(), 4)
            && data.getEndDate().before(cal.getTime())
            && (data.getDateOfSecondSignEnd()==null || DateUtility.isAfterDate(new Date(), data.getDateOfSecondSignEnd()))) {
            li.setStyle("background: #FF7373");
        }
        else if (data.getCertnumber() != null) {
            if(data.getSigndate() != null && data.getCertnumber() != null && data.getCertnumber().equals("  ")) {
                li.setStyle("background: #bff0b6; text-align : center ; height : 60px"); //подписана без ЭЦП
            }else{
                li.setStyle("background : #99FF99; text-align : center ; height : 60px"); //подписана
            }
        } else {
            li.setStyle(" text-align : center ; height : 60px"); //открыта
        }

    }
    private Date getOverdueDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return  cal.getTime();
    }
}
