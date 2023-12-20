package org.edec.utility.doc.model.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.edec.utility.doc.model.ParagraphData;

import java.util.List;

@Data
@AllArgsConstructor
public class RowData {
    private List<ParagraphData> content;
    private boolean isHeader;
}
