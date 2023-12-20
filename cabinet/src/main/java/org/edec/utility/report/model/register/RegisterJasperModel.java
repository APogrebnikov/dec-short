package org.edec.utility.report.model.register;

import lombok.Data;
import org.edec.newOrder.model.report.ProtocolComissionerModel;

import java.util.ArrayList;
import java.util.List;


@Data
public class RegisterJasperModel
{
    private Integer formOfControl;
    private Integer retakeCount;
    private Integer type;

    private String certnumber;
    private String course;
    private String coWorkOrProj;
    private String dateOfExamination;
    private String dateOfExaminationTitle;
    private String groupname;
    private String institute;
    private String marksCount;
    private String registerNumber = "";
    private String semester;
    private String signatorytutor;
    private String signdate;
    private String subject;
    private String teacher;
    private String totalHours;
    private String typeOfRegister;

    private String chair;
    private String regtype;

    private String speciality;
    private String major;

    private String direction;
    private String dircode;

    private String receiver;
    private Integer formofstudy;

    private List<StudentModel> students = new ArrayList<>();
    private List<ProtocolComissionerModel> comissionerList = new ArrayList<>();

}