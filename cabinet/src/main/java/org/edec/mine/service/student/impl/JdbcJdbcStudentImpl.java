package org.edec.mine.service.student.impl;

import org.edec.cloud.sync.mine.api.student.data.StudentCurrentSemesterDto;
import org.edec.cloud.sync.mine.api.student.data.StudentForComparingFilter;
import org.edec.mine.dao.student.StudentDao;
import org.edec.mine.service.student.JdbcStudentService;

import java.util.List;

public class JdbcJdbcStudentImpl implements JdbcStudentService {

    private StudentDao studentDao = new StudentDao();

    @Override
    public List<StudentCurrentSemesterDto> getCabinetHumafnaces(StudentForComparingFilter filter) {
        return studentDao.getHumafncesByFilter(filter);
    }
}
