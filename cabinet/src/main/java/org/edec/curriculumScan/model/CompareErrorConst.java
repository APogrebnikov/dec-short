package org.edec.curriculumScan.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
@AllArgsConstructor
public enum CompareErrorConst {
    NAME(1,"Несоответствие названия"),
    HOURS(2, "Несоответствие часов"),
    FC(3, "Несоответствие формы контроля"),
    CODE(4, "Отсутствует код"),
    MISS_MINE(5, "Отсутствует в шахтах"),
    MISS_ESO(6, "Отсутствует в деканате");

    private int code;
    private String description;

    public static CompareErrorConst getDataByCode(int code) {
        for (CompareErrorConst ratingConst : CompareErrorConst.values()) {
            if (ratingConst.getCode() - code == 0) {
                return ratingConst;
            }
        }
        return null;
    }

    public static String getErrorByCode(int code) {
        for (CompareErrorConst ratingConst : CompareErrorConst.values()) {
            if (ratingConst.getCode() - code == 0) {
                return ratingConst.getDescription();
            }
        }
        return "";
    }

}
