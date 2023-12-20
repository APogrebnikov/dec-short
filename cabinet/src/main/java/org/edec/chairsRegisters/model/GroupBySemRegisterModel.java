package org.edec.chairsRegisters.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class GroupBySemRegisterModel {
    private String sem;
    private List<ChairsRegisterModel> listRegister = new ArrayList<>();
}
