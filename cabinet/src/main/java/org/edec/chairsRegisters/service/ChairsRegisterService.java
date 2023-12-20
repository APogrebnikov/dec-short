package org.edec.chairsRegisters.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.edec.chairsRegisters.model.ChairsDepartmentModel;
import org.edec.chairsRegisters.model.ChairsRegisterModel;
import org.edec.chairsRegisters.model.GroupBySemRegisterModel;

import java.util.Date;
import java.util.List;

public interface ChairsRegisterService {

    int UNSIGNED = 0;
    int SIGNED = 1;
    int OVERDUE = 2;
    int WITH_NUMBER = 3;

    int SPEC = 4;
    int BACHELOR = 5;
    int MASTER = 6;

    List<ChairsRegisterModel> getListMainRegisters (Long idDepartment, Long idSemester);
    List<ChairsRegisterModel> getListRetakeRegisters (Long idDepartment, Long idSemester);
    List<ChairsRegisterModel> getListComissionRegisters (Long idDepartment, Long idSemester);
    List<GroupBySemRegisterModel> getRetakeRegiseterByPeriod (Long idDepartment, Long idSemester, Date dateFrom, Date dateTo);
    ChairsDepartmentModel getIdDepartment (Long idHumanface);
    int countUnsignRegister (List<ChairsRegisterModel> listRegister);
    Date getOverdueRetakeDate(Date date);
    Date getOverdueMainDate(Date date);
    Long getCurrentSem(int fos, Long idInst);
    HSSFWorkbook getRetakeRegisterReportXLS(List<GroupBySemRegisterModel> listRetakeRegister);

 }
