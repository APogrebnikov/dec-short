package org.edec.contingentMovement.model;

import lombok.Data;

import java.util.List;

@Data
public class StudentMoveModel {
    private Long id;
    private String fio;
    private String reason;
}
