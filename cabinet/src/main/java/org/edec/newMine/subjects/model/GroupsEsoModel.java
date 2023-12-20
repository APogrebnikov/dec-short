package org.edec.newMine.subjects.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.model.GroupModel;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.edec.synchroMine.model.eso.StudentModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GroupsEsoModel  extends GroupModel {
    private Boolean military = false;

    private Date createdSchoolYear, enterSchoolYear;

    private Double periodOfStudy;

    //заочная форма обучения: 1 - заочно-очная, 2 - заочная
    private Integer distanceType;
    //форма обучения: 1 - очная, 2 - заочная
    private Integer formOfStudy;
    private Integer generation;
    //Квалификация: 1 - инженер, 2 - бакалавр, 3 - магистр
    private Integer qualification;

    private Long idChair, idDirection, idLGS, idChairMineCabinet, idCurriculum;
    private Integer idChairMine, idCurriculumMine, idGroupMine, idDirectionMine;

    private String chairName, directionCode, directionTitle;
    private String planfileName, specialityTitle, qualificationCode;

    private GroupMineModel linkedGroup;

    private List<StudentModel> students = new ArrayList<>();
}
