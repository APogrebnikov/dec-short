package org.edec.kcp.model;

import lombok.Data;

@Data
public class KCPModel {
    Long id;
    Long id_curriculum;
    String direction;
    String year;
    Integer budget;
    Integer dogovor;
}
