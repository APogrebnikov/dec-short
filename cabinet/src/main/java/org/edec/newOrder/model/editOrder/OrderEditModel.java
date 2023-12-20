package org.edec.newOrder.model.editOrder;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderEditModel {
    private Date datecreated;
    private Date datefinish;
    private Date datesign;

    private Long idOrder;
    private Long idOrderRule;
    private Long orderType;
    private Long idSemester;
    private Long idInstitute;
    private int formOfStudy;

    private Long countStudents;

    private String currenthumanface;
    private String description;
    private String idLotus;
    private String number;
    private String semesterSeason;
    private String status;
    private String type;
    private String url;

    private List<SectionModel> sections;
    private List<GroupModel> groups;

    public List<StudentModel> getStudentsInSection(Long idOS) {
        List<StudentModel> result = new ArrayList<>();
        if(groups != null) {
            groups.forEach(el -> el.getSections().stream().filter(sec -> sec.getIdOS() == idOS).forEach(sec -> {
                if(sec.getStudentModels() != null) {
                    result.addAll(sec.getStudentModels());
                }
            }));
        } else {
            sections.stream().filter(sec -> sec.getIdOS() == idOS).forEach(el -> el.getGroups().forEach(groupModel -> {
                if(groupModel.getStudentModels() != null) {
                    result.addAll(groupModel.getStudentModels());
                }
            }));
        }
        return result;
    }

    public List<StudentModel> getAllStudents(){
        List<StudentModel> result = new ArrayList<>();
        if(groups != null) {
            groups.forEach(el -> el.getSections().forEach(sec -> {
                if(sec.getStudentModels() != null) {
                    result.addAll(sec.getStudentModels());
                }
            }));
        } else {
            sections.stream().forEach(el -> el.getGroups().forEach(groupModel -> {
                if(groupModel.getStudentModels() != null) {
                    result.addAll(groupModel.getStudentModels());
                }
            }));
        }
        return result;
    }
}
