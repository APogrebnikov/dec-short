package org.edec.passportGroup.model.passportGroupReport.curProgressXls;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CurProgressGroupModel {
    private String groupname;
    private Long idLgs;
    private List<CurProgressStudentModel> students = new ArrayList<>();
}
