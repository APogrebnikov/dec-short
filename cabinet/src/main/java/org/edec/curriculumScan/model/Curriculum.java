package org.edec.curriculumScan.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.utility.constants.FormOfStudyConst;
import org.edec.utility.constants.QualificationConst;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Curriculum {
    private Long id;

    private Float periodOfStudy;

    private Integer distanceType = 2;
    private Integer generation;

    private Long chairId = 0L;
    private Long createdSchoolYear, enterSchoolYear;

    //Титул ПолноеИмяПлана
    private String fileName;
    //Специальность Название
    private String specTitle;
    private String specialityTitle, directionCode, qualificationCode;
    private String programCode, planFilename;

    private FormOfStudyConst formOfStudy;
    private Integer formOfStudyType;
    private QualificationConst qualification;
    private Integer qualificationType;

    //Список блоков
    private List<Block> blockList;
    private List<Competence> competenceList;

    //Родительский план для история
    private Long parent;
    //private Date enterYear;

}
