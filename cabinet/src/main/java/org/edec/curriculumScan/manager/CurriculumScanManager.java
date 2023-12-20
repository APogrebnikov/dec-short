package org.edec.curriculumScan.manager;

import org.edec.curriculumScan.model.Block;
import org.edec.curriculumScan.model.Competence;
import org.edec.curriculumScan.model.Curriculum;
import org.edec.curriculumScan.model.Subject;
import org.edec.utility.component.model.YearModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.FormOfStudyConst;
import org.edec.utility.constants.QualificationConst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CurriculumScanManager {

    String CICLE_CONTAINER = "АтрибутыЦиклов";
    String CICLE_CONTAINER_NEW = "АтрибутыЦикловНов";
    String CUR_DOC = "Документ";
    String CUR_FILENAME = "LastName";
    String CUR_FOS = "ФормаОбучения";
    String CUR_LEVEL = "Уровень";
    String CUR_SPEC = "Специальности";
    String CUR_SPEC_INNER = "Специальность";
    String CUR_SPEC_INNER_NAME = "Название";
    String CUR_SPEC_CODE = "ПоследнийШифр";
    String CUR_YEAR = "ГодНачалаПодготовки";
    String CUR_PLAN = "План";
    String CUR_TITLE = "Титул";
    String CUR_NAME = "ИмяПлана";
    String BLOCK_NAME = "Название";
    String BLOCK_CODE = "Аббревиатура";
    String SUB_NAME = "Дис";
    String SUB_CICLE = "Цикл";
    String SUB_CHAIR = "Кафедра";
    String SUB_COMP_NUM = "КомпетенцииКоды";
    String SUB_PRACTICE_NUM = "Компетенции";
    String SUB_HOURS_ALL = "ГОС";
    String SUB_SEMESTER = "Ном";
    String SUB_EXAM = "Экз";
    String SUB_PASS = "Зач";
    String SUB_CP = "КП";
    String SUB_CW = "КР";
    String SUB_HOURS_PR = "Пр";
    String SUB_HOURS_SRS = "СРС";
    String SUB_HOURS_LEC = "Лек";
    String SUB_HOURS_LAB = "Лаб";
    String SUB_HOURS_EXAM = "Экз";
    String SUB_PROJ_ZE = "ПроектЗЕТ";
    String CUR_ANOTHER_WORK = "СпецВидыРаботНов";
    String CUR_PRACTICE = "ПрочаяПрактика";
    String CUR_PRACTICE_UCHEB = "УчебПрактики";
    String CUR_PRACTICE_OTHER = "ПрочиеПрактики";
    String CUR_PRACTICE_DISSER = "ДиссерПодготовка";
    String CUR_COMPETENCES = "Компетенции";
    String COMP_NAME = "Индекс";
    String COMP_DESCRIPT = "Содержание";
    String COMP_CODE = "Код";
    String SUB_PRACTICE_NAME = "Наименование";
    String SUB_PRACTICE_ALLHOURS = "ПланЧасов";

    String PRACTIC_BLOCK_NAME = "Практики";
    String PRACTIC_BLOCK_BLOCK = "Б2";

    List<Competence> competences = new ArrayList<>();

    public Curriculum parseCurriculum(Document doc, YearModel yearModel, CurriculumScanDAOManager dao)
    {
        Curriculum curriculum = new Curriculum();
        // Получаем информацию из титульной части УП
        NodeList nl = doc.getElementsByTagName(CUR_TITLE);
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nl.item(i);
                curriculum.setFileName(element.getAttribute(CUR_NAME));
                curriculum.setQualification(QualificationConst.getQualificationByValue(Integer.valueOf(element.getAttribute(CUR_LEVEL))));
                curriculum.setDirectionCode(element.getAttribute(CUR_SPEC_CODE));

                DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date date = format.parse("01.09."+element.getAttribute(CUR_YEAR));
                    if(date != null) {
                        curriculum.setEnterSchoolYear(dao.searchSchoolYearByDate(date));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        NodeList nlsp=doc.getElementsByTagName(CUR_SPEC);
        NodeList nlsp_inner=nlsp.item(0).getChildNodes();
        String fullspec="";
        for(int i=0;i<nlsp_inner.getLength();i++) {
            if (nlsp_inner.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nlsp_inner.item(i);
                fullspec += element.getAttribute(CUR_SPEC_INNER_NAME);
            }
        }

        String directionCode = curriculum.getDirectionCode();

        String[] res = fullspec.split(directionCode);
        String directionName = res[res.length - 1];
        String specialityName = directionName.substring(directionName.indexOf(" "));
        String programCode = directionName.substring(0, directionName.indexOf(" "));
        directionName = res[1].replace(" программа ", "").trim();

        curriculum.setSpecialityTitle(specialityName.trim());
        curriculum.setProgramCode(directionCode + programCode);

        NodeList nlp=doc.getElementsByTagName(CUR_PLAN);
        for(int i=0;i<nlp.getLength();i++) {
            if (nlp.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nlp.item(i);
                curriculum.setFormOfStudy(FormOfStudyConst.getFormOfStudyByName(element.getAttribute(CUR_FOS)));
            }
        }

        NodeList nld = doc.getElementsByTagName(CUR_DOC);
        for (int i = 0; i < nld.getLength(); i++) {
            if (nld.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nld.item(i);
                curriculum.setPlanFilename(element.getAttribute(CUR_FILENAME));
            }
        }

        // TODO: Добавить парсинг графика учебного процесса

        // Парсим компетенции в глобальный список
        competences = parseAllCompetence(doc);

        curriculum.setCompetenceList(competences);

        // Отдельно парсим блок Б2 - Практики из CUR_ANOTHER_WORK
        List<Subject> practices = parseAllPractice(doc);
        Block practiceBlock = new Block();
        practiceBlock.setName(PRACTIC_BLOCK_NAME);
        practiceBlock.setCode(PRACTIC_BLOCK_BLOCK);
        practiceBlock.setSelectable(false);
        practiceBlock.setSubjectList(practices);

        // Парсим блоки из "Цикл", чтобы попытаться уточнить их название
        List<Block> blocksWithName = parseBlocks(doc, doc.getElementsByTagName(CICLE_CONTAINER).item(0));
        blocksWithName.addAll(parseBlocks(doc, doc.getElementsByTagName(CICLE_CONTAINER_NEW).item(0)));

        // Парсим все дисциплины и распределяем их по блокам
        List<Block> blocks = parseAllSubjects(doc);
        // Добавляем распаршеный ранее Б2
        blocks.add(practiceBlock);

        // Уточняем названия блоков
        for (Block block : blocks) {
            for (Block blockWN : blocksWithName) {
                if (block.getCode().equals(blockWN.getCode())) {
                    block.setName(blockWN.getName());
                }
            }
        }
        // Костыль 1 - TODO: понять как правильно парсить
        if (curriculum.getQualification().getValue() == 1) {
            curriculum.setPeriodOfStudy((float) 5.5);
        }
        if (curriculum.getQualification().getValue() == 2) {
            curriculum.setPeriodOfStudy((float) 4);
        }
        if (curriculum.getQualification().getValue() == 3) {
            curriculum.setPeriodOfStudy((float) 2);
        }

        // Костыль 2 - TODO: достать поколение плана
        curriculum.setGeneration(3);

        // Парсим год создания - для корректной связи в цепочку
        curriculum.setCreatedSchoolYear(yearModel.getIdSchoolYear());

        // Ищем родителя, если данный план уже сканировался в прошлых годах
        curriculum.setParent(dao.searchParentCurriculum(curriculum));

        // Заносим итоговый список блоков в объект УП
        curriculum.setBlockList(blocks);
        return curriculum;
    }

    /**
     * Получаем все возможные дисциплины (Строки) через маску - "есть атрибут Цикл"
     * И распределяем их по блокам
     *
     * @param doc
     * @return
     */
    public List<Block> parseAllSubjects (Document doc) {
        List<Block> blocks = new ArrayList<>();
        List<Subject> subjects = new ArrayList<>();
        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            String expression = "//*[@Цикл]";

            NodeList subjectNodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);

            for (int i = 0; i < subjectNodes.getLength(); i++) {
                // Element element = (Element) subjectNodes.item(i);
                subjects.addAll(parseOneSubject(subjectNodes.item(i)));
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        // Распределяем найденные дисциплины по блокам
        for (Subject subject : subjects) {
            boolean findBlock = false;
            for (Block block : blocks) {
                if (block.getCode().equals(subject.getCicleCode())) {
                    block.getSubjectList().add(subject);
                    findBlock = true;
                }
            }
            if (!findBlock) {
                Block block = new Block();
                block.setName(subject.getCicleCode());
                block.setCode(subject.getCicleCode());
                block.setSubjectList(new ArrayList<>());
                block.getSubjectList().add(subject);
                // Ищем в коде блока ДВ - блок дисциплин по выбору
                block.setSelectable(subject.getCicleCode().contains(".ДВ"));
                blocks.add(block);
            }
        }

        return blocks;
    }

    /**
     * Парсим одну сущность "Строка", превращая ее в дисциплину
     * Вполне может возвращать список значенией, если дисциплина многосеместровая
     *
     * @param subNode
     * @return
     */
    public List<Subject> parseOneSubject (Node subNode) {
        List<Subject> subjects = new ArrayList<>();
        // Делим нод на семестры, чтобы собрать часы и разделить дисциплину на части
        NodeList nl = subNode.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nl.item(i);
                if (element.getAttribute(SUB_SEMESTER) != null && !element.getAttribute(SUB_SEMESTER).equals("")) {
                    Subject subject = new Subject();
                    // Поля из самой строки плана
                    subject.setName(((Element) subNode).getAttribute(SUB_NAME));
                    subject.setAllHours(Float.parseFloat(((Element) subNode).getAttribute(SUB_HOURS_ALL)));
                    subject.setCicleCode(((Element) subNode).getAttribute(SUB_CICLE));
                    subject.setChairCode(Long.getLong(((Element) subNode).getAttribute(SUB_CICLE)));

                    // Парсим компетенции
                    String comps = ((Element) subNode).getAttribute(SUB_COMP_NUM);
                    List<String> compList = Arrays.asList(comps.split("&"));

                    for (String s : compList) {
                        for (Competence competence : competences) {
                            if (s.equals(competence.getId().toString())) {
                                subject.getCompetenceList().add(competence);
                            }
                        }
                    }

                    // Поля из семестра
                    subject.setSemesterNumber(Integer.parseInt(element.getAttribute(SUB_SEMESTER)));
                    subject.setFcList(new ArrayList<>());
                    // Форма контроля
                    if (element.getAttribute(SUB_PASS) != "") {
                        subject.getFcList().add(FormOfControlConst.PASS);
                    }
                    if (element.getAttribute(SUB_EXAM) != "") {
                        subject.getFcList().add(FormOfControlConst.EXAM);
                    }
                    if (element.getAttribute(SUB_CP) != "") {
                        subject.getFcList().add(FormOfControlConst.CP);
                    }
                    if (element.getAttribute(SUB_CW) != "") {
                        subject.getFcList().add(FormOfControlConst.CW);
                    }
                    // Часы
                    if (element.getAttribute(SUB_HOURS_LEC) != "") {
                        subject.setLecHours(Float.parseFloat(element.getAttribute(SUB_HOURS_LEC)));
                    }
                    if (element.getAttribute(SUB_HOURS_SRS) != "") {
                        subject.setKsrHours(Float.parseFloat(element.getAttribute(SUB_HOURS_SRS)));
                    }
                    if (element.getAttribute(SUB_HOURS_PR) != "") {
                        subject.setPraHours(Float.parseFloat(element.getAttribute(SUB_HOURS_PR)));
                    }
                    if (element.getAttribute(SUB_HOURS_LAB) != "") {
                        subject.setLabHours(Float.parseFloat(element.getAttribute(SUB_HOURS_LAB)));
                    }
                    if (element.getAttribute(SUB_HOURS_EXAM) != "") {
                        subject.setExamHours(Float.parseFloat(element.getAttribute(SUB_HOURS_EXAM)));
                    }

                    subjects.add(subject);
                } else {
                    // Подраздел дисциплины курсовой проет
                    // Пока обрабатывать не обязательно - указатель есть и в частях
                }
            }
        }

        return subjects;
    }

    /**
     * Попытка распарсить практики как дисциплины
     *
     * @return
     */
    public List<Subject> parseAllPractice (Document doc) {
        List<Subject> subjects = new ArrayList<>();
        // Попытка найти блок практик
        NodeList nl = doc.getElementsByTagName(CUR_ANOTHER_WORK);
        NodeList childNl = nl.item(0).getChildNodes();
        for (int i = 0; i < childNl.getLength(); i++) {
            if (childNl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element node = (Element) childNl.item(i);

                //  TODO: Добавить отличительные признаки практикам

                if (node.getNodeName() == CUR_PRACTICE_UCHEB) {
                    subjects.addAll(parseOnePractice(node));
                }

                if (node.getNodeName() == CUR_PRACTICE_OTHER) {
                    subjects.addAll(parseOnePractice(node));
                }

                if (node.getNodeName() == CUR_PRACTICE_DISSER) {
                    subjects.addAll(parseOnePractice(node));
                }
            }
        }
        return subjects;
    }

    /**
     * Универсальный метод для парсинга одной практики
     *
     * @param node
     * @return
     */
    public List<Subject> parseOnePractice (Element node) {
        List<Subject> subjects = new ArrayList<>();
        NodeList nlSub = node.getChildNodes();
        for (int j = 0; j < nlSub.getLength(); j++) {

            // Получаем всех детей семестры
            NodeList nlSem = nlSub.item(j).getChildNodes();
            for (int k = 0; k < nlSem.getLength(); k++) {

                if (nlSem.item(k).getNodeType() == Node.ELEMENT_NODE) {
                    // Пробегаемся по списку семестров и получаем часы
                    Element semesterElem = (Element) nlSem.item(k);
                    if (semesterElem.getAttribute(SUB_SEMESTER) != null && !semesterElem.getAttribute(SUB_SEMESTER).equals("")) {
                        Subject subject = new Subject();
                        subject.setName(((Element) nlSub.item(j)).getAttribute(SUB_PRACTICE_NAME));
                        if (semesterElem.getAttribute(SUB_PRACTICE_ALLHOURS) != "") {
                            subject.setAllHours(Float.parseFloat(semesterElem.getAttribute(SUB_PRACTICE_ALLHOURS)));
                        }
                        subject.getFcList().add(FormOfControlConst.PRACTIC);
                        subject.setSemesterNumber(Integer.parseInt(semesterElem.getAttribute(SUB_SEMESTER)));
                        subjects.add(subject);
                    }
                }
            }
        }
        return subjects;
    }

    /**
     * Парсим список всех присутствующих в документе компетенций
     *
     * @param doc
     * @return
     */
    public List<Competence> parseAllCompetence (Document doc) {
        List<Competence> competences = new ArrayList<>();
        // Попытка найти блок практик
        NodeList compParent = doc.getElementsByTagName(CUR_COMPETENCES);
        NodeList compChilds = compParent.item(0).getChildNodes();
        for (int i = 0; i < compChilds.getLength(); i++) {
            if (compChilds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element compElem = (Element) compChilds.item(i);
                Competence competence = new Competence();
                competence.setName(compElem.getAttribute(COMP_NAME));
                competence.setDescription(compElem.getAttribute(COMP_DESCRIPT));
                competence.setId(Long.parseLong(compElem.getAttribute(COMP_CODE)));
                competences.add(competence);
            }
        }
        return competences;
    }

    /**
     * Пытаемся получить все блоки, а потом достать для каждого из них дисциплины
     * описаны только блоки первого уровня, поэтому нужен только чтобы достать названия
     *
     * @param doc
     * @param parentNode
     * @return
     */
    public List<Block> parseBlocks (Document doc, Node parentNode) {
        List<Block> blocks = new ArrayList<>();
        NodeList nl = ((Node) parentNode).getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nl.item(i);
                if (element.hasAttribute(BLOCK_NAME)) {
                    Block block = new Block();
                    block.setName(element.getAttribute(BLOCK_NAME));
                    block.setCode(element.getAttribute(BLOCK_CODE));
                    /*
                    XPathFactory factory = XPathFactory.newInstance();
                    XPath xpath = factory.newXPath();
                    String expression="//*[@Цикл='"+block.getCode()+"']";
                    try {
                        NodeList nodeList=(NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
                        block.setSubjectList(parseSubjectsForNode(nodeList));
                    } catch (XPathExpressionException e) {
                        e.printStackTrace();
                    }
                    */
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public List<Subject> parseSubjectsForNode (NodeList nl) {
        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
        }
        return null;
    }

    public void compareCurriculum(Curriculum oldCur, Curriculum newCur)
    {

    }
}
