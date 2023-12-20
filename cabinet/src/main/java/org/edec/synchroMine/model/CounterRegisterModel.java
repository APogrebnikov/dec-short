package org.edec.synchroMine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CounterRegisterModel {
    private Long idSem, idSemFromCounters, idCounters;
    private Long counterRegister;
}
