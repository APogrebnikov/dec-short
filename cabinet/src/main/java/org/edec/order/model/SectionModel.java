package org.edec.order.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
public class SectionModel {
    private String foundationLos;
    private String foundation;
    private String name;

    private Date firstDate;
    private Date secondDate;
    private Date thirdDate;

    private long id;

    private List<GroupModel> groups = new ArrayList<>();
    private List<StudentModel> studentModels = new ArrayList<>();
}
