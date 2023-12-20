package org.edec.orderAttach.model.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class OrderAttachDAOModel {
    //Приказ
    private Date dateCreated;
    private Integer idOrder;
    private String description, number, type, url;

    //Прикрепленный файл
    private Integer idOrderAttach;
    private String certNumber, certFioAttach, fileName, params, reportType, orderType;
}
