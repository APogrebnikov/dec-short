package org.edec.utility.fileManager;

/**
 * Created by dmmax
 */
public class FileModel {
    public enum Inst {
        ISIT("isit", 1L, "ИКИТ"), VII("vii", 23L, "ВИИ");
        private String value;
        private Long id;
        private String shortname;

        public String getValue () {
            return value;
        }

        public Long getId () {
            return id;
        }

        public String getShortname () {
            return shortname;
        }

        public void setValue (String value) {
            this.value = value;
        }

        Inst (String value, Long id, String shortname) {
            this.value = value;
            this.id = id;
            this.shortname = shortname;
        }

        public static Inst getInstById (Long id) {
            for (Inst inst : Inst.values()) {
                if (inst.getId().equals(id)) {
                    return inst;
                }
            }
            return null;
        }

        public static Inst getInstByShortName (String shortname) {
            for (Inst inst : Inst.values()) {
                if (inst.getShortname().equals(shortname)) {
                    return inst;
                }
            }
            return null;
        }
    }

    public enum TypeDocument {
        ORDER("order"), REGISTER("register"), STUDENTCARD("studentcard"), SOCIAL_REFERENCE("socialReference"), MEMORANDUM("memorandum");
        private String value;

        public String getValue () {
            return value;
        }

        public void setValue (String value) {
            this.value = value;
        }

        TypeDocument (String value) {
            this.value = value;
        }
    }

    public enum SubTypeDocument {
        //ПРИКАЗЫ
        ACADEMIC("academic"),MATERIAL_SUPPORT("material_support"), ACADEMIC_INCREASE("academic_increase"), ACADEMIC_INCREASED("academic_increased"), SOCIAL("social"), SOCIAL_INCREASE("social_increase"), DEDUCTION(
                "deduction"), TRANSFER("transfer"), SET_ELIMINATION("setElimination"),
        //ВЕДОМОСТИ
        MAIN("main"), COMMISSION_RETAKE("commissionRetake"), COMMON_RETAKE("commonRetake"), INDIVIDUAL_RETAKE(
                "individualRetake"),
        //СТУДЕНТЧЕСКАЯ КАРТА
        REFERENCE("reference"),
        //СПРАВКИ УСЗН
        INVALID("invalid"), INDIGENT("indigent"), VETERAN("veteran"),
        //СЛУЖЕБНЫЕ ЗАПИСКИ
        CORRECT_RATING("correctRating");

        private String value;

        public String getValue () {
            return value;
        }

        public void setValue (String value) {
            this.value = value;
        }

        SubTypeDocument (String value) {
            this.value = value;
        }
    }

    private Inst inst;
    private TypeDocument typeDocument;
    private SubTypeDocument subTypeDocument;

    /**
     * Имя файла
     */
    private String name;
    /**
     * Формат
     */
    private String format;

    private Long sem;

    public FileModel (Inst inst, TypeDocument typeDocument, SubTypeDocument subTypeDocument, Long sem, String name) {
        this.inst = inst;
        this.typeDocument = typeDocument;
        this.subTypeDocument = subTypeDocument;
        this.sem = sem;
        this.name = name;
    }

    public FileModel (Inst inst, TypeDocument typeDocument, SubTypeDocument subTypeDocument, String name, String format, Long sem) {
        this.inst = inst;
        this.typeDocument = typeDocument;
        this.subTypeDocument = subTypeDocument;
        this.name = name;
        this.format = format;
        this.sem = sem;
    }

    public Inst getInst () {
        return inst;
    }

    public void setInst (Inst inst) {
        this.inst = inst;
    }

    public TypeDocument getTypeDocument () {
        return typeDocument;
    }

    public void setTypeDocument (TypeDocument typeDocument) {
        this.typeDocument = typeDocument;
    }

    public SubTypeDocument getSubTypeDocument () {
        return subTypeDocument;
    }

    public void setSubTypeDocument (SubTypeDocument subTypeDocument) {
        this.subTypeDocument = subTypeDocument;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getFormat () {
        return format;
    }

    public void setFormat (String format) {
        this.format = format;
    }

    public Long getSem () {
        return sem;
    }

    public void setSem (Long sem) {
        this.sem = sem;
    }
}
