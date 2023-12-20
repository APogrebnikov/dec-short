package org.edec.commons.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
@Data
public class DirectionModel {
    private Long idDirection;

    private String code;
    private String title;

    private List<CurriculumModel> listCurriculum = new ArrayList<>();
}
