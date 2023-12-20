package org.edec.synchroMine.manager.orderSynchro;

import org.edec.dao.DAO;
import org.edec.synchroMine.model.mine.Order;
import org.edec.synchroMine.model.mine.OrderAction;
import org.edec.synchroMine.model.mine.OrderActionStudent;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;

import java.util.Collections;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class EntityManagerOrderMineESO extends DAO {
    public Order getOrderDBO (Long idOrder) {
        String query = "SELECT OH.descriptionspec AS description, OH.ordernumber AS number, OH.dateofbegin AS dateCreated,\n" +
                       "\tOH.dateofend AS dateSigned, INST.otherdbid AS instituteId, EMPcreator.other_dbuid AS creatorEmpId,\n" +
                       "\tEMPhelper.other_dbuid AS helperId, EMPconfirm.other_dbuid AS confirmerId\n" + "FROM order_head OH\n" +
                       "\tINNER JOIN order_rule ORR ON OH.id_order_rule = ORR.id_order_rule\n" +
                       "\tINNER JOIN semester SEM ON OH.semester = SEM.id_semester\n" +
                       "\tINNER JOIN institute INST ON SEM.id_institute = INST.id_institute\n" +
                       "\tINNER JOIN EMPLOYEE EMPcreator ON EMPcreator.id_humanface = OH.id_humanface\n" +
                       "\tINNER JOIN link_rule_employee LRE ON ORR.id_order_rule = LRE.id_rule AND LRE.actionrule = 0\n" +
                       "\tINNER JOIN employee EMPhelper ON LRE.id_employee = EMPhelper.id_employee\n" +
                       "\tINNER JOIN employee EMPconfirm ON LRE.id_employee = EMPconfirm.id_employee\n" +
                       "WHERE OH.id_order_head = :idOrder";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("description")
                              .addScalar("number")
                              .addScalar("dateCreated")
                              .addScalar("dateSigned")
                              .addScalar("instituteId", StandardBasicTypes.LONG)
                              .addScalar("creatorEmpId", StandardBasicTypes.LONG)
                              .addScalar("helperId", StandardBasicTypes.INTEGER)
                              .addScalar("confirmerId", StandardBasicTypes.INTEGER)
                              .setResultTransformer(Transformers.aliasToBean(Order.class));
        q.setParameter("idOrder", idOrder);
        List<Order> orders = (List<Order>) getList(q);
        return orders.size() == 0 ? null : orders.get(0);
    }

    public List<OrderAction> getOrderAction (Long idOrder) {
        String query = "SELECT OS.otherdbid AS idAction\n" + "\tFROM link_order_section LOS\n" +
                       "\tINNER JOIN order_section OS ON LOS.id_order_section = OS.id_order_section\n" +
                       "\tINNER JOIN link_order_student_status LOSS USING(id_link_order_section)\n" +
                       "WHERE LOS.id_order_head = :idOrder\n" + "GROUP BY OS.otherdbid";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idAction", StandardBasicTypes.LONG)
                              .setResultTransformer(Transformers.aliasToBean(OrderAction.class));
        q.setParameter("idOrder", idOrder);
        return (List<OrderAction>) getList(q);
    }

    public List<OrderActionStudent> getOrderActionStudent (Long idOrder, Long idAction) {
        String query = "SELECT DISTINCT ON (LGS.course, DG.groupname, HF.family, HF.name, LOSS.id_link_order_student_status)\n" +
                       "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio, SC.other_dbuid AS idStudent,\n" + "\tCASE\n" +
                       "\t\tWHEN SSS.is_deducted = 1 THEN 3\n" + "\t\tWHEN SSS.is_educationcomplete = 1 THEN 4\n" +
                       "\t\tWHEN SSS.is_deducted = 0 AND SSS.is_educationcomplete = 0 THEN 1\n" + "\tEND AS idStatus, \n" + "\tCASE\n" +
                       "\t\tWHEN SSS.is_government_financed = 1 THEN 1\n" + "\t\tWHEN SSS.is_trustagreement = 1 THEN 2\n" +
                       "\t\tWHEN SSS.is_government_financed = 0 THEN 3\n" + "\tEND AS idStudyFormBefore,\n" + "\tCASE\n" +
                       "\t\tWHEN SSS.is_government_financed = 1 THEN 1\n" + "\t\tWHEN SSS.is_trustagreement = 1 THEN 2\n" +
                       "\t\tWHEN SSS.is_government_financed = 0 THEN 3\n" + "\tEND AS idStudyForm,\n" +
                       "\tCOALESCE(LOS.foundation, OS.foundation, '') AS foundation, LOSS.first_date AS dateFrom, LOSS.second_date AS dateTo,\n" +
                       "\tINST.otherdbid AS idInstitute, LGS.otherdbid AS idGroupFrom, LGS2.otherdbid AS idGroupNextCourse,\n" +
                       "\tDG.groupname, SC.recordbook, DIR.code AS directioncode, DIR.title AS directionname,\n" +
                       "\tCUR.directioncode AS profilecode, CUR.specialitytitle AS profilename,\n" + "\tCASE\n" +
                       "\t\tWHEN CUR.formofstudy = 1 THEN 'очная форма'\n" +
                       "\t\tWHEN CUR.formofstudy = 2 AND CUR.distancetype = 2 THEN 'заочная форма'\n" +
                       "\t\tWHEN CUR.formofstudy = 2 AND CUR.distancetype = 1 THEN 'очно-заочная форма'\n" + "\tEND AS formofstudy, " +
                       "\tCASE\n" + "\t\tWHEN SSS.is_government_financed = 1 THEN 'за счет бюджетных ассигнований федерального бюджета'\n" +
                       "\t\tWHEN SSS.is_government_financed = 0 THEN 'на условиях договора об оказании платных образовательных услуг'\n" +
                       "\tEND AS foundationofstudy, SSS.is_trustagreement,\n" + "\tCASE\n" +
                       "\t\tWHEN (OS.name LIKE 'Хорошо' OR OS.name LIKE 'Продление(Хорошо)') THEN 2\n" +
                       "\t\tWHEN (OS.name LIKE '\"Отлично\" и \"хорошо\"' OR OS.name LIKE 'Продление(Хорошо и Отлично)') THEN 3\n" +
                       "\t\tWHEN (OS.name LIKE 'Отлично' OR OS.name LIKE 'Продление(Отлично)') THEN 4\n" + "\tEND AS sessresult, \n" +
                       "\tCUR.qualification, LGS.course, OS.name AS sectionName, LOSS.additional\n" + "FROM\n" +
                       "\tlink_order_section LOS\n" + "\tINNER JOIN order_section OS ON LOS.id_order_section = OS.id_order_section\n" +
                       "\tINNER JOIN link_order_student_status LOSS ON LOS.id_link_order_section = LOSS.id_link_order_section\n" +
                       "\tINNER JOIN student_semester_status SSS ON LOSS.id_student_semester_status = SSS.id_student_semester_status\n" +
                       "\tINNER JOIN studentcard SC ON SSS.id_studentcard = SC.id_studentcard\n" +
                       "\tINNER JOIN humanface HF ON SC.id_humanface = HF.id_humanface\n" +
                       "\tINNER JOIN link_group_semester LGS ON LGS.id_link_group_semester = SSS.id_link_group_semester\n" +
                       "\tINNER JOIN dic_group DG ON LGS.id_dic_group = DG.id_dic_group\n" +
                       "\tINNER JOIN curriculum CUR ON DG.id_curriculum = CUR.id_curriculum\n" +
                       "\tINNER JOIN direction DIR ON CUR.id_direction = DIR.id_direction\n" +
                       "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                       "\tINNER JOIN institute INST ON SEM.id_institute = INST.id_institute\n" +
                       "\tLEFT JOIN link_group_semester LGS2 ON LGS.id_dic_group = LGS2.id_dic_group AND LGS2.course = (LGS.course + 1)\n" +
                       "WHERE OS.otherdbid = :idAction AND LOS.id_order_head = :idOrder\n" +
                       "ORDER BY LGS.course, DG.groupname, HF.family, HF.name ";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("idStudent", StandardBasicTypes.LONG)
                              .addScalar("idStatus", StandardBasicTypes.LONG)
                              .addScalar("foundation")
                              .addScalar("dateFrom")
                              .addScalar("dateTo")
                              .addScalar("idInstitute", StandardBasicTypes.LONG)
                              .addScalar("idGroupFrom", StandardBasicTypes.LONG)
                              .addScalar("groupname")
                              .addScalar("recordbook")
                              .addScalar("directioncode")
                              .addScalar("directionname")
                              .addScalar("profilecode")
                              .addScalar("profilename")
                              .addScalar("formofstudy")
                              .addScalar("foundationofstudy")
                              .addScalar("sessresult")
                              .addScalar("is_trustagreement")
                              .addScalar("qualification")
                              .addScalar("course")
                              .addScalar("idStudyFormBefore", LongType.INSTANCE)
                              .addScalar("idStudyForm", LongType.INSTANCE)
                              .addScalar("sectionName")
                              .addScalar("additional")
                              .addScalar("idGroupNextCourse", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(OrderActionStudent.class));
        q.setLong("idAction", idAction).setLong("idOrder", idOrder);
        return (List<OrderActionStudent>) getList(q);
    }
}
