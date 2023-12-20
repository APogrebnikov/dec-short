package org.edec.utility.constants;

public enum OrderRuleConst {
    ACADEMIC_NOT_IN_SESSION("Академическая стипендия(не в сессию)", 19L),
    ACADEMIC_IN_SESSION("Академическая стипендия(сессия)", 8L),
    DEDUCTION_INITIATIVE("Отчисление по собственному желанию", 13L),
    SOCIAL_IN_SESSION("Социальный приказ(сессия)", 20L),
    SOCIAL_NEW_REFERENCE("Социальный приказ(новые справки)", 21L),
    SOCIAL_INCREASED_IN_SESSION("Социальный повышенный приказ(сессия)", 22L),
    SOCIAL_INCREASED_NEW_REFERENCE("Социальный повышенный приказ(новые справки)", 23L),
    TRANSFER("О переводе на следующий курс", 24L),
    TRANSFER_CONDITIONALLY("О переводе на следующий курс(условно)", 25L),
    TRANSFER_AFTER_TRANSFER_CONDITIONALLY_RESPECTFUL("Перевод ранее условно переведённых студентов(уваж. причины)", 29L),
    TRANSFER_AFTER_TRANSFER_CONDITIONALLY_NOT_RESPECTFUL("Перевод ранее условно переведённых студентов(не уваж. причины)", 30L),
    TRANSFER_PROLONGATION("Продление сроков ликвидации", 31L),
    SET_ELIMINATION_RESPECTFUL("Установление сроков ЛАЗ(уваж.)", 32L),
    SET_ELIMINATION_NOT_RESPECTFUL("Установление сроков ЛАЗ(не уваж.)", 33L),
    PROLONGATION_ELIMINATION_WINTER("Продление срок ЛАЗ(по результатам зим. сессии)", 35L),
    CANCEL_ACADEMICAL_SCHOLARSHIP_IN_SESSION("Отмена гос. академ. стипендии(сессия)", 42L),
    TRANSFER_CONDITIONALLY_RESPECTFUL("О переводе на следующий курс(условно, уваж.)", 45L),
    ACADEMIC_INCREASED("Повышенная академическая стипендия", 44L),
    CANCEL_SCHOLARSHIP_AFTER_PRACTICE("Отмена гос. академ. стипендии(практика)", 46L),
    MATERIAL_SUPPORT("Материальная поддержка", 49L),
    SET_ELIMINATION_AFTER_TRANSFER_RESPECTFUL("Установление сроков ЛАЗ после перевода(уважительно)", 47L),
    SET_ELIMINATION_AFTER_TRANSFER_NOT_RESPECTFUL("Установление сроков ЛАЗ после перевода(не уважительно)", 50L),
    SET_ELIMINATION_AFTER_PRACTICE("Установление сроков первой ППА (практика)", 48L),
    SET_FIRST_ELIMINATION("Установление сроков первой ППА (сессия)", 51L),
    SET_SECOND_ELIMINATION("Установление сроков второй ППА (сессия)", 52L),
    DEDUCTION_BY_COMMISSION_RESULT("Отчисление по результатам комиссии", 53L),
    ACADEMIC_FIRST_COURSE("Академ. стип. 1 курс", 54L),
    CANCEL_SOCIAL_INCREASED_BY_PRACTICE("Отмена соц. повыш. стипендии(практика)", 64L),
    CANCEL_SOCIAL_INCREASED_IN_SESSION("Отмена соц. повыш. стипендии(сессия)", 66L),
    ACADEMIC_INDIVIDUAL("Назначение ГАС(индивидуально)", 67L);

    private Long id;
    private String name;

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    OrderRuleConst(String name, long id) {
        this.name = name;
        this.id = id;
    }

    public static OrderRuleConst getByName(String name) {
        for (OrderRuleConst orderRuleConst : OrderRuleConst.values()) {
            if (orderRuleConst.getName().equals(name)) {
                return orderRuleConst;
            }
        }
        return null;
    }

    public static OrderRuleConst getById(Long id) {
        for (OrderRuleConst orderRuleConst : OrderRuleConst.values()) {
            if (orderRuleConst.getId().equals(id)) {
                return orderRuleConst;
            }
        }
        return null;
    }
}
