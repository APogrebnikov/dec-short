package org.edec.register.model.mine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.utility.constants.RatingConst;

@Getter
@Setter
@NoArgsConstructor
public class StudentMineModel {
    private String fio;
    private RatingConst mainMark, mark1, mark2;
}
