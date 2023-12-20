package org.edec.signEditor.manager;

import org.edec.dao.DAO;
import org.edec.newOrder.model.createOrder.OrderCreateTypeModel;
import org.edec.signEditor.model.OrderRuleModel;
import org.edec.signEditor.model.OrderSectionModel;
import org.edec.signEditor.model.RuleModel;
import org.edec.signEditor.model.SignatoryModel;
import org.edec.signEditor.model.dao.EmployeeModelEso;
import org.edec.signEditor.model.dao.SignatoryModelEso;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;

import java.util.*;

public class SignatoryEditorManager extends DAO {

    public List<RuleModel> getRuleList(Long idInstitute) {
        String query = "SELECT\n" +
                "\tORR.id_order_rule as idRule,\n" +
                "\tORR.name as rule\n" +
                "FROM\n" +
                "\torder_rule ORR\n" +
                "WHERE\n" +
                "\tORR.id_institute = " + idInstitute + "\n" +
                "\tORDER BY\n" +
                "\t name";

        Query q = getSession().createSQLQuery(query)
                .addScalar("rule")
                .addScalar("idRule", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(RuleModel.class));
        return (List<RuleModel>) getList(q);
    }

    public List<SignatoryModelEso> getSignatoryList(Long idRule) {
        String query = "SELECT\n" +
                "\tEMP.id_employee as idEmp,\n" +
                "\tHF.name as name,\n" +
                "\tHF.family as family,\n" +
                "\tHF.patronymic as patronymic,\n" +
                "\tLRE.id_link_rule_employee as idLre,\n" +
                "\tORR.name as rule,\n" +
                "\tLRE.actionrule as role,\n" +
                "\tLRE.pos as pos,\n" +
                "\tLRE.post as post,\n" +
                "\tLRE.subquery as subquery,\n" +
                "\tLRE.sign as sign,\n" +
                "\tLRE.id_rule as idRule, \n" +
                "\tLRE.is_print as isPrint, \n" +
                "\tLRE.formofstudy as fos \n" +
                "FROM\n" +
                "\tlink_rule_employee LRE\n" +
                "\tINNER JOIN order_rule ORR ON LRE.id_rule = ORR.id_order_rule\n" +
                "\tINNER JOIN employee EMP ON LRE.id_employee = EMP.id_employee\n" +
                "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "WHERE\n" +
                "\tLRE.id_rule = " + idRule + "\n" +
                "\tORDER BY\n" +
                "\t rule, role, pos";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idEmp", LongType.INSTANCE)
                .addScalar("name").addScalar("family").addScalar("patronymic")
                .addScalar("idLre", LongType.INSTANCE)
                .addScalar("role").addScalar("rule").addScalar("pos").addScalar("post").addScalar("subquery")
                .addScalar("sign", BooleanType.INSTANCE).addScalar("isPrint", BooleanType.INSTANCE)
                .addScalar("idRule", LongType.INSTANCE).addScalar("fos")
                .setResultTransformer(Transformers.aliasToBean(SignatoryModelEso.class));
        return (List<SignatoryModelEso>) getList(q);
    }

    public List<EmployeeModelEso> getEmployeeList() {
        String query = "SELECT\n" +
                "\tEMP.id_employee AS idEmp,\n" +
                "\tHF.name AS name,\n" +
                "\tHF.family AS family,\n" +
                "\tHF.patronymic AS patronymic\n" +
                "FROM\n" +
                "\temployee EMP\n" +
                "\tINNER JOIN humanface HF USING (id_humanface)\n";

        Query q = getSession().createSQLQuery(query)
                .addScalar("idEmp", LongType.INSTANCE)
                .addScalar("name").addScalar("family").addScalar("patronymic")
                .setResultTransformer(Transformers.aliasToBean(EmployeeModelEso.class));
        return (List<EmployeeModelEso>) getList(q);
    }

    public boolean addSignatory(SignatoryModel signatory) {
        String query = "INSERT INTO link_rule_employee (id_rule, id_employee, actionrule, pos, post, sign,subquery,is_print" +
                (signatory.getFos() != null ? ",formofstudy" : "") + ")" +
                "VALUES (:idRule, :idEmp, :role, :pos, :post, :sign, :subquery, :isPrint" +
                (signatory.getFos() != null ? "," + signatory.getFos() : "") + ")";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idRule", signatory.getIdRule())
                .setParameter("idEmp", signatory.getEmployee().getIdEmp())
                .setParameter("role", signatory.getRole())
                .setParameter("pos", signatory.getPosition())
                .setParameter("post", signatory.getPost())
                .setParameter("sign", signatory.getSign())
                .setParameter("subquery", signatory.getSubquery())
                .setParameter("isPrint", signatory.getPrint());
        return executeUpdate(q);
    }

    public boolean updateSignatory(SignatoryModel signatory) {
        String query = "UPDATE link_rule_employee \n" +
                "SET id_rule = :idRule," +
                " id_employee = :idEmp," +
                " actionrule = :role," +
                " pos = :pos," +
                " post = :post," +
                " sign = :sign," +
                " subquery = :subquery," +
                " is_print = :isPrint" +
                ((signatory.getFos() != null) ? ",formofstudy = " + signatory.getFos() + "\n" : "\n") +
                "WHERE id_link_rule_employee = :idLre\n" +
                "\n";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idRule", signatory.getIdRule())
                .setParameter("idEmp", signatory.getEmployee().getIdEmp())
                .setParameter("role", signatory.getRole())
                .setParameter("pos", signatory.getPosition())
                .setParameter("post", signatory.getPost())
                .setParameter("sign", signatory.getSign())
                .setParameter("isPrint", signatory.getPrint())
                .setParameter("subquery", signatory.getSubquery())
                .setParameter("idLre", signatory.getIdLre());
        return executeUpdate(q);
    }

