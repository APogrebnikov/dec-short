package org.edec.mine.service.student;

import org.edec.cloud.sync.mine.api.student.data.StudentCurrentSemesterDto;
import org.edec.cloud.sync.mine.api.student.data.StudentForComparingFilter;

import java.util.List;

public interface JdbcStudentService {

    List<StudentCurrentSemesterDto> getCabinetHumafnaces(StudentForComparingFilter filter);
}
