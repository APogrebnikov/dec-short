package org.edec.rest.model.student;

import lombok.Data;
import lombok.ToString;
import org.json.JSONPropertyIgnore;
import org.json.JSONPropertyName;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProgressStat {
    private String subjectName;
    private Long idLGSS;
    private Long min, max, avg, current;

    @JSONPropertyIgnore
    @JSONPropertyName("list")
    public List<Progress> getList() {
        return list;
    }

    @ToString.Exclude
    private List<Progress> list = new ArrayList<>();



}
