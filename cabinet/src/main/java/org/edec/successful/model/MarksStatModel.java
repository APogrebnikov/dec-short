package org.edec.successful.model;

import lombok.Data;

@Data
public class MarksStatModel {
    int countFiveMarks, countFourMarks, countThreeMarks, countTwoMarks, countUnpassMarks,
        countPassMarks, countDebts, countMissToAppearMarks;
}
