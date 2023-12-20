package org.edec.chairsRegisters.docReport.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RunData {
    private String text;
    private Boolean isBold, isUnderline;

    public RunData(String text){
        this.text = text;
        isBold = false;
        isUnderline = false;
    }
}
