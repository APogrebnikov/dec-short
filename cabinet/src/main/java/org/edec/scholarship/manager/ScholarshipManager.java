package org.edec.scholarship.manager;

import org.edec.dao.DAO;
import org.edec.scholarship.model.ScholarshipHistoryDTO;
import org.edec.scholarship.model.ScholarshipModel;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;

import java.util.List;

public class ScholarshipManager extends DAO{

    public boolean updateScholarship(ScholarshipModel scholarship){
        //language=PostgreSQL
        String query = "UPDATE studentcard\n" +
                       "SET order_info = jsonb_set(" +
                       "    order_info, " +
                       "    CAST('{' || :jsonName || '}' AS text[]),\n" +
                       "    jsonb_build_object(" +
                       "'dateFrom', :dateFrom, " +
                       "'dateTo', :dateTo), true) \n" +
                       "WHERE id_studentcard = :idStudentCard";

        Query q = getSession().createSQLQuery(query);
               q.setParameter("jsonName", scholarship.getType().getJsonName())
                .setParameter("dateFrom", DateConverter.convertDateToStringByFormat(scholarship.getDateFrom(), "yyyy-MM-dd"))
                .setParameter("dateTo", DateConverter.convertDateToStringByFormat(scholarship.getDateTo(), "yyyy-MM-dd"))
                .setParameter("idStudentCard", scholarship.getIdStudentCard());

        return executeUpdate(q);
    }

    public boolean deleteScholarship(ScholarshipModel scholarship){
        String query = "UPDATE studentcard\n" +
                       "SET order_info = order_info - :scholarshipType\n" +
                       "WHERE id_studentcard = :idStudentCard \n";

        Query q = getSession().createSQLQuery(query)
                .setParameter("scholarshipType", scholarship.getType().getJsonName())
                .setParameter("idStudentCard", scholarship.getIdStudentCard());

        return executeUpdate(q);
    }

    public String getScholarshipsByStudent(Long idStudentcard){
        String query = "SELECT CAST(order_info AS TEXT)\n" +
                       "FROM studentcard \n" +
                       "WHERE id_studentcard = :idSc \n";

        Query q = getSession().createSQLQuery(query)
                .setLong("idSc", idStudentcard);

        return ((List<String>) getList(q)).get(0);
    }

    public List<ScholarshipHistoryDTO> getScholarshipHistory(Long idStudentCard){
        String query = "select id_scholarship_history as idScholarshipHistory,\n" +
                       "       id_mine                as idMine,\n" +
                       "       scholarship_type       as scholarshipType,\n" +
                       "       date_from              as dateFrom,\n" +
                       "       date_to                as dateTo,\n" +
                       "       date_cancel            as dateCancel,\n" +
                       "       id_mine_cancel         as idMineCancel,\n" +
                       "       size                   as size,\n" +
                       "       order_number as orderNumber,\n" +
                       "       cancel_order_number as cancelOrderNumber\n" +
                       "from scholarship_history\n" +
                       "where id_studentcard = :idStudentCard \n" +
                       "order by id_scholarship_history";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("idScholarshipHistory")
                              .addScalar("idMine")
                              .addScalar("scholarshipType")
                              .addScalar("dateFrom", DateType.INSTANCE)
                              .addScalar("dateTo", DateType.INSTANCE)
                              .addScalar("dateCancel", DateType.INSTANCE)
                              .addScalar("idMineCancel")
                              .addScalar("idStudentCard")
                              .addScalar("size")
                              .addScalar("orderNumber")
                              .addScalar("cancelOrderNumber")
                              .setResultTransformer(Transformers.aliasToBean(ScholarshipHistoryDTO.class))
                              .setLong("idStudentCard", idStudentCard);

        return (List<ScholarshipHistoryDTO>) getList(q);
    }
}
