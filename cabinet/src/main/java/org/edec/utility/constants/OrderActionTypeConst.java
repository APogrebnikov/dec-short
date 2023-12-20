package org.edec.utility.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Enum для сопоставления приказов в шахтах с видами стипендий
 */
@AllArgsConstructor
@Getter
public enum OrderActionTypeConst {

    //стипендии
    ACADEMIC_FIRST_COURSE(1,
                          "Академическая стипендия (1 курс)",
                          Arrays.asList(679L,680L),
                          Collections.emptyList()),
    ACADEMIC(2,
             "Академическая стипендия",
             Arrays.asList(1004L,1005L, 1115L),
             Arrays.asList(754L)),
    ACADEMIC_INCREASED(3,
                       "Академическая повышенная стипендия" ,
                       Collections.emptyList(),
                       Collections.emptyList()),
    SOCIAL(4,
           "Социальная стипендия",
           Arrays.asList(725L,1102L),
           Arrays.asList(540L)),
    SOCIAL_INCREASED(5,
                     "Социальная повышенная стипендия",
                     Arrays.asList(539L),
                     Arrays.asList(819L)),
    MATERIAL_SUPPORT(6,
                     "Материальная поддержка",
                     Arrays.asList(537L),
                     Arrays.asList());

    private Integer type;
    private String name;
    //список id секций приказов для назначения стипендии
    private List<Long> orderActionList;
    //список id секций приказов для отмены стипендии
    private List<Long> orderCancelActionList;

    public static OrderActionTypeConst getByType(Integer type) {
        return Stream.of(OrderActionTypeConst.values())
                     .filter(orderActionTypeConst -> orderActionTypeConst.getType().equals(type))
                     .findFirst().orElse(null);
    }

    public static OrderActionTypeConst getById(Long id) {
        return Stream.of(OrderActionTypeConst.values())
                     .filter(orderActionTypeConst -> orderActionTypeConst.getOrderActionList().contains(id))
                     .findFirst().orElse(null);
    }

    public static OrderActionTypeConst getByCancelId(Long cancelId) {
        return Stream.of(OrderActionTypeConst.values())
                     .filter(orderActionTypeConst -> orderActionTypeConst.getOrderCancelActionList().contains(cancelId))
                     .findFirst().orElse(null);
    }

    public static List<Long> getAllOrdersId(){
        List<Long> ids = new ArrayList<>();
        Arrays.stream(OrderActionTypeConst.values())
              .peek(orderActionTypeConst -> ids.addAll(orderActionTypeConst.getOrderActionList()))
              .peek(orderActionTypeConst -> ids.addAll(orderActionTypeConst.getOrderCancelActionList()));
        return ids;
    }
}

