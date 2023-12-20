package org.edec.studentPassport.ctrl.renderer;

import org.edec.main.model.ModuleModel;
import org.edec.studentPassport.ctrl.IndexPageCtrl;
import org.edec.studentPassport.ctrl.WinPersonalStudentCtrl;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.studentPassport.service.StudentPassportService;
import org.edec.studentPassport.service.impl.StudentPassportServiceESOimpl;
import org.edec.utility.pdfViewer.model.PdfViewer;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.HashMap;
import java.util.Map;

public class StudentRenderer implements ListitemRenderer<StudentStatusModel> {
    private IndexPageCtrl indexPageCtrl;
    private ModuleModel currentModule;

    private StudentPassportService studentPassportService = new StudentPassportServiceESOimpl();

    public StudentRenderer(IndexPageCtrl indexPageCtrl, ModuleModel currentModule) {
        this.indexPageCtrl = indexPageCtrl;
        this.currentModule = currentModule;
    }

    @Override
    public void render(Listitem li, final StudentStatusModel data, final int index) throws Exception {
        li.setValue(data);

        new Listcell(data.getFamily()).setParent(li);
        new Listcell(data.getName()).setParent(li);
        new Listcell(data.getPatronymic()).setParent(li);
        new Listcell(data.getRecordBook()).setParent(li);
        new Listcell(data.getGroupname()).setParent(li);
        new Listcell(String.valueOf(data.getCourse())).setParent(li);
        new Listcell("").setParent(li);
        new Listcell("", data.getAcademicLeave() ? "/imgs/okCLR.png" : "").setParent(li);
        new Listcell("", data.getDeducted() ? "/imgs/okCLR.png" : "").setParent(li);

        if (!currentModule.isReadonly()) {
            li.addEventListener(Events.ON_CLICK, event -> {
                Map<String, Object> arg = new HashMap<>();
                arg.put(WinPersonalStudentCtrl.STUDENT_MODEL, data);
                arg.put(WinPersonalStudentCtrl.INDEX_ELEMENT, index);
                arg.put(WinPersonalStudentCtrl.INDEX_PAGE, indexPageCtrl);

                ComponentHelper.createWindow("winPersonalStudent.zul", "winPersonalStudent", arg).doModal();
            });
            Listcell lcIndexCard = new Listcell("", "/imgs/pdf.png");
            lcIndexCard.setParent(li);
            lcIndexCard.addEventListener(Events.ON_CLICK, event -> new PdfViewer(
                    studentPassportService.generateIndexCard(data.getMineId(), data.getGroupname()),
                    "Учетная карточка студента " + data.getFio() + ", " + data.getGroupname())
                    .showPdf());
        }
    }
}
