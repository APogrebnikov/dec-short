package org.edec.order.ctrl;

import org.edec.order.model.OrderModel;
import org.edec.utility.fileManager.FileManager;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;


public class WinAttachNewFilesCtrl extends SelectorComposer<Component> {
    @Wire
    Button btnUpload;

    @Wire
    Listbox lbFiles;

    private OrderModel orderModel;
    private FileManager manager;
    private List<String> attachedFiles;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);

        manager = new FileManager();
        orderModel = (OrderModel) Executions.getCurrent().getArg().get(WinEditOrderCtrl.ORDER_MODEL);

        attachedFiles = new ArrayList<>();
        File[] files = manager.getAttachedFilesForOrder(orderModel);
        if (files != null) {
            Arrays.asList(files).forEach(file -> attachedFiles.add(file.getName()));
        }

        lbFiles.setItemRenderer((Listitem listitem, String file, int i) -> {
            new Listcell(file).setParent(listitem);
            Listcell lcDelete = new Listcell();
            lcDelete.setParent(listitem);
            Button btnDel = new Button("", "/imgs/del.png");
            btnDel.setParent(lcDelete);
            btnDel.addEventListener(Events.ON_CLICK, event -> {
                manager.removeAttachFileInOrder(orderModel, file);
                attachedFiles.remove(file);
                lbFiles.setModel(new ListModelList<>(attachedFiles));
                lbFiles.renderAll();
            });
        });

        lbFiles.setModel(new ListModelList<>(attachedFiles));
        lbFiles.renderAll();
    }

    @Listen("onUpload = #btnUpload")
    public void onUploadFiles (UploadEvent event) {
        List<Media> medias = new ArrayList<>();
        if (event.getMedias() != null && event.getMedias().length > 0) {
            Collections.addAll(medias, event.getMedias());
        } else {
            return;
        }

        if (medias != null && medias.size() > 0) {
            manager.createAttachForOrderUrl(orderModel, medias);
        }

        for (Media m : medias) {
            if (attachedFiles.contains(m.getName())) {
                continue;
            }
            attachedFiles.add(m.getName());
        }

        lbFiles.setModel(new ListModelList<>(attachedFiles));
        lbFiles.renderAll();
    }
}
