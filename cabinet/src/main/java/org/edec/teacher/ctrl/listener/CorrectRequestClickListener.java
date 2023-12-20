package org.edec.teacher.ctrl.listener;

import lombok.extern.log4j.Log4j;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.teacher.model.register.RegisterRowModel;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

/**
 * Created by apogrebnikov on 12.11.18.
 */
@Log4j
public class CorrectRequestClickListener implements EventListener<Event> {
    private RegisterRowModel data;

    private UserModel currentUser = (UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER);

    public CorrectRequestClickListener(RegisterRowModel data) {
        this.data = data;
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (data.getCurCorrectRequest() == null) {
            PopupUtil.showWarning("Сначала введите оценку!");
        } else {
            new JasperReportService().getJasperReportCorrectRating(data.getCurCorrectRequest()).showPdf();
        }

    }
}
