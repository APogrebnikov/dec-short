package org.edec.studentPassport.service;

import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.studentPassport.model.filter.StudentPassportFilter;
import org.edec.utility.component.model.RatingModel;

import java.io.ByteArrayOutputStream;
import java.util.List;


public interface StudentPassportService {

    List<StudentStatusModel> getStudentsByFilter(StudentPassportFilter filter);

    boolean saveStudentInfo(StudentStatusModel studentStatusModel);

    List<RatingModel> getRatingByHumAndDG(Long idHum, Long idDG, boolean debt);

    ByteArrayOutputStream generateIndexCard(Long idStudentCardMine, String groupName);
}
