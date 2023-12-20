package org.edec.secretaryChair.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.edec.secretaryChair.manager.EntityManagerSecretaryChair;
import org.edec.secretaryChair.model.CommissionDayModel;
import org.edec.secretaryChair.model.CommissionModel;
import org.edec.secretaryChair.model.EmployeeModel;
import org.edec.secretaryChair.model.StudentCommissionModel;
import org.edec.secretaryChair.service.SecretaryChairService;
import org.edec.teacher.model.commission.StudentModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.converter.DateConverter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Log4j
public class SecretaryChairImpl implements SecretaryChairService {
    private EntityManagerSecretaryChair emSecretary = new EntityManagerSecretaryChair();

    @Override
    public List<CommissionModel> getCommission(Long idSem, Long idChair, Integer formOfStudy, boolean signed) {
        return emSecretary.getCommissionByChair(idSem, idChair, formOfStudy, signed);
    }

    @Override
    public List<CommissionDayModel> getInfoCommissionDays(CommissionModel commission) {
        List<Date> commissionPeriod = DateConverter.getDateRangeByTwoDates(commission.getDateBegin(), commission.getDateEnd());
        //Получаем все комиссии у студентов за тот же период, что и у выбранной комиссии
        List<CommissionModel> commissionByStudent = emSecretary.getCommissionByDate(commission.getId());
        List<CommissionDayModel> commissionDays = new ArrayList<>();
        for (Date date : commissionPeriod) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                continue;
            }
            CommissionDayModel commissionDay = new CommissionDayModel(date);
            commissionDays.add(commissionDay);
            List<CommissionModel> findCommissionsInDay = commissionByStudent.stream()
                    //Ищем комиссии на текущий день
                    .filter(commissionModel -> DateUtils.isSameDay(date, commissionModel.getCommissionDate()))
                    .collect(Collectors.toList());
            if (findCommissionsInDay.size() == 0) {
                continue;
            }

