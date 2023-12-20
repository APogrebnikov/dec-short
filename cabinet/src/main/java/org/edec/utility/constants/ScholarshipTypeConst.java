package org.edec.utility.constants;

/**
 * Перечисление полей для заполнения дат у конкретных типов стипендий в поле order_info в studentcard
 */
public enum ScholarshipTypeConst {

    ACADEMIC(0,"academicalScholarship", "ГАС"),
    SOCIAL(1,"socialScholarship", "Соц. стипендия"),
    SOCIAL_INCREASED(2,"socialIncreasedScholarship","Соц. повышенная стипендия"),
    MATERIAL(3,"materialSupport", "Мат. поддержка"),
    ACADEMIC_INCREASED(4, "academicalIncreasedScholarship", "ПГАС");

    private Integer value;
    private String jsonName;
    private String name;

    public Integer getValue(){
        return value;
    }

    public String getJsonName(){
        return jsonName;
    }

    public String getName() { return name; }

    ScholarshipTypeConst(Integer value, String jsonName, String name){
        this.value = value;
        this.jsonName = jsonName;
        this.name = name;
    }
}
