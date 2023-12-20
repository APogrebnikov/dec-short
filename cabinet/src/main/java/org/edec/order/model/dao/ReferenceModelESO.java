package org.edec.order.model.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class ReferenceModelESO {
    private Long id;
    private Date dateGet;
    private Date dateStart;
    private Date dateFinish;
}
