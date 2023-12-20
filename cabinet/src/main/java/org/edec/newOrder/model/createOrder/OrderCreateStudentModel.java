package org.edec.newOrder.model.createOrder;

import lombok.Getter;
import lombok.Setter;
import org.edec.newOrder.model.ScholarshipModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class OrderCreateStudentModel {
    private Long id;
    private Long idStudentcard;
    private Long idSessionRating;

    private String groupname;
    private String prevOrderNumber;
    private String nomination;
    private String fio;
    private String family;
    private String name;
    private String practicName;
    private String practicType;
    private String instituteName;
    private String specialityCode;
    private String scholarshipCode;
    private String transferInfo;
    private String transferConditionallyInfo;

    private ScholarshipModel scholarshipInfo;

    private Boolean isChecked, foreigner, sirota, invalid, indigent, veteran, transfer,
            nextGovernmentFinanced, governmentFinanced, getSocialPrev, deductedCurSem, isSessionProlongation;

    private Integer semesternumber;
    private Integer curSemester;
    private Integer typeInvalid;
    private Integer typeScholarship;
    private Integer sessionResult;
    private Integer sessionResultPrev;
    private Integer qualification;
    private Integer scholarship;
    private Integer refType;
    private Integer rating;
    private Integer course;
    private Integer debtsAmount;

    private Date dateCompleteSession;
    private Date dateOfEndSession;
    private Date dateOfEndCurSession;
    private Date dateNextEndOfSession;
    private Date datePrevEndOfSession;
    private Date dateOfEndEducation;
    private Date dateOfEndElimination;
    private Date prolongationEndDate;
    private Date firstDate;
    private Date secondDate;
    private Date datePracticFrom;
    private Date datePracticTo;
    private Date birthDate;
    private Date prevOrderDateSign;
    private Date prevOrderTransferTo;
    private Date prevOrderTransferToProl;
    private Date dateOfBeginSchoolYear;
    private Date dateOfStartSemester;
    private Date dateOfEndSemester;
    private Date dateSocialScholarshipFrom;
    private Date dateAcademicalScholarshipFrom;
    private Date dateSocialScholarshipTo;
    private Date dateReferenceFrom;
    private Date dateReferenceTo;
    private Date dateReferenceApply;
    private Date dateOfCertificationTo;
    private Date signDate;

    private Long idLastDicAction;

    private Double periodOfStudy;

    private List<Object> paramsFromCreate = new ArrayList<>();

    private List<Integer> ratingsList = new ArrayList<>();

    // Флаг одобрения мат. помощи
    private int approvedMaterial = 1;
    // Причина отказа в мат. помощи
    private String refusalReason;

    @Override
    public boolean equals (Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OrderCreateStudentModel)) {
            return false;
        }

        return ((OrderCreateStudentModel) obj).id.equals(this.id);
    }

    @Override
    public int hashCode () {
        return this.id.hashCode();
    }
}
