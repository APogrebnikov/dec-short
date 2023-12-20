package org.edec.curriculumScan.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Competence {
    private Long id;
    private String name;
    private String description;
}
