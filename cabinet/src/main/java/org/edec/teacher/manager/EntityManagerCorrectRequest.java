package org.edec.teacher.manager;

import org.edec.dao.DAO;
import org.edec.teacher.model.correctRequest.CorrectRequestModel;
import org.edec.teacher.model.dao.RegisterRequestESOModel;
import org.edec.teacher.model.register.RegisterRowModel;
import org.edec.teacher.model.registerRequest.GroupModel;
import org.edec.teacher.model.registerRequest.RegisterRequestModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EntityManagerCorrectRequest extends DAO {

    public List<CorrectRequestModel> getRequestHistory (long idHumanface, long idSRH, int foc) {
        // TODO: написать запрос для получения всех корректировок
        String query = "";

        Query q = getSession().createSQLQuery(query)
                              .setResultTransformer(Transformers.aliasToBean(CorrectRequestModel.class));

        return (List<CorrectRequestModel>) getList(q);
    }

    public List<CorrectRequestModel> getRequestsForSubject (long idLGSS, int foc) {
        // TODO: написать запрос для получения всех корректировок
        String query = "";

        Query q = getSession().createSQLQuery(query)
                .setResultTransformer(Transformers.aliasToBean(CorrectRequestModel.class));

        return (List<CorrectRequestModel>) getList(q);
    }

    public boolean createRequest (CorrectRequestModel correctRequestModel) {
        String query = "INSERT INTO correct_request (id_humanface," + " id_sessionratinghistory," + " status," +
                " newrating, oldrating," +" dateofapplying) " + "VALUES (:idHf, :idSRH, :status, :newRating, :oldRating, :dateOfApplying) RETURNING id_correct_request";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idHf", correctRequestModel.getIdHumanface())
                .setParameter("idSRH", correctRequestModel.getIdSRH())
                .setParameter("status", correctRequestModel.getStatus())
                .setParameter("newRating", correctRequestModel.getNewRating())
                .setParameter("oldRating", correctRequestModel.getOldRating())
                .setDate("dateOfApplying", correctRequestModel.getDateOfApplying());
        List<Long> list = (List<Long>) getList(q);
        if(list.size() == 0){
            return false;
        } else {
            correctRequestModel.setId(list.get(0));
            return true;
        }
    }

    public boolean updateRequest (CorrectRequestModel correctRequestModel) {
        String query = "UPDATE correct_request SET id_humanface = :idHf, id_sessionratinghistory = :idSRH, status = :status, newrating = :newRating, dateofapplying = :dateOfApplying WHERE id_correct_request = :idCorrectRequest";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idHf", correctRequestModel.getIdHumanface())
                .setParameter("idSRH", correctRequestModel.getIdSRH())
                .setParameter("status", correctRequestModel.getStatus())
                .setParameter("newRating", correctRequestModel.getNewRating())
                .setDate("dateOfApplying", correctRequestModel.getDateOfApplying())
                .setParameter("idCorrectRequest", correctRequestModel.getId());
        return executeUpdate(q);
    }

    public boolean updateCorrectAfterSign (CorrectRequestModel correctRequestModel) {
        String query =  "UPDATE correct_request SET thumbprint = :thumbprint, certnumber = :certnumber, file_path = :filePath, dateofapplying = :dateofapplying WHERE id_correct_request = :id";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("thumbprint", correctRequestModel.getThumbprint())
                .setParameter("certnumber", correctRequestModel.getCertnumber())
                .setParameter("filePath", correctRequestModel.getFilePath())
                .setDate("dateofapplying", correctRequestModel.getDateOfApplying())
                .setParameter("id", correctRequestModel.getId());
        return executeUpdate(q);
    }

    public List<Long> checkRequestsForExisting(List<Long> innerIds) {
        String query =  "SELECT id_sessionratinghistory FROM correct_request WHERE id_sessionratinghistory IN (:innerIds) AND file_path IS NOT NULL";
        Query q = getSession().createSQLQuery(query);
        q.setParameterList("innerIds", innerIds);
        return (List<Long>) getList(q);
    }

    public List<CorrectRequestModel> getCorrectRequest(List<Long> srhList){
        String query = "select\n" +
                       "    id_sessionratinghistory as idSRH,   \n" +
                       "    oldrating as oldRating,\n" +
                       "    newrating as newRating,\n" +
                       "    id_humanface as idHumanface,\n" +
                       "    certnumber as certnumber\n" +
                       "from correct_request\n" +
                       "where id_sessionratinghistory in (" + srhList.stream().map(Objects::toString).collect(Collectors.joining(",")) + ")";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idSRH", LongType.INSTANCE)
                              .addScalar("oldRating", IntegerType.INSTANCE)
                              .addScalar("newRating", IntegerType.INSTANCE)
                              .addScalar("idHumanface", LongType.INSTANCE)
                              .addScalar("certnumber")
                              .setResultTransformer(Transformers.aliasToBean(CorrectRequestModel.class));

        return ((List<CorrectRequestModel>) getList(q));
    }
}
