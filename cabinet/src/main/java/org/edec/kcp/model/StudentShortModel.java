package org.edec.kcp.model;

import lombok.Data;

@Data
public class StudentShortModel {
    Long id;
    String groupName;
    String recordBook;
    String fio;
    boolean financed;
    Integer debt;
}
