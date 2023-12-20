package org.edec.synchroMine.service.impl;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.edec.curriculumScan.model.CompareErrorConst;
import org.edec.curriculumScan.model.Subject;
import org.edec.synchroMine.model.CurriculumCompareScanResult;
import org.edec.synchroMine.model.dao.SubjectGroupMineModel;
import org.edec.synchroMine.model.dao.SubjectGroupModel;
import org.edec.synchroMine.service.CurriculumComparatorService;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CurriculumComparatorImpl implements CurriculumComparatorService {
    @Override
    public List<CurriculumCompareScanResult> compareSubjectsFromFile(List<Subject> subjectsESO, List<Subject> subjectsDBO) {
        return null;
    }

    @Override
    public List<CurriculumCompareScanResult> compareSubjectsFromDB(List<Subject> subjectsESO, List<Subject> subjectsDBO, String groupName) {
        // Результат сопоставления дисциплин
        List<CurriculumCompareScanResult> resultList = new ArrayList<>();
        // Лист для дисциплин, совпадения для которых не найдены
        List<CurriculumCompareScanResult> notFoundESOList = new ArrayList<>();
        // Сопоставляем 2 списка и образуем пары для сравнения
        for (Subject subjectESO : subjectsESO) {
            CurriculumCompareScanResult compSub = new CurriculumCompareScanResult();
            compSub.setGroupName(groupName);
            compSub.setEsoModel(subjectESO);
            boolean find = false;
            for (Subject subjectMINE : subjectsDBO) {
                // TODO: придумать адекватное сравнение. Мб код?
                if (subjectESO.getCode() != null) {
                    if (subjectESO.getCode().equals(subjectMINE.getCode())
                            && subjectESO.getSemesterNumber().equals(subjectMINE.getSemesterNumber())) {
                        compSub.setMineModel(subjectMINE);
                        resultList.add(compSub);
                        subjectsDBO.remove(subjectMINE);
                        find = true;
                        break;
                    }
                } else if (subjectESO.getName().equals(subjectMINE.getName())
                        && subjectESO.getSemesterNumber().equals(subjectMINE.getSemesterNumber())) {
                    compSub.setMineModel(subjectMINE);
                    resultList.add(compSub);
                    subjectsDBO.remove(subjectMINE);
                    compSub.getNotes().add(CompareErrorConst.CODE);
                    find = true;
                    break;
                }
            }
            if (!find){
                compSub.getNotes().add(CompareErrorConst.MISS_MINE);
                notFoundESOList.add(compSub);
            }
        }

        // Здесь, чтобы отображать дисциплины из ESO для которых пару найти не удалось
        resultList.addAll(notFoundESOList);

        // Остаток дисциплин из файла так же записываем в общий список
        for (Subject subjectMINE : subjectsDBO) {
            CurriculumCompareScanResult compSub = new CurriculumCompareScanResult();
            compSub.setEsoModel(null);
            compSub.setMineModel(subjectMINE);
            compSub.getNotes().add(CompareErrorConst.MISS_ESO);
            resultList.add(compSub);
        }

        // Начинаем проверку и формирование файла отчета:

        return resultList;
    }

    @Override
    public String generateCompareReport(List<CurriculumCompareScanResult> inLis) {
        for (CurriculumCompareScanResult currentPair : inLis) {
            Subject eso = currentPair.getEsoModel();
            Subject mine = currentPair.getMineModel();
            // Если отсутствует у нас или в шахтах - значит несоответствие названия
            if (eso == null || mine == null) {
                // currentPair.getNotes().add(CompareErrorConst.NAME);
                // Других ошибок явно не будет ибо нечего сравнивать
                continue;
            }

            // Прямая сверка имен
            if (!eso.getName().equals(mine.getName())){
                currentPair.getNotes().add(CompareErrorConst.NAME);
            }

            // Сверка всех часов - пока не совсем всех, потом можно добавить
            /*
            if (eso.getLecHours() != mine.getLecHours()
                    || eso.getLabHours() != mine.getLabHours()
                    || eso.getPraHours() != mine.getPraHours()) {
                currentPair.getNotes().add(CompareErrorConst.HOURS);
            }
            */

            if (!eso.getAllHours().equals(mine.getHoursSum())) {
                currentPair.getNotes().add(CompareErrorConst.HOURS);
            }

            // Сверка форм контроля
            if (!eso.getIsExam().equals(mine.getIsExam())
                || !eso.getIsPass().equals(mine.getIsPass())
                || !eso.getIsCP().equals(mine.getIsCP())
                || !eso.getIsCW().equals(mine.getIsCW())
            ) {
                currentPair.getNotes().add(CompareErrorConst.FC);
            }
        }

        try {
            createCSVFile(inLis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createCSVFile(List<CurriculumCompareScanResult> inLis) throws IOException {
        Writer out;
        out = new OutputStreamWriter(new FileOutputStream("compare_result.csv"), Charset.forName("Cp1251"));
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.EXCEL.withDelimiter(';'))) {

            for (CurriculumCompareScanResult currentPair : inLis) {

                Subject eso = currentPair.getEsoModel();
                Subject mine = currentPair.getMineModel();

                List subjectRecord = new ArrayList();
                subjectRecord.add(eso != null ? eso.getId() : "-");
                subjectRecord.add(eso != null ? eso.getCode()+" "+eso.getName() : "-");
                subjectRecord.add(currentPair.getGroupName());
                subjectRecord.add(eso != null ? eso.getSeason() : mine.getSemesterNumber());
                subjectRecord.add(eso != null ? eso.getAllHours() : "-");
                subjectRecord.add(mine != null ? mine.getCode()+" "+mine.getName() : "-");
                subjectRecord.add(mine != null ? mine.getHoursSum() : "-");
                String notes = "-";
                for (CompareErrorConst note : currentPair.getNotes()) {
                    notes += note.getDescription()+". ";
                }
                if (notes.length() > 1) {
                    notes=notes.substring(0,notes.length()-1).replace("-","");
                }
                subjectRecord.add(notes);
                printer.printRecord(subjectRecord);
            }
        }
    }
}
