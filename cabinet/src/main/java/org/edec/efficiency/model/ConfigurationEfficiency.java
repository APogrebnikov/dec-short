package org.edec.efficiency.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ConfigurationEfficiency {
    private Boolean attendance = true;
    private Boolean eok = true;
    private Boolean master = true;
    private Boolean performance = true;
    private Boolean physcul = true;

    private Integer maxRedLevel = 49;
    private Integer minGreenLevel = 78;

    private Long idConfigurationEfficiency;
    private Long idSem;
}
