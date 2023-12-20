package org.edec.orderAttach.manager;

import lombok.NonNull;
import org.edec.dao.DAO;
import org.edec.orderAttach.model.OrderWithAttachModel;
import org.edec.orderAttach.model.dao.OrderAttachDAOModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;

public class OrderAttachDAO extends DAO {
    private static final String CHECK_ON_SIGN_ORDER_ATTACH = "SELECT cert_number IS NOT NULL FROM order_attach WHERE id_order_attach = :idOrderAttach";

    private static final String GET_ORDER_ATTACHES = "SELECT ORR.name AS \"orderType\", CAST(OH.id_order_head AS INTEGER) AS \"idOrder\", OH.descriptionspec AS \"description\",\n" +
            "\tOH.ordernumber AS \"number\", OH.order_url AS \"url\", OH.dateofbegin AS \"dateCreated\",\n" +
            "\tCAST(OA.id_order_attach AS INTEGER) AS \"idOrderAttach\", OA.cert_number AS \"certNumber\",\n" +
            "\tOA.file_name AS \"fileName\", OA.params AS \"params\", RT.name AS \"reportType\", OA.certfio AS \"certFioAttach\"\n" +
            "FROM order_head OH\n" +
            "\tINNER JOIN order_rule ORR USING (id_order_rule)\n" +
            "\tINNER JOIN order_attach OA USING (id_order_head)\n" +
            "\tINNER JOIN report_type RT USING (id_report_type)";

    private static final String UPDATE_ORDER_AFTER_SIGN = "UPDATE order_attach\n" +
            "SET cert_number = :certnumber, certfio = :certfio, file_name = :fileName\n" +
            "WHERE id_order_attach = :idOrderAttach";

    public List<OrderAttachDAOModel> getOrderAttaches() {
        Query q = getSession().createSQLQuery(GET_ORDER_ATTACHES)
                .setResultTransformer(Transformers.aliasToBean(OrderAttachDAOModel.class));
        return (List<OrderAttachDAOModel>) getList(q);
    }

    public boolean checkOnSignAttachFile(Long idOrderAttach) {
        Query q = getSession().createSQLQuery(CHECK_ON_SIGN_ORDER_ATTACH)
                .setParameter("idOrderAttach", idOrderAttach);
        List<?> list = getList(q);
        return (Boolean) list.get(0);
    }

    public boolean updateOrderAttachAfterSign(@NonNull String certnumber, @NonNull String certfio, @NonNull String fileName, @NonNull Long idOrderAttach) {
        return executeUpdate(getSession().createSQLQuery(UPDATE_ORDER_AFTER_SIGN)
                .setParameter("certnumber", certnumber)
                .setParameter("certfio", certfio)
                .setParameter("fileName", fileName)
                .setParameter("idOrderAttach", idOrderAttach));
    }
}
