package org.edec.mine.dao.student;

import org.edec.cloud.sync.mine.api.student.data.StudentCurrentSemesterDto;
import org.edec.cloud.sync.mine.api.student.data.StudentForComparingFilter;
import org.edec.dao.DAO;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import java.util.List;

public class StudentDao extends DAO {

    public List<StudentCurrentSemesterDto> getHumafncesByFilter(StudentForComparingFilter filter) {

        String query = "select hf.id_humanface as idHumanface, hf.family, hf.name, hf.patronymic, hf.foreigner,\n" +
                "    case when string_agg(dep.fulltitle, ',') is null then string_agg(distinct dg.groupname, ',')\n" +
                "        when string_agg(dg.groupname, ',') is null then string_agg(distinct dep.fulltitle || '(' || er.rolename || ')', ',')\n" +
                "        else string_agg(distinct dep.fulltitle || '(' || er.rolename || ')', ',') || ',' || string_agg(distinct dg.groupname, ',')\n" +
                "end as groupname\n" +
                "from humanface hf\n" +
                "  left join employee emp using (id_humanface)\n" +
                "  left join link_employee_department led using (id_employee)\n" +
                "  left join employee_role er using (id_employee_role)\n" +
                "  left join department dep using (id_department)\n" +
                "  left join studentcard sc using (id_humanface)\n" +
                "  left join student_semester_status sss using (id_studentcard)\n" +
                "  left join link_group_semester lgs using (id_link_group_semester)\n" +
                "  left join dic_group dg using (id_dic_group)\n" +
                "where hf.family like '%" + filter.getFamilyCabinet() + "%'\n" +
                "  and hf.name like '%" + filter.getNameCabinet() + "%'\n" +
                "  and hf.patronymic like '%" + filter.getPatronymicCabinet() + "%'\n" +
                "group by hf.id_humanface\n" +
                "having case when string_agg(dep.fulltitle, ',') is null then string_agg(distinct dg.groupname, ',')\n" +
                "            when string_agg(dg.groupname, ',') is null then string_agg(distinct dep.fulltitle || '(' || er.rolename || ')', ',')\n" +
                "            else string_agg(distinct dep.fulltitle || ' (' || er.rolename || ')', ',') || ',' || string_agg(distinct dg.groupname, ',')\n" +
                "end ilike '%" + filter.getGroupname() + "%'";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idHumanface").addScalar("foreigner")
                .addScalar("family").addScalar("name").addScalar("patronymic").addScalar("groupname")
                .setResultTransformer(Transformers.aliasToBean(StudentCurrentSemesterDto.class));
        return (List<StudentCurrentSemesterDto>) getList(q);
    }
}
