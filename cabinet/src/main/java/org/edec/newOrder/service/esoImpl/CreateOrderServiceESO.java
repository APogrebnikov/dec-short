package org.edec.newOrder.service.esoImpl;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.edec.newOrder.manager.CreateOrderManagerESO;
import org.edec.newOrder.model.ScholarshipModel;
import org.edec.newOrder.model.createOrder.OrderCreateRuleModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.createOrder.OrderCreateTypeModel;
import org.edec.newOrder.model.createOrder.OrderSectionModel;
import org.edec.newOrder.model.report.ProtocolComissionerModel;
import org.edec.newOrder.service.CreateOrderService;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.util.media.Media;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by antonskripacev on 08.01.17.
 */
public class CreateOrderServiceESO implements CreateOrderService {

    private CreateOrderManagerESO managerCreate = new CreateOrderManagerESO();

    @Override
    public List<OrderCreateTypeModel> getOrderTypes() {
        return managerCreate.getListOrderType();
    }

    @Override
    public List<OrderCreateRuleModel> getOrderRulesByInstituteAndType(long idInstitute, Long type) {
        return managerCreate.getListOrderRule(idInstitute, type);
    }

    @Override
    public Long getCurrentSemester(long institute, int formOfControl) {
        return managerCreate.getCurrentSemester(institute, formOfControl);
    }

    @Override
    public List<OrderCreateStudentModel> searchStudentsForOrderCreation(Long semester, Long nextSemester, String fio) {
        return managerCreate.getStudentsForSearch(semester, nextSemester, fio);
    }

    @Override
    public List<OrderSectionModel> getSection(long idOrderRule) {
        return managerCreate.getSectionName(idOrderRule);
    }

    public List<OrderCreateStudentModel> parseForAcademicIncreasedOrder(Media media, Long idSemCheck,
                                                                        Long idSemForAcademicIncreased) throws IOException, InvalidFormatException {
        List<OrderCreateStudentModel> students = new ArrayList<>();

        // Read XSL file

        Workbook workbook = WorkbookFactory.create(media.getStreamData());

        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        Row row = rowIterator.next();
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();

            OrderCreateStudentModel orderCreateStudentModel = new OrderCreateStudentModel();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                switch (cell.getColumnIndex()) {
                    case 1:
                        orderCreateStudentModel.setFio(cell.getStringCellValue());
                        break;
                    case 2:
                        orderCreateStudentModel.setGroupname(cell.getStringCellValue());
                        break;
                    case 3:
                        orderCreateStudentModel.setNomination(cell.getStringCellValue());
                        break;
                }
            }

            List<OrderCreateStudentModel> checkStudentsModel = searchStudentsForOrderCreation(idSemCheck, idSemForAcademicIncreased, orderCreateStudentModel.getFio());

            boolean checkStudent = false;
            for (OrderCreateStudentModel student : checkStudentsModel) {
                if (student.getGroupname().equals(orderCreateStudentModel.getGroupname()) &&
                        student.getFio().equals(orderCreateStudentModel.getFio())) {
                    orderCreateStudentModel.setId(student.getId());
                    orderCreateStudentModel.setSessionResult(managerCreate.getSessionResult(orderCreateStudentModel.getId()));
                    orderCreateStudentModel.setDateOfEndSession(student.getDateOfEndSession());
                    orderCreateStudentModel.setDateNextEndOfSession(student.getDateNextEndOfSession());
                    orderCreateStudentModel.setQualification(student.getQualification());
                    orderCreateStudentModel.setCourse(student.getCourse());
                    students.add(orderCreateStudentModel);
                    checkStudent = true;
                    break;
                }
            }
            if (!checkStudent) {
                orderCreateStudentModel.setId(-1L);
                students.add(orderCreateStudentModel);
            }

