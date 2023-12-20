package org.edec.report.debtorsReport.manager;

import org.edec.dao.MineDAO;
import org.edec.report.debtorsReport.model.DebtorContactModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MineDebtorManager extends MineDAO {
    public List<DebtorContactModel> getContactInfoByListId(List<Long> ids) {
        String idStr = ids.stream().filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(", "));

        String query = "select " +
                       "    Код as idStudentMine, " +
                       "    Фамилия + ' ' +  Имя + ' ' + Отчество as fio, " +
                       "    Телефон_ПП as phone, " +
                       "    E_Mail as email\n" +
                       "from " +
                       "    Все_Студенты " +
                       "where " +
                       "    Код in (" + idStr + ")";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idStudentMine", LongType.INSTANCE)
                .addScalar("fio")
                .addScalar("phone")
                .addScalar("email")
                .setResultTransformer(Transformers.aliasToBean(DebtorContactModel.class));

        return (List<DebtorContactModel>)getList(q);
    }
}
