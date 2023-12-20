package org.edec.utility.constants;

/**
 * Класс для хранения размеров стипендий (используется в приказах)
 */
public enum ScholarshipSizeConst {

    SOCIAL(0, "Социальная стипендия", "3860"),
    SOCIAL_FOR_ORPHAN(1, "Социальная стипендия(сироты)", "6745"),
    SOCIAL_INCREASED(2, "Повышенная социальная", "7300"),
    SOCIAL_INCREASED_FOR_ORPHAN(3, "Повышенная социальная(сироты)","12715");

    private Integer value;
    private String name;
    private String size;

    public Integer getValue(){
        return value;
    }

    public String getName(){
        return name;
    }

    public String getSize() {return  size;}

    ScholarshipSizeConst(Integer value, String name, String size){
        this.value = value;
        this.name = name;
        this.size = size;
    }
}
