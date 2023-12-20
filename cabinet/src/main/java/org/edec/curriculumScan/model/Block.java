package org.edec.curriculumScan.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Block {
    private Long id;
    private String name;
    private String code;
    private Long parentId;
    private Boolean selectable;
    private List<Subject> subjectList;
}
