package org.edec.rest.model.student.request;

import lombok.Data;
import org.edec.rest.model.BaseUserMsg;

import java.util.List;

@Data
public class EfficiencyMsg extends BaseUserMsg {
    private Long idLGSS;
    private List<Integer> weeks;
}
