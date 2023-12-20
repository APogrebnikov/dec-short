package org.edec.utility.doc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParagraphData {

    private ParagraphAlignment paragraphAlignment = ParagraphAlignment.BOTH;
    private List<RunData> runDataList;
    private Integer indentLevel = 0; // 0 - нет отступа, 1 - красная строка, 5 - центрированный список
    private String style = "";

    public ParagraphData(ParagraphAlignment paragraphAlignment, List<RunData> runDataList){
        this.paragraphAlignment = paragraphAlignment;
        this.runDataList = runDataList;
    }

    public ParagraphData(ParagraphAlignment paragraphAlignment, List<RunData> runDataList, int indentLevel){
        this.paragraphAlignment = paragraphAlignment;
        this.runDataList = runDataList;
        this.indentLevel = indentLevel;
    }

    public ParagraphData(ParagraphAlignment paragraphAlignment, List<RunData> runDataList, String style){
        this.paragraphAlignment = paragraphAlignment;
        this.runDataList = runDataList;
        this.style = style;
    }
}
