package org.edec.newMine.subjects.model;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SubjectWorkloadModel {
        private Integer course, semester;
        private String family, name, patronymic, subjectname;

        public String getFio() {
            String fio = family + " " + name + " " + patronymic;
            return  fio;
        }
}
