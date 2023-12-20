package org.edec.utility.doc.model.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableData {
    private List<RowData> content;
    private List<Integer> columnSize;
}
