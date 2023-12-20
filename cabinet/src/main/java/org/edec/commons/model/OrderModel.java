package org.edec.commons.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Общая модель приказов
 */
@Data
@NoArgsConstructor
public class OrderModel {
    private Date dateCreated;

    private Long idOrder;

    private String description, number, type, url;
}
