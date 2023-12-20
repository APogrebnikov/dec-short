package org.edec.order.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class GroupModel {
    private String name;

    private List<SectionModel> sections = new ArrayList<>();
    private List<StudentModel> studentModels = new ArrayList<>();
}
