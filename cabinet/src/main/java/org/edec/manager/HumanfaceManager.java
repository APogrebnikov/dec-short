package org.edec.manager;

import org.edec.dao.DAO;
import org.edec.model.HumanfaceModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;


public class HumanfaceManager extends DAO {
    public HumanfaceModel getHumanById (Long idHum) {
        String query = "SELECT family, name, patronymic, id_humanface AS idHum FROM humanface WHERE id_humanface = :idHum";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("family")
                              .addScalar("name")
                              .addScalar("patronymic")
                              .addScalar("idHum", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(HumanfaceModel.class));
        q.setLong("idHum", idHum);
        List<?> list = getList(q);
        return list.size() == 0 ? null : (HumanfaceModel) list.get(0);
    }
}
