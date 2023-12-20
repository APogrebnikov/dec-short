package org.edec.utility.constants;

public enum StudentAttendConst {

    MISS(0, "отсутствовал", null, "Н"),
    ATTEND(1, "посетил", 0, "+"),
    ECOURSE(2, "электронные курсы", 1, "Э"),
    TEACHER_MISS(3,"преподаватель отсуствовал", 2, "П"),
    EMPTY(-1, "не заполнена посещаемость", 3, ""),

    REASON(4,"отсутствовал по уважительной причине", 4, "Б");

    private Integer value;
    private Integer databaseValue;
    private String name;

    private String shortName;

    public Integer getValue () {
        return value;
    }
    public Integer getDatabaseValue() {return databaseValue;}

    public String getName () {
        return name;
    }
    public String getShortName () {
        return shortName;
    }

    StudentAttendConst(Integer value, String name, Integer databaseValue, String shortName) {
        this.value = value;
        this.name = name;
        this.databaseValue = databaseValue;
        this.shortName = shortName;
    }

    public static StudentAttendConst getByValue (Integer value) {
        for (StudentAttendConst studentAttendConst : StudentAttendConst.values()) {
            if (studentAttendConst.getValue().equals(value)) {
                return studentAttendConst;
            }
        }
        return null;
    }

    public static StudentAttendConst getByName (String name) {
        for (StudentAttendConst studentAttendConst : StudentAttendConst.values()) {
            if (studentAttendConst.getName().equals(name)) {
                return studentAttendConst;
            }
        }
        return null;
    }


    public static StudentAttendConst getDatabaseValue (Integer databaseValue) {
        for (StudentAttendConst studentAttendConst : StudentAttendConst.values()) {
            if (studentAttendConst.getDatabaseValue().equals(databaseValue)) {
                return studentAttendConst;
            }
        }
        return null;
    }

    public static Integer getDatabaseValueByValue (Integer value) {
        for (StudentAttendConst studentAttendConst : StudentAttendConst.values()) {
            if (studentAttendConst.getValue().equals(value)) {
                return studentAttendConst.getDatabaseValue();
            }
        }
        return null;
    }

    public static Integer getValueByDatabaseValue (Integer databaseValue) {
        if (databaseValue!=null && (databaseValue == 3 || databaseValue == 4)) {
            return null;
        }
        for (StudentAttendConst studentAttendConst : StudentAttendConst.values()) {
            if (studentAttendConst.getDatabaseValue() == null && databaseValue == null) {
                return 0;
            }
            if (studentAttendConst.getDatabaseValue()!=null && studentAttendConst.getDatabaseValue().equals(databaseValue)) {
                return studentAttendConst.getValue();
            }
        }
        return null;
    }

    public static String getShortnameByValue (Integer value) {
        for (StudentAttendConst studentAttendConst : StudentAttendConst.values()) {
            if (studentAttendConst.getValue().equals(value)) {
                return studentAttendConst.getShortName();
            }
        }
        return null;
    }
}
