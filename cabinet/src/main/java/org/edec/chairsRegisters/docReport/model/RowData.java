package org.edec.chairsRegisters.docReport.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RowData {
    private List<ParagraphData> content;
    private boolean isHeader;
}
