package org.edec.synchroMine.service.impl;

import org.apache.commons.io.IOUtils;
import org.edec.order.model.OrderModel;
import org.edec.synchroMine.manager.orderSynchro.EntityManagerOrderMineDBO;
import org.edec.synchroMine.manager.orderSynchro.EntityManagerOrderMineESO;
import org.edec.synchroMine.model.mine.*;
import org.edec.synchroMine.service.MineOrderService;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.constants.OrderStudentJSONConst;
import org.edec.utility.constants.OrderTypeConst;
import org.edec.utility.converter.DateConverter;
import org.json.JSONObject;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.IOException;
import java.util.Date;
import java.util.List;


public class MineOrderServiceESOimpl implements MineOrderService {
    private EntityManagerOrderMineDBO emMineDBO = new EntityManagerOrderMineDBO();
    private EntityManagerOrderMineESO emMinESO = new EntityManagerOrderMineESO();

    @Override
    public boolean createOrderInMine (OrderModel orderESO) throws IOException, BadLocationException {
        Order order = emMinESO.getOrderDBO(orderESO.getIdOrder());
        order.setConfirm(true);
        order.setHeld(true);
        order.setConfirmerId(0);
        order.setOrderType("Приказ");
        order.setArchive(0);
        order.setDeleted(false);
        order.setSubtitle("");

        order = emMineDBO.createOrder(order);

        List<Scholarship> scholarships = emMineDBO.getAllScholarship();
        List<OrderAction> listOrderAction = emMinESO.getOrderAction(orderESO.getIdOrder());
        int layout = 0;

        for (OrderAction orderAction : listOrderAction) {
            orderAction.setIdOrder(order.getId());

            // Для установления сроков ЛАЗ приказ делаем не массовый
            switch (OrderRuleConst.getById(orderESO.getIdOrderRule())) {
                case SET_ELIMINATION_RESPECTFUL:
                case SET_ELIMINATION_NOT_RESPECTFUL:
                case ACADEMIC_IN_SESSION:
                    orderAction.setMass(false);
                    break;
                default:
                    orderAction.setMass(true);
                    break;
            }
            orderAction.setText("");
            orderAction.setNumber(++layout);

            orderAction = emMineDBO.createOrderAction(orderAction);
            OrderActionType oat = emMineDBO.getOrderActionType(orderAction.getIdAction());

            List<OrderActionStudent> listOrderActionStudent = emMinESO.getOrderActionStudent(
                    orderESO.getIdOrder(), orderAction.getIdAction());

            for (OrderActionStudent oas : listOrderActionStudent) {
                RTFEditorKit rtl = new RTFEditorKit();
                Document document = rtl.createDefaultDocument();

                oas.setIdOrder(order.getId());
                oas.setIdOrderAction(orderAction.getId());
                oas.setHeld(true);

                StudentMove studentMove = new StudentMove();
                studentMove.setComputerName("ACS ISIT");

                // Для установления сроков ЛАЗ студентам заполняем вторую дату первой из нашей системы
                // TODO поменять механизм в нашей системе по заполнению даты сроков ЛАЗ
                switch (OrderRuleConst.getById(orderESO.getIdOrderRule())) {
                    case SET_ELIMINATION_RESPECTFUL:
                    case SET_ELIMINATION_NOT_RESPECTFUL:
                        studentMove.setDateTo(oas.getDateFrom());
                        studentMove.setDateFrom(null);
                        oas.setDateTo(oas.getDateFrom());
                        oas.setDateFrom(null);
                        break;
                    default:
                        studentMove.setDateFrom(oas.getDateFrom());
                        studentMove.setDateTo(oas.getDateTo());
                        break;
                }

                studentMove.setDateMove(order.getDateSigned());
                studentMove.setDateWrite(new Date());
                studentMove.setGroupFrom(oas.getGroupname());
                studentMove.setIdGroupFrom(oas.getIdGroupFrom());
                studentMove.setIdInstituteFrom(oas.getIdInstitute());
                studentMove.setHide(0);
                studentMove.setStatusOld(oas.getIdStatus().intValue());
                studentMove.setOrderNumber(order.getNumber());
                if (oas.getIdStudyForm() == 1) {
                    studentMove.setReason("Общие основания");
                } else if (oas.getIdStudyForm() == 2) {
                    studentMove.setReason("Целевой прием");
                } else if (oas.getIdStudyForm() == 3) {
                    studentMove.setReason("Сверхплановый набор");
                }
                studentMove.setIdStudent(oas.getIdStudent());
                studentMove.setUserName("d229");

                Student student = emMineDBO.getStudent(oas.getIdStudent());
                Scholarship scholarship;

                switch (OrderTypeConst.getByType(orderESO.getOrderType())) {
                    case ACADEMIC:
                        if (oas.getSessresult() == 4) {
                            oas.setAttr1("на отлично");
                            if (oas.getQualification() == 3) {
                                scholarship = getScholarshipByName("академ. стипендия магистрам на \"отлично\"", scholarships);
                                oas.setScholarship(scholarship.getName());
                                oas.setSumm(scholarship.getSumm());
                            } else {
                                scholarship = getScholarshipByName("академ. стипендия на \"отлично\"", scholarships);
                                oas.setScholarship(scholarship.getName());
                                oas.setSumm(scholarship.getSumm());
                            }
                        } else if (oas.getSessresult() == 3) {
                            oas.setAttr1("на хорошо и отлично");
                            if (oas.getQualification() == 3) {
                                scholarship = getScholarshipByName("академ. стипендия магистрам на \"хорошо и отлично\"", scholarships);
                                oas.setScholarship(scholarship.getName());
                                oas.setSumm(scholarship.getSumm());
                            } else {
                                scholarship = getScholarshipByName("академ. стипендия на \"хорошо и отлично\"", scholarships);
                                oas.setScholarship(scholarship.getName());
                                oas.setSumm(scholarship.getSumm());
                            }
                        } else if (oas.getSessresult() == 2) {
                            oas.setAttr1("на хорошо");
                            if (oas.getQualification() == 3) {
                                scholarship = getScholarshipByName("академ. стипендия магистрам на \"хорошо\"", scholarships);
                                oas.setScholarship(scholarship.getName());
                                oas.setSumm(scholarship.getSumm());
                            } else {
                                scholarship = getScholarshipByName("академ. стипендия на \"хорошо\"", scholarships);
                                oas.setScholarship(scholarship.getName());
                                oas.setSumm(scholarship.getSumm());
                            }
                        }
                        oas.setConditional(false);

                        studentMove.setOrderType(8);
                        studentMove.setMoveType("Стипендиальный приказ (назначить ГРУППОВОЙ)");

                        student.setAcademicScholarship(1);
                        student.setDateAcademicScholarshipFrom(oas.getDateFrom());
                        student.setDateAcademicScholarshipTo(oas.getDateTo());
                        break;
                    case DEDUCTION:
                        String str = oat.getTemplate();
                        rtl.read(IOUtils.toInputStream(str), document, 0);
                        str = new String(document.getText(0, document.getLength()).getBytes("Cp1252"), "Cp1251");

                        str = str.replace("TabN", oas.getRecordbook() == null ? "" : oas.getRecordbook())
                                 .replace("FIO0", oas.getFio() == null ? "" : oas.getFio())
                                 .replace("KURS1", oas.getCourse() == null ? "" : String.valueOf(oas.getCourse()))
                                 .replace("GR1", oas.getGroupname() == null ? "" : oas.getGroupname())
                                 .replace("SK1", oas.getDirectioncode() == null ? "" : oas.getDirectioncode())
                                 .replace("SPEC1", oas.getDirectionname() == null ? "" : oas.getDirectionname())
                                 .replace("FORM1", oas.getFormofstudy() == null ? "" : oas.getFormofstudy() + " форма")
                                 .replace("OSN1", oas.getFoundationofstudy() == null ? "" : oas.getFoundationofstudy())
                                 .replace("DATE1", oas.getDateFrom() == null ? "" : DateConverter.convertDateToString(oas.getDateFrom()))
                                 .replace("PRICH", "")
                                 .replace("FRG", "");

                        if (!oas.getProfilename().equals(oas.getDirectionname())) {
                            str = str.replace("SPC1", oas.getProfilename() == null ? "" : oas.getProfilename())
                                     .replace("SSN1", oas.getProfilecode() == null ? "" : oas.getProfilecode());

                            String type = "";
                            // COSTILINA
                            if (oas.getGroupname().trim().toLowerCase().endsWith("м")) {
                                type = "специализированная магистерская программа";
                            } else if (oas.getGroupname().trim().toLowerCase().endsWith("б")) {
                                type = "профиль";
                            } else if (!oas.getGroupname().trim().toLowerCase().endsWith("в")) {
                                type = "специализация";
                            }

                            str = str.replace("профиль/специализация/специализированная магистерская программа", type);
                        } else {
                            str = str.replace(", профиль/специализация/специализированная магистерская программа SSN1 \"SPC1\"", "");
                        }

                        if (oas.getDateTo() == null) {
                            str = str.replace("ATR1", "");
                        } else {
                            str = str.replace("ATR1", DateConverter.convertDateToString(oas.getDateTo()));
                        }

                        try {
                            JSONObject additional = new JSONObject(oas.getAdditional());
                            if (additional.has(OrderStudentJSONConst.FOUNDATION)) {
                                oas.setFoundation(additional.getString(OrderStudentJSONConst.FOUNDATION));
                            }
                        } catch (Exception e) {
                        }
                        oas.setTemplate(str);

                        // если это не заочное отделение, то есть дата отмены выплат с
                        if (oas.getDateTo() != null) {
                            oas.setAttr1(DateConverter.convertDateToString(oas.getDateTo()));
                            oas.setDateTo(null);
                        }

                        studentMove.setOrderType(4);
                        studentMove.setMoveType("Об отчислении по собств.желанию");
                        student.setIdStatus(3L);
                        break;
                    case SOCIAL:
                        if (oas.getSectionName().contains("Cироты") || oas.getSectionName().contains("Инвалиды")) {
                            scholarship = getScholarshipByName("соц. стипендия (сироты, инвалиды)", scholarships);
                            oas.setScholarship(scholarship.getName());
                            oas.setSumm(scholarship.getSumm());
                        } else {
                            scholarship = getScholarshipByName("соц. стипендия (соц.защита)", scholarships);
                            oas.setScholarship(scholarship.getName());
                            oas.setSumm(scholarship.getSumm());
                        }
                        oas.setTemplate(
                                "В соответствии с Уставом СФУ, на основании Положения о стипендиальном обеспечении и других формах материальной поддержки студентов и аспирантов  Федерального государственного автономного образовательного учреждения высшего образования \"Сибирский федеральный университет\"\n" +
                                "\n" + "<c>ПРИКАЗЫВАЮ:</c>\n" + "\n" +
                                "Назначить государственную социальную стипендию в размере, установленном приказом ректора СФУ, " + "с " +
                                DateConverter.convertDateToString(oas.getDateFrom()) + " г. по " +
                                DateConverter.convertDateToString(oas.getDateTo()) + " г. следующим студентам:\n" + "\n" + "Курс " +
                                oas.getCourse() + "\n" + "Группа " + oas.getGroupname() + "\n" + "(# " + oas.getRecordbook() + ") " +
                                oas.getFio() + " - " + scholarship.getSumm() + "(руб.)\n");
                        studentMove.setMoveType("О назначении соц.стипендии");
                        studentMove.setOrderType(9);
                        break;
                    case SOCIAL_INCREASED:
                        if (orderAction.getIdAction().equals(539L)) {
                            scholarship = getScholarshipByName("соц. повышенная стипендия", scholarships);
                            oas.setScholarship(scholarship.getName());
                            oas.setSumm(scholarship.getSumm());
                            oas.setTemplate(
                                    "В соответствии с Постановлением Правительства РФ от 2 июля 2012 г. № 679, Уставом СФУ, на основании Положения о стипендиальном обеспечении и других формах материальной поддержки студентов и аспирантов Федерального государственного автономного образовательного учреждения высшего образования \"Сибирский федеральный университет\"\n" +
                                    "\n" + "<c>ПРИКАЗЫВАЮ:</c>\n" + "\n" + "(# " + oas.getRecordbook() + ") " + oas.getFio() + ", ИКИТ, " +
                                    oas.getCourse() + " курс, гр. " + oas.getGroupname() +
                                    " - назначить  повышенную государственную социальную стипендию в размере, установленном приказом ректора СФУ, с " +
                                    DateConverter.convertDateToString(oas.getDateFrom()) + " г. по " +
                                    DateConverter.convertDateToString(oas.getDateTo()) + " г. ");
                            studentMove.setMoveType("О назначении повышенной соц.стипендии");
                        } else if (orderAction.getIdAction().equals(540L)) {
                            scholarship = getScholarshipByName("соц. стипендия (соц.защита)", scholarships);
                            oas.setScholarship("соц. стипендия");
                            oas.setSumm(scholarship.getSumm());
                            oas.setTemplate("(# " + oas.getRecordbook() + ") " + oas.getFio());
                            studentMove.setMoveType("О прекращении выплаты соц.стипендии");
                        }
                        studentMove.setOrderType(9);

                        break;
                    case TRANSFER:
                        Group group = emMineDBO.getGroupByNameAndCourse(oas.getGroupname(), oas.getCourse() + 1);
                        if (orderAction.getIdAction().equals(733L)) {
                            studentMove.setMoveType("О переводе на сл.курс по итогам промежуточной аттестации");
                            oas.setTemplate(" (# " + oas.getRecordbook() + ") " + oas.getFio());
                        } else if (orderAction.getIdAction().equals(587L)) {
                            studentMove.setMoveType("О переводе на сл.курс условно по уваж.причинам+срок оконч.аттест.");
                            oas.setTemplate(" (# " + oas.getRecordbook() + ") " + oas.getFio());
                        } else if (orderAction.getIdAction().equals(588L)) {
                            studentMove.setMoveType("О переводе на сл. курс условно с задолж.+срок ЛЗ");
                            oas.setTemplate(
                                    "В соответствии со ст. 58 ФЗ от 29.12.12 г. №273-ФЗ «Об образовании в РФ» и Уставом ФГАОУ ВО «Сибирский федеральный университет»,  Положением о промежуточной аттестации обучающихся ФГАОУ ВО \"Сибирский федеральный университет\"\n" +
                                    "\n" + "<c>ПРИКАЗЫВАЮ:</c>\n" + "\n" + "Перевести на следующий курс условно с " +
                                    DateConverter.convertDateToString(oas.getDateFrom()) +
                                    " г. студентов, не прошедших промежуточную аттестацию, имеющих академическую задолженность и установить для них срок ликвидации академической задолженности до " +
                                    DateConverter.convertDateToString(oas.getDateTo()) + " г.:\n" + "\n" + oas.getFormofstudy() +
                                    " обучения " + oas.getFoundationofstudy() + "\n" + "<c>На " + (oas.getCourse() + 1) + " курс</c>\n" +
                                    "Группа " + oas.getGroupname() + "\n" + "\n" + "(# " + oas.getRecordbook() + ") " + oas.getFio() +
                                    "\n" + "\n");
                        } else if (orderAction.getIdAction().equals(473L) || orderAction.getIdAction().equals(607L)) {
                            if (orderAction.getIdAction().equals(473L)) {
                                studentMove.setMoveType("Считать ст-тами после ЛЗ НЕУВАЖ условно переведённых");
                            } else {
                                studentMove.setMoveType("Считать ст-тами после ЛЗ УВАЖ условно переведённых");
                            }
                            oas.setIdGroupFrom(oas.getIdGroupNextCourse());
                            studentMove.setIdGroupFrom(oas.getIdGroupNextCourse());
                            oas.setTemplate("(# " + oas.getRecordbook() + ") " + oas.getFio());
                            if (oas.getAdditional() != null && !oas.getAdditional().equals("")) {
                                JSONObject json = new JSONObject(oas.getAdditional());
                                if (json.has(OrderStudentJSONConst.ORDER_NUMBER) &&
                                    json.has(OrderStudentJSONConst.ORDER_DATE)) {
                                    String atr1 = json.getString(OrderStudentJSONConst.ORDER_NUMBER) + " от " +
                                                  DateConverter.convertDateToString(DateConverter.convertStringToDate(
                                                          (String) json.get(OrderStudentJSONConst.ORDER_DATE),
                                                          "yyyy-MM-dd"
                                                  ));
                                    oas.setAttr1(atr1);
                                }
                            }
                            break;
                        } else if (orderAction.getIdAction().equals(738L)) {
                            studentMove.setOrderType(13);
                            oas.setDateTo(oas.getDateFrom());
                            oas.setDateFrom(null);
                            studentMove.setDateTo(studentMove.getDateFrom());
                            studentMove.setDateFrom(null);
                            /*String strTemplate = oat.getTemplate();
                            rtl.read(IOUtils.toInputStream(strTemplate), document, 0);
                            strTemplate = new String(document.getText(0, document.getLength()).getBytes("Cp1252"), "Cp1251");
                            strTemplate = strTemplate.replace(orderESO.ge)*/
                            oas.setTemplate("(# " + oas.getRecordbook() + ") " + oas.getFio());
                            studentMove.setMoveType("Продление срока ЛЗ");
                            break;
                        } else if (orderAction.getIdAction().equals(851L)) {
                            studentMove.setOrderType(13);
                            oas.setDateTo(oas.getDateFrom());
                            oas.setDateFrom(null);
                            oas.setAttr1("осеннего");
                            oas.setTemplate("(# " + oas.getRecordbook() + ") " + oas.getFio());
                            studentMove.setDateTo(studentMove.getDateFrom());
                            studentMove.setDateFrom(null);
                            studentMove.setMoveType("Продление срока ЛЗ после промеж.аттестации(уваж.прич.)");
                            break;
                        }
                        if (group != null) {
                            oas.setIdGroupTo(group.getId());
                            studentMove.setIdGroupTo(group.getId());
                            studentMove.setGroupTo(group.getName());
                            student.setIdGroup(group.getId());
                        }
                        break;
                    case SET_ELIMINATION_DEBTS:
                        oas.setTemplate(" (# " + oas.getRecordbook() + ") " + oas.getFio());
                        studentMove.setOrderType(13);

                        if (orderAction.getIdAction().equals(737L)) {
                            studentMove.setMoveType("Установление индив. сроков прохожд. аттестации (осенний семестр)");
                        } else if (orderAction.getIdAction().equals(759L)) {
                            studentMove.setMoveType("Установление сроков ЛЗ после промеж.аттестации (осенний семестр)");
                        }
                        break;
                }

                emMineDBO.createOrderActionStudent(oas);
                emMineDBO.createStudentMove(studentMove);
                emMineDBO.saveOrUpdate(student);
            }
        }

        return true;
    }

    private Scholarship getScholarshipByName (String name, List<Scholarship> scholarships) {
        for (Scholarship scholarship : scholarships) {
            if (scholarship.getName().trim().equals(name.trim())) {
                return scholarship;
            }
        }
        return null;
    }
}
