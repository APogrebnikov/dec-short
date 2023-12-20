package org.edec.utility.parser.manager;

import org.edec.dao.DAO;
import org.edec.synchroMine.model.eso.entity.Parent;
import org.hibernate.Query;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;

import java.util.List;

public class ParseParentManager extends DAO {

    public Long getOneStudentCardId(String studentF, String studentN, String studentP, String studentGroup) {
        //Получение студента по ФИО
        String query = "SELECT sc.id_studentcard as idStudentcard FROM studentcard sc\n" +
                "INNER JOIN humanface hf ON hf.id_humanface=sc.id_humanface\n" +
                "INNER JOIN student_semester_status sss ON sss.id_studentcard=sc.id_studentcard\n" +
                "INNER JOIN link_group_semester lgs ON lgs.id_link_group_semester=sss.id_link_group_semester\n" +
                "INNER JOIN dic_group dg ON dg.id_dic_group=lgs.id_dic_group\n" +
                "WHERE family like :studentF\n" +
                "AND name like :studentN\n" +
                "AND patronymic like :studentP\n" +
                "AND dg.groupname like :studentGroup\n" +
                "GROUP BY sc.id_studentcard";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idStudentcard", LongType.INSTANCE)
                .setParameter("studentF", "%" + studentF + "%")
                .setParameter("studentN", "%" + studentN + "%")
                .setParameter("studentP", "%" + studentP + "%")
                .setParameter("studentGroup", studentGroup);

        List<Long> list = (List<Long>) getList(q);
        if (list != null && list.size() != 0) {
            return list.get(0);
        }
        return null;
    }

    public boolean createParent(Parent parent) {
        //Получение студента по ФИО
        String query = "INSERT INTO parent (" +
                "name,\n" +
                "family,\n" +
                "patronymic,\n" +
                "username,\n" +
                "password,\n" +
                "id_studentcard,\n" +
                "is_active,\n" +
                "email,\n" +
                "start_page" +
                ")" +
                "VALUES (" +
                ":name,\n" +
                ":family,\n" +
                ":patronymic,\n" +
                ":username,\n" +
                ":password,\n" +
                ":id_studentcard,\n" +
                ":is_active,\n" +
                ":email,\n" +
                ":start_page" +
                ")";

        Query q = getSession().createSQLQuery(query)
                .setParameter("name", parent.getName())
                .setParameter("family", parent.getFamily())
                .setParameter("patronymic", parent.getPatronymic())
                .setParameter("username", parent.getUsername())
                .setParameter("password", parent.getPassword())
                .setParameter("id_studentcard", parent.getIdStudentCard())
                .setParameter("is_active", parent.getActive(), BooleanType.INSTANCE)
                .setParameter("email", parent.getEmail())
                .setParameter("start_page", parent.getStartPage());

        return executeUpdate(q);
    }



}
