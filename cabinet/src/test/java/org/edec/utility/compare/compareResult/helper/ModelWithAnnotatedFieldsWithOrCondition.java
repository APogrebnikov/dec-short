package org.edec.utility.compare.compareResult.helper;

import lombok.Builder;
import lombok.Data;
import org.edec.utility.compare.compareResult.CompareField;
import org.edec.utility.compare.compareResult.CompareLevel;
import org.edec.utility.compare.compareResult.LogicalCondition;

@Data
@Builder
public class ModelWithAnnotatedFieldsWithOrCondition {

    @CompareField(logicalCondition =  LogicalCondition.OR)
    private String groupname;
    @CompareField(logicalCondition =  LogicalCondition.OR)
    private Long idGroup;
    @CompareField(required = false)
    private Integer course;
}
