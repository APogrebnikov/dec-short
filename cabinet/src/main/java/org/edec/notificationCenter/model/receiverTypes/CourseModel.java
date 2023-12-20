package org.edec.notificationCenter.model.receiverTypes;

import lombok.Data;
import org.edec.notificationCenter.model.ReceiverModel;

@Data
public class CourseModel extends ReceiverModel {

    private Integer courseNumber;

    private Integer fos;

    public CourseModel(){}

    public CourseModel(Integer courseNumber, Integer fos){
        this.courseNumber = courseNumber;
        this.fos = fos;
    }
}
