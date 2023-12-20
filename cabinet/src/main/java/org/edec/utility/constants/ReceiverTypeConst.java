package org.edec.utility.constants;

import java.util.ArrayList;
import java.util.List;

public enum ReceiverTypeConst {

    STUDENTS(1L,"Студентам"),
    EMPLOYEES(2L,"Сотрудникам"),
    GROUPS(3L,"Группам"),
    COURSES(4L,"Курсам"),
    LEADERS(6L,"Старостам");

    private Long value;
    private String name;

    public String getName(){
        return name;
    }
    public Long getValue() { return value; }

    ReceiverTypeConst(Long value, String name){
        this.value = value;
        this.name = name;
    }

    public static ReceiverTypeConst getByName(String name){
        for(ReceiverTypeConst receiverType : ReceiverTypeConst.values()){
            if(receiverType.getName().equals(name)){
                return receiverType;
            }
        }

        return null;
    }

    public static Long getValueByName(String name){
        for(ReceiverTypeConst receiverType : ReceiverTypeConst.values()){
            if(receiverType.getName().equals(name)){
                return receiverType.value;
            }
        }

        return null;
    }

    public static List<String> getNames(){
        List<String> names = new ArrayList<>();

        for(ReceiverTypeConst receiverType : ReceiverTypeConst.values()){
            names.add(receiverType.name);
        }

        return names;
    }

}

