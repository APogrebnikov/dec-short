package org.edec.synchroMine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SemesterModel {
    private Long idSemester, idSY, idInstitute;
    private int formOfStudy, isCurrentSem, isClosed, season, firstWeek;
}