            commissionDay.setBusyTimes(findCommissionsInDay.stream()
                    //Если текущая комиссия и найденаая комиссия физ. кул-ры, то их добавлять не нужно
                    .filter(commissionModel -> !(commissionModel.isPhysicalCulture() && commission.isPhysicalCulture()))
                    .collect(Collectors.groupingBy(
                            CommissionModel::getCommissionDate)) //Вытаскиваем уникальные commissionDate
                    .keySet()
                    .stream()
                    .sorted()
                    .collect(Collectors.toList()));
        }
        return commissionDays;
    }

    /**
     * Получить свободный интервал. Список содержит два значения
     * 1 - минимально возможное время (может быть начало рабочего дня
     * или максимальная возможное время от предыдущей даты)
     * 2 - максимальное возможное время (может быть окончанием рабочего дня
     * или минимальное возможное время следующей комиссии)
     *
     * @return - список из двух переменные: минимальное и максимальное возможно время для комиссии
     */
    public Map<Integer, List<Date>> getFreeIntervalByCommissionDay(CommissionDayModel commissionDay) {

        //Ограничения по рабочему дню dateMin = 8:30, dateMax = 21:00
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(commissionDay.getDay());

        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);
        Date dateMin = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 0);
        Date dateMax = calendar.getTime();

        int count = 0;
        Map<Integer, List<Date>> mapDates = new HashMap<>();

        if (CollectionUtils.isEmpty(commissionDay.getBusyTimes())) {
            mapDates.put(++count, Arrays.asList(dateMin, dateMax));
        }

        Iterator<Date> iterator = commissionDay.getBusyTimes().iterator();

        while (iterator.hasNext()) {
            Date date = iterator.next();
            if (!DateUtils.isSameDay(date, dateMin)) {
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, 8);
                calendar.set(Calendar.MINUTE, 30);
                dateMin = calendar.getTime();
            }

            //Интервал, когда нельзя назначать комиссию (+-2 часа от комиссии)
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, -2);
            Date curDateMin = calendar.getTime();

            calendar.add(Calendar.HOUR_OF_DAY, 4);
            Date curDateMax = calendar.getTime();

            if (DateConverter.getMinuteOfDay(dateMin) <= DateConverter.getMinuteOfDay(curDateMin)) {
                mapDates.put(++count, Arrays.asList(dateMin, curDateMin));
            }
            dateMin = curDateMax;
            if (!iterator.hasNext() && DateConverter.getMinuteOfDay(curDateMax) <= DateConverter.getMinuteOfDay(dateMax)) {
                mapDates.put(++count, Arrays.asList(curDateMax, dateMax));
            }
        }
        return mapDates;
    }

    @Override
    public List<SemesterModel> getSemesterByChair(Long idChair, Integer formOfStudy) {
        return emSecretary.getSemestersByIdChair(idChair, formOfStudy);
    }

    @Override
    public List<StudentModel> getStudentByCommission(Long idCommission) {
        return emSecretary.getStudentsByCommission(idCommission);
    }

    @Override
    public Map<String, List<StudentCommissionModel>> getStudentsForCheckFreeDate(CommissionModel commission, Date dateComm) {
        List<StudentCommissionModel> studentsByDate = emSecretary.getStudentsForCheckFreeDate(commission.getId(), dateComm, true);
        Map<String, List<StudentCommissionModel>> students = new HashMap<>();
        //Сначала группируем предметы по студентам
        for (Map.Entry<Long, List<StudentCommissionModel>> entry : studentsByDate.stream()
                .collect(Collectors.groupingBy(StudentCommissionModel::getIdStudentCard))
                .entrySet()) {
            //После этого группируем предметы студента по idSubject
            Set<Map.Entry<Long, List<StudentCommissionModel>>> subjectEntries = entry.getValue().stream()
                    .collect(Collectors.groupingBy(StudentCommissionModel::getIdSubject))
                    .entrySet();
            //Если использовано больше одного предмета, то добавляем в список
            if (subjectEntries.size() > 1) {
                //Считаем предметы
                AtomicInteger countPhysicalCulture = new AtomicInteger(0);
                AtomicInteger countSubject = new AtomicInteger(0);
                subjectEntries.forEach(subjectEntry -> {
                    if (subjectEntry.getValue().stream().allMatch(StudentCommissionModel::isPhysicalCulture)) {
                        countPhysicalCulture.incrementAndGet();
                    } else {
                        countSubject.incrementAndGet();
                    }
                });
                //Если физической культуры больше одной, а предметов 0, то можно пропустить
                if (countPhysicalCulture.get() > 0 && countSubject.get() == 0) {
                    continue;
                }
                if (countPhysicalCulture.get() > 0 && countSubject.get() == 1 && commission.isPhysicalCulture()) {
                    continue;
                }
                for (Map.Entry<Long, List<StudentCommissionModel>> subjectEntry : subjectEntries) {
                    StudentCommissionModel studentCommission = subjectEntry.getValue().get(0);
                    if (students.containsKey(studentCommission.getShortFio())) {
                        students.get(studentCommission.getShortFio()).add(studentCommission);
                    } else {
                        List<StudentCommissionModel> subjects = new ArrayList<>();
                        subjects.add(studentCommission);
                        students.put(studentCommission.getShortFio(), subjects);
                    }
                }
            }
        }

        return students;
    }

    @Override
    public List<StudentCommissionModel> getStudentsForCheckFreeTime(CommissionModel commission, Date dateComm) {
        return emSecretary.getStudentsForCheckFreeDate(commission.getId(), dateComm, false);
    }

    @Override
    public List<EmployeeModel> getEmployeeByChair(Long idChair) {
        return emSecretary.getEmployeesByDepartment(idChair);
    }

    @Override
    public List<EmployeeModel> getEmployeeByCommission(Long idCommission) {
        return emSecretary.getCommissionEmployee(idCommission);
    }

    @Override
    public boolean updateCommissionInfo(Date dateCommission, String classroom, Long idCommission) {
        return emSecretary.updateCommissionInfo(dateCommission, classroom, idCommission);
    }

    @Override
    public boolean updateComissionStatusNotification(Long idComission, Integer status) {
        log.info("statusNotification комисиионной ведомости поменялся на 1 или 2, idRegCom = " + idComission );
        if (status == null) {
            return emSecretary.updateCommissionStatusNotification(idComission, 0);
        } else {
            return emSecretary.updateCommissionStatusNotification(idComission, status);
        }

    }

    @Override
    public boolean deleteCommissionStaff(Long idCommission) {
        return emSecretary.deleteCommissionStaff(idCommission);
    }

    @Override
    public boolean addCommissionStaff(EmployeeModel employee, Long idCommission) {
        return emSecretary.addCommissionStaff(employee, idCommission);
    }
}
