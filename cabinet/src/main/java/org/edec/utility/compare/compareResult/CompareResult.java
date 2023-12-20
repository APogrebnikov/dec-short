package org.edec.utility.compare.compareResult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompareResult<T> {

    private T compareObj;
    private T linkedObj;

    private CompareResultStatus compareStatus = CompareResultStatus.NOT_EQUAL;
}
