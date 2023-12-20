package org.edec.register.model.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RegisterDateModel {
    private String date;
    private List<RegisterModel> registers;


}
