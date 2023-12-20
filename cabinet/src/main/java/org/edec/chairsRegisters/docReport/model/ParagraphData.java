package org.edec.chairsRegisters.docReport.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

import java.util.List;

@Data
@AllArgsConstructor
public class ParagraphData {
    private ParagraphAlignment paragraphAlignment;
    private List<RunData> runDataList;
}
