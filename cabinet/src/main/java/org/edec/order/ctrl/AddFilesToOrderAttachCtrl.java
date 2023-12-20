package org.edec.order.ctrl;

import org.edec.utility.zk.PopupUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.*;


public class AddFilesToOrderAttachCtrl extends SelectorComposer<Component> {
    public static final String MEDIAS = "medias";
    public static final String LABEL_COUNT_DOCUMENT = "label_count_doc";

    @Wire
    private Div divFound;

    @Wire
    private Label countDocument;

    @Wire
    private Window winAttachFiles;

    private List<Media> medias;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        countDocument = (Label) Executions.getCurrent().getArg().get(LABEL_COUNT_DOCUMENT);
        medias = (List<Media>) Executions.getCurrent().getArg().get(MEDIAS);

        if (medias != null) {
            fillDivFound(medias);
        }
    }

    @Listen("onUpload = #btnUpload")
    public void uploadFiles (UploadEvent event) {
        fillDivFound(new ArrayList<Media>(Arrays.asList(event.getMedias())));
    }

    private void fillDivFound (final List<Media> tempMedias) {
        for (Media media : tempMedias) {
            final Hbox hbFile = new Hbox();
            hbFile.setStyle("float: left; margin: 5px; background: #cccccc;");
            hbFile.setAttribute("data", media);
            hbFile.setParent(divFound);

            Label lFileName = new Label(media.getName());
            lFileName.setParent(hbFile);

            Button btnDel = new Button("", "/imgs/crossCLR.png");
            btnDel.setStyle("background: ccc;");
            btnDel.setParent(hbFile);
            btnDel.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
                @Override
                public void onEvent (Event event) throws Exception {
                    hbFile.getParent().removeChild(hbFile);
                    tempMedias.remove(hbFile.getAttribute("data"));
                }
            });
        }
    }

    @Listen("onClick = #btnSave")
    public void saveDocuments () {
        for (Component comp : divFound.getChildren()) {
            Media media = (Media) comp.getAttribute("data");
            medias.add(media);
        }

        countDocument.setValue(medias.size() + "");
        countDocument.setStyle(medias.size() == 0 ? "color:red;" : "color:green;");
        PopupUtil.showInfo("Файлы успешно добавлены.");
        winAttachFiles.detach();
    }
}
