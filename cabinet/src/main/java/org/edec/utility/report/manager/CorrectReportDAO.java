package org.edec.utility.report.manager;

import org.edec.dao.DAO;
import org.edec.teacher.model.correctRequest.CorrectRequestModel;
import org.edec.utility.report.model.register.CorrectRatingJasperModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;

import java.util.List;


public class CorrectReportDAO extends DAO {
    public CorrectRatingJasperModel getCorrectReportModel(CorrectRequestModel correctRequest) {
        String query = "SELECT \n" +
                "\tHFT.family||' '||HFT.name||' '||HFT.patronymic AS signatorytutor,\n" +
                "\tDE.fulltitle AS department,\n" +
                "\tRE.register_number AS registerNumber,\n" +
                "\tRE.signdate AS registerDateVal,\n" +
                "\tDG.groupname AS group,\n" +
                "\tHFS.family||' '||HFS.name||' '||HFS.patronymic AS fio,\n" +
                "\tDS.subjectname AS subject,\n" +
                "\tSRH.oldrating AS oldRatingVal\n" +
                "\tFROM sessionratinghistory SRH\n" +
                "\tINNER JOIN register RE ON SRH.id_register=RE.id_register\n" +
                "\tINNER JOIN sessionrating SR ON SRH.id_sessionrating = SR.id_sessionrating\n" +
                "\tINNER JOIN student_semester_status SSS ON SSS.id_student_semester_status = SR.id_student_semester_status\n" +
                "\tINNER JOIN studentcard SC ON SC.id_studentcard = SSS.id_studentcard\n" +
                "\tINNER JOIN humanface HFS ON HFS.id_humanface = SC.id_humanface\n" +
                "\tINNER JOIN subject SU ON SU.id_subject = SR.id_subject\n" +
                "\tINNER JOIN dic_subject DS ON DS.id_dic_subject = SU.id_dic_subject\n" +
                "\tINNER JOIN link_group_semester_subject LGSS ON LGSS.id_subject = SU.id_subject\n" +
                "\tINNER JOIN link_employee_subject_group LESG ON LGSS.id_link_group_semester_subject = LESG.id_link_group_semester_subject\n" +
                "\tINNER JOIN humanface HFT ON HFT.id_humanface = :idHum\n" +
                "\tINNER JOIN employee EM ON EM.id_employee=LESG.id_employee AND HFT.id_humanface = EM.id_humanface\n" +
                "\tINNER JOIN chair CH ON SU.id_chair = CH.id_chair\n" +
                "\tINNER JOIN department DE ON DE.id_chair = CH.id_chair\n" +
                "\tINNER JOIN link_group_semester LGS ON LGS.id_link_group_semester = LGSS.id_link_group_semester\n" +
                "\tINNER JOIN dic_group DG ON DG.id_dic_group = LGS.id_dic_group\n" +
                "WHERE\n" +
                "\tSRH.id_sessionratinghistory = :idSRH";
        Query q = getSession().createSQLQuery(query)
                .addScalar("signatorytutor")
                .addScalar("department")
                .addScalar("registerNumber")
                .addScalar("registerDateVal", DateType.INSTANCE)
                .addScalar("group")
                .addScalar("fio")
                .addScalar("subject")
                .addScalar("oldRatingVal", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(CorrectRatingJasperModel.class));
        q.setLong("idSRH", correctRequest.getIdSRH()).setParameter("idHum", correctRequest.getIdHumanface());
        List<?> list = getList(q);
        return list.size() == 0 ? null : (CorrectRatingJasperModel) list.get(0);
    }
}
