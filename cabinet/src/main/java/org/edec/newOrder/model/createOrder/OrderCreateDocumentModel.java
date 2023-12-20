package org.edec.newOrder.model.createOrder;

import org.edec.newOrder.model.enums.DocumentEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель документа у приказа
 */
public class OrderCreateDocumentModel {
    /**
     * Название документа
     */
    private String nameDocument;

    /**
     * Тип документа
     */
    private DocumentEnum typeDocument;

    /**
     * Список параметров для создания документа
     */
    private List<OrderCreateParamModel> listDocumentParam;

    public OrderCreateDocumentModel () { }

    public OrderCreateDocumentModel (String nameDocument, DocumentEnum typeDocument) {
        this.nameDocument = nameDocument;
        this.typeDocument = typeDocument;
        this.listDocumentParam = new ArrayList<>();
    }

    public String getNameDocument () {
        return nameDocument;
    }

    public void setNameDocument (String nameDocument) {
        this.nameDocument = nameDocument;
    }

    public List<OrderCreateParamModel> getListDocumentParam () {
        return listDocumentParam;
    }

    public void setListDocumentParam (List<OrderCreateParamModel> listDocumentParam) {
        this.listDocumentParam = listDocumentParam;
    }

    public DocumentEnum getTypeDocument () {
        return typeDocument;
    }

    public void setTypeDocument (DocumentEnum typeDocument) {
        this.typeDocument = typeDocument;
    }
}
