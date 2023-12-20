package org.edec.order.model.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class SectionModelEso {
    String name;

    Long idLOS;

    Date firstDate;
    Date secondDate;
}
