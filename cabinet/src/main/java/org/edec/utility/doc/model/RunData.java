package org.edec.utility.doc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RunData {
    private String text;
    private String additionalText;
    @Builder.Default
    private String fontFamily = "Times New Roman";
    @Builder.Default
    private Boolean isBold = false;
    @Builder.Default
    private Boolean isUnderline = false;
    @Builder.Default
    private Boolean isItalic = false;
    @Builder.Default
    private Integer fontSize = 12;
    @Builder.Default
    private Boolean addTab = false;

    public RunData(String text){
        this.text = text;
    }

    public RunData(String text, Boolean isBold, Boolean isUnderline){
        this.text = text;
        this.isBold = isBold;
        this.isUnderline = isUnderline;
    }
}
