package org.edec.chairEmployee.model;

import lombok.Data;

/**
 * @author Max Dimukhametov
 */
@Data
public class PostModel {
    private Long idDepartment;
    private Long idLED;
    private Long idPost;

    private String department;
    private String post;
}
