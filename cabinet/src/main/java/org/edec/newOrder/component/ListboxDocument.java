package org.edec.newOrder.component;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateDocumentModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderSectionModel;
import org.edec.newOrder.service.ComponentProvider;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import org.zkoss.zul.impl.XulElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ListboxDocument extends Listbox {
    private List<XulElement> documentParamElements = new ArrayList<>();
    private ComponentProvider componentProvider = new ComponentProvider();
    private OrderService orderService;

    public ListboxDocument(OrderService service) {
        super();
        setWidth("350px");
        setHeight("300px");

        Listhead lhDocument = new Listhead();
        lhDocument.setParent(this);

        Listheader lhrName = new Listheader();
        lhrName.setParent(this.getListhead());
        lhrName.setAlign("center");
        lhrName.setWidth("200px");

        Label lName = new Label("Название");
        lName.setSclass("cwf-listheader-label");
        lName.setParent(lhrName);

        Listheader lhrAction = new Listheader();
        lhrAction.setParent(this.getListhead());
        lhrAction.setAlign("center");
        lhrAction.setWidth("150px");

        setOrderService(service);
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
        fill();
    }

    private void fill() {
        this.getItems().clear();

        int i = 0;
        for (OrderCreateDocumentModel model : orderService.getOrderDocuments()) {
            Listitem li = new Listitem();
            li.appendChild(new Listcell(model.getNameDocument()));

            Listcell lcElementParam = new Listcell("");
            lcElementParam.setStyle("cursor: pointer; color: #FF0000;");
            li.appendChild(lcElementParam);

            Label lParam = new Label("Параметры");
            lParam.setStyle(" font-size: 14px");
            lParam.setParent(lcElementParam);

            createPopupParams(lcElementParam, lParam, model);

            Clients.showNotification("Нажмите чтобы заполнить параметры документа", "info", lcElementParam, "before_end", 2000);

            this.appendChild(li);
            i++;
        }

        if (orderService.isFilesNeeded()) {
            this.appendChild(getLiWithUploadButton());
        }
    }

    private void createPopupParams(Listcell listcell, Label lParam, OrderCreateDocumentModel model) {
        Popup popupParam = new Popup();
        popupParam.setParent(listcell);

        listcell.addEventListener(Events.ON_CLICK, event -> {
            popupParam.open(lParam, "Overlap");
        });

        Vbox vbParam = new Vbox();
        vbParam.setParent(popupParam);

        for (OrderCreateParamModel param : model.getListDocumentParam()) {
            Hbox hbParam = new Hbox();

            XulElement element = componentProvider.provideComponent(param.getUiElement());

            if (element.getAttribute("Media") != null) {
                XulElement t  = (XulElement) element.getFirstChild();
                t.addEventListener(Events.ON_FOCUS,event -> onChangeColor(vbParam, listcell));
            }

            element.addEventListener(Events.ON_CHANGE,event -> onChangeColor(vbParam, listcell));
            documentParamElements.add(element);

            Label lParamName = new Label();
            lParamName.setStyle("line-height: 24px");
            lParamName.setValue(param.getLabelParam());

            hbParam.appendChild(lParamName);
            hbParam.appendChild(element);

            vbParam.appendChild(hbParam);
        }
    }

    private void onChangeColor(Vbox vbParam, Listcell listcell) {
        List<XulElement> listParams = vbParam.getChildren();

        int fillParams = 0;
        for(XulElement xulElement : listParams)
        {
            XulElement secondChildrenXulElement = (XulElement) xulElement.getChildren().get(1);
            Object object =  componentProvider.getValueComponent(secondChildrenXulElement);

            if (object != null && (!object.toString().equals(""))) {
                fillParams++;
            }
        }
        if(fillParams < listParams.size()){
            listcell.setStyle("color: #FF0000");
        }
        else if(listParams.size() == fillParams){
            listcell.setStyle("color: #32CD32");
        }

    }

    private Listitem getLiWithUploadButton() {
        Listitem li = new Listitem();

        Listcell lcButtonAdd = new Listcell();
        lcButtonAdd.appendChild(getUploadButton());

        li.appendChild(lcButtonAdd);
        li.appendChild(new Listcell());
        return li;
    }

    public Button getUploadButton() {
        Button btnAddDocument = new Button("Добавить");
        btnAddDocument.setUpload("true,maxsize=3000000,multiple=true");

        btnAddDocument.addEventListener(Events.ON_UPLOAD, (EventListener<UploadEvent>) event -> {
            this.removeChild(this.getLastChild());

            Arrays.stream(event.getMedias()).forEach(m -> this.appendChild(getLiWithUploadedFile(m)));

            this.appendChild(getLiWithUploadButton());
        });

        return btnAddDocument;
    }

    private Listitem getLiWithUploadedFile(Media m) {
        Listitem li = new Listitem();
        li.setValue(m);

        Listcell lcName = new Listcell(m.getName());
        Listcell lcButtonDel = new Listcell();

        Button btnDelete = new Button("", "/imgs/del.png");
        btnDelete.addEventListener(Events.ON_CLICK, event -> this.removeChild(li));

        lcButtonDel.appendChild(btnDelete);

        li.appendChild(lcName);
        li.appendChild(lcButtonDel);
        return li;
    }

    public List<Object> getParams() {
        return documentParamElements.stream()
                             .map(element -> componentProvider.getValueComponent(element))
                             .collect(Collectors.toList());
    }

    public List<Media> getAttachedDocuments() {
        return this.getItems()
                .stream()
                .map(item -> (Media) item.getValue())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