            if (orderCreateStudentModel.getSessionResult() < 2) {
                orderCreateStudentModel.setId((long) -1);
            }
        }

        return students;
    }

    public List<OrderCreateStudentModel> parseForMaterialSupportOrder(Media media, Date firstDate, Date secondDate,
                                                                      Long idSem, Long idNextSem) throws IOException, InvalidFormatException {
        List<OrderCreateStudentModel> students = new ArrayList<>();
        List<OrderCreateStudentModel> falseStudents = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(media.getStreamData());

        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        Row row;
        boolean checkFIO = false;

        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            OrderCreateStudentModel orderCreateStudentModel = new OrderCreateStudentModel();

            if (checkFIO) {
                boolean emptyString = false;
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cell.getColumnIndex()) {
                        case 0:
                            orderCreateStudentModel.setFio(cell.getStringCellValue());
                            if (orderCreateStudentModel.getFio().isEmpty()) {
                                emptyString = true;
                            }
                            break;
                        case 1:
                            orderCreateStudentModel.setGroupname(cell.getStringCellValue().replace(" ", ""));
                            break;
                        case 2:
                            orderCreateStudentModel.setScholarship((int) cell.getNumericCellValue());
                            break;
                        case 3:
                            orderCreateStudentModel.setTypeScholarship((int) cell.getNumericCellValue());
                            break;
                        case 4:
                            orderCreateStudentModel.setApprovedMaterial((int) cell.getNumericCellValue());
                            break;
                        case 5:
                            orderCreateStudentModel.setRefusalReason(cell.getStringCellValue());
                            break;
                    }
                }

                if (!emptyString) {
                    List<OrderCreateStudentModel> checkStudentsModel = searchStudentsForOrderCreation(idSem, idNextSem, orderCreateStudentModel.getFio());

                    //Дополнительный поиск для тех кого уже может не быть в текущем семестре (например выпускники-инженеры в приказе за февраль)
                    if (checkStudentsModel.isEmpty())
                        checkStudentsModel = searchStudentsForOrderCreation(managerCreate.getPrevSemester(idSem), idNextSem, orderCreateStudentModel.getFio());

                    boolean checkStudent = false;
                    for (OrderCreateStudentModel student : checkStudentsModel) {
                        if (student.getGroupname().equals(orderCreateStudentModel.getGroupname()) &&
                                student.getFio().equals(orderCreateStudentModel.getFio())) {
                            if (student.getDeductedCurSem()) {
                                // Студент отчислен - не заполняем и не добавляем в приказ
                                checkStudent = true;
                                break;
                            }
                            orderCreateStudentModel.setId(student.getId());
                            orderCreateStudentModel.setFirstDate(firstDate);
                            orderCreateStudentModel.setIdStudentcard(student.getIdStudentcard());
                            orderCreateStudentModel.setSecondDate(secondDate);
                            orderCreateStudentModel.setGovernmentFinanced(student.getGovernmentFinanced());
                            students.add(orderCreateStudentModel);
                            checkStudent = true;
                            break;
                        }
                    }

                    if (!checkStudent) {
                        orderCreateStudentModel.setId(-1L);
                        students.add(orderCreateStudentModel);
                    }
                }
            } else {
                Cell cell = cellIterator.next();
                if (cell.getStringCellValue().equals("ФИО")) checkFIO = true;
            }
        }

        students = validateList(students);

        return students;
    }

    public List<OrderCreateStudentModel> validateList(List<OrderCreateStudentModel> students) {

        students = students.stream()
                .filter(student -> student.getFio() != null && !student.getFio().isEmpty())
                .collect(Collectors.toList());

        students.sort(Comparator.comparing(OrderCreateStudentModel::getFio));

        StringBuilder governmentFinancedList = new StringBuilder("\n Следуюшие студенты обучаются на договорной основе:\n");
        StringBuilder studentDuplicateList = new StringBuilder("\n Следующие студенты встречаются в списке больще одного раза:\n");

        boolean hasDuplicates = false;
        boolean hasNotGovernmentFinanced = false;

        OrderCreateStudentModel prevStudent = new OrderCreateStudentModel();
        for (OrderCreateStudentModel student : students) {
            if (student.getId() == -1) continue;

            if (prevStudent.getId() == null) {
                prevStudent = student;
                continue;
            }

            if (prevStudent.getId().equals(student.getId())) {
                hasDuplicates = true;
                studentDuplicateList.append(student.getFio());
            }

            if (!student.getGovernmentFinanced() && !prevStudent.getId().equals(student.getId())) {
                hasNotGovernmentFinanced = true;

                governmentFinancedList.append(student.getFio() + "\n");
            }

            prevStudent = student;
        }

        if (hasDuplicates || hasNotGovernmentFinanced)
            DialogUtil.exclamation((hasDuplicates ? studentDuplicateList.toString() : "") + "\n" +
                    (hasNotGovernmentFinanced ? governmentFinancedList.toString() : ""), "Обратите внимание");

        return students;
    }

    public List<ProtocolComissionerModel> parseForMaterialSupportProtocol(Media media) throws IOException, InvalidFormatException {
        List<ProtocolComissionerModel> protocolCommissionerModelList = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(media.getStreamData());

        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        Row row;
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();

            ProtocolComissionerModel protocolComissionerModel = new ProtocolComissionerModel();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                switch (cell.getColumnIndex()) {
                    case 0:
                        protocolComissionerModel.setFio(cell.getStringCellValue());
                        if (protocolComissionerModel.getFio() != "") {
                            protocolComissionerModel.setShortFio(cell.getStringCellValue());
                        }
                        break;
                    case 1:
                        protocolComissionerModel.setGroupname(cell.getStringCellValue());
                        break;
                    case 2:
                        protocolComissionerModel.setRole(cell.getStringCellValue());
                        break;
                }
            }
            if (!protocolComissionerModel.getFio().equals("ФИО")) {
                protocolCommissionerModelList.add(protocolComissionerModel);
            }
        }
        return protocolCommissionerModelList;
    }

    public List<String> getListGroupsByCurrentSemForFoc() {
        return managerCreate.getListGroupsForCurSemester();
    }

    public List<OrderCreateStudentModel> parseForAcademicalFirstCourseIncreaseOrder(Date firstDate,
                                                                                    Date secondDate, Media media) throws IOException {
        List<OrderCreateStudentModel> students = new ArrayList<>();
        List<ScholarshipModel> scholarshipModelList = new ArrayList<>();

        //Алгоритм действий:
        //парсим критерии - Над критериями должна быть строка, содержащая слово "Критерии"
        //после того как заканчивается строка парсим студентов
        //для этого определяем номера столбцов с нужными нам названиями и парсим нужные столбцы
        InputStream inputStream = media.getStreamData();
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
        HSSFSheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        Row row;

        boolean isParseCriteria = false;
        boolean isParseColumn = false;
        boolean isParseStudent = false;

        Map<String, Integer> parsingColumnList = new HashMap<>();

        while (rowIterator.hasNext()) {
            row = rowIterator.next();

            ScholarshipModel scholarshipModel = new ScholarshipModel();

            //если критерий достигнут то начинаем работать со студентами отступив три пустые строки согласно файлу
            if (!isParseStudent && parsingColumnList.size() == 4) {
                rowIterator.next();
                rowIterator.next();
                rowIterator.next();

                row = rowIterator.next();

                isParseColumn = false;
                isParseStudent = true;
            }

            Iterator<Cell> cellIterator = row.cellIterator();

            OrderCreateStudentModel orderCreateStudentModel = new OrderCreateStudentModel();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                //парсим критерии и колонки
                if (!isParseStudent) {

                    //парсим критерии
                    if (cell.getStringCellValue().contains("Таблица критериев") && !isParseCriteria) {
                        isParseCriteria = true;
                        rowIterator.next();
                        break;
                    }

                    if (isParseCriteria) {
                        switch (cell.getAddress().getColumn()) {
                            case 0:
                                if (cell.getStringCellValue().isEmpty()) {
                                    isParseCriteria = false;
                                    isParseColumn = true;
                                    break;
                                }
                                scholarshipModel.setParagraph(cell.getStringCellValue());
                                break;
                            case 1:
                                scholarshipModel.setName(cell.getStringCellValue());
                                break;
                            case 2:
                                scholarshipModel.setText(cell.getStringCellValue());
                                scholarshipModelList.add(scholarshipModel);
                                break;
                        }
                    }

                    if (isParseColumn) {
                        //определяем номера колонок в которых будет содержаться нужная информация
                        if (cell.getStringCellValue().equals("ФИО")) {
                            parsingColumnList.put("ФИО", cell.getAddress().getColumn());
                        } else if (cell.getStringCellValue().equals("Институт")) {
                            parsingColumnList.put("Институт", cell.getAddress().getColumn());
                        } else if (cell.getStringCellValue().equals("Код специальности")) {
                            parsingColumnList.put("Код специальности", cell.getAddress().getColumn());
                        } else if (cell.getStringCellValue().equals("Код стипендии")) {
                            parsingColumnList.put("Код стипендии", cell.getAddress().getColumn());
                        }
                    }
                } else { //парсим студентов по интересущим полям
                    if (parsingColumnList.get("ФИО") == cell.getAddress().getColumn()) {
                        orderCreateStudentModel.setFio(cell.getStringCellValue());
                    } else if (parsingColumnList.get("Институт") == cell.getAddress().getColumn()) {
                        orderCreateStudentModel.setInstituteName(cell.getStringCellValue());
                    } else if (parsingColumnList.get("Код специальности") == cell.getAddress().getColumn()) {
                        orderCreateStudentModel.setSpecialityCode(cell.getStringCellValue());
                    } else if (parsingColumnList.get("Код стипендии") == cell.getAddress().getColumn()) {
                        orderCreateStudentModel.setScholarshipCode(cell.getStringCellValue());
                        orderCreateStudentModel.setScholarshipInfo(getScholarshipInfo(cell.getStringCellValue(), scholarshipModelList));
                    }
                }
            }

            //TODO:сделать динамическую проверку на институт в зависимости от текущего пользователя если возможно
            //оставляем только студентов из нужного института
            if (isParseStudent && orderCreateStudentModel.getInstituteName().equals("Институт космических и информационных технологий")) {
                orderCreateStudentModel.setFirstDate(firstDate);
                orderCreateStudentModel.setSecondDate(secondDate);
                students.add(orderCreateStudentModel);
            }
        }

        students.sort(Comparator.comparing(OrderCreateStudentModel::getScholarshipCode));

        return students;
    }

    private ScholarshipModel getScholarshipInfo(String scholarshipCode, List<ScholarshipModel> scholarshipList) {
        for (ScholarshipModel scholarship : scholarshipList) {
            if (scholarship.getParagraph().equals(scholarshipCode)) {
                return scholarship;
            }
        }

        return null;
    }
}
