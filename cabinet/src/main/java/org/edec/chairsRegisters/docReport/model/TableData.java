package org.edec.chairsRegisters.docReport.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TableData {
    private List<RowData> content;
    private List<Integer> columnSize;
}
