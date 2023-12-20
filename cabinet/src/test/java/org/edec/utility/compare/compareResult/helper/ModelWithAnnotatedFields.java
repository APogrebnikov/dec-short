package org.edec.utility.compare.compareResult.helper;

import lombok.Builder;
import lombok.Data;
import org.edec.utility.compare.compareResult.CompareField;
import org.edec.utility.compare.compareResult.CompareLevel;

@Data
@Builder
public class ModelWithAnnotatedFields {

    @CompareField
    private String groupname;
    @CompareField
    private Long idGroup;
    @CompareField(required = false)
    private Integer course;
}
