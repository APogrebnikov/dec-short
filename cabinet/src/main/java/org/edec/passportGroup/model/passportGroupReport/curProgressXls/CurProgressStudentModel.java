package org.edec.passportGroup.model.passportGroupReport.curProgressXls;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CurProgressStudentModel {
    private String fioStudent;
    private Long idSSS;
    private List<CurProgressSubjectModel> subjects = new ArrayList<>();
}
