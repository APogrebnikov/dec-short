package org.edec.passportGroup.service;

import org.edec.passportGroup.model.SemesterModel;
import org.edec.passportGroup.model.passportGroupReport.EokModel;
import org.edec.report.xls.XlsReport;
import org.zkoss.util.media.AMedia;
import org.zkoss.zul.Listitem;

import java.util.List;

/**
 * Created by Roman Assaulyanov on 09.06.2017.
 */
public interface ReportsService {
    /**
     * Отчет для Гончарик
     *
     * @param Inst
     * @param TextInst
     * @param Sem
     * @param SemText
     * @param FoS
     * @return
     */
    String getGoncharicReport (int Inst, String TextInst, long Sem, String SemText, int FoS);

    /**
     * Первый отчет по должникам
     *
     * @param TypeOfSemester
     * @param idInstitute
     * @param formOfStudy
     * @param Course
     * @param group
     * @param isBachelor
     * @param isMaster
     * @param isEngineer
     * @param FoS
     * @return
     */
    String getDebtorsReport (int TypeOfSemester, long idInstitute, int formOfStudy, int Course, String group, boolean isBachelor,
                             boolean isMaster, boolean isEngineer, String FoS);

    /**
     * Должники (Версия 2)
     *
     * @param semester
     * @param gov
     * @param type
     * @return
     */
    String getReportDeb (Integer semester, Integer gov, Integer type, List<Listitem> grouplist);

    /**
     * Формы контроля на семестр
     *
     * @param Sem
     * @return
     */
    String getFormControlReport (long Sem);

    /**
     * Справка декана
     *
     * @param grouplist
     * @param size
     * @return
     */
    String getDecanReport (List<Listitem> grouplist, int size);

    /**
     * Список студентов группы
     *
     * @param grouplist
     * @param sem
     * @return
     */
    String getStudentlistReport (List<Listitem> grouplist, int sem);

    String getPassportGroupReport (List<Long> listGroupId);

    AMedia getReportEok(SemesterModel semester, Long idChair);
}
