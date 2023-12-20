package org.edec.studentOrder.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class OrderModel {

    private long idOrderHead;
    private String descriptionspec;
    private Date dateOfBegin;
    private String typeOrder;
    private String section;
}
