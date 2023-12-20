package org.edec.chairsRegisters.ctrl.renderer;

import org.edec.chairsRegisters.model.ChairsRegisterModel;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.Calendar;
import java.util.Date;

public class ChairsComissionRegisterRenderer implements ListitemRenderer<ChairsRegisterModel> {
    @Override
    public void render(Listitem li, ChairsRegisterModel data, int i) throws Exception {
        li.appendChild(new Listcell(data.getSubjectname()));
        li.appendChild(new Listcell(data.getGroupname()));

        Listcell lcFioTeachers = new Listcell(data.getFioTeachers());
        lcFioTeachers.setTooltiptext(data.getFioTeachers());
        li.appendChild(lcFioTeachers);

        li.appendChild(new Listcell(data.getFoc()));

        Listcell lcSignDate = new Listcell();
        if (data.getSigndate() == null) {
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
            JasperReportService jasperReport = new JasperReportService();
            jasperReport.getJasperReportRegister(data.getIdRegister(), data.getIdSemester(), data.getIdInstitute(), data.getFos())
                    .showPdf();
        });
        lcPdf.appendChild(btnPdf);
        li.appendChild(lcPdf);

        Listcell lcComissionDate = new Listcell();
        if (data.getComissionDate() != null) {
            lcComissionDate.setLabel(DateConverter.convertDateToStringByFormat(data.getComissionDate(), "dd.MM.yyyy"));
        } else {
            lcComissionDate.setLabel("-");
        }
        li.appendChild(lcComissionDate);

        if ((data.getComissionDate() != null && new Date().after(getOverdueDate(data.getComissionDate())))
                && (data.getCertnumber() == null || data.getSigndate() == null)) {
            li.setStyle("background : #ff9494;  text-align : center; height : 60px");
        } else if (data.getCertnumber() != null) {
            li.setStyle("background : #99FF99; text-align : center; height : 60px");
        } else {
            li.setStyle(" text-align : center; height : 60px");
        }

    }

    private Date getOverdueDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }
}
