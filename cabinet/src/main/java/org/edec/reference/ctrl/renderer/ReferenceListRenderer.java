package org.edec.reference.ctrl.renderer;

import org.edec.reference.model.ReferenceModel;
import org.edec.utility.constants.ReferenceType;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.pdfViewer.ctrl.PdfViewerCtrl;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ReferenceListRenderer implements ListitemRenderer<ReferenceModel> {

    @Override
    public void render (Listitem item, ReferenceModel data, int index) throws Exception {
        item.setValue(data);

        Listcell refCell = new Listcell();
        Listcell appCell = new Listcell();

        if (data.getUrl() != null && !data.getUrl().equals("")) {
            createButtonShow(refCell, data, "reference");
            createButtonShow(appCell, data, "application");
        }

        item.appendChild(new Listcell(data.getBooknumber()));

        String type = "";
        if (data.getRefType() == ReferenceType.INDIGENT.getValue()) {
            type = "УСЗН";
        } else if (data.getRefType() == ReferenceType.ORPHAN.getValue()) {
            type = "Сирота";
        } else if (data.getRefType() == ReferenceType.INVALID.getValue()) {
            type = "Об инвалидности";
        } else if (data.getRefType() == ReferenceType.VETERAN.getValue()) {
            type = "Ветеран";
        }

        item.appendChild(new Listcell(type));
        item.appendChild(new Listcell(DateConverter.convertDateToString(data.getDateStart())));
        item.appendChild(new Listcell(DateConverter.convertDateToString(data.getDateFinish())));
        item.appendChild(refCell);
        item.appendChild(appCell);
    }

    private void createButtonShow (Listcell listcell, ReferenceModel reference, String fileName) {
        Button btnShow = new Button("", "/imgs/pdf.png");
        btnShow.addEventListener(Events.ON_CLICK, event -> {
            Map arg = new HashMap();
            if (reference.getUrl() != null) {
                arg.put(PdfViewerCtrl.FILE, new FileManager().getFileByRelativePath(reference.getUrl() + File.separator + fileName + ".pdf"));
                ComponentHelper.createWindow("/utility/pdfViewer/index.zul", "winPdfViewer", arg).doModal();
            }
        });
        btnShow.setHeight("100%");
        btnShow.setParent(listcell);
    }
}
