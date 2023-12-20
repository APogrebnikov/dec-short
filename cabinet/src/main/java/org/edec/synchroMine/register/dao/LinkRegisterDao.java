package org.edec.synchroMine.register.dao;

import org.edec.commons.entity.dec.mine.LinkRegister;
import org.edec.dao.DAO;
import org.edec.synchroMine.register.dao.request.LinkRegisterDaoRequest;
import org.hibernate.Query;
import org.hibernate.type.StringType;

import java.util.List;

public class LinkRegisterDao extends DAO {

    public List<LinkRegister> findLinkRegistersByRequest(LinkRegisterDaoRequest request) {
        String query = "select *\n" +
                "from mine.link_register\n" +
                "where 1 = 1\n" +
                "  and cast(course as varchar) ilike :course\n" +
                "  and cast(semester_number as varchar) ilike :semesterNumber\n" +
                "  and groupname ilike :groupname\n" +
                "  and subjectname_cabinet ilike :subjectCabinet\n" +
                "  and (subjectname_mine is null or subjectname_mine ilike :subjectMine)\n" +
                (request.isOnlyUnlinked() ? "  and subjectname_mine is null " : "");
        Query q = getSession().createSQLQuery(query)
                .addEntity(LinkRegister.class)
                .setParameter("course", request.getCourse() == null ? "%%" : String.valueOf(request.getCourse()), StringType.INSTANCE)
                .setParameter("semesterNumber", request.getSemesterNumber() == null ? "%%" : String.valueOf(request.getSemesterNumber()), StringType.INSTANCE)
                .setParameter("groupname", "%" + request.getGroupname() + "%", StringType.INSTANCE)
                .setParameter("subjectCabinet", "%" + request.getSubjectnameCabinet() + "%", StringType.INSTANCE)
                .setParameter("subjectMine", "%" + request.getSubjectnameMine() + "%", StringType.INSTANCE);

        return (List<LinkRegister>) getList(q);
    }
}
