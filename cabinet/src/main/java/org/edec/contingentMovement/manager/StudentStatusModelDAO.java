package org.edec.contingentMovement.manager;

import org.edec.contingentMovement.model.StudentStatusModel;
import org.edec.dao.DAO;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;

import java.util.List;

public class StudentStatusModelDAO extends DAO {

    public List<StudentStatusModel> getCustomQuery (String stringQuery) {
        Query q = getSession().createSQLQuery(stringQuery).addEntity(StudentStatusModel.class);
        List<StudentStatusModel> list = q.list();
        return (List<StudentStatusModel>) getList(q);
    }

    public List<StudentStatusModel> getAll () throws Exception {
        return (List<StudentStatusModel>) getList(getSession().createQuery("from StudentStatusModel"));
    }

    public StudentStatusModel get (Long id) throws Exception {
        Query q = getSession().createQuery("from StudentStatusModel where id_student_semester_status = :id");
        q.setLong("id", id);
        List<?> list = getList(q);
        return list.size() == 0 ? null : (StudentStatusModel) list.get(0);
    }

    public StudentStatusModel create (StudentStatusModel object) throws Exception {
        try {
            begin();
            getSession().save(object);
            commit();
            return object;
        } catch (HibernateException e) {
            rollback();
            throw new Exception("Не могу создать StudentStatusModel c id : " + object.getId(), e);
        } finally {
            close();
        }
    }

    public void delete (StudentStatusModel object) throws Exception {
        try {
            begin();
            getSession().delete(object);
            commit();
        } catch (HibernateException e) {
            rollback();
            throw new Exception("Не могу удалить StudentStatusModel c id : " + object.getId(), e);
        } finally {
            close();
        }
    }

    public StudentStatusModel update (StudentStatusModel object) throws Exception {
        try {
            begin();
            getSession().update(object);
            commit();
            return object;
        } catch (HibernateException e) {
            rollback();
            throw new Exception("Не могу обновить StudentStatusModel c id : " + object.getId(), e);
        } finally {
            getSession().flush();
            getSession().clear();
            close();
        }
    }

    public org.edec.contingentMovement.model.StudentStatusModel getStasuses (Long idSSS) {
        try {
            String sql = "SELECT id_student_semester_status AS id,\n" + "is_government_financed AS governmentFinanced, \n" +
                    "is_put_app_for_social_grant AS putAppForSocialGrant, \n" + "is_get_social_grant AS getSocialGrant, \n" +
                    "is_deducted AS deducted, \n" + "is_scientificwork AS scientificWork, \n" + "is_publicwork AS publicWork, \n" +
                    "is_chernobolec AS chernobolec, \n" + "is_sirota AS sirota, \n" + "is_invalid AS invalid,\n" +
                    "is_sessionprolongation AS sessionProlongation, \n" + "is_combatants AS combatants, \n" +
                    "is_academicleave AS academicLeave, \n" + "is_listener AS listener, \n" +
                    "is_trustagreement AS trustAGreement, \n" + "is_seconddegree AS secondDegree, \n" +
                    "is_group_leader AS groupLeader, \n" + "is_educationcomplete AS educationComplete, \n" +
                    "prolongationbegindate AS prolongationBeginDate,\n" + "prolongationenddate AS prolongationEndDate, \n" +
                    "dateofscholarshipbegin AS dateOfScholarShipBegin, \n" + "dateofscholarshipend AS dateOfScholarShipEnd, \n" +
                    "is_ord_scholarship AS ordScholarShip, \n" + "is_ord_transfer AS ordTransfer, \n" +
                    "is_transfer_student AS transferStudent, \n" + "is_early_session_complited AS earlySessionComplited,\n" +
                    "is_specialeducation AS specialEducaion,\n" + "is_get_academic AS getAcademic,\n" +
                    "is_get_social AS getSocial,\n" + "is_get_social_increased AS getSocialIncreased,\n" +
                    "is_transfered AS transfered,\n" + "is_transfered_conditionally AS transferedConditionally\n" + "FROM \n" +
                    "student_semester_status\n" + "WHERE id_student_semester_status=:idSSS";
            Query q = getSession().createSQLQuery(sql)
                    .addScalar("id", LongType.INSTANCE)
                    .addScalar("governmentFinanced", BooleanType.INSTANCE)
                    .addScalar("putAppForSocialGrant", BooleanType.INSTANCE)
                    .addScalar("getSocialGrant", BooleanType.INSTANCE)
                    .addScalar("deducted", BooleanType.INSTANCE)
                    .addScalar("scientificWork", BooleanType.INSTANCE)
                    .addScalar("publicWork", BooleanType.INSTANCE)
                    .addScalar("chernobolec", BooleanType.INSTANCE)
                    .addScalar("sirota", BooleanType.INSTANCE)
                    .addScalar("invalid", BooleanType.INSTANCE)
                    .addScalar("sessionProlongation", BooleanType.INSTANCE)
                    .addScalar("combatants", BooleanType.INSTANCE)
                    .addScalar("academicLeave", BooleanType.INSTANCE)
                    .addScalar("listener", BooleanType.INSTANCE)
                    .addScalar("trustAGreement", BooleanType.INSTANCE)
                    .addScalar("secondDegree", BooleanType.INSTANCE)
                    .addScalar("groupLeader", BooleanType.INSTANCE)
                    .addScalar("educationComplete", BooleanType.INSTANCE)
                    .addScalar("prolongationBeginDate", DateType.INSTANCE)
                    .addScalar("prolongationEndDate", DateType.INSTANCE)
                    .addScalar("dateOfScholarShipBegin", DateType.INSTANCE)
                    .addScalar("dateOfScholarShipEnd", DateType.INSTANCE)
                    .addScalar("ordScholarShip", BooleanType.INSTANCE)
                    .addScalar("ordTransfer", BooleanType.INSTANCE)
                    .addScalar("transferStudent", BooleanType.INSTANCE)
                    .addScalar("earlySessionComplited", BooleanType.INSTANCE)
                    .addScalar("specialEducaion", BooleanType.INSTANCE)
                    .addScalar("getAcademic", BooleanType.INSTANCE)
                    .addScalar("getSocial", BooleanType.INSTANCE)
                    .addScalar("getSocialIncreased", BooleanType.INSTANCE)
                    .addScalar("transfered", BooleanType.INSTANCE)
                    .addScalar("transferedConditionally", BooleanType.INSTANCE)
                    .setResultTransformer(
                            Transformers.aliasToBean(org.edec.contingentMovement.model.StudentStatusModel.class));
            q.setLong("idSSS", idSSS);
            List<org.edec.contingentMovement.model.StudentStatusModel> list = (List<org.edec.contingentMovement.model.StudentStatusModel>) getList(
                    q);
            if (list.size() <= 0) {
                return null;
            }
            return list.get(0);
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return null;
        }
    }

}

