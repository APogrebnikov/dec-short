package org.edec.curriculumScan.manager;

import org.apache.log4j.Logger;
import org.edec.curriculumScan.model.*;
import org.edec.dao.DAO;
import org.edec.utility.constants.FormOfControlConst;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.FloatType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CurriculumScanDAOManager extends DAO {

    public final Logger log = Logger.getLogger(CurriculumScanDAOManager.class.getName());

    /**
     * Основная функция сохранения УП, включающая сохранения дочерних элементов
     *
     * @param curriculum
     * @return
     */
    public Curriculum createCurriculum (Curriculum curriculum) {
        try {
            String query = "INSERT INTO curriculum \n" + "(\n" +
                    //"id_direction,\n" +
                    "formofstudy,\n" + "distancetype,\n" + "periodofstudy,\n" + "qualification,\n" + "enter_school_year,\n" +
                    "generation,\n" +
                    //"id_chair,\n" +
                    "specialitytitle,\n" + "directioncode,\n" + "qualificationcode,\n" + "programcode,\n" + "planfilename,\n" +
                    "created_school_year\n" + ")\n" + "VALUES \n" + "(\n" +
                    //":id_direction,\n" +
                    ":formofstudy,\n" + ":distancetype,\n" + ":periodofstudy,\n" + ":qualification,\n" + ":enterSchoolYear,\n" +
                    ":generation,\n" +
                    //":id_chair,\n" +
                    ":specialitytitle,\n" + ":directioncode,\n" + ":qualificationcode,\n" + ":programcode,\n" + ":planfilename,\n" +
                    ":created_school_year\n" + ") RETURNING id_curriculum";

            Query q = getSession().createSQLQuery(query);
            //q.setString("id_direction",);
            q.setParameter("formofstudy", curriculum.getFormOfStudy().getType());
            q.setParameter("distancetype", curriculum.getDistanceType());
            q.setFloat("periodofstudy", curriculum.getPeriodOfStudy());
            q.setParameter("qualification", curriculum.getQualification().getValue());
            q.setParameter("enterSchoolYear", curriculum.getEnterSchoolYear());
            q.setParameter("generation", curriculum.getGeneration());
            //q.setLong("id_chair",curriculum.getChairId());
            q.setString("specialitytitle", curriculum.getSpecialityTitle());
            q.setString("directioncode", curriculum.getDirectionCode());
            q.setString("qualificationcode", curriculum.getQualificationCode());
            q.setString("programcode", curriculum.getProgramCode());
            q.setString("planfilename", curriculum.getPlanFilename());
            q.setLong("created_school_year", curriculum.getCreatedSchoolYear());

            List<Long> list = (List<Long>) getList(q);
            curriculum.setId(list.size() == 0 ? null : list.get(0));

            // Сохраняем компетенции
            for (Competence competence : curriculum.getCompetenceList()) {
                competence = createCompetence(competence, curriculum.getId());
            }

            // Сохраняем блоки
            for (Block block : curriculum.getBlockList()) {
                block = createBlock(block, curriculum.getId());
                // Сохраняем дисциплины
                for (Subject subject : block.getSubjectList()) {
                    // Пытаемся для каждой дисциплины найти DicSubject - находим, здорово, не находим - плохо
                    subject.setIdDicSubject(findDicSubject(subject.getName()));
                    // Создаем новую дисциплину
                    subject = createSubject(subject, block.getId());
                    // Создаем связи с компетенциями
                    for (Competence competence : subject.getCompetenceList()) {
                        createCompetenceSubject(subject, competence.getName(), curriculum.getId());
                    }
                }
            }

            return curriculum;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * Обновление уже существующего УП
     * @param curriculum
     * @return
     */
    public Boolean updateCurriculum (Curriculum curriculum) {
        try {
            String query = "UPDATE curriculum SET \n" +
                    //"id_direction,\n" +
                    "formofstudy = :formofstudy ,\n" +
                    "distancetype = :distancetype,\n" +
                    "periodofstudy = :periodofstudy,\n" +
                    "qualification = :qualification,\n" +
                    "enter_school_year = :enterSchoolYear,\n" +
                    "generation = :generation,\n" +
                    //"id_chair,\n" +
                    "specialitytitle = :specialitytitle,\n" +
                    "directioncode = :directioncode,\n" +
                    "qualificationcode = :qualificationcode,\n" +
                    "programcode = ,\n" +
                    "planfilename = programcode,\n" +
                    "created_school_year =: created_school_year\n" +
                    " WHERE id_curriculum=:id_curriculum";

            Query q = getSession().createSQLQuery(query);
            //q.setString("id_direction",);
            q.setParameter("formofstudy", curriculum.getFormOfStudy().getType());
            q.setParameter("distancetype", curriculum.getDistanceType());
            q.setFloat("periodofstudy", curriculum.getPeriodOfStudy());
            q.setParameter("qualification", curriculum.getQualification().getValue());
            q.setParameter("enterSchoolYear", curriculum.getEnterSchoolYear());
            q.setParameter("generation", curriculum.getGeneration());
            //q.setLong("id_chair",curriculum.getChairId());
            q.setString("specialitytitle", curriculum.getSpecialityTitle());
            q.setString("directioncode", curriculum.getDirectionCode());
            q.setString("qualificationcode", curriculum.getQualificationCode());
            q.setString("programcode", curriculum.getProgramCode());
            q.setString("planfilename", curriculum.getPlanFilename());
            q.setLong("created_school_year", curriculum.getCreatedSchoolYear());
            q.setLong("id_curriculum", curriculum.getId());

            q.executeUpdate();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Curriculum getOneCurriculum(Long curriculumId) {
        try{
            String query = "SELECT  \n" +
                    "formofstudy as formOfStudyType,\n" +
                    "distancetype as distanceType,\n" +
                    "periodofstudy as periodOfStudy,\n" +
                    "qualification as qualificationType,\n" +
                    "enter_school_year as enterSchoolYear,\n" +
                    "generation as generation,\n" +
                    "specialitytitle as specialityTitle,\n" +
                    "directioncode as directionCode,\n" +
                    "qualificationcode as qualificationCode,\n" +
                    "programcode as programCode,\n" +
                    "planfilename as planFilename,\n" +
                    "created_school_year as createdSchoolYear\n" +
                    "FROM curriculum WHERE id_curriculum=:curriculumId";

            Query q = getSession().createSQLQuery(query)
                    .addScalar("formOfStudyType", IntegerType.INSTANCE)
                    .addScalar("distanceType")
                    .addScalar("periodOfStudy", FloatType.INSTANCE)
                    .addScalar("qualificationType", IntegerType.INSTANCE)
                    .addScalar("enterSchoolYear", LongType.INSTANCE)
                    .addScalar("generation")
                    .addScalar("specialityTitle")
                    .addScalar("directionCode")
                    .addScalar("qualificationCode")
                    .addScalar("programCode")
                    .addScalar("planFilename")
                    .addScalar("createdSchoolYear", LongType.INSTANCE)
                    .setResultTransformer(Transformers.aliasToBean(Curriculum.class));;
            q.setLong("curriculumId",curriculumId);

            List<Curriculum> list = (List<Curriculum>) getList(q);

            if (list.size() > 0) {
                Curriculum curriculum = list.get(0);
                curriculum.setId(curriculumId);
                // Получить все блоки
                curriculum.setBlockList(getBlocksForCurriculum(curriculumId));
                return curriculum;
            } else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public List<Block> getBlocksForCurriculum(Long curriculumId) {
        try{
            String query = "SELECT  id_curriculum_block as id, " +
                    "name, " +
                    "code, " +
                    "is_optional as selectable, " +
                    "pid as parentId " +
                    "FROM curriculum_block WHERE id_curriculum=:curriculumId";

            Query q = getSession().createSQLQuery(query)
                    .addScalar("id",LongType.INSTANCE)
                    .addScalar("name")
                    .addScalar("code")
                    .addScalar("selectable")
                    .addScalar("parentId")
                    .setResultTransformer(Transformers.aliasToBean(Block.class));;
            q.setLong("curriculumId",curriculumId);

            List<Block> blocks = (List<Block>) getList(q);

            // Получить все дисциплины
            for (Block block : blocks) {
                block.setSubjectList(getSubjectsForBlock(block.getId()));
                for (Subject subject : block.getSubjectList()) {
                    subject.setCicleCode(block.getCode());
                }
            }
            return blocks;
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public List<Subject> getSubjectsForBlock(Long blockId) {
        try{
            String query = "SELECT id_curriculum_subject as id, "+
                        "ds.subjectname as name, " +
                        "cs.id_dic_subject as idDicSubject, " +
                        "semester_number as semesterNumber, " +
                        "hours_lec as lecHours, " +
                        "hours_lab as labHours, " +
                        "hours_pra as praHours, " +
                        "hours_srs as ksrHours, " +
                        "hours_exam as examHours, " +
                        "code as code, " +
                        "hours_all as allHours, " +
                        "hours_sr as srHours " +
                    "FROM curriculum_subject cs " +
                    "INNER JOIN dic_subject ds ON ds.id_dic_subject=cs.id_dic_subject "+
                    "WHERE id_curriculum_block=:blockId";

            Query q = getSession().createSQLQuery(query)
                    .addScalar("id", LongType.INSTANCE)
                    .addScalar("name")
                    .addScalar("idDicSubject", LongType.INSTANCE)
                    .addScalar("semesterNumber", IntegerType.INSTANCE)
                    .addScalar("lecHours", FloatType.INSTANCE)
                    .addScalar("labHours", FloatType.INSTANCE)
                    .addScalar("praHours", FloatType.INSTANCE)
                    .addScalar("ksrHours", FloatType.INSTANCE)
                    .addScalar("examHours", FloatType.INSTANCE)
                    .addScalar("code")
                    .addScalar("allHours", FloatType.INSTANCE)
                    .addScalar("srHours", FloatType.INSTANCE)
                    .setResultTransformer(Transformers.aliasToBean(Subject.class));
            q.setLong("blockId",blockId);

            List<Subject> subjects = (List<Subject>) getList(q);

            for (Subject subject : subjects) {
                subject.setCompetenceList(getCompetenceForSubject(subject.getId()));
            }

            return subjects;
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public List<Competence> getCompetenceForSubject(Long subjectId){
        try{
            String query = " SELECT " +
                    "cc.id_curriculum_competence as id, " +
                    "cc.name as description, " +
                    "cc.code as name " +
                    "FROM curriculum_competence_subject ccs\n" +
                    "INNER JOIN curriculum_competence cc ON cc.id_curriculum_competence=ccs.id_curriculum_competence\n" +
                    "WHERE ccs.id_curriculum_subject=:subjectId\n" +
                    "ORDER BY cc.id_curriculum_competence";

            Query q = getSession().createSQLQuery(query)
                    .addScalar("id", LongType.INSTANCE)
                    .addScalar("description")
                    .addScalar("name")
                    .setResultTransformer(Transformers.aliasToBean(Competence.class));;
            q.setLong("subjectId",subjectId);

            List<Competence> competences = (List<Competence>) getList(q);

            return competences;
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }


    /**
     * Создание новой дисциплины УП в БД
     *
     * @param subject
     * @param blockId
     * @return
     */
    public Subject createSubject (Subject subject, Long blockId) {
        try {
            String query =
                    "INSERT INTO curriculum_subject (" + "  id_dic_subject,\n" + "  id_curriculum_block,\n" + "  semester_number,\n" +
                            "  hours_lec,\n" + "  hours_lab,\n" + "  hours_pra,\n" + "  hours_srs,\n" + "  hours_exam,\n" + "  code,\n" +
                            "  hours_all,\n" + "  hours_sr) " + "VALUES (  " + ":idDicSubject,\n" + ":idBlock,\n" + ":semesterNumber,\n" +
                            ":lecHours,\n" + ":labHours,\n" + ":praHours,\n" + ":srsHours,\n" + ":examHours,\n" + ":code,\n" + ":allHours,\n" +
                            ":srHours) RETURNING id_curriculum_subject";

            Query q = getSession().createSQLQuery(query);
            q.setLong("idDicSubject", subject.getIdDicSubject());
            q.setLong("idBlock", blockId);
            q.setFloat("allHours", subject.getAllHours());
            q.setFloat("srHours", subject.getSrHours());
            q.setFloat("examHours", subject.getExamHours());
            q.setFloat("lecHours", subject.getLecHours());
            q.setFloat("labHours", subject.getLabHours());
            q.setFloat("praHours", subject.getPraHours());
            q.setFloat("srsHours", subject.getKsrHours());
            q.setInteger("semesterNumber", subject.getSemesterNumber());
            q.setString("code", subject.getCode());

            // TODO: Подумать что делать с кафедрами, и тут ли это делать?
            // TODO: Решить что делать с формой контроля

            List<Long> list = (List<Long>) getList(q);
            subject.setId(list.size() == 0 ? null : list.get(0).longValue());

            return subject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Создание блока для УП
     *
     * @param block
     * @param curriculumId
     * @return
     */
    public Block createBlock(Block block, Long curriculumId) {
        try{
            String query = "INSERT INTO curriculum_block (id_curriculum, name, code, is_optional, pid) VALUES (:idCurriculum, :name, :code, :selectable, :pid) RETURNING id_curriculum_block";

            Query q = getSession().createSQLQuery(query);
            q.setLong("idCurriculum", curriculumId);
            q.setString("name", block.getName());
            q.setString("code", block.getCode());
            q.setBoolean("selectable", block.getSelectable());
            q.setParameter("pid",block.getParentId(),LongType.INSTANCE);

            List<Long> list = (List<Long>) getList(q);
            block.setId(list.size() == 0 ? null : list.get(0).longValue());
            return block;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Создание новой компетенции для учебного плана
     *
     * @param competence
     * @param curriculumId
     * @return
     */
    public Competence createCompetence (Competence competence, Long curriculumId) {
        try {
            String query = "INSERT INTO curriculum_competence (id_curriculum, name, code) " +
                    "VALUES (:idCurriculum, :name, :code) RETURNING id_curriculum_competence";

            Query q = getSession().createSQLQuery(query);
            q.setString("code", competence.getName());
            q.setString("name", competence.getDescription());
            q.setLong("idCurriculum", curriculumId);

            List<Long> list = (List<Long>) getList(q);
            competence.setId(list.size() == 0 ? null : list.get(0).longValue());

            return competence;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Создаем связку между компетенцией и предметом
     *
     * @param subject
     * @param competenceCode
     * @param curriculumId
     * @return
     */
    public boolean createCompetenceSubject (Subject subject, String competenceCode, Long curriculumId) {
        String query = "INSERT INTO curriculum_competence_subject (id_curriculum_subject, id_curriculum_competence) " +
                "VALUES (:idSubject, (SELECT id_curriculum_competence FROM curriculum_competence WHERE code=:compCode AND id_curriculum=:idCurriculum LIMIT 1))";

        Query q = getSession().createSQLQuery(query);
        q.setLong("idSubject", subject.getId());
        q.setString("compCode", competenceCode);
        q.setLong("idCurriculum", curriculumId);

        return executeUpdate(q);
    }

    /**
     * Ищем существующий или создаем Dic_Subject
     *
     * @param subjectName
     * @return
     */
    public Long findDicSubject (String subjectName) {
        String query = "SELECT dic_subject_create_or_get(:subjectName) AS idDicSubject";

        Query q = getSession().createSQLQuery(query).addScalar("idDicSubject", LongType.INSTANCE);
        q.setString("subjectName", subjectName);

        List<Long> list = (List<Long>) getList(q);

        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * Проверяем, есть ли уже в БД предыдущие версии данного учебного плана
     * проверяем свойство parent и сортируя по году берем последний
     *
     * @return
     */
    public Long searchParentCurriculum(Curriculum curriculum) {
        String query = "SELECT id_curriculum AS idCurriculum FROM Curriculum " +
                "WHERE planfilename=:planfilename " +
                "AND enter_school_year=:enter_school_year " +
                "AND directioncode=:directioncode " +
                "AND created_school_year<:created_school_year "+
                "ORDER BY created_school_year DESC LIMIT 1"
                ;

        Query q = getSession().createSQLQuery(query).addScalar("idCurriculum", LongType.INSTANCE);
        q.setString("planfilename", curriculum.getPlanFilename());
        q.setLong("enter_school_year", curriculum.getEnterSchoolYear());
        q.setString("directioncode", curriculum.getDirectionCode());
        // RETARD ALERT! сравнение по id по порядку
        q.setLong("created_school_year",curriculum.getCreatedSchoolYear());

        List<Long> list = (List<Long>) getList(q);

        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * Проверяем, есть ли полная копия плана в БД
     * @param curriculum
     * @return
     */
    public Long searchExistCurriculum(Curriculum curriculum)
    {
        String query = "SELECT id_curriculum AS idCurriculum FROM Curriculum " +
                "WHERE planfilename=:planfilename " +
                "AND enter_school_year=:enter_school_year " +
                "AND directioncode=:directioncode " +
                "AND created_school_year=:created_school_year "+
                "ORDER BY created_school_year DESC LIMIT 1"
                ;

        Query q = getSession().createSQLQuery(query).addScalar("idCurriculum",LongType.INSTANCE);
        q.setString("planfilename", curriculum.getPlanFilename());
        q.setLong("enter_school_year", curriculum.getEnterSchoolYear());
        q.setString("directioncode", curriculum.getDirectionCode());
        // RETARD ALERT! сравнение по id по порядку
        q.setLong("created_school_year",curriculum.getCreatedSchoolYear());

        List<Long> list = (List<Long>) getList(q);

        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * Ищем ID учебного года по дате его начала
     * @param date
     * @return
     */
    public Long searchSchoolYearByDate(Date date) {
        String query = "SELECT id_schoolyear as idSchoolYear FROM schoolyear WHERE dateofbegin = :dateOfBegin";
        Query q = getSession().createSQLQuery(query).addScalar("idSchoolYear", LongType.INSTANCE);
        q.setDate("dateOfBegin",date);

        List<Long> list = (List<Long>) getList(q);

        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * Сохраняем новый учебный план с учетом связей из старого
     * @return
     */
    public Boolean storeNewCurriculum(Curriculum oldCur, Curriculum newCur, List<CurrCompare> subjectList) {
        // Берем id старого Curriculum и сохраняем в него новый
        newCur.setId(oldCur.getId());
        updateCurriculum(oldCur);

        // Пробегаемся по списку дисциплин и удаляем не найденные
        for (CurrCompare currCompare : subjectList) {
            if (currCompare.getNewModel() == null) {
                // TODO: Удаляем старую дисциплину по id DELETE

            }else{
                if (currCompare.getOldModel() == null) {
                    // TODO: Пытаемся найти ее блок

                    // TODO: Создаем новую дисциплину INSERT
                    createSubject(currCompare.getNewModel(), null);
                } else {
                    // TODO: Перезатираем старую дисциплину UPDATE

                }
            }
        }

        // Пробегаемся по

        return false;
    }
}
