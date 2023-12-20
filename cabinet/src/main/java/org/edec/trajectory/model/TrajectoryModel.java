package org.edec.trajectory.model;

import lombok.Data;
import org.edec.commons.model.CurriculumModel;


@Data
public class TrajectoryModel {
    private Boolean currentYear;

    private Long id;
    private Long idDicTrajectory;

    private String name;

    private CurriculumModel curriculum;
}