    public boolean removeSignatory(long idLre) {
        String query = "DELETE FROM link_rule_employee WHERE id_link_rule_employee = :idLre";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idLre", idLre);
        return executeUpdate(q);
    }

    public List<OrderRuleModel> getRuleModel(){
        String query = "select  \n" +
                "name, \n" +
                "description, \n" +
                "head_description as headDescription, \n" +
                "id_order_type as idOrderType, \n" +
                "id_institute as idInstitute, \n" +
                "form_of_control as formOfControl,\n" +
                "is_automatic as isAutomatic,\n" +
                "id_order_rule as idOrderRule"+
                "\n" +
                "from order_rule ";

        Query q = getSession().createSQLQuery(query)
                .addScalar("name")
                .addScalar("description")
                .addScalar("headDescription")
                .addScalar("idOrderType", LongType.INSTANCE)
                .addScalar("idInstitute", LongType.INSTANCE)
                .addScalar("formOfControl",  LongType.INSTANCE)
                .addScalar("isAutomatic", BooleanType.INSTANCE)
                .addScalar("idOrderRule", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderRuleModel.class));
        return (List<OrderRuleModel>) getList(q);

    }

    public void createRuleModel(String name, String description, String headDescription, Long idOrderType,
                                                      Long idInstitute, Long formOfControl, boolean isAutomatic){
        String query = "insert into order_rule (name, description, head_description, id_order_type, id_institute, " +
                "form_of_control, is_automatic) values ('"+name+"', '"+description+"', '"+headDescription+"', " +
                ""+idOrderType+", "+idInstitute+", "+formOfControl+", "+isAutomatic+")";

        executeUpdate(getSession().createSQLQuery(query));
    }

    public void updateOrderRule(Long idOrderRule, String name, String description, String headDescription, Long idOrderType,
                                Long idInstitute, Long formOfControl, boolean isAutomatic){

        String query = "update order_rule\n" +
                "set \n" +
                "name = '"+name+"', \n" +
                "description = '"+description+"',\n" +
                "head_description = '"+headDescription+"',\n" +
                "id_order_type = "+idOrderType+",\n" +
                "id_institute = "+idInstitute+",\n" +
                "form_of_control = "+formOfControl+",\n" +
                "is_automatic = "+isAutomatic+"\n" +
                "where\n" +
                "id_order_rule="+idOrderRule+"";

        executeUpdate(getSession().createSQLQuery(query));

    }

    public void deleteOrderRule(Long idOrderRule){
        String queryLinkRuleEmployee = "delete from link_rule_employee where id_rule= "+idOrderRule;
        executeUpdate(getSession().createSQLQuery(queryLinkRuleEmployee));
        String querySection = "delete from order_section where id_order_rule= "+idOrderRule;
        executeUpdate(getSession().createSQLQuery(querySection));
        String query = "delete from order_rule where id_order_rule = "+idOrderRule;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public List<OrderSectionModel> getOrderSection(Long idOrderRule) {

        String query ="select description, layout, name, foundation, otherdbid, id_order_section as idOrderSection from order_section where id_order_rule = "+idOrderRule+"";

        Query q = getSession().createSQLQuery(query)
                .addScalar("description")
                .addScalar(("layout"), IntegerType.INSTANCE)
                .addScalar("name")
                .addScalar("foundation")
                .addScalar("otherdbid", LongType.INSTANCE)
                .addScalar("idOrderSection", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(OrderSectionModel.class));

        return (List<OrderSectionModel>) getList(q);

    }

    public void createOrderSection(String description, Integer layout, String name, String foundation,Long otherdbid, Long idOrderRule){
        String query = "insert into order_section (description, layout, name, foundation, otherdbid, id_order_rule) values  \n" +
                "('"+description+"', "+layout+", '"+name+"', '"+foundation+"', "+otherdbid+", "+idOrderRule+")";

        executeUpdate(getSession().createSQLQuery(query));
    }

    public void updateOrderSection(Long idOrderSection, String description, Integer layout, String name, String foundation,Long otherdbid){

        String query = "update order_section set \n" +
                "description = '"+description+"',\n" +
                "layout = "+layout+",\n" +
                "name = '"+name+"',\n" +
                "foundation = '"+foundation+"',\n" +
                "otherdbid = "+otherdbid+"\n" +
                "where id_order_section = "+idOrderSection+"";

        executeUpdate(getSession().createSQLQuery(query));

    }

    public void deleteOrderSection(Long idOrderSection){

        String query = "delete from order_section where id_order_section= "+idOrderSection+";";
        executeUpdate(getSession().createSQLQuery(query));

    }

    public List<OrderCreateTypeModel> getListOrderType(){
        String query = "SELECT id_order_type as id, name from order_type order by position";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("name")
                              .setResultTransformer(Transformers.aliasToBean(OrderCreateTypeModel.class));
        return (List<OrderCreateTypeModel>) getList(q);
    }
}
